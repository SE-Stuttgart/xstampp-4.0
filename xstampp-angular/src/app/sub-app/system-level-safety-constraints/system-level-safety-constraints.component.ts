import { IncompleteEntitiesService } from './../../services/dataServices/incomplete-entities-data.service';
import { incompleteEntityDTO } from 'src/app/services/dataServices/incomplete-entities-data.service';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, OnDestroy, Output } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MessageService } from 'primeng/api';
import { SheetMode } from '../../common/detailed-sheet/detailed-sheet.component';
import { DetailedSheetUtils } from '../../common/detailed-sheet/utils/detailed-sheet-utils';
import { FieldType } from '../../common/entities/detailed-field';
import { ColumnType, TableColumn } from '../../common/entities/table-column';
import { HazardDataService } from '../../services/dataServices/hazard-data.service';
import { LockService } from '../../services/dataServices/lock.service';
import { UserDataService, UserDisplayRequestDTO } from 'src/app/services/dataServices/user-data.service';
import { SubSystemConstraintDataService } from '../../services/dataServices/sub-system-constraint-data.service';
import { SystemLevelSafetyConstraintDataService } from '../../services/dataServices/system-level-safety-constraint-data.service';
import { FilterService } from '../../services/filter-service/filter.service';
import { HazardResponseDTO, PageRequest, SystemConstraintResponseDTO } from '../../types/local-types';
import { DetailedField } from './../../common/entities/detailed-field';
import { AuthService } from './../../services/auth.service';
import { WebsocketService } from './../../services/websocket.service';
import { HazardRequestDTO, SubSystemConstraintResponseDTO, SYSTEM_CONSTRAINT, UnlockRequestDTO } from './../../types/local-types';
import { DETAILEDSHEET_EXPECTED_NAME_NOT_NULL, ENTITY_FAILED_LOADING, CHANGED_STATE_FAILED, DELETED_USER, DETAILEDSHEET_FAILED_CREATION, DETAILEDSHEET_SUCCESSFUL_CREATION, DETAILEDSHEET_FAILED_EDIT, DETAILEDSHEET_SUCCESSFUL_EDIT, ENTITY_SUCCESSFUL_DELETE, ENTITY_FAILED_DELETE, NO_USER } from 'src/app/globals';
import { Hotkeys } from 'src/app/hotkeys/hotkeys.service';
import { Subscription } from 'rxjs';
import { SelectedEntity } from '../dependent-element-tree/dependent-elements-types.component';

interface SafetyConstraintLinksForAutoComplete {
  haz_links: HazardResponseDTO[];
}

interface SafetyConstraintForDetailedSheet {
  id: string;
  name: string;
  description: string;
  haz_links: HazardRequestDTO[];
  allLinksForAutocomplete: SafetyConstraintLinksForAutoComplete;
  subTable: SubSystemConstraintResponseDTO[],
  state: string,
}

@Component({
  selector: 'app-system-level-safety-constraints',
  templateUrl: './system-level-safety-constraints.component.html',
  styleUrls: ['./system-level-safety-constraints.component.css']
})
export class SystemLevelSafetyConstraintsComponent implements OnDestroy {

  SheetMode: typeof SheetMode = SheetMode;
  private projectId: string;
  safetyConstraints: SystemConstraintResponseDTO[] = [];
  columns: TableColumn[] = [];
  detailedColumns: TableColumn[] = [];
  safetyConstraint: SafetyConstraintForDetailedSheet;
  sheetMode: SheetMode = SheetMode.Closed;
  fields: DetailedField[];
  title: string = DetailedSheetUtils.generateSheetTitle(SYSTEM_CONSTRAINT);
  private OwnChange: boolean = false;
  disableSaveButton: boolean = false;
  additionalDeleteMessage: string = 'By deleting a System-Safety-Constraint it\'s linked Sub-Safety-Constraints will be deleted too!';
  private newHazLinks = [];
  private allLinksForAutocomplete: SafetyConstraintLinksForAutoComplete;
  private newLink: boolean = false;
  subscriptions: Subscription[] = [];
  selectSystemConstraintEntity: SelectedEntity<Object>;

  @Output() selectEntityMessage: any = new EventEmitter<SelectedEntity<Object>>();

  constructor(
    private readonly route: ActivatedRoute,
    private readonly filterService: FilterService,
    private readonly messageService: MessageService,
    private readonly systemConstraintService: SystemLevelSafetyConstraintDataService,
    private readonly hazardDataService: HazardDataService,
    private readonly subSystemConstraintDataService: SubSystemConstraintDataService,
    private readonly wsService: WebsocketService,
    private readonly authService: AuthService,
    private readonly lockService: LockService,
    private readonly userDataService: UserDataService,
    private readonly incompleteEntityService: IncompleteEntitiesService,
    private readonly hotkeys: Hotkeys) {

    this.subscriptions.push(this.hotkeys.addShortcut({ keys: 'shift.control.n', description: 'Create Safety-Constraint' }).subscribe(() => {
      this.createSafetyConstraint();
    }));
    this.fields = [
      { title: 'ID', key: 'id', type: FieldType.Text, readonly: true },
      { title: 'Name', key: 'name', type: FieldType.Text, readonly: false },
      { title: 'Description', key: 'description', type: FieldType.Text_Variable, readonly: false, minRows: 3, maxRows: 10 },
      {
        title: 'Linked Sub-Safety-Constraints', key: 'subTable', type: FieldType.SubTable, readonly: false,
        shortcutButton: { title: 'Go to Subhazards', routerLink: (): string => 'project/' + this.projectId + '/refine-hazards/' }
      },
      {
        title: 'Linked Hazards', key: 'haz_links', type: FieldType.Chips, readonly: false, listKey: 'id', displayShortName: 'H-',
        shortcutButton: { title: 'Go to Hazards', routerLink: (): string => 'project/' + this.projectId + '/system-level-hazards/' }
      },
      { title: 'State', key: 'state', type: FieldType.StateButtonGroup, readonly: false },
    ];
    this.deleteSafetyConstraints = this.deleteSafetyConstraints.bind(this);
    this.route.parent.params.subscribe((value: any) => {
      this.projectId = value.id;
      this.initColumns();
      this.loadData();
    });

    // Websocket implementation
    this.authService.getProjectToken().then((token: string) => {
      this.wsService.connect('subscribe', SYSTEM_CONSTRAINT, token, (data) => {
        this.loadData();
        if (this.OwnChange === false) {
          this.messageService.add({ severity: 'info', summary: 'User ' + data.displayName + ' updated the data!' });
        }
      });
    }).catch((err: Error) => {
      console.log(err);
    });
  }

  async deleteSafetyConstraints($event) {
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
      return await this.systemConstraintService.deleteSafetyConstraint(this.projectId, elem.id);
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

  createSafetyConstraint(): void {
    this.sheetMode = SheetMode.New;
    this.allLinksForAutocomplete = { haz_links: [] };
    this.safetyConstraint = {
      id: '', name: '', description: '',
      haz_links: [],
      allLinksForAutocomplete: { haz_links: [] },
      subTable: [], state: '',
    };

    this.hazardDataService.getAllHazards(this.projectId, {}).then((value: HazardResponseDTO[]) => {

      this.allLinksForAutocomplete.haz_links = value;
      this.safetyConstraint = {
        id: '', name: '', description: '',
        haz_links: [],
        allLinksForAutocomplete: this.allLinksForAutocomplete,
        subTable: [], state: '',
      };
    }).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: DETAILEDSHEET_FAILED_CREATION, detail: response.message });
    });

  }

  // TODO discuss and document in detail which data is necessary to pass, maybe define interfaces for safety constraint sth. like ToSaveEntity interface
  saveSafetyConstraint(safetyConstraint): void {
    this.filterService.ClearSelectionEmitter.emit(true);
    this.OwnChange = true;
    this.disableSaveButton = true;

    if (!safetyConstraint.ent.name || safetyConstraint.ent.name.trim() === '') {
      this.messageService.add({ severity: 'warn', summary: DETAILEDSHEET_EXPECTED_NAME_NOT_NULL })
      this.disableSaveButton = false;
      return;
    }
    if (safetyConstraint.mode === SheetMode.EditWithLock) {
      const promises = [];
      promises.push(this.systemConstraintService.alterSafetyConstraint(this.projectId, safetyConstraint.id, safetyConstraint.ent));
      // safety constraint added is a map which has keys like haz_links or sc_links and values are a list of the related ids
      if (safetyConstraint.addedMap.has('haz_links')) {
        for (const newHazLink of safetyConstraint.addedMap.get('haz_links')) {
          console.log(newHazLink.id + ' will be added');
          promises.push(this.systemConstraintService.createSystemConstraintHazardLink(this.projectId, safetyConstraint.id, newHazLink.id));
        }
      }
      if (safetyConstraint.deletedMap.has('haz_links')) {
        for (const newHazLink of safetyConstraint.deletedMap.get('haz_links')) {
          console.log(newHazLink.id + ' will be deleted');
          promises.push(this.systemConstraintService.deleteSystemConstraintHazardLink(this.projectId, safetyConstraint.id, newHazLink.id));
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
    } else if (safetyConstraint.mode === SheetMode.New) {



      if (safetyConstraint.addedMap.has('haz_links')) {
        this.newLink = true;
        for (const newHazLink of safetyConstraint.addedMap.get('haz_links')) {
          this.newHazLinks.push(newHazLink);

        }
      }
      this.systemConstraintService.createSafetyConstraint(this.projectId, safetyConstraint.ent)
        .then(() => {
          this.messageService.add({ severity: 'success', summary: DETAILEDSHEET_SUCCESSFUL_CREATION });
          this.closeSheet();
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
        title: 'Safety-Constraint-Name',
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
        title: 'Sub-Safety-Constraint-Name',
        type: ColumnType.Text,
        style: { width: '85%' }
      }
    ];
  }
  changeEntity(event: { list: SystemConstraintResponseDTO[], state: string }) {
    event.list.forEach((element: SystemConstraintResponseDTO) => {
      let incomplete: incompleteEntityDTO = { entityName: 'system_constraint', parentId: undefined, state: event.state };

      this.incompleteEntityService.updateState(this.projectId, element.id, incomplete).then(value => {
        this.loadData();
      }).catch((reason: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: CHANGED_STATE_FAILED, detail: reason.message });
      })
    });
  }

  getSelectedEntities($event: any): void {
    this.selectSystemConstraintEntity = new SelectedEntity<Object>($event, this.title, this.projectId,
      true,
      false,
      false);
  }

  private loadData(): void {
    let userRequest: UserDisplayRequestDTO = new UserDisplayRequestDTO;
    let userId: string[] = [];
    let valueIndex: string[] = [];
    this.systemConstraintService.getAllSafetyConstraints(this.projectId, {'orderBy': 'id', 'orderDirection': 'asc'}).then(value => {
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
      this.safetyConstraints = value;
      if (this.newLink === true) {
        this.createNewLinkForNewConstraint();
      }}
    }).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_LOADING, detail: response.message });
    });
  }

  toggleSheet(data): void {
    // here load hazards and pass them into the Safety constraints array
    this.disableSaveButton = false;
    let hazardLinks = [];
    let subTable = [];
    this.allLinksForAutocomplete = { haz_links: [] };
    const page: PageRequest = { from: 0, amount: 100 };

    Promise.all(
      [this.systemConstraintService.getHazardsBySafetyConstraintId(this.projectId, data.entity.id, page),
      this.hazardDataService.getAllHazards(this.projectId, {}),
      this.subSystemConstraintDataService.getAllSubSystemConstraints(this.projectId, data.entity.id, {})
      ]
    )
      .then(value => {
        hazardLinks = value[0];
        this.allLinksForAutocomplete.haz_links = value[1];
        subTable = value[2];
        let id: string;
        if (this.safetyConstraint) {
          id = this.safetyConstraint.id;
        }
        if (this.sheetMode === SheetMode.Edit) {
          this.closeSheet(false, SheetMode.View);
        }
        this.sheetMode = DetailedSheetUtils.toggle(id, data.entity.id, this.sheetMode, data.mode);
        this.safetyConstraint = {
          id: data.entity.id, name: data.entity.name, description: data.entity.description,
          haz_links: hazardLinks,
          allLinksForAutocomplete: this.allLinksForAutocomplete,
          subTable: subTable,
          state: data.entity.state,
        };
      }).catch((error: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_LOADING, detail: error.toString() });
      });
  }

  closeSheet(isUnlocked: boolean = false, sheetMode: SheetMode = SheetMode.Closed): void {
    if (!isUnlocked && this.sheetMode === SheetMode.Edit) {
      const unlockDTO: UnlockRequestDTO = { id: this.safetyConstraint.id, entityName: this.title.toLowerCase() };
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
  private createNewLinkForNewConstraint() {
    const promises = [];
    this.newLink = false;
    let altSlscId = "";
    this.systemConstraintService.getAllSafetyConstraints(this.projectId, {}).then(value => {
      altSlscId = value.pop().id.toString();
      for (const newLossLink of this.newHazLinks) {
        promises.push(this.systemConstraintService.createSystemConstraintHazardLink(this.projectId, altSlscId, newLossLink.id));
      }
      this.newHazLinks = [];
    });



  }
}
