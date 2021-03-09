import {Component, Inject, Input, OnInit} from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material';
import { DetailedSheetUtils } from '../../common/detailed-sheet/utils/detailed-sheet-utils';
import {
  RULE, SUB_SYSTEM_CONSTRAINT,
} from '../../types/local-types';
import {
  actuatorTitle,
  controlActionTitle,
  controlledProcessTitle,
  controllerConstraintTitle,
  controllerTitle, converationTitle,
  DependentElementNode,
  feedbackTitle, hazardTitle,
  implementationConstraintTitle,
  inputTitle,
  lossScenarioTitle,
  outputTitle,
  processModelTitle,
  processVariableTitle,
  responsibilityTitle, ruleTitle,
  SelectedEntity,
  sensorTitle,
  stepTwoTitleElement,
  subHazardTitle,
  subSystemConstraintTitle,
  systemConstraintTitle,
  ucaTitle
} from './dependent-elements-types.component';
import { FilterService } from '../../services/filter-service/filter.service';
import { ControlActionDataService } from '../../services/dataServices/control-structure/control-action-data.service';
import { RuleDataService } from '../../services/dataServices/rule-data.service';
import { ResponsibilityDataService } from '../../services/dataServices/responsibility-data.service';
import { ProcessModelDataService } from '../../services/dataServices/control-structure/process-model-data.service';
import { ControllerDataService } from '../../services/dataServices/control-structure/controller-data.service';
import { FeedbackDataService } from '../../services/dataServices/control-structure/feedback-data.service';
import { SensorDataService } from '../../services/dataServices/control-structure/sensor-data.service';
import { InputDataService } from '../../services/dataServices/control-structure/input-data.service';
import { LossScenarioDataService } from '../../services/dataServices/loss-scenario.service';
import { MessageService } from 'primeng/api';
import { BoxDataService } from '../../services/dataServices/box-data.service';
import { LockService } from '../../services/dataServices/lock.service';
import { ChangeDetectionService } from '../../services/change-detection/change-detection-service.service';
import { UcaDataService } from '../../services/dataServices/uca-data.service';
import { ControllerConstraintDataService } from '../../services/dataServices/controller-constraint-data.service';
import { ImplementationConstraintDataService } from '../../services/dataServices/implementation-constraint-data.service';
import { SystemLevelSafetyConstraintDataService } from '../../services/dataServices/system-level-safety-constraint-data.service';
import { SubSystemConstraintDataService } from '../../services/dataServices/sub-system-constraint-data.service';
import { HazardDataService } from '../../services/dataServices/hazard-data.service';
import { SubHazardDataService } from '../../services/dataServices/sub-hazard-data.service';
import { ProcessVariableDataService } from '../../services/dataServices/control-structure/process-variable-data.service';
import { ActuatorDataService } from '../../services/dataServices/control-structure/actuator-data.service';
import { ControlStructureDataService } from '../../services/dataServices/control-structure-data.service';
import { DeleteDialogComponent } from '../../common/action-bar/action-bar.component';
import {
  ENTITY_FAILED_DELETE,
  ENTITY_SUCCESSFUL_DELETE,
  MOVE_ELEMENTS,
  MOVE_ELEMENTS_TO_NOT_DELETE,
  REALLY_TO_DELETE
} from '../../globals';
import { HttpErrorResponse } from '@angular/common/http';
import { OutputDataService } from '../../services/dataServices/control-structure/output-data.service';
import { ControlStructureEntitiesWrapperService } from '../../services/dataServices/control-structure/control-structure-entities-wrapper.service';
import { ControlledProcessDataService } from '../../services/dataServices/control-structure/controlled-process-data.service';

/**
 * This component creates and deletes the dependency tree.
 */
@Component({
  selector: 'app-dependent-element-tree',
  templateUrl: './dependent-element-tree.component.html',
  styleUrls: ['./dependent-element-tree.component.css']
})
export class DependentElementTreeComponent implements OnInit {

  private projectId: string;
  private controllerIsRoot: boolean = false;
  private ruleIsRoot: boolean = false;
  private controlActionIsRoot: boolean = false;
  private ucaIsRoot: boolean = false;
  private lossScenarioIsRoot: boolean = false;
  private processModelIsRoot: boolean = false;
  private controlledProcessIsRoot: boolean = false;
  private sensorIsRoot: boolean = false;
  private actuatorIsRoot: boolean = false;
  private feedbackIsRoot: boolean = false;
  private inputIsRoot: boolean = false;
  private outputIsRoot: boolean = false;
  private responsibilityIsRoot: boolean = false;
  private controllerConstraintIsRoot: boolean = false;
  private implementationConstraintIsRoot: boolean = false;
  selectedEntity: SelectedEntity<Object>;
  stepOneSelected: boolean = false;
  stepTwoSelected: boolean = false;
  controllerTreeSelected: boolean = false;
  controlStructureEntity: boolean = false;
  reloadWindow: boolean = false;

  constructor(
    public filterService: FilterService,
    public dialogRef: MatDialogRef<DependentElementTreeComponent>,
    public deleteDialog: MatDialog,
    private readonly controlActionDataService: ControlActionDataService,
    private readonly ruleDataService: RuleDataService,
    private readonly responsibilityDataService: ResponsibilityDataService,
    private readonly processModelDataService: ProcessModelDataService,
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
    private readonly systemConstraintService: SystemLevelSafetyConstraintDataService,
    private readonly subSystemConstraintDataService: SubSystemConstraintDataService,
    private readonly hazardDataService: HazardDataService,
    private readonly subHazardDataService: SubHazardDataService,
    private readonly processVariableDataService: ProcessVariableDataService,
    private readonly actuatorDataService: ActuatorDataService,
    private readonly controlStructureDataService: ControlStructureDataService,
    private readonly outputDataService: OutputDataService,
    private readonly csEntitesWrapper: ControlStructureEntitiesWrapperService,
    private readonly controlledProcessDataService: ControlledProcessDataService,
    @Inject(MAT_DIALOG_DATA) public data: any) {
  }

  ngOnInit(): void {
    this.controllerTreeSelected = this.data.selectedEntityData.controllerTreeSelected;
    this.stepOneSelected = this.data.selectedEntityData.stepOneSelected;
    this.stepTwoSelected = this.data.selectedEntityData.stepTwoSelected;
    this.selectedEntity = this.data.selectedEntityData;
    this.projectId = this.data.selectedEntityData.projectId;
    if (this.data.selectedEntityData.controlStructureEntity) {
      this.controlStructureEntity = this.data.selectedEntityData.controlStructureEntity;
    }
    this.sendMessage();
  }

  private sendMessage(): void {
    if (!this.selectedEntity.moveTree) {
      this.messageService.add({ severity: 'warn', detail: MOVE_ELEMENTS_TO_NOT_DELETE });
    } else {
      this.messageService.add({ severity: 'warn', detail: MOVE_ELEMENTS });
    }
  }

  private sendModelMessage(): void {
    this.messageService.add({ severity: 'warn', detail: 'Rework modelled Control Structure!' });
  }

  private deleteTree($event: DependentElementNode[]): void {
    let tree = $event;
    const dialogRef: MatDialogRef<DeleteDialogComponent, any> = this.deleteDialog.open(DeleteDialogComponent, {
      width: '245px',
      data: {
        description: REALLY_TO_DELETE,
        tree: true
      }
    });
    dialogRef.afterClosed().subscribe((deleteTree: boolean): void => {
      if (deleteTree) {
        this.deleteDependencies(tree);
      }
    });
  }

  private delete(): void {
    this.filterService.DeleteRequestEmitter.emit();
    this.filterService.ClearSelectionEmitter.emit(true);
    this.dialogRef.close();
    if (this.reloadWindow) {
      window.location.reload();
    }
  }

  private deleteDependencies(dependentElementTree: DependentElementNode[]): void {
    dependentElementTree.forEach((dependentElementNode: DependentElementNode): void => {
      switch (dependentElementNode.entityTitle) {
        case controllerTitle: {
          this.controllerIsRoot = true;
          this.deleteControllerNode(dependentElementNode);
          break;
        }
        case processModelTitle: {
          this.processModelIsRoot = true;
          this.deleteProcessModelNode(dependentElementNode);
          break;
        }
        case ruleTitle: {
          this.ruleIsRoot = true;
          this.deleteRuleNode(dependentElementNode);
          break;
        }
        case controlActionTitle: {
          this.controlActionIsRoot = true;
          this.deleteControlActionNode(dependentElementNode);
          break;
        }
        case ucaTitle: {
          this.ucaIsRoot = true;
          this.deleteUCANode(dependentElementNode);
          break;
        }
        case lossScenarioTitle: {
          this.lossScenarioIsRoot = true;
          this.deleteLossScenarioNode(dependentElementNode);
          break;
        }
        case inputTitle: {
          this.inputIsRoot = true;
          this.deleteInputNode(dependentElementNode);
          break;
        }
        case outputTitle: {
          this.outputIsRoot = true;
          this.deleteOutputNode(dependentElementNode);
          break;
        }
        case feedbackTitle: {
          this.feedbackIsRoot = true;
          this.deleteFeedbackNode(dependentElementNode);
          break;
        }
        case controlledProcessTitle: {
          this.controlledProcessIsRoot = true;
          this.deleteControlledProcessNode(dependentElementNode);
          break;
        }
        case sensorTitle: {
          this.sensorIsRoot = true;
          this.deleteSensorNode(dependentElementNode);
          break;
        }
        case actuatorTitle: {
          this.actuatorIsRoot = true;
          this.deleteActuatorNode(dependentElementNode);
          break;
        }
        case converationTitle: {
          this.reloadWindow = true;
          break;
        }
        default: {
          break;
        }
      }
    });

    if (!this.controlStructureEntity) {
      this.delete();
    } else {
      this.dialogRef.close();
    }
  }

  private async deleteControllerNode(dependentElementNode: DependentElementNode): Promise<void> {
    let list: any[] = [];
    list.push(dependentElementNode.entity);

    if (dependentElementNode.children.length > 0) {
      dependentElementNode.children.forEach((controllerNode: DependentElementNode): void => {
        switch (controllerNode.entityTitle) {
          case ruleTitle: {
            this.deleteRuleNode(controllerNode);
            break;
          }
          case processModelTitle: {
            this.deleteProcessModelNode(controllerNode);
            break;
          }
          case responsibilityTitle: {
            this.deleteResponsibilities(controllerNode);
            break;
          }
          case stepTwoTitleElement: {
            controllerNode.children.forEach((stepTwoNode: DependentElementNode): void => {
              if (stepTwoNode.entity) {
                switch (stepTwoNode.entityTitle) {
                  case controlActionTitle: {
                    this.deleteControlActionNode(stepTwoNode);
                    break;
                  }
                  case outputTitle: {
                    this.deleteOutputNode(stepTwoNode);
                    break;
                  }
                  case inputTitle: {
                    this.deleteInputNode(stepTwoNode);
                    break;
                  }
                  case feedbackTitle: {
                    this.deleteFeedbackNode(stepTwoNode);
                    break;
                  }
                }
              }
            });
            break;
          }
        }
      });
    }

    if (!this.controllerIsRoot || this.controlStructureEntity) {
      const promiseList = list.map(async (elem: any) => {
        await this.lockService.lockEntity(this.projectId, {
          id: elem.id,
          entityName: controllerTitle.toLocaleLowerCase(),
          expirationTime: DetailedSheetUtils.calculateLockExpiration(),
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: null, detail: response.message });
        });
        return await this.controllerDataService.deleteController(this.projectId, elem.id);
      });

      const failedResponses: Error[] = [];
      for (const prom of promiseList) {
        try {
          await prom;
        } catch (e) {
          failedResponses.push(e);
        }
      }
    }
  }

  private async deleteRuleNode(dependentElementNode: DependentElementNode): Promise<void> {
    let list: any[] = [];
    list.push(dependentElementNode.entity);

    if (dependentElementNode.children.length > 0) {
      dependentElementNode.children.forEach((ruleNode: DependentElementNode): void => {
        if (ruleNode.entity) {
          this.deleteControlActionNode(ruleNode);
        }
      });
    }

    if (!this.ruleIsRoot) {
     const promiseList = list.map(async (elem: any) => {
        await this.lockService.lockEntity(this.projectId, {
          id: elem.id,
          parentId: elem.controllerId,
          entityName: RULE,
          expirationTime: DetailedSheetUtils.calculateLockExpiration(),
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: null, detail: response.message });
        });
        return await this.ruleDataService.deleteRule(this.projectId, elem.controllerId, elem.id);
      });

      const failedResponses: Error[] = [];
      for (const prom of promiseList) {
        try {
          await prom;
        } catch (e) {
          failedResponses.push(e);
        }
      }
    }
  }

  private async deleteControlActionNode(dependentElementNode: DependentElementNode): Promise<void> {
    let list: any[] = [];
    list.push(dependentElementNode.entity);

    if (dependentElementNode.children.length > 0) {
      dependentElementNode.children.forEach((controlActionNode: DependentElementNode): void => {
        if (controlActionNode.entity) {
          switch (controlActionNode.entityTitle) {
            case ucaTitle: {
              this.deleteUCANode(controlActionNode);
              break;
            }
          }
        }
      });
    }

    if (!this.controlActionIsRoot || this.controlStructureEntity) {
      const promiseList = list.map(async (elem: any) => {
        await this.lockService.lockEntity(this.projectId, {
          id: elem.id,
          entityName: controlActionTitle.toLocaleLowerCase(),
          expirationTime: DetailedSheetUtils.calculateLockExpiration(),
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: null, detail: response.message });
        });
        return await this.controlActionDataService.deleteControlAction(this.projectId, elem.id);
      });

      const failedResponses: Error[] = [];
      for (const prom of promiseList) {
        try {
          await prom;
        } catch (e) {
          failedResponses.push(e);
        }
      }
    }
  }

  private async deleteUCANode(dependentElementNode: DependentElementNode): Promise<void> {
    let list: any[] = [];
    list.push(dependentElementNode.entity.uca);

    if (dependentElementNode.children.length > 0) {
      dependentElementNode.children.forEach((ucaNode: DependentElementNode): void => {
        if (dependentElementNode.entity) {
          switch (ucaNode.entityTitle) {
            case lossScenarioTitle: {
              this.deleteLossScenarioNode(ucaNode);
              break;
            }
            case controllerConstraintTitle: {
              this.deleteControllerConstraint(ucaNode);
              break;
            }
          }
        }
      });
    }

    if (!this.ucaIsRoot) {
      const promiseList = list.map(async (elem: any) => {
        await this.lockService.lockEntity(this.projectId, {
          id: elem.id,
          entityName: ucaTitle.toLowerCase(),
          expirationTime: DetailedSheetUtils.calculateLockExpiration(),
          parentId: elem.parentId
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: null, detail: response.message });
        });
        return await this.ucaDataService.deleteUca(this.projectId, elem.parentId, elem.id);
      });

      const failedResponses: Error[] = [];
      for (const prom of promiseList) {
        try {
          await prom;
        } catch (e) {
          failedResponses.push(e);
        }
      }
    }
  }

  private async deleteControllerConstraint(dependentElementNode: DependentElementNode): Promise<void> {
    let list: any[] = [];
    list.push(dependentElementNode.entity);

    if (!this.controllerConstraintIsRoot) {
      const promiseList = list.map(async (elem: any) => {
        await this.lockService.lockEntity(this.projectId, {
          id: elem.id,
          entityName: controllerConstraintTitle.toLowerCase(),
          expirationTime: DetailedSheetUtils.calculateLockExpiration(),
          parentId: elem.controlActionId
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: null, detail: response.message });
        });
        return await this.controllerConstraintDataService.deleteControllerConstraint(this.projectId, elem.controlActionId, elem.id, elem.id);
      });

      const failedResponses: Error[] = [];
      for (const prom of promiseList) {
        try {
          await prom;
        } catch (e) {
          failedResponses.push(e);
        }
      }
    }
  }

  private async deleteLossScenarioNode(dependentElementNode: DependentElementNode): Promise<void> {
    let list: any[] = [];
    list.push(dependentElementNode.entity);

    if (dependentElementNode.children.length > 0) {
      dependentElementNode.children.forEach((implementationNode: DependentElementNode): void => {
        if (implementationNode.entity) {
          this.deleteImplementationConstraintNode(implementationNode);
        }
      });
    }

    if (!this.lossScenarioIsRoot) {
      const promiseList = list.map(async (elem: any) => {
        await this.lockService.lockEntity(this.projectId, {
          id: elem.id,
          expirationTime: DetailedSheetUtils.calculateLockExpiration(),
          entityName: lossScenarioTitle.toLocaleLowerCase(),
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: null, detail: response.message });
        });
        return await this.lossScenarioDataService.deleteLossScenario(this.projectId, elem.id);
      });

      const failedResponses: Error[] = [];
      for (const prom of promiseList) {
        try {
          await prom;
        } catch (e) {
          failedResponses.push(e);
        }
      }
    }
  }

  private async deleteImplementationConstraintNode(dependentElementNode: DependentElementNode): Promise<void> {
    let list: any[] = [];
    list.push(dependentElementNode.entity);

    if (!this.implementationConstraintIsRoot) {
      const promiseList = list.map(async (elem: any) => {
        await this.lockService.lockEntity(this.projectId, {
          id: elem.id,
          entityName: implementationConstraintTitle.toLocaleLowerCase(),
          expirationTime: DetailedSheetUtils.calculateLockExpiration(),
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: null, detail: response.message });
        });
        return await this.implementationConstraintDataService.deleteImplementationConstraint(this.projectId, elem.id);
      });

      const failedResponses: Error[] = [];
      for (const prom of promiseList) {
        try {
          await prom;
        } catch (e) {
          failedResponses.push(e);
        }
      }
    }
  }

  private async deleteProcessModelNode(dependentElementNode: DependentElementNode): Promise<void> {
    let list: any[] = [];
    list.push(dependentElementNode.entity);

    if (dependentElementNode.children.length > 0) {
      dependentElementNode.children.forEach((processModelNode: DependentElementNode): void => {
        if (processModelNode.entity) {
          this.deleteProcessVariable(processModelNode);
        }
      });
    }

    if (!this.processModelIsRoot) {
      const promiseList = list.map(async (elem: any) => {
        if (elem) {
          await this.lockService.lockEntity(this.projectId, {
            id: elem.id,
            entityName: processModelTitle.toLowerCase(),
            expirationTime: DetailedSheetUtils.calculateLockExpiration()
          }).catch((response: HttpErrorResponse) => {
            this.messageService.add({ severity: 'error', summary: null, detail: response.message });
          });
          return await this.processModelDataService.delete(this.projectId, elem.id);
        }
      });

      const failedResponses: Error[] = [];
      for (const prom of promiseList) {
        try {
          await prom;
        } catch (e) {
          failedResponses.push(e);
        }
      }
    }
  }

  private async deleteProcessVariable(dependentElementNode: DependentElementNode): Promise<void> {
    let list: any[] = [];
    list.push(dependentElementNode.entity);

    const promiseList = list.map(async (elem: any) => {
      if (elem) {
        await this.lockService.lockEntity(this.projectId, {
          id: elem.id,
          entityName: processVariableTitle.toLowerCase(),
          expirationTime: DetailedSheetUtils.calculateLockExpiration()
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: null, detail: response.message });
        });
        return await this.processVariableDataService.deleteProcessVariable(this.projectId, elem.id);
      }

      const failedResponses: Error[] = [];
      for (const prom of promiseList) {
        try {
          await prom;
        } catch (e) {
          failedResponses.push(e);
        }
      }
    });
  }

  private async deleteResponsibilities(dependentElementNode: DependentElementNode): Promise<void> {
    let list: any[] = [];
    list.push(dependentElementNode.entity);

    if (!this.responsibilityIsRoot) {
     const promiseList = list.map(async (elem: any) => {
        await this.lockService.lockEntity(this.projectId, {
          id: elem.id,
          entityName: responsibilityTitle.toLocaleLowerCase(),
          expirationTime: DetailedSheetUtils.calculateLockExpiration(),
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: null, detail: response.message });
        });
        return await this.responsibilityDataService.deleteResponsibility(this.projectId, elem.id);
      });

      const failedResponses: Error[] = [];
      for (const prom of promiseList) {
        try {
          await prom;
        } catch (e) {
          failedResponses.push(e);
        }
      }
    }
  }

  private async deleteFeedbackNode(dependentElementNode: DependentElementNode): Promise<void> {
    let list: any[] = [];
    list.push(dependentElementNode.entity);

    if (!this.feedbackIsRoot) {
      const promiseList = list.map(async (elem: any) => {
        await this.lockService.lockEntity(this.projectId, {
          id: elem.id,
          entityName: feedbackTitle.toLowerCase(),
          expirationTime: DetailedSheetUtils.calculateLockExpiration(),
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: null, detail: response.message });
        });
        return await this.feedbackDataService.deleteFeedback(this.projectId, elem.id).catch((u: any): void => {
        });
        const failedResponses: Error[] = [];
        for (const prom of promiseList) {
          try {
            await prom;
          } catch (e) {
            failedResponses.push(e);
          }
        }
      });
    }
  }

  private async deleteOutputNode(dependentElementNode: DependentElementNode): Promise<void> {
    let list: any[] = [];
    list.push(dependentElementNode.entity);

    if (!this.outputIsRoot || this.controlStructureEntity) {
      const promiseList = list.map(async (elem: any) => {
        await this.lockService.lockEntity(this.projectId, {
          id: elem.id,
          entityName: outputTitle.toLowerCase(),
          expirationTime: DetailedSheetUtils.calculateLockExpiration(),
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: null, detail: response.message });
        });
        return await this.outputDataService.deleteOutput(this.projectId, elem.id);
      });

      const failedResponses: Error[] = [];
      for (const prom of promiseList) {
        try {
          await prom;
        } catch (e) {
          failedResponses.push(e);
        }
      }
    }
  }

  private async deleteSensorNode(dependentElementNode: DependentElementNode): Promise<void> {
    let list: any[] = [];
    list.push(dependentElementNode.entity);

    if (dependentElementNode.children.length > 0) {
      dependentElementNode.children.forEach((sensorNode: DependentElementNode): void => {
        if (sensorNode.entity) {
          if (sensorNode.entityTitle === feedbackTitle) {
            this.deleteFeedbackNode(sensorNode);
          }
        }
      });
    }

    if (!this.sensorIsRoot || this.controlStructureEntity) {
     const promiseList = list.map(async (elem: any) => {
        await this.lockService.lockEntity(this.projectId, {
          id: elem.id,
          entityName: sensorTitle.toLowerCase(),
          expirationTime: DetailedSheetUtils.calculateLockExpiration(),
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: null, detail: response.message });
        });
        return await this.sensorDataService.deleteSensor(this.projectId, elem.id);
      });

      const failedResponses: Error[] = [];
      for (const prom of promiseList) {
        try {
          await prom;
        } catch (e) {
          failedResponses.push(e);
        }
      }
    }
  }

  private async deleteControlledProcessNode(dependentElementNode: DependentElementNode): Promise<void> {
    let list: any[] = [];
    list.push(dependentElementNode.entity);

    this.messageService.add({ severity: 'error', summary: null, detail: dependentElementNode.children.length + ''});

    dependentElementNode.children.forEach((controrlledProcessNode: DependentElementNode): void => {
      if (controrlledProcessNode.entity) {
        switch (controrlledProcessNode.entityTitle) {
          case controlActionTitle: {
            this.deleteControlActionNode(controrlledProcessNode);
            break;
          }
          case outputTitle: {
            this.deleteOutputNode(controrlledProcessNode);
            break;
          }
          case inputTitle: {
            this.deleteInputNode(controrlledProcessNode);
            break;
          }
          case feedbackTitle: {
            this.deleteFeedbackNode(controrlledProcessNode);
            break;
          }
        }
      }
    });

    if (!this.controlledProcessIsRoot || this.controlStructureEntity) {
      const promiseList = list.map(async (elem: any) => {
        await this.lockService.lockEntity(this.projectId, {
          id: elem.id,
          entityName: controlledProcessTitle.toLowerCase(),
          expirationTime: DetailedSheetUtils.calculateLockExpiration(),
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: null, detail: response.message });
        });
        return await this.controlledProcessDataService.deleteControlledProcess(this.projectId, elem.id);
      });

      const failedResponses: Error[] = [];
      for (const prom of promiseList) {
        try {
          await prom;
        } catch (e) {
          failedResponses.push(e);
        }
      }
    }
  }

  private async deleteActuatorNode(dependentElementNode: DependentElementNode): Promise<void> {
    let list: any[] = [];
    list.push(dependentElementNode.entity);

    if (dependentElementNode.children.length > 0) {
      dependentElementNode.children.forEach((actuatorNode: DependentElementNode): void => {
        if (dependentElementNode.entity) {
          if (actuatorNode.entityTitle === controlActionTitle) {
            this.deleteControlActionNode(actuatorNode);
          }
        }
      });
    }

    if (!this.actuatorIsRoot || this.controlStructureEntity) {
      const promiseList = list.map(async (elem: any) => {
        await this.lockService.lockEntity(this.projectId, {
          id: elem.id,
          entityName: actuatorTitle.toLowerCase(),
          expirationTime: DetailedSheetUtils.calculateLockExpiration(),
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: null, detail: response.message });
        });
        return await this.actuatorDataService.deleteActuator(this.projectId, elem.id);
      });

      const failedResponses: Error[] = [];
      for (const prom of promiseList) {
        try {
          await prom;
        } catch (e) {
          failedResponses.push(e);
        }
      }
    }
  }

  private async deleteInputNode(dependentElementNode: DependentElementNode): Promise<void> {
    let list: any[] = [];
    list.push(dependentElementNode.entity);

    if (!this.inputIsRoot || this.controlStructureEntity) {
     const promiseList = list.map(async (elem: any) => {
        await this.lockService.lockEntity(this.projectId, {
          id: elem.id,
          entityName: inputTitle.toLocaleLowerCase(),
          expirationTime: DetailedSheetUtils.calculateLockExpiration(),
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: null, detail: response.message });
        });
        return await this.inputDataService.deleteInput(this.projectId, elem.id);
      });
      const failedResponses: Error[] = [];
      for (const prom of promiseList) {
        try {
          await prom;
        } catch (e) {
          failedResponses.push(e);
        }
      }
    }
  }
}


