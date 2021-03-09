import { Component, EventEmitter, OnDestroy, Output } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MessageService } from 'primeng/api';
import { TableColumn } from 'src/app/common/entities/table-column';
import { SheetMode } from '../../common/detailed-sheet/detailed-sheet.component';
import { DetailedSheetUtils } from '../../common/detailed-sheet/utils/detailed-sheet-utils';
import { FieldType } from '../../common/entities/detailed-field';
import { ColumnType } from '../../common/entities/table-column';
import { LockService } from '../../services/dataServices/lock.service';
import { FilterService } from '../../services/filter-service/filter.service';
import { DetailedField } from './../../common/entities/detailed-field';
import { AuthService } from './../../services/auth.service';
import { WebsocketService } from './../../services/websocket.service';
import { UnlockRequestDTO, LossScenarioRequestDTO, ImplementationConstraintResponseDTO, LossScenarioResponseDTO, UcaResponseDTO, IMPLEMENTATION_CONSTRAINT, ImplementationConstraintRequestDTO, ControllerConstraintResponseDTO } from './../../types/local-types';
import { HttpErrorResponse } from '@angular/common/http';
import { FilteringTableColumn, FilteringTableElement } from '../../common/filtering-table/filtering-table.component';
import { Subject, Subscription } from 'rxjs';
import { DetailData } from 'src/app/common/entities/data';
import { ImplementationConstraintDataService } from 'src/app/services/dataServices/implementation-constraint-data.service';
import { LossScenarioDataService } from 'src/app/services/dataServices/loss-scenario.service';
import { UcaDataService } from 'src/app/services/dataServices/uca-data.service';
import { Hotkeys } from 'src/app/hotkeys/hotkeys.service';
import { incompleteEntityDTO, IncompleteEntitiesService } from 'src/app/services/dataServices/incomplete-entities-data.service';
import { UserDataService, UserDisplayRequestDTO } from 'src/app/services/dataServices/user-data.service';
import { ENTITY_FAILED_DELETE, ENTITY_SUCCESSFUL_DELETE, DETAILEDSHEET_SUCCESSFUL_EDIT, DETAILEDSHEET_FAILED_EDIT, DETAILEDSHEET_SUCCESSFUL_CREATION, DETAILEDSHEET_FAILED_CREATION, CHANGED_STATE_FAILED, ENTITY_FAILED_LOADING, DETAILEDSHEET_EXPECTED_NAME_NOT_NULL, DELETED_USER, NO_USER } from 'src/app/globals';
import {
  implementationConstraintTitle,
  SelectedEntity
} from '../dependent-element-tree/dependent-elements-types.component';

/**
* Contains data of Implementation Constrained currently loaded into Detailed Sheet
*/
interface ImplConstraintForDetailedSheet {
  id: string;
  name: string;
  description: string;
  controllerConstraint: string;
  lsId: string;
  state: string;
}

@Component({
  selector: 'app-implementation-constraints',
  templateUrl: './implementation-constraints.component.html',
  styleUrls: ['./implementation-constraints.component.css']
})
export class ImplementationConstraintsComponent implements OnDestroy {

  SheetMode: typeof SheetMode = SheetMode;
  // seperate attributes which are for detailed sheet, table or nav table
  private projectId: string;
  implementationConstraints: ImplementationConstraintResponseDTO[] = [];
  columns: TableColumn[] = [];
  detailedColumns: TableColumn[] = [];
  implementationConstraint: ImplConstraintForDetailedSheet;
  sheetMode: SheetMode = SheetMode.Closed;
  detailedSheetFields: DetailedField[];
  detailedSheetTitle: string = DetailedSheetUtils.generateSheetTitle(IMPLEMENTATION_CONSTRAINT);
  navigationTableDefinitions: FilteringTableColumn[] = [];
  selectedUca: UcaResponseDTO;
  selectedLossScenario: LossScenarioRequestDTO;

  private OwnChange: boolean = false;
  disableSaveButton: boolean = false;
  updateSelectedItemSubject: Subject<{ element: FilteringTableElement, columnNumber: number }> =
    new Subject<{ element: FilteringTableElement, columnNumber: number }>();
  subscriptions: Subscription[] = [];

  selectImplementationConstraintEntity: SelectedEntity<Object>;
  @Output() selectEntityMessage: any = new EventEmitter<SelectedEntity<Object>>();

  constructor(
    private readonly implementationConstraintDataService: ImplementationConstraintDataService,
    private readonly ucaDataService: UcaDataService,
    private readonly lossScenarioDataService: LossScenarioDataService,
    private readonly route: ActivatedRoute,
    private readonly filterService: FilterService,
    private readonly messageService: MessageService,
    private readonly wsService: WebsocketService,
    private readonly authService: AuthService,
    private readonly lockService: LockService,
    private readonly incompleteEntityService: IncompleteEntitiesService,
    private readonly userDataService: UserDataService,
    private readonly hotkeys: Hotkeys) {

    this.subscriptions.push(this.hotkeys.addShortcut({ keys: 'shift.control.n', description: 'Create Implementation Constraint' }).subscribe(() => {
      this.createImplementationConstraint();
    }));
    this.detailedSheetFields = [
      { title: 'ID', key: 'id', type: FieldType.Text, readonly: true },
      { title: 'Name', key: 'name', type: FieldType.Text, readonly: false },
      { title: 'Description', key: 'description', type: FieldType.Text_Variable, readonly: false, minRows: 3, maxRows: 10 },
      {
        title: 'Controller Constraint Name', key: 'controllerConstraintDependency', type: FieldType.Text, readonly: true,
        shortcutButton: { title: 'Go to Controller Constraints', routerLink: (): string => 'project/' + this.projectId + '/controller-constraints/' }
      },
      { title: 'State', key: 'state', type: FieldType.StateButtonGroup, readonly: false },
    ];
    this.deleteImplementationConstraint = this.deleteImplementationConstraint.bind(this);
    this.route.parent.params.subscribe((value: any) => {
      this.projectId = value.id;
      this.initColumns();
      this.loadData();
    });

    // Websocket implementation
    this.authService.getProjectToken().then((token: string) => {
      this.wsService.connect('subscribe', IMPLEMENTATION_CONSTRAINT, token, (data) => {
        this.loadData();
        if (this.OwnChange === false) {
          this.messageService.add({ severity: 'info', summary: 'User ' + data.displayName + ' updated the data!' });
        }
      });
    }).catch((err: HttpErrorResponse) => {
      console.log(err);
    });
  }

  /**
  * This methods is invoked by the main table and deletes all constraints indicated by event.
  * The data in the view is reloaded afterwards.
  * @param $event a list of elements. Each element e has a field e.id corresponding to an implemenation constraint's id.
  */
  async deleteImplementationConstraint($event): Promise<boolean> {
    this.OwnChange = true;
    const list: any[] = $event;
    if (list.length === 0) {
      return;
    }
    const promiseList: Promise<boolean>[] = list.map(async (elem: any) => {
      await this.lockService.lockEntity(this.projectId, {
        id: elem.id,
        entityName: this.detailedSheetTitle.toLowerCase(),
        expirationTime: DetailedSheetUtils.calculateLockExpiration()
      });
      return this.implementationConstraintDataService.deleteImplementationConstraint(this.projectId, elem.id);
    });
    /* collect all responses from failed promises */
    const failedResponses: Error[] = [];
    for (const prom of promiseList) {
      try {
        await prom;
      } catch (e) {
        failedResponses.push(e);
      }
    }
    if (failedResponses.length !== 0) {
      for (const response of failedResponses) {
        this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_DELETE, detail: response.message });
      }
    }
    if (failedResponses.length < list.length) {
      /* some must have worked, we have fewer errors than requests */
      this.messageService.add({ severity: 'success', summary: ENTITY_SUCCESSFUL_DELETE });
    }
    this.OwnChange = false;
    this.loadData();
  }

  createImplementationConstraint(): void {
    this.sheetMode = SheetMode.New;
    this.implementationConstraint = {
      id: '', description: '', name: '',
      lsId: '', controllerConstraint: 'N/A',
      state: '',
    };
    this.ucaDataService.getControllerConstraintByUnsafeControlAction(this.projectId, this.selectedUca.parentId, this.selectedUca.id)
      .then((value: ControllerConstraintResponseDTO) => {
        if (value !== null) {
          this.implementationConstraint.name = value.name;
        }
      });
  }

  saveImplementationConstraint(implConst: DetailData): void {
    console.warn('Controller Constraint Name: ' + implConst.ent.controllerConstraint);

    if (!this.validateForm(implConst.ent.name)) {
      this.messageService.add({ severity: 'warn', summary: DETAILEDSHEET_EXPECTED_NAME_NOT_NULL });
      this.disableSaveButton = false;
      return;
    }
    this.filterService.ClearSelectionEmitter.emit(true);
    this.OwnChange = true;
    this.disableSaveButton = true;

    if (implConst.mode === SheetMode.EditWithLock) {
      const implConstEntity: ImplementationConstraintRequestDTO = {
        projectId: this.projectId, id: implConst.ent.id, description: implConst.ent.description, name: implConst.ent.name, lossScenarioId: this.selectedLossScenario.id,
        controllerConstraint: implConst.ent.controllerConstraint,
        state: implConst.ent.state,
      };
      const promises: Promise<ImplementationConstraintResponseDTO | boolean>[] = [];
      promises.push(this.implementationConstraintDataService.alterImplementationConstraint(this.projectId, implConst.ent.id, implConstEntity));
      Promise.all(promises)
        .then(() => {
          this.messageService.add({ severity: 'success', summary: DETAILEDSHEET_SUCCESSFUL_EDIT });
          this.closeSheet();
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: DETAILEDSHEET_FAILED_EDIT, detail: response.message });
        }).finally(() => {
          this.OwnChange = false;
          this.disableSaveButton = false;
          this.loadData();
        });
    } else if (implConst.mode === SheetMode.New) {
      const implConstEntity: ImplementationConstraintRequestDTO = {
        projectId: this.projectId, id: implConst.ent.id, lossScenarioId: this.selectedLossScenario.id, description: implConst.ent.description, name: implConst.ent.name,
        controllerConstraint: implConst.ent.controllerConstraint,
        state: implConst.ent.state,
      };
      this.implementationConstraintDataService.createImplementationConstraint(this.projectId, implConstEntity)
        .then(() => {
          this.messageService.add({ severity: 'success', summary: DETAILEDSHEET_SUCCESSFUL_CREATION });
          this.closeSheet(true);
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: DETAILEDSHEET_FAILED_CREATION, detail: response.message });
        }).finally(() => {
          this.OwnChange = false;
          this.disableSaveButton = false;
          this.loadData();
        });
    }
  }

  getSelectedEntities($event: any): void {
    this.selectImplementationConstraintEntity = new SelectedEntity<Object>($event, implementationConstraintTitle,
      this.projectId,
      false,
      false,
      true);
  }

  /**
  * Initialize columns of main table which display information about the loaded implementation constraints.
  */
  private initColumns(): void {
    // Note Views with navigation table have checkbox(leftmost column) width  10% for aesthetic reasons
    this.columns = [
      {
        key: 'select',
        title: 'Select',
        type: ColumnType.Checkbox,
        style: { width: '10%' }
      }, {
        key: 'id',
        title: 'ID',
        type: ColumnType.Text,
        style: { width: '5%' }
      }, {
        key: 'name',
        title: 'Implementation-Constraint-Name',
        type: ColumnType.Text,
        style: { width: '55%' }
      },
      {
        key: 'state',
        title: 'State',
        type: ColumnType.StateIcon,
        style: { width: '15%' }
      },
      {
        key: 'icon',
        title: 'Edited-By',
        type: ColumnType.Icon,
        userName: 'lastEditor',
        style: { width: '8%' }
      }, {
        key: 'lastEdited',
        title: 'Last-Edited',
        type: ColumnType.Date_Time,
        style: { width: '14%' }
      }, {
        key: 'edit',
        title: 'Edit',
        type: ColumnType.Button,
        style: { width: '3%' }
      }, {
        key: 'show',
        title: 'Show',
        type: ColumnType.Button,
        style: { width: '3%' }
      }
    ];
  }

  changeEntity(event: { list: ImplementationConstraintResponseDTO[], state: string }) {
    event.list.forEach((element: ImplementationConstraintResponseDTO) => {

      let incomplete: incompleteEntityDTO = { entityName: 'implementation_constraint', parentId: undefined, state: event.state };
      this.incompleteEntityService.updateState(this.projectId, element.id, incomplete).then(() => {
        this.loadData();
      }).catch((reason: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: CHANGED_STATE_FAILED, detail: reason.message });
      });
    });
  }
  /**
  * Asynchronously loads UCAs and populates left-most column with them. Set this.selectedUca to first UCA retrieved.
  * If none exists this.selectedUca will remain undefined after the method returns.
  * If no UCAs exist the column header is set to indicate exactly that and a notification highlighting
  * the necessitiy of creating a UCA is displayed.
  */
  async loadUcas(): Promise<void | UcaResponseDTO> {
    this.navigationTableDefinitions[0] = {
      dataSource: [], style: { width: '50%' }, columnHeaderName: 'No UCAs found',
      columnHeaderButton: { title: 'Go to UCAs', routerLink: 'project/' + this.projectId + '/uca-table/' }
    };
    this.navigationTableDefinitions[1] = {
      dataSource: [], style: { width: '50%' }, columnHeaderName: 'No Loss Scenarios found',
      columnHeaderButton: { title: 'Go to Loss Scenarios', routerLink: 'project/' + this.projectId + '/loss-scenario-table/' }
    };
    return this.ucaDataService.getAllUcas(this.projectId, { 'orderBy': 'name' })
      .then((value: UcaResponseDTO[]) => {
        if (value !== null && value.length > 0) {
          this.navigationTableDefinitions[0].dataSource = value;
          this.navigationTableDefinitions[0].columnHeaderName = 'UCA';
          this.selectedUca = value[0];
          return this.selectedUca;
        } else {
          this.messageService.add({ severity: 'warn', life: 10000, summary: 'Create a UCA to be able to add implementation constraints to it!' });
        }
      }).catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_LOADING, detail: response.message });
      });
  }

  /**
  * Asynchronously loads Loss Scenarios belonging to this.selectedUCA and populates second column with them.
  * Set this.selectedLossScenario to first loss scenario retrieved. If none exists this.selectedLossScenario
  * will remain undefined holds after this method returns.
  * If no Loss Scenarios exist the column header is set to indicate exactly that and a notification highlighting
  * the necessitiy of creating a Loss Scenario is displayed.
  */
  private async loadLossScenariosBySelectedUca(): Promise<void> {
    this.implementationConstraints = [];
    this.implementationConstraint = undefined;
    this.selectedLossScenario = undefined;
    this.navigationTableDefinitions[1] = {
      dataSource: [], style: { width: '50%' }, columnHeaderName: 'No Loss Scenarios found',
      columnHeaderButton: { title: 'Go to Loss Scenarios', routerLink: 'project/' + this.projectId + '/loss-scenario-table/' }
    };
    return this.lossScenarioDataService.getLossScenariosByUcaAndCAId(this.projectId, this.selectedUca.id, this.selectedUca.parentId)
      .then((value: LossScenarioResponseDTO[]) => {
        if (value !== null && value.length > 0) {
          this.navigationTableDefinitions[1].dataSource = value;
          this.navigationTableDefinitions[1].columnHeaderName = 'Loss Scenario';
          this.selectedLossScenario = value[0];
        } else {
          this.messageService.add({ severity: 'warn', life: 10000, summary: 'Create a loss scenario to be able to add implementation constraints to it!' });
        }
      }).catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: 'Loading loss scenarios failed', detail: response.message });
      });
  }

    /**
    * Asynchronously loads Implementation Constraints and populates the main table with them.
    */
    private async loadImplementationConstraints(): Promise<void> {
      // Load implementation constraints according to selected loss scenario
      let userRequest: UserDisplayRequestDTO = new UserDisplayRequestDTO;
      let userId: string[] = [];
      let valueIndex: string[] =[];
      return this.implementationConstraintDataService.getImplementationConstraintsByLsId(this.projectId, this.selectedLossScenario.id)
      .then((value: ImplementationConstraintResponseDTO[]) => {
        if (value !== null) {
          value.forEach(element => {
            if(element.lastEditorId !== null && element.lastEditorId !== ''){
              userId.push(element.lastEditorId);
              valueIndex.push(value.indexOf(element).toString());
            } else {
              element.lastEditor = NO_USER;
              element.icon = this.userDataService.getDefaultOrDeleteIcon(element.lastEditor);
            }
        }
        );
        if(valueIndex.length !== 0){
          userRequest.userIds = userId;
          this.userDataService.getUserDisplay(userRequest).then((map:Map<string, string[]>) =>{
            valueIndex.forEach(element =>{

                let nameIcon = map.get(value[element].lastEditorId);

                if(nameIcon[0] !== null){

                  value[element].lastEditor = nameIcon[0];
                }
                if(nameIcon[1] !== ''){
                  value[element].icon = nameIcon[1];
                }else{
                  value[element].icon = this.userDataService.getDefaultOrDeleteIcon(nameIcon[1]);
                }


            });
          });

        }
        this.implementationConstraints = value;
      }
      }).catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_LOADING, detail: response.message });
      });
  }

  /**
  * Populate the navigation table columns and main table by loading data from backend.
  * Notify
  * Hint: this.selectedUca and this.selectedLossScenario are set by this.loadUcas(),
  * this.loadLossScenariosBySelectedUca() if any Ucas/Loss Scenarios exist.
  */
  private async loadData(): Promise<void> {
    if (this.selectedUca === undefined) {
      await this.loadUcas();
    }
    if (this.selectedUca !== undefined && this.selectedLossScenario === undefined) {
      await this.loadLossScenariosBySelectedUca();
    }
    if (this.selectedUca !== undefined && this.selectedLossScenario !== undefined) {
      await this.loadImplementationConstraints();
    }
    this.updateSelectedItemSubject.next({ element: this.selectedUca, columnNumber: 0 });
    this.updateSelectedItemSubject.next({ element: this.selectedLossScenario, columnNumber: 1 });
  }

  /**
  * This method is invoked when a UCA or Loss Scenario is clicked.
  * this.selectedUca or this.selectedLossScenario is set to reflect the selection in this component.
  * Afterwards the data for the updated UCA and Loss Scenario selection is loaded.
  * @param tupleOfElemAndTableNumber contains the UCA/Loss Scenario data of the clicked element and is used to decide
  * which column was clicked
  */
  onClickedFilterElement(tupleOfElemAndTableNumber: [FilteringTableElement, number]): void {
    this.filterService.ClearSelectionEmitter.emit(true);
    if (tupleOfElemAndTableNumber[1] === 0) {
      // Leftmost UCA table was clicked
      this.selectedUca = tupleOfElemAndTableNumber[0] as UcaResponseDTO;
      // undefining is crucical - otherwise loadData will not load the loss scenarios corresponding to this uca
      this.selectedLossScenario = undefined;
    } else if (tupleOfElemAndTableNumber[1] === 1) {
      // Second table was clicked
      this.selectedLossScenario = tupleOfElemAndTableNumber[0] as LossScenarioRequestDTO;
    }
    this.loadData();
  }

  /**
  * Toggles the DetailedSheet
  * 1. Click Edit-/View button or on table row --> onClickEdit(...)  of main-Table is called
  * 2. onClickEdit fires toggleEvent EventEmitter
  * 3. toggleEvent emitter triggers toggleSheet function in the .html-part of the component
  * 4. In toggleSheet function the detailed sheet content can be accessed (data is the selected element)
  * @param data: the data coming from the mainTable
  */
  toggleSheet(data): void {
    this.disableSaveButton = false;
    let id: string;
    // holding reference to data.entity causes impl constraint in maintable to change when changes
    // are made in detailed sheet. Thus we spread the properties of data.entity to get copies of them into a new object.
    // This stops working when data.entity becomes a composte object holding other objects as properties.
    this.implementationConstraint = { ...data.entity };
    this.implementationConstraint.controllerConstraint = 'N/A';

    if (this.implementationConstraint) {
      id = data.entity.id;
    }
    if (this.sheetMode === SheetMode.Edit) {
      this.closeSheet(false, SheetMode.View);
    }
    this.sheetMode = DetailedSheetUtils.toggle(id, data.entity.id, this.sheetMode, data.mode);
    this.ucaDataService.getControllerConstraintByUnsafeControlAction(this.projectId, this.selectedUca.parentId, this.selectedUca.id)
      .then((value: ControllerConstraintResponseDTO) => {
        if (value !== null) {
          this.implementationConstraint.controllerConstraint = value.name;
        }
      });
  }

  /**
  * @param isUnlocked indicates whether the entity loaded into the Detailed Sheet is locked.
  * @param sheetMode current sheet mode before closing
  */
  closeSheet(isUnlocked: boolean = false, sheetMode: SheetMode = SheetMode.Closed): void {
    if (!isUnlocked && this.sheetMode === SheetMode.Edit) {
      const unlockDTO: UnlockRequestDTO = { id: this.implementationConstraint.id, entityName: this.detailedSheetTitle.toLowerCase() };
      this.lockService.unlockEntity(this.projectId, unlockDTO).then(() => {
        this.sheetMode = sheetMode;
      }).catch(() => {
        this.sheetMode = sheetMode;
      });
    } else {
      this.sheetMode = sheetMode;
    }
    this.disableSaveButton = false;
  }

  editSheet(): void {
    this.sheetMode = SheetMode.Edit;
  }
  /**
  * Clears the selection before component is destroyed.
  */
  ngOnDestroy(): void {
    this.filterService.ClearSelectionEmitter.emit(true);
    this.subscriptions.forEach((subscription: Subscription) => subscription.unsubscribe());
  }

  /**
   * Returns true if trimmed versions of both name and description of implementation constraint are not null;
   */
  private validateForm(name: String): boolean {
    return this.isValid(name.trim());
  }

  /**
  * Returns true if string is not undefined and not empty
  * @param string The string to check
  */
  private isValid(string: String): boolean {
    if (string !== undefined) {
      // Explicit cast to string since 'string' turns out to be int sometimes. Then string.length is undefined.
      return String(string).length > 0;
    }
    return false;
  }
}
