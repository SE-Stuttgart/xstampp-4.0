import { incompleteEntityDTO, IncompleteEntitiesService } from 'src/app/services/dataServices/incomplete-entities-data.service';
import { Component, EventEmitter, OnDestroy, Output } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MessageService } from 'primeng/api';
import { TableColumn } from 'src/app/common/entities/table-column';
import { SheetMode } from '../../common/detailed-sheet/detailed-sheet.component';
import { DetailedSheetUtils } from '../../common/detailed-sheet/utils/detailed-sheet-utils';
import { FieldType } from '../../common/entities/detailed-field';
import { ColumnType } from '../../common/entities/table-column';
import { ControllerDataService } from '../../services/dataServices/control-structure/controller-data.service';
import { LockService } from '../../services/dataServices/lock.service';
import { ResponsibilityDataService } from '../../services/dataServices/responsibility-data.service';
import { FilterService } from '../../services/filter-service/filter.service';
import { DetailedField } from './../../common/entities/detailed-field';
import { AuthService } from './../../services/auth.service';
import { WebsocketService } from './../../services/websocket.service';
import { RESPONSIBILITY, ResponsibilityResponseDTO, UnlockRequestDTO, SystemConstraintResponseDTO, ControllerDTO, ResponsibilityFilterRequestDTO, ResponsibilityCreationDTO, ResponsibilityFilterPreviewResponseDTO, ResponsibilityRequestDTO } from './../../types/local-types';
import { HttpErrorResponse } from '@angular/common/http';
import { SystemLevelSafetyConstraintDataService } from 'src/app/services/dataServices/system-level-safety-constraint-data.service';
import { FilteringTableColumn, FilteringTableElement } from '../../common/filtering-table/filtering-table.component';
import { Subject, Subscription } from 'rxjs';
import { DetailData } from 'src/app/common/entities/data';
import { UserDataService, UserDisplayRequestDTO } from 'src/app/services/dataServices/user-data.service';
import { Hotkeys } from 'src/app/hotkeys/hotkeys.service';
import { ENTITY_FAILED_LOADING, DELETED_USER, CHANGED_STATE_FAILED, DETAILEDSHEET_FAILED_CREATION, DETAILEDSHEET_SUCCESSFUL_CREATION, DETAILEDSHEET_FAILED_EDIT, DETAILEDSHEET_SUCCESSFUL_EDIT, DETAILEDSHEET_EXPECTED_CHIPFIELDS_NOT_NULL, ENTITY_SUCCESSFUL_DELETE, ENTITY_FAILED_DELETE, DETAILEDSHEET_EXPECTED_NAME_NOT_NULL, NO_USER } from 'src/app/globals';
import { SelectedEntity } from '../dependent-element-tree/dependent-elements-types.component';

interface LinksForAutocomplete {
  cont_links: ControllerDTO[];
  sc_links: SystemConstraintResponseDTO[];
}

interface ResponsibilityForDetailedSheet {
  id: string;
  name: string;
  description: string;
  parentId: string;
  cont_links: ControllerDTO[];
  sc_links: SystemConstraintResponseDTO[];
  allLinksForAutocomplete: LinksForAutocomplete;
  state: string;
}

@Component({
  selector: 'app-responsibility',
  templateUrl: './responsibility.component.html',
  styleUrls: ['./responsibility.component.css']
})
export class ResponsibilityComponent implements OnDestroy {

  private readonly SELECT_ALL_TEXT: string = 'Select all';

  SheetMode: typeof SheetMode = SheetMode;
  private projectId: string;
  responsibilities: ResponsibilityResponseDTO[] = [];
  columns: TableColumn[] = [];
  detailedColumns: TableColumn[] = [];
  previewNumbers: Map<FilteringTableElement, number>[];
  responsibility: ResponsibilityForDetailedSheet;
  private allLinksForAutocomplete: LinksForAutocomplete;
  sheetMode: SheetMode = SheetMode.Closed;
  detailedSheetFields: DetailedField[];
  detailedSheetTitle: string = DetailedSheetUtils.generateSheetTitle(RESPONSIBILITY);
  navigationTableDefinitions: FilteringTableColumn[] = [];
  selectedController: ControllerDTO;
  selectedConstraint: SystemConstraintResponseDTO;
  private OwnChange: boolean = false;
  disableSaveButton: boolean = false;
  updateSelectedItemSubject: Subject<{ element: FilteringTableElement, columnNumber: number }> =
    new Subject<{ element: FilteringTableElement, columnNumber: number }>();
  subscriptions: Subscription[] = [];
  selectResponsibilityEntity: SelectedEntity<Object>;

  @Output() selectEntityMessage: any = new EventEmitter<SelectedEntity<Object>>();

  constructor(
    private readonly responsibilityDataService: ResponsibilityDataService,
    private readonly controllerDataService: ControllerDataService,
    private readonly constraintDataService: SystemLevelSafetyConstraintDataService,
    private readonly route: ActivatedRoute,
    private readonly filterService: FilterService,
    private readonly messageService: MessageService,
    private readonly wsService: WebsocketService,
    private readonly authService: AuthService,
    private readonly lockService: LockService,
    private readonly userDataService: UserDataService,
    private readonly incompleteEntityService: IncompleteEntitiesService,
    private readonly hotkeys: Hotkeys) {

    this.subscriptions.push(this.hotkeys.addShortcut({ keys: 'shift.control.n', description: 'Create Responsibility' }).subscribe(() => {
      this.createResponsibility();
    }));
    this.detailedSheetFields = [
      { title: 'ID', key: 'id', type: FieldType.Text, readonly: true },
      { title: 'Name', key: 'name', type: FieldType.Text, readonly: false },
      { title: 'Description', key: 'description', type: FieldType.Text_Variable, readonly: false, minRows: 3, maxRows: 10 },
      {
        title: 'Constraints', key: 'sc_links', type: FieldType.Chips, readonly: false, listKey: 'id', displayShortName: 'SC-',
        shortcutButton: { title: 'Go to System Constraints', routerLink: (): string => 'project/' + this.projectId + '/system-level-safety-constraints/' }
      },
      {
        title: 'Controller', key: 'cont_links', type: FieldType.Chips_Single, readonly: false, listKey: 'id', displayShortName: 'CTR-',
        shortcutButton: { title: 'Go to Controllers', routerLink: (): string => 'project/' + this.projectId + '/controller/' }
      },
      { title: 'State', key: 'state', type: FieldType.StateButtonGroup, readonly: false },
    ];
    this.deleteResponsibility = this.deleteResponsibility.bind(this);
    this.route.parent.params.subscribe((value: any) => {
      this.projectId = value.id;
      this.initColumns();
      this.loadData();
    });
    this.previewNumbers = new Array<Map<FilteringTableElement, number>>(2);

    // Websocket implementation
    this.authService.getProjectToken().then((token: string) => {
      this.wsService.connect('subscribe', RESPONSIBILITY, token, (data) => {
        this.loadData();
        if (this.OwnChange === false) {
          this.messageService.add({ severity: 'info', summary: 'User ' + data.displayName + ' updated the data!' });
        }
      });
    }).catch((err: HttpErrorResponse) => {
      console.log(err);
    });
  }

  onClickedFilterElement(tupleOfElemAndTableNumber: [FilteringTableElement, number]): void {
    this.filterService.ClearSelectionEmitter.emit(true);
    if (tupleOfElemAndTableNumber[1] === 0) {
      // First table was clicked
      this.selectedConstraint = tupleOfElemAndTableNumber[0] as SystemConstraintResponseDTO;
    } else if (tupleOfElemAndTableNumber[1] === 1) {
      // Second table was clicked
      this.selectedController = tupleOfElemAndTableNumber[0] as ControllerDTO;
    }
    this.loadData();
  }

  async deleteResponsibility($event): Promise<boolean> {
    this.OwnChange = true;
    const list: any[] = $event;
    if (list.length === 0) {
      return;
    }

    const promiseList = list.map(async (elem: any) => {
      await this.lockService.lockEntity(this.projectId, {
        id: elem.id,
        entityName: this.detailedSheetTitle.toLowerCase(),
        expirationTime: DetailedSheetUtils.calculateLockExpiration()
      });
      return this.responsibilityDataService.deleteResponsibility(this.projectId, elem.id);
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

  createResponsibility(): void {
    this.sheetMode = SheetMode.New;
    this.allLinksForAutocomplete = { cont_links: [], sc_links: [] };
    this.responsibility = {
      id: '', description: '', name: '',
      parentId: this.selectedController.id,
      cont_links: [],
      sc_links: [],
      allLinksForAutocomplete: this.allLinksForAutocomplete,
      state: '',
    };

    Promise.all([
      this.constraintDataService.getAllSafetyConstraints(this.projectId, {}),
      this.controllerDataService.getAllControllers(this.projectId, {})
    ])
      .then((value: [SystemConstraintResponseDTO[], ControllerDTO[]]) => {
        this.allLinksForAutocomplete.sc_links = value[0];
        this.allLinksForAutocomplete.cont_links = value[1];

        this.responsibility = {
          id: '', description: '', name: '',
          parentId: this.selectedController.id,
          cont_links: [],
          sc_links: [],
          allLinksForAutocomplete: this.allLinksForAutocomplete,
          state: '',
        };
      });
  }

  getSelectedEntities($event: any): void {
    this.selectResponsibilityEntity = new SelectedEntity<Object>($event, this.detailedSheetTitle, this.projectId,
      false,
      false,
      true);
  }

  saveResponsibility(responsibility: DetailData): void {
    this.filterService.ClearSelectionEmitter.emit(true);
    this.OwnChange = true;
    this.disableSaveButton = true;

    if (!responsibility.ent.name || responsibility.ent.name.trim() === '') {
      this.messageService.add({ severity: 'warn', summary: DETAILEDSHEET_EXPECTED_NAME_NOT_NULL })
      this.disableSaveButton = false;
      return;
    }

    if (responsibility.ent.sc_links.length === 0) {
      // If there are no system constraints in sc chip field
      this.messageService.add({ severity: 'error', summary: DETAILEDSHEET_EXPECTED_CHIPFIELDS_NOT_NULL });
      this.disableSaveButton = false;
    } else if (responsibility.ent.cont_links.length === 0) {
      // If there is no controller in controller chip field
      this.messageService.add({ severity: 'error', summary: DETAILEDSHEET_EXPECTED_CHIPFIELDS_NOT_NULL });
      this.disableSaveButton = false;
    } else if (responsibility.mode === SheetMode.EditWithLock) {

      const responsibilityEntity: ResponsibilityRequestDTO = {
        projectId: this.projectId, description: responsibility.ent.description, name: responsibility.ent.name,
        controllerId: responsibility.ent.cont_links[0].id, state: responsibility.ent.state,
      };

      const promises: Promise<ResponsibilityResponseDTO | boolean>[] = [];
      promises.push(this.responsibilityDataService.alterResponsibility(this.projectId, responsibility.ent.id, responsibilityEntity));

      // Create links for chips which have been added
      if (responsibility.addedMap.has('sc_links')) {
        for (const newSCLink of responsibility.addedMap.get('sc_links')) {
          promises.push(this.responsibilityDataService.createResponsibilitySCLink(this.projectId, String(responsibility.id), newSCLink.id));
        }
      }

      // Delete links of chips which have been removed
      if (responsibility.deletedMap.has('sc_links')) {
        for (const scLink of responsibility.deletedMap.get('sc_links')) {
          promises.push(this.responsibilityDataService.deleteResponsibilitySCLink(this.projectId, String(responsibility.id), scLink.id));
        }
      }

      // TODO more detailed Message for example which deletion or addition failed
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

    } else if (responsibility.mode === SheetMode.New) {
      const scIds: number[] = [];
      for (const systemConstraint of responsibility.ent.sc_links) {
        scIds.push(systemConstraint.id);
      }

      const responsibilityEntity: ResponsibilityCreationDTO = {
        projectId: this.projectId, description: responsibility.ent.description, name: responsibility.ent.name,
        controllerId: responsibility.ent.cont_links[0].id, systemConstraintIds: scIds, state: responsibility.ent.state,
      };

      this.responsibilityDataService.createResponsibility(this.projectId, responsibilityEntity)
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
    // Note Views with navigation table have checkbox(leftmost column) width  10% for aesthetic reasons
    this.columns = [
      {
        key: 'select',
        title: 'Select',
        type: ColumnType.Checkbox,
        style: { width: '10%' }
      }, {
        key: 'id',
        title: 'ID',
        type: ColumnType.Text,
        style: { width: '5%' }
      }, {
        key: 'name',
        title: 'Responsibility-Name',
        type: ColumnType.Text,
        style: { width: '55%' }
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
        style: { width: '8%' }
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

  changeEntity(event: { list: ResponsibilityResponseDTO[], state: string }) {
    event.list.forEach((element: ResponsibilityResponseDTO) => {
      let incomplete: incompleteEntityDTO = { entityName: 'responsibility', parentId: undefined, state: event.state };
      this.incompleteEntityService.updateState(this.projectId, element.id, incomplete).then(value => {
        this.loadData();
      }).catch((reason: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: CHANGED_STATE_FAILED, detail: reason.message });
      })
    });
  }

  private loadData(): void {
    if (this.selectedController !== undefined && this.selectedConstraint !== undefined) {

      // Load responsibilities according to selection (controllers & constraints)
      let filterRequest: ResponsibilityFilterRequestDTO = {
        systemConstraintId: this.selectedConstraint.id,
        controllerId: this.selectedController.id
      };
      let userRequest: UserDisplayRequestDTO = new UserDisplayRequestDTO;
      let userId: string[] = [];
      let valueIndex: string[] = [];
      this.responsibilityDataService.getResponsibilitiesByControllerAndSystemConstraintID(this.projectId, filterRequest)
        .then((value: ResponsibilityResponseDTO[]) => {
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
          this.responsibilities = value;
       } }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_LOADING, detail: response.message });
        });

      // Load preview numbers for the filtering table
      this.responsibilityDataService.getResponsibilityFilterPreview(this.projectId, filterRequest)
        .then((value: ResponsibilityFilterPreviewResponseDTO) => {
          // System Constraint column
          this.previewNumbers[0] = new Map<FilteringTableElement, number>();
          for (const element of this.navigationTableDefinitions[0].dataSource as SystemConstraintResponseDTO[]) {
            if (element.name === this.SELECT_ALL_TEXT) {
              this.previewNumbers[0].set(element, value.allSystemConstraintsPreview);
            } else if (value.systemConstraintIdToPreviewMap[element.id] !== undefined) {
              /* TODO: Replace value.systemConstraintIdToPreviewMap[element.id]
               * with value.systemConstraintIdToPreviewMap.get(element.id) as soon as
               * maps get correctly parsed as maps @Rico */
              this.previewNumbers[0].set(element, value.systemConstraintIdToPreviewMap[element.id]);
            } else {
              this.previewNumbers[0].set(element, 0);
            }
          }

          // Controller column
          this.previewNumbers[1] = new Map<FilteringTableElement, number>();
          for (const element of this.navigationTableDefinitions[1].dataSource as ControllerDTO[]) {
            if (element.name === this.SELECT_ALL_TEXT) {
              this.previewNumbers[1].set(element, value.allControllersPreview);
            } else if (value.controllerIdToPreviewMap[element.id] !== undefined) {
              /* TODO: Replace value.systemConstraintIdToPreviewMap[element.id]
               * with value.systemConstraintIdToPreviewMap.get(element.id) as soon as
               * maps get correctly parsed as maps @Rico */
              this.previewNumbers[1].set(element, value.controllerIdToPreviewMap[element.id]);
            } else {
              this.previewNumbers[1].set(element, 0);
            }
          }
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: 'Loading preview numbers failed', detail: response.message });
        });
    } else {
      if (this.selectedConstraint === undefined) {
        // Set up System Constraint & load data for it
        this.constraintDataService.getAllSafetyConstraints(this.projectId, {})
          .then((value: SystemConstraintResponseDTO[]) => {
            this.navigationTableDefinitions[0] = { dataSource: value, style: { width: '50%' }, columnHeaderName: 'Constraint' };
            if (value !== null && value.length > 0) {
              value.unshift({
                name: this.SELECT_ALL_TEXT, lastEdited: undefined, lastEditor: undefined, lastEditorId: undefined,
                lockExpirationTime: undefined, lockHolderDisplayName: undefined, lockHolderId: undefined,
                id: undefined, description: undefined, state: undefined
              });
              this.selectedConstraint = value[0];
              this.updateSelectedItemSubject.next({ element: value[0], columnNumber: 0 });
              this.loadData();
            } else {
              this.navigationTableDefinitions[0] = { dataSource: value, style: { width: '50%' }, columnHeaderName: 'No Constraints found' };
              this.messageService.add({ severity: 'warn', life: 10000, summary: 'Create a Constraint to be able to add responsibilities to it!' });
            }
            // Create the shortcut button to the System Constraint table
            this.navigationTableDefinitions[0].columnHeaderButton = {
              title: 'Go to System Constraints',
              routerLink: 'project/' + this.projectId + '/system-level-safety-constraints/'
            };
          }).catch((response: HttpErrorResponse) => {
          }).catch((response: HttpErrorResponse) => {
            this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_LOADING, detail: response.message });
          });
      }

      if (this.selectedController === undefined) {
        // Set up Controller table & load data for it
        this.controllerDataService.getAllControllers(this.projectId, {})
          .then((value: ControllerDTO[]) => {
            this.navigationTableDefinitions[1] = { dataSource: value, style: { width: '50%' }, columnHeaderName: 'Controller' };
            if (value !== null && value.length > 0) {
              value.unshift({ name: this.SELECT_ALL_TEXT, boxId: undefined, projectId: this.projectId });
              this.selectedController = value[0];
              this.updateSelectedItemSubject.next({ element: value[0], columnNumber: 1 });
              this.loadData();
            } else {
              this.navigationTableDefinitions[1] = { dataSource: value, style: { width: '50%' }, columnHeaderName: 'No Controller found' };
              this.messageService.add({ severity: 'warn', life: 10000, summary: 'Create a Controller to be able to add responsibilities to it!' });
            }
            // Create the shortcut button to the Controller table
            this.navigationTableDefinitions[1].columnHeaderButton = {
              title: 'Go to Controllers',
              routerLink: 'project/' + this.projectId + '/controller/'
            };
          }).catch((response: HttpErrorResponse) => {
            this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_LOADING, detail: response.message });
          });
      }
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
    const contLinks: ControllerDTO[] = [];
    let scLinks: SystemConstraintResponseDTO[] = [];
    this.allLinksForAutocomplete = { sc_links: [], cont_links: [] };

    Promise.all([
      this.constraintDataService.getAllSafetyConstraints(this.projectId, {}),
      this.constraintDataService.getLinkedSystemConstraintByResponsibility(this.projectId, data.entity.id),
      this.controllerDataService.getAllControllers(this.projectId, {}),
      this.controllerDataService.getControllerByResponsibilityId(this.projectId, data.entity.id)
    ])
      .then((value: [SystemConstraintResponseDTO[], SystemConstraintResponseDTO[], ControllerDTO[], ControllerDTO]) => {
        this.allLinksForAutocomplete.sc_links = value[0];
        this.allLinksForAutocomplete.cont_links = value[2];

        if (value[1].length > 0) {
          // If there are already system constraints belonging
          // to this responsibility show them in detail sheet
          scLinks = value[1];
        }

        if (value[3] !== null) {
          // If there is already a controller belonging
          // to this responsibility, show it in detail sheet
          contLinks.push(value[3]);
        }

        let id: string;
        if (this.responsibility) {
          id = this.responsibility.id;
        }

        if (this.sheetMode === SheetMode.Edit) {
          this.closeSheet(false, SheetMode.View);
        }

        this.sheetMode = DetailedSheetUtils.toggle(id, data.entity.id, this.sheetMode, data.mode);

        this.responsibility = {
          id: data.entity.id, name: data.entity.name, description: data.entity.description,
          parentId: this.selectedController.id,
          sc_links: scLinks,
          cont_links: contLinks,
          allLinksForAutocomplete: this.allLinksForAutocomplete,
          state: data.entity.state,
        };
      }).catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_LOADING, detail: response.message });
      });
  }

  closeSheet(isUnlocked: boolean = false, sheetMode: SheetMode = SheetMode.Closed): void {
    if (!isUnlocked && this.sheetMode === SheetMode.Edit) {
      const unlockDTO: UnlockRequestDTO = { id: this.responsibility.id, entityName: this.detailedSheetTitle.toLowerCase() };
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
