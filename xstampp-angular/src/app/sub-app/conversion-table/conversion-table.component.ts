import {Component, EventEmitter, OnDestroy, Output} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { MessageService } from 'primeng/api';
import { LockService, LockResponse } from '../../services/dataServices/lock.service';
import { SheetMode } from './../../common/detailed-sheet/detailed-sheet.component';
import { DetailedSheetUtils } from './../../common/detailed-sheet/utils/detailed-sheet-utils';
import { DetailedField, FieldType } from './../../common/entities/detailed-field';
import { ColumnType, TableColumn } from './../../common/entities/table-column';
import { AuthService } from './../../services/auth.service';
import { ControlActionDataService } from './../../services/dataServices/control-structure/control-action-data.service';
import { ActuatorDataService } from './../../services/dataServices/control-structure/actuator-data.service';
import { ConversionDataService } from './../../services/dataServices/conversion-data.service';
import { FilterService } from './../../services/filter-service/filter.service';
import { WebsocketService } from './../../services/websocket.service';
import {
    ActuatorDTO,
    ControlActionDTO,
    UnlockRequestDTO,
    ControlActionResponseDTO,
    ConversionResponseDTO,
    CONVERSION,
} from './../../types/local-types';
import { EXPECTED_ENTITY_NOT_NULL, DELETED_USER, ENTITY_FAILED_LOADING, DETAILEDSHEET_FAILED_LOADING_CHIPS, DETAILEDSHEET_SUCCESSFUL_EDIT, DETAILEDSHEET_FAILED_EDIT, DETAILEDSHEET_SUCCESSFUL_CREATION, DETAILEDSHEET_FAILED_CREATION, ENTITY_FAILED_DELETE, ENTITY_SUCCESSFUL_DELETE, CHANGED_STATE_FAILED, DETAILEDSHEET_EXPECTED_NAME_NOT_NULL, NO_USER } from './../../globals';
import { Hotkeys } from 'src/app/hotkeys/hotkeys.service';
import { IncompleteEntitiesService, incompleteEntityDTO } from 'src/app/services/dataServices/incomplete-entities-data.service';
import { DateAdapter } from '@angular/material';
import { UserDataService, UserDisplayRequestDTO } from 'src/app/services/dataServices/user-data.service';
import { Subscription } from 'rxjs';
import {SelectedEntity} from "../dependent-element-tree/dependent-elements-types.component";

interface ControlActionsForAutoComplete {
    links: ControlActionDTO[];
}

interface ConversionForDetailedSheet {
    id: string;
    conversion: string;
    actuatorId: string;
    actuatorName: string;
    links: ControlActionResponseDTO[];
    allLinksForAutocomplete: ControlActionsForAutoComplete;
    oldControlActionId: string;
    state: string;
}

@Component({
    selector: 'app-conversion-table',
    templateUrl: './conversion-table.component.html',
    styleUrls: ['./conversion-table.component.css']
})
export class ConversionTableComponent implements OnDestroy {

    SheetMode: typeof SheetMode = SheetMode;
    private projectId: string;
    conversions: ConversionResponseDTO[] = [];
    columns: TableColumn[] = [];
    conversion: ConversionForDetailedSheet;
    fields: DetailedField[];
    title: string = DetailedSheetUtils.generateSheetTitle(CONVERSION);
    navigationTableDefinitions = [];
    selectedActuator: ActuatorDTO;
    origNavData = [];
    allActuators = [];
    sheetMode: SheetMode = SheetMode.Closed;
    private allLinksForAutocomplete: ControlActionsForAutoComplete;
    private OwnChange: boolean = false;
    disableSaveButton: boolean = false;
    subscriptions: Subscription[] = [];
    autoCompleteWords: any[] = [];
    examples: string[] = [
        '- distance^2 - 5',
        '- sin(angle)',
        '- [5 < speed < 10] OR [speed = 0]',
        '- (obstacle OR red_traffic_light) AND [speed > 1.1 * safe_speed]'];
  selectConverationEntity: SelectedEntity<Object>;

  @Output() selectEntityMessage: any = new EventEmitter<SelectedEntity<Object>>();

    constructor(
        private readonly actuatorDataService: ActuatorDataService,
        private readonly controlActionDataService: ControlActionDataService,
        private readonly conversionDataService: ConversionDataService,
        private readonly route: ActivatedRoute,
        private readonly filterService: FilterService,
        private readonly messageService: MessageService,
        private readonly wsService: WebsocketService,
        private readonly authService: AuthService,
        private readonly lockService: LockService,
        private readonly incompleteEntityService: IncompleteEntitiesService,
        private readonly userDataService: UserDataService,
        private readonly hotkeys: Hotkeys) {

        this.subscriptions.push(this.hotkeys.addShortcut({ keys: 'shift.control.n', description: 'Create Conversion' }).subscribe(() => {
            this.createConversion();
        }));

        this.fields = [
            { title: 'Actuator', key: 'actuatorName', type: FieldType.Text, readonly: true },
            { title: 'ID', key: 'id', type: FieldType.Text, readonly: true },
            {
                title: 'Control Action', key: 'links', type: FieldType.Chips_Single, readonly: false, listKey: 'name',
                shortcutButton: { title: 'Go to Control Actions', routerLink: (): string => 'project/' + this.projectId + '/control-action/' }
            },
            { title: 'Conversion', key: 'conversion', type: FieldType.Text_Variable_With_Auto_Completion, readonly: false, minRows: 3, maxRows: 10 },
            { title: 'State', key: 'state', type: FieldType.StateButtonGroup, readonly: false },
        ];
        this.deleteConversion = this.deleteConversion.bind(this);
        this.route.parent.params.subscribe((value: any) => {
            this.projectId = value.id;
            this.initColumns();
            this.loadData();
        });

        // Websocket implementation
        this.authService.getProjectToken().then((token: string) => {
            this.wsService.connect('subscribe', CONVERSION, token, (data) => {
                this.loadData();
                if (this.OwnChange === false) {
                    this.messageService.add({ severity: 'info', summary: 'User ' + data.displayName + ' updated the data!' });
                }
            });
        }).catch((err: HttpErrorResponse) => {
            console.log(err);
        });
    }

    // Optional set lastTimeEdited
    private loadData(): void {
        // load Conversion into table if Controller is selected
        if (typeof this.selectedActuator !== 'undefined') {
          let userRequest: UserDisplayRequestDTO = new UserDisplayRequestDTO;
          let userId: string[] = [];
          let valueIndex: string[] = [];
            this.conversionDataService.getAllConversions(this.projectId, this.selectedActuator.id, {})
                .then((value: ConversionResponseDTO[]) => {
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
                  this.conversions = value;
                }


                }).catch((response: HttpErrorResponse) => {
                    this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_LOADING, detail: response.message });
                });
        } else {
            this.actuatorDataService.getAllActuators(this.projectId, { 'orderBy': 'id', 'orderDirection': 'asc' })
                .then((value: ActuatorDTO[]) => {
                    this.navigationTableDefinitions = [];
                    this.navigationTableDefinitions[0] = {
                        dataSource: value, style: { width: '100%' }, columnHeaderName: 'Actuator',
                        columnHeaderButton: { title: 'Go to Actuators', routerLink: 'project/' + this.projectId + '/actuator/' }
                    };
                    if (value !== null && value.length > 0) {
                        this.selectedActuator = value[0];
                        this.loadData();
                    } else {
                        this.navigationTableDefinitions[0].columnHeaderName = 'No Actuators found';
                        this.messageService.add({
                            severity: 'warn',
                            life: 10000, summary: 'You need to define an actuator first.'
                        });
                    }
                }).catch((response: HttpErrorResponse) => {
                    this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_LOADING, detail: response.message });
                });
        }
    }

    createConversion(): void {
        this.sheetMode = SheetMode.New;
        // necessary to pass empty element first, else old data will be loaded
        // since the request for allLinksForAutocomplete is asynchronous and takes time to load new data
        this.conversion = {
            id: '',
            conversion: '',
            actuatorId: this.selectedActuator.id,
            actuatorName: this.selectedActuator.name,

            links: [],
            allLinksForAutocomplete: { links: [] },
            oldControlActionId: '0',
            state: '',
        };
        this.controlActionDataService.getAllControlActions(this.projectId, {})
            .then((value: ControlActionDTO[]) => {
                this.conversion = {
                    id: '',
                    conversion: '',
                    actuatorId: this.selectedActuator.id,
                    actuatorName: this.selectedActuator.name,
                    links: [],
                    allLinksForAutocomplete: { links: value },
                    oldControlActionId: '0',
                    state: '',
                };
            }).catch((response: HttpErrorResponse) => {
                this.messageService.add({ severity: 'error', summary: DETAILEDSHEET_FAILED_LOADING_CHIPS, detail: response.message });
            });
        this.autoCompleteWords = [];
        this.controlActionDataService.getAllControlActions(this.projectId, {})
            .then((value: ControlActionDTO[]) => {
                for (const v of value) {
                    this.autoCompleteWords.push(v);
                }
            }).catch((response: HttpErrorResponse) => {
                this.messageService.add({ severity: 'error', summary: 'Error while loading control action names', detail: response.message });
            });
    }

    saveConversion(conversion): void {
        this.filterService.ClearSelectionEmitter.emit(true);
        this.OwnChange = true;
        this.disableSaveButton = true;
        if (!conversion.ent.conversion || conversion.ent.conversion.trim() === '') {
            this.messageService.add({ severity: 'warn', summary: 'Add Conversion' })
            this.disableSaveButton = false;
            return;
        }
        if (conversion.mode === SheetMode.EditWithLock) {

            const promises = [];
            const conversionData = {
                projectId: this.projectId,
                conversion: conversion.ent.conversion,
                controlActionId: conversion.ent.oldControlActionId,
                state: conversion.ent.state,
            };
            if (conversion.deletedMap.has('links')) {
                for (const newLink of conversion.deletedMap.get('links')) {
                    conversionData.controlActionId = '0';
                }
            }
            if (conversion.addedMap.has('links')) {
                for (const newLink of conversion.addedMap.get('links')) {
                    conversionData.controlActionId = newLink.id;
                }
            }
            promises.push(this.conversionDataService.editConversion(this.projectId, this.selectedActuator.id, conversion.ent.id, conversionData));

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

        } else if (conversion.mode === SheetMode.New) {
            let conversionData = {
                projectId: this.projectId,
                conversion: conversion.ent.conversion,
                controlActionId: '',
                state: conversion.ent.state,
            };
            if (conversion.addedMap.has('links')) {
                for (const newLink of conversion.addedMap.get('links')) {
                    conversionData.controlActionId = newLink.id;
                }
            }
            this.conversionDataService.createConversion(this.projectId, this.selectedActuator.id, conversionData)
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

    async deleteConversion($event): Promise<void> {
        this.OwnChange = true;
        const list: any[] = $event;
        if (list.length === 0) {
            return;
        }

        const promiseList = list.map(async (elem: any) => {
            await this.lockService.lockEntity(this.projectId, {
                id: elem.id,
                parentId: elem.actuatorId,
                entityName: CONVERSION,
                expirationTime: DetailedSheetUtils.calculateLockExpiration(),
            });
            return await this.conversionDataService.deleteConversion(this.projectId, this.selectedActuator.id, elem.id);
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
        this.selectedActuator = tupleOfElemAndTableNumber[0];
        this.navigationTableDefinitions[0].selectedElement = this.selectedActuator;
        this.loadData();
    }

    // data is an object passed from the main-table.ts
    // it is an object with {entity:,mode:} and entity was passed from here i.e. the conversion object
    toggleSheet(data): void {
        this.disableSaveButton = false;
        let id: string;
        if (this.conversion) {
            id = this.conversion.id;
        }
        if (this.sheetMode === SheetMode.Edit) {
            this.closeSheet(false, SheetMode.View);
        }
        let link = [];
        this.allLinksForAutocomplete = { links: [] };
        this.sheetMode = DetailedSheetUtils.toggle(id, data.entity.id, this.sheetMode, data.mode);

        // Default passed values while links are asychronously loaded
        this.conversion = {
            id: data.entity.id,
            conversion: data.entity.conversion,
            actuatorId: this.selectedActuator.id,
            actuatorName: this.selectedActuator.name,
            links: [],
            allLinksForAutocomplete: { links: [] },
            oldControlActionId: data.entity.controlActionId,
            state: data.entity.state
        };

        if (data.entity.controlActionId !== '0') {
            this.controlActionDataService.getControlActionById(this.projectId, data.entity.controlActionId)
                .then((value: ControlActionDTO) => {
                    link.push(value);
                })
                .catch((error: HttpErrorResponse) => {
                    if (error.message === EXPECTED_ENTITY_NOT_NULL) {
                        // leave link array empty, but do nothing else
                    } else {
                        console.log(error);
                        const notice = {
                            name: 'Error loading data',
                            id: data.entity.actuatorId
                        };
                        link.push(notice);
                    }
                });
        }
        this.controlActionDataService.getAllControlActions(this.projectId, {})
            .then((value: ControlActionDTO[]) => {
                this.allLinksForAutocomplete.links = value;

                this.conversion = {
                    id: data.entity.id,
                    conversion: data.entity.conversion,
                    actuatorId: this.selectedActuator.id,
                    actuatorName: this.selectedActuator.name,
                    links: link,
                    allLinksForAutocomplete: this.allLinksForAutocomplete,
                    oldControlActionId: data.entity.controlActionId,
                    state: data.entity.state,
                };
            }).catch((response: HttpErrorResponse) => {
                this.messageService.add({ severity: 'error', summary: DETAILEDSHEET_FAILED_LOADING_CHIPS, detail: response.message });
            });
        this.autoCompleteWords = [];
        this.controlActionDataService.getAllControlActions(this.projectId, {})
            .then((value: ControlActionDTO[]) => {
                for (const v of value) {
                    this.autoCompleteWords.push(v);
                }
            }).catch((response: HttpErrorResponse) => {
                this.messageService.add({ severity: 'error', summary: 'Error while loading control action names', detail: response.message });
            });
    }

  getSelectedEntities($event: any): void {
    this.selectConverationEntity = new SelectedEntity<Object>($event, this.title,
      this.projectId,
      false,
      false,
      true);
  }

    editSheet(): void {
        this.sheetMode = SheetMode.Edit;
    }

    closeSheet(isUnlocked: boolean = false, sheetMode: SheetMode = SheetMode.Closed): void {
        if (!isUnlocked && this.sheetMode === SheetMode.Edit) {
            const conversionUnlockDTO: UnlockRequestDTO = {
                id: this.conversion.id,
                parentId: this.conversion.actuatorId,
                entityName: CONVERSION
            };
            this.lockService.unlockEntity(this.projectId, conversionUnlockDTO).then(() => {
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
    changeEntity(event: { list: ConversionResponseDTO[], state: string }) {
        event.list.forEach((element: ConversionResponseDTO) => {

            let incomplete: incompleteEntityDTO = { entityName: 'conversion', parentId: element.actuatorId, state: event.state };

            this.incompleteEntityService.updateState(this.projectId, element.controlActionId, incomplete).then(() => {
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
                key: 'conversion',
                title: 'Conversion',
                type: ColumnType.Text,
                style: { 'width': '47%' }
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
            }, {
                key: 'lastEdited',
                title: 'Last-Edited',
                type: ColumnType.Date_Time,
                style: { 'width': '9%' }
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
            }, {
                key: 'graph',
                title: 'Graph',
                type: ColumnType.Button,
                style: { 'width': '3%' }
            }
        ];
    }

}
