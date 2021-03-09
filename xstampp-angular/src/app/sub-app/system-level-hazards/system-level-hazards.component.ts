import { incompleteEntityDTO, IncompleteEntitiesService } from 'src/app/services/dataServices/incomplete-entities-data.service';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from './../../services/auth.service';
import { WebsocketService } from './../../services/websocket.service';
import { DetailedField } from './../../common/entities/detailed-field';
import { SubHazardResponseDTO, HAZARD, SUB_SYSTEM_CONSTRAINT, SUB_HAZARD, UnlockRequestDTO, UcaResponseDTO, LossResponseDTO } from './../../types/local-types';
import { SubSystemConstraintDataService } from '../../services/dataServices/sub-system-constraint-data.service';
import { SystemLevelSafetyConstraintDataService } from '../../services/dataServices/system-level-safety-constraint-data.service';
import { SubHazardDataService } from '../../services/dataServices/sub-hazard-data.service';
import { LossDataService } from '../../services/dataServices/loss-data.service';
import { HazardDataService } from '../../services/dataServices/hazard-data.service';
import { PageRequest, SystemConstraintResponseDTO, HazardResponseDTO } from '../../types/local-types';
import { ActivatedRoute } from '@angular/router';
import { Component, EventEmitter, OnDestroy, Output } from '@angular/core';
import { ColumnType, TableColumn } from '../../common/entities/table-column';
import { FilterService } from '../../services/filter-service/filter.service';
import { FieldType } from '../../common/entities/detailed-field';
import { SheetMode } from '../../common/detailed-sheet/detailed-sheet.component';
import { MessageService } from 'primeng/api';
import { DetailedSheetUtils } from '../../common/detailed-sheet/utils/detailed-sheet-utils';
import { LockService } from '../../services/dataServices/lock.service';
import { UserDataService, UserDisplayRequestDTO } from 'src/app/services/dataServices/user-data.service';
import { UcaDataService } from 'src/app/services/dataServices/uca-data.service';
import { DETAILEDSHEET_EXPECTED_NAME_NOT_NULL, ENTITY_FAILED_LOADING, DETAILEDSHEET_FAILED_LOADING_CHIPS, DELETED_USER, CHANGED_STATE_FAILED, DETAILEDSHEET_FAILED_CREATION, DETAILEDSHEET_SUCCESSFUL_CREATION, DETAILEDSHEET_FAILED_EDIT, DETAILEDSHEET_SUCCESSFUL_EDIT, ENTITY_SUCCESSFUL_DELETE, NO_USER } from 'src/app/globals';
import { Hotkeys } from 'src/app/hotkeys/hotkeys.service';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { SelectedEntity } from '../dependent-element-tree/dependent-elements-types.component';

interface HazardLinksForAutoComplete {
  loss_links: LossResponseDTO[];
  sc_links: SystemConstraintResponseDTO[];
  uca_links: UcaResponseDTO[];

}

interface HazardForDetailedSheet {
  id: string;
  name: string;
  description: string;
  loss_links: LossResponseDTO[];
  sc_links: SystemConstraintResponseDTO[];
  uca_links: UcaResponseDTO[];
  allLinksForAutocomplete: { loss_links: LossResponseDTO[], sc_links: SystemConstraintResponseDTO[] };
  subTable: SubHazardResponseDTO[];
  state: string;
}

@Component({
  selector: 'app-system-level-hazards',
  templateUrl: './system-level-hazards.component.html',
  styleUrls: ['./system-level-hazards.component.css']
})
export class SystemLevelHazardsComponent implements OnDestroy {

  private newLink: boolean = false;
  SheetMode: typeof SheetMode = SheetMode;
  private projectId: string;
  hazards: HazardResponseDTO[] = [];
  columns: TableColumn[] = [];
  detailedColumns: TableColumn[] = [];
  hazard: HazardForDetailedSheet;
  sheetMode: SheetMode = SheetMode.Closed;
  fields: DetailedField[];
  title = DetailedSheetUtils.generateSheetTitle(HAZARD);
  additionalDeleteMessage: string = 'By deleting a Hazard all it\'s Sub-Hazards and linked Sub-Safety-Constraints will be deleted too!';
  private allLinksForAutocomplete: HazardLinksForAutoComplete;
  private OwnChange: boolean = false;
  disableSaveButton: boolean = false;
  newLossLinks: any[] = [];
  newSCLinks = [];
  subscriptions: Subscription[] = [];
  selectHazardEntity: SelectedEntity<Object>;

  @Output() selectEntityMessage: any = new EventEmitter<SelectedEntity<Object>>();

  constructor(
    private readonly route: ActivatedRoute,
    private readonly filterService: FilterService,
    private readonly messageService: MessageService,
    private readonly hazardDataService: HazardDataService,
    private readonly lossDataService: LossDataService,
    private readonly subHazardDataService: SubHazardDataService,
    private readonly systemLevelSafetyConstraintDataService: SystemLevelSafetyConstraintDataService,
    private readonly subSystemConstraintDataService: SubSystemConstraintDataService,
    private readonly wsService: WebsocketService,
    private readonly authService: AuthService,
    private readonly lockService: LockService,
    private readonly ucaService: UcaDataService,
    private readonly hotkeys: Hotkeys,
    private readonly userDataService: UserDataService,
    private readonly incompleteEntityService: IncompleteEntitiesService,
    private readonly router: Router) {

    this.subscriptions.push(this.hotkeys.addShortcut({ keys: 'shift.control.n', description: 'Create Hazard' }).subscribe(() => {
      this.createHazard();
    }));

    this.fields = [
      { title: 'ID', key: 'id', type: FieldType.Text, readonly: true },
      { title: 'Name', key: 'name', type: FieldType.Text, readonly: false },
      { title: 'Description', key: 'description', type: FieldType.Text_Variable, readonly: false, minRows: 3, maxRows: 10 },
      {
        title: 'Linked Sub-Hazards', key: 'subTable', type: FieldType.SubTable, readonly: false,
        shortcutButton: { title: 'Go to Subhazards', routerLink: (): string => 'project/' + this.projectId + '/refine-hazards/' }
      },
      {
        title: 'Linked Losses', key: 'loss_links', type: FieldType.Chips, readonly: false, listKey: 'id', displayShortName: 'L-',
        shortcutButton: { title: 'Go to Losses', routerLink: (): string => 'project/' + this.projectId + '/identify-losses/' }
      },
      {
        title: 'Linked Safety-Constraints', key: 'sc_links', type: FieldType.Chips, readonly: false, listKey: 'id', displayShortName: 'SC-',
        shortcutButton: { title: 'Go to System Constraints', routerLink: (): string => 'project/' + this.projectId + '/system-level-safety-constraints/' }
      },
      {
        title: 'Linked UCA', key: 'uca_links', type: FieldType.Chips, readonly: true, listKey: 'id', displayShortName: 'UCA-',
        shortcutButton: { title: 'Go to UCAs', routerLink: (): string => 'project/' + this.projectId + '/uca-table/' }
      },
      { title: 'State', key: 'state', type: FieldType.StateButtonGroup, readonly: false },
    ];

    this.deleteHazards = this.deleteHazards.bind(this);
    this.route.parent.params.subscribe((value: any) => {
      this.projectId = value.id;
      this.initColumns();
      this.loadData();
    });

    // Websocket implementation
    this.authService.getProjectToken().then((token: string) => {
      this.wsService.connect('subscribe', HAZARD, token, (data) => {
        this.loadData();
        if (this.OwnChange === false) {
          this.messageService.add({ severity: 'info', summary: 'User ' + data.displayName + ' updated the data!' });
        }
      });
    }).catch((err: HttpErrorResponse) => {
      console.log(err);
    });
  }

  async deleteHazards($event) {
    this.OwnChange = true;
    const list: any[] = $event;
    if (list.length === 0) {
      return;
    }
    const failedResponses: Error[] = [];

    for (const hazard of list) {
      this.OwnChange = true;
      // For deleting a Hazard it is necessary to delete all linked subSafetyConstraint of their subHazard before it is allowed to delete the Hazard
      // subHazards are automatically deleted from database if Hazard gets deleted
      // 0. Retrieve all subHazards SUCCESS
      await this.subHazardDataService.getAllSubHazards(this.projectId, hazard.id, {}).then(async subHazardArray => {
        let deleteSafetyConstSuccessful;
        for (const subHazard of subHazardArray) {
          // flag is used to check if subSC of a ALL subHazard were deleted correctly, if a single subSC deletion fails this flags will always stay false
          deleteSafetyConstSuccessful = true;
          // try to 1.retrieve linked subSafetyConstraint, 2. get lock for subSafetyConstraint, 3.delete subSC
          await this.subHazardDataService.getSubConstraintBySubHazardId(this.projectId, hazard.id, subHazard.id)
            .then(async value => {
              // 1.Retrieve subSC SUCCESS
              if (value === null) {
                // Retrieve subSC returns null -> subHazard has no subSC -> skip else part and continue in loop to check for next subHazard
                deleteSafetyConstSuccessful = deleteSafetyConstSuccessful && true;
              } else {
                // Retrieve subSC returns a subHazard try to lock and delete
                // try to lock SubSC
                await this.lockService.lockEntity(this.projectId, {
                  id: value.id,
                  parentId: value.parentId,
                  entityName: SUB_SYSTEM_CONSTRAINT.toLowerCase(),
                  expirationTime: DetailedSheetUtils.calculateLockExpiration()
                }).then(async () => {
                  // 2.Lock subSC SUCCES
                  await this.subSystemConstraintDataService.deleteSubSystemConstraint(this.projectId, value.parentId, value.id).then(() => {
                    // 3.Delete subSC SUCCESS
                    // if eachSubSC arrives at this part deleteSafetyConstSuccessful will stay true
                    deleteSafetyConstSuccessful = deleteSafetyConstSuccessful && true;
                  }).catch((deleteResponse: HttpErrorResponse) => {
                    // 3.Delete subSC FAILED
                    deleteSafetyConstSuccessful = deleteSafetyConstSuccessful && false;
                    failedResponses.push(deleteResponse);
                    this.messageService.add({
                      severity: 'error',
                      summary: 'Error on deleting sub hazard and linked sub safety constraint',
                      life: 10000,
                      detail: 'Failed to delete sub safety constraint ' + value.id + ' Therefore Hazard H-' + hazard.id + ' will not be deleted. Try again'
                    });
                  });
                }).catch((lockResponse: HttpErrorResponse) => {
                  // 2.Lock subSC FAILED
                  deleteSafetyConstSuccessful = deleteSafetyConstSuccessful && false;
                  failedResponses.push(lockResponse);
                  this.messageService.add({
                    severity: 'error',
                    summary: 'Error on deleting sub hazard and linked sub safety constraint',
                    life: 10000,
                    detail: 'Failed to get lock for sub safety constraint link of sub hazard  ' + hazard.id + '.' + subHazard.id +
                      ' Therefore Hazard H-' + hazard.id + ' will not be deleted'
                  });
                });
              }
            }).catch((retrieveSubSCresponse: HttpErrorResponse) => {
              // 1.Retrieve subSC FAILED
              deleteSafetyConstSuccessful = deleteSafetyConstSuccessful && false;
              failedResponses.push(retrieveSubSCresponse);
              this.messageService.add({
                severity: 'error',
                summary: 'Error on deleting sub hazard and linked sub safety constraint',
                life: 10000,
                detail: 'Failed to retrieve sub safety constraint of sub hazard  ' + hazard.id + '.' + subHazard.id +
                  ' Therefore Hazard H-' + hazard.id + ' will not be deleted. Try again'
              });
            });
        }
        // upper await block shpuld execute before this part
        // if all subHazards of Hazard are checked and all their linked subSafetyConstraint could be deleted then try to delete Hazard
        if (deleteSafetyConstSuccessful || subHazardArray.length === 0) {
          await this.lockService.lockEntity(this.projectId, {
            id: hazard.id,
            expirationTime: DetailedSheetUtils.calculateLockExpiration(),
            entityName: HAZARD.toLowerCase()
          }).then(() => {
            // Lock for hazard SUCCESS
            this.hazardDataService.deleteHazard(this.projectId, hazard.id).then(() => {
              // Delete hazard SUCCESS
              // this.messageService.add({severity: 'success', summary: 'Delete successful'});
            }).catch((deleteHazardResponse: HttpErrorResponse) => {
              failedResponses.push(deleteHazardResponse);
              this.messageService.add({
                // Delete hazard FAILED
                severity: 'error',
                summary: 'Error on deleting hazard',
                life: 10000,
                detail: 'Failed to delete hazard H-' + hazard.id + ' Therefore Hazard H-' + hazard.id + ' will not be deleted. Try again'
              });
            });
          }
          ).catch((lockResponse: HttpErrorResponse) => {
            // Lock for hazard FAILED
            failedResponses.push(lockResponse);
            this.messageService.add({
              severity: 'error',
              summary: 'Error on deleting hazard',
              life: 10000,
              detail: 'Failed to get lock for hazard H-' + hazard.id + ' Therefore Hazard H-' + hazard.id + ' will not be deleted'
            });
          }
          );
        }
      }).catch((retrieveResponse: HttpErrorResponse) => {
        // 0. Retrieve all subHazards FAILED
        failedResponses.push(retrieveResponse);
        this.messageService.add({
          severity: 'error',
          life: 10000,
          summary: 'Error on deleting hazard',
          detail: 'Deletion of hazard H-' + hazard.id + ' failed, Retrieving and deletion of sub entities failed. Try again'
        });
      }).finally(() => {
        this.OwnChange = false;
        this.loadData();
      });
    }
    // end of for loop of all to be delted Hazards

    if (failedResponses.length === 0) {
      // only display this message if everything 100% worked
      this.messageService.add({ severity: 'success', summary: ENTITY_SUCCESSFUL_DELETE });
    }
    this.OwnChange = false;
    this.loadData();
  }

  createHazard(): void {

    this.sheetMode = SheetMode.New;
    this.allLinksForAutocomplete = { loss_links: [], sc_links: [], uca_links: [] };

    this.hazard = {

      id: '', name: '', description: '',
      loss_links: [],
      sc_links: [],
      allLinksForAutocomplete: this.allLinksForAutocomplete,
      subTable: [],
      uca_links: [],
      state: '',
    };
    Promise.all(
      [this.lossDataService.getAllLosses(this.projectId, {}),
      this.systemLevelSafetyConstraintDataService.getAllSafetyConstraints(this.projectId, {}),
      ]).then(value => {
        this.allLinksForAutocomplete.loss_links = value[0];
        this.allLinksForAutocomplete.sc_links = value[1];
        this.hazard = {

          id: '', name: '', description: '',
          loss_links: [],
          sc_links: [],
          uca_links: [],
          allLinksForAutocomplete: this.allLinksForAutocomplete,
          subTable: [],
          state: '',
        };
      }).catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: DETAILEDSHEET_FAILED_CREATION, detail: response.message });
      });

  }

  saveHazard(hazard): void {
    this.filterService.ClearSelectionEmitter.emit(true);
    this.OwnChange = true;
    this.disableSaveButton = true;

    if (!hazard.ent.name || hazard.ent.name.trim() === '') {
      this.messageService.add({ severity: 'warn', summary: DETAILEDSHEET_EXPECTED_NAME_NOT_NULL })
      this.disableSaveButton = false;
      return;
    }

    if (hazard.mode === SheetMode.EditWithLock) {
      if (this.hazard.name !== '') {
        const promises = [];
        const hazardRequestEnt = { id: hazard.ent.id, description: hazard.ent.description, name: hazard.ent.name, state: hazard.ent.state };
        promises.push(this.hazardDataService.alterHazard(this.projectId, hazard.id, hazardRequestEnt));
        // loss added is a map which has keys like haz_links or sc_links and values are a list of the related ids
        if (hazard.addedMap.has('loss_links')) {
          for (const newLossLink of hazard.addedMap.get('loss_links')) {
            promises.push(this.hazardDataService.createHazardLossLink(this.projectId, hazard.id, newLossLink.id));
          }
        }
        if (hazard.deletedMap.has('loss_links')) {
          for (const newLossLink of hazard.deletedMap.get('loss_links')) {
            promises.push(this.hazardDataService.deleteHazardLossLink(this.projectId, hazard.id, newLossLink.id));
          }
        }
        if (hazard.addedMap.has('sc_links')) {
          for (const newSCLink of hazard.addedMap.get('sc_links')) {
            promises.push(this.hazardDataService.createHazardSystemConstraintLink(this.projectId, hazard.id, newSCLink.id));
          }
        }
        if (hazard.deletedMap.has('sc_links')) {
          for (const newSCLink of hazard.deletedMap.get('sc_links')) {
            promises.push(this.hazardDataService.deleteHazardSystemConstraintLink(this.projectId, hazard.id, newSCLink.id));
          }
        }
        Promise.all(promises)
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
      }
    } else if (hazard.mode === SheetMode.New) {
      if (this.hazard.name !== '') {
        // TODO Temporarly fix
        const hazardRequestEnt = { id: hazard.ent.id, description: hazard.ent.description, name: hazard.ent.name, state: hazard.ent.state };
        this.newLink = true;
        if (hazard.addedMap.has('loss_links')) {
          for (const newLossLink of hazard.addedMap.get('loss_links')) {

            this.newLossLinks.push(newLossLink);
          }

        }
        if (hazard.addedMap.has('sc_links')) {

          for (const newSCLink of hazard.addedMap.get('sc_links')) {
            this.newSCLinks.push(newSCLink);
          }
        }
        this.hazardDataService.createHazard(this.projectId, hazardRequestEnt).then(() => {

          this.messageService.add({ severity: 'success', summary: DETAILEDSHEET_SUCCESSFUL_CREATION });
          this.closeSheet();
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: DETAILEDSHEET_FAILED_CREATION, detail: response.message });
        }).finally(() => {

          this.OwnChange = false;
          this.disableSaveButton = false;
          this.loadData();
        });
      } else if (hazard.mode === SheetMode.New) {
        // TODO Temporarly fix
        console.log(hazard.ent.id)
        const hazardRequestEnt = { id: hazard.ent.id, description: hazard.ent.description, name: hazard.ent.name, state: hazard.ent.state };
        this.hazardDataService.createHazard(this.projectId, hazardRequestEnt).then(() => {
          this.messageService.add({ severity: 'success', summary: DETAILEDSHEET_SUCCESSFUL_CREATION });
          this.closeSheet();
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: DETAILEDSHEET_FAILED_CREATION, detail: response.message });
        }).finally(() => {
          this.OwnChange = false;
        });
      }
    }
  }

  changeEntity(event: { list: HazardResponseDTO[], state: string }) {
    event.list.forEach((element: HazardResponseDTO) => {
      let incomplete: incompleteEntityDTO = { entityName: 'hazard', parentId: undefined, state: event.state };

      this.incompleteEntityService.updateState(this.projectId, element.id, incomplete).then(value => {
        this.loadData();
      }).catch((reason: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: CHANGED_STATE_FAILED, detail: reason.message });
      })
    });
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
        title: 'Hazard-Name',
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

    this.detailedColumns = [
      {
        key: 'id',
        title: 'ID',
        type: ColumnType.Text,
        style: { width: '15%' }
      }, {
        key: 'name',
        title: 'Sub-Hazard-Name',
        type: ColumnType.Text,
        style: { width: '85%' }
      }
    ];
  }

  getSelectedEntities($event: any): void {
    this.selectHazardEntity = new SelectedEntity<Object>($event,
      this.title,
      this.projectId,
      true,
      false,
      false);
  }


  private loadData(): void {
    let userRequest: UserDisplayRequestDTO = new UserDisplayRequestDTO;
    let userId: string[] = [];
    let valueIndex: string[] = [];
    this.hazardDataService.getAllHazards(this.projectId, { 'orderBy': 'id', 'orderDirection': 'asc' })
      .then((value: HazardResponseDTO[]) => {
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
        this.hazards = value;
        if (this.newLink === true) {

          this.createNewLinkForNewHazard();
        }
     } }).catch((response: HttpErrorResponse) => {
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
    let lossLinks = [];
    let scLinks = [];
    let subTable = [];
    let ucaLinks = [];
    this.allLinksForAutocomplete = { loss_links: [], sc_links: [], uca_links: [] };
    const page: PageRequest = { from: 0, amount: 100 };

    Promise.all(
      [this.hazardDataService.getLossesByHazardId(this.projectId, data.entity.id, page),
      this.lossDataService.getAllLosses(this.projectId, {}),
      this.hazardDataService.getSystemConstraintsByHazardId(this.projectId, data.entity.id, page),
      this.systemLevelSafetyConstraintDataService.getAllSafetyConstraints(this.projectId, {}),
      this.subHazardDataService.getAllSubHazards(this.projectId, data.entity.id, {}),

      ])
      .then(value => {
        lossLinks = value[0];
        this.allLinksForAutocomplete.loss_links = value[1];
        scLinks = value[2];
        this.allLinksForAutocomplete.sc_links = value[3];
        subTable = value[4];

        this.ucaService.getUcaByHazardDetailSheet(this.projectId, data.entity.id, {}).then(value => {
          if (value !== null) {
            ucaLinks = value;
            let id: string;
            if (this.hazard) {
              id = this.hazard.id;
            }
            if (this.sheetMode === SheetMode.Edit) {
              this.closeSheet(false, SheetMode.View);
            }
            this.sheetMode = DetailedSheetUtils.toggle(id, data.entity.id, this.sheetMode, data.mode);
            this.hazard = {
              description: data.entity.description,
              name: data.entity.name,
              id: data.entity.id,
              loss_links: lossLinks,
              sc_links: scLinks,
              uca_links: ucaLinks,
              allLinksForAutocomplete: this.allLinksForAutocomplete,
              subTable: subTable,
              state: data.entity.state,
            };
          } else {
            let id: string;
            if (this.hazard) {
              id = this.hazard.id;
            }
            if (this.sheetMode === SheetMode.Edit) {
              this.closeSheet(false, SheetMode.View);
            }
            this.sheetMode = DetailedSheetUtils.toggle(id, data.entity.id, this.sheetMode, data.mode);
            this.hazard = {
              description: data.entity.description,
              name: data.entity.name,
              id: data.entity.id,
              loss_links: lossLinks,
              sc_links: scLinks,
              uca_links: [],
              allLinksForAutocomplete: this.allLinksForAutocomplete,
              subTable: subTable,
              state: data.entity.state,
            };
          }

        });

      }).catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: DETAILEDSHEET_FAILED_LOADING_CHIPS, detail: response.message });
      });

  }

  closeSheet(isUnlocked: boolean = false, sheetMode = SheetMode.Closed): void {
    if (!isUnlocked && this.sheetMode === SheetMode.Edit) {
      // todo parent?
      const unlockDTO: UnlockRequestDTO = { id: this.hazard.id, entityName: this.title.toLowerCase() };
      this.lockService.unlockEntity(this.projectId, unlockDTO).then(() => {
        this.sheetMode = sheetMode;
      }).catch((error: Error) => {
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

  // method to create a new link or a new hazard
  private createNewLinkForNewHazard(): void {
    const promises = [];
    let altHazId = '';
    this.hazardDataService.getAllHazards(this.projectId, {}).then(value => {
      altHazId = value.pop().id.toString();

      for (const newLossLink of this.newLossLinks) {
        promises.push(this.hazardDataService.createHazardLossLink(this.projectId, altHazId, newLossLink.id));
      }
      for (const newSCLink of this.newSCLinks) {

        promises.push(this.hazardDataService.createHazardSystemConstraintLink(this.projectId, altHazId, newSCLink.id));
      }
      this.newLossLinks = [];
      this.newSCLinks = [];
    });

    this.newLink = false;
  }
}
