import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, Input, OnChanges, OnInit, Output } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MessageService } from 'primeng/api';
import { ControllerDataService } from 'src/app/services/dataServices/control-structure/controller-data.service';
import { ProcessModelDataService, ProcessModelDTO, ProcessModelResponseDTO } from 'src/app/services/dataServices/control-structure/process-model-data.service';
import { ProcessVariableDataService, ProcessVariableRequestDTO, ProcessVariableResponseDTO } from 'src/app/services/dataServices/control-structure/process-variable-data.service';
import { LockService } from 'src/app/services/dataServices/lock.service';
import { ResponsibilityDataService } from 'src/app/services/dataServices/responsibility-data.service';
import { FilterService } from 'src/app/services/filter-service/filter.service';
import { BoxEntityResponseDTO, BoxRequestDTO, ControllerDTO, PROCESS_MODEL, PROCESS_VARIABLE, ResponsibilityResponseDTO, UnlockRequestDTO } from 'src/app/types/local-types';
import { SheetMode } from '../../detailed-sheet/detailed-sheet.component';
import { DetailedSheetUtils } from '../../detailed-sheet/utils/detailed-sheet-utils';
import { DDEvent, DetailData } from '../../entities/data';
import { DetailedField, FieldType } from '../../entities/detailed-field';
import { ContextMenuData } from '../cs-types';
import { FAILED_LOADING, CREATE_SUCCESSFUL, EDIT_FAILED, EDIT_SUCCESSFUL, PLEASE_CREATE_OBJECT } from 'src/app/globals';

interface AllLinksForAutoComplete {
  input_links: BoxEntityResponseDTO[];
  responsibility_links: ResponsibilityResponseDTO[];
  pm_links: ProcessModelResponseDTO[];
}

interface PVForDetailSheet {
  id: string;
  name: string;
  description: string;
  role: '' | 'DISCREET' | 'INDISCREET';
  diskret: string;
  nichtDiskret: string;
  valueStates: string[];
  pm_links: any[];
  input_links: BoxEntityResponseDTO[];
  responsibility_links: ResponsibilityResponseDTO[];
  state: string;
  allLinksForAutocomplete: AllLinksForAutoComplete;

}
interface ControllerLinksForAutoComplete {
  con_links: ControllerDTO[];

}
interface PMForDetailSheet {
  id: string;
  name: string;
  description: string;
  state: string;
  con_links: ControllerDTO[];
  allLinksForAutocomplete: ControllerLinksForAutoComplete;
}

@Component({
  selector: 'app-detail-oversheet',
  templateUrl: './detail-oversheet.component.html',
  styleUrls: ['./detail-oversheet.component.css']
})

export class DetailOversheetComponent implements OnChanges {
  @Input() transfer: ContextMenuData;
  @Input() isPm: boolean;
  @Input() pmRef: ProcessModelDTO;
  @Input() pvRef: ProcessVariableResponseDTO;
  @Input() selectedInput: BoxEntityResponseDTO;
  @Input() isNew: boolean = false;

  private projectId: string;
  @Output() create: EventEmitter<void> = new EventEmitter<void>();
  @Output() close: EventEmitter<ProcessModelDTO> = new EventEmitter<ProcessModelDTO>();
  private allCrLinksForAutocomplete: ControllerLinksForAutoComplete;
  id: string = '';
  conName: string = '';
  controller: ControllerDTO;
  finished: boolean = false;
  pv: PVForDetailSheet;
  pm: PMForDetailSheet;
  SheetMode: typeof SheetMode = SheetMode;
  sheetModePv: SheetMode = SheetMode.Closed;
  sheetModePm: SheetMode = SheetMode.Closed;
  fieldsPv: DetailedField[];
  fieldsPm: DetailedField[];
  controllername: string;
  selectedController: ControllerDTO;
  titlePv: string = DetailedSheetUtils.generateSheetTitle(PROCESS_VARIABLE);
  titlePm: string = DetailedSheetUtils.generateSheetTitle(PROCESS_MODEL);
  promises: any[] = [];
  test: string;
  selCont: ControllerDTO[] = [];
  dropValues: Map<string, string> = new Map<string, string>([]);
  private OwnChange: boolean = false;
  disableSaveButton: boolean = false;
  private allLinksForAutocomplete: AllLinksForAutoComplete;
  constructor(
    private readonly controllerDataService: ControllerDataService,
    private readonly filterService: FilterService,
    private readonly messageService: MessageService,
    private readonly processModelDataSevice: ProcessModelDataService,
    private readonly route: ActivatedRoute,
    private readonly lockService: LockService,
    private readonly processVariableDataService: ProcessVariableDataService,
    private readonly responsibilityDataService: ResponsibilityDataService,
  ) {
    this.dropValues.set('DISCREET', 'Discrete Variable');
    this.dropValues.set('INDISCREET', 'Indiscrete Variable');

    this.fieldsPm = [
      { title: 'ID', key: 'id', type: FieldType.Text, readonly: true },
      { title: 'Process Model name', key: 'name', type: FieldType.Text, readonly: false },
      { title: 'Description', key: 'description', type: FieldType.Text_Variable, readonly: false, minRows: 3, maxRows: 10 },
      {
        title: 'Controller Id ', key: 'con_links', type: FieldType.Chips_Single, readonly: true, listKey: 'id', displayShortName: 'C-',
        shortcutButton: { title: 'Go to Controllers', routerLink: (): string => 'project/' + this.transfer.projectId + '/controller/' }
      },
      { title: 'State', key: 'state', type: FieldType.StateButtonGroup, readonly: false },
    ];

    this.fieldsPv = [
      { title: 'ID', key: 'id', type: FieldType.Text, readonly: true },
      { title: 'Process Variable name', key: 'name', type: FieldType.Text, readonly: false },
      { title: 'Description', key: 'description', type: FieldType.Text_Variable, readonly: false, minRows: 3, maxRows: 10 },
      { title: 'Variable type', key: 'role', type: FieldType.Dropdown, readonly: false, dropDownText: 'Choose variable type...', values: this.dropValues },
      { title: 'Discrete Variable', key: 'diskret', type: FieldType.ButtonGroup, readonly: false, listKey: 'valueStates', hidden: true },
      { title: 'Indiscrete Variable', key: 'nichtDiskret', type: FieldType.Text, readonly: false, minRows: 3, maxRows: 10, hidden: true },
      {
        title: 'Process Model', key: 'pm_links', type: FieldType.Chips, readonly: false, listKey: 'id', displayShortName: 'PM-',
        shortcutButton: { title: 'Go to Process Models', routerLink: (): string => 'project/' + this.transfer.projectId + '/processmodel-table/' }
      },
      {
        title: 'Input Links', key: 'input_links', type: FieldType.Chips_Single, readonly: false, listKey: 'name', displayShortName: 'I-',
        shortcutButton: { title: 'Go to Control Structure', routerLink: (): string => 'project/' + this.transfer.projectId + '/control-structure-diagram/STEP2/' }
      },
      {
        title: 'Responsibilities', key: 'responsibility_links', type: FieldType.Chips_Single, readonly: false, listKey: 'id', displayShortName: 'R-',
        shortcutButton: { title: 'Go to Responsibilities', routerLink: (): string => 'project/' + this.transfer.projectId + '/responsibilities/' }
      },
      { title: 'State', key: 'state', type: FieldType.StateButtonGroup, readonly: false },
    ];

    this.pv = {
      id: '', name: '', description: '', role: '',
      diskret: '', nichtDiskret: '',
      valueStates: [],
      input_links: [],
      pm_links: [],
      responsibility_links: [],
      allLinksForAutocomplete: { responsibility_links: [], input_links: [], pm_links: [] },
      state: '',
    };

    this.pm = {
      id: '', name: '', description: '', con_links: [], allLinksForAutocomplete: { con_links: [] }, state: '',
    };
  }

  dropDownPv($event: DDEvent): void {
    this.fieldsPv.forEach((element: DetailedField) => {
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

  ngOnChanges(): void {
    this.finished = false;
    if (this.isPm === false) {
      this.loadSheetPV();
    }
    if (this.isPm === true) {
      this.loadSheetPM();
    }
  }

  // Method loads the content of the detail sheet for Process Variable
  loadSheetPV(): void {
    this.isPm = false;
    this.sheetModePv = SheetMode.New;
    this.disableSaveButton = false;

    this.allLinksForAutocomplete = { responsibility_links: [], input_links: [], pm_links: [] };
    this.controllerDataService.getControllerByBoxId(this.transfer.projectId, this.transfer.id)
      .then((value: ControllerDTO) => {
        this.selectedController = value;
        Promise.all([
          this.processModelDataSevice.getAllByControllerId(this.transfer.projectId, this.selectedController.id),
          this.responsibilityDataService.getResponsibilitiesByControllerId(this.transfer.projectId, this.selectedController.id),
          this.controllerDataService.getSourceBoxByControllerId(this.transfer.projectId, this.selectedController.id),
        ]).then((valueArray: [ProcessModelResponseDTO[], ResponsibilityResponseDTO[], BoxEntityResponseDTO[]]) => {

          this.allLinksForAutocomplete.pm_links = valueArray[0];
          this.allLinksForAutocomplete.responsibility_links = valueArray[1];
          this.allLinksForAutocomplete.input_links = valueArray[2];

          this.pv = {
            id: '',
            name: '',
            description: '',
            role: '',
            state: '',
            diskret: '',
            valueStates: [],
            nichtDiskret: '',
            input_links: [this.selectedInput],
            pm_links: [this.pmRef],
            responsibility_links: [],
            allLinksForAutocomplete: this.allLinksForAutocomplete
          };

          // wenn pvRef geliefert zeigt dieses an !
          if (!!this.pvRef && !this.isNew) {
            let selPm: ProcessModelResponseDTO[] = [];
            for (let ele of this.pvRef.process_models) {
              for (let pm of valueArray[0]) {
                if (pm.id === ele) {
                  selPm.push(pm);
                }
              }
            }

            let selResponsibility: ResponsibilityResponseDTO[] = [];
            for (let resp of valueArray[1]) {
              if (this.pvRef.responsibilityIds.length > 0 &&
                resp.id === this.pvRef.responsibilityIds[0]) {
                selResponsibility.push(resp);
              }
            }

            let selInput: BoxEntityResponseDTO[] = [];
            for (let input of valueArray[2]) {
              if (this.pvRef.source &&
                this.pvRef.source.id === input.id) {
                selInput.push(input);
              }
            }

            this.sheetModePv = SheetMode.View;
            this.pv = {
              ...this.pv,
              id: this.pvRef.id,
              description: this.pvRef.description,
              name: this.pvRef.name,
              state: this.pvRef.state,
              diskret: this.pvRef.variable_value,
              nichtDiskret: this.pvRef.variable_value,
              role: this.pvRef.variable_type,
              input_links: selInput,
              pm_links: selPm,
              valueStates: this.pvRef.variable_type === 'DISCREET' ? this.pvRef.valueStates : [],
              responsibility_links: selResponsibility,
              allLinksForAutocomplete: this.allLinksForAutocomplete
            };
            this.dropDownPv({ title: 'role', value: this.pv.role });
          }
        });
      }).catch((reason: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: FAILED_LOADING + ' Controller', detail: reason.message });
      });

  }

  loadSheetPM(): void {
    this.sheetModePm = SheetMode.New;
    this.allCrLinksForAutocomplete = { con_links: [] };
    this.pm = {
      id: '', name: '', description: '', con_links: [], allLinksForAutocomplete: { con_links: [] }, state: '',
    };
    this.controllerDataService.getControllerByBoxId(this.transfer.projectId, this.transfer.id)
      .then((value: ControllerDTO) => {
        this.selCont = [];
        this.selCont.push(value);
        this.allCrLinksForAutocomplete.con_links = this.selCont;
        this.pm = {
          id: '',
          name: '',
          description: '',
          con_links: this.selCont,
          allLinksForAutocomplete: this.allCrLinksForAutocomplete,
          state: '',
        };
        // wenn pmRef geliefert zeigt dieses an !
        if (!!this.pmRef && !this.isNew) {
          this.sheetModePm = SheetMode.View;
          this.pm = {
            ...this.pm,
            id: this.pmRef.id,
            description: this.pmRef.description,
            state: this.pmRef.state,
            name: this.pmRef.name,
          };
        }

      }).catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: FAILED_LOADING + ' entities for linking', detail: response.message });
      });
  }

  savePm(pm: DetailData): void {
    this.filterService.ClearSelectionEmitter.emit(true);
    this.OwnChange = true;
    this.disableSaveButton = true;
    if (this.pm.con_links.length === 0) {
      this.messageService.add({ severity: 'error', summary: 'ADD ControllerChip' });
      this.disableSaveButton = false;
      return;
    }
    if (pm.mode === SheetMode.New) {
      const value: ProcessModelDTO = {
        name: this.pm.name,
        description: this.pm.description,
        controllerId: this.pm.con_links[0].id,
        state: this.pm.state,
      };
      this.processModelDataSevice.create(this.transfer.projectId, value).then(() => {
        this.messageService.add({ severity: 'success', summary: CREATE_SUCCESSFUL });
      }).catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: EDIT_FAILED, detail: response.message });
      }).finally(() => {
        this.OwnChange = false;
        this.disableSaveButton = false;
        this.pmRef = {
          ...this.pmRef,
          description: this.pm.description,
          name: this.pm.name,
          id: this.pm.id,
          state: this.pm.state,
        };
        this.closeSheetPm(true);
        this.close.emit(this.pmRef);
        // this.loadData();
      });
    }

    if (pm.mode === SheetMode.EditWithLock) {
      const value: ProcessModelDTO = {
        name: pm.ent.name,
        description: pm.ent.description,
        controllerId: this.pm.con_links[0].id,
        state: pm.ent.state,
      };

      this.processModelDataSevice.update(this.transfer.projectId, pm.id.toString(), value).then(() => {
        this.messageService.add({ severity: 'success', summary: EDIT_SUCCESSFUL });
      }).catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: EDIT_FAILED, detail: response.message });
      }).finally(() => {
        this.OwnChange = false;
        this.disableSaveButton = false;
        this.pmRef = {
          ...this.pmRef,
          description: this.pm.description,
          name: this.pm.name
        };
        this.closeSheetPm();
        this.close.emit(this.pmRef);
      });
    }
  }

  savePv(pv: DetailData): void {
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

    let value: ProcessVariableRequestDTO = {
      name: this.pv.name,
      description: this.pv.description,
      source: inputLinks[0],
      variable_type: this.pv.role,
      variable_value: this.pv.role === 'DISCREET' ? this.pv.diskret : this.pv.nichtDiskret,
      currentProcessModel: this.pmRef.id,
      process_models: pmLinks,
      valueStates: this.pv.valueStates,
      responsibilityIds: respLinks,
      state: this.pv.state,
    };
    if (pmLinks.length === 0 || respLinks.length === 0 || inputLinks.length === 0) {
      this.messageService.add({ severity: 'warn', summary: PLEASE_CREATE_OBJECT + ' Responsibility' });
    } else {
      if (pv.mode === SheetMode.New) {
        this.processVariableDataService.createProcessVariable(this.transfer.projectId, this.selectedController.id, value)
          .then(() => {
            this.messageService.add({ severity: 'success', summary: CREATE_SUCCESSFUL });
            this.closeSheetPv(true);
          })
          .catch((response: HttpErrorResponse) => {
            this.messageService.add({ severity: 'error', summary: EDIT_FAILED, detail: response.message });
          })
          .finally(() => {
            this.OwnChange = false;
            this.disableSaveButton = false;
          });
      }

      if (pv.mode === SheetMode.EditWithLock) {
        this.processVariableDataService.alterProcessVariable(
          this.transfer.projectId,
          this.selectedController.id,
          this.pv.pm_links.findIndex((item: any) => item.id === this.pmRef.id) > -1 ? this.pmRef.id : this.pv.pm_links[0].id,
          this.pv.id,
          value
        )
          .then(() => {
            this.messageService.add({ severity: 'success', summary: EDIT_SUCCESSFUL });
          })
          .catch((response: HttpErrorResponse) => {
            this.messageService.add({ severity: 'error', summary: EDIT_FAILED, detail: response.message });
          })
          .finally(() => {
            this.closeSheetPv(true);
            this.OwnChange = false;
            this.disableSaveButton = false;
          });
      }
      this.fieldsPv.forEach((field: DetailedField) => {
        if (field.key === 'nichtDiskret' || field.key === 'diskret') {
          field.hidden = true;
        }
      });
    }
  }

  editSheetPm(): void {
    this.sheetModePm = SheetMode.Edit;
  }

  editSheetPv(): void {
    this.sheetModePv = SheetMode.Edit;
  }

  // closes the sheet
  closeSheetPm(isUnlocked: boolean = false, sheetMode: SheetMode = SheetMode.Closed): void {
    if (!isUnlocked && this.sheetModePm === SheetMode.Edit) {
      const unlockDTO: UnlockRequestDTO = { id: this.pm.id, entityName: this.titlePm.toLowerCase(), parentId: this.pm.con_links[0].id };
      this.lockService.unlockEntity(this.projectId, unlockDTO).then(() => {
        this.sheetModePm = sheetMode;
      }).catch(() => {
        this.sheetModePm = sheetMode;
      });
    } else {
      this.sheetModePm = sheetMode;
    }
    this.disableSaveButton = false;
    this.close.emit();
  }

  closeSheetPv(isUnlocked: boolean = false, sheetMode: SheetMode = SheetMode.Closed): void {
    if (!isUnlocked && this.sheetModePv === SheetMode.Edit) {
      const unlockDTO: UnlockRequestDTO = { id: this.pv.id, entityName: this.titlePv.toLowerCase(), parentId: this.selectedController.id };
      this.lockService.unlockEntity(this.projectId, unlockDTO).then(() => {
        this.sheetModePv = sheetMode;
      }).catch(() => {
        this.sheetModePv = sheetMode;
      });
    } else {
      this.sheetModePv = sheetMode;
    }
    this.disableSaveButton = false;
    this.fieldsPv.forEach((field: DetailedField) => {
      if (field.key === 'nichtDiskret' || field.key === 'diskret') {
        field.hidden = true;
      }
    });
    this.close.emit();
  }
}
