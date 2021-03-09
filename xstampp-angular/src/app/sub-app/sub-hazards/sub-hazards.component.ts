import { AuthService } from './../../services/auth.service';
import { WebsocketService } from './../../services/websocket.service';
import { DetailedField } from './../../common/entities/detailed-field';
import { SubSystemConstraintResponseDTO, SubHazardResponseDTO, SUB_HAZARD, UnlockRequestDTO, SUB_SYSTEM_CONSTRAINT, HazardResponseDTO, UcaResponseDTO } from './../../types/local-types';
import { SubhazardExtendedDataService } from './../../services/componentUtilsServices/subhazard-extended-data.service';
import { DetailedSheetUtils } from './../../common/detailed-sheet/utils/detailed-sheet-utils';
import { SystemLevelSafetyConstraintDataService } from '../../services/dataServices/system-level-safety-constraint-data.service';
import { SubSystemConstraintDataService } from '../../services/dataServices/sub-system-constraint-data.service';
import { SubHazardDataService } from '../../services/dataServices/sub-hazard-data.service';
import { HazardDataService } from '../../services/dataServices/hazard-data.service';
import { SystemConstraintResponseDTO } from '../../types/local-types';
import { ActivatedRoute } from '@angular/router';
import { Component, OnDestroy, ChangeDetectorRef, Output, EventEmitter } from '@angular/core';
import { ColumnType, TableColumn } from '../../common/entities/table-column';
import { FilterService } from '../../services/filter-service/filter.service';
import { FieldType } from '../../common/entities/detailed-field';
import { SheetMode } from '../../common/detailed-sheet/detailed-sheet.component';
import { MessageService } from 'primeng/api';
import { LockResponse, LockService } from '../../services/dataServices/lock.service';
import { HttpErrorResponse } from '@angular/common/http';
import { UserDataService, UserDisplayRequestDTO } from 'src/app/services/dataServices/user-data.service';
import { UcaDataService } from 'src/app/services/dataServices/uca-data.service';
import { Hotkeys } from 'src/app/hotkeys/hotkeys.service';
import { Router } from '@angular/router';
import { incompleteEntityDTO, IncompleteEntitiesService } from 'src/app/services/dataServices/incomplete-entities-data.service';
import { DETAILEDSHEET_EXPECTED_NAME_NOT_NULL, ENTITY_FAILED_LOADING, CHANGED_STATE_FAILED, DELETED_USER, DETAILEDSHEET_FAILED_CREATION, DETAILEDSHEET_SUCCESSFUL_CREATION, DETAILEDSHEET_FAILED_EDIT, DETAILEDSHEET_SUCCESSFUL_EDIT, DETAILEDSHEET_FAILED_LOADING_CHIPS, ENTITY_FAILED_DELETE, ENTITY_SUCCESSFUL_DELETE, NO_USER } from 'src/app/globals';
import { Subscription } from 'rxjs';
import { SelectedEntity } from '../dependent-element-tree/dependent-elements-types.component';

interface SubHazardLinksForAutoComplete {
  sc_links: SystemConstraintResponseDTO[];
  uca_links: UcaResponseDTO[];
}

interface SubHazardForDetailedSheet {
  id: string;
  name: string;
  description: string;
  parentHazardId: string;
  parentHazardName: string;
  parentSubSafetyConstraintId: string;
  subSafetyConstraintId: string;
  subSafetyConstraintName: string;
  subConstraintEnt: SubSystemConstraintResponseDTO;
  subSafetyConstraintDisplayId: string;
  sc_links: SystemConstraintResponseDTO[];
  uca_links: UcaResponseDTO[];
  allLinksForAutocomplete: SubHazardLinksForAutoComplete;
  state: string;
}

interface SubHazardAndSubConstraintForTable extends SubHazardResponseDTO {
  subHazDisplayId?: string;
  subSafetyConstraintName?: string;
  subSafetyConstraintId?: string;
  parentSubSafetyConstraintId?: string;
  subConstraintEnt?: SubSystemConstraintResponseDTO;
  subSafetyConstraintDisplayId?: string;
}

@Component({
  selector: 'app-sub-hazards',
  templateUrl: './sub-hazards.component.html',
  styleUrls: ['./sub-hazards.component.css']
})
export class SubHazardsComponent implements OnDestroy {

  SheetMode: typeof SheetMode = SheetMode;
  private projectId: string;
  public subHazards: SubHazardAndSubConstraintForTable[] = [];
  columns: TableColumn[] = [];
  subHazard: SubHazardForDetailedSheet;
  sheetMode: SheetMode = SheetMode.Closed;
  fields: DetailedField[];
  title: string = DetailedSheetUtils.generateSheetTitle(SUB_HAZARD);   // Title of detailed-sheet
  private allLinksForAutocomplete: SubHazardLinksForAutoComplete;
  selectedHazard;
  navigationTableDefinitions = [];
  mode;
  private OwnChange: boolean = false;
  disableSaveButton: boolean = false;
  subscriptions: Subscription[] = [];
  selectSubHazardEntity: SelectedEntity<Object>;

  @Output() selectSubHazardMessage: any = new EventEmitter<SelectedEntity<Object>>();

  onClickedNavigation(tupleOfElemAndTableNumber): void {
    this.filterService.ClearSelectionEmitter.emit(true);
    this.selectedHazard = tupleOfElemAndTableNumber[0];
    this.navigationTableDefinitions[0].selectedElement = this.selectedHazard;
    this.loadData();
  }

  constructor(
    private readonly route: ActivatedRoute,
    private readonly filterService: FilterService,
    private readonly messageService: MessageService,
    private readonly hazardDataService: HazardDataService,
    private readonly subHazardDataService: SubHazardDataService,
    private readonly subSystemConstraintDataService: SubSystemConstraintDataService,
    private readonly systemConstraintDataService: SystemLevelSafetyConstraintDataService,
    private readonly subHazardExtendeDataService: SubhazardExtendedDataService,
    private readonly cdr: ChangeDetectorRef,
    private readonly ucaDataService: UcaDataService,
    private readonly wsService: WebsocketService,
    private readonly authService: AuthService,
    private readonly lockService: LockService,
    private readonly hotkeys: Hotkeys,
    private readonly userDataService: UserDataService,
    private readonly incompleteEntityService: IncompleteEntitiesService,
    private readonly router: Router) {

    this.subscriptions.push(this.hotkeys.addShortcut({ keys: 'shift.control.n', description: 'Create Sub-Hazard' }).subscribe(() => {
      this.createSubHazard();
    }));
    this.fields = [
      { title: 'Sub-Hazard-ID', key: 'id', type: FieldType.Text, readonly: true },
      { title: 'Sub-Safety-Constraint-ID', key: 'subSafetyConstraintDisplayId', type: FieldType.Text, readonly: true },
      { title: 'Parent-Hazard-Name', key: 'parentHazardName', type: FieldType.Text, readonly: true },
      { title: 'Sub-Hazard-Name', key: 'name', type: FieldType.Text, readonly: false, maxRows: 10 },
      {
        title: 'Safety-Constraint of Sub-Safety-Constraint', key: 'sc_links', type: FieldType.Chips_Single, readonly: false, listKey: 'id', displayShortName: 'SC-',
        shortcutButton: { title: 'Go to System Constraints', routerLink: (): string => 'project/' + this.projectId + '/system-level-safety-constraints/' }
      },
      { title: 'Sub-Safety-Constraint-Name', key: 'subSafetyConstraintName', type: FieldType.Text, readonly: false, maxRows: 10 },
      {
        title: 'Linked UCA', key: 'uca_links', type: FieldType.Chips, readonly: true, listKey: 'id', displayShortName: 'UCA-',
        shortcutButton: { title: 'Go to UCAs', routerLink: (): string => 'project/' + this.projectId + '/uca-table/' }
      },
      { title: 'State', key: 'state', type: FieldType.StateButtonGroup, readonly: false },
    ];
    this.deleteSubHazards = this.deleteSubHazards.bind(this);
    this.route.parent.params.subscribe((value: any) => {
      this.projectId = value.id;
      this.initColumns();
      this.loadData();
    });

    // Websocket implementation
    this.authService.getProjectToken().then((token: string) => {
      this.wsService.connect('subscribe', SUB_HAZARD, token, (data) => {
        this.selectedHazard = undefined;
        this.loadData();
        if (this.OwnChange === false) {
          this.messageService.add({ severity: 'info', summary: 'User ' + data.displayName + ' updated the data!' });
        }
      });
      this.wsService.connect('subscribe', SUB_SYSTEM_CONSTRAINT, token, (data) => {
        this.loadData();
        if (this.OwnChange === false) {
          this.messageService.add({ severity: 'info', summary: 'User ' + data.displayName + ' updated the data!' });
        }
      });
    }).catch((err: HttpErrorResponse) => { console.log(err); });
  }

  getSelectedEntities($event: any): void {
    this.selectSubHazardEntity = new SelectedEntity<Object>($event, this.title, this.projectId,
      true,
      false,
      false);
  }

  async deleteSubHazards($event): Promise<void> {
    this.OwnChange = true;
    const list: any[] = $event;
    if (list.length === 0) {
      return;
    }
    /* collect all responses from failed promises */
    const failedResponses: Error[] = [];

    let deleteSafetyConstSuccessful: boolean;
    for (const elem of list) {
      this.OwnChange = true;
      deleteSafetyConstSuccessful = false;
      await this.subHazardDataService.getSubConstraintBySubHazardId(this.projectId, this.selectedHazard.id, elem.id)
        .then(async (value: SubSystemConstraintResponseDTO) => {
          // retrieve subSC SUCCESS
          if (value === null) {
            deleteSafetyConstSuccessful = true;
          } else {
            await this.lockService.lockEntity(this.projectId, {
              id: value.id,
              parentId: value.parentId,
              entityName: SUB_SYSTEM_CONSTRAINT.toLowerCase(),
              expirationTime: DetailedSheetUtils.calculateLockExpiration()
            }).then(async () => {
              // lock subSC SUCCESS
              await this.subSystemConstraintDataService.deleteSubSystemConstraint(this.projectId, value.parentId, value.id).then(() => {
                // delete subSC SUCCESS
                deleteSafetyConstSuccessful = true;
              }).catch((response: HttpErrorResponse) => {
                // delete subSC FAILED
                deleteSafetyConstSuccessful = false;
                failedResponses.push(response.error);
                this.messageService.add({
                  severity: 'error',
                  summary: 'Error on deleting sub hazard and linked sub safety constraint',
                  detail: 'Failed to delete sub safety constraint ' + value.parentId + '.' + value.id + ' Try again.'
                });
              });
            }).catch((lockResponse: HttpErrorResponse) => {
              // lock subSC FAILED
              deleteSafetyConstSuccessful = false;
              failedResponses.push(lockResponse);
              this.messageService.add({
                severity: 'error',
                summary: 'Error on deleting sub hazard and linked sub safety constraint',
                life: 10000,
                detail: 'Failed to delete sub safety constraint ' + value.parentId + '.' + value.id + ' Failed to get lock.'
              });
            });
          }

          // delete the subhazard
          if (deleteSafetyConstSuccessful) {
            await this.lockService.lockEntity(this.projectId, {
              id: elem.id,
              entityName: this.title.toLowerCase(),
              expirationTime: DetailedSheetUtils.calculateLockExpiration(),
              parentId: this.selectedHazard.id
            }).then(async () => {
              // lock subHazrd SUCCESS
              await this.subHazardDataService.deleteSubHazard(this.projectId, this.selectedHazard.id, elem.id).then(() => {
                // delete subHazard SUCCESS
                console.log('deletion complete');
              }).catch((response: HttpErrorResponse) => {
                // delete subHazard FAILED
                failedResponses.push(response.error);
                this.messageService.add({
                  severity: 'error',
                  summary: 'Error on deleting sub hazard and linked sub safety constraint',
                  detail: 'Failed to delete sub hazard ' + elem.subHazDisplayId + ' Try again.'
                });
              });
            }
            ).catch((lockResponse: HttpErrorResponse) => {
              // lock subHazard FAILED
              failedResponses.push(lockResponse);
              this.messageService.add({
                severity: 'error',
                summary: 'Error on deleting sub hazard and linked sub safety constraint',
                life: 10000,
                detail: 'Failed to delete sub hazard ' + elem.subHazDisplayId + ' Failed to get lock.'
              });
            }
            );
          }
        }).catch((response: HttpErrorResponse) => {
          // retrieve subSC FAILED
          failedResponses.push(response.error);
          deleteSafetyConstSuccessful = false;
          this.messageService.add({
            severity: 'error',
            summary: 'Error on deleting sub hazard and linked sub safety constraint',
            life: 10000,
            detail: 'Failed to retrieve sub safety constraint of sub hazard ' + elem.subHazDisplayId + ' Try again.'
          });
        }).finally(() => {
          this.OwnChange = false;
          this.loadData();
        });
    }
    if (failedResponses.length < 1) {
      this.messageService.add({ severity: 'success', summary: ENTITY_SUCCESSFUL_DELETE });
    }
    this.OwnChange = false;
  }

  createSubHazard(): void {
    this.sheetMode = SheetMode.New;
    this.allLinksForAutocomplete = { sc_links: [], uca_links: [] };
    // necesary to pass empty element first else old data will be loaded
    // bcs the request for allLinksForAutocomplete is asynchronous and takes time to load new data

    this.subHazard = {
      id: '', name: '', description: '',
      parentHazardId: this.selectedHazard.id, parentHazardName: this.selectedHazard.name,
      parentSubSafetyConstraintId: '',
      subSafetyConstraintId: '', subSafetyConstraintName: '', subConstraintEnt: null,
      subSafetyConstraintDisplayId: '',
      sc_links: [],
      uca_links: [],
      allLinksForAutocomplete: this.allLinksForAutocomplete,
      state: '',
    };

    this.systemConstraintDataService.getAllSafetyConstraints(this.projectId, {})
      .then((value: SystemConstraintResponseDTO[]) => {

        this.allLinksForAutocomplete.sc_links = value;
        this.subHazard = {
          id: '', name: '', description: '',
          parentHazardId: this.selectedHazard.id, parentHazardName: this.selectedHazard.name,
          parentSubSafetyConstraintId: '',
          subSafetyConstraintId: '', subSafetyConstraintName: '', subConstraintEnt: null,
          subSafetyConstraintDisplayId: '',
          sc_links: [],
          uca_links: [],
          allLinksForAutocomplete: this.allLinksForAutocomplete,
          state: '',
        };
      }).catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: DETAILEDSHEET_FAILED_LOADING_CHIPS, detail: response.message });
      });
  }

  saveSubHazard(subHazard): void {
    this.filterService.ClearSelectionEmitter.emit(true);
    this.OwnChange = true;
    this.disableSaveButton = true;

    if (!subHazard.ent.name || subHazard.ent.name.trim() === '') {
      this.messageService.add({ severity: 'warn', summary: DETAILEDSHEET_EXPECTED_NAME_NOT_NULL })
      this.disableSaveButton = false;
      return;
    }
    // check if only one of name or scLinks is misssing
    if ((subHazard.ent.subSafetyConstraintName === '' && subHazard.ent.sc_links.length !== 0)) {
      this.messageService.add({ severity: 'error', summary: 'Add Sub-Safety-Constraint name ' });
      this.disableSaveButton = false;
    } else if ((subHazard.ent.subSafetyConstraintName !== '' && subHazard.ent.sc_links.length === 0)) {
      this.messageService.add({ severity: 'error', summary: 'Add Safety-Constraint link ' });
      this.disableSaveButton = false;
    } else if (subHazard.mode === SheetMode.EditWithLock) {

      this.subHazardExtendeDataService.editSubHazardAndSubSysCon(this.projectId, subHazard)
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

    } else if (subHazard.mode === SheetMode.New) {

      this.subHazardExtendeDataService.createSubHazardAndSubSysCon(this.projectId, subHazard, this.selectedHazard)
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

  // Optional set lastTimeEdited
  private loadData(): void {
    // load SubHazard and SubSystemConstraint into table if Hazard is selected
    let userRequest: UserDisplayRequestDTO = new UserDisplayRequestDTO;
    let userId: string[] = [];
    let valueIndex: string[] = [];
    if (typeof this.selectedHazard !== 'undefined') {
      this.subHazardDataService.getAllSubHazards(this.projectId, this.selectedHazard.id, {})
        .then((value: SubHazardResponseDTO[]) => {
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
          this.subHazards = value;
          for (const subHazard of this.subHazards) {
            subHazard.subHazDisplayId = subHazard.parentId + '.' + subHazard.id;
            // get the related subConstraint for each subHazard; and saving it into suhazardsAndSystemConstraints
            this.subHazardDataService.getSubConstraintBySubHazardId(this.projectId, this.selectedHazard.id, subHazard.id)
              .then((subConst: SubSystemConstraintResponseDTO) => {
                if (subConst !== null) {

                  subHazard.subSafetyConstraintName = subConst.name;
                  subHazard.subSafetyConstraintId = subConst.id;
                  subHazard.parentSubSafetyConstraintId = subConst.parentId;
                  subHazard.subConstraintEnt = subConst;
                  subHazard.subSafetyConstraintDisplayId = subConst.parentId + '.' + subConst.id;
                  if (subConst.lastEdited > subHazard.lastEdited) {
                    subHazard.lastEdited = subConst.lastEdited;
                    subHazard.lastEditor = subConst.lastEditor;
                  }
                } else {
                  subHazard.subSafetyConstraintName = '';
                  subHazard.subSafetyConstraintId = '';
                  subHazard.parentSubSafetyConstraintId = '';
                  subHazard.subConstraintEnt = null;
                  subHazard.subSafetyConstraintDisplayId = '';
                }
              }).catch((response: HttpErrorResponse) => {
                this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_LOADING, detail: response.message });
              });
          }
      }  }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_LOADING, detail: response.message });
        });
    } else {
      this.hazardDataService.getAllHazards(this.projectId, { 'orderBy': 'id', 'orderDirection': 'asc' })
        .then((value: HazardResponseDTO[]) => {
          this.navigationTableDefinitions = [];
          this.navigationTableDefinitions[0] = { dataSource: value, style: { width: '100%' }, columnHeaderName: 'Hazard' };
          if (value !== null && value.length > 0) {
            this.selectedHazard = value[0];
            this.loadData();
          } else {
            this.navigationTableDefinitions[0] = { dataSource: value, style: { width: '100%' }, columnHeaderName: 'No Hazards found' };
            this.messageService.add({
              severity: 'warn',
              life: 10000, summary: 'Create hazards to be able to create, link and edit corresponding sub-hazards!'
            });
          }
          // Create the shortcut button to the Hazard table
          this.navigationTableDefinitions[0].columnHeaderButton = { title: 'Go to Hazards', routerLink: 'project/' + this.projectId + '/system-level-hazards/' };
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_LOADING, detail: response.message });
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
        style: { width: '10%' }
      }, {
        key: 'subHazDisplayId',
        title: 'SH-ID',
        type: ColumnType.Text,
        style: { width: '5%' }
      }, {
        key: 'name',
        title: 'Sub-Hazard-Name',
        type: ColumnType.Text,
        style: { width: '25%' }
      }, {
        key: 'subSafetyConstraintDisplayId',
        title: 'SC-ID',
        type: ColumnType.Text,
        style: { width: '5%' }
      }, {
        key: 'subSafetyConstraintName',
        title: 'Sub-Safety-Constraint-Name',
        type: ColumnType.Text,
        style: { width: '25%' }
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

  changeEntity(event: { list: SubHazardResponseDTO[], state: string }) {
    event.list.forEach((element: SubHazardResponseDTO) => {

      let incomplete: incompleteEntityDTO = { entityName: 'sub_hazard', parentId: this.selectedHazard.id, state: event.state };

      this.incompleteEntityService.updateState(this.projectId, element.id, incomplete).then(value => {
        this.loadData();
      }).catch((reason: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: CHANGED_STATE_FAILED, detail: reason.message });
      })
    });
  }
  // data is an object passed from the maint-table.ts its an object with {entity:,mode:} and entity was passed from here which is the subhazard Object
  toggleSheet(data): void {
    this.disableSaveButton = false;
    const scLinks: SystemConstraintResponseDTO[] = [];
    let ucaLinks: UcaResponseDTO[] = [];
    this.allLinksForAutocomplete = { sc_links: [], uca_links: [] };
    const promises = [];
    promises.push(this.systemConstraintDataService.getAllSafetyConstraints(this.projectId, {}));

    if (data.entity.subConstraintEnt !== null) {
      promises.push(this.systemConstraintDataService.getSafetyConstraintById(this.projectId, data.entity.parentSubSafetyConstraintId));
    }

    Promise.all(promises)
      .then((value) => {
        this.allLinksForAutocomplete.sc_links = value[0];
        if (value.length > 1) {
          scLinks.push(value[1]);

        }
        this.ucaDataService.getUcaBySubhazardDetailSheet(this.projectId, this.selectedHazard.id, data.entity.id, {}).then(value => {
          if (value !== null) {
            ucaLinks = value;
            let id: string;
            if (this.subHazard) {
              id = this.subHazard.id;
            }

            if (this.sheetMode === SheetMode.Edit) {
              this.closeSheet(false, SheetMode.View);
            }

            this.sheetMode = DetailedSheetUtils.toggle(id, data.entity.id, this.sheetMode, data.mode);

            this.subHazard = {
              id: data.entity.id, name: data.entity.name, description: data.entity.description,
              parentHazardId: this.selectedHazard.id, parentHazardName: this.selectedHazard.name,
              parentSubSafetyConstraintId: data.entity.parentSubSafetyConstraintId,
              subSafetyConstraintId: data.entity.subSafetyConstraintId, subSafetyConstraintName: data.entity.subSafetyConstraintName,
              subConstraintEnt: data.entity.subConstraintEnt,
              subSafetyConstraintDisplayId: data.entity.subSafetyConstraintDisplayId,
              sc_links: scLinks,
              uca_links: ucaLinks,
              allLinksForAutocomplete: this.allLinksForAutocomplete,
              state: data.entity.state,
            };

          } else {
            let id: string;
            if (this.subHazard) {
              id = this.subHazard.id;
            }

            if (this.sheetMode === SheetMode.Edit) {
              this.closeSheet(false, SheetMode.View);
            }

            this.sheetMode = DetailedSheetUtils.toggle(id, data.entity.id, this.sheetMode, data.mode);

            this.subHazard = {
              id: data.entity.id, name: data.entity.name, description: data.entity.description,
              parentHazardId: this.selectedHazard.id, parentHazardName: this.selectedHazard.name,
              parentSubSafetyConstraintId: data.entity.parentSubSafetyConstraintId,
              subSafetyConstraintId: data.entity.subSafetyConstraintId, subSafetyConstraintName: data.entity.subSafetyConstraintName,
              subConstraintEnt: data.entity.subConstraintEnt,
              subSafetyConstraintDisplayId: data.entity.subSafetyConstraintDisplayId,
              sc_links: scLinks,
              uca_links: [],
              allLinksForAutocomplete: this.allLinksForAutocomplete,
              state: data.entity.state,
            };

          }
        });


      }).catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_LOADING, detail: response.message });
      });
  }

  closeSheet(isUnlocked: boolean = false, sheetMode: SheetMode = SheetMode.Closed): void {
    if (!isUnlocked && this.sheetMode === SheetMode.Edit) {
      const promises: Promise<LockResponse>[] = [];
      const subHazUnlockDTO: UnlockRequestDTO = {
        id: this.subHazard.id,
        parentId: this.selectedHazard.id,
        entityName: this.title.toLowerCase()
      };
      promises.push(this.lockService.unlockEntity(this.projectId, subHazUnlockDTO));
      const subScUnlockDTO: UnlockRequestDTO = {
        id: this.subHazard.subSafetyConstraintId,
        entityName: SUB_SYSTEM_CONSTRAINT,
        parentId: this.subHazard.parentSubSafetyConstraintId
      };
      promises.push(this.lockService.unlockEntity(this.projectId, subScUnlockDTO));
      Promise.all(promises).then(() => {
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
