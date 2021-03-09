import {Component} from '@angular/core';
import {EventEmitter, Input, Output} from '@angular/core';
import {HttpErrorResponse} from '@angular/common/http';
import {MatDialog, MatDialogRef} from '@angular/material';
import {MessageService} from 'primeng/api';
import {BoxDataService} from 'src/app/services/dataServices/box-data.service';
import {ControlActionDataService} from 'src/app/services/dataServices/control-structure/control-action-data.service';
import {ControllerDataService} from 'src/app/services/dataServices/control-structure/controller-data.service';
import {FeedbackDataService} from 'src/app/services/dataServices/control-structure/feedback-data.service';
import {InputDataService} from 'src/app/services/dataServices/control-structure/input-data.service';
import {SensorDataService} from 'src/app/services/dataServices/control-structure/sensor-data.service';
import {LockService} from 'src/app/services/dataServices/lock.service';
import {LossScenarioDataService} from 'src/app/services/dataServices/loss-scenario.service';
import {
  ActuatorDTO, Box, ControlActionDTO, ControlledProcessDTO, ControllerDTO, FeedbackDTO, InputDTO, OutputDTO,
  PageRequest,
  SensorDTO,
} from 'src/app/types/local-types';
import {ChangeDetectionService} from 'src/app/services/change-detection/change-detection-service.service';
import {FilterService} from '../../../../services/filter-service/filter.service';
import {UcaDataService} from '../../../../services/dataServices/uca-data.service';
import {ControllerConstraintDataService} from '../../../../services/dataServices/controller-constraint-data.service';
import {ImplementationConstraintDataService} from '../../../../services/dataServices/implementation-constraint-data.service';
import {
  ProcessModelDataService,
} from '../../../../services/dataServices/control-structure/process-model-data.service';
import {RuleDataService} from '../../../../services/dataServices/rule-data.service';
import {ResponsibilityDataService} from '../../../../services/dataServices/responsibility-data.service';
import {
  actuatorTitleNode,
  controlActionTitleNode, controlActionTitle, controlledProcessTitle,
  controllerTitleNode, controllerTitle,
  entitiesExist, entityExist,
  feedbackTitleNode, feedbackTitle,
  inputTitleNode, inputTitle,
  outputTitleNode, outputTitle,
  controlledProcessTitleNode,
  SelectedEntity,
  sensorTitleNode, sensorTitle,
} from '../../../../sub-app/dependent-element-tree/dependent-elements-types.component';
import {MoveEntityComponent} from './../../move-entity/move-entity.component';
import {SystemLevelSafetyConstraintDataService} from '../../../../services/dataServices/system-level-safety-constraint-data.service';
import {SubSystemConstraintDataService} from '../../../../services/dataServices/sub-system-constraint-data.service';
import {HazardDataService} from '../../../../services/dataServices/hazard-data.service';
import {SubHazardDataService} from '../../../../services/dataServices/sub-hazard-data.service';
import {
  ProcessVariableDataService,
} from '../../../../services/dataServices/control-structure/process-variable-data.service';
import {FlatTreeControl} from '@angular/cdk/tree';
import {MatTreeFlatDataSource, MatTreeFlattener} from '@angular/material/tree';
import {ActuatorDataService} from '../../../../services/dataServices/control-structure/actuator-data.service';
import {ControlStructureDataService} from '../../../../services/dataServices/control-structure-data.service';
import {
  DependentElementNode,
  DependentTreeNode
} from '../../../../sub-app/dependent-element-tree/dependent-elements-types.component';
import {DependentElementDetailsComponent} from './../../dependent-element-details/dependent-element-details.component';
import {LossDataService} from '../../../../services/dataServices/loss-data.service';
import {ControlledProcessDataService} from '../../../../services/dataServices/control-structure/controlled-process-data.service';
import {OutputDataService} from '../../../../services/dataServices/control-structure/output-data.service';

/**
 * This component creates the dependency tree of the second step.
 */
@Component({
  selector: 'app-create-dependency-tree-step-two',
  templateUrl: './create-dependency-tree-step-two.component.html',
  styleUrls: ['./create-dependency-tree-step-two.component.css']
})
export class CreateDependencyTreeStepTwoComponent {

  @Input() selectedEntity: SelectedEntity<Object>;
  @Output() entitySelectedEvent: any = new EventEmitter<DependentElementNode[]>();

  private projectId: string;
  private displayedColumns: string[] = ['name'];
  dependentElementNodes: DependentElementNode[];
  controlledProcessIsRoot: boolean = false;
  sensorIsRoot: boolean = false;
  actuatorIsRoot: boolean = false;
  feedbackIsRoot: boolean = false;
  outputIsRoot: boolean = false;

  constructor(
    public filterService: FilterService,
    public moveEntityDialog: MatDialog,
    public dependentElementDetails: MatDialog,
    public dialogRef: MatDialogRef<CreateDependencyTreeStepTwoComponent>,
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
    private readonly controlledProcessDataService: ControlledProcessDataService,
    private readonly lossDataService: LossDataService,
    private readonly outputDataService: OutputDataService) {
  }

  treeControl: FlatTreeControl<DependentElementNode> = new FlatTreeControl<DependentTreeNode>((node: any): number => node.level, (node: any): boolean => node.expandable);
  treeFlattener: any = new MatTreeFlattener((node: DependentElementNode, level: number): any => {
    return {
      expandable: !!node.children && node.children.length > 0,
      name: node.name,
      entityTitle: node.entityTitle,
      level: level,
      entity: node.entity,
      rootElement: node.rootElement,
      titleElement: node.titleElement,
      linkTitleElement: node.linkTitleElement,
      controlStructureElement: node.controlStructureElement,
      moveElement: node.moveElement,
      linkElement: node.linkElement,
    };
  }, (node: any): number => node.level, (node: any): boolean => node.expandable, (node: any): DependentElementNode[] => node.children);
  dataSource: MatTreeFlatDataSource<any, any> = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener);

  hasChild = (_: number, node: DependentTreeNode): boolean => node.expandable;

  ngOnInit(): void {
    this.loadData();
  }

  detailedInformation(node?: any): void {
    const elementDetailsRef: MatDialogRef<DependentElementDetailsComponent, any> = this.dependentElementDetails.open(DependentElementDetailsComponent, {
      width: 'auto',
      height: 'auto',
      data: {
        node: node,
      }
    });
  }

  selectedEntityToMove(node?: DependentElementNode): void {
    let entityTitle: string = node.entityTitle;
    switch (entityTitle) {
      case feedbackTitle: {
        this.openMoveDialog(null, node);
        break;
      }
      case sensorTitle: {
        this.openMoveDialog(null, node);
        break;
      }
      case controlledProcessTitle: {
        this.openMoveDialog(null, node);
        break;
      }
      case outputTitle: {
        this.openMoveDialog(null, node);
        break;
      }
      case controlActionTitle: {
        this.openMoveDialog(null, node);
        break;
      }
      default: {
        this.dialogRef.close();
        break;
      }
    }
  }

  openMoveDialog<T>(entitiesToMove: T[], node?: any): void {
    const moveEntityRef: MatDialogRef<MoveEntityComponent> = this.moveEntityDialog.open(MoveEntityComponent, {
      width: 'auto',
      height: 'auto',
      data: {
        projectId: this.projectId,
        node: node,
        entityTitle: node.entityTitle,
        entitiesToMoveTo: entitiesToMove,
      }
    });
    moveEntityRef.afterClosed().subscribe((entityIsMoved: DependentElementNode): void => {
      if (entityIsMoved) {
        this.loadData();
      }
    });
  }

  cancelDialog(): void {
    this.dialogRef.close(true);
  }

  deleteDependentElementTree(): void {
    this.entitySelectedEvent.emit(this.dependentElementNodes);
  }

  loadData(): void {
    this.dependentElementNodes = [];

    if (entityExist<string>(this.selectedEntity.projectId)) {
      this.projectId = this.selectedEntity.projectId;
    }
    switch (this.selectedEntity.entityTitle) {
      case sensorTitle: {
        this.sensorIsRoot = true;
        this.addSensorNode(<SensorDTO[]> this.selectedEntity.entity);
        break;
      }
      case controlledProcessTitle: {
        this.controlledProcessIsRoot = true;
        this.addControlledProcessNode(<ControlledProcessDTO[]> this.selectedEntity.entity);
        break;
      }
      case outputTitle: {
        this.outputIsRoot = true;
        this.addOutputNode(<OutputDTO[]> this.selectedEntity.entity);
        break;
      }
      case feedbackTitle: {
        this.feedbackIsRoot = true;
        this.addFeedbackNode(<FeedbackDTO[]> this.selectedEntity.entity);
        break;
      }
      default: {
        this.dialogRef.close();
        break;
      }
    }
  }

  private addSensorNode(sensorDTOs: SensorDTO[], dependentElementNode?: DependentElementNode): void {
    if (entityExist<SensorDTO[]>(sensorDTOs)) {
      if (!this.sensorIsRoot) {
        dependentElementNode.children.push(sensorTitleNode);
      }

      sensorDTOs.forEach((sensor: SensorDTO): void => {
        let sensorNode: DependentElementNode = {
          entity: sensor,
          name: sensor.name,
          children: [],
          rootElement: false,
          controlStructureElement: false,
          linkElement: false,
          entityTitle: sensorTitle
        };

        if (this.sensorIsRoot) {
          sensorNode.controlStructureElement = true;
          this.getFeedBackBySensorDestinationBoxId(sensor, sensorNode);
          this.getFeedbackBySensorSource(sensor, sensorNode);
          this.dependentElementNodes.push(sensorNode);
        } else {
          sensorNode.linkElement = true;
          dependentElementNode.children.push(sensorNode);
        }

        this.dataSource.data = this.dependentElementNodes;
        this.treeControl.expandAll();
      });
    }
  }

  private getFeedbackBySensorSource(sensor: SensorDTO, dependentElementNode?: DependentElementNode): void {
    this.feedbackDataService.getFeedbackBySourceBoxId(this.projectId, sensor.boxId)
      .then((feedbacks: FeedbackDTO[]): void => {
        if (feedbacks) {
          this.addFeedbackNode(feedbacks, dependentElementNode);
        }
      });
  }

  private getFeedBackBySensorDestinationBoxId(sensor: SensorDTO, dependentElementNode?: DependentElementNode): void {
    this.feedbackDataService.getFeedbackByDestinationBoxId(this.projectId, sensor.boxId)
      .then((feedbacks: FeedbackDTO[]): void => {
        if (feedbacks) {
          this.addFeedbackNode(feedbacks, dependentElementNode);
        }
      });
  }

  private addFeedbackNode(feedbacks: FeedbackDTO[], dependentElementNode?: DependentElementNode): void {
    if (entitiesExist<FeedbackDTO>(feedbacks)) {
      if (!this.feedbackIsRoot) {
        dependentElementNode.children.push(feedbackTitleNode);
      }
      feedbacks.forEach((feedbackDTO: FeedbackDTO) => {
        let feedbackNode: DependentElementNode = {
          entity: feedbackDTO,
          name: feedbackDTO.name,
          children: [],
          controlStructureElement: true,
          entityTitle: feedbackTitle
        };

        if (!this.sensorIsRoot && !this.controlledProcessIsRoot) {
          this.getControllerByFeedbackArrow(feedbackDTO, feedbackNode);
        }

        if (this.feedbackIsRoot) {
          this.dependentElementNodes.push(feedbackNode);
        } else {
          dependentElementNode.children.push(feedbackNode);
        }

        this.dataSource.data = this.dependentElementNodes;
        this.treeControl.expandAll();
      });
    }
  }

  private getControllerByFeedbackArrow(feedbackDTO: FeedbackDTO, dependentElementNode?: DependentElementNode): void {
    this.controllerDataService.getControllerByFeedbackDestinationArrowId(this.projectId, feedbackDTO.arrowId)
      .then((controller: ControllerDTO): void => {
        if (controller) {
          if (!this.feedbackIsRoot) {
            this.orderFeedbackNodes(feedbackDTO, dependentElementNode, [controller]);
          } else {
            this.addControllerNode([controller], dependentElementNode);
          }
        } else {
          this.getSensorByFeedbackDestination(feedbackDTO, dependentElementNode);
        }
      }).catch((response: HttpErrorResponse) => {
      this.getSensorByFeedbackDestination(feedbackDTO, dependentElementNode);
    });
  }

  private orderFeedbackNodes(feedback: FeedbackDTO,
                             dependentElementNode: DependentElementNode,
                             controller?: ControllerDTO[],
                             sensorDTO?: SensorDTO[]): void {
    if (controller && !this.sensorIsRoot) {
      this.addControllerNode(controller, dependentElementNode);
      this.getSensorByFeedbackDestination(feedback, dependentElementNode);
    } else if (sensorDTO) {
      this.addSensorNode(sensorDTO, dependentElementNode);
      if (!this.controlledProcessIsRoot && !this.feedbackIsRoot) {
        this.getControlledProcessByFeedbackArrowId(feedback, dependentElementNode);
      }
    }
  }

  private addControllerNode(controllerDTOs: ControllerDTO[], dependentElementNode?: DependentElementNode): void {
    if (entitiesExist<ControllerDTO>(controllerDTOs)) {
      dependentElementNode.children.push(controllerTitleNode);

      controllerDTOs.forEach((controllerDTO: ControllerDTO) => {
        let controllerNode = {
          entity: controllerDTO,
          name: controllerDTO.name,
          children: [],
          entityTitle: controllerTitle,
          linkElement: true
        };
        dependentElementNode.children.push(controllerNode);

        this.dataSource.data = this.dependentElementNodes;
        this.treeControl.expandAll();
      });
    }
  }

  private getSensorByFeedbackDestination(feedback: FeedbackDTO, dependentElementNode?: DependentElementNode): void {
    this.sensorDataService.getSensorByFeedbackDestintationArrowId(feedback.projectId, feedback.arrowId)
      .then((sensorDTO: SensorDTO) => {
        if (sensorDTO) {
          this.orderFeedbackNodes(feedback, dependentElementNode, null, [sensorDTO]);
        } else {
          this.getSensorByFeedbackDestination(feedback, dependentElementNode);
        }
      }).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: null, detail: response.message});
    });
  }

  private getControlledProcessByFeedbackArrowId(feedbackDTO: FeedbackDTO, dependentElementNode?: DependentElementNode): void {
    this.controlledProcessDataService.getControlledProcessByFeedbackSourceArrowId(this.projectId, feedbackDTO.arrowId)
      .then((controlledProcess: ControlledProcessDTO): void => {
        if (controlledProcess) {
          this.addControlledProcessNode([controlledProcess], dependentElementNode);
        }
      });
  }

  private addControlledProcessNode(controlledProcesses: ControlledProcessDTO[], dependentElementNode?: DependentElementNode): void {
    if (entitiesExist<ControlledProcessDTO>(controlledProcesses)) {
      if (!this.controlledProcessIsRoot) {
        dependentElementNode.children.push(controlledProcessTitleNode);
      }

      controlledProcesses.forEach((controlledProcess: ControlledProcessDTO): void => {
        let processNode: DependentElementNode = {
          entity: controlledProcess,
          name: controlledProcess.name,
          entityTitle: controlledProcessTitle,
          children: [],
          controlStructureElement: false,
          linkElement: false
        };

        if (this.controlledProcessIsRoot) {
          this.getControlActionByControlledProcessBoxId(controlledProcess, processNode);
        }

        if (this.controlledProcessIsRoot) {
          processNode.controlStructureElement = true;
          this.dependentElementNodes.push(processNode);
        } else {
          processNode.linkElement = true;
          dependentElementNode.children.push(processNode);
        }
        this.dataSource.data = this.dependentElementNodes;
        this.treeControl.expandAll();
      });
    }
  }

  private getControlActionByControlledProcessBoxId(controlledProcess: ControlledProcessDTO, dependentElementNode?: DependentElementNode): void {
    this.controlActionDataService.getControlActionByDestinationBoxId(this.projectId, controlledProcess.boxId)
      .then((controlAction: ControlActionDTO[]) => {
        if (controlAction.length > 0) {
          this.orderControlledProcessNodes(controlledProcess, dependentElementNode, controlAction);
        } else {
          this.getInputByControlledProcess(controlledProcess, dependentElementNode);
        }
      }).catch((response: HttpErrorResponse) => {
      this.getInputByControlledProcess(controlledProcess, dependentElementNode);
    });
  }

  private getFeedbackByControlledProcess(controlledProcess: ControlledProcessDTO, dependentElementNode?: DependentElementNode): void {
    this.feedbackDataService.getFeedbackBySourceBoxId(this.projectId, controlledProcess.boxId)
      .then((feedback: FeedbackDTO[]) => {
        if (feedback.length > 0) {
          this.addFeedbackNode(feedback, dependentElementNode);
        }
      }).catch((response: HttpErrorResponse) => {
      if (!this.controlledProcessIsRoot) {
        this.getInputByControlledProcess(controlledProcess, dependentElementNode);
      }
    });
  }

  private orderControlledProcessNodes(controlledProcessDTO: ControlledProcessDTO,
                                      dependentElementNode: DependentElementNode,
                                      controlActionDTO?: ControlActionDTO[],
                                      feedbackDTO?: FeedbackDTO[],
                                      inputDTO?: InputDTO[],
                                      outputDTO?: OutputDTO[]): void {
    if (this.controlledProcessIsRoot) {
      if (controlActionDTO) {
        this.addControlActionNode(controlActionDTO, dependentElementNode);
        this.getInputByControlledProcess(controlledProcessDTO, dependentElementNode);
      } else if (inputDTO) {
        this.addInputNode(inputDTO, dependentElementNode);
        this.getOutputByControlledProcess(controlledProcessDTO, dependentElementNode);
      } else if (outputDTO) {
        this.addOutputNode(outputDTO, dependentElementNode);
        this.getFeedbackByControlledProcess(controlledProcessDTO, dependentElementNode);
      }
    }
  }

  private getInputByControlledProcess(controlledProcess: ControlledProcessDTO, dependentElementNode?: DependentElementNode): void {
    this.inputDataService.getInputsByBoxDestinationId(this.projectId, controlledProcess.boxId)
      .then((inputs: InputDTO[]) => {
        if (inputs) {
          this.orderControlledProcessNodes(controlledProcess, dependentElementNode, null, null, inputs);
        } else {
          this.getOutputByControlledProcess(controlledProcess, dependentElementNode);
        }
      }).catch((response: HttpErrorResponse) => {
      this.getOutputByControlledProcess(controlledProcess, dependentElementNode);
      this.messageService.add({ severity: 'error', summary: null, detail: response.message});
    });
  }

  private addInputNode(inputs: InputDTO[], dependentElementNode?: DependentElementNode): void {
    if (entitiesExist<InputDTO>(inputs)) {
      dependentElementNode.children.push(inputTitleNode);

      inputs.forEach((inputDTO: InputDTO): void => {
        let inputNode: DependentElementNode = {
          entity: inputDTO,
          name: inputDTO.name,
          entityTitle: controlledProcessTitle,
          children: [],
          rootElement: false,
          controlStructureElement: true
        };

        dependentElementNode.children.push(inputNode);
        this.dataSource.data = this.dependentElementNodes;
        this.treeControl.expandAll();
      });
    }
  }

  private getOutputByControlledProcess(controlledProcess: ControlledProcessDTO, dependentElementNode?: DependentElementNode): void {
    this.outputDataService.getOutputsByBoxSourceId(this.projectId, controlledProcess.boxId)
      .then((outputs: OutputDTO[]) => {
        if (outputs && this.controlledProcessIsRoot) {
          this.orderControlledProcessNodes(controlledProcess, dependentElementNode, null, null, null, outputs);
        } else {
          this.getFeedbackByControlledProcess(controlledProcess, dependentElementNode);
        }
      }).catch((response: HttpErrorResponse) => {
      this.getFeedbackByControlledProcess(controlledProcess, dependentElementNode);
      this.messageService.add({ severity: 'error', summary: null, detail: response.message});
    });
  }

  private addControlActionNode(controlActions: ControlActionDTO[], dependentElementNode?: DependentElementNode): void {
    if (entitiesExist<ControlActionDTO>(controlActions)) {
      dependentElementNode.children.push(controlActionTitleNode);
      controlActions.forEach((controlActionDTO: ControlActionDTO) => {
        let controlActionNode: DependentElementNode = {
          entity: controlActionDTO,
          name: controlActionDTO.name,
          entityId: controlActionDTO.id,
          children: [],
          controlStructureElement: true,
          entityTitle: controlActionTitle
        };

        if (this.controlledProcessIsRoot) {

        } else {
          this.getActuatorByControlActionDestination(controlActionDTO, controlActionNode);
        }

        dependentElementNode.children.push(controlActionNode);
        this.dataSource.data = this.dependentElementNodes;
        this.treeControl.expandAll();
      });
    }
  }

  private orderControlActionNodes(controlActionDTO: ControlActionDTO,
                                  dependentElementNode?: DependentElementNode,
                                  controllerDTO?: ControllerDTO[]): void {
    if (controllerDTO) {
      this.addControllerNode(controllerDTO, dependentElementNode);
      this.getActuatorByControlActionDestination(controlActionDTO, dependentElementNode);
    }
  }

  private getControllerByControlActionSourceId(controlActionDTO: ControlActionDTO, dependentElementNode?: DependentElementNode): void {
    this.controllerDataService.getControllerByControlActionSourceArrowId(this.projectId, controlActionDTO.arrowId)
      .then((controller: ControllerDTO): void => {
        if (controller) {
          this.orderControlActionNodes(controlActionDTO, dependentElementNode, [controller]);
        } else {
          this.getActuatorByControlActionDestination(controlActionDTO, dependentElementNode);
        }
      });
  }

  private getActuatorByControlActionDestination(controlActionDTO: ControlActionDTO, dependentElementNode?: DependentElementNode): void {
    this.actuatorDataService.getActuatorByControlActionDestinationArrowId(controlActionDTO.projectId, controlActionDTO.arrowId)
      .then((actuatorDTO: ActuatorDTO) => {
        this.addActuatorNode([actuatorDTO], dependentElementNode);
      }).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: null, detail: response.message});
    });
  }

  private addActuatorNode(actuatorDTO: ActuatorDTO[], dependentElementNode?: DependentElementNode): void {
    if (entityExist<ActuatorDTO[]>(actuatorDTO)) {
      if (!this.actuatorIsRoot) {
        dependentElementNode.children.push(actuatorTitleNode);
      }

      actuatorDTO.forEach((actuator: ActuatorDTO): void => {
        let actuatorNode: DependentElementNode = {
          entity: actuator,
          name: actuator.name,
          children: [],
          controlStructureElement: true,
          entityTitle: controlActionTitle
        };

        dependentElementNode.children.push(actuatorNode);
        this.dataSource.data = this.dependentElementNodes;
        this.treeControl.expandAll();
      });
    }
  }

  private addOutputNode(outputs: OutputDTO[], dependentElementNode?: DependentElementNode): void {
    if (entitiesExist<OutputDTO>(outputs)) {

      if (!this.outputIsRoot) {
        dependentElementNode.children.push(outputTitleNode);
      }

      if (entitiesExist<OutputDTO>(outputs)) {
        outputs.forEach((outputDTO: OutputDTO): void => {
          let outputNode: DependentElementNode = {
            entity: outputDTO,
            name: outputDTO.name,
            entityTitle: outputTitle,
            children: [],
            controlStructureElement: true
          };

          this.getAllBoxesByOutput(outputDTO, outputNode);

          if (!this.outputIsRoot) {
            dependentElementNode.children.push(outputNode);
          } else {
            this.dependentElementNodes.push(outputNode);
          }
          this.dataSource.data = this.dependentElementNodes;
          this.treeControl.expandAll();
        });
      }
    }
  }

  private getAllBoxesByOutput(outputDTO: OutputDTO, dependentElementNode?: DependentElementNode): void {
    this.outputDataService.getOutputsSource(this.projectId, outputDTO).then((outputBox: Box[]): void => {
      this.loadBoxByTypes(outputBox, dependentElementNode);
    });
  }

  private loadBoxByTypes(boxes: Box[], dependentElementNode?: DependentElementNode): void {
    const controllerBoxes: Box[] = boxes.filter((box: Box) => box.type === 'Controller');
    const controlledProcessBoxes: Box[] = boxes.filter((box: Box) => box.type === 'ControlledProcess');

    controllerBoxes.forEach((box: Box): void => {
      this.getControllerByBoxId(box, dependentElementNode);
    });

    controlledProcessBoxes.forEach((box: Box): void => {
      if (!this.controlledProcessIsRoot) {
        this.getControlledProcessByBoxId(box, dependentElementNode);
      }
    });
  }

  private getControllerByBoxId(controllerBox: Box, dependentElementNode?: DependentElementNode): void {
    this.controllerDataService.getControllerByBoxId(this.projectId, controllerBox.id).then((controllerDTO: ControllerDTO): void => {
      this.addControllerNode([controllerDTO], dependentElementNode);
    });
  }

  private getControlledProcessByBoxId(controlledProcessBox: Box, dependentElementNode?: DependentElementNode): void {
    this.controlledProcessDataService.getControlledProcessByBoxId(this.projectId, controlledProcessBox.id)
      .then((controlledProcess: ControlledProcessDTO) => {
        this.addControlledProcessNode([controlledProcess], dependentElementNode);
      }).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: null, detail: response.message});
    });
  }
}

