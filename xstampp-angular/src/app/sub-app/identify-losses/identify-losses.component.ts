import { DETAILEDSHEET_EXPECTED_NAME_NOT_NULL, NO_USER } from './../../globals';
import { Component, OnDestroy, Input, Output, EventEmitter } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MessageService } from 'primeng/api';
import { SheetMode } from '../../common/detailed-sheet/detailed-sheet.component';
import { DetailedSheetUtils } from '../../common/detailed-sheet/utils/detailed-sheet-utils';
import { FieldType } from '../../common/entities/detailed-field';
import { ColumnType, TableColumn } from '../../common/entities/table-column';
import { LockService } from '../../services/dataServices/lock.service';
import { FilterService } from '../../services/filter-service/filter.service';
import { DetailedField } from './../../common/entities/detailed-field';
import { AuthService } from './../../services/auth.service';
import { HazardDataService } from './../../services/dataServices/hazard-data.service';
import { LossDataService } from './../../services/dataServices/loss-data.service';
import { WebsocketService } from './../../services/websocket.service';
import { HazardResponseDTO, LOSS, PageRequest, UnlockRequestDTO } from './../../types/local-types';
import { HttpErrorResponse } from '@angular/common/http';
import { UserDataService, UserDisplayRequestDTO } from 'src/app/services/dataServices/user-data.service';
import { Hotkeys } from 'src/app/hotkeys/hotkeys.service';
import { Router } from '@angular/router';
import { DDEvent } from 'src/app/common/entities/data';
import { IncompleteEntitiesService, incompleteEntityDTO } from 'src/app/services/dataServices/incomplete-entities-data.service';
import { LossResponseDTO } from './../../types/local-types';
import { state } from '@angular/animations';
import { ENTITY_FAILED_DELETE, ENTITY_SUCCESSFUL_DELETE, DETAILEDSHEET_SUCCESSFUL_EDIT, DETAILEDSHEET_FAILED_EDIT, DETAILEDSHEET_SUCCESSFUL_CREATION, DETAILEDSHEET_FAILED_CREATION, CHANGED_STATE_FAILED, DELETED_USER, ENTITY_FAILED_LOADING, DETAILEDSHEET_FAILED_LOADING_CHIPS } from 'src/app/globals';
import { Subscription } from 'rxjs';
import { SelectedEntity } from '../dependent-element-tree/dependent-elements-types.component';

interface LossLinksForAutoComplete { haz_links: HazardResponseDTO[]; }
interface LossForDetailedSheet {
  id: string; name: string; description: string;
  state: string;
  haz_links: HazardResponseDTO[];
  allLinksForAutocomplete: LossLinksForAutoComplete;

}

@Component({
  selector: 'app-identify-losses',
  templateUrl: './identify-losses.component.html',
  styleUrls: ['./identify-losses.component.css']
})
export class IdentifyLossesComponent implements OnDestroy {

  // CATEGORIZE - Detailed sheet , main-Table, global for this component/class
  SheetMode: typeof SheetMode = SheetMode;
  private projectId: string;
  losses: LossResponseDTO[] = [];
  columns: TableColumn[] = [];
  loss: LossForDetailedSheet;
  sheetMode: SheetMode = SheetMode.Closed;
  fields: DetailedField[];
  title: string = DetailedSheetUtils.generateSheetTitle(LOSS);
  private allLinksForAutocomplete: LossLinksForAutoComplete;
  private OwnChange: boolean = false; // Variable to check if the change to the topic was made by the own user
  disableSaveButton: boolean = false; // Varible to check if save process is ongoing
  private hazardId: string;
  private newHazardLinks = [];
  private newLink: boolean = false;
  selectedState: string = '';
  subscriptions: Subscription[] = [];
  selectLossEntity: SelectedEntity<Object>;

  @Output() selectEntityMessage: any = new EventEmitter<SelectedEntity<Object>>();

  constructor(
    private readonly route: ActivatedRoute,
    private readonly filterService: FilterService, // Change filter Service to Emitter service?
    private readonly messageService: MessageService,
    private readonly lossDataService: LossDataService,
    private readonly hazardDataService: HazardDataService,
    private readonly wsService: WebsocketService,
    private readonly authService: AuthService,
    private readonly lockService: LockService,
    private readonly router: Router,
    private readonly userDataService: UserDataService,
    private readonly incompleteEntityService: IncompleteEntitiesService,
    private readonly hotkeys: Hotkeys) {
    this.fields = [
      { title: 'ID', key: 'id', type: FieldType.Text, readonly: true },
      { title: 'Name', key: 'name', type: FieldType.Text, readonly: false },
      { title: 'Description', key: 'description', type: FieldType.Text_Variable, readonly: false, minRows: 3, maxRows: 10 },
      {
        title: 'Linked Hazards', key: 'haz_links', type: FieldType.Chips, readonly: false, listKey: 'id', displayShortName: 'H-',
        shortcutButton: { title: 'Go to Hazards', routerLink: (): string => 'project/' + this.projectId + '/system-level-hazards/' }
      },
      { title: 'State', key: 'state', type: FieldType.StateButtonGroup, readonly: false },
    ];
    this.deleteLosses = this.deleteLosses.bind(this);
    this.route.parent.params.subscribe((value: any) => {
      this.projectId = value.id;
      this.initColumns();
      this.loadData();
    });

    // Websocket implementation
    this.authService.getProjectToken().then((token: string) => {
      this.wsService.connect('subscribe', LOSS, token, (data) => {
        this.loadData();
        if (this.OwnChange === false) {
          this.messageService.add({ severity: 'info', summary: 'User ' + data.displayName + ' updated the data!' });
        }
      });
    }).catch((err: HttpErrorResponse) => { console.log(err); });

    this.subscriptions.push(this.hotkeys.addShortcut({ keys: 'shift.control.n', description: 'New Loss' }).subscribe(() => {
      this.createLoss();
    }));
  }

  getSelectedEntities($event: any): void {
    this.selectLossEntity = new SelectedEntity<Object>($event,
      this.title,
      this.projectId,
      true,
      false,
      false);
  }

  async deleteLosses($event) {
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
      return await this.lossDataService.deleteLoss(this.projectId, elem.id);
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

  getState(state: DDEvent): void {
    this.selectedState = state.value;
    console.log(this.selectedState);
  }
  createLoss(): void {
    this.sheetMode = SheetMode.New;

    // TODO load allLinksForAutocomplete if linking in create mode is allowed
    // TODO handle Linking errors when alllinks for autocomplete loading is not possible
    this.loss = {
      id: '', description: '', name: '', state: '',
      haz_links: [],
      allLinksForAutocomplete: { haz_links: [] }
    };
    this.allLinksForAutocomplete = { haz_links: [] };

    this.hazardDataService.getAllHazards(this.projectId, {}).then(value => {
      this.allLinksForAutocomplete.haz_links = value;
      this.loss = {
        id: '', description: '', name: '', state: '',
        haz_links: [],
        allLinksForAutocomplete: this.allLinksForAutocomplete
      };

    });
  }

  // TODO discuss and document in detail which data is necessary to pass, maybe define interfaces for loss sth. like ToSaveEntity interface
  saveLoss(loss): void {
    this.filterService.ClearSelectionEmitter.emit(true);
    this.OwnChange = true;
    this.disableSaveButton = true;
    if (!loss.ent.name || loss.ent.name.trim() === '') {
      this.messageService.add({ severity: 'warn', summary: DETAILEDSHEET_EXPECTED_NAME_NOT_NULL });

      this.disableSaveButton = false;
      return;
    }
    if (loss.mode === SheetMode.EditWithLock) {
      if (this.loss.name !== '') {
        const promises = [];
        promises.push(this.lossDataService.alterLoss(this.projectId, loss.id, loss.ent));
        // loss added is a map which has keys like haz_links and values are a list of the related ids
        if (loss.addedMap.has('haz_links')) {
          for (const newHazLink of loss.addedMap.get('haz_links')) {
            promises.push(this.lossDataService.createLossHazardLink(this.projectId, loss.id, newHazLink.id));
          }
        }
        if (loss.deletedMap.has('haz_links')) {
          for (const newHazLink of loss.deletedMap.get('haz_links')) {
            promises.push(this.lossDataService.deleteLossHazardLink(this.projectId, loss.id, newHazLink.id));
          }
        }
        // TODO more detailed Message for example which deletion or addition failed
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
      } else {
        this.messageService.add({ severity: 'error', summary: 'Name field empty' });
        this.disableSaveButton = false;
      }
    } else if (loss.mode === SheetMode.New) {

      if (this.loss.name !== '') {
        if (loss.addedMap.has('haz_links')) {
          this.newLink = true;
          for (const newHazLink of loss.addedMap.get('haz_links')) {
            this.newHazardLinks.push(newHazLink);
          }
        }
        this.lossDataService.createLoss(this.projectId, loss.ent)
          .then(() => {
            this.messageService.add({ severity: 'success', summary: DETAILEDSHEET_SUCCESSFUL_CREATION });
            this.closeSheet(true);
          }).catch((response: HttpErrorResponse) => {
            this.messageService.add({ severity: 'error', summary: DETAILEDSHEET_FAILED_CREATION, detail: response.message });
          }).finally(() => {
            this.OwnChange = false;
            this.disableSaveButton = false;
            this.loadData();
          }
          );
      } else {
        this.messageService.add({ severity: 'error', summary: 'Name field empty' });
        this.disableSaveButton = false;
      }
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
        title: 'Loss-Name',
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
      },
    ];
  }
  changeEntity(event: { list: LossResponseDTO[], state: string }) {
    event.list.forEach((element: LossResponseDTO) => {

      let incomplete: incompleteEntityDTO = { entityName: 'loss', parentId: undefined, state: event.state };

      this.incompleteEntityService.updateState(this.projectId, element.id, incomplete).then(value => {
        this.loadData();
      }).catch((reason: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: CHANGED_STATE_FAILED, detail: reason.message });
      })
    });
  }

  loadData(): void {
    let userRequest: UserDisplayRequestDTO = new UserDisplayRequestDTO;
    let userId: string[] = [];
    let valueIndex: string[] = [];
    this.lossDataService.getAllLosses(this.projectId, { 'orderBy': 'id', 'orderDirection': 'asc' }).then(value => {
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
      this.losses = value;
      if (this.newLink === true) {
        this.createNewLinkForNewLoss();
      }
    }
    }).catch((response: HttpErrorResponse) => {
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
    let hazardLinks: HazardResponseDTO[] = [];
    const page: PageRequest = { from: 0, amount: 100 };
    this.allLinksForAutocomplete = { haz_links: [] };

    Promise.all(
      [this.lossDataService.getHazardsByLossId(this.projectId, data.entity.id, page),
      this.hazardDataService.getAllHazards(this.projectId, {})
      ])
      .then(value => {
        hazardLinks = value[0];
        this.allLinksForAutocomplete.haz_links = value[1];

        let id: string;
        if (this.loss) {
          id = this.loss.id;
        }

        if (this.sheetMode === SheetMode.Edit) {
          this.closeSheet(false, SheetMode.View);
        }

        this.sheetMode = DetailedSheetUtils.toggle(id, data.entity.id, this.sheetMode, data.mode);
        this.loss = {
          id: data.entity.id, description: data.entity.description, name: data.entity.name, state: data.entity.state,
          haz_links: hazardLinks,
          allLinksForAutocomplete: this.allLinksForAutocomplete
        };
      }).catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: DETAILEDSHEET_FAILED_LOADING_CHIPS, detail: response.message });
      });
  }

  closeSheet(isUnlocked: boolean = false, sheetMode: SheetMode = SheetMode.Closed): void {
    if (!isUnlocked && this.sheetMode === SheetMode.Edit) {
      const unlockDTO: UnlockRequestDTO = { id: this.loss.id, entityName: this.title.toLowerCase() };
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

  // method to create a new link or a new hazard
  private createNewLinkForNewLoss() {
    const promises = [];
    let altLossId = '';
    this.lossDataService.getAllLosses(this.projectId, {}).then(value => {
      altLossId = value.pop().id.toString();
      for (const newLossLink of this.newHazardLinks) {
        promises.push(this.lossDataService.createLossHazardLink(this.projectId, altLossId, newLossLink.id));
      }
      this.newHazardLinks = [];
    });
    this.newLink = false;
  }

}
