import { Component, EventEmitter, OnDestroy, Output } from '@angular/core';
import { incompleteEntityDTO } from './../../services/dataServices/incomplete-entities-data.service';
import { IncompleteEntitiesService } from 'src/app/services/dataServices/incomplete-entities-data.service';
import { ActivatedRoute } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { MessageService } from 'primeng/api';
import { LockService } from '../../services/dataServices/lock.service';
import { SheetMode } from './../../common/detailed-sheet/detailed-sheet.component';
import { DetailedSheetUtils } from './../../common/detailed-sheet/utils/detailed-sheet-utils';
import { DetailedField, FieldType } from './../../common/entities/detailed-field';
import { ColumnType, TableColumn } from './../../common/entities/table-column';
import { AuthService } from './../../services/auth.service';
import { ControlActionDataService } from './../../services/dataServices/control-structure/control-action-data.service';
import { ControllerDataService } from './../../services/dataServices/control-structure/controller-data.service';
import { RuleDataService } from './../../services/dataServices/rule-data.service';
import { FilterService } from './../../services/filter-service/filter.service';
import { ProcessVariableDataService, ProcessVariableResponseDTO } from './../../services/dataServices/control-structure/process-variable-data.service';
import { WebsocketService } from './../../services/websocket.service';
import {
  ControllerDTO,
  ControlActionDTO,
  UnlockRequestDTO,
  ControlActionResponseDTO,
  ControllerConstraintResponseDTO,
  ImplementationConstraintResponseDTO,
  LossScenarioResponseDTO,
  UcaResponseDTO,
  RuleResponseDTO,
  RULE,
} from './../../types/local-types';

import { EXPECTED_ENTITY_NOT_NULL, CHANGED_STATE_FAILED, DELETED_USER, ENTITY_FAILED_LOADING, DETAILEDSHEET_FAILED_LOADING_CHIPS, DETAILEDSHEET_FAILED_CREATION, DETAILEDSHEET_SUCCESSFUL_CREATION, DETAILEDSHEET_FAILED_EDIT, DETAILEDSHEET_SUCCESSFUL_EDIT, ENTITY_SUCCESSFUL_DELETE, ENTITY_FAILED_DELETE, NO_USER } from './../../globals';
import { ProcessModelDataService, ProcessModelResponseDTO } from 'src/app/services/dataServices/control-structure/process-model-data.service';
import { Hotkeys } from 'src/app/hotkeys/hotkeys.service';
import { NavigationTableComponent } from 'src/app/common/navigation-table/navigation-table.component';
import { UserDataService, UserDisplayRequestDTO } from 'src/app/services/dataServices/user-data.service';
import { Subscription } from 'rxjs';
import { UcaDataService } from '../../services/dataServices/uca-data.service';
import { ControllerConstraintDataService } from '../../services/dataServices/controller-constraint-data.service';
import { LossScenarioDataService } from '../../services/dataServices/loss-scenario.service';
import { ImplementationConstraintDataService } from '../../services/dataServices/implementation-constraint-data.service';
import { SelectedEntity } from '../dependent-element-tree/dependent-elements-types.component';

interface ControlActionsForAutoComplete {
  links: ControlActionDTO[];
}

interface RuleForDetailedSheet {
  id: string;
  rule: string;
  controllerId: string;
  controllerName: string;
  links: ControlActionResponseDTO[];
  allLinksForAutocomplete: ControlActionsForAutoComplete;
  oldControlActionId: string;
  state: string;
}

@Component({
  selector: 'app-control-algorithm',
  templateUrl: './control-algorithm.component.html',
  styleUrls: ['./control-algorithm.component.css']
})
export class ControlAlgorithmComponent implements OnDestroy {

  SheetMode: typeof SheetMode = SheetMode;
  private projectId: string;
  rules: RuleResponseDTO[] = [];
  columns: TableColumn[] = [];
  rule: RuleForDetailedSheet;
  fields: DetailedField[];
  title: string = DetailedSheetUtils.generateSheetTitle(RULE);
  navigationTableDefinitions = [];
  selectedController: ControllerDTO;
  origNavData = [];
  allControllers = [];
  sheetMode: SheetMode = SheetMode.Closed;
  private allLinksForAutocomplete: ControlActionsForAutoComplete;
  private OwnChange: boolean = false;
  disableSaveButton: boolean = false;
  autoCompleteWords: any[] = [];
  subscriptions: Subscription[] = [];
  examples: string[] = [
    '- distance^2 - 5',
    '- sin(angle)',
    '- [5 < speed < 10] OR [speed = 0]',
    '- (obstacle OR red_traffic_light) AND [speed > 1.1 * safe_speed]'];
  selectControlAlgorithmEntity: SelectedEntity<Object>;

  @Output() selectEntityMessage: any = new EventEmitter<SelectedEntity<Object>>();

  constructor(
    private readonly controllerDataService: ControllerDataService,
    private readonly controlActionDataService: ControlActionDataService,
    private readonly ruleDataService: RuleDataService,
    private readonly route: ActivatedRoute,
    private readonly filterService: FilterService,
    private readonly messageService: MessageService,
    private readonly wsService: WebsocketService,
    private readonly authService: AuthService,
    private readonly lockService: LockService,
    private readonly processVariableDataService: ProcessVariableDataService,
    private readonly processModelDataService: ProcessModelDataService,
    private readonly hotkeys: Hotkeys,
    private readonly incompleteEntityService: IncompleteEntitiesService,
    private readonly userDataService: UserDataService) {

    this.subscriptions.push(this.hotkeys.addShortcut({ keys: 'shift.control.n', description: 'Create Rule' }).subscribe(() => {
      this.createRule();
    }));
    this.fields = [
      { title: 'Controller', key: 'controllerName', type: FieldType.Text, readonly: true },
      { title: 'ID', key: 'id', type: FieldType.Text, readonly: true },
      {
        title: 'Control Action', key: 'links', type: FieldType.Chips_Single, readonly: false, listKey: 'name',
        shortcutButton: { title: 'Go to Control Actions', routerLink: (): string => 'project/' + this.projectId + '/control-action/' }
      },
      { title: 'Rule', key: 'rule', type: FieldType.Text_Variable_With_Auto_Completion, readonly: false, minRows: 3, maxRows: 10 },
      { title: 'State', key: 'state', type: FieldType.StateButtonGroup, readonly: false },
    ];
    this.deleteRule = this.deleteRule.bind(this);
    this.route.parent.params.subscribe((value: any) => {
      this.projectId = value.id;
      this.initColumns();
      this.loadData();
    });

    // Websocket implementation
    this.authService.getProjectToken().then((token: string) => {
      this.wsService.connect('subscribe', RULE, token, (data) => {
        this.loadData();
        if (this.OwnChange === false) {
          this.messageService.add({ severity: 'info', summary: 'User ' + data.displayName + ' updated the data!' });
        }
      });
    }).catch((err: HttpErrorResponse) => {
      console.log(err);
    });
  }

  changeEntity(event: { list: RuleResponseDTO[], state: string }) {
    event.list.forEach((element: RuleResponseDTO) => {
      // Use rule as entity name, because control algorithms seem to be stored there - Timo
      let incompleteEntity: incompleteEntityDTO = { entityName: 'rule', parentId: element.controllerId, state: event.state };

      this.incompleteEntityService.updateState(this.projectId, element.id, incompleteEntity).then(() => {
        this.loadData();
      }).catch((reason: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: CHANGED_STATE_FAILED, detail: reason.message });
      })
    });
  }

  getSelectedEntities($event: any): any {
    this.selectControlAlgorithmEntity = new SelectedEntity<Object>($event,
      this.title,
      this.projectId,
      false,
      false,
      true);
  }

  // Optional set lastTimeEdited
  private loadData(): void {
    // load Rule into table if Controller is selected
    if (typeof this.selectedController !== 'undefined') {
      let userRequest: UserDisplayRequestDTO = new UserDisplayRequestDTO;
      let userId: string[] = [];
      let valueIndex: string[] = [];
      this.ruleDataService.getAllRules(this.projectId, this.selectedController.id, {})
        .then((value: RuleResponseDTO[]) => {
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
            this.rules = value;
        }}).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_LOADING, detail: response.message });
        });
    } else {
      this.controllerDataService.getAllControllers(this.projectId, { 'orderBy': 'id', 'orderDirection': 'asc' })
        .then((value: ControllerDTO[]) => {
          this.navigationTableDefinitions = [];
          this.navigationTableDefinitions[0] = {
            dataSource: value, style: { width: '100%' }, columnHeaderName: 'Controller',
            columnHeaderButton: { title: 'Go to Controllers', routerLink: 'project/' + this.projectId + '/controller/' }
          };
          if (value !== null && value.length > 0) {
            this.selectedController = value[0];
            this.loadData();
          } else {
            this.navigationTableDefinitions[0].columnHeaderName = 'No Controllers found';
            this.messageService.add({
              severity: 'warn',
              life: 10000, summary: 'You need to define a controller first.'
            });
          }
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_LOADING, detail: response.message });
        });
    }
  }

  createRule(): void {
    this.sheetMode = SheetMode.New;
    // necessary to pass empty element first, else old data will be loaded
    // since the request for allLinksForAutocomplete is asynchronous and takes time to load new data
    this.rule = {
      id: '',
      rule: '',
      controllerId: this.selectedController.id,
      controllerName: this.selectedController.name,

      links: [],
      allLinksForAutocomplete: { links: [] },
      oldControlActionId: '0',
      state: '',
    };
    this.controlActionDataService.getAllControlActions(this.projectId, {})
      .then((value: ControlActionDTO[]) => {
        this.rule = {
          id: '',
          rule: '',
          controllerId: this.selectedController.id,
          controllerName: this.selectedController.name,
          links: [],
          allLinksForAutocomplete: { links: value },
          oldControlActionId: '0',
          state: '',
        };
      }).catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: DETAILEDSHEET_FAILED_LOADING_CHIPS, detail: response.message });
      });
    this.autoCompleteWords = [];

    Promise.all([
      this.processModelDataService.getAllByControllerId(this.projectId, this.selectedController.id),
      this.processVariableDataService.getAllProcessVariables(this.projectId)
    ])
      .then((value: [ProcessModelResponseDTO[], ProcessVariableResponseDTO[]]) => {
        if (!value[0] || !value[1]) {
          // TODO: fehlermeldung
          return;
        }
        for (let vari of value[1]) {
          value[0].forEach((pm: ProcessModelResponseDTO) => {
            if (vari.process_models.findIndex((pmId: string) => pmId === pm.id) > -1) {
              this.autoCompleteWords.push(vari);
            }
          });
        }
      })
      .catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_LOADING, detail: response.message });
      });
  }

  saveRule(rule): void {
    this.filterService.ClearSelectionEmitter.emit(true);
    this.OwnChange = true;
    this.disableSaveButton = true;
    console.log(rule)
    if (!rule.ent.rule || rule.ent.rule.trim() === '') {
      this.messageService.add({ severity: 'warn', summary: 'Add Rule' })
      this.disableSaveButton = false;
      return;
    }
    if (rule.mode === SheetMode.EditWithLock) {

      const promises = [];
      const ruleData = {
        projectId: this.projectId,
        id: rule.ent.id,
        rule: rule.ent.rule,
        controlActionId: rule.ent.oldControlActionId,
        state: rule.ent.state,
      };
      if (rule.deletedMap.has('links')) {
        for (const newLink of rule.deletedMap.get('links')) {
          ruleData.controlActionId = '0';
        }
      }
      if (rule.addedMap.has('links')) {
        for (const newLink of rule.addedMap.get('links')) {
          ruleData.controlActionId = newLink.id;
        }
      }
      promises.push(this.ruleDataService.editRule(this.projectId, this.selectedController.id, rule.ent.id, ruleData));

      Promise.all(promises).then(() => {
        this.messageService.add({ severity: 'success', summary: DETAILEDSHEET_SUCCESSFUL_EDIT });
        this.closeSheet(true);
      }).catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: DETAILEDSHEET_FAILED_EDIT, detail: response.message });
      }).finally(() => {
        this.OwnChange = false;
        this.disableSaveButton = false;
        this.loadData();
      });

    } else if (rule.mode === SheetMode.New) {
      let ruleData = {
        projectId: this.projectId,
        id: rule.ent.id,
        rule: rule.ent.rule,
        controlActionId: '',
        state: rule.ent.state,
      };
      if (rule.addedMap.has('links')) {
        for (const newLink of rule.addedMap.get('links')) {
          ruleData.controlActionId = newLink.id;
        }
      }
      this.ruleDataService.createRule(this.projectId, this.selectedController.id, ruleData)
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

  async deleteRule($event): Promise<void> {
    this.OwnChange = true;
    const list: any[] = $event;
    if (list.length === 0) {
      return;
    }

    const promiseList = list.map(async (elem: any) => {
      await this.lockService.lockEntity(this.projectId, {
      id: elem.id,
      parentId: elem.controllerId,
      entityName: RULE,
      expirationTime: DetailedSheetUtils.calculateLockExpiration(),
    });
    return await this.ruleDataService.deleteRule(this.projectId, this.selectedController.id, elem.id);
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

  onClickedNavigation(tupleOfElemAndTableNumber): void {
    this.filterService.ClearSelectionEmitter.emit(true);
    this.selectedController = tupleOfElemAndTableNumber[0];
    this.navigationTableDefinitions[0].selectedElement = this.selectedController;
    this.loadData();
  }

  // data is an object passed from the main-table.ts
  // it is an object with {entity:,mode:} and entity was passed from here i.e. the rule object
  toggleSheet(data): void {
    this.disableSaveButton = false;
    let id: string;
    if (this.rule) {
      id = this.rule.id;
    }
    if (this.sheetMode === SheetMode.Edit) {
      this.closeSheet(false, SheetMode.View);
    }
    let link = [];
    this.allLinksForAutocomplete = { links: [] };
    this.sheetMode = DetailedSheetUtils.toggle(id, data.entity.id, this.sheetMode, data.mode);

    // Default passed values while links are asychronously loaded
    this.rule = {
      id: data.entity.id,
      rule: data.entity.rule,
      controllerId: this.selectedController.id,
      controllerName: this.selectedController.name,
      links: [],
      allLinksForAutocomplete: { links: [] },
      oldControlActionId: data.entity.controlActionId,
      state: data.entity.state,
    };

    if (data.entity.controlActionId !== '0') {
      this.controlActionDataService.getControlActionById(this.projectId, data.entity.controlActionId)
        .then((value: ControlActionDTO) => {
          link.push(value);
        }).catch((error: HttpErrorResponse) => {
          if (error.message === EXPECTED_ENTITY_NOT_NULL) {
            // leave link array empty, but do nothing else
          } else {
            console.log(error);
            const notice = {
              name: 'Error loading data',
              id: data.entity.controllerId
            };
            link.push(notice);
          }
        });
    }
    this.controlActionDataService.getAllControlActions(this.projectId, {})
      .then((value: ControlActionDTO[]) => {
        this.allLinksForAutocomplete.links = value;

        this.rule = {
          id: data.entity.id,
          rule: data.entity.rule,
          controllerId: this.selectedController.id,
          controllerName: this.selectedController.name,
          links: link,
          allLinksForAutocomplete: this.allLinksForAutocomplete,
          oldControlActionId: data.entity.controlActionId,
          state: data.entity.state,
        };
      }).catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: DETAILEDSHEET_FAILED_LOADING_CHIPS, detail: response.message });
      });
    this.autoCompleteWords = [];

    Promise.all([
      this.processModelDataService.getAllByControllerId(this.projectId, this.selectedController.id),
      this.processVariableDataService.getAllProcessVariables(this.projectId)
    ])
      .then((value: [ProcessModelResponseDTO[], ProcessVariableResponseDTO[]]) => {
        if (!value[0] || !value[1]) {
          // TODO: fehlermeldung
          return;
        }
        for (let vari of value[1]) {
          value[0].forEach((pm: ProcessModelResponseDTO) => {
            if (vari.process_models.findIndex((pmId: string) => pmId === pm.id) > -1) {
              this.autoCompleteWords.push(vari);
            }
          });
        }
      })
      .catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_LOADING, detail: response.message });
      });
  }

  editSheet(): void {
    this.sheetMode = SheetMode.Edit;
  }

  closeSheet(isUnlocked: boolean = false, sheetMode: SheetMode = SheetMode.Closed): void {
    if (!isUnlocked && this.sheetMode === SheetMode.Edit) {
      const ruleUnlockDTO: UnlockRequestDTO = {
        id: this.rule.id,
        parentId: this.rule.controllerId,
        entityName: RULE
      };
      this.lockService.unlockEntity(this.projectId, ruleUnlockDTO).then(() => {
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
    this.filterService.ClearSelectionEmitter.emit(true);
    this.subscriptions.forEach((subscription: Subscription) => subscription.unsubscribe());
  }

  private initColumns(): void {
    this.columns = [
      {
        key: 'select',
        title: 'Select',
        type: ColumnType.Checkbox,
        style: { 'width': '7%' }
      }, {
        key: 'id',
        title: 'ID',
        type: ColumnType.Text,
        style: { 'width': '3%' }
      }, {
        key: 'controlActionName',
        title: 'Control-Action-Name',
        type: ColumnType.Text,
        style: { 'width': '15%' }
      }, {
        key: 'rule',
        title: 'Rule',
        type: ColumnType.Text,
        style: { 'width': '47%' }
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
        style: {width: '6%'}
      }, {
        key: 'lastEdited',
        title: 'Last-Edited',
        type: ColumnType.Date_Time,
        style: { 'width': '9%' }
      },

      {
        key: 'edit',
        title: 'Edit',
        type: ColumnType.Button,
        style: { 'width': '3%' }
      }, {
        key: 'show',
        title: 'Show',
        type: ColumnType.Button,
        style: { 'width': '3%' }
      }, {
        key: 'graph',
        title: 'Graph',
        type: ColumnType.Button,
        style: { 'width': '3%' }
      }
    ];
  }

}
