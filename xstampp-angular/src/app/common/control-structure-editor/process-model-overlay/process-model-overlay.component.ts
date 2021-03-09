import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { MatExpansionPanel, MatDialog } from '@angular/material';
import { MessageService } from 'primeng/api';
import { ChangeDetectionService } from 'src/app/services/change-detection/change-detection-service.service';
import { ControllerDataService } from 'src/app/services/dataServices/control-structure/controller-data.service';
import { ProcessModelDataService, ProcessModelDTO } from 'src/app/services/dataServices/control-structure/process-model-data.service';
import { ProcessVariableDataService } from 'src/app/services/dataServices/control-structure/process-variable-data.service';
import { LockService } from 'src/app/services/dataServices/lock.service';
import { BoxEntityDTO, BoxEntityResponseDTO, ControllerDTO, ProcessVariableDTO, PROCESS_MODEL, PROCESS_VARIABLE } from 'src/app/types/local-types';
import { DetailedSheetUtils } from '../../detailed-sheet/utils/detailed-sheet-utils';
import { ContextMenuData } from '../cs-types';
import { CDSAddObject, CDSCanExecute } from 'src/app/services/change-detection/change-detection-decorator';
import { SaveActions, SaveActionDialogComponent } from '../../save-action-dialog/save-action-dialog.component';
import { map } from 'rxjs/operators';
import { CanDeactivateGuard, DeactivationMode } from 'src/app/services/change-detection/can-deactivate-guard.service';

@Component({
  selector: 'app-process-model-overlay',
  templateUrl: './process-model-overlay.component.html',
  styleUrls: ['./process-model-overlay.component.scss']
})

export class ProcessModelOverlayComponent implements OnInit {
  isPmDs: boolean;
  isNewPmOrPv: boolean = false;

  @Input() inputData: ContextMenuData;
  @Output() cancelClick: EventEmitter<void> = new EventEmitter<void>();
  cscontextmenuData: ContextMenuData;
  detailsheet: boolean = false;
  list: Array<number> = [];
  inputList: BoxEntityResponseDTO[] = [];
  variableList: ProcessVariableDTO[] = [];
  panelOpenState: boolean = false;
  pmList: Array<ProcessModelDTO> = [];
  inputSelect: BoxEntityDTO;
  selectPv: ProcessVariableDTO;
  pmSelect: ProcessModelDTO = {
    controllerId: undefined,
    description: undefined,
    name: 'Select Process Model',
    state: undefined,
  };
  selectedController: ControllerDTO;

  @ViewChild('exPanelInput', { static: true }) inputPanel: MatExpansionPanel;

  constructor(
    private readonly processModelService: ProcessModelDataService,
    private readonly messageService: MessageService,
    private readonly controllerDataService: ControllerDataService,
    private readonly processVariableDataService: ProcessVariableDataService,
    private readonly lockService: LockService,
    private readonly canDeactivate: CanDeactivateGuard,
    public dialog: MatDialog,
    private readonly cds: ChangeDetectionService, // don't delete class needs isntance of CDS
  ) { }

  ngOnInit(): void {
    this.cscontextmenuData = this.inputData;

    this.controllerDataService.getControllerByBoxId(this.inputData.projectId, this.inputData.id).then((value: ControllerDTO) => {
      this.selectedController = value;

      Promise.all([
        this.processModelService.getAllByControllerId(this.inputData.projectId, this.selectedController.id),
        this.controllerDataService.getSourceBoxByControllerId(this.inputData.projectId, this.selectedController.id)
      ])
        .then((response: [ProcessModelDTO[], BoxEntityResponseDTO[]]) => {
          this.pmList = response[0];
          this.inputList = response[1];
        })
        .catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: 'there is no Processmodel or BoxEntity to load please create a new one', detail: response.message });
        });

    }).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: 'failed getting controller', detail: response.message });
    });
  }

  openDetailsheetPm(event: MouseEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isPmDs = true;
    this.isNewPmOrPv = true;
    this.detailsheet = true;
  }

  openDetailsheetPv(input: BoxEntityDTO, event: MouseEvent): void {
    event.preventDefault();
    event.stopPropagation();
    if (!this.pmSelect || this.pmSelect.name === 'Select Process Model') {
      this.messageService.add({ severity: 'error', summary: 'Please choose ProcessModel first', });
    } else {
      this.inputSelect = input;
      this.isPmDs = false;
      this.isNewPmOrPv = true;
      this.detailsheet = true;
    }
  }

  onOverClose(pmRefReturn?: ProcessModelDTO): void {
    Promise.all([
      this.processModelService.getAllByControllerId(this.inputData.projectId, this.selectedController.id),
      this.controllerDataService.getSourceBoxByControllerId(this.inputData.projectId, this.selectedController.id),
    ])
      .then((response: [ProcessModelDTO[], BoxEntityResponseDTO[]]) => {
        this.pmList = response[0];
        this.inputList = response[1];

        if (!!pmRefReturn) {
          if (!!pmRefReturn.id && pmRefReturn.id.length > 0) {
            this.pmSelect = pmRefReturn;
          } else {
            // sets the pm when a new pm is generated
            this.pmSelect = response[0].find((ele: ProcessModelDTO) => ele.name === pmRefReturn.name && ele.description === pmRefReturn.description);
          }
        }
        this.detailsheet = false;
      })
      .catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: 'there is no Processmodel or BoxEntity to load please create a new one', detail: response.message });
      });
  }

  @CDSCanExecute('startDialog')
  onCancel(): void {
    this.isNewPmOrPv = false;
    this.cancelClick.emit();
  }

  startDialog(): void {
    const dialogRef = this.dialog.open(SaveActionDialogComponent, {
      width: '450px',
      disableClose: true,
      data: {
        isRouting: true,
      }
    });
    dialogRef.afterClosed().subscribe((result: SaveActions): void => {
      switch (result) {
        case SaveActions.CANCEL: {
          break;
        }
        case SaveActions.CLOSE: {
          this.cds.resetStates();
          this.canDeactivate.setDeactivationMode(DeactivationMode.ALLOWED);
          this.isNewPmOrPv = false;
          this.cancelClick.emit();
          break;
        }
        case SaveActions.SAVE_AND_CLOSE: {
          this.cds.resetStates();
          this.canDeactivate.setDeactivationMode(DeactivationMode.ALLOWED);
          this.isNewPmOrPv = false;
          this.cancelClick.emit();
          break;
        }
      }
    });
  }

  onInputClicked(event: MouseEvent, input: BoxEntityDTO, exPanel: MatExpansionPanel): void {
    event.preventDefault();
    event.stopPropagation();
    this.inputSelect = input;

    if (this.pmSelect.name !== 'Select Process Model') {
      this.processVariableDataService.getProcessVariablesBySource(this.inputData.projectId, this.inputSelect.id, this.pmSelect.id)
        .then((value: ProcessVariableDTO[]) => {
          if (value.length !== 0) {
            this.variableList = value;
          } else {
            exPanel.close();
            this.variableList = [];
            this.messageService.add({ severity: 'warn', summary: 'Please create Process Variable first', });
          }
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: 'Please create ProcessVariable', detail: response.message });
        });
    } else {
      this.variableList = [];
      exPanel.close();
      this.messageService.add({ severity: 'error', summary: 'Please choose ProcessModel first', });
    }
  }

  onPmItemClick(pm: ProcessModelDTO, exPanel: MatExpansionPanel, exPanelInput: MatExpansionPanel): void {
    exPanel.close();
    if (this.inputPanel) {
      this.inputPanel.close();
    }
    this.pmSelect = pm;
    this.isPmDs = true;
    this.isNewPmOrPv = false;
    this.detailsheet = true;
  }

  onInputItemClick(variable: ProcessVariableDTO, exPanel: MatExpansionPanel): void {
    exPanel.close();
    this.selectPv = variable;
    this.isPmDs = false;
    this.isNewPmOrPv = false;
    this.detailsheet = true;
  }

  /**
  * delets the selected process-variables
  */
  async deletePv(event: MouseEvent, $event: ProcessVariableDTO): Promise<boolean> {
    event.stopPropagation();
    const elem: ProcessVariableDTO = $event;

    await this.lockService.lockEntity(
      this.inputData.projectId,
      {
        id: elem.id,
        entityName: DetailedSheetUtils.generateSheetTitle(PROCESS_VARIABLE).toLowerCase(),
        expirationTime: DetailedSheetUtils.calculateLockExpiration()
      });
    return await this.processVariableDataService.deleteProcessVariable(
      this.inputData.projectId,
      elem.id
    )
      .then((value: boolean): boolean => {
        if (value) {
          this.messageService.add({ severity: 'success', summary: 'Deletion successfully' });
          this.onOverClose(this.pmSelect);
        } else {
          this.messageService.add({ severity: 'error', summary: 'Error on deleting process variable' });
        }
        return value;
      })
      .catch();
  }

  async deletePm(event: MouseEvent, $event: ProcessModelDTO): Promise<boolean> {
    event.stopPropagation();
    if (this.pmSelect = $event) {
      this.pmSelect = {
        controllerId: undefined,
        description: undefined,
        name: 'Select Process Model',
        state: undefined,
      };
    }
    const list: any[] = [];
    list.push($event);
    if (list.length === 0) {
      return;
    }

    const promiseList = list.map(async (elem: any) => {
      await this.lockService.lockEntity(this.inputData.projectId, {
        id: elem.id,
        entityName: DetailedSheetUtils.generateSheetTitle(PROCESS_MODEL).toLowerCase(),
        expirationTime: DetailedSheetUtils.calculateLockExpiration()
      });
      return await this.processModelService.delete(this.inputData.projectId, elem.id);
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
        this.messageService.add({ severity: 'error', summary: 'Error on deleting process model', detail: response.message });
      }
    }
    if (failedResponses.length < list.length) {
      /* some must have worked, we have fewer errors than requests */
      this.messageService.add({ severity: 'success', summary: 'Deletion successfully' });
    }
    this.onOverClose();
    return null;
  }

  /**
   * Listener shortcut button, opens the selected
   * view in a new browser tab.
   * @param selection The selected view
   */
  onClickedShortcutButton(selection: string): void {
    const linkMap = {
      'model': '/processmodel-table',
      'variable': '/processvariable-table'
    };
    window.open('project/' + this.inputData.projectId + linkMap[selection], '_blank');
  }
}
