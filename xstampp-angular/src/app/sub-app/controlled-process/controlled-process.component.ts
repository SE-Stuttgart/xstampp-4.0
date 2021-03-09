import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, OnDestroy, Output } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MessageService } from 'primeng/api';
import { IncompleteEntitiesService, incompleteEntityDTO } from 'src/app/services/dataServices/incomplete-entities-data.service';
import { UserDataService, UserDisplayRequestDTO } from 'src/app/services/dataServices/user-data.service';
import { SheetMode } from '../../common/detailed-sheet/detailed-sheet.component';
import { DetailedSheetUtils } from '../../common/detailed-sheet/utils/detailed-sheet-utils';
import { FieldType } from '../../common/entities/detailed-field';
import { ColumnType, TableColumn } from '../../common/entities/table-column';
import { ControlledProcessDataService } from '../../services/dataServices/control-structure/controlled-process-data.service';
import { LockService } from '../../services/dataServices/lock.service';
import { FilterService } from '../../services/filter-service/filter.service';
import { DetailedField } from './../../common/entities/detailed-field';
import { AuthService } from './../../services/auth.service';
import { WebsocketService } from './../../services/websocket.service';
import { Hotkeys } from 'src/app/hotkeys/hotkeys.service';
import { ControlledProcessResponseDTO, CONTROLLED_PROCESS, UnlockRequestDTO } from './../../types/local-types';
import { DETAILEDSHEET_EXPECTED_NAME_NOT_NULL, DETAILEDSHEET_SUCCESSFUL_EDIT, DETAILEDSHEET_FAILED_EDIT, ENTITY_SUCCESSFUL_DELETE, ENTITY_FAILED_DELETE, CHANGED_STATE_FAILED, DELETED_USER, ENTITY_FAILED_LOADING, NO_USER } from 'src/app/globals';
import { Subscription } from 'rxjs';
import { SelectedEntity } from '../dependent-element-tree/dependent-elements-types.component';

@Component({
  selector: 'app-controlled-process',
  templateUrl: './controlled-process.component.html',
  styleUrls: ['./controlled-process.component.css']
})
export class ControlledProcessComponent implements OnDestroy {

  SheetMode: typeof SheetMode = SheetMode;
  private projectId: string;
  controlledProcesses = [];
  columns: TableColumn[] = [];
  controlledProcess: { id: string, name: string, description: string, state: string };
  sheetMode: SheetMode = SheetMode.Closed;
  fields: DetailedField[];
  title: string = DetailedSheetUtils.generateSheetTitle(CONTROLLED_PROCESS);
  private OwnChange: boolean = false;
  disableSaveButton: boolean = false;
  subscriptions: Subscription[] = [];
  selectControlledProcessEntity: SelectedEntity<Object>;

  @Output() selectEntityMessage: any = new EventEmitter<SelectedEntity<Object>>();

  constructor(
    private readonly route: ActivatedRoute,
    private readonly filterService: FilterService,
    private readonly messageService: MessageService,
    private readonly controlledProcessDataService: ControlledProcessDataService,
    private readonly wsService: WebsocketService,
    private readonly authService: AuthService,
    private readonly lockService: LockService,
    private readonly userDataService: UserDataService,
    private readonly incompleteEntityService: IncompleteEntitiesService,
    private readonly hotkeys: Hotkeys) {

    this.subscriptions.push(hotkeys.addShortcut({ keys: 'shift.control.n', description: 'Create Controlled Process' }).subscribe(() => {
      this.createControlledProcess();
    }));
    this.fields = [
      { title: 'ID', key: 'id', type: FieldType.Text, readonly: true },
      { title: 'Name', key: 'name', type: FieldType.Text, readonly: false },
      { title: 'Description', key: 'description', type: FieldType.Text_Variable, readonly: false, minRows: 3, maxRows: 10 },
      { title: 'State', key: 'state', type: FieldType.StateButtonGroup, readonly: false },
    ];
    this.deleteControlledProcesses = this.deleteControlledProcesses.bind(this);
    this.route.parent.params.subscribe((value: any) => {
      this.projectId = value.id;
      this.initColumns();
      this.loadData();
    });

    // Websocket implementation
    this.authService.getProjectToken().then((token: string) => {
      this.wsService.connect('subscribe', CONTROLLED_PROCESS, token, (data) => {
        this.loadData();
        if (this.OwnChange === false) {
          this.messageService.add({ severity: 'info', summary: 'User ' + data.displayName + ' updated the data!' });
        }
      });
    }).catch((err: HttpErrorResponse) => {
      console.log(err);
    });
  }

  getSelectedEntities($event: any): any {
    this.selectControlledProcessEntity = new SelectedEntity<Object>($event,
      this.title,
      this.projectId,
      false,
      true,
      false);
  }

  async deleteControlledProcesses($event: any): Promise<void> {
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
      return await this.controlledProcessDataService.deleteControlledProcess(this.projectId, elem.id);
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

  createControlledProcess(): void {
    this.sheetMode = SheetMode.New;
    this.controlledProcess = { id: '', description: '', name: '', state: '' };
  }

  saveControlledProcess(controlledProcess): void {
    this.filterService.ClearSelectionEmitter.emit(true);
    this.OwnChange = true;
    this.disableSaveButton = true;
    if (!controlledProcess.ent.name || controlledProcess.ent.name.trim() === '') {
      this.messageService.add({ severity: 'warn', summary: DETAILEDSHEET_EXPECTED_NAME_NOT_NULL })
      this.disableSaveButton = false;
      return;
    }
    if (controlledProcess.mode === SheetMode.EditWithLock) {
      this.controlledProcessDataService.alterControlledProcess(this.projectId, controlledProcess.id, controlledProcess.ent)
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
    } else if (controlledProcess.mode === SheetMode.New) {
      this.controlledProcessDataService.createControlledProcess(this.projectId, controlledProcess.ent)
        .then(() => {
          this.messageService.add({ severity: 'success', summary: 'Create successful' });
          this.closeSheet(true);
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: 'Create failed', detail: response.message });
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
        title: 'Controlled-Process-Name',
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
  changeEntity(event: { list: ControlledProcessResponseDTO[], state: string }) {
    event.list.forEach((element: ControlledProcessResponseDTO) => {

      let incomplete: incompleteEntityDTO = { entityName: 'controlled_process', parentId: undefined, state: event.state };

      this.incompleteEntityService.updateState(this.projectId, element.id, incomplete).then(value => {
        this.loadData();
      }).catch((reason: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: CHANGED_STATE_FAILED, detail: reason.message });
      })
    });
  }
  private loadData(): void {
    let userRequest: UserDisplayRequestDTO = new UserDisplayRequestDTO;
    let userId: string[] = [];
    let valueIndex: string[] = [];
    this.controlledProcessDataService.getAllControlledProcessesForTable(this.projectId, {'orderBy': 'id', 'orderDirection': 'asc'}).then(value => {
        if(value !== null){
          value.forEach(element => {
            if(element.lastEditorId !== null && element.lastEditorId !== ''){
              userId.push(element.lastEditorId);
              valueIndex.push(value.indexOf(element).toString());

            }else{
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
      this.controlledProcesses = value;
    }}).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_LOADING, detail: response.message });
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
    if (this.controlledProcess) {
      id = this.controlledProcess.id;
    }

    if (this.sheetMode === SheetMode.Edit) {
      this.closeSheet(false, SheetMode.View);
    }

    this.sheetMode = DetailedSheetUtils.toggle(id, data.entity.id, this.sheetMode, data.mode);
    this.controlledProcess = {
      id: data.entity.id, description: data.entity.description, name: data.entity.name, state: data.entity.state,
    };
  }

  closeSheet(isUnlocked: boolean = false, sheetMode: SheetMode = SheetMode.Closed): void {
    if (!isUnlocked && this.sheetMode === SheetMode.Edit) {
      const unlockDTO: UnlockRequestDTO = {
        id: this.controlledProcess.id,
        entityName: this.title.toLowerCase()
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
