import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MessageService } from 'primeng/api';
import { Subject, Subscription } from 'rxjs';
import { DDEvent, DetailData } from 'src/app/common/entities/data';
import { FilteringTableElement } from 'src/app/common/filtering-table/filtering-table.component';
import { ProcessModelDataService, ProcessModelResponseDTO } from 'src/app/services/dataServices/control-structure/process-model-data.service';
import { ProcessVariableDataService, ProcessVariableRequestDTO, ProcessVariableResponseDTO } from 'src/app/services/dataServices/control-structure/process-variable-data.service';
import { ResponsibilityDataService } from 'src/app/services/dataServices/responsibility-data.service';
import { LockService } from '../../services/dataServices/lock.service';
import { SheetMode } from './../../common/detailed-sheet/detailed-sheet.component';
import { DetailedSheetUtils } from './../../common/detailed-sheet/utils/detailed-sheet-utils';
import { DetailedField, FieldType } from './../../common/entities/detailed-field';
import { ColumnType, TableColumn } from './../../common/entities/table-column';
import { AuthService } from './../../services/auth.service';
import { ControllerDataService } from './../../services/dataServices/control-structure/controller-data.service';
import { FilterService } from './../../services/filter-service/filter.service';
import { WebsocketService } from './../../services/websocket.service';
import { BoxEntityResponseDTO, ControllerDTO, PROCESS_VARIABLE, ResponsibilityResponseDTO, UnlockRequestDTO, BoxRequestDTO } from './../../types/local-types';
import { ChangeDetectionService } from 'src/app/services/change-detection/change-detection-service.service';
import { Hotkeys } from 'src/app/hotkeys/hotkeys.service';
import { incompleteEntityDTO, IncompleteEntitiesService } from 'src/app/services/dataServices/incomplete-entities-data.service';
import { UserDataService, UserDisplayRequestDTO } from './../../services/dataServices/user-data.service';
import { DETAILEDSHEET_SUCCESSFUL_EDIT, DETAILEDSHEET_FAILED_EDIT, DETAILEDSHEET_SUCCESSFUL_CREATION, DETAILEDSHEET_FAILED_CREATION, DETAILEDSHEET_EXPECTED_CHIPFIELDS_NOT_NULL, ENTITY_FAILED_LOADING, CHANGED_STATE_FAILED, ENTITY_SUCCESSFUL_DELETE, ENTITY_FAILED_DELETE, DELETED_USER, DETAILEDSHEET_EXPECTED_NAME_NOT_NULL, NO_USER } from 'src/app/globals';

interface LinksForAutoComplete {
  // TODO: typisieren sobald Service vorhanden @Eva
  input_links: BoxEntityResponseDTO[];
  responsibility_links;
  pm_links: any[];
}

interface PVforDetailedSheet {
  id: string;
  name: string;
  description: string;
  role: '' | 'DISCREET' | 'INDISCREET';
  diskret: string;
  valueStates: string[];
  nichtDiskret: string;
  pm_links: any[]; // TODO: typisieren @Eva
  input_links: BoxEntityResponseDTO[];
  responsibility_links;
  allLinksForAutocomplete: LinksForAutoComplete;
  state: string;
}

@Component({
  selector: 'app-process-variable-table',
  templateUrl: './process-variable-table.component.html',
  styleUrls: ['./process-variable-table.component.css']
})
export class ProcessVariableTableComponent implements OnDestroy {

  SheetMode: typeof SheetMode = SheetMode;
  private projectId: string;
  rangeArray = [];
  pmArray = [];
  subscriptions: Subscription[] = [];

  processvariables: Array<ProcessVariableResponseDTO> = [];
  readonly SELECT_ALL_TEXT: string = 'Unlinked Process Variables';
  isPm: boolean = true;
  columns: TableColumn[] = [];
  detailedColumns: TableColumn[] = [];
  pv: PVforDetailedSheet;
  fields: DetailedField[];
  title: string = DetailedSheetUtils.generateSheetTitle(PROCESS_VARIABLE);
  navigationTableDefinitions = [];
  filteringTableDefinitions = [];
  updateSelectedItemSubject: Subject<{ element: FilteringTableElement, columnNumber: number }> =
    new Subject<{ element: FilteringTableElement, columnNumber: number }>();

  selectedInput: BoxEntityResponseDTO;
  selectedPm: ProcessModelResponseDTO;
  selectedController: ControllerDTO;
  selectedResponsibility;
  selectedValue;
  FilterService = null;
  advancedFilterSubscription = null;
  origNavData = [];
  allControllers = [];
  sheetMode: SheetMode = SheetMode.Closed;
  dropValues: Map<string, string> = new Map<string, string>([]);

  private OwnChange: boolean = false;
  disableSaveButton: boolean = false;
  private allLinksForAutocomplete: LinksForAutoComplete;

  constructor(
    private readonly controllerDataService: ControllerDataService,
    private readonly route: ActivatedRoute,
    private readonly processVariableDataService: ProcessVariableDataService,
    private readonly processModelDataSevice: ProcessModelDataService,
    private readonly filterService: FilterService,
    private readonly messageService: MessageService,
    private readonly wsService: WebsocketService,
    private readonly authService: AuthService,
    private readonly lockService: LockService,
    private readonly responsibilityDataService: ResponsibilityDataService,
    private readonly cds: ChangeDetectionService,
    private readonly incompleteEntityService: IncompleteEntitiesService,
    private readonly userDataService: UserDataService,
    private readonly hotkeys: Hotkeys) {

    this.subscriptions.push(this.hotkeys.addShortcut({ keys: 'shift.control.n', description: 'Create Process Variable' }).subscribe(() => {
      this.onCreatePv();
    }));
    this.dropValues.set('DISCREET', 'Discrete Variable');
    this.dropValues.set('INDISCREET', 'Indiscrete Variable');

    this.fields = [
      { title: 'ID', key: 'id', type: FieldType.Text, readonly: true },
      { title: 'Name', key: 'name', type: FieldType.Text, readonly: false },
      { title: 'Description', key: 'description', type: FieldType.Text_Variable, readonly: false, minRows: 3, maxRows: 10 },
      { title: 'Variable type', key: 'role', type: FieldType.Dropdown, readonly: false, dropDownText: 'Choose variable type...', values: this.dropValues },
      { title: 'Discrete Variable', key: 'diskret', type: FieldType.ButtonGroup, readonly: false, listKey: 'valueStates', hidden: true },
      { title: 'Indiscrete Variable', key: 'nichtDiskret', type: FieldType.Text, readonly: false, minRows: 3, maxRows: 10, hidden: true },
      {
        title: 'Process Model', key: 'pm_links', type: FieldType.Chips, readonly: false, listKey: 'id', displayShortName: 'PM-',
        shortcutButton: { title: 'Go to Process Models', routerLink: (): string => 'project/' + this.projectId + '/processmodel-table/' }
      },
      {
        title: 'Input Links', key: 'input_links', type: FieldType.Chips_Single, readonly: false, listKey: 'name', displayShortName: 'I-',
        shortcutButton: {
          title: 'Go to Control Structure',
          routerLink: (): string => '/project/' + this.projectId + '/control-structure-diagram/STEP2/'
        }
      },
      {
        title: 'Responsibilities', key: 'responsibility_links', type: FieldType.Chips_Single, readonly: false, listKey: 'id', displayShortName: 'R-',
        shortcutButton: { title: 'Go to Responsibilities', routerLink: (): string => 'project/' + this.projectId + '/responsibilities/' }
      },
      { title: 'State', key: 'state', type: FieldType.StateButtonGroup, readonly: false },
    ];


    this.route.parent.params.subscribe((value: any) => {
      this.projectId = value.id;
      this.initColumns();

      this.filteringTableDefinitions[0] = {
        dataSource: [],
        style: { 'width': '50%', 'min-width': '200 px' },
        columnHeaderName: 'No Process Models found',
        columnHeaderButton: {
          title: 'Go to Process Models',
          routerLink: 'project/' + this.projectId + '/processmodel-table/'
        }
      };

      this.filteringTableDefinitions[1] = {
        dataSource: [],
        style: { 'width': '50%', 'min-width': '200 px' },
        columnHeaderName: 'No Input found',
        columnHeaderButton: {
          title: 'Go to Control Structure',
          routerLink: 'project/' + this.projectId + '/control-structure-diagram/2/'
        }
      };

      this.loadData();
    });

    this.authService.getProjectToken().then((token: string) => {
      this.wsService.connect('subscribe', PROCESS_VARIABLE, token, (data: any) => {
        if (this.OwnChange === false) {
          this.messageService.add({ severity: 'info', summary: 'User ' + data.displayName + ' updated the data!' });
        }
      });
    }).catch((err: HttpErrorResponse) => {
      console.log(err);
    });

  }

  /**
   * fired when navigation table is clicked.
   */
  onClickedNavigation(tupleOfElemAndTableNumber: [ControllerDTO, number]): void {
    this.filterService.ClearSelectionEmitter.emit(true);
    if (tupleOfElemAndTableNumber[1] === 0) {
      this.selectedController = tupleOfElemAndTableNumber[0];

      this.loadTableColumnes(); // loads controller inputs and controller PM's
    }
  }

  /**
   * fired on filter-table-element click
   */
  onClickedFilteringTable(tupleOfElemAndTableNumber: [FilteringTableElement, number]): void {
    this.filterService.ClearSelectionEmitter.emit(true);
    if (tupleOfElemAndTableNumber[1] === 0) {
      this.selectedPm = tupleOfElemAndTableNumber[0] as ProcessModelResponseDTO;
    } else if (tupleOfElemAndTableNumber[1] === 1) {
      this.selectedInput = tupleOfElemAndTableNumber[0] as BoxEntityResponseDTO;
    }
    this.loadPVs(); // loads the list with the PV's
  }

  /**
   * lodas the PV-list for the selected controller, model and input
   */
  loadPVs(): void {
    let userRequest: UserDisplayRequestDTO = new UserDisplayRequestDTO;
    let userId: string[] = [];
    let valueIndex: string[] = [];
    if (!!this.selectedPm && this.selectedInput.name === this.SELECT_ALL_TEXT && !!this.selectedController) {
      this.processVariableDataService.getAllUnlinkedProcessModels(this.projectId, 'input').then((value: ProcessVariableResponseDTO[]) => {

        if(value !== null){
          value.forEach(element => {
            if(element.last_editor_id !== null && element.last_editor_id !== ''){
              userId.push(element.last_editor_id);
              valueIndex.push(value.indexOf(element).toString());

            }else{
              element.last_editor_displayname = NO_USER;
              element.icon = this.userDataService.getDefaultOrDeleteIcon(element.last_editor_displayname);
            }
        }
        );
        if(valueIndex.length !== 0){
          userRequest.userIds = userId;
          this.userDataService.getUserDisplay(userRequest).then((map:Map<string, string[]>) =>{
            valueIndex.forEach(element =>{

                let nameIcon = map.get(value[element].last_editor_id);

                if(nameIcon[0] !== null){

                  value[element].last_editor_displayname = nameIcon[0];
                }
                if(nameIcon[1] !== ''){
                  value[element].icon = nameIcon[1];
                }else{
                  value[element].icon = this.userDataService.getDefaultOrDeleteIcon(nameIcon[1]);
                }


            });
          });

        }
          this.processvariables = value;}
      }).catch((response: HttpErrorResponse) => {
        this.messageService.add({
          severity: 'error',
          summary: ENTITY_FAILED_LOADING,
          detail: response.message
        });
      });
    }
    if (!!this.selectedPm && !!this.selectedInput && !!this.selectedController) {
      this.processVariableDataService.getProcessVariablesBySource(this.projectId, this.selectedInput.id, this.selectedPm.id)
        .then((value: ProcessVariableResponseDTO[]) => {
          if(value !== null){
            value.forEach(element => {
              if(element.last_editor_id !== null && element.last_editor_id !== ''){
                userId.push(element.last_editor_id);
                valueIndex.push(value.indexOf(element).toString());

              }else{
                element.last_editor_displayname = NO_USER;
                element.icon = this.userDataService.getDefaultOrDeleteIcon(element.last_editor_displayname);
              }
          }
          );
          if(valueIndex.length !== 0){
            userRequest.userIds = userId;
            this.userDataService.getUserDisplay(userRequest).then((map:Map<string, string[]>) =>{
              valueIndex.forEach(element =>{

                  let nameIcon = map.get(value[element].last_editor_id);

                  if(nameIcon[0] !== null){

                    value[element].last_editor_displayname = nameIcon[0];
                  }
                  if(nameIcon[1] !== ''){
                    value[element].icon = nameIcon[1];
                  }else{
                    value[element].icon = this.userDataService.getDefaultOrDeleteIcon(nameIcon[1]);
                  }


              });
            });

          }
          this.processvariables = value;
            }
        })
        .catch((response: HttpErrorResponse) => {
          this.messageService.add({
            severity: 'error',
            summary: ENTITY_FAILED_LOADING,
            detail: response.message
          });
        });
    }

  }

  /**
   * shows a new empty detailed-sheet for PV-creation
   */
  onCreatePv(): void {
    Promise.all([
      this.processModelDataSevice.getAllByControllerId(this.projectId, this.selectedController.id),
      this.responsibilityDataService.getResponsibilitiesByControllerId(this.projectId, this.selectedController.id),
      this.controllerDataService.getSourceBoxByControllerId(this.projectId, this.selectedController.id),
    ]).then((value: [ProcessModelResponseDTO[], ResponsibilityResponseDTO[], BoxEntityResponseDTO[]]) => {
      if (value[1].length === 0) {
        this.messageService.add({ severity: 'warn', summary: 'Please create Responsibility' })
      } else {
        this.sheetMode = SheetMode.Closed;
        this.isPm = false;
        this.allLinksForAutocomplete = { responsibility_links: [], input_links: [], pm_links: [] };

        this.pv = {
          id: '',
          name: '',
          description: '',
          role: '',
          diskret: '',
          valueStates: [],
          nichtDiskret: '',
          input_links: [],
          pm_links: [],
          responsibility_links: [],
          allLinksForAutocomplete: { responsibility_links: [], input_links: [], pm_links: [] },
          state: '',
        };

        let selPm: ProcessModelResponseDTO[] = [];
        for (let index: number = 0; index < value[0].length; index++) {
          if (this.selectedPm.id === value[0][index].id) {
            selPm.push(value[0][index]);
            break;
          }
        }

        // check if pm is set
        if (!this.selectedPm) {
          this.messageService.add({ severity: 'warn', summary: 'Please create Process Model!' });
          this.sheetMode = SheetMode.Closed;
          return;
        }

        if (!!value[1] && !!value[1][0] && !!value[1][0].id) {
          // this.selectedResponsibility = value[1][0].id;
        } else {
          //  check if resp is set
          this.messageService.add({ severity: 'warn', summary: 'Please create Responsibility' });
          this.sheetMode = SheetMode.Closed;
          return;
        }

        let selInput: BoxEntityResponseDTO[] = [];
        for (let index2: number = 0; index2 < value[2].length; index2++) {
          if (this.selectedInput.id === value[2][index2].id) {
            selInput.push(value[2][index2]);
          }
        }

        if (!this.selectedInput) {
          this.messageService.add({ severity: 'warn', summary: 'Please create Input!' });
          this.sheetMode = SheetMode.Closed;
          return;
        }

        this.allLinksForAutocomplete.pm_links = !!value[0] ? value[0] : [];
        this.allLinksForAutocomplete.responsibility_links = !!value[1] ? value[1] : [];
        this.allLinksForAutocomplete.input_links = !!value[2] ? value[2] : [];

        this.pv = {
          id: '',
          name: '',
          description: '',
          role: '',
          diskret: '',
          valueStates: [],
          nichtDiskret: '',
          input_links: selInput,
          pm_links: selPm,
          responsibility_links: [],
          allLinksForAutocomplete: {
            ...this.allLinksForAutocomplete,
          },
          state: '',
        };

        this.sheetMode = SheetMode.New;
      }

    }).catch((response: HttpErrorResponse) => {

      this.messageService.add({ severity: 'error', summary: DETAILEDSHEET_FAILED_CREATION, detail: response.message });
    });
  }

  /**
   * sets the visibility of the pv-value fields
   * a discreet variable has a button goup, a indiscreet variable a textfield
   */
  dropDownPv($event: DDEvent): void {
    this.fields.forEach((element: DetailedField) => {
      if ($event.value === 'DISCREET') {
        if (element.key === 'diskret') {
          element.hidden = false;
        } else if (element.key === 'nichtDiskret') {
          element.hidden = true;
        }
      } else if ($event.value === 'INDISCREET') {
        if (element.key === 'nichtDiskret') {
          element.hidden = false;
        } else if (element.key === 'diskret') {
          element.hidden = true;
        }
      }
    });
  }

  /**
   * tries to save the process-variable
   */
  save(pv: DetailData): void {
    let pmLinks: string[] = this.pv.pm_links.map((ele: ProcessModelResponseDTO) => {
      return ele.id;
    });

    let respLinks: string[] = this.pv.responsibility_links.map((ele: ResponsibilityResponseDTO) => {
      return ele.id;
    });

    let inputLinks: BoxRequestDTO[] = this.pv.input_links.map((ele: BoxEntityResponseDTO): BoxRequestDTO => ({
      id: ele.id,
      name: ele.name,
      projectId: this.projectId
    }));

    if(!pv.ent.name || pv.ent.name.trim() === ''){
      this.messageService.add({ severity: 'warn', summary: DETAILEDSHEET_EXPECTED_NAME_NOT_NULL })
      this.disableSaveButton = false;
      return;
    }

    let value: ProcessVariableRequestDTO = {
      name: this.pv.name,
      description: this.pv.description,
      source: inputLinks[0],
      variable_type: this.pv.role,
      variable_value: this.pv.role === 'DISCREET' ? this.pv.diskret : this.pv.nichtDiskret,
      currentProcessModel: this.selectedPm.id,
      process_models: pmLinks,
      valueStates: this.pv.valueStates,
      responsibilityIds: respLinks,
      state: this.pv.state,
    };

    if (value.variable_value.length === 0) {
      this.messageService.add({ severity: 'warn', summary: 'Select a variable value!' });
      return;
    }

    if (pmLinks.length === 0 || respLinks.length === 0 || inputLinks.length === 0) {
      this.messageService.add({ severity: 'warn', summary: DETAILEDSHEET_EXPECTED_CHIPFIELDS_NOT_NULL });
    } else {
      if (pv.mode === SheetMode.New) {
        this.processVariableDataService.createProcessVariable(this.projectId, this.selectedController.id, value)
          .then(() => {
            this.messageService.add({ severity: 'success', summary: DETAILEDSHEET_SUCCESSFUL_CREATION });
            this.closeSheet(true);
          })
          .catch((response: HttpErrorResponse) => {
            this.messageService.add({ severity: 'error', summary: DETAILEDSHEET_FAILED_CREATION, detail: response.message });
          })
          .finally(() => {
            this.OwnChange = false;
            this.disableSaveButton = false;
            this.loadPVs();
          });
      }

      if (pv.mode === SheetMode.EditWithLock) {
        this.processVariableDataService.alterProcessVariable(
          this.projectId,
          this.selectedController.id,
          this.pv.pm_links.findIndex((item: any) => item.id === this.selectedPm.id) > -1 ? this.selectedPm.id : this.pv.pm_links[0].id,
          this.pv.id,
          value
        )
          .then(() => {
            this.messageService.add({ severity: 'success', summary: DETAILEDSHEET_SUCCESSFUL_EDIT });
          })
          .catch((response: HttpErrorResponse) => {
            this.messageService.add({ severity: 'error', summary: DETAILEDSHEET_FAILED_EDIT, detail: response.message });
          })
          .finally(() => {
            this.closeSheet(true);
            this.OwnChange = false;
            this.disableSaveButton = false;
            this.loadPVs();
          });
      }
      this.fields.forEach((field: DetailedField) => {
        if (field.key === 'nichtDiskret' || field.key === 'diskret') {
          field.hidden = true;
        }
      });
    }
  }

  /**
   * delets the selected process-variables
   */
  async delete($event): Promise<boolean> {
    this.OwnChange = true;
    const list: any[] = $event;
    if (list.length === 0) {
      return;
    }

    const promiseList = list.map(async (elem: any) => {
      await this.lockService.lockEntity(
        this.projectId,
        {
          id: elem.id,
          entityName: this.title.toLowerCase(),
          expirationTime: DetailedSheetUtils.calculateLockExpiration()
        });
      return await this.processVariableDataService.deleteProcessVariable(
        this.projectId,
        elem.id
      );
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
    this.loadPVs();
  }

  /**
   * initializes the filter table columns
   */
  private initColumns(): void {
    // Note Views with navigation table have checkbox width  10% for aesthetic reasons
    this.columns = [
      {
        key: 'select',
        title: 'Select',
        type: ColumnType.Checkbox,
        style: { 'width': '5%' }
      }, {
        key: 'id',
        title: 'ID',
        type: ColumnType.Text,
        style: { 'width': '5%' }
      }, {
        key: 'name',
        title: 'Process-Variable-Name',
        type: ColumnType.Text,
        style: { 'width': '20%' }
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
        userName: 'last_editor_displayname',
        style: {width: '6%'}
      },
      {
        key: 'last_edited',
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

  changeEntity(event: { list: ProcessVariableResponseDTO[], state: string }) {
    event.list.forEach((element: ProcessVariableResponseDTO) => {

      let incomplete: incompleteEntityDTO = { entityName: 'process_variable', parentId: undefined, state: event.state };

      this.incompleteEntityService.updateState(this.projectId, element.id, incomplete).then(value => {
        this.loadTableColumnes();
      }).catch((reason: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary:CHANGED_STATE_FAILED, detail: reason.message });
      });
    });
  }
  /**
   * loads all controllers for the navigation table
   */
  loadData(): void {
    this.controllerDataService.getAllControllers(this.projectId, {})
      .then((value: ControllerDTO[]) => {
        this.navigationTableDefinitions = [];
        this.navigationTableDefinitions[0] = {
          dataSource: value,
          style: { 'width': '100%', 'min-width': '200 px' },
          columnHeaderName: 'Controller',
          columnHeaderButton: { title: 'Go to Controllers', routerLink: 'project/' + this.projectId + '/controller/' }
        };

        // fires if controller is not selected
        if (!!value && value.length > 0 && !this.selectedController) {
          this.selectedController = value[0];

          this.loadTableColumnes();
        } else {
          this.navigationTableDefinitions[0].columnHeaderName = 'No Controller Found';
        }
      })
      .catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_LOADING, detail: response.message });
      });
  }

  /**
   * oads the tables for the
   */
  loadTableColumnes(): void {
    Promise.all([
      this.processModelDataSevice.getAllByControllerId(this.projectId, this.selectedController.id),
      this.controllerDataService.getSourceBoxByControllerId(this.projectId, this.selectedController.id),
    ]).then((value: [ProcessModelResponseDTO[], BoxEntityResponseDTO[]]) => {
      value[1].push({
        name: this.SELECT_ALL_TEXT, lockHolderDisplayName: undefined, lockHolderId: undefined,
        id: undefined, description: undefined, lastEdited: undefined, lastEditor: undefined,
        lastEditorId: undefined, lockExpirationTime: undefined,
      });
      if (value[0] !== null && value[0].length > 0) {
        this.filteringTableDefinitions[0].dataSource = value[0];
        this.filteringTableDefinitions[0].columnHeaderName = 'Process Model';
        this.selectedPm = value[0][0];
        this.updateSelectedItemSubject.next({ element: value[0][0], columnNumber: 0 });

        if (value[1] !== null && value[1].length > 0) {
          this.filteringTableDefinitions[1].dataSource = value[1];
          this.filteringTableDefinitions[1].columnHeaderName = 'Box Input';
          this.selectedInput = value[1][0];

          this.updateSelectedItemSubject.next({ element: value[1][0], columnNumber: 1 });
          this.loadPVs();
        } else {
          this.filteringTableDefinitions[1].dataSource = [];
          this.selectedInput = null;
          this.updateSelectedItemSubject.next({ element: null, columnNumber: 1 });
          this.messageService.add({ severity: 'info', summary: 'No Inputs found!' });
        }

      } else {
        this.filteringTableDefinitions[0].dataSource = [];
        this.filteringTableDefinitions[1].dataSource = [];
        this.selectedPm = null;
        this.updateSelectedItemSubject.next({ element: null, columnNumber: 0 });
        this.selectedInput = null;
        this.processvariables = undefined;
        this.updateSelectedItemSubject.next({ element: null, columnNumber: 1 });
        this.messageService.add({ severity: 'info', summary: 'No Process Models found!' });
      }
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
    this.isPm = false;
    this.disableSaveButton = false;

    this.allLinksForAutocomplete = { input_links: [], pm_links: [], responsibility_links: [] };
    Promise.all([
      this.processModelDataSevice.getAllByControllerId(this.projectId, this.selectedController.id),
      this.responsibilityDataService.getResponsibilitiesByControllerId(this.projectId, this.selectedController.id),
      this.controllerDataService.getSourceBoxByControllerId(this.projectId, this.selectedController.id),

    ]).then((valueArray: [ProcessModelResponseDTO[], ResponsibilityResponseDTO[], BoxEntityResponseDTO[]]) => {

      let selPm: ProcessModelResponseDTO[] = [];
      for (let ele of (data.entity as ProcessVariableResponseDTO).process_models) {
        for (let pm of valueArray[0]) {
          if (pm.id === ele) {
            selPm.push(pm);
          }
        }
      }

      let selResponsibility: ResponsibilityResponseDTO[] = [];
      for (let resp of valueArray[1]) {
        if ((data.entity as ProcessVariableResponseDTO).responsibilityIds.length > 0 &&
          resp.id === (data.entity as ProcessVariableResponseDTO).responsibilityIds[0]) {
          selResponsibility.push(resp);
        }
      }

      let selInput: BoxEntityResponseDTO[] = [];
      for (let input of valueArray[2]) {
        if ((data.entity as ProcessVariableResponseDTO).source &&
          (data.entity as ProcessVariableResponseDTO).source.id === input.id) {
          selInput.push(input);
        }
      }

      this.allLinksForAutocomplete.pm_links = valueArray[0];
      this.allLinksForAutocomplete.responsibility_links = valueArray[1];
      this.allLinksForAutocomplete.input_links = valueArray[2];

      this.pv = {
        id: data.entity.id,
        name: data.entity.name,
        description: data.entity.description,
        role: data.entity.variable_type,
        diskret: data.entity.variable_value,
        valueStates: !!data.entity.valueStates ? data.entity.valueStates : [],
        nichtDiskret: data.entity.variable_value,
        input_links: selInput,
        pm_links: selPm,
        responsibility_links: selResponsibility,
        allLinksForAutocomplete: {
          responsibility_links: this.allLinksForAutocomplete.responsibility_links,
          input_links: this.allLinksForAutocomplete.input_links,
          pm_links: this.allLinksForAutocomplete.pm_links,
        },
        state: data.entity.state,
      };

      let id: string;
      if (!!this.pv) {
        id = this.pv.id;
      }

      if (this.sheetMode === SheetMode.Edit) {
        this.closeSheet(false, SheetMode.View);
      }

      this.sheetMode = DetailedSheetUtils.toggle(id, data.entity.id, this.sheetMode, data.mode);
      this.dropDownPv({ title: 'role', value: this.pv.role });
    });
  }

  /**
   * closes the detailed-sheet
   */
  closeSheet(isUnlocked: boolean = false, sheetMode: SheetMode = SheetMode.Closed): void {
    if (!isUnlocked && this.sheetMode === SheetMode.Edit) {
      const unlockDTO: UnlockRequestDTO = { id: this.pv.id, entityName: this.title.toLowerCase(), parentId: this.selectedController.id };
      this.lockService.unlockEntity(this.projectId, unlockDTO).then(() => {
        this.sheetMode = sheetMode;
      }).catch(() => {
        this.sheetMode = sheetMode;
      });
    } else {
      this.sheetMode = sheetMode;
    }
    this.disableSaveButton = false;
    this.fields.forEach((field: DetailedField) => {
      if (field.key === 'nichtDiskret' || field.key === 'diskret') {
        field.hidden = true;
      }
    });
  }

  editSheet(): void {
    this.sheetMode = SheetMode.Edit;
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((subscription: Subscription) => subscription.unsubscribe());
  }
}
