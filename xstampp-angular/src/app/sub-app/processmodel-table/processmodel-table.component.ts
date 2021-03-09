import { HttpErrorResponse } from '@angular/common/http';
import { MessageService } from 'primeng/api';
import { SheetMode } from 'src/app/common/detailed-sheet/detailed-sheet.component';
import { DetailedSheetUtils } from 'src/app/common/detailed-sheet/utils/detailed-sheet-utils';
import { DetailData } from 'src/app/common/entities/data';
import { DetailedField, FieldType } from 'src/app/common/entities/detailed-field';
import { ColumnType, TableColumn } from 'src/app/common/entities/table-column';
import { AuthService } from 'src/app/services/auth.service';
import { ControllerDataService } from 'src/app/services/dataServices/control-structure/controller-data.service';
import { ProcessModelDataService, ProcessModelDTO, ProcessModelResponseDTO } from 'src/app/services/dataServices/control-structure/process-model-data.service';
import { LockService } from 'src/app/services/dataServices/lock.service';
import { FilterService } from 'src/app/services/filter-service/filter.service';
import { WebsocketService } from 'src/app/services/websocket.service';
import { ControllerDTO, PROCESS_MODEL, UnlockRequestDTO } from 'src/app/types/local-types';
import { Hotkeys } from 'src/app/hotkeys/hotkeys.service';
import { incompleteEntityDTO, IncompleteEntitiesService } from 'src/app/services/dataServices/incomplete-entities-data.service';
import { UserDataService, UserDisplayRequestDTO } from 'src/app/services/dataServices/user-data.service';
import { ActivatedRoute } from '@angular/router';
import { ChangeDetectorRef, Component, EventEmitter, Output } from '@angular/core';
import { DETAILEDSHEET_EXPECTED_CHIPFIELDS_NOT_NULL, DETAILEDSHEET_EXPECTED_NAME_NOT_NULL, ENTITY_SUCCESSFUL_DELETE, ENTITY_FAILED_DELETE, DETAILEDSHEET_FAILED_LOADING_CHIPS, DETAILEDSHEET_SUCCESSFUL_EDIT, DETAILEDSHEET_FAILED_EDIT, DETAILEDSHEET_SUCCESSFUL_CREATION, DETAILEDSHEET_FAILED_CREATION, DETAILEDSHEET_ERROR, ENTITY_FAILED_LOADING, DELETED_USER, CHANGED_STATE_FAILED, EDIT_FAILED, NO_USER } from 'src/app/globals';
import { Subscription } from 'rxjs';
import { SelectedEntity } from '../dependent-element-tree/dependent-elements-types.component';

interface PMforDetailedSheet {
  id: string;
  name: string;
  description: string;
  con_links: ControllerDTO[];
  allLinksForAutocomplete: LinksForAutoCompleteController;
  state: string;
}

interface LinksForAutoCompleteController {
  con_links: ControllerDTO[];
}

@Component({
  selector: 'app-processmodel-table',
  templateUrl: './processmodel-table.component.html',
  styleUrls: ['./processmodel-table.component.css']
})
export class ProcessmodelTableComponent {

  readonly SELECT_ALL_TEXT: string = 'Unlinked Process Models';
  SheetMode: typeof SheetMode = SheetMode;
  private projectId: string;
  sheetMode: SheetMode = SheetMode.Closed;
  disableSaveButton: boolean = false;
  title: string = DetailedSheetUtils.generateSheetTitle(PROCESS_MODEL);
  columns: TableColumn[] = [];
  fields: DetailedField[];
  processmodels = [];
  private OwnChange: boolean = false;
  navigationTableDefinitions = [];
  selectedController: ControllerDTO;
  pm: PMforDetailedSheet;
  subscriptions: Subscription[] = [];
  private allCrLinksForAutocomplete: LinksForAutoCompleteController;
  selectProcessModelEntity: SelectedEntity<Object>;

  @Output() selectEntityMessage: any = new EventEmitter<SelectedEntity<Object>>();

  constructor(private readonly route: ActivatedRoute,
    private readonly wsService: WebsocketService,
    private readonly authService: AuthService,
    private readonly lockService: LockService,
    private readonly filterService: FilterService,
    private readonly messageService: MessageService,
    private readonly controllerDataService: ControllerDataService,
    private readonly processModelDataSevice: ProcessModelDataService,
    private readonly incompleteEntityService: IncompleteEntitiesService,
    private readonly userDataService: UserDataService,
    private readonly hotkeys: Hotkeys) {

    this.subscriptions.push(this.hotkeys.addShortcut({ keys: 'shift.control.n', description: 'Create Process Model' }).subscribe(() => {
      this.createProcessModel();
    }));
    this.fields = [
      { title: 'ID', key: 'id', type: FieldType.Text, readonly: true },
      { title: 'Name', key: 'name', type: FieldType.Text, readonly: false },
      { title: 'Description', key: 'description', type: FieldType.Text_Variable, readonly: false, minRows: 3, maxRows: 10 },
      {
        title: 'Controller Id ', key: 'con_links', type: FieldType.Chips_Single, readonly: false, listKey: 'id', displayShortName: 'C-',
        shortcutButton: { title: 'Go to Controllers', routerLink: (): string => 'project/' + this.projectId + '/controller/' }
      },
      { title: 'State', key: 'state', type: FieldType.StateButtonGroup, readonly: false },
    ];
    this.deleteProcessmodels = this.deleteProcessmodels.bind(this);
    this.route.parent.params.subscribe((value: any) => {
      this.projectId = value.id;
      this.initColumns();
      this.loadData();
    });
    this.authService.getProjectToken().then((token: string) => {
      this.wsService.connect('subscribe', PROCESS_MODEL, token, (data) => {
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
   * shows a new empty detailed-sheet for PM-creation
   */
  createProcessModel(): void {
    if (!this.selectedController) {
      this.messageService.add({ severity: 'warn', summary: 'Please create Controller!' });
      return;
    }

    this.sheetMode = SheetMode.New;
    this.allCrLinksForAutocomplete = { con_links: [] };
    this.pm = {
      id: '',
      name: '',
      description: '',
      con_links: [],
      allLinksForAutocomplete: { con_links: [] },
      state: '',
    };

    this.controllerDataService.getAllControllers(this.projectId, {})
      .then((value: ControllerDTO[]) => {
        let selCont = [];
        for (let index: number = 0; index < value.length; index++) {

          if (value[index].id === this.selectedController.id) {
            selCont.push(value[index]);
            break;
          }
        }

        this.allCrLinksForAutocomplete.con_links = value;
        let clinks = [];
        if (value.length > 1) {
          clinks.push(value[1]);
        }
        this.pm = {
          id: '', name: '', description: '',
          con_links: selCont,
          allLinksForAutocomplete: this.allCrLinksForAutocomplete,
          state: '',
        };
      }).catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: DETAILEDSHEET_FAILED_LOADING_CHIPS, detail: response.message });
      });

  }
  /**
     * deletes the selected process-models
     */
  async deleteProcessmodels($event): Promise<boolean> {
    this.OwnChange = true;
    const list: any[] = $event;
    if (list.length === 0) {
      return;
    }

    const promiseList = list.map(async (elem: any) => {
      console.error(elem);
      await this.lockService.lockEntity(this.projectId, {
        id: elem.id,
        entityName: this.title.toLowerCase(),
        expirationTime: DetailedSheetUtils.calculateLockExpiration()
      });
      console.log(elem.id);
      return await this.processModelDataSevice.delete(this.projectId, elem.id);
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
        this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_DELETE + PROCESS_MODEL, detail: response.message });
      }
    }
    if (failedResponses.length < list.length) {
      /* some must have worked, we have fewer errors than requests */
      this.messageService.add({ severity: 'success', summary: ENTITY_SUCCESSFUL_DELETE });
    }
    this.OwnChange = false;
    this.loadProcessModel();
  }

  /**
   * tries to save the process-model
   */
  saveProcessmodel(pm: DetailData): void {
    this.filterService.ClearSelectionEmitter.emit(true);
    this.OwnChange = true;
    this.disableSaveButton = true;
    if (!pm.ent.name || pm.ent.name.trim() === '') {
      this.messageService.add({ severity: 'warn', summary: DETAILEDSHEET_EXPECTED_NAME_NOT_NULL })
      this.disableSaveButton = false;
      return;
    }
    if (pm.addedMap.has('con_links')) {
      for (const pmChip of pm.addedMap.get('con_links')) {
        this.selectedController = pmChip;
      }
    }
    if (this.pm.con_links.length === 0) {
      this.messageService.add({ severity: 'warn', summary: DETAILEDSHEET_EXPECTED_CHIPFIELDS_NOT_NULL });
      this.disableSaveButton = false;
      return;
    }

    if (pm.mode === SheetMode.EditWithLock) {
      const value: ProcessModelDTO = {
        // id: this.pm.id,
        name: this.pm.name,
        description: this.pm.description,
        controllerId: this.selectedController.id,
        state: this.pm.state,
      };

      const promises = [];
      promises.push(this.processModelDataSevice.update(this.projectId, pm.ent.id, value));
      Promise.all(promises)
        .then(() => {
          this.messageService.add({ severity: 'success', summary: DETAILEDSHEET_SUCCESSFUL_EDIT });

          this.closeSheet(true);
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: DETAILEDSHEET_FAILED_EDIT, detail: response.message });
        }).finally(() => {
          this.OwnChange = false;
          this.disableSaveButton = false;
          this.sheetMode = SheetMode.Closed;
          this.loadProcessModel();
        });
    }

    if (pm.mode === SheetMode.New) {
      const value: ProcessModelDTO = {
        // id: this.pm.id,
        name: this.pm.name,
        description: this.pm.description,
        controllerId: this.selectedController.id,
        state: this.pm.state,
      };
      this.processModelDataSevice.create(this.projectId, value).then(() => {
        this.messageService.add({ severity: 'success', summary: DETAILEDSHEET_SUCCESSFUL_CREATION });
        this.closeSheet(true);
      }).catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: DETAILEDSHEET_FAILED_CREATION, detail: response.message });
      }).finally(() => {
        this.OwnChange = false;
        this.disableSaveButton = false;
        this.loadProcessModel();
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
    this.allCrLinksForAutocomplete = { con_links: [] };
    Promise.all([
      this.processModelDataSevice.getById(this.projectId, data.entity.id),
      this.controllerDataService.getAllControllers(this.projectId, {}),
    ]).then((value: [ProcessModelResponseDTO, ControllerDTO[]]) => {
      let selCont: ControllerDTO[] = [];
      for (let index = 0; index < value[1].length; index++) {
        if (value[0].controllerId === value[1][index].id) {

          selCont.push(value[1][index]);
          break;
        }
      }
      this.allCrLinksForAutocomplete.con_links = value[1];
      let id: string;
      if (this.pm) {
        id = this.pm.id;
      }

      if (this.sheetMode === SheetMode.Edit) {
        this.closeSheet(false, SheetMode.View);
      }

      this.sheetMode = DetailedSheetUtils.toggle(id, data.entity.id, this.sheetMode, data.mode);
      this.pm = {
        id: value[0].id,
        name: value[0].name,
        description: value[0].description,
        con_links: selCont,
        allLinksForAutocomplete: this.allCrLinksForAutocomplete,
        state: value[0].state,
      };
    }).catch((response: HttpErrorResponse) => { this.messageService.add({ severity: 'error', summary: DETAILEDSHEET_ERROR, detail: response.message }) });
  }

  /**
 * fired when navigation table is clicked.
 */
  onClickedNavigation(tupleOfElemAndTableNumber: ControllerDTO[]): void {
    this.filterService.ClearSelectionEmitter.emit(true);

    this.selectedController = tupleOfElemAndTableNumber[0];
    this.navigationTableDefinitions[0].selectedElement = this.selectedController;

    console.log(this.navigationTableDefinitions[0])
    if (this.selectedController != undefined) {

      this.loadProcessModel();
    }
  }

  /**
   * loads all existing Processmodels
   */
  loadProcessModel(): void {
    let userRequest: UserDisplayRequestDTO = new UserDisplayRequestDTO;
    let userId: string[] = [];
    let valueIndex: string[] = [];
    if (this.selectedController.name === this.SELECT_ALL_TEXT) {

      this.processModelDataSevice.getAllUnlinkedProcessModels(this.projectId)
        .then((value: ProcessModelResponseDTO[]) => {
          if (value !== null) {
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
          this.processmodels = value;
          this.navigationTableDefinitions[0].selectedElement = this.selectedController;
     }   }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_LOADING, detail: response.message });
        });
    } else {
      this.processModelDataSevice.getAllByControllerId(this.projectId, this.selectedController.id).then((value: ProcessModelResponseDTO[]) => {
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
        this.processmodels = value;
        this.navigationTableDefinitions[0].selectedElement = this.selectedController;
     } }).catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: EDIT_FAILED, detail: response.message });
      });
    }
  }
  changeEntity(event: { list: ProcessModelResponseDTO[], state: string }) {
    event.list.forEach((element: ProcessModelResponseDTO) => {

      let incomplete: incompleteEntityDTO = { entityName: 'process_model', parentId: undefined, state: event.state };

      this.incompleteEntityService.updateState(this.projectId, element.id, incomplete).then(value => {
        this.loadData();
      }).catch((reason: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: CHANGED_STATE_FAILED, detail: reason.message });
      })
    });
  }

  getSelectedEntities($event: any): void {
    this.selectProcessModelEntity = new SelectedEntity<Object>($event,
      this.title,
      this.projectId,
      false,
      false,
      true);
  }

  /**
   * loads all controllers for the navigation table
   */
  loadData(): void {
    let userRequest: UserDisplayRequestDTO = new UserDisplayRequestDTO;
    let userId: string[] = [];
    let valueIndex: string[] = [];
    this.controllerDataService.getAllControllers(this.projectId, {}).then(value => {
      this.navigationTableDefinitions = [];
      this.selectedController = value[0];
      if (!this.selectedController) {
        this.navigationTableDefinitions[0] = {
          style: { 'width': '100%', 'min-width': '200 px' },
          columnHeaderName: 'No Controllers found',
          columnHeaderButton: { title: 'Go to Controllers', routerLink: 'project/' + this.projectId + '/controller/' }
        };
        this.messageService.add({ severity: 'warn', summary: 'Please create Controller!' });
        return;
      }
      this.navigationTableDefinitions[0] = {
        dataSource: value,
        style: { 'width': '100%', 'min-width': '200 px' },
        columnHeaderName: 'Controller',
        columnHeaderButton: { title: 'Go to Controllers', routerLink: 'project/' + this.projectId + '/controller/' }
      };

      value.push({ name: this.SELECT_ALL_TEXT, boxId: undefined, projectId: this.projectId, });
      if (this.selectedController.name === this.SELECT_ALL_TEXT) {
        this.processModelDataSevice.getAllUnlinkedProcessModels(this.projectId).then((value: ProcessModelResponseDTO[]) => {
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
          this.processmodels = value;
     }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_LOADING, detail: response.message });
        });
      }
      else {
        //hier ProcessModel laden einmal
        this.processModelDataSevice.getAllByControllerId(this.projectId, this.selectedController.id).then((value: ProcessModelResponseDTO[]) => {
          if (value !== null) {
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
          this.processmodels = value;
       } }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_LOADING, detail: response.message });
        });
      }

    }).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_LOADING, detail: response.message });
    });

  }
  /**
   * initializes the filter table columns
   */
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
        title: 'Process-Model-Name',
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
        style: { width: '7%' },
        userName: 'lastEditor'
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

  editSheet(): void {
    this.sheetMode = SheetMode.Edit;
  }
  /**
     * closes the detailed-sheet
     */
  closeSheet(isUnlocked: boolean = false, sheetMode: SheetMode = SheetMode.Closed): void {
    if (!isUnlocked && this.sheetMode === SheetMode.Edit) {
      const unlockDTO: UnlockRequestDTO = { id: this.pm.id, entityName: this.title.toLowerCase(), parentId: this.selectedController.id };
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
  ngOnDestroy(): void {
    this.subscriptions.forEach((subscription: Subscription) => subscription.unsubscribe());
  }
}
