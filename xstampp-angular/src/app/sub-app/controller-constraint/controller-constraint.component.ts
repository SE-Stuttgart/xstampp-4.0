import { HttpErrorResponse } from '@angular/common/http';
import {Component, EventEmitter, OnDestroy, Output} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Ng4LoadingSpinnerService } from 'ng4-loading-spinner';
import { MessageService } from 'primeng/api';
import { Subscription } from 'rxjs';
import { CHANGED_STATE_FAILED, DELETED_USER, DETAILEDSHEET_EXPECTED_NAME_NOT_NULL, DETAILEDSHEET_FAILED_EDIT, DETAILEDSHEET_FAILED_LOADING_CHIPS, DETAILEDSHEET_SUCCESSFUL_EDIT, ENTITY_FAILED_DELETE, ENTITY_FAILED_LOADING, ENTITY_SUCCESSFUL_DELETE, NO_USER } from 'src/app/globals';
import { Hotkeys } from 'src/app/hotkeys/hotkeys.service';
import { IncompleteEntitiesService, incompleteEntityDTO } from 'src/app/services/dataServices/incomplete-entities-data.service';
import { UserDataService, UserDisplayRequestDTO } from 'src/app/services/dataServices/user-data.service';
import { SheetMode } from '../../common/detailed-sheet/detailed-sheet.component';
import { DetailedSheetUtils } from '../../common/detailed-sheet/utils/detailed-sheet-utils';
import { FieldType } from '../../common/entities/detailed-field';
import { ColumnType, TableColumn } from '../../common/entities/table-column';
import { ControlActionDataService } from '../../services/dataServices/control-structure/control-action-data.service';
import { ControllerConstraintDataService } from '../../services/dataServices/controller-constraint-data.service';
import { LockService } from '../../services/dataServices/lock.service';
import { UcaDataService } from '../../services/dataServices/uca-data.service';
import { FilterService } from '../../services/filter-service/filter.service';
import { DetailedField } from './../../common/entities/detailed-field';
import { AuthService } from './../../services/auth.service';
import { WebsocketService } from './../../services/websocket.service';
import {
  ControlActionDTO, ControllerConstraintResponseDTO, CONTROLLER_CONSTRAINT, UcaResponseDTO, UnlockRequestDTO,
  LossScenarioResponseDTO, ImplementationConstraintResponseDTO
} from './../../types/local-types';
import { LossScenarioDataService } from '../../services/dataServices/loss-scenario.service';
import { ImplementationConstraintDataService } from '../../services/dataServices/implementation-constraint-data.service';


@Component({
  selector: 'app-controller-constraint',
  templateUrl: './controller-constraint.component.html',
  styleUrls: ['./controller-constraint.component.css']
})
export class ControllerConstraintComponent implements OnDestroy {

  SheetMode: typeof SheetMode = SheetMode;
  private projectId: string;
  columns: TableColumn[] = [];
  ucaAndConstraint: {
    id: string, name: string, description: string,
    constraintId: string, constraintName: string,
    parentId: string, parentControlActionName: string,
    state: string,
  };
  sheetMode: SheetMode = SheetMode.Closed;
  fields: DetailedField[];
  title: string = DetailedSheetUtils.generateSheetTitle(CONTROLLER_CONSTRAINT);   // Title of detailed-sheet generate from constants
  navigationTableDefinitions = [];
  selectedControlAction: ControlActionDTO;
  ucas = [];
  private OwnChange: boolean = false;
  disableSaveButton: boolean = false;
  subscriptions: Subscription[] = [];

  constructor(
    private readonly route: ActivatedRoute,
    private readonly filterService: FilterService,
    private readonly messageService: MessageService,
    private readonly controlActionDataService: ControlActionDataService,
    private readonly ucaDataService: UcaDataService,
    private readonly controllerConstraintDataService: ControllerConstraintDataService,
    private readonly wsService: WebsocketService,
    private readonly authService: AuthService,
    private readonly lockService: LockService,
    private readonly spinnerService: Ng4LoadingSpinnerService,
    private readonly userDataService: UserDataService,
    private readonly incompleteEntityService: IncompleteEntitiesService,
    private readonly hotkeys: Hotkeys) {

    this.subscriptions.push(this.hotkeys.addShortcut({ keys: 'shift.control.n', description: 'Create Controller Constraint' }).subscribe(() => {
      this.createControllerConstraint();
    }));
    // this.spinnerService.show();
    this.fields = [
      { title: 'ID', key: 'constraintId', type: FieldType.Text, readonly: true },
      { title: 'Name', key: 'constraintName', type: FieldType.Text, readonly: false, maxRows: 10 },
      {
        title: 'Control Action', key: 'parentControlActionName', type: FieldType.Text, readonly: true,
        shortcutButton: { title: 'Go to Control Actions', routerLink: (): string => 'project/' + this.projectId + '/control-action/' }
      },
      { title: 'UCA ID', key: 'id', type: FieldType.Text, readonly: true },
      {
        title: 'Name of UCA ', key: 'name', type: FieldType.Text, readonly: true, maxRows: 10,
        shortcutButton: { title: 'Go to UCAs', routerLink: (): string => 'project/' + this.projectId + '/uca-table/' }
      },
      { title: 'Description of UCA', key: 'description', type: FieldType.Text_Variable, readonly: true, minRows: 3, maxRows: 10 },
      { title: 'State', key: 'state', type: FieldType.StateButtonGroup, readonly: true },
    ];
    this.deleteControllerConstraints = this.deleteControllerConstraints.bind(this);
    this.route.parent.params.subscribe((value: any) => {
      this.projectId = value.id;
      this.initColumns();
      this.loadData();
    });

    // Websocket implementation
    this.authService.getProjectToken().then((token: string) => {
      this.wsService.connect('subscribe', CONTROLLER_CONSTRAINT, token, (data) => {
        this.loadData();
        if (this.OwnChange === false) {
          this.messageService.add({ severity: 'info', summary: 'User ' + data.displayName + ' updated the data!' });
        }
      });
    }).catch((err: HttpErrorResponse) => {
    });
  }

  // this is the handler/callback function for the clickedNavigationEmitter defined in the navigationTable
  // if clickedNavigationEmitter is fired onClickedNavigation will be called
  // clickedNavigationEmitter will pass its parameters to onClickedNavigation(tupleOfElemAndTableNumber)
  onClickedNavigation(tupleOfElemAndTableNumber: ControlActionDTO[]): void {
    this.filterService.ClearSelectionEmitter.emit(true);
    this.selectedControlAction = tupleOfElemAndTableNumber[0];
    this.loadData();
  }

  async deleteControllerConstraints($event): Promise<void> {
    this.OwnChange = true;
    const list: any[] = $event;
    if (list.length === 0) {
      return;
    }

    const promiseList = list.map(async (elem: any) => {
      await this.lockService.lockEntity(this.projectId, {
        id: elem.id,
        entityName: this.title.toLowerCase(),
        expirationTime: DetailedSheetUtils.calculateLockExpiration(),
        parentId: elem.parentId
      });
      return await this.controllerConstraintDataService.deleteControllerConstraint(this.projectId, elem.parentId, elem.id, elem.id);
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

  createControllerConstraint(): void {
    this.sheetMode = SheetMode.New;
    this.ucaAndConstraint = {
      id: '', description: '', name: '',
      constraintId: '', constraintName: '',
      parentId: this.selectedControlAction.id, parentControlActionName: this.selectedControlAction.name,
      state: '',
    };
  }

  saveControllerConstraint(uca): void {
    this.filterService.ClearSelectionEmitter.emit(true);
    this.OwnChange = true;
    this.disableSaveButton = true;
    if (!uca.ent.constraintName || uca.ent.constraintName.trim() === '') {
      this.messageService.add({ severity: 'warn', summary: DETAILEDSHEET_EXPECTED_NAME_NOT_NULL })
      this.disableSaveButton = false;
      return;
    }
    if (uca.mode === SheetMode.EditWithLock) {
      // TODO remove description in backend ?
      const controllerConstraintEntity = {
        projectId: this.projectId, description: uca.ent.description as string, name: uca.ent.constraintName as string,
        controlActionId: uca.ent.parentId as number, state: uca.ent.state,
      };
      // Currently it's not necessary to use alteControllerConstraints to alter constraints,
      // createControllerConstraint also edits the Constraints of the passed id
      this.controllerConstraintDataService.createControllerConstraint(this.projectId, uca.ent.parentId, uca.ent.id, controllerConstraintEntity)
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

    } else if (uca.mode === SheetMode.New) {
      // this.disableSaveButton = false; - necessary if somewhen add is enabled...
      // TODO logg not possible ?
    }
  }

  private initColumns(): void {
    // Note the first table column in views with navigation table have width: 10% for aestetical reasons
    this.columns = [
      {
        key: 'id',
        title: 'UCA-ID',
        type: ColumnType.Text,
        style: { width: '10%' }
      }, {
        key: 'name',
        title: 'UCA-Name',
        type: ColumnType.Text,
        style: { width: '25%' }
      }, {
        key: 'select',
        title: 'Select',
        type: ColumnType.Checkbox,
        style: { width: '5%' }
      }, {
        key: 'constraintId',
        title: 'CC-ID',
        type: ColumnType.Text,
        style: { width: '5%' }
      }, {
        key: 'constraintName',
        title: 'Controller-Constraint-Name',
        type: ColumnType.Text,
        style: { width: '25%' }
      },
      {
        key: 'state',
        title: 'State',
        type: ColumnType.StateIcon,
        style: { width: '10%' }
      },
      {
        key: 'icon',
        title: 'Edited-By',
        type: ColumnType.Icon,
        userName: 'lastEditor',
        style: { width: '6%' }
      },
      {
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
  changeEntity(event: { list: ControllerConstraintResponseDTO[], state: string }) {
    event.list.forEach((element: ControllerConstraintResponseDTO) => {

      let incomplete: incompleteEntityDTO = { entityName: 'controller_constraint', parentId: this.selectedControlAction.id, state: event.state };

      this.incompleteEntityService.updateState(this.projectId, element.id, incomplete).then(value => {
        this.loadData();
      }).catch((reason: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: CHANGED_STATE_FAILED, detail: reason.message });
      })
    });
  }


  private loadData(): void {
    if (this.selectedControlAction !== undefined) {
      let userRequest: UserDisplayRequestDTO = new UserDisplayRequestDTO;
      let userId: string[] = [];
      let valueIndex: string[] = [];

      this.ucaDataService.getAllUcasByControlActionId(this.projectId, this.selectedControlAction.id, {})
         .then((value: UcaResponseDTO[]) => {
          value.forEach(element => {
            if(element.lastEditorId !== null && element.lastEditorId !== ''){
              userId.push(element.lastEditorId);
              valueIndex.push(value.indexOf(element).toString());
            }else{
              element.lastEditor = NO_USER;
              element.icon = this.userDataService.getDefaultOrDeleteIcon(element.lastEditor);
            }
        });

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
          this.ucas = value;
          for (const uca of this.ucas) {
            uca.select = false; // hide all checkboxes at first, the next requests for controlConstraint will decide if checbox is vidible or not
            this.controllerConstraintDataService.getControllerConstraintById(this.projectId, uca.parentId, uca.id, uca.id)
              .then((constraint: ControllerConstraintResponseDTO) => {
                uca.constraintName = constraint !== null ? constraint.name : null;
                uca.constraintId = constraint !== null ? uca.id : null;
                uca.select = constraint !== null ? undefined : false; // checkbox will be hidden if uca.select is set to false in all other cases it will be diplayed
                if (constraint !== null && constraint.lastEdited > uca.lastEdited) {
                  uca.lastEdited = constraint.lastEdited;
                  uca.lastEditor = constraint.lastEditor;
                  uca.lastEditorId = constraint.lastEditorId;
                }
                if (uca.lastEditor === null) {
                  uca.lastEditor = DELETED_USER;
                }
                this.userDataService.getIcon(uca.lastEditorId, uca.lastEditor).then(iconValue => {
                  if (iconValue !== null) {
                    uca.icon = iconValue;
                  }
                });
                // this.spinnerService.hide();
              }).catch((response: HttpErrorResponse) => {
                this.messageService.add({
                  severity: 'error',
                  summary: DETAILEDSHEET_FAILED_LOADING_CHIPS,
                  detail: response.toString()
                });
              });
          }
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_LOADING, detail: response.message });
        });

    } else {
      this.controlActionDataService.getAllControlActions(this.projectId, {})
        .then((value: ControlActionDTO[]) => {
          this.navigationTableDefinitions = [];
          this.navigationTableDefinitions[0] = { dataSource: value, style: { width: '100%' }, columnHeaderName: 'Control Action' };
          if (value !== null && value.length > 0) {
            this.selectedControlAction = value[0];
            this.loadData();
          } else {
            this.navigationTableDefinitions[0] = { dataSource: value, style: { width: '100%' }, columnHeaderName: 'No Control Actions found' };
            this.messageService.add({
              severity: 'warn',
              life: 10000,
              summary: 'Create a Control-Action and an Unsafe-Control-Action to edit a controller constraint!'
            });
            // this.spinnerService.hide();
          }
          // Create the shortcut button to the Control Action table
          this.navigationTableDefinitions[0].columnHeaderButton = { title: 'Go to Control Actions', routerLink: 'project/' + this.projectId + '/control-action/' };
        })
        .catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_LOADING, detail: response.message });
        });
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
    let id: string;
    if (this.ucaAndConstraint) {
      id = this.ucaAndConstraint.id;
    }
    console.log(this.sheetMode);

    if (this.sheetMode === SheetMode.Edit) {
      this.closeSheet(false, SheetMode.View);
    }

    this.sheetMode = DetailedSheetUtils.toggle(id, data.entity.id, this.sheetMode, data.mode);
    this.ucaAndConstraint = {
      description: data.entity.description,
      name: data.entity.name,
      id: data.entity.id,
      parentId: this.selectedControlAction.id,
      constraintName: data.entity.constraintName,
      parentControlActionName: this.selectedControlAction.name,
      constraintId: data.entity.constraintId,
      state: data.entity.state,
    };
  }

  closeSheet(isUnlocked: boolean = false, sheetMode: SheetMode = SheetMode.Closed): void {
    if (!isUnlocked && this.sheetMode === SheetMode.Edit) {
      const unlockDTO: UnlockRequestDTO = {
        id: this.ucaAndConstraint.constraintId,
        entityName: this.title.toLowerCase(),
        parentId: this.ucaAndConstraint.parentId
      };
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

  ngOnDestroy(): void {
    this.filterService.ClearSelectionEmitter.emit(true);
    this.subscriptions.forEach((subscription: Subscription) => subscription.unsubscribe());
  }

}
