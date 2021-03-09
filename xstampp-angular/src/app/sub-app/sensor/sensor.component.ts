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
import { SensorDataService } from '../../services/dataServices/control-structure/sensor-data.service';
import { LockService } from '../../services/dataServices/lock.service';
import { FilterService } from '../../services/filter-service/filter.service';
import { DetailedField } from './../../common/entities/detailed-field';
import { AuthService } from './../../services/auth.service';
import { WebsocketService } from './../../services/websocket.service';
import { Hotkeys } from 'src/app/hotkeys/hotkeys.service';
import { SENSOR, SensorResponseDTO, UnlockRequestDTO } from './../../types/local-types';
import { DETAILEDSHEET_EXPECTED_NAME_NOT_NULL, CHANGED_STATE_FAILED, ENTITY_FAILED_LOADING, DETAILEDSHEET_FAILED_CREATION, DETAILEDSHEET_SUCCESSFUL_CREATION, DETAILEDSHEET_FAILED_EDIT, DETAILEDSHEET_SUCCESSFUL_EDIT, ENTITY_SUCCESSFUL_DELETE, ENTITY_FAILED_DELETE, DELETED_USER, NO_USER } from 'src/app/globals';
import { Subscription } from 'rxjs';
import { SelectedEntity } from '../dependent-element-tree/dependent-elements-types.component';

@Component({
  selector: 'app-sensor',
  templateUrl: './sensor.component.html',
  styleUrls: ['./sensor.component.css']
})
export class SensorComponent implements OnDestroy {

  SheetMode: typeof SheetMode = SheetMode;
  private projectId: string;
  sensors = [];
  columns: TableColumn[] = [];
  sensor: { id: string, name: string, description: string, state: string, };
  sheetMode: SheetMode = SheetMode.Closed;
  fields: DetailedField[];
  title: string = DetailedSheetUtils.generateSheetTitle(SENSOR);
  private OwnChange: boolean = false;
  disableSaveButton: boolean = false;
  subscriptions: Subscription[] = [];
  selectSensorEntity: SelectedEntity<Object>;

  @Output() selectEntityMessage: any = new EventEmitter<SelectedEntity<Object>>();

  constructor(
    private readonly route: ActivatedRoute,
    private readonly filterService: FilterService,
    private readonly messageService: MessageService,
    private readonly sensorDataService: SensorDataService,
    private readonly wsService: WebsocketService,
    private readonly authService: AuthService,
    private readonly lockService: LockService,
    private readonly userDataService: UserDataService,
    private readonly incompleteEntityService: IncompleteEntitiesService,
    private readonly hotkeys: Hotkeys) {

    this.subscriptions.push(this.hotkeys.addShortcut({ keys: 'shift.control.n', description: 'Create Sensor' }).subscribe(() => {
      this.createSensor();
    }));
    this.fields = [
      { title: 'ID', key: 'id', type: FieldType.Text, readonly: true },
      { title: 'Name', key: 'name', type: FieldType.Text, readonly: false },
      { title: 'Description', key: 'description', type: FieldType.Text_Variable, readonly: false, minRows: 3, maxRows: 10 },
      { title: 'State', key: 'state', type: FieldType.StateButtonGroup, readonly: false },

    ];
    this.deleteSensors = this.deleteSensors.bind(this);
    this.route.parent.params.subscribe((value: any) => {
      this.projectId = value.id;
      this.initColumns();
      this.loadData();
    });

    // Websocket implementation
    this.authService.getProjectToken().then((token: string) => {
      this.wsService.connect('subscribe', SENSOR, token, (data) => {
        this.loadData();
        if (this.OwnChange === false) {
          this.messageService.add({ severity: 'info', summary: 'User ' + data.displayName + ' updated the data!' });
        }
      });
    }).catch((err: HttpErrorResponse) => {
      console.log(err);
    });
  }

  getSelectedEntities($event: any): void {
    this.selectSensorEntity = new SelectedEntity<Object>($event,
      this.title,
      this.projectId,
      false,
      true,
      false);
  }

  async deleteSensors($event: any): Promise<void> {
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
      return await this.sensorDataService.deleteSensor(this.projectId, elem.id);

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

  createSensor(): void {
    this.sheetMode = SheetMode.New;
    this.sensor = { id: '', description: '', name: '', state: '' };
  }

  saveSensor(sensor): void {
    this.filterService.ClearSelectionEmitter.emit(true);
    this.OwnChange = true;
    this.disableSaveButton = true;

    if (!sensor.ent.name || sensor.ent.name.trim() === '') {
      this.messageService.add({ severity: 'warn', summary: DETAILEDSHEET_EXPECTED_NAME_NOT_NULL })
      this.disableSaveButton = false;
      return;
    }
    if (sensor.mode === SheetMode.EditWithLock) {
      this.sensorDataService.alterSensor(this.projectId, sensor.id, sensor.ent)
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
    } else if (sensor.mode === SheetMode.New) {
      this.sensorDataService.createSensor(this.projectId, sensor.ent)
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
        title: 'Sensor-Name',
        type: ColumnType.Text,
        style: { width: '61%' }
      },
      {
        key: 'state',
        title: 'State',
        type: ColumnType.StateIcon,
        style: { width: '15%' }
      }, {
        key: 'icon',
        title: 'Edited-By',
        type: ColumnType.Icon,
        userName: 'lastEditor',
        style: { width: '5%' }

      }, {
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
  changeEntity(event: { list: SensorResponseDTO[], state: string }) {
    event.list.forEach((element: SensorResponseDTO) => {
      let incomplete: incompleteEntityDTO = { entityName: 'sensor', parentId: undefined, state: event.state };
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
    this.sensorDataService.getAllSensorsForTable(this.projectId, {'orderBy': 'id', 'orderDirection': 'asc'}).then(value => {
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
      this.sensors = value;
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
    if (this.sensor) {
      id = this.sensor.id;
    }

    if (this.sheetMode === SheetMode.Edit) {
      this.closeSheet(false, SheetMode.View);
    }

    this.sheetMode = DetailedSheetUtils.toggle(id, data.entity.id, this.sheetMode, data.mode);
    this.sensor = {
      id: data.entity.id, description: data.entity.description, name: data.entity.name, state: data.entity.state,
    };
  }

  closeSheet(isUnlocked: boolean = false, sheetMode: SheetMode = SheetMode.Closed): void {
    if (!isUnlocked && this.sheetMode === SheetMode.Edit) {
      const unlockDTO: UnlockRequestDTO = { id: this.sensor.id, entityName: this.title.toLowerCase() };
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
