import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, OnDestroy, Output } from '@angular/core';
import { MatDialog } from '@angular/material';
import { ActivatedRoute } from '@angular/router';
import { MessageService } from 'primeng/api';
import { LossScenarioDataService } from 'src/app/services/dataServices/loss-scenario.service';
import { LockResponse, LockService } from '../../services/dataServices/lock.service';
import { CreateLossScenarioComponent } from '../create-loss-scenario/create-loss-scenario.component';
import { SheetMode } from './../../common/detailed-sheet/detailed-sheet.component';
import { DetailedSheetUtils } from './../../common/detailed-sheet/utils/detailed-sheet-utils';
import { DetailedField, FieldType } from './../../common/entities/detailed-field';
import { ColumnType, TableColumn } from './../../common/entities/table-column';
import { AuthService } from './../../services/auth.service';
import { ControlActionDataService } from './../../services/dataServices/control-structure/control-action-data.service';
import { UcaDataService } from './../../services/dataServices/uca-data.service';
import { FilterService } from './../../services/filter-service/filter.service';
import { WebsocketService } from './../../services/websocket.service';
import {
  BoxEntityDTO, ControlActionDTO, ControllerDTO, FeedbackDTO, InputArrowRequestDTO, LockRequestDTO, LOSS_SCENARIO,
  LossScenarioRequestDTO, LossScenarioResponseDTO, SensorDTO, UcaResponseDTO
} from './../../types/local-types';
import { Subject, Subscription } from 'rxjs';
import { NavigationTableElement } from 'src/app/common/navigation-table/navigation-table.component';
import { Hotkeys } from 'src/app/hotkeys/hotkeys.service';
import {
  IncompleteEntitiesService, incompleteEntityDTO
} from 'src/app/services/dataServices/incomplete-entities-data.service';
import { UserDataService, UserDisplayRequestDTO } from 'src/app/services/dataServices/user-data.service';
import { CHANGED_STATE_FAILED, ENTITY_FAILED_LOADING, ENTITY_SUCCESSFUL_DELETE, NO_USER } from 'src/app/globals';
import { ImplementationConstraintDataService } from '../../services/dataServices/implementation-constraint-data.service';
import { SelectedEntity } from '../dependent-element-tree/dependent-elements-types.component';

interface LossScenarioForDetailedSheet {
  id: string;
  name: string;
  description: string;
  parentId: string;
  parentName: string;
  state: string;
}

@Component({
  selector: 'app-loss-scenario-table',
  templateUrl: './loss-scenario-table.component.html',
  styleUrls: ['./loss-scenario-table.component.css']
})
// TODO: what happens if no controlAction is created or no categrory has data ?

/**
 * Class with the table, where all loss-scenarios are show. The user can create, edit and delete a scenario
 */
export class LossScenarioTableComponent implements OnDestroy {

  SheetMode: typeof SheetMode = SheetMode;
  // seperate attributes which are for detailed sheet, table or nav table
  private projectId: string;
  columns: TableColumn[] = [];
  detailedColumns: TableColumn[] = [];
  lossScenario: LossScenarioForDetailedSheet;
  lossScenarios: LossScenarioResponseDTO[] = [];
  fields: DetailedField[];
  description: string;
  title: string = DetailedSheetUtils.generateSheetTitle(LOSS_SCENARIO);
  navigationTableDefinitions = [];
  FilterService = null;
  advancedFilterSubscription = null;
  additionalDeleteMessage: string = 'By deleting an Unsafe-Control-Action (UCA) the corresponding Controller-Constraint (CC) will be deleted too!';
  // TODO: Create Type of Categories
  origNavData = [];
  allControllers = [];
  sheetMode: SheetMode = SheetMode.Closed;
  private OwnChange: boolean = false;
  disableSaveButton: boolean = false;
  headCategory: string;
  // Attributes for the selected Categorie
  selectedHeadCategory: number;
  selectedSubCategory: number;
  // Attributes for all Loss Scenario
  selectedControlAction: ControlActionDTO;
  selectedUca: UcaResponseDTO;
  nameLS: string = '';
  id: string;
  descriptionGeneral: number;
  // selectedController for categorie 0,1,3
  selectedController: ControllerDTO;
  // Attributes for categorie 0
  descriptionFailure: string;
  // Additionally Attributes for categorie 1
  attacker: string;
  // Attributes for categorie 2
  selectedSourceControlller: ControllerDTO;
  selectedTargetController: ControllerDTO;
  // Attributes for categorie 3
  selectedInput: InputArrowRequestDTO;
  selectedFeedback: FeedbackDTO;
  selectedSensor: SensorDTO;
  descriptionReason: string;
  // Additionally Attributes for categorie 1 and 3
  selectedReason: string;
  // Additionally Attributes for categorie 2 and 3
  selectedInputBox: BoxEntityDTO;
  descriptionMisinterpretedHypothesis: string;
  selectLossScenarioEntity: SelectedEntity<Object>;

  @Output() selectEntityMessage: any = new EventEmitter<SelectedEntity<Object>>();

  subscriptions: Subscription[] = [];
  updateSelectedItemSubject: Subject<{ element: NavigationTableElement, columnNumber: number }> =
    new Subject<{ element: NavigationTableElement, columnNumber: number }>();

  constructor(
    public dialog: MatDialog,
    private readonly lossScenarioDataService: LossScenarioDataService,
    private readonly controlActionDataService: ControlActionDataService,
    private readonly ucaDataService: UcaDataService,
    private readonly route: ActivatedRoute,
    private readonly filterService: FilterService,
    private readonly messageService: MessageService,
    private readonly wsService: WebsocketService,
    private readonly authService: AuthService,
    private readonly lockService: LockService,
    private readonly incompleteEntityService: IncompleteEntitiesService,
    private readonly userDataService: UserDataService,
    private readonly implementationConstraintDataService: ImplementationConstraintDataService,
    private readonly hotkeys: Hotkeys) {

    this.subscriptions.push(this.hotkeys.addShortcut({ keys: 'shift.control.n', description: 'Create Loss-Scenario' }).subscribe(() => {
      this.createLossScenario();
    }));
    this.fields = [
      { title: 'Control Action', key: 'selectedControlAction', type: FieldType.Text, readonly: true },
      { title: 'UCA ', key: 'selectedUca', type: FieldType.Text, readonly: true },
      { title: 'ID', key: 'id', type: FieldType.Text, readonly: true },
      { title: 'Name', key: 'name', type: FieldType.Text, readonly: true },
      { title: 'Description', key: 'description', type: FieldType.Text_Variable, readonly: true, minRows: 3, maxRows: 10 },
      { title: 'Type', key: 'lsType', type: FieldType.Text_Variable, readonly: true, minRows: 3, maxRows: 10 },
      { title: 'State', key: 'state', type: FieldType.StateButtonGroup, readonly: false },
    ];
    this.deleteLossScenario = this.deleteLossScenario.bind(this);
    this.route.parent.params.subscribe((value: any) => {
      this.projectId = value.id;
      this.initColumns();
      this.loadData();
    });

    // Websocket implementation
    this.authService.getProjectToken().then((token: string) => {
      this.wsService.connect('subscribe', LOSS_SCENARIO, token, (data) => {
        this.loadData();
        if (this.OwnChange === false) {
          this.messageService.add({ severity: 'info', summary: 'User ' + data.displayName + ' updated the data!' });
        }
      });
    }).catch((err: HttpErrorResponse) => {
      console.log(err);
    });
  }

  // responsible for filtering -> therefore responsible for delegating filters to navTable (or mainTable currently not may be in the future)
  // rsponsible to tell view(ucaHtml) which navTable rows are selected and should be highlighted
  // TODO REWORK THIS !
  applyAdvancedFilter(obj): void {
    let filteredControlActions = [];
    const promises = [];
    for (const con of obj) {
      if (con.boxId !== null) {
        promises.push(this.controlActionDataService.getControlActionBySourceBoxId(this.projectId, con.boxId));
      }
    }
    Promise.all(promises).then(value => {
      for (const val of value) {
        filteredControlActions = filteredControlActions.concat(val);
      }
      if (obj.length > 0) {
        this.navigationTableDefinitions = this.navigationTableDefinitions.slice();
        this.navigationTableDefinitions[0].dataSource = filteredControlActions;
        const arr = this.navigationTableDefinitions[0].dataSource as Array<ControlActionDTO>;
        // Case 1 after filtering the filtered data doesn't contain currently selectedControlAction
        if (arr.includes(this.selectedControlAction) === false) {
          this.selectedControlAction = undefined;
          // if filtered data isn't empty then just select first elem as new selected elem
          if (arr !== undefined && arr.length > 0) {
            this.selectedControlAction = filteredControlActions[0];
            this.navigationTableDefinitions[0].selectedElement = this.selectedControlAction;
            this.loadData();
          } else {
            // else filtered data is empty then only display one empty navigationTable,maintable and remove categories navigation
            this.navigationTableDefinitions = [];
            this.navigationTableDefinitions[0] = {
              dataSource: [],
              style: { width: '100%' },
              columnHeaderName: 'No Control Action Found'
            };
            this.lossScenarios = [];
          }
        }
      } else {
        this.selectedControlAction = undefined;
        this.selectedUca = undefined;
        this.loadData();
      }
    }).catch((reason: HttpErrorResponse) => {
      // handle error
      console.log(reason);
    });

    // FIX ME  currently navTable automatically chooses the first element as selected if the passed navigationTableDefinition was modified
    // therefore here  we set variables to remember which element was selected
    if (this.selectedControlAction !== undefined) {
      this.navigationTableDefinitions[0].selectedElement = this.selectedControlAction;
      this.navigationTableDefinitions[0].columnHeaderName = 'Control Actions';
      if (this.selectedUca !== undefined) {
        this.navigationTableDefinitions[1].selectedElement = this.selectedUca;
      }
    }
  }

  // // this is the handler/callback function for the clickedNavigationEmitter defined in the navigationTable
  // // if clickedNavigationEmitter is fired onClickedNavigation will be called
  // // clickedNavigationEmitter will pass its parameters to onClickedNavigation(tupleOfElemAndTableNumber)
  onClickedNavigation(tupleOfElemAndTableNumber): void {
    this.filterService.ClearSelectionEmitter.emit(true);
    if (tupleOfElemAndTableNumber[1] === 0) {
      // First table was clicked
      this.selectedControlAction = tupleOfElemAndTableNumber[0];
      this.loadUCA(this.selectedControlAction);
    } else if (tupleOfElemAndTableNumber[1] === 1) {
      // Second table was clicked
      this.selectedUca = tupleOfElemAndTableNumber[0];
    }
    this.loadData();
  }

  async deleteLossScenario($event): Promise<void> {
    this.OwnChange = true;
    const list: any[] = $event;
    if (list.length === 0) {
      return;
    }

    const promiseList = list.map(async (elem: any) => {
      let lockDTO: LockRequestDTO = {
        id: elem.id,
        expirationTime: DetailedSheetUtils.calculateLockExpiration(),
        entityName: this.title.toLowerCase()
      };

      await this.lockService.lockEntity(this.projectId, lockDTO)
        .then(async (success: LockResponse) => {
          await this.lossScenarioDataService.deleteLossScenario(this.projectId, elem.id);
        }).catch((error: Error) => {
          this.messageService.add({ severity: 'error', summary: 'Error on getting lock', life: 4000, detail: error.message });
          console.error(error);
        });
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
        this.messageService.add({ severity: 'error', summary: 'Error on deleting loss scenario', detail: response.message });
      }
    }

    if (failedResponses.length < list.length) {
      /* some must have worked, we have fewer errors than requests */
      this.messageService.add({ severity: 'success', summary: ENTITY_SUCCESSFUL_DELETE });
    }
    this.OwnChange = false;
    this.loadData();
  }

  /**
   * Open and close the dialog to create a loss Scenario
   */
  createLossScenario(): void {
    const emptyLS: LossScenarioRequestDTO = {
      name: '',
      id: '',
      description: '',
      projectId: '',
      ucaId: '',
      headCategory: '',
      subCategory: '',
      state: 'DOING',
    };

    // Open the dialog and give over the information with the attribute data
    const dialogRef = this.dialog.open(CreateLossScenarioComponent, {
      width: '90%',
      height: '90%',
      data: { selectedControlAction: this.selectedControlAction, selectedUca: this.selectedUca, projectId: this.projectId, sheetMode: SheetMode.New, lossScenario: emptyLS },
      disableClose: true
    });

    // To refresh loss scenario table after dialog has been closed
    dialogRef.afterClosed().subscribe(value => {
      this.sheetMode = SheetMode.Closed;
      this.loadData();
    });
  }

  /**
   * Method open a loss-Scenario, which exists
   * @param lsId: The Id from the Loss Scenario
   * @param sheetMode
   */
  openLossScenario(lsId: string, sheetMode: SheetMode): void {
    // Load loss to edit and pass its data to create-loss-scenario component
    this.lossScenarioDataService.getLossScenarioById(this.projectId, lsId)
      .then(value => {
        const val: LossScenarioResponseDTO = value;
        const dialogRef = this.dialog.open(CreateLossScenarioComponent, {
          width: '90%',
          height: '90%',
          data: { selectedControlAction: this.selectedControlAction, selectedUca: this.selectedUca, projectId: this.projectId, sheetMode: sheetMode, lossScenario: value },
          disableClose: true
        });

        // To refresh loss scenario table after dialog has been closed
        dialogRef.afterClosed().subscribe(value => {
          this.sheetMode = SheetMode.Closed;
          this.loadData();
        });
      }).catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_LOADING, detail: response.message });
      });
  }

  /**
   * Initialisation the special columns of the loss scenario table
   */
  private initColumns(): void {
    // Note Views with navigation table have checkbox width  10% for aesthetic reasons
    this.columns = [
      {
        key: 'select',
        title: 'Select',
        type: ColumnType.Checkbox,
        style: { 'width': '10%' }
      }, {
        key: 'id',
        title: 'LS-ID',
        type: ColumnType.Text,
        style: { 'width': '5%' }
      }, {
        key: 'name',
        title: 'LS-Name',
        type: ColumnType.Text,
        style: { 'width': '30%' }
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
        userName: 'lastEditedBy',
        style: { width: '7%' }
      },
      {
        key: 'lastEdited',
        title: 'Last-Edited',
        type: ColumnType.Date_Time,
        style: { 'width': '13%' }
      }, {
        key: 'edit',
        title: 'Edit',
        type: ColumnType.Button,
        style: { 'width': '3%' }
      }, {
        key: 'show',
        title: 'Show',
        type: ColumnType.Button,
        style: { 'width': '3%' }
      }
    ];
  }

  changeEntity(event: { list: LossScenarioResponseDTO[], state: string }) {
    event.list.forEach((element: LossScenarioResponseDTO) => {

      let incomplete: incompleteEntityDTO = { entityName: 'LossScenario', parentId: undefined, state: event.state };

      this.incompleteEntityService.updateState(this.projectId, element.id, incomplete).then(value => {
        this.loadData();
      }).catch((reason: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: CHANGED_STATE_FAILED, detail: reason.message });
      })
    });
  }

  getSelectedEntities($event: any): void {
    this.selectLossScenarioEntity = new SelectedEntity<Object>($event,
      this.title,
      this.projectId,
      false,
      false,
      true);
  }

  /**
   * Load the controlActions and the Scenario (All the datas where are used for the loss scenario table)
   */
  private loadData(): void {
    if (this.selectedControlAction !== undefined && this.selectedUca !== undefined) {
      let userRequest: UserDisplayRequestDTO = new UserDisplayRequestDTO;
      let userId: string[] = [];
      let valueIndex: string[] =[];
      // Load loss-scenario(s) according to selection (controller action & UCA)
      this.lossScenarioDataService.getLossScenariosByUcaAndCAId(this.projectId, this.selectedUca.id, this.selectedControlAction.id)
        .then((value: LossScenarioResponseDTO[]) => {
          if (value !== null) {
            value.forEach(element => {
              if(element.lastEditorId !== null && element.lastEditorId !== ''){
                userId.push(element.lastEditorId);
                valueIndex.push(value.indexOf(element).toString());

              }else{
                element.lastEditedBy = NO_USER;
                element.icon = this.userDataService.getDefaultOrDeleteIcon(element.lastEditedBy);
              }
          }
          );
          if(valueIndex.length !== 0){
            userRequest.userIds = userId;
            this.userDataService.getUserDisplay(userRequest).then((map:Map<string, string[]>) =>{
              valueIndex.forEach(element =>{

                  let nameIcon = map.get(value[element].lastEditorId);

                  if(nameIcon[0] !== null){

                    value[element].lastEditedBy = nameIcon[0];
                  }
                  if(nameIcon[1] !== ''){
                    value[element].icon = nameIcon[1];
                  }else{
                    value[element].icon = this.userDataService.getDefaultOrDeleteIcon(nameIcon[1]);
                  }
              });
            });

          }
          this.lossScenarios = value;
          }
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_LOADING, detail: response.message });
        });
    } else {
      // Load Control Action
      if (this.selectedControlAction === undefined) {
        // Set up first table & load data for it
        this.controlActionDataService.getAllControlActions(this.projectId, {})
          .then(value => {
            this.navigationTableDefinitions[0] = {
              dataSource: value, style: { width: '50%' }, columnHeaderName: 'Control Action',
              columnHeaderButton: { title: 'Go to Control Actions', routerLink: 'project/' + this.projectId + '/control-action/' }
            };
            if (value !== null && value.length > 0) {
              this.selectedControlAction = value[0];
              this.updateSelectedItemSubject.next({ element: value[0], columnNumber: 0 });
              this.loadData();
            } else {
              this.navigationTableDefinitions[0].columnHeaderName = 'No Control Action found';
              this.messageService.add({ severity: 'warn', life: 10000, summary: 'Create a Control Action to be able to add loss-scenario to it!' });
            }
          }).catch((response: HttpErrorResponse) => {
            this.messageService.add({ severity: 'error', summary: 'Loading Control Action failed', detail: response.message });
          });
      }

      // load UCA by ControlAction ID
      if (this.selectedUca === undefined && this.selectedControlAction !== undefined) {
        // Set up second table & load data for it
        this.ucaDataService.getAllUcasByControlActionId(this.projectId, this.selectedControlAction.id, {})
          .then(value => {
            this.navigationTableDefinitions[1] = {
              dataSource: value, style: { width: '50%' }, columnHeaderName: 'UCA',
              columnHeaderButton: { title: 'Go to UCAs', routerLink: 'project/' + this.projectId + '/uca-table/' }
            };
            if (value !== null && value.length > 0) {
              this.selectedUca = value[0];
              this.updateSelectedItemSubject.next({ element: value[0], columnNumber: 1 });
              this.loadData();
            } else {
              this.navigationTableDefinitions[1].columnHeaderName = 'No UCA found';
              this.messageService.add({ severity: 'warn', life: 10000, summary: 'Create a UCA to be able to add loss-scenarios to it!' });
            }
          }).catch((response: HttpErrorResponse) => {
            this.messageService.add({ severity: 'error', summary: 'Loading UCA failed', detail: response.message });
          });
      }
    }
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
    this.sheetMode = data.mode;

    if (this.sheetMode === SheetMode.New) {
      this.createLossScenario();
    } else {
      this.openLossScenario(data.entity.id, this.sheetMode);
    }
  }

  /**
   * Set the SheetMode to Edit
   */
  editSheet(): void {
    this.sheetMode = SheetMode.Edit;
  }

  ngOnDestroy(): void {
    this.filterService.ClearSelectionEmitter.emit(true);
    if (this.advancedFilterSubscription) {
      this.advancedFilterSubscription.unsubscribe();
    }
    this.subscriptions.forEach((subscription: Subscription) => subscription.unsubscribe());
  }

  /**
   * Method loads the UCAs which belongs to a selected ControlAction,
   * is called in onClickedNavigation only right now
   */
  loadUCA(selConAn: ControlActionDTO): void {

    this.ucaDataService.getAllUcasByControlActionId(this.projectId, selConAn.id, {})
      .then(value => {
        this.navigationTableDefinitions[1] = {
          dataSource: value, style: { width: '50%' }, columnHeaderName: 'UCA',
          columnHeaderButton: { title: 'Go to UCAs', routerLink: 'project/' + this.projectId + '/uca-table/' }
        };
        if (value !== null && value.length > 0) {
          this.selectedUca = value[0];
          this.updateSelectedItemSubject.next({ element: value[0], columnNumber: 1 });
          // this.loadData();
        } else {
          this.navigationTableDefinitions[1].columnHeaderName = 'No UCA found';
          this.messageService.add({ severity: 'warn', life: 10000, summary: 'Create a UCA to be able to add loss-scenarios to it!' });
        }
      }).catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: 'Loading UCA failed', detail: response.message });
      });
  }
}
