import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, Input, OnDestroy, Output } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MessageService } from 'primeng/api';
import { incompleteEntityDTO } from 'src/app/services/dataServices/incomplete-entities-data.service';
import { UserDataService, UserDisplayRequestDTO } from 'src/app/services/dataServices/user-data.service';
import {
  ControlActionDTO,
  ControlActionResponseDTO,
  ControllerConstraintResponseDTO, HazardResponseDTO,
  ImplementationConstraintResponseDTO, LossScenarioRequestDTO,
  LossScenarioResponseDTO, UcaResponseDTO
} from 'src/app/types/local-types';
import { SheetMode } from '../../common/detailed-sheet/detailed-sheet.component';
import { DetailedSheetUtils } from '../../common/detailed-sheet/utils/detailed-sheet-utils';
import { FieldType } from '../../common/entities/detailed-field';
import { ColumnType, TableColumn } from '../../common/entities/table-column';
import { ControlActionDataService } from '../../services/dataServices/control-structure/control-action-data.service';
import { LockService } from '../../services/dataServices/lock.service';
import { FilterService } from '../../services/filter-service/filter.service';
import { DetailedField } from './../../common/entities/detailed-field';
import { AuthService } from './../../services/auth.service';
import { IncompleteEntitiesService } from './../../services/dataServices/incomplete-entities-data.service';
import { WebsocketService } from './../../services/websocket.service';
import { CONTROL_ACTION, UnlockRequestDTO } from './../../types/local-types';
import { ENTITY_FAILED_DELETE, ENTITY_SUCCESSFUL_DELETE, DETAILEDSHEET_EXPECTED_NAME_NOT_NULL, DETAILEDSHEET_SUCCESSFUL_EDIT, DETAILEDSHEET_FAILED_EDIT, DETAILEDSHEET_SUCCESSFUL_CREATION, DETAILEDSHEET_FAILED_CREATION, DELETED_USER, ENTITY_FAILED_LOADING, CHANGED_STATE_FAILED, NO_USER } from 'src/app/globals';
import { Hotkeys } from 'src/app/hotkeys/hotkeys.service';
import { Subscription } from 'rxjs';
import { UcaDataService } from '../../services/dataServices/uca-data.service';
import { ControllerConstraintDataService } from '../../services/dataServices/controller-constraint-data.service';
import { LossScenarioDataService } from '../../services/dataServices/loss-scenario.service';
import { ImplementationConstraintDataService } from '../../services/dataServices/implementation-constraint-data.service';

import { MatDialog } from '@angular/material';

import { MainTableComponent } from '../../common/main-table/main-table.component';
import { SelectedEntity } from '../dependent-element-tree/dependent-elements-types.component';

@Component({
  selector: 'app-control-action',
  templateUrl: './control-action.component.html',
  styleUrls: ['./control-action.component.css']
})
export class ControlActionComponent implements OnDestroy {

  SheetMode: typeof SheetMode = SheetMode;
  private projectId: string;
  controlActions = [];
  columns: TableColumn[] = [];
  controlAction: { id: string, name: string, description: string, state: string };
  sheetMode: SheetMode = SheetMode.Closed;
  fields: DetailedField[];
  title: string = DetailedSheetUtils.generateSheetTitle(CONTROL_ACTION);
  private OwnChange: boolean = false;
  disableSaveButton: boolean = false;
  additionalDeleteMessage: string = 'By deleting a Control-Action all it\'s underlying Unsafe-Control-Actions and linked Controller-Constraints will be deleted too!';
  subscriptions: Subscription[] = [];
  selectControlActionEntity: SelectedEntity<Object>;

  @Output() selectEntityMessage: any = new EventEmitter<SelectedEntity<Object>>();

  constructor(
    private readonly route: ActivatedRoute,
    private readonly filterService: FilterService,
    private readonly messageService: MessageService,
    private readonly controlActionDataService: ControlActionDataService,
    private readonly wsService: WebsocketService,
    private readonly authService: AuthService,
    private readonly lockService: LockService,
    private readonly hotkeys: Hotkeys,
    private readonly userDataService: UserDataService,
    private readonly incompleteEntityService: IncompleteEntitiesService) {

    this.subscriptions.push(hotkeys.addShortcut({ keys: 'shift.control.n', description: 'Create Control Action' }).subscribe(() => {
      this.createControlAction();
    }));

    this.fields = [
      { title: 'ID', key: 'id', type: FieldType.Text, readonly: true },
      { title: 'Name', key: 'name', type: FieldType.Text, readonly: false },
      { title: 'Description', key: 'description', type: FieldType.Text_Variable, readonly: false, minRows: 3, maxRows: 10 },
      { title: 'State', key: 'state', type: FieldType.StateButtonGroup, readonly: false },
    ];
    this.deleteControlActions = this.deleteControlActions.bind(this);
    this.route.parent.params.subscribe((value: any) => {
      this.projectId = value.id;
      this.initColumns();
      this.loadData();
    });

    // Websocket implementation
    this.authService.getProjectToken().then((token: string) => {
      this.wsService.connect('subscribe', CONTROL_ACTION, token, (data) => {
        this.loadData();
        if (this.OwnChange === false) {
          this.messageService.add({ severity: 'info', summary: 'User ' + data.displayName + ' updated the data!' });
        }
      });
    }).catch((err: HttpErrorResponse) => {
      console.log(err);
    });
  }

  async deleteControlActions($event: any): Promise<void> {
    this.OwnChange = true;
    const list: any[] = $event;
    if (list.length === 0) {
      return;
    }

    const promiseList = list.map(async (elem: any) => {
      await this.lockService.lockEntity(this.projectId, {
        id: elem.id,
        entityName: this.title.toLowerCase(),
        expirationTime: DetailedSheetUtils.calculateLockExpiration()
      });
      return await this.controlActionDataService.deleteControlAction(this.projectId, elem.id);
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

  createControlAction(): void {
    this.sheetMode = SheetMode.New;
    this.controlAction = { id: '', description: '', name: '', state: '' };
  }

  saveControlAction(controlAction): void {
    this.filterService.ClearSelectionEmitter.emit(true);
    this.OwnChange = true;
    this.disableSaveButton = true;
    if (!controlAction.ent.name || controlAction.ent.name.trim() === '') {
      this.messageService.add({ severity: 'warn', summary: DETAILEDSHEET_EXPECTED_NAME_NOT_NULL })
      this.disableSaveButton = false;
      return;
    }
    if (controlAction.mode === SheetMode.EditWithLock) {
      this.controlActionDataService.alterControlAction(this.projectId, controlAction.id, controlAction.ent)
        .then(() => {
          this.messageService.add({ severity: 'success', summary: DETAILEDSHEET_SUCCESSFUL_EDIT });
          this.closeSheet(true);
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: DETAILEDSHEET_FAILED_EDIT, detail: response.message });
        }).finally(() => {
          this.OwnChange = false;
          this.disableSaveButton = false;
          this.loadData();
        });
    } else if (controlAction.mode === SheetMode.New) {
      this.controlActionDataService.createControlAction(this.projectId, controlAction.ent)
        .then(() => {
          this.messageService.add({ severity: 'success', summary: DETAILEDSHEET_SUCCESSFUL_CREATION });
          this.closeSheet(true);
        })
        .catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: DETAILEDSHEET_FAILED_CREATION, detail: response.message });
        }).finally(() => {
          this.OwnChange = false;
          this.disableSaveButton = false;
          this.loadData();
        });
    }
  }

  private initColumns(): void {
    this.columns = [
      {
        key: 'select',
        title: 'Select',
        type: ColumnType.Checkbox,
        style: { width: '5%' }
      }, {
        key: 'id',
        title: 'ID',
        type: ColumnType.Text,
        style: { width: '5%' }
      }, {
        key: 'name',
        title: 'Control-Action-Name',
        type: ColumnType.Text,
        style: { width: '61%' }
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
        style: { width: '5%' }
      },
      {
        key: 'lastEdited',
        title: 'Last-Edited',
        type: ColumnType.Date_Time,
        style: { width: '13%' }
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

  getSelectedEntities ($event: any): void {
    this.selectControlActionEntity = new SelectedEntity<Object>($event,
      this.title,
      this.projectId,
      false,
      false,
      true);
  }

  private loadData(): void {
    let userRequest: UserDisplayRequestDTO = new UserDisplayRequestDTO;
    let userId: string[] = [];
    let valueIndex: string[] = [];

    this.controlActionDataService.getAllControlActionsForTable(this.projectId, {'orderBy': 'id', 'orderDirection': 'asc'})
      .then((controlActionResponseDTOs: ControlActionResponseDTO[]) => {
        if(controlActionResponseDTOs !== null){
          controlActionResponseDTOs.forEach(controlActionResponseDTO => {
            if(controlActionResponseDTO.lastEditorId !== null && controlActionResponseDTO.lastEditorId !== ''){
              userId.push(controlActionResponseDTO.lastEditorId);
              valueIndex.push(controlActionResponseDTOs.indexOf(controlActionResponseDTO).toString());

            }else{
              controlActionResponseDTO.lastEditor = NO_USER;
              controlActionResponseDTO.icon = this.userDataService.getDefaultOrDeleteIcon(controlActionResponseDTO.lastEditor);
            }
          });

          if(valueIndex.length !== 0){
            userRequest.userIds = userId;
            this.userDataService.getUserDisplay(userRequest).then((map:Map<string, string[]>) =>{
              valueIndex.forEach(element =>{

                let nameIcon = map.get(controlActionResponseDTOs[element].lastEditorId);

                if(nameIcon[0] !== null){

                  controlActionResponseDTOs[element].lastEditor = nameIcon[0];
                }
                if(nameIcon[1] !== ''){
                  controlActionResponseDTOs[element].icon = nameIcon[1];
                }else{
                  controlActionResponseDTOs[element].icon = this.userDataService.getDefaultOrDeleteIcon(nameIcon[1]);
                }


              });
            });
          }
          this.controlActions = controlActionResponseDTOs;
        }
      }).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_LOADING, detail: response.message });
    });
  }

  changeEntity(event: { list: ControlActionResponseDTO[], state: string }) {
    let incompleteEntity: incompleteEntityDTO = { entityName: 'control_action', parentId: undefined, state: event.state };
    event.list.forEach((element: ControlActionResponseDTO) => {
      this.incompleteEntityService.updateState(this.projectId, element.id, incompleteEntity).then(value => {
        this.loadData();
      }).catch((reason: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: CHANGED_STATE_FAILED, detail: reason.message });
      })
    });
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
    if (this.controlAction) {
      id = this.controlAction.id;
    }

    if (this.sheetMode === SheetMode.Edit) {
      this.closeSheet(false, SheetMode.View);
    }

    this.sheetMode = DetailedSheetUtils.toggle(id, data.entity.id, this.sheetMode, data.mode);
    this.controlAction = {
      id: data.entity.id, description: data.entity.description, name: data.entity.name, state: data.entity.state,
    };
  }

  closeSheet(isUnlocked: boolean = false, sheetMode: SheetMode = SheetMode.Closed): void {
    if (!isUnlocked && this.sheetMode === SheetMode.Edit) {
      const unlockDTO: UnlockRequestDTO = { id: this.controlAction.id, entityName: this.title.toLowerCase() };
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
