import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, OnDestroy, OnInit, Output } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MessageService } from 'primeng/api';
import { LockService } from '../../services/dataServices/lock.service';
import { DetailedSheetComponent, SheetMode } from './../../common/detailed-sheet/detailed-sheet.component';
import { DetailedSheetUtils } from './../../common/detailed-sheet/utils/detailed-sheet-utils';
import { Category } from './../../common/entities/categories-enum';
import { DetailedField, FieldType } from './../../common/entities/detailed-field';
import { ColumnType, TableColumn } from './../../common/entities/table-column';
import { AuthService } from './../../services/auth.service';
import { ControlActionDataService } from './../../services/dataServices/control-structure/control-action-data.service';
import { ControllerDataService } from './../../services/dataServices/control-structure/controller-data.service';
import { HazardDataService } from './../../services/dataServices/hazard-data.service';
import { SubHazardDataService } from './../../services/dataServices/sub-hazard-data.service';
import { UcaDataService } from './../../services/dataServices/uca-data.service';
import { FilterService } from './../../services/filter-service/filter.service';
import { WebsocketService } from './../../services/websocket.service';
import { UserDataService, UserDisplayRequestDTO } from 'src/app/services/dataServices/user-data.service';
import {
  ControlActionDTO, ControllerConstraintResponseDTO, ControllerResponseDTO, HazardResponseDTO,
  ImplementationConstraintResponseDTO, LossScenarioResponseDTO,
  PageRequest,
  SubHazardResponseDTO, Type, UcaResponseDTO,
  UnlockRequestDTO, UNSAFE_CONTROL_ACTION
} from './../../types/local-types';
import { Hotkeys } from 'src/app/hotkeys/hotkeys.service';
import { IncompleteEntitiesService, incompleteEntityDTO } from 'src/app/services/dataServices/incomplete-entities-data.service';
import { DETAILEDSHEET_EXPECTED_NAME_NOT_NULL, DELETED_USER, DETAILEDSHEET_FAILED_LOADING_CHIPS, ENTITY_FAILED_LOADING, CHANGED_STATE_FAILED, DETAILEDSHEET_FAILED_CREATION, DETAILEDSHEET_SUCCESSFUL_CREATION, DETAILEDSHEET_FAILED_EDIT, DETAILEDSHEET_SUCCESSFUL_EDIT, ENTITY_SUCCESSFUL_DELETE, ENTITY_FAILED_DELETE, NO_USER } from 'src/app/globals';
import { Subscription } from 'rxjs';
import { ControllerConstraintDataService } from '../../services/dataServices/controller-constraint-data.service';
import { LossScenarioDataService } from '../../services/dataServices/loss-scenario.service';
import { ImplementationConstraintDataService } from '../../services/dataServices/implementation-constraint-data.service';
import { SelectedEntity } from '../dependent-element-tree/dependent-elements-types.component';

interface UcaLinksForAutoComplete {
  haz_links: HazardResponseDTO[];
  subHaz_links: SubHazardResponseDTO[];
}

interface UcaForDetailedSheet {
  id: string;
  name: string;
  description: string;
  parentId: string;
  parentName: string;
  category: any[];
  haz_links: HazardResponseDTO[];
  subHaz_links: SubHazardResponseDTO[];
  allLinksForAutocomplete: UcaLinksForAutoComplete;
  state: string;
}

@Component({
  selector: 'app-uca-table',
  templateUrl: './uca-table.component.html',
  styleUrls: ['./uca-table.component.css']
})
// TODO what happens if no controlAction is created or no categrory has data ?

export class UcaTableComponent implements OnInit, OnDestroy {

  ds: DetailedSheetComponent<any>;
  SheetMode: typeof SheetMode = SheetMode;
  // seperate attributes which are for detailed sheet, table or nav table
  private projectId: string;
  ucaArray: UcaResponseDTO[] = [];
  columns: TableColumn[] = [];
  detailedColumns: TableColumn[] = [];
  uca: UcaForDetailedSheet;
  fields: DetailedField[];
  title: string = DetailedSheetUtils.generateSheetTitle(UNSAFE_CONTROL_ACTION);
  navigationTableDefinitions = [];
  selectedControlAction: ControlActionDTO;
  selectedCategory;
  Category = Category;
  FilterService = null;
  advancedFilterSubscription = null;
  additionalDeleteMessage = 'By deleting an Unsafe-Control-Action (UCA) the corresponding Controller-Constraint (CC) will be deleted too!';
  altUcaParentId: string = '';
  hazLinks = [];
  // listSubHaz beinhaltet alle subHazard, die zu den ausgewählten Hazards passen
  listSubHaz: SubHazardResponseDTO[] = [];
  selectedChips: SubHazardResponseDTO[] = [];
  // TODO Create Type of Categories
  controllerId: string = null;
  selectUCAEntity: SelectedEntity<Object>;

  @Output() selectEntityMessage = new EventEmitter<SelectedEntity<Object>>();

  private categories = [

    { name: Category.NOT_PROVIDED.toString(), type: Category.NOT_PROVIDED, count: 0 },
    { name: Category.PROVIDED.toString(), type: Category.PROVIDED, count: 0 },
    { name: Category.TOO_EARLY_TOO_LATE, type: Category.TOO_EARLY_TOO_LATE, count: 0 },
    { name: Category.STOPPED_TOO_SOON_OR_APPLIED_TOO_LONG.toString(), type: Category.STOPPED_TOO_SOON_OR_APPLIED_TOO_LONG, count: 0 },
    { name: Category.ALL.toString(), type: Category.ALL, count: 0 }];
  origNavData = [];
  allControllers = [];
  sheetMode: SheetMode = SheetMode.Closed;
  private allLinksForAutocomplete: UcaLinksForAutoComplete;
  private OwnChange: boolean = false;
  disableSaveButton: boolean = false;
  newLink: boolean = false;
  altHaz_links: HazardResponseDTO[] = [];
  altSubHaz_links: SubHazardResponseDTO[] = [];
  subscriptions: Subscription[] = [];
  constructor(
    private readonly controllerDataService: ControllerDataService,
    private readonly controlActionDataService: ControlActionDataService,
    private readonly ucaDataService: UcaDataService,
    private readonly hazardDataService: HazardDataService,
    private readonly subHazardDataService: SubHazardDataService,
    private readonly route: ActivatedRoute,
    private readonly filterService: FilterService,
    private readonly messageService: MessageService,
    private readonly wsService: WebsocketService,
    private readonly authService: AuthService,
    private readonly lockService: LockService,
    private readonly userDataService: UserDataService,
    private readonly incompleteEntityService: IncompleteEntitiesService,
    private readonly controllerConstraintDataService: ControllerConstraintDataService,
    private readonly lossScenarioDataService: LossScenarioDataService,
    private readonly implementationConstraintDataService: ImplementationConstraintDataService,
    private readonly hotkeys: Hotkeys) {

    this.subscriptions.push(this.hotkeys.addShortcut({ keys: 'shift.control.n', description: 'Create UCA' }).subscribe(() => {
      this.createUCA();
    }));
    // Felder die im Detailsheet angezeigt werden
    this.fields = [
      { title: 'Control Action', key: 'parentName', type: FieldType.Text, readonly: true },
      { title: 'UCA Category', key: 'category', type: FieldType.Text, readonly: true },
      { title: 'ID', key: 'id', type: FieldType.Text, readonly: true },
      { title: 'Name', key: 'name', type: FieldType.Text, readonly: false },
      { title: 'Description', key: 'description', type: FieldType.Text_Variable, readonly: false, minRows: 3, maxRows: 10 },
      {
        title: 'Linked Hazards', key: 'haz_links', type: FieldType.Chips, readonly: false, listKey: 'id', displayShortName: 'H-',
        shortcutButton: { title: 'Go to Hazards', routerLink: (): string => 'project/' + this.projectId + '/system-level-hazards/' }
      },
      {
        title: 'Linked Sub-Hazards', key: 'subHaz_links', type: FieldType.Chips, readonly: false, listKey: 'id', displayShortName: 'SH -',
        shortcutButton: { title: 'Go to Subhazards', routerLink: (): string => 'project/' + this.projectId + '/refine-hazards/' }
      },
      { title: 'State', key: 'state', type: FieldType.StateButtonGroup, readonly: false },
    ];
    this.deleteUCA = this.deleteUCA.bind(this);
    this.route.parent.params.subscribe((value: any) => {
      this.projectId = value.id;
      this.initColumns();
      this.loadData();
    });

    // Websocket implementation
    this.authService.getProjectToken().then((token: string) => {
      this.wsService.connect('subscribe', UNSAFE_CONTROL_ACTION, token, (data) => {
        this.loadData();
        if (this.OwnChange === false) {
          this.messageService.add({ severity: 'info', summary: 'User ' + data.displayName + ' updated the data!' });
        }
      });
    }).catch((err: HttpErrorResponse) => {
      console.log(err);
    });
  }

  ngOnInit(): void {
    this.advancedFilterSubscription = this.filterService.AdvancedFilterEmitter.subscribe(event => this.applyAdvancedFilter(event));
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
          this.selectedCategory = undefined;
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
            this.ucaArray = [];
          }
        }
      } else {
        this.selectedControlAction = undefined;
        this.selectedCategory = undefined;
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
      this.navigationTableDefinitions[0].columnHeaderName = 'Control Action';
      if (this.selectedCategory !== undefined) {
        this.navigationTableDefinitions[1].selectedElement = this.selectedCategory;
      }
    }
  }

  // // this is the handler/callback function for the clickedNavigationEmitter defined in the navigationTable
  // // if clickedNavigationEmitter is fired onClickedNavigation will be called
  // // clickedNavigationEmitter will pass its parameters to onClickedNavigation(tupleOfElemAndTableNumber)
  onClickedNavigation(tupleOfElemAndTableNumber): void {
    this.filterService.ClearSelectionEmitter.emit(true);
    if (tupleOfElemAndTableNumber[1] === 0) {
      this.selectedControlAction = tupleOfElemAndTableNumber[0];
      this.selectedCategory = this.categories[0];
    } else if (tupleOfElemAndTableNumber[1] === 1) {
      this.selectedCategory = tupleOfElemAndTableNumber[0];
    }
    this.loadData();
  }

  async deleteUCA($event: any[]): Promise<void> {
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
        parentId: this.selectedControlAction.id
      });
      return await this.ucaDataService.deleteUca(this.projectId, elem.parentId, elem.id);
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

  createUCA(): void {

    this.sheetMode = SheetMode.New;
    this.uca = {
      id: '', description: '', name: '',
      parentId: this.selectedControlAction.id, parentName: this.selectedControlAction.name,
      category: this.selectedCategory.type,
      haz_links: [],
      subHaz_links: [],
      allLinksForAutocomplete: { haz_links: [], subHaz_links: [] },
      state: '',
    };

    this.allLinksForAutocomplete = { haz_links: [], subHaz_links: [] };

    this.hazardDataService.getAllHazards(this.projectId, {}).then(value => {
      this.allLinksForAutocomplete.haz_links = value;
      this.allLinksForAutocomplete.haz_links.forEach(element => {
        this.subHazardDataService.getAllSubHazards(this.projectId, element.id, {}).then(value => {
          this.allLinksForAutocomplete.subHaz_links = this.allLinksForAutocomplete.subHaz_links.concat(value);
        });
        this.uca = {
          id: '', description: '', name: '',
          parentId: this.selectedControlAction.id, parentName: this.selectedControlAction.name,
          category: this.selectedCategory.type,
          haz_links: [],
          subHaz_links: [],
          allLinksForAutocomplete: this.allLinksForAutocomplete,
          state: '',
        };
      });
    });
  }

  saveUCA(uca): void {
    this.filterService.ClearSelectionEmitter.emit(true);
    this.OwnChange = true;
    this.disableSaveButton = true;
    if (!uca.ent.name || uca.ent.name.trim() === '') {
      this.messageService.add({ severity: 'warn', summary: DETAILEDSHEET_EXPECTED_NAME_NOT_NULL })
      this.disableSaveButton = false;
      return;
    }
    const page: PageRequest = { from: 0, amount: 100 };
    if (uca.mode === SheetMode.EditWithLock) {
      let altHazLinksBack: HazardResponseDTO[] = [];
      let altSubHazLinksBack: SubHazardResponseDTO[] = [];
      const responsibilityEntity = {
        projectId: this.projectId, description: uca.ent.description, name: uca.ent.name,
        category: uca.ent.category,
      };
      Promise.all([
        this.hazardDataService.getHazardsByUCAId(this.projectId, uca.ent.parentId, uca.ent.id, page),
        this.subHazardDataService.getHazardsByUCAId(this.projectId, uca.ent.parentId, uca.ent.id, page)
      ]).then((value: [HazardResponseDTO[], SubHazardResponseDTO[]]) => {
        if (value[0] !== null) {
          altHazLinksBack = value[0];
        }
        if (value[1] !== null) {
          altSubHazLinksBack = value[1];
        }

        const promises: Promise<boolean>[] = [];
        const linkPromises: Promise<boolean>[] = [];
        for (const altHazLink of altHazLinksBack) {
          linkPromises.push(this.ucaDataService.deleteUCAHazLink(this.projectId, uca.ent.parentId, uca.ent.id, altHazLink.id, {}));
        }
        for (const altSubHazLink of altSubHazLinksBack) {
          linkPromises.push(this.ucaDataService.deleteUCASubHazLink(this.projectId, uca.ent.parentId, uca.ent.id, altSubHazLink.parentId, altSubHazLink.id));
        }
        Promise.all(linkPromises)
          .then(() => {
            promises.push(this.ucaDataService.alterUca(this.projectId, uca.ent.parentId, uca.ent.id, responsibilityEntity));
            if (uca.addedMap.has('haz_links')) {
              for (const newHazLink of this.uca.haz_links) {
                promises.push(this.ucaDataService.createUCAHazLink(this.projectId, uca.ent.parentId, uca.ent.id, newHazLink.id, {}));
              }
            }
            if (uca.addedMap.has('subHaz_links')) {
              for (const newSubHazLink of this.uca.subHaz_links) {
                promises.push(this.ucaDataService.createUCASubHazLink(this.projectId, uca.ent.parentId, uca.ent.id, newSubHazLink.parentId, newSubHazLink.id, {}));

              }
            }

            Promise.all(promises)
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
          }).catch((response: HttpErrorResponse) => {
            this.messageService.add({ severity: 'error', summary: DETAILEDSHEET_FAILED_LOADING_CHIPS, detail: response.message });
          }).finally(() => {
            this.OwnChange = false;
            this.disableSaveButton = false;
            this.loadData();
          });

      });

    } else if (uca.mode === SheetMode.New) {
      if (uca.addedMap.has('haz_links')) {
        this.newLink = true;

        this.altHaz_links = this.uca.haz_links;

        if (uca.addedMap.has('subHaz_links')) {
          this.altSubHaz_links = this.uca.subHaz_links;

        }

        this.altUcaParentId = this.uca.parentId;
      }
      const responsibilityEntity = {
        projectId: this.projectId, description: uca.ent.description, name: uca.ent.name, category: uca.ent.category,
        state: uca.ent.state,
      };
      this.ucaDataService.createUCA(this.projectId, this.selectedControlAction.id, responsibilityEntity)
        .then(() => {
          this.messageService.add({ severity: 'success', summary: DETAILEDSHEET_SUCCESSFUL_CREATION });
          this.closeSheet(true);
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: DETAILEDSHEET_FAILED_CREATION, detail: response.message });
        }).finally(() => {
          this.listSubHaz = [];
          this.OwnChange = false;
          this.disableSaveButton = false;
          this.loadData();
        });
    }
  }

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
        title: 'ID',
        type: ColumnType.Text,
        style: { 'width': '5%' }
      }, {
        key: 'name',
        title: 'UCA-Name',
        type: ColumnType.Text,
        style: { 'width': '56%' }
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
        style: { width: '8%' },
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

  changeEntity(event: { list: UcaResponseDTO[], state: string }) {
    event.list.forEach((element: UcaResponseDTO) => {
      let incomplete: incompleteEntityDTO = { entityName: 'unsafe_control_action', parentId: this.selectedControlAction.id, state: event.state };

      this.incompleteEntityService.updateState(this.projectId, element.id, incomplete).then(value => {
        this.loadData();
      }).catch((reason: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: CHANGED_STATE_FAILED, detail: reason.message });
      })
    });
  }

  getSelectedEntities($event: any): void {
    this.selectUCAEntity = new SelectedEntity<Object>($event, this.title, this.projectId,
      false,
      false,
      true);
  }

  private loadData(): void {

    if (this.newLink) {
      this.saveLinks();
    }

    if (this.selectedControlAction !== undefined && this.selectedCategory !== undefined) {
      let userRequest: UserDisplayRequestDTO = new UserDisplayRequestDTO;
      let userId: string[] = [];
      let valueIndex: string[] = [];
      if (this.selectedCategory.type === Category.ALL) {
        this.ucaDataService.getAllUcasByControlActionId(this.projectId, this.selectedControlAction.id, {})
          .then(value => {
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
            this.ucaArray = value;
          }
          }).catch((response: HttpErrorResponse) => {
            this.messageService.add({ severity: 'error', summary: 'Loading UCAs of type all failed', detail: response.message });
          });
      } else {

        const ucaFilter = {

          filter: {
            type: Type[Type.ANY_LIKE],
            fieldName: 'TODO_IS_IGNORED',
            fieldValue: this.selectedCategory.type
          },
          orderBy: 'nae',
          orderDirection: 'IgnoreProvided',
          from: 0,
          amount: 100
        };
        this.ucaDataService.getUnsafeControlActionsByControlActionIdAndType(this.projectId, this.selectedControlAction.id, ucaFilter)
          .then((value: UcaResponseDTO[]) => {
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
            this.ucaArray = value;
         } }).catch((response: HttpErrorResponse) => {
            this.messageService.add({ severity: 'error', summary: 'Loading UCA of specific type failed', detail: response.message });
          });
        this.loadCategoryCount();
      }

    } else if (this.selectedControlAction !== undefined) {

      this.navigationTableDefinitions[1] = {
        dataSource: this.categories,
        style: { 'width': '30%', 'min-width': '200px' },
        columnHeaderName: 'Type',
        allowLineBreak: true
      };
      Promise.all([

        this.ucaDataService.getUnsafeControlActionsCountByControlActionIdAndType(this.projectId, this.selectedControlAction.id, this.generateCategoryFilter(Category.NOT_PROVIDED)),
        this.ucaDataService.getUnsafeControlActionsCountByControlActionIdAndType(this.projectId, this.selectedControlAction.id, this.generateCategoryFilter(Category.PROVIDED)),
        this.ucaDataService.getUnsafeControlActionsCountByControlActionIdAndType(this.projectId, this.selectedControlAction.id,
          this.generateCategoryFilter(Category.TOO_EARLY_TOO_LATE)),
        this.ucaDataService.getUnsafeControlActionsCountByControlActionIdAndType(this.projectId, this.selectedControlAction.id,
          this.generateCategoryFilter(Category.STOPPED_TOO_SOON_OR_APPLIED_TOO_LONG)),
        this.ucaDataService.getAllUcasByControlActionId(this.projectId, this.selectedControlAction.id, {})
      ]).then((value: [number, number, number, number, UcaResponseDTO[]]) => {

        this.categories[0].name = this.categories[0].type + ' (' + value[0] + ')';
        this.categories[1].name = this.categories[1].type + ' (' + value[1] + ')';
        this.categories[2].name = this.categories[2].type + ' (' + value[2] + ')';
        this.categories[3].name = this.categories[3].type + ' (' + value[3] + ')';
        this.categories[4].name = this.categories[4].type + ' (' + value[4].length + ')';
        this.navigationTableDefinitions[1] = {
          dataSource: this.categories,
          style: { 'width': '30%', 'min-width': '200px' },
          columnHeaderName: 'Type',
          allowLineBreak: true
        };
        this.selectedCategory = this.categories[0];
        this.loadData();
      }).catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: 'Updating UCAs category count failed', detail: response.message });
      });

    } else {

      this.controlActionDataService.getAllControlActions(this.projectId, {})
        .then((value: ControlActionDTO[]) => {
          this.navigationTableDefinitions = [];
          this.origNavData = [];
          this.origNavData = value;
          this.navigationTableDefinitions[0] = {
            dataSource: value,
            style: { 'width': '70%', 'min-width': '150px' },
            columnHeaderName: 'Control Action'
          };
          if (value !== null && value.length > 0) {
            this.selectedControlAction = value[0];
            this.loadData();
          } else {
            this.navigationTableDefinitions[0] = { dataSource: value, style: { width: '100%' }, columnHeaderName: 'No Control Actions found' };
            this.messageService.add({ severity: 'warn', life: 10000, summary: 'Create a Control-Action to be able to add Unsafe-Control-Actions!' });
          }
          // Create the shortcut button to the Control Action table
          this.navigationTableDefinitions[0].columnHeaderButton = { title: 'Go to Control Actions', routerLink: 'project/' + this.projectId + '/control-action/' };
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_LOADING, detail: response.message });
        });
    }

    // TODO NEEDED FOR THE ADVANCED FILTER
    this.controllerDataService.getAllControllers(this.projectId, {}).then((value) => {
      this.allControllers = value;
    }).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_LOADING, detail: response.message });
    });
  }

  generateCategoryFilter(category) {
    return {
      filter: {
        type: Type[Type.ANY_LIKE],
        fieldName: 'TODOISIGNORED',
        fieldValue: category
      },
      orderBy: 'nae',
      orderDirection: 'IgnoreProvided',
      from: 0,
      amount: 100
    };
  }

  loadCategoryCount(): void {
    Promise.all([
      this.ucaDataService.getUnsafeControlActionsCountByControlActionIdAndType(this.projectId, this.selectedControlAction.id, this.generateCategoryFilter(Category.NOT_PROVIDED)),
      this.ucaDataService.getUnsafeControlActionsCountByControlActionIdAndType(this.projectId, this.selectedControlAction.id, this.generateCategoryFilter(Category.PROVIDED)),

      this.ucaDataService.getUnsafeControlActionsCountByControlActionIdAndType(this.projectId, this.selectedControlAction.id,
        this.generateCategoryFilter(Category.TOO_EARLY_TOO_LATE)),
      this.ucaDataService.getUnsafeControlActionsCountByControlActionIdAndType(this.projectId, this.selectedControlAction.id,
        this.generateCategoryFilter(Category.STOPPED_TOO_SOON_OR_APPLIED_TOO_LONG)),
      this.ucaDataService.getAllUcasByControlActionId(this.projectId, this.selectedControlAction.id, {})
    ]).then((value: [number, number, number, number, UcaResponseDTO[]]) => {

      this.categories[0].name = this.categories[0].type + ' (' + value[0] + ')';
      this.categories[1].name = this.categories[1].type + ' (' + value[1] + ')';
      this.categories[2].name = this.categories[2].type + ' (' + value[2] + ')';
      this.categories[3].name = this.categories[3].type + ' (' + value[3] + ')';
      this.categories[4].name = this.categories[4].type + ' (' + value[4].length + ')';
      this.navigationTableDefinitions[1] = {
        dataSource: this.categories,
        style: { 'width': '30%', 'min-width': '200px' },
        columnHeaderName: 'Type',
        allowLineBreak: true
      };
    }).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: 'Loading UCA count by type failed', detail: response.message });
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
    if (this.uca) {
      id = this.uca.id;
    }

    if (this.sheetMode === SheetMode.Edit) {
      this.closeSheet(false, SheetMode.View);
    }

    this.sheetMode = DetailedSheetUtils.toggle(id, data.entity.id, this.sheetMode, data.mode);

    let hazLinks: HazardResponseDTO[] = [];
    let subHazLinks: SubHazardResponseDTO[] = [];
    const page: PageRequest = { from: 0, amount: 100 };
    this.allLinksForAutocomplete = { haz_links: [], subHaz_links: [] };

    // Default passed values while links are asychronicaly loaded
    this.uca = {
      id: data.entity.id, description: data.entity.description, name: data.entity.name,
      parentId: this.selectedControlAction.id, parentName: this.selectedControlAction.name,
      category: data.entity.category,
      haz_links: [],
      subHaz_links: [],
      allLinksForAutocomplete: { haz_links: [], subHaz_links: [] },
      state: data.entity.state,
    };

    Promise.all([
      this.hazardDataService.getAllHazards(this.projectId, {}),
      this.hazardDataService.getHazardsByUCAId(this.projectId, data.entity.parentId, data.entity.id, page),
      this.subHazardDataService.getHazardsByUCAId(this.projectId, data.entity.parentId, data.entity.id, page)

    ]).then((value: [HazardResponseDTO[], HazardResponseDTO[], SubHazardResponseDTO[]]) => {

      this.allLinksForAutocomplete.haz_links = value[0];
      this.getAllSubHazard(value[1]);

      if (value[1] !== null) {
        hazLinks = value[1];
      }

      if (value[2] !== null) {
        subHazLinks = value[2];
      }

      // TODO adapt all Parameters which are passed to the Detailedsheet
      this.uca = {
        id: data.entity.id, description: data.entity.description, name: data.entity.name,
        parentId: this.selectedControlAction.id, parentName: this.selectedControlAction.name,
        category: data.entity.category,
        haz_links: hazLinks,
        subHaz_links: subHazLinks,
        allLinksForAutocomplete: this.allLinksForAutocomplete,
        state: data.entity.state,
      };
    }).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: DETAILEDSHEET_FAILED_LOADING_CHIPS, detail: response.message });
    });
  }

  closeSheet(isUnlocked: boolean = false, sheetMode: SheetMode = SheetMode.Closed): void {
    this.listSubHaz = [];
    if (!isUnlocked && this.sheetMode === SheetMode.Edit) {
      const unlockDTO: UnlockRequestDTO = { id: this.uca.id, entityName: this.title.toLowerCase(), parentId: this.selectedControlAction.id };
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
    if (this.advancedFilterSubscription) {
      this.advancedFilterSubscription.unsubscribe();
    }
    this.subscriptions.forEach((subscription: Subscription) => subscription.unsubscribe());
  }

  // Method saves the new Links for a new UCA. Is a workaround as long as the  entity is not changed
  // in Backend so that the links can be created at the same time the new UCA is created
  saveLinks() {
    this.newLink = false;
    const promises = [];
    let altUcaId = '';
    this.ucaDataService.getAllUcasByControlActionId(this.projectId, this.selectedControlAction.id, {}).then((value: UcaResponseDTO[]) => {
      altUcaId = value.pop().id.toString();

      for (const newHazLink of this.altHaz_links) {
        promises.push(this.ucaDataService.createUCAHazLink(this.projectId, this.altUcaParentId, altUcaId, newHazLink.id, {}));
      }

      for (const newSubHazLink of this.altSubHaz_links) {

        promises.push(this.ucaDataService.createUCASubHazLink(this.projectId, this.altUcaParentId, altUcaId, newSubHazLink.parentId, newSubHazLink.id, {}));
      }

      this.altHaz_links = [];
      this.altSubHaz_links = [];
    });

  }

  // Method is used to get all Subhazards for a HazardResponseDTO[]
  getAllSubHazard(hazards: HazardResponseDTO[]): void {
    this.listSubHaz = [];

    hazards.forEach(element => {
      this.subHazardDataService.getAllSubHazards(this.projectId, element.id, {}).then(value => {
        if (value !== null) {
          this.listSubHaz = this.listSubHaz.concat(value);
          this.allLinksForAutocomplete.subHaz_links = this.listSubHaz;
        }
      });
    });

  }

  // Method loads the list for this.allLinksForAutoComplete.subHaz_Links, so that the subHaz_Links List matches the Haz_links List
  selected(uca) {

    let selectedChip: SubHazardResponseDTO = uca.pop();
    if (uca.includes('haz_links')) {

      this.subHazardDataService.getAllSubHazards(this.projectId, selectedChip.id, {}).then((value: SubHazardResponseDTO[]) => {

        if (value !== null) {
          this.listSubHaz = this.listSubHaz.concat(value);

          this.allLinksForAutocomplete.subHaz_links = this.listSubHaz;
          this.uca = {
            id: this.uca.id, description: this.uca.description, name: this.uca.name,
            parentId: this.selectedControlAction.id, parentName: this.selectedControlAction.name,
            category: this.selectedCategory.type,
            haz_links: this.uca.haz_links,
            subHaz_links: this.uca.subHaz_links,
            allLinksForAutocomplete: this.allLinksForAutocomplete,
            state: this.uca.state,
          };
        } else {
          this.uca = {
            id: this.uca.id, description: this.uca.description, name: this.uca.name,
            parentId: this.selectedControlAction.id, parentName: this.selectedControlAction.name,
            category: this.selectedCategory.type,
            haz_links: this.uca.haz_links,
            subHaz_links: this.uca.subHaz_links,
            allLinksForAutocomplete: this.allLinksForAutocomplete,
            state: this.uca.state,
          };
        }

      });
    } else {

      this.uca = {
        id: this.uca.id, description: this.uca.description, name: this.uca.name,
        parentId: this.selectedControlAction.id, parentName: this.selectedControlAction.name,
        category: this.selectedCategory.type,
        haz_links: this.uca.haz_links,
        subHaz_links: this.uca.subHaz_links,
        allLinksForAutocomplete: this.allLinksForAutocomplete,
        state: this.uca.state,
      };

    }
  }

  // Method reacts to the removal of the Chips, so that if a hazard is removed the related subHazards are also removed
  remove(chip) {

    if (chip.includes('haz_links')) {

      let subHaz: SubHazardResponseDTO[] = [];
      let subHazLinks: SubHazardResponseDTO[] = [];
      let selectedChip: SubHazardResponseDTO = chip.pop();

      this.uca.subHaz_links.forEach(element => {
        if (element.parentId !== selectedChip.id) {
          subHaz.push(element);
        }
      });
      this.uca.allLinksForAutocomplete.subHaz_links.forEach(element => {
        if (element.parentId !== selectedChip.id) {
          subHazLinks.push(element);
        }
      });

      this.listSubHaz = subHazLinks;
      this.allLinksForAutocomplete.subHaz_links = subHazLinks;

      this.uca = {
        id: this.uca.id, description: this.uca.description, name: this.uca.name,
        parentId: this.selectedControlAction.id, parentName: this.selectedControlAction.name,
        category: this.selectedCategory.type,
        haz_links: this.uca.haz_links,
        subHaz_links: subHaz,
        allLinksForAutocomplete: this.allLinksForAutocomplete,
        state: this.uca.state,
      };
    }
  }

}
