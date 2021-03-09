import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { FilterService } from '../../../services/filter-service/filter.service';
import {
  CONVERSION, HazardResponseDTO,
  ImplementationConstraintRequestDTO,
  ImplementationConstraintResponseDTO,
  LockRequestDTO,
  LossScenarioRequestDTO,
  LossScenarioResponseDTO, PageRequest,
  ResponsibilityRequestDTO,
  ResponsibilityResponseDTO, RULE,
  SUB_SYSTEM_CONSTRAINT, SubHazardResponseDTO, SubSystemConstraintResponseDTO,
  UcaResponseDTO,
} from '../../../types/local-types';
import { DetailedSheetUtils } from '../../../common/detailed-sheet/utils/detailed-sheet-utils';
import {
  ELEMENT_SUCCESFULLY_MOVED_MESSAGE,
  ENTITY_FAILED_DELETE,
} from '../../../globals';
import { HttpErrorResponse } from '@angular/common/http';
import { ControlActionDataService } from '../../../services/dataServices/control-structure/control-action-data.service';
import { RuleDataService } from '../../../services/dataServices/rule-data.service';
import { ResponsibilityDataService } from '../../../services/dataServices/responsibility-data.service';
import { ControllerDataService } from '../../../services/dataServices/control-structure/controller-data.service';
import { FeedbackDataService } from '../../../services/dataServices/control-structure/feedback-data.service';
import { SensorDataService } from '../../../services/dataServices/control-structure/sensor-data.service';
import { InputDataService } from '../../../services/dataServices/control-structure/input-data.service';
import { LossScenarioDataService } from '../../../services/dataServices/loss-scenario.service';
import { MessageService } from 'primeng/api';
import { BoxDataService } from '../../../services/dataServices/box-data.service';
import { LockService } from '../../../services/dataServices/lock.service';
import { ChangeDetectionService } from '../../../services/change-detection/change-detection-service.service';
import { UcaDataService } from '../../../services/dataServices/uca-data.service';
import { ControllerConstraintDataService } from '../../../services/dataServices/controller-constraint-data.service';
import { ImplementationConstraintDataService } from '../../../services/dataServices/implementation-constraint-data.service';
import { ConversionDataService } from '../../../services/dataServices/conversion-data.service';
import {
  ProcessModelDataService, ProcessModelDTO
} from '../../../services/dataServices/control-structure/process-model-data.service';
import { SubHazardDataService } from '../../../services/dataServices/sub-hazard-data.service';
import { SubSystemConstraintDataService } from '../../../services/dataServices/sub-system-constraint-data.service';
import { HazardDataService } from '../../../services/dataServices/hazard-data.service';
import {
  controlActionTitle, controlledProcessTitle,
  controllerConstraintTitle, controllerTitle, converationTitle, DependentElementNode, feedbackTitle,
  implementationConstraintTitle, implementationConstraintTitleNode,
  inputTitle, lossScenarioTitle, outputTitle, processModelTitle, responsibilityTitle, ruleTitle,
  subHazardTitle, subSystemConstraintTitle,
  systemConstraintTitle, ucaTitle
} from '../dependent-elements-types.component';

/**
 * This component allows to move dependent elements.
 * State of moved Elements is changed after moving.
 */
@Component({
  selector: 'app-move-entity',
  templateUrl: './move-entity.component.html',
  styleUrls: ['./move-entity.component.css']
})
export class MoveEntityComponent {

  projectId: string;
  entityTitle: string;
  indexOfSelectedElement: number;
  selectedDependecyNode: any;
  entitiesToMoveTo: any;
  isMoved: boolean = true;
  controlActionSelected: boolean = false;
  page: PageRequest = { from: 0, amount: 100 };
  allSubHazards: SubHazardResponseDTO[];
  showDeleteMessage: boolean = false;

  constructor(public filterService: FilterService,
              public dialogRef: MatDialogRef<MoveEntityComponent>,
              private readonly controlActionDataService: ControlActionDataService,
              private readonly ruleDataService: RuleDataService,
              private readonly responsibilityDataService: ResponsibilityDataService,
              private readonly controllerDataService: ControllerDataService,
              private readonly feedbackDataService: FeedbackDataService,
              private readonly sensorDataService: SensorDataService,
              private readonly inputDataService: InputDataService,
              private readonly lossScenarioDataService: LossScenarioDataService,
              private readonly messageService: MessageService,
              private readonly boxDataService: BoxDataService,
              private readonly lockService: LockService,
              private readonly cds: ChangeDetectionService,
              private readonly ucaDataService: UcaDataService,
              private readonly controllerConstraintDataService: ControllerConstraintDataService,
              private readonly implementationConstraintDataService: ImplementationConstraintDataService,
              private readonly processModelDataService: ProcessModelDataService,
              private readonly subHazardDataService: SubHazardDataService,
              private readonly subSystemDataService: SubSystemConstraintDataService,
              private readonly hazardDataService: HazardDataService,
              private readonly conversionDataService: ConversionDataService,
              @Inject(MAT_DIALOG_DATA) public data: any) {
    this.entitiesToMoveTo = this.data.entitiesToMoveTo;
    this.initMoveNode();
    this.checkControlStructureElements(this.entityTitle);
  }

  private initMoveNode(): void {
    this.allSubHazards = [];
    this.projectId = this.data.projectId;
    this.entityTitle = this.data.entityTitle;
    this.selectedDependecyNode = this.data.node;
    if (this.entityTitle === subSystemConstraintTitle) {
      this.getSubHazardsToMove();
    }
    if (this.selectedDependecyNode.controlStructureElement) {
      this.showDeleteMessage = true;
    }
    if (this.entityTitle === controlActionTitle) {
      this.controlActionSelected = true;
    }
  }

  selectEntity(): void {
    (<HTMLInputElement[]> <any> document.getElementsByName(this.entityTitle)).forEach((selectedEntity: HTMLInputElement): void => {
      if (selectedEntity.checked) {
        this.indexOfSelectedElement = selectedEntity.value as unknown as number;
      }
    });
  }

  onNoClick(): void {
    this.isMoved = false;
    this.dialogRef.close(this.isMoved);
  }

  moveEntity(): void {
    switch (this.entityTitle) {
      case ruleTitle: {
        this.moveRule();
        break;
      }
      case controlActionTitle: {
        this.moveControlAction();
        break;
      }
      case ucaTitle: {
        this.moveUCA();
        break;
      }
      case controllerConstraintTitle: {
        this.moveControllerConstraints();
        break;
      }
      case lossScenarioTitle: {
        this.moveLossScenario(this.selectedDependecyNode.entity);
        break;
      }
      case implementationConstraintTitle: {
        this.moveImplementationConstraint(this.selectedDependecyNode.entity);
        break;
      }
      case responsibilityTitle: {
        this.moveResponsibility(this.selectedDependecyNode.entity);
        break;
      }
      case processModelTitle: {
        this.moveProcessModel(this.selectedDependecyNode.entity);
        break;
      }
      case subHazardTitle: {
        this.moveSubHazard(this.selectedDependecyNode.entity);
        break;
      }
      case subSystemConstraintTitle: {
        if (this.selectedDependecyNode.entity.subHazardId !== '') {
          this.moveSubSystemConstraintToSubHazard(this.selectedDependecyNode.entity);
        } else {
          this.getSubHazardLink();
        }
        break;
      }
      case systemConstraintTitle: {
        this.getSubHazardLink();
        break;
      }
      case converationTitle: {
        this.moveConversionTable();
        break;
      }
      default: {
        this.dialogRef.close(true);
        break;
      }
    }
  }

  private succesfullyMoved(): void {
    this.messageService.add({ severity: 'success', summary: ELEMENT_SUCCESFULLY_MOVED_MESSAGE });
    this.dialogRef.close(this.isMoved);
    if (this.selectedDependecyNode.rootElement) {
      window.location.reload();
    }
  }

  private async moveConversionTable(): Promise<void> {
    let editList: any[] = [];
    editList.push(this.selectedDependecyNode.entity);

    const promiseList = editList.map(async (conversionDTO: any) => {
      await this.lockService.lockEntity(this.projectId, {
        id: conversionDTO.id,
        parentId: conversionDTO.actuatorId,
        entityName: CONVERSION,
        expirationTime: DetailedSheetUtils.calculateLockExpiration(),
      });
      return await this.conversionDataService.deleteConversion(this.projectId, conversionDTO.actuatorId, conversionDTO.id);
    });

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

    if (failedResponses.length < editList.length) {
      this.createConverationTable();
    }
  }

  private createConverationTable(): void {
    let conversionData = {
      projectId: this.projectId,
      conversion: this.selectedDependecyNode.entity.conversion,
      controlActionId: this.selectedDependecyNode.entity.controlActionId,
      state: 'TODO',
    };
    this.conversionDataService.createConversion(this.projectId, this.entitiesToMoveTo[this.indexOfSelectedElement].id, conversionData)
      .catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: null, detail: response.message });
      });
    this.succesfullyMoved();
  }

  private async moveRule(): Promise<void> {
    let editList: any[] = [];
    editList.push(this.selectedDependecyNode.entity);

    const promiseList = editList.map(async (ruleDTO: any) => {
      await this.lockService.lockEntity(this.projectId, {
        id: ruleDTO.id,
        parentId: ruleDTO.controllerId,
        entityName: ruleTitle.toLowerCase(),
        expirationTime: DetailedSheetUtils.calculateLockExpiration(),
      }).catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: null, detail: response.message });
      });
      return await this.ruleDataService.deleteRule(this.projectId, ruleDTO.controllerId, ruleDTO.id)
        .catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: null, detail: response.message });
        });
    });

    const failedDeletionResponses: Error[] = [];
    for (const prom of promiseList) {
      try {
        await prom;
      } catch (e) {
        failedDeletionResponses.push(e);
      }
    }

    if (failedDeletionResponses.length !== 0) {
      for (const response of failedDeletionResponses) {
        this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_DELETE, detail: response.message });
      }
    }

    if (failedDeletionResponses.length < editList.length) {
      this.createRule();
    }
  }

  private createRule(): void {
    let rule = {
      projectId: this.projectId,
      id: this.selectedDependecyNode.entity,
      rule: this.selectedDependecyNode.entity.rule,
      controlActionId: this.selectedDependecyNode.entity.controlActionId,
      state: 'TODO',
    };
    this.ruleDataService.createRule(this.projectId, this.entitiesToMoveTo[this.indexOfSelectedElement].id, rule)
      .then((value: boolean) => {
        this.succesfullyMoved();
      }).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: null, detail: response.message });
    });
  }

  private async moveControlAction(): Promise<void> {
    let editList: any[] = [];
    editList.push(this.selectedDependecyNode.entity);

    editList.map(async (controlAction: any) => {
      const ruleData = {
        projectId: this.projectId,
        id: controlAction.ruleId,
        rule: controlAction.rule,
        controlActionId: '0',
        state: controlAction.state,
      };

      await this.lockService.lockEntity(this.projectId, {
        id: controlAction.ruleId,
        parentId: controlAction.ruleControllerId,
        entityName: RULE,
        expirationTime: DetailedSheetUtils.calculateLockExpiration(),
      }).catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: null, detail: response.message });
      });
      return await this.ruleDataService.editRule(this.projectId, controlAction.ruleControllerId, controlAction.ruleId, ruleData)
        .catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: null, detail: response.message });
        });
    });

    let listToMove: any[] = [];
    listToMove.push(this.entitiesToMoveTo[this.indexOfSelectedElement]);
    listToMove.map(async (elem: any) => {
      const rule = {
        projectId: this.projectId,
        id: elem.id,
        rule: elem.rule,
        controlActionId: this.selectedDependecyNode.entity.id,
        state: 'TODO',
      };

      await this.lockService.lockEntity(this.projectId, {
        id: elem.id,
        parentId: elem.controllerId,
        entityName: RULE,
        expirationTime: DetailedSheetUtils.calculateLockExpiration(),
      });
      return await this.ruleDataService.editRule(this.projectId, elem.controllerId, elem.id, rule)
        .then((value: boolean) => {
          this.succesfullyMoved();
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: null, detail: response.message });
        });
    });
  }

  private async moveUCA(): Promise<void> {
    Promise.all([
      this.hazardDataService.getHazardsByUCAId(this.projectId, this.selectedDependecyNode.entity.uca.parentId, this.selectedDependecyNode.entity.uca.id, this.page),
      this.subHazardDataService.getHazardsByUCAId(this.projectId, this.selectedDependecyNode.entity.uca.parentId, this.selectedDependecyNode.entity.uca.id, this.page)
    ]).then((value: [HazardResponseDTO[], SubHazardResponseDTO[]]) => {
      this.getHazardAndSubHazardLinking(value[0], value[1]);
    });
  }

  private async getHazardAndSubHazardLinking(hazardResponseDTO?: HazardResponseDTO[], subHazardResponseDTO?: SubHazardResponseDTO[]): Promise<void> {
    let editList: any[] = [];
    editList.push(this.selectedDependecyNode.entity.uca);
    const promiseList = editList.map(async (ucaDTO: any) => {
      await this.lockService.lockEntity(this.projectId, {
        id: ucaDTO.id,
        entityName: this.entityTitle.toLowerCase(),
        expirationTime: DetailedSheetUtils.calculateLockExpiration(),
        parentId: ucaDTO.parentId
      }).catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: null, detail: response.message });
      });
      return await this.ucaDataService.deleteUca(this.projectId, ucaDTO.parentId, ucaDTO.id);
    });

    const failedDeletionResponses: Error[] = [];
    for (const prom of promiseList) {
      try {
        await prom;
      } catch (e) {
        failedDeletionResponses.push(e);
      }
    }

    if (failedDeletionResponses.length !== 0) {
      for (const response of failedDeletionResponses) {
        this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_DELETE, detail: response.message });
      }
    }

    if (failedDeletionResponses.length < editList.length) {
      this.deleteLossScenarien(hazardResponseDTO, subHazardResponseDTO);
    }
  }

  private async deleteLossScenarien(hazardResponseDTO?: HazardResponseDTO[], subHazardResponseDTO?: SubHazardResponseDTO[]): Promise<void> {
    let editList: any[] = [];
    editList = this.selectedDependecyNode.entity.lossScenarios;

    const promiseList = editList.map(async (lossScenarioDTO: any) => {
      await this.lockService.lockEntity(this.projectId, {
        id: lossScenarioDTO.entity.id,
        expirationTime: DetailedSheetUtils.calculateLockExpiration(),
        entityName: lossScenarioTitle.toLocaleLowerCase(),
      }).catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: null, detail: response.message });
      });
      return await this.lossScenarioDataService.deleteLossScenario(this.projectId, lossScenarioDTO.entity.id);
    });

    const failedDeletionResponses: Error[] = [];
    for (const prom of promiseList) {
      try {
        await prom;
      } catch (e) {
        failedDeletionResponses.push(e);
      }
    }

    if (failedDeletionResponses.length !== 0) {
      for (const response of failedDeletionResponses) {
        this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_DELETE, detail: response.message });
      }
    }

    this.createUCA(hazardResponseDTO, subHazardResponseDTO);
  }

  private createUCA(hazardResponseDTO?: HazardResponseDTO[], subHazardResponseDTO?: SubHazardResponseDTO[]): void {
    let controlActionId: string;
    controlActionId = this.entitiesToMoveTo[this.indexOfSelectedElement].id;

    const uca = {
      projectId: this.projectId, description: this.selectedDependecyNode.entity.uca.description,
      name: this.selectedDependecyNode.entity.uca.name,
      category: this.selectedDependecyNode.entity.uca.category,
      state: 'TODO', controllerId: this.selectedDependecyNode.entity.uca.controllerId,
    };

    this.ucaDataService.createUCA(this.projectId, controlActionId, uca)
      .then(() => {
        this.ucaDataService.getAllUcasByControlActionId(this.projectId, controlActionId, {})
          .then((ucas: UcaResponseDTO[]): void => {
            ucas.forEach((ucaResponseDTO: UcaResponseDTO): void => {
              if (ucaResponseDTO.name === uca.name) {
                this.initUCAElements(ucaResponseDTO, hazardResponseDTO, subHazardResponseDTO);
              }
            });
          });
      });
  }

  private async initUCAElements(uca: UcaResponseDTO, hazardResponseDTO?: HazardResponseDTO[], subHazardResponseDTO?: SubHazardResponseDTO[]): Promise<void> {
    let editList: any[] = [];
    editList.push(uca);
    const promiseList = editList.map(async (ucaDTO: any) => {
      await this.lockService.lockEntity(this.projectId, {
        id: ucaDTO.id,
        entityName: this.entityTitle.toLowerCase(),
        expirationTime: DetailedSheetUtils.calculateLockExpiration(),
        parentId: ucaDTO.parentId
      }).catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: null, detail: response.message });
      });

      if (this.selectedDependecyNode.entity.lossScenarios.length > 0) {
        this.selectedDependecyNode.entity.lossScenarios.map((lossNode: DependentElementNode): void => {
          let lsEntity: LossScenarioRequestDTO = {
            name: lossNode.entity.name,
            id: lossNode.entity.id,
            description: lossNode.entity.description,
            projectId: this.projectId,
            ucaId: uca.id,
            state: lossNode.entity.state,
            headCategory: lossNode.entity.headCategory,
            subCategory: lossNode.entity.subCategory,
            controller1Id: lossNode.entity.controller1Id,
            controller2Id: lossNode.entity.controller2Id,
            description1: lossNode.entity.description1,
            description2: lossNode.entity.description2,
            description3: lossNode.entity.description3,
            controlActionId: uca.parentId,
            inputArrowId: lossNode.entity.inputArrowId,
            feedbackArrowId: lossNode.entity.feedbackArrowId,
            inputBoxId: lossNode.entity.inputBoxId,
            sensorId: lossNode.entity.sensorId,
            reason: lossNode.entity.reason,
          };
          this.lossScenarioDataService.createLossScenario(this.projectId, lsEntity)
            .then((lossScenario: LossScenarioResponseDTO) => {
              if (lossNode.children[0]) {
                this.createImplementationConstraints(lossScenario, lossNode.children.slice(1, lossNode.children.length));
              }
            }).catch((response: HttpErrorResponse) => {
            this.messageService.add({ severity: 'error', summary: null, detail: response.message });
          });
        });
      }

      if (this.selectedDependecyNode.entity.controllerConstraints.length > 0) {
        const controllerConstraintEntity = {
          projectId: this.projectId,
          description: this.selectedDependecyNode.entity.description,
          name: this.selectedDependecyNode.entity.controllerConstraints[0].name,
          controlActionId: this.selectedDependecyNode.entity.controllerConstraints[0].controlActionId,
          state: this.selectedDependecyNode.entity.controllerConstraints[0].state,
        };
        this.controllerConstraintDataService.createControllerConstraint(this.projectId, ucaDTO.parentId, ucaDTO.id, controllerConstraintEntity);
      }

      if (hazardResponseDTO) {
        hazardResponseDTO.map((hazard: any): void => {
          this.ucaDataService.createUCAHazLink(this.projectId, ucaDTO.parentId, ucaDTO.id, hazard.id, {});
        });
      }

      if (subHazardResponseDTO) {
        subHazardResponseDTO.map((subHazard: SubHazardResponseDTO): void => {
          this.ucaDataService.createUCASubHazLink(this.projectId, ucaDTO.parentId, ucaDTO.id, subHazard.parentId, subHazard.id, {});
        });
      }
    });

    const failedDeletionResponses: Error[] = [];
    for (const prom of promiseList) {
      try {
        await prom;
      } catch (e) {
        failedDeletionResponses.push(e);
      }
    }

    if (failedDeletionResponses.length !== 0) {
      for (const response of failedDeletionResponses) {
        this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_DELETE, detail: response.message });
      }
    }

    if (failedDeletionResponses.length < editList.length) {
      this.succesfullyMoved();
    }
  }

  private async moveControllerConstraints(): Promise<void> {
    let editList: any[] = [];
    editList.push(this.selectedDependecyNode.entity);

    const promiseList = editList.map(async (controllerConstraint: any) => {
      await this.lockService.lockEntity(this.projectId, {
        id: controllerConstraint.ucaId,
        entityName: this.entityTitle.toLowerCase(),
        expirationTime: DetailedSheetUtils.calculateLockExpiration(),
        parentId: controllerConstraint.controlActionId
      }).catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: null, detail: response.message });
      });
      return await this.controllerConstraintDataService.deleteControllerConstraint(this.projectId, controllerConstraint.controlActionId, controllerConstraint.ucaId, controllerConstraint.ucaId);
    });

    const failedDeletionResponses: Error[] = [];
    for (const prom of promiseList) {
      try {
        await prom;
      } catch (e) {
        failedDeletionResponses.push(e);
      }
    }

    if (failedDeletionResponses.length !== 0) {
      for (const response of failedDeletionResponses) {
        this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_DELETE, detail: response.message });
      }
    }

    if (failedDeletionResponses.length < editList.length) {
      this.createControllerConstraint();
    }
  }

  private createControllerConstraint(): void {
    const controllerConstraint = {
      projectId: this.projectId,
      description: this.selectedDependecyNode.entity.description,
      name: this.selectedDependecyNode.entity.constraintName,
      controlActionId: this.entitiesToMoveTo[this.indexOfSelectedElement].parentId,
      state: 'TODO',
    };

    this.controllerConstraintDataService.createControllerConstraint(this.projectId,
      this.entitiesToMoveTo[this.indexOfSelectedElement].parentId,
      this.entitiesToMoveTo[this.indexOfSelectedElement].id, controllerConstraint).then(() => {
      this.succesfullyMoved();
    });
  }

  private getSubHazardLink(): void {
    if (this.allSubHazards.length > 0) {
      this.allSubHazards.forEach((subHazardResponseDTO: SubHazardResponseDTO): void => {
        this.subHazardDataService.getSubConstraintBySubHazardId(this.projectId, subHazardResponseDTO.parentId, subHazardResponseDTO.id)
          .then((subSystemConstraintResponseDTO: SubSystemConstraintResponseDTO) => {
            if (subSystemConstraintResponseDTO.id === this.selectedDependecyNode.entity.id) {
              this.moveSubSystemConstraintToSystemConstraint(subSystemConstraintResponseDTO, subHazardResponseDTO);
            } else {
              this.moveSubSystemConstraintToSystemConstraint(this.selectedDependecyNode.entity);
            }
          }).catch((): void => {
          this.moveSubSystemConstraintToSystemConstraint(this.selectedDependecyNode.entity);
        });
      });
    } else {
      //this.moveSubSystemConstraintToSystemConstraint(this.selectedDependecyNode.entity);
    }
  }

  private async moveSubSystemConstraintToSystemConstraint(subSystemConstraintResponseDTO: SubSystemConstraintResponseDTO, subHazardResponseDTO?: SubHazardResponseDTO): Promise<void> {
    let editList: any[] = [];
    editList.push(subSystemConstraintResponseDTO);

    const promiseList = editList.map(async (subSystemConstraint: any) => {
      await this.lockService.lockEntity(this.projectId, {
        id: subSystemConstraint.id,
        parentId: subSystemConstraint.parentId,
        entityName: SUB_SYSTEM_CONSTRAINT.toLowerCase(),
        expirationTime: DetailedSheetUtils.calculateLockExpiration()
      });

      return await this.subSystemDataService.deleteSubSystemConstraint(this.projectId, subSystemConstraint.parentId, subSystemConstraint.id)
        .catch((response: HttpErrorResponse) => {
        });
    });

    const failedDeletionResponses: Error[] = [];
    for (const prom of promiseList) {
      try {
        await prom;
      } catch (e) {
        failedDeletionResponses.push(e);
      }
    }

    if (failedDeletionResponses.length !== 0) {
      for (const response of failedDeletionResponses) {

      }
    }
    if (failedDeletionResponses.length === 0) {
      this.createSubSystemConstraint(subHazardResponseDTO);
    }
  }

  private createSubSystemConstraint(subHazard?: SubHazardResponseDTO): void {
    const safetyConstraint = {
      projectId: this.projectId,
      description: subHazard.description,
      name: this.selectedDependecyNode.entity.name,
    };

    this.subSystemDataService.createSubSystemConstraint(this.projectId, this.entitiesToMoveTo[this.indexOfSelectedElement].id, safetyConstraint)
      .then((subSystemConstraintResponseDTO: SubSystemConstraintResponseDTO): void => {
        if (subHazard) {
          this.subSystemDataService.createSubHazardSubSystemConstraintLink(this.projectId, subSystemConstraintResponseDTO.parentId, subSystemConstraintResponseDTO.id, subHazard.parentId, subHazard.id);
        }
      });

    this.succesfullyMoved();
  }

  private async moveSubSystemConstraintToSubHazard(subSystemConstraintResponseDTO: SubSystemConstraintResponseDTO): Promise<void> {
    let list: any[] = [];
    list.push(subSystemConstraintResponseDTO);
    const promiseList = list.map(async (subSystemConstraint: any) => {
      return await this.subSystemDataService.deleteSubSystemConstraintSubHazardLink(this.projectId,
        subSystemConstraintResponseDTO.parentId,
        subSystemConstraintResponseDTO.id,
        this.selectedDependecyNode.entity.hazardId,
        this.selectedDependecyNode.entity.subHazardId);
    });

    const failedDeletionResponses: Error[] = [];
    for (const prom of promiseList) {
      try {
        await prom;
      } catch (e) {
        failedDeletionResponses.push(e);
      }
    }

    if (failedDeletionResponses.length !== 0) {
      for (const response of failedDeletionResponses) {
        this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_DELETE, detail: response.message });
      }
    }

    if (failedDeletionResponses.length === 0) {
      this.subHazardDataService.getSubConstraintBySubHazardId(this.projectId,
        this.entitiesToMoveTo[this.indexOfSelectedElement].parentId,
        this.entitiesToMoveTo[this.indexOfSelectedElement].id).then((subSystem: SubSystemConstraintResponseDTO): void => {
          if (subSystem) {
            this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_DELETE, detail: subSystem.name });
            this.subSystemDataService.deleteSubSystemConstraintSubHazardLink(this.projectId,
              subSystem.parentId,
              subSystem.id,
              this.entitiesToMoveTo[this.indexOfSelectedElement].parentId,
              this.entitiesToMoveTo[this.indexOfSelectedElement].id).then(() => {
                this.subSystemDataService.createSubHazardSubSystemConstraintLink(this.projectId,
                  subSystemConstraintResponseDTO.parentId,
                  subSystemConstraintResponseDTO.id,
                  this.entitiesToMoveTo[this.indexOfSelectedElement].parentId,
                  this.entitiesToMoveTo[this.indexOfSelectedElement].id).then((): void => {
                  this.dialogRef.close(this.isMoved);
                }).catch((response: HttpErrorResponse) => {
                  this.messageService.add({ severity: 'error', summary: null, detail: response.message });
                });
              });
          } else {
            this.createSubHazardSubSystemConstraintLink(this.entitiesToMoveTo[this.indexOfSelectedElement], subSystemConstraintResponseDTO);
          }
        }).catch((response: HttpErrorResponse) => {
        this.createSubHazardSubSystemConstraintLink(this.entitiesToMoveTo[this.indexOfSelectedElement], subSystemConstraintResponseDTO);
      });
    }
  }

  private async createSubHazardSubSystemConstraintLink(subHazard: SubHazardResponseDTO, subSystemConstraint: SubSystemConstraintResponseDTO): Promise<void> {
    const promiseList: any[] = [];

    promiseList.push(this.subSystemDataService.createSubHazardSubSystemConstraintLink(this.projectId,
      subSystemConstraint.parentId,
      subSystemConstraint.id,
      subHazard.parentId,
      subHazard.id).then(() => {
    }));

    const failedDeletionResponses: Error[] = [];
    for (const prom of promiseList) {
      try {
        await prom;
      } catch (e) {
        failedDeletionResponses.push(e);
      }
    }

    if (failedDeletionResponses.length !== 0) {
      for (const response of failedDeletionResponses) {
        this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_DELETE, detail: response.message });
      }
    }

    if (failedDeletionResponses.length === 0) {
      this.dialogRef.close(this.isMoved);
    }
  }

  private async moveSubHazard(subHazardResponseDTO: any): Promise<void> {
    let list: any[] = [];
    list.push(subHazardResponseDTO);

    const promiseList = list.map(async (subHazard: any) => {
      await this.lockService.lockEntity(this.projectId, {
        id: subHazard.id,
        entityName: this.entityTitle.toLowerCase(),
        expirationTime: DetailedSheetUtils.calculateLockExpiration(),
        parentId: subHazard.hazardId
      }).catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: null, detail: response.message });
      });
      return await this.subHazardDataService.deleteSubHazard(this.projectId, subHazardResponseDTO.hazardId, subHazardResponseDTO.id);
    });

    const failedDeletionResponses: Error[] = [];
    for (const prom of promiseList) {
      try {
        await prom;
      } catch (e) {
        failedDeletionResponses.push(e);
      }
    }

    if (failedDeletionResponses.length !== 0) {
      for (const response of failedDeletionResponses) {
        this.messageService.add({ severity: 'error', summary: ENTITY_FAILED_DELETE, detail: response.message });
      }
    }

    if (failedDeletionResponses.length < list.length) {
      this.createSubHazard();
    }
  }

  private createSubHazard(): void {
    const subHazard = {
      projectId: this.projectId,
      description: this.selectedDependecyNode.entity.description,
      name: this.selectedDependecyNode.entity.name,
      state: 'TODO'
    };

    this.subHazardDataService.createSubHazard(this.data.projectId, this.entitiesToMoveTo[this.indexOfSelectedElement].id, subHazard)
      .then((subHazardResponseDTO: SubHazardResponseDTO) => {
        if (subHazardResponseDTO && this.selectedDependecyNode.children[1]) {
          this.createSubHazardSubSystemConstraintLink(subHazardResponseDTO, this.selectedDependecyNode.children[1].entity).then(() => {
            this.succesfullyMoved();
          });
        } else {
          this.succesfullyMoved();
        }
      });
  }

  private async moveResponsibility(responsibilityResponseDTO: ResponsibilityResponseDTO): Promise<void> {
    let responsibility: ResponsibilityRequestDTO = {
      projectId: responsibilityResponseDTO.projectId,
      controllerId: this.entitiesToMoveTo[this.indexOfSelectedElement].id,
      name: responsibilityResponseDTO.name,
      description: responsibilityResponseDTO.description,
      state: 'TODO'
    };

    await this.lockService.lockEntity(this.projectId, {
      id: responsibilityResponseDTO.id,
      entityName: responsibilityTitle.toLowerCase(),
      expirationTime: DetailedSheetUtils.calculateLockExpiration()
    });

    this.responsibilityDataService.alterResponsibility(responsibilityResponseDTO.projectId, responsibilityResponseDTO.id, responsibility)
      .then(() => {
        this.succesfullyMoved();
      });
  }

  private async moveProcessModel(processModel: ProcessModelDTO): Promise<void> {
    let processModelDTO: ProcessModelDTO = {
      id: processModel.id,
      name: processModel.name,
      description: processModel.description,
      controllerId: this.entitiesToMoveTo[this.indexOfSelectedElement].id,
      state: 'TODO'
    };

    await this.lockService.lockEntity(this.projectId, {
      id: processModel.id,
      entityName: processModelTitle.toLowerCase(),
      expirationTime: DetailedSheetUtils.calculateLockExpiration()
    });

    this.processModelDataService.update(this.data.projectId, processModelDTO.id, processModelDTO).then(() => {
      this.succesfullyMoved();
    });
  }

  private async createImplementationConstraints(lossScenario: LossScenarioResponseDTO, implementationConstraintNodes: DependentElementNode[]): Promise<void> {
    let promiseList: any;
    implementationConstraintNodes.map((implNode: DependentElementNode): void => {
      let implConstEntity: ImplementationConstraintRequestDTO = {
        projectId: this.projectId,
        id: implNode.entity.id,
        lossScenarioId: lossScenario.id,
        description: implNode.entity.description,
        name: implNode.entity.name,
        controllerConstraint: implNode.entity.controllerConstraint,
        state: implNode.entity.state,
      };

      promiseList = this.implementationConstraintDataService.createImplementationConstraint(this.projectId, implConstEntity)
        .catch((response: HttpErrorResponse) => {
          this.messageService.add({
            severity: 'error',
            summary: null,
            detail: 'Impl Failed Message ' + response.message
          });
        });
    });
  }

  private async moveLossScenario(lossScenarioResponseDTO: LossScenarioResponseDTO): Promise<void> {
    let lossScenario: LossScenarioRequestDTO = {
      name: lossScenarioResponseDTO.name,
      id: lossScenarioResponseDTO.id,
      description: lossScenarioResponseDTO.description,
      projectId: lossScenarioResponseDTO.projectId,
      ucaId: this.entitiesToMoveTo[this.indexOfSelectedElement].id,
      state: 'TODO',
      headCategory: lossScenarioResponseDTO.headCategory,
      subCategory: lossScenarioResponseDTO.subCategory,
      controller1Id: lossScenarioResponseDTO.controller1Id,
      controller2Id: lossScenarioResponseDTO.controller2Id,
      description1: lossScenarioResponseDTO.description,
      description2: lossScenarioResponseDTO.description2,
      description3: lossScenarioResponseDTO.description3,
      controlActionId: this.entitiesToMoveTo[this.indexOfSelectedElement].parentId,
      inputArrowId: lossScenarioResponseDTO.inputArrowId,
      feedbackArrowId: lossScenarioResponseDTO.feedbackArrowId,
      inputBoxId: lossScenarioResponseDTO.inputArrowId,
      sensorId: lossScenarioResponseDTO.sensorId,
      reason: lossScenarioResponseDTO.reason,
    };

    let lockEntity: LockRequestDTO = {
      id: lossScenarioResponseDTO.id,
      entityName: lossScenarioTitle.toLowerCase(),
      expirationTime: DetailedSheetUtils.calculateLockExpiration()
    };
    await this.lockService.lockEntity(lossScenario.projectId, lockEntity);

    this.lossScenarioDataService.alterLossScenario(lossScenario.projectId, lossScenario.id, lossScenario).then(() => {
      this.succesfullyMoved();
    });
  }

  private async moveImplementationConstraint(implementationConstraintDTO: ImplementationConstraintResponseDTO): Promise<void> {
    let implementationConstraint: ImplementationConstraintRequestDTO = {
      projectId: implementationConstraintDTO.projectId,
      id: implementationConstraintDTO.id,
      lossScenarioId: this.entitiesToMoveTo[this.indexOfSelectedElement].id,
      name: implementationConstraintDTO.name,
      description: implementationConstraintDTO.description,
      controllerConstraint: implementationConstraintDTO.controllerConstraint,
      state: 'TODO'
    };

    let lockEntity: LockRequestDTO = {
      id: implementationConstraintDTO.id,
      entityName: implementationConstraintTitle.toLowerCase(),
      expirationTime: DetailedSheetUtils.calculateLockExpiration()
    };
    await this.lockService.lockEntity(implementationConstraintDTO.projectId, lockEntity);

    this.implementationConstraintDataService.alterImplementationConstraint(implementationConstraint.projectId, implementationConstraint.id, implementationConstraint)
      .then(() => {
        this.succesfullyMoved();
      });
  }

  private getSubHazardsToMove(): void {
    this.hazardDataService.getAllHazards(this.projectId, {})
      .then((hazards: HazardResponseDTO[]): void => {
        hazards.forEach((hazard: HazardResponseDTO): void => {
          this.subHazardDataService.getAllSubHazards(this.projectId, hazard.id, {})
            .then((subHazards: SubHazardResponseDTO[]): void => {
              subHazards.map((sub: SubHazardResponseDTO): void => {
                this.allSubHazards.push(sub);
              });
            });
        });
      });
  }

  private checkControlStructureElements(entityTitle: string): void {
    if (entityTitle === inputTitle
      || entityTitle === outputTitle
      || entityTitle === controlledProcessTitle
      || entityTitle === controllerTitle
      || entityTitle === feedbackTitle) {
      this.showDeleteMessage = true;
    }
  }
}




