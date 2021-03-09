import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
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
  ACTUATOR,
  ActuatorDTO,
  Arrow,
  Box,
  CONTROL_ACTION,
  ControlActionDTO,
  CONTROLLED_PROCESS,
  ControlledProcessDTO,
  CONTROLLER,
  CONTROLLER_CONSTRAINT,
  ControllerConstraintResponseDTO,
  ControllerDTO,
  ControlStructureDTO,
  ConversionResponseDTO,
  FEEDBACK,
  FeedbackDTO,
  HAZARD,
  HazardResponseDTO,
  IMPLEMENTATION_CONSTRAINT,
  ImplementationConstraintResponseDTO,
  INPUT,
  InputDTO,
  LOSS_SCENARIO,
  LossScenarioRequestDTO,
  LossScenarioResponseDTO,
  OUTPUT,
  OutputDTO,
  PageRequest,
  ResponsibilityResponseDTO,
  RULE,
  RuleResponseDTO,
  SENSOR,
  SensorDTO,
  SubHazardResponseDTO,
  SYSTEM_CONSTRAINT,
  SystemConstraintResponseDTO,
  UcaResponseDTO,
  UNSAFE_CONTROL_ACTION
} from 'src/app/types/local-types';
import {ChangeDetectionService} from 'src/app/services/change-detection/change-detection-service.service';
import {FilterService} from '../../../services/filter-service/filter.service';
import {UcaDataService} from '../../../services/dataServices/uca-data.service';
import {ControllerConstraintDataService} from '../../../services/dataServices/controller-constraint-data.service';
import {ImplementationConstraintDataService} from '../../../services/dataServices/implementation-constraint-data.service';
import {ConversionTableComponent} from '../../conversion-table/conversion-table.component';
import {ConversionDataService} from '../../../services/dataServices/conversion-data.service';
import {
  ProcessModelDataService, ProcessModelResponseDTO
} from '../../../services/dataServices/control-structure/process-model-data.service';
import {RuleDataService} from '../../../services/dataServices/rule-data.service';
import {ResponsibilityDataService} from '../../../services/dataServices/responsibility-data.service';
import {
  actuatorTitleNode, actuatorTitle,
  controlActionTitleNode,
  controlActionTitle,
  controlledProcessesTitleNode, controlledProcessTitle, controllerConstraintTitle,
  controllerTitleNode,
  controllerTitle, entitiesExist, entityExist,
  feedbackTitleNode,
  feedbackTitle, hazardTitleNode, hazardTitle, implementationConstraintTitle,
  implementationConstraintTitleNode, inputTitleNode,
  inputTitle, linkedControlStructureTitleNode, lossScenarioTitle,
  outputTitleNode,
  outputTitle,
  processModelsNode, processModelTitle, processsVariableNode, processVariableTitle,
  responsibilityTitleNode,
  responsibilityTitle,
  ruleTitleNode,
  ruleTitle,
  SelectedEntity,
  sensorTitleNode, sensorTitle, stepTwoTitleElement, subHazardLiningTitle,
  systemConstraintTitleNode, systemConstraintTitle,
  ucaTitleNode, ucaTitle, converationTitle, ConverationTitleNode
} from '../../../sub-app/dependent-element-tree/dependent-elements-types.component';
import {MoveEntityComponent} from './../move-entity/move-entity.component';
import {SystemLevelSafetyConstraintDataService} from '../../../services/dataServices/system-level-safety-constraint-data.service';
import {SubSystemConstraintDataService} from '../../../services/dataServices/sub-system-constraint-data.service';
import {HazardDataService} from '../../../services/dataServices/hazard-data.service';
import {SubHazardDataService} from '../../../services/dataServices/sub-hazard-data.service';
import {
  ProcessVariableDataService, ProcessVariableResponseDTO
} from '../../../services/dataServices/control-structure/process-variable-data.service';
import {FlatTreeControl} from '@angular/cdk/tree';
import {MatTreeFlatDataSource, MatTreeFlattener} from '@angular/material/tree';
import {ActuatorDataService} from '../../../services/dataServices/control-structure/actuator-data.service';
import {ControlStructureDataService} from '../../../services/dataServices/control-structure-data.service';
import {
  DependentElementNode,
  DependentTreeNode
} from '../../../sub-app/dependent-element-tree/dependent-elements-types.component';
import {DependentElementDetailsComponent} from './../dependent-element-details/dependent-element-details.component';
import {LossDataService} from '../../../services/dataServices/loss-data.service';
import {ControlledProcessDataService} from '../../../services/dataServices/control-structure/controlled-process-data.service';
import {OutputDataService} from '../../../services/dataServices/control-structure/output-data.service';

/**
 * This component creates the dependency tree of the first step.
 */
@Component({
  selector: 'app-create-dependent-elements',
  templateUrl: './create-dependency-tree.component.html',
  styleUrls: ['./create-dependency-tree.component.css']
})
export class CreateDependencyTreeComponent implements OnInit {

  @Input() selectedEntity: SelectedEntity<Object>;
  @Output() entitySelectedEvent: any = new EventEmitter<DependentElementNode[]>();

  dependentElementNodes: DependentElementNode[];
  displayedColumns: string[] = ['name'];
  rules: RuleResponseDTO[];
  loadEntitiesToShow: boolean = false;
  private projectId: string;
  private page: PageRequest = { from: 0, amount: 100 };
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
  private implementationConstraintIsRoot: boolean = false;
  private converationIsRoot: boolean = false;

  constructor(
    public filterService: FilterService,
    public moveEntityDialog: MatDialog,
    public dependentElementDetails: MatDialog,
    public dialogRef: MatDialogRef<CreateDependencyTreeComponent>,
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
    private readonly outputDataService: OutputDataService,
    private readonly converationTableDataService: ConversionDataService) {
  }

  treeControl: FlatTreeControl<DependentElementNode> = new FlatTreeControl<DependentTreeNode>((node: any): number => node.level, (node: any): boolean => node.expandable);
  treeFlattener: any = new MatTreeFlattener((node: DependentElementNode, level: number): any => {
    return {
      expandable: !!node.children && node.children.length > 0,
      name: node.name,
      entityTitle: node.entityTitle,
      level: level,
      entity: node.entity,
      children: node.children,
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

  detailedInformation(node?: DependentElementNode): void {
    this.dependentElementDetails.open(DependentElementDetailsComponent, {
      width: 'auto',
      height: 'auto',
      data: {
        node: node
      }
    });
  }

  selectedEntityToMove(node: DependentElementNode): void {
    let entityTitle: string = node.entityTitle;
    switch (entityTitle) {
      case controllerTitle: {
        this.openMoveDialog(null, node);
        break;
      }
      case ruleTitle: {
        this.controllerDataService.getAllControllers(this.projectId, { 'orderBy': 'name' })
          .then((controllers: ControllerDTO[]) => {
            this.openMoveDialog<ControllerDTO>(controllers, node);
          });
        break;
      }
      case controlActionTitle: {
        this.openMoveDialog<RuleResponseDTO>(this.rules, node);
        break;
      }
      case ucaTitle: {
        this.controlActionDataService.getAllControlActions(this.projectId, { 'orderBy': 'name' })
          .then((controlActions: ControlActionDTO[]): void => {
            this.openMoveDialog<ControlActionDTO>(controlActions, node);
          });
        break;
      }
      case processModelTitle: {
        this.controllerDataService.getAllControllers(this.projectId, { 'orderBy': 'name' })
          .then((controllers: ControllerDTO[]) => {
            this.openMoveDialog<ControllerDTO>(controllers, node);
          });
        break;
      }
      case responsibilityTitle: {
        this.controllerDataService.getAllControllers(this.projectId, { 'orderBy': 'name' })
          .then((controllers: ControllerDTO[]) => {
            this.openMoveDialog<ControllerDTO>(controllers, node);
          });
        break;
      }
      case lossScenarioTitle: {
        this.ucaDataService.getAllUcas(this.projectId, { 'orderBy': 'name' })
          .then((ucas: UcaResponseDTO[]) => {
            this.openMoveDialog<UcaResponseDTO>(ucas, node);
          });
        break;
      }
      case implementationConstraintTitle: {
        this.lossScenarioDataService.getAllLossScenarios(this.projectId, { 'orderBy': 'name' })
          .then((lossScenarios: LossScenarioResponseDTO[]) => {
            this.openMoveDialog<LossScenarioResponseDTO>(lossScenarios, node);
          });
        break;
      }
      case controllerConstraintTitle: {
        this.ucaDataService.getAllUcas(this.projectId, { 'orderBy': 'name' })
          .then((ucas: UcaResponseDTO[]) => {
            this.openMoveDialog<UcaResponseDTO>(ucas, node);
          });
        break;
      }
      case converationTitle: {
        this.actuatorDataService.getAllActuators(this.projectId, {})
          .then((actutators: ActuatorDTO[]) => {
            this.openMoveDialog<ActuatorDTO>(actutators, node);
          });
        break;
      }
      case actuatorTitle: {
        this.openMoveDialog(null, node);
        break;
      }
      case controlledProcessTitle: {
        this.openMoveDialog(null, node);
        break;
      }
      case inputTitle: {
        this.openMoveDialog(null, node);
        break;
      }
      case feedbackTitle: {
        this.openMoveDialog(null, node);
        break;
      }
      case outputTitle: {
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

  private loadControllerToMoveControlAction(): void {
    this.controllerDataService.getAllControllers(this.projectId, { 'orderBy': 'name' })
      .then((controllerDTOs: ControllerDTO[]) => {
        controllerDTOs.forEach((controller: ControllerDTO): void => {
          this.getRulesToMoveControlAction(controller);
        });
      });
  }

  private getRulesToMoveControlAction(controllerDTO: ControllerDTO): void {
    this.ruleDataService.getAllRules(this.projectId, controllerDTO.id, {})
      .then((rules: RuleResponseDTO[]) => {
        rules.forEach((rule: RuleResponseDTO) => {
          this.rules.push(rule);
        });
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
    this.rules = [];
    this.loadEntitiesToShow = false;

    if (entityExist<string>(this.selectedEntity.projectId)) {
      this.projectId = this.selectedEntity.projectId;
    }
    switch (this.selectedEntity.entityTitle) {
      case controllerTitle: {
        this.controllerIsRoot = true;
        this.addControllerNode(<ControllerDTO[]> this.selectedEntity.entity);
        break;
      }
      case responsibilityTitle: {
        this.responsibilityIsRoot = true;
        this.addResponsibilityNode(<ResponsibilityResponseDTO[]> this.selectedEntity.entity);
        break;
      }
      case processModelTitle: {
        this.processModelIsRoot = true;
        this.addProcessModelNode(<ProcessModelResponseDTO[]> this.selectedEntity.entity);
        break;
      }
      case ruleTitle: {
        this.ruleIsRoot = true;
        this.addRuleNode(<RuleResponseDTO[]> this.selectedEntity.entity);
        break;
      }
      case controlActionTitle: {
        this.controlActionIsRoot = true;
        this.addControlActionNode(<ControlActionDTO[]> this.selectedEntity.entity, null, true);
        break;
      }
      case ucaTitle: {
        this.ucaIsRoot = true;
        this.addUCANode(<UcaResponseDTO[]> this.selectedEntity.entity);
        break;
      }
      case lossScenarioTitle: {
        this.lossScenarioIsRoot = true;
        this.addLossScenarioNode(<LossScenarioResponseDTO[]> this.selectedEntity.entity);
        break;
      }
      case inputTitle: {
        this.inputIsRoot = true;
        this.addInputNode(<InputDTO[]> this.selectedEntity.entity);
        break;
      }
      case outputTitle: {
        this.outputIsRoot = true;
        this.addOutputNode(<OutputDTO[]> this.selectedEntity.entity);
        break;
      }
      case actuatorTitle: {
        this.actuatorIsRoot = true;
        this.addActuatorNode(<ActuatorDTO[]> this.selectedEntity.entity);
        break;
      }
      case feedbackTitle: {
        this.feedbackIsRoot = true;
        this.addFeedbackNode(<FeedbackDTO[]> this.selectedEntity.entity);
        break;
      }
      case implementationConstraintTitle: {
        this.implementationConstraintIsRoot = true;
        this.addImplementationConstraintNode(<ImplementationConstraintResponseDTO[]> this.selectedEntity.entity);
        break;
      }
      case converationTitle: {
        this.converationIsRoot = true;
        this.addConversionNode(<ConversionResponseDTO[]> this.selectedEntity.entity);
        break;
      }
      case implementationConstraintTitle: {
        this.implementationConstraintIsRoot = true;
        this.addImplementationConstraintNode(<ImplementationConstraintResponseDTO[]> this.selectedEntity.entity);
        break;
      }
      default: {
        this.dialogRef.close();
        break;
      }
    }
  }

  private addControllerNode(controllerDTOs: ControllerDTO[], dependentElementNode?: DependentElementNode): void {
    if (entitiesExist<ControllerDTO>(controllerDTOs)) {
      if (!this.controllerIsRoot) {
        dependentElementNode.children.push(controllerTitleNode);
      }

      controllerDTOs.forEach((controllerDTO: ControllerDTO) => {
        let controllerNode = {
          entity: controllerDTO,
          name: controllerDTO.name,
          children: [],
          entityTitle: controllerTitle,
          rootElement: false,
          controlStructureElement: false,
          linkElement: false
        };

        if (!this.actuatorIsRoot && !this.inputIsRoot) {
          this.getRules(controllerDTO, controllerNode);
        }

        if (this.controllerIsRoot) {
          controllerNode.rootElement = true;
          this.dependentElementNodes.push(controllerNode);
        } else {
          controllerNode.linkElement = true;
          dependentElementNode.children.push(controllerNode);
        }

        this.dataSource.data = this.dependentElementNodes;
        this.treeControl.expandAll();
      });
    }
  }

  private getRules(controllerDTO: ControllerDTO, dependentElementNode?: DependentElementNode): void {
    this.ruleDataService.getAllRules(controllerDTO.projectId, controllerDTO.id, {
      'orderBy': 'id',
      'orderDirection': 'asc'
    })
      .then((rules: RuleResponseDTO[]) => {
        if (rules.length > 0) {
          this.orderControllerNodes(controllerDTO, dependentElementNode, rules);
        } else {
          this.getResponsibilitiesByController(controllerDTO, dependentElementNode);
        }
      }).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: null, detail: response.message });
    });
  }

  private orderControllerNodes(controllerDTO: ControllerDTO,
                               dependentElementNode: DependentElementNode,
                               rules?: RuleResponseDTO[],
                               responsibilities?: ResponsibilityResponseDTO[],
                               processModels?: ProcessModelResponseDTO[],
                               controlActions?: ControlActionDTO[]): void {
    if (rules) {
      this.addRuleNode(rules, dependentElementNode, controllerDTO);
      this.getResponsibilitiesByController(controllerDTO, dependentElementNode);
    } else if (responsibilities) {
      this.addResponsibilityNode(responsibilities, dependentElementNode);
      this.getProcessModel(controllerDTO, dependentElementNode);
    } else if (processModels) {
      this.addProcessModelNode(processModels, dependentElementNode);
      this.getControlActionByControllerBoxId(controllerDTO, dependentElementNode);
    } else if (controlActions) {
      this.addControlActionControlStructureNode(controlActions, dependentElementNode, controllerDTO, true);
    }
  }

  private addRuleNode(ruleResponseDTOs: RuleResponseDTO[],
                      dependentElementNode?: DependentElementNode,
                      controllerDTO?: ControllerDTO): void {

    if (entitiesExist<RuleResponseDTO>(ruleResponseDTOs)) {
      if (!this.ruleIsRoot) {
        dependentElementNode.children.push(ruleTitleNode);
      }
      if (!this.loadEntitiesToShow) {
        this.loadEntitiesToShow = true;
        this.loadControllerToMoveControlAction();
      }

      ruleResponseDTOs.forEach((ruleResponseDTO: RuleResponseDTO) => {
        let ruleNode: DependentElementNode;
        if (controllerDTO) {
          ruleNode = {
            entity: {
              projectId: ruleResponseDTO.projectId,
              id: ruleResponseDTO.id,
              rule: ruleResponseDTO.rule.slice(0, 35),
              state: ruleResponseDTO.state,
              controlActionId: ruleResponseDTO.controlActionId,
              controllerId: controllerDTO.id
            },
            name: ruleResponseDTO.rule.slice(0, 35),
            children: [],
            entityTitle: ruleTitle,
            rootElement: false,
            moveElement: false,
          };
        } else {
          ruleNode = {
            entity: ruleResponseDTO,
            name: ruleResponseDTO.rule.slice(0, 35),
            children: [],
            entityTitle: ruleTitle,
            rootElement: false,
            moveElement: false
          };
        }

        this.getControlActions(ruleResponseDTO, ruleNode);

        if (this.ruleIsRoot) {
          ruleNode.rootElement = true;
          this.dependentElementNodes.push(ruleNode);
        } else {
          ruleNode.moveElement = true;
          dependentElementNode.children.push(ruleNode);
        }
        this.dataSource.data = this.dependentElementNodes;
        this.treeControl.expandAll();
      });
    }
  }

  private getControlActions(ruleResponseDTO?: RuleResponseDTO, dependentElementNode?: DependentElementNode): void {
    if (entityExist<RuleResponseDTO>(ruleResponseDTO)) {
      if (ruleResponseDTO.controlActionId !== '0') {
        this.controlActionDataService.getControlActionById(ruleResponseDTO.projectId, ruleResponseDTO.controlActionId)
          .then((controlActionDTO: ControlActionDTO) => {
            if (controlActionDTO) {
              this.addControlActionNode([controlActionDTO], dependentElementNode, true, ruleResponseDTO);
            }
          }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: null, detail: response.message });
        });
      } else {
        this.dataSource.data = this.dependentElementNodes;
        this.treeControl.expandAll();
      }
    }
  }

  private addControlActionNode(controlActionDTOs?: ControlActionDTO[],
                               dependentElementNode?: DependentElementNode,
                               childNode?: boolean,
                               ruleResponseDTO?: RuleResponseDTO,
                               actuatorSource?: boolean): void {
    if (entitiesExist<ControlActionDTO>(controlActionDTOs)) {
      if (!this.controlActionIsRoot) {
        dependentElementNode.children.push(controlActionTitleNode);
      }
      if (!this.loadEntitiesToShow) {
        this.loadEntitiesToShow = true;
        this.loadControllerToMoveControlAction();
      }

      controlActionDTOs.forEach((controlActionDTO: ControlActionDTO) => {
        let controlActionNode: DependentElementNode = {
          entity: controlActionDTO,
          name: controlActionDTO.name,
          entityTitle: controlActionTitle,
          children: [],
          controlStructureElement: false,
          moveElement: false
        };
        if (ruleResponseDTO) {
          controlActionNode.entity = {
            id: controlActionDTO.id,
            projectId: controlActionDTO.projectId,
            description: controlActionDTO.description,
            state: controlActionDTO.state,
            name: controlActionDTO.name,
            ruleId: ruleResponseDTO.id,
            ruleControllerId: ruleResponseDTO.controllerId,
            rule: ruleResponseDTO.rule,
            ruleState: ruleResponseDTO.state
          }
        }
        if (childNode) {
          this.getUCAs(controlActionDTO, controlActionNode, false);
        } else {
          this.getUCAs(controlActionDTO, controlActionNode, true);
        }

        if (this.controlActionIsRoot) {
          this.getActuatorByControlActionDestination(controlActionDTO, controlActionNode);
        }

        if (this.controlActionIsRoot) {
          controlActionNode.controlStructureElement = true;
          this.dependentElementNodes.push(controlActionNode);
        } else {
          if (!this.actuatorIsRoot) {
            controlActionNode.moveElement = true;
          } else {
            controlActionNode.controlStructureElement = true;
          }
          dependentElementNode.children.push(controlActionNode);
        }
        this.dataSource.data = this.dependentElementNodes;
        this.treeControl.expandAll();
      });
    }
  }

  private getUCAs(controlActionDTO?: ControlActionDTO,
                  dependentElementNode?: DependentElementNode,
                  controlActionByBox?: boolean): void {
    this.ucaDataService.getAllUcasByControlActionId(controlActionDTO.projectId, controlActionDTO.id, {})
      .then((ucaResponseDTOs: UcaResponseDTO[]) => {
        if (ucaResponseDTOs.length > 0) {
          if (controlActionByBox) {
            this.orderControlActionControlStructureNodes(controlActionDTO, dependentElementNode, ucaResponseDTOs);
          } else {
            this.orderControlActionNodes(controlActionDTO, dependentElementNode, ucaResponseDTOs);
          }
        } else if (!controlActionByBox) {
          this.getControlledProcessByControlActionDestination(controlActionDTO, dependentElementNode);
        }
      }).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: null, detail: response.message });
    });
  }

  private addUCANode(ucaResponseDTOs?: UcaResponseDTO[], dependentElementNode?: DependentElementNode): void {
    if (entitiesExist<UcaResponseDTO>(ucaResponseDTOs)) {
      if (!this.ucaIsRoot) {
        dependentElementNode.children.push(ucaTitleNode);
      }

      ucaResponseDTOs.forEach((ucaResponseDTO: UcaResponseDTO): void => {
        let ucaNode: DependentElementNode = {
          entity: {
            uca: ucaResponseDTO,
            controllerConstraints: [],
            lossScenarios: [],
            name: ucaResponseDTO.name,
            description: ucaResponseDTO.description
          },
          name: ucaResponseDTO.name,
          entityTitle: ucaTitle,
          children: [],
          rootElement: false,
          moveElement: false
        };
        this.getLossScenarios(ucaResponseDTO, ucaNode);

        if (this.ucaIsRoot) {
          ucaNode.rootElement = true;
          this.dependentElementNodes.push(ucaNode);
        } else {
          ucaNode.moveElement = true;
          dependentElementNode.children.push(ucaNode);
        }
        this.dataSource.data = this.dependentElementNodes;
        this.treeControl.expandAll();
      });
    }
  }

  getLossScenarios(ucaResponseDTO: UcaResponseDTO, dependentElementNode?: DependentElementNode): void {
    this.lossScenarioDataService.getLossScenariosByUcaAndCAId(ucaResponseDTO.projectId, ucaResponseDTO.id, ucaResponseDTO.parentId)
      .then((lossScenarioResponseDTOs: LossScenarioResponseDTO[]) => {
        if (lossScenarioResponseDTOs.length > 0) {
          this.orderUCANodes(ucaResponseDTO, dependentElementNode, lossScenarioResponseDTOs);
        } else {
          this.getControllerConstraint(ucaResponseDTO, dependentElementNode);
        }
      }).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: null, detail: response.message });
    });
  }

  private orderUCANodes(uca: UcaResponseDTO,
                        dependentElementNode?: DependentElementNode,
                        lossScenarios?: LossScenarioResponseDTO[],
                        controllerConstraint?: ControllerConstraintResponseDTO,
                        hazards?: HazardResponseDTO[],
                        subHazardResponseDTOs?: SubHazardResponseDTO[]): void {
    if (lossScenarios) {
      this.addLossScenarioNode(lossScenarios, dependentElementNode);
      this.getControllerConstraint(uca, dependentElementNode);
    } else if (controllerConstraint) {
      this.addControllerConstraintNode(controllerConstraint, dependentElementNode, uca);
      this.getUCAHazardLink(uca, dependentElementNode);
    } else if (hazards) {
      this.addHazardLinkNode(hazards, dependentElementNode);
      this.getUCASubHazardLink(uca, dependentElementNode);
    } else if (subHazardResponseDTOs) {
      this.addSubHazardLinkNode(subHazardResponseDTOs, dependentElementNode);
    }
  }

  private getControllerConstraint(ucaResponseDTO: UcaResponseDTO, dependentElementNode?: DependentElementNode): void {
    this.ucaDataService.getControllerConstraintByUnsafeControlAction(ucaResponseDTO.projectId, ucaResponseDTO.parentId, ucaResponseDTO.id)
      .then((controllerConstraintDTO: ControllerConstraintResponseDTO) => {
        if (controllerConstraintDTO) {
          this.orderUCANodes(ucaResponseDTO, dependentElementNode, null, controllerConstraintDTO);
        } else {
          this.getUCAHazardLink(ucaResponseDTO, dependentElementNode);
        }
      }).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: null, detail: response.message });
    });
  }

  private addControllerConstraintNode(controllerConstraint: ControllerConstraintResponseDTO,
                                      dependentElementNode?: DependentElementNode,
                                      ucaResponseDTO?: UcaResponseDTO): void {
    if (entityExist(<ControllerConstraintResponseDTO> controllerConstraint)) {
      let controllerConstraintsNodeIndex: number;

      let controllerConstraintsNode: DependentElementNode = {
        name: controllerConstraintTitle,
        children: [],
        titleElement: true
      };

      let controllerConstraintNode: DependentElementNode = {
        entity: {
          constraintName: controllerConstraint.name,
          description: controllerConstraint.description,
          ucaId: ucaResponseDTO.id,
          controlActionId: ucaResponseDTO.parentId
        },
        name: controllerConstraint.name,
        children: [],
        entityTitle: controllerConstraintTitle,
        rootElement: false,
        moveElement: true,
      };

      dependentElementNode.entity.controllerConstraints.push(controllerConstraint);
      controllerConstraintsNodeIndex = dependentElementNode.children.push(controllerConstraintsNode) - 1;
      dependentElementNode.children[controllerConstraintsNodeIndex].children.push(controllerConstraintNode);

      this.dataSource.data = this.dependentElementNodes;
      this.treeControl.expandAll();
    }
  }

  private getUCASubHazardLink(ucaResponseDTO: UcaResponseDTO, dependentElementNode?: DependentElementNode): void {
    this.subHazardDataService.getHazardsByUCAId(ucaResponseDTO.projectId, ucaResponseDTO.parentId, ucaResponseDTO.id, this.page)
      .then((subhazardResponseDTOs: SubHazardResponseDTO[]) => {
        this.addSubHazardLinkNode(subhazardResponseDTOs, dependentElementNode);
      }).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: null, detail: response.message });
    });
  }

  private addSubHazardLinkNode(subhazardResponseDTOs: SubHazardResponseDTO[], dependentElementNode?: DependentElementNode): void {
    if (entitiesExist<SubHazardResponseDTO>(subhazardResponseDTOs)) {
      dependentElementNode.children.push(subHazardLiningTitle);

      subhazardResponseDTOs.forEach((subHazardResponse: SubHazardResponseDTO): void => {
        let subHazardLinkNode: DependentElementNode = {
          entity: subHazardResponse,
          name: subHazardResponse.name,
          children: [],
          entityTitle: hazardTitle,
          linkElement: true
        };
        dependentElementNode.children.push(subHazardLinkNode);

        this.dataSource.data = this.dependentElementNodes;
        this.treeControl.expandAll();
      });
    }
  }

  private getUCAHazardLink(ucaResponseDTO: UcaResponseDTO, dependentElementNode?: DependentElementNode): void {
    this.hazardDataService.getHazardsByUCAId(ucaResponseDTO.projectId, ucaResponseDTO.parentId, ucaResponseDTO.id, this.page)
      .then((hazards: HazardResponseDTO[]) => {
        if (hazards.length > 0) {
          this.orderUCANodes(ucaResponseDTO, dependentElementNode, null, null, hazards);
        } else {
          this.getUCASubHazardLink(ucaResponseDTO, dependentElementNode);
        }
      }).catch((response: HttpErrorResponse) => {
      }).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: null, detail: response.message });
    });
  }

  private addHazardLinkNode(hazardResponseDTOs: HazardResponseDTO[], dependentElementNode?: DependentElementNode): void {
    if (entitiesExist<HazardResponseDTO>(hazardResponseDTOs)) {
      dependentElementNode.children.push(hazardTitleNode);
      hazardResponseDTOs.forEach((hazard: HazardResponseDTO): void => {
        let hazLinkNode: DependentElementNode = {
          entity: hazard,
          name: hazard.name,
          children: [],
          entityTitle: hazardTitle,
          linkElement: true
        };
        dependentElementNode.children.push(hazLinkNode);

        this.dataSource.data = this.dependentElementNodes;
        this.treeControl.expandAll();
      });
    }
  }

  private orderControlActionNodes(controlAction: ControlActionDTO,
                                  dependentElementNode?: DependentElementNode,
                                  ucas?: UcaResponseDTO[],
                                  controlledProcess?: ControlledProcessDTO[]): void {
    if (ucas) {
      this.addUCANode(ucas, dependentElementNode);
      this.getControlledProcessByControlActionDestination(controlAction, dependentElementNode);
    } else if (controlledProcess) {
      this.addControlledProcessNode(controlledProcess, dependentElementNode);
      this.getControllerByControlActionSourceId(controlAction, dependentElementNode);
    }
  }

  private getControllerByControlActionSourceId(controlActionDTO: ControlActionDTO, dependentElementNode?: DependentElementNode): void {
    this.controllerDataService.getControllerByControlActionSourceArrowId(this.projectId, controlActionDTO.arrowId)
      .then((controller: ControllerDTO): void => {
        if (controller) {
          this.addControllerNode([controller], dependentElementNode);
        }
      });
  }

  private getControlledProcessByControlActionDestination(controlActionDTO: ControlActionDTO,
                                                         dependentElementNode?: DependentElementNode,
                                                         controlActionByBox?: boolean): void {
    this.controlledProcessDataService.getControlledProcessByControlActionDestinationArrowId(controlActionDTO.projectId, controlActionDTO.arrowId)
      .then((controlledProcess: ControlledProcessDTO) => {
        if (this.controlActionIsRoot) {
          this.orderControlActionControlStructureNodes(controlActionDTO, dependentElementNode, null, null, [controlledProcess]);
        } else {
          if (controlledProcess) {
            if (controlActionByBox) {
              this.orderControlActionControlStructureNodes(controlActionDTO, dependentElementNode, null, null, [controlledProcess]);
            } else {
              if (!this.controlActionIsRoot) {
                this.orderControlActionNodes(controlActionDTO, dependentElementNode, null, [controlledProcess]);
              } else {
                this.orderControlActionControlStructureNodes(controlActionDTO, dependentElementNode, null, null, [controlledProcess]);
              }
            }
          } else {
            if (!this.controlActionIsRoot) {
              this.getControllerByControlActionSourceId(controlActionDTO, dependentElementNode);
            }
          }
        }
      }).catch((response: HttpErrorResponse) => {
    });
  }

  private orderControlActionControlStructureNodes(controlActionDTO: ControlActionDTO,
                                                  dependentElementNode: DependentElementNode,
                                                  ucas?: UcaResponseDTO[],
                                                  actuators?: ActuatorDTO[],
                                                  controlledProcess?: ControlledProcessDTO[]): void {

    if (ucas) {
      this.addUCANode(ucas, dependentElementNode);
      this.getActuatorByControlActionDestination(controlActionDTO, dependentElementNode);
    } else if (actuators) {
      this.addActuatorNode(actuators, dependentElementNode);
      this.getControlledProcessByControlActionDestination(controlActionDTO, dependentElementNode, true);
    } else if (controlledProcess) {
      this.addControlledProcessNode(controlledProcess, dependentElementNode);
    }
  }

  private addLossScenarioNode(lossScenarioResponseDTOs: LossScenarioResponseDTO[], dependentElementNode?: DependentElementNode): void {
    if (entitiesExist<LossScenarioResponseDTO>(lossScenarioResponseDTOs)) {
      let lossScenariosNode: DependentElementNode = {
        name: lossScenarioTitle,
        children: [],
        titleElement: true
      };

      if (!this.lossScenarioIsRoot) {
        dependentElementNode.children.push(lossScenariosNode);
      }

      lossScenarioResponseDTOs.forEach((lossScenario: LossScenarioResponseDTO): void => {
        let lossScenarioNode: DependentElementNode = {
          entity: lossScenario,
          name: lossScenario.name,
          entityTitle: lossScenarioTitle,
          children: [],
          rootElement: false,
          moveElement: false
        };
        this.getImplementationConstraint(lossScenario, lossScenarioNode);
        if (!this.lossScenarioIsRoot) {
          dependentElementNode.entity.lossScenarios.push(lossScenarioNode);
        }

        if (this.lossScenarioIsRoot) {
          lossScenarioNode.rootElement = true;
          this.dependentElementNodes.push(lossScenarioNode);
        } else {
          lossScenarioNode.moveElement = true;
          dependentElementNode.children.push(lossScenarioNode);
        }
        this.dataSource.data = this.dependentElementNodes;
        this.treeControl.expandAll();
      });
    }
  }

  private getImplementationConstraint(lossScenarioResponseDTO: LossScenarioResponseDTO, dependentElementNode?: DependentElementNode): void {
    this.implementationConstraintDataService.getImplementationConstraintsByLsId(lossScenarioResponseDTO.projectId, lossScenarioResponseDTO.id)
      .then((implementationConstraintResponseDTO: ImplementationConstraintResponseDTO[]) => {
        this.addImplementationConstraintNode(implementationConstraintResponseDTO, dependentElementNode);
      }).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: null, detail: response.message });
    });
  }

  private addImplementationConstraintNode(implementationConstraintResponseDTOs: ImplementationConstraintResponseDTO[], dependentElementNode?: DependentElementNode): void {
    if (entitiesExist<ImplementationConstraintResponseDTO>(implementationConstraintResponseDTOs)) {
      if (!this.implementationConstraintIsRoot) {
        dependentElementNode.children.push(implementationConstraintTitleNode);
      }

      implementationConstraintResponseDTOs.forEach((implementationConstraint: ImplementationConstraintResponseDTO): void => {
        let implementationConstraintNode: DependentElementNode = {
          entity: implementationConstraint,
          name: implementationConstraint.name,
          children: [],
          entityTitle: implementationConstraintTitle,
          rootElement: false,
          moveElement: false
        };
        if (this.implementationConstraintIsRoot) {
          implementationConstraintNode.rootElement = true;
          this.dependentElementNodes.push(implementationConstraintNode);
        } else {
          implementationConstraintNode.moveElement = true;
          dependentElementNode.children.push(implementationConstraintNode);
        }
        this.dataSource.data = this.dependentElementNodes;
        this.treeControl.expandAll();
      });
    }
  }

  private getResponsibilitiesByController(controllerDTO: ControllerDTO, dependentElementNode?: DependentElementNode): void {
    this.responsibilityDataService.getResponsibilitiesByControllerId(controllerDTO.projectId, controllerDTO.id)
      .then((responsibilityResponseDTOs: ResponsibilityResponseDTO[]) => {
        if (responsibilityResponseDTOs.length > 0) {
          this.orderControllerNodes(controllerDTO, dependentElementNode, null, responsibilityResponseDTOs);
        } else {
          this.getProcessModel(controllerDTO, dependentElementNode);
        }
      }).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: null, detail: response.message });
    });
  }

  private addResponsibilityNode(responsibilityResponseDTOs: ResponsibilityResponseDTO[], dependentElementNode?: DependentElementNode): void {
    if (entitiesExist<ResponsibilityResponseDTO>(responsibilityResponseDTOs)) {
      if (!this.responsibilityIsRoot) {
        dependentElementNode.children.push(responsibilityTitleNode);
      }
      responsibilityResponseDTOs.forEach((responsibilityResponseDTO: ResponsibilityResponseDTO) => {
        let responsibilityNode: DependentElementNode = {
          entity: responsibilityResponseDTO,
          name: responsibilityResponseDTO.name,
          children: [],
          entityTitle: responsibilityTitle,
          rootElement: false,
          moveElement: false
        };

        this.getSystemContraintByResponsibility(responsibilityResponseDTO, responsibilityNode);
        if (this.responsibilityIsRoot) {
          responsibilityNode.rootElement = true;
          this.dependentElementNodes.push(responsibilityNode);
        } else {
          responsibilityNode.moveElement = true;
          dependentElementNode.children.push(responsibilityNode);
        }
        this.dataSource.data = this.dependentElementNodes;
        this.treeControl.expandAll();
      });
    }
  }

  private getSystemContraintByResponsibility(responsibilityResponseDTO: ResponsibilityResponseDTO, dependentElementNode?: DependentElementNode): void {

    this.systemConstraintService.getLinkedSystemConstraintByResponsibility(this.projectId, responsibilityResponseDTO.id)
      .then((systemConstraintResponseDTO: SystemConstraintResponseDTO[]): void => {
        this.addSystemConstraintNode(systemConstraintResponseDTO, dependentElementNode);
      });
  }

  private addSystemConstraintNode(systemConstraints?: SystemConstraintResponseDTO[], dependentElementNode?: DependentElementNode): void {
    if (entitiesExist<SystemConstraintResponseDTO>(systemConstraints)) {
      dependentElementNode.children.push(systemConstraintTitleNode);
      systemConstraints.forEach((systemConstraintDTO: SystemConstraintResponseDTO) => {
        let systemConstraintNode: DependentElementNode = {
          entity: systemConstraintDTO,
          name: systemConstraintDTO.name,
          entityTitle: systemConstraintTitle,
          children: [],
          linkElement: true
        };
        dependentElementNode.children.push(systemConstraintNode);
        this.dataSource.data = this.dependentElementNodes;
        this.treeControl.expandAll();
      });
    }
  }

  private getProcessModel(controllerDTO: ControllerDTO, dependentElementNode?: DependentElementNode): void {
    this.processModelDataService.getAllByControllerId(controllerDTO.projectId, controllerDTO.id)
      .then((processModels: ProcessModelResponseDTO[]) => {
        if (processModels.length > 0) {
          this.orderControllerNodes(controllerDTO, dependentElementNode, null, null, processModels);
        } else {
          this.getControlActionByControllerBoxId(controllerDTO, dependentElementNode);
        }
      }).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: null, detail: response.message });
    });
  }

  private addProcessModelNode(processModelResponseDTOs: ProcessModelResponseDTO[], dependentElementNode?: DependentElementNode): void {
    if (entitiesExist<ProcessModelResponseDTO>(processModelResponseDTOs)) {
      if (!this.processModelIsRoot) {
        dependentElementNode.children.push(processModelsNode);
      }

      processModelResponseDTOs.forEach((processModelResponseDTO: ProcessModelResponseDTO) => {
        let processModelNode: DependentElementNode = {
          entity: processModelResponseDTO,
          name: processModelResponseDTO.name,
          children: [],
          entityTitle: processModelTitle,
          rootElement: false,
          moveElement: false
        };
        this.getProcessVariable(processModelResponseDTO, processModelNode);

        if (this.processModelIsRoot) {
          processModelNode.rootElement = true;
          this.dependentElementNodes.push(processModelNode);
        } else {
          processModelNode.moveElement = true;
          dependentElementNode.children.push(processModelNode);
        }
        this.dataSource.data = this.dependentElementNodes;
        this.treeControl.expandAll();
      });
    }
  }

  private getProcessVariable(processModelResponseDTO: ProcessModelResponseDTO, dependentElementNode?: DependentElementNode): void {
    this.processVariableDataService.getAllProcessVariables(this.projectId)
      .then((processVariableResponseDTOs: ProcessVariableResponseDTO[]) => {
        if (processVariableResponseDTOs.length > 0) {
          this.loadProcessVariables(processVariableResponseDTOs, processModelResponseDTO, dependentElementNode);
        }
      }).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: null, detail: response.message });
    });
  }

  private loadProcessVariables(processVariableResponseDTOs: ProcessVariableResponseDTO[],
                               processModelResponseDTO: ProcessModelResponseDTO,
                               dependentElementNode?: DependentElementNode): void {
    let titleLoaded: boolean = false;

    if (entitiesExist<ProcessVariableResponseDTO>(processVariableResponseDTOs)) {
      processVariableResponseDTOs.forEach((processVariableResponseDTO: ProcessVariableResponseDTO) => {
        processVariableResponseDTO.process_models.forEach((processModelId: string): void => {
          if (processModelResponseDTO.id === processModelId) {
            if (!titleLoaded) {
              dependentElementNode.children.push(processsVariableNode);
              titleLoaded = true;
            }
            let processVariableNode: DependentElementNode = {
              entity: processVariableResponseDTO,
              name: processVariableResponseDTO.name,
              children: [],
              entityTitle: processVariableTitle,
            };
            dependentElementNode.children.push(processVariableNode);
          }
          this.dataSource.data = this.dependentElementNodes;
          this.treeControl.expandAll();
        });
      });
    }
  }

  private getControlActionByController(arrow?: Arrow, controllerDTO?: ControllerDTO, dependentElementNode?: DependentElementNode): void {
    this.controlActionDataService.getControlActionsByArrowId(this.projectId, arrow.id)
      .then((controlActions: ControlActionDTO[]) => {
        this.orderControllerControlStructureElementes(dependentElementNode, controllerDTO, controlActions);
      }).catch((reason: HttpErrorResponse) => {
    });
  }

  private getAllControlActionByControllerBoxId(controllerDTO?: ControllerDTO, dependentElementNode?: DependentElementNode): void {
    this.controlStructureDataService.loadRootControlStructure(this.projectId)
      .then((value: ControlStructureDTO): void => {
        const arrows: Arrow[] = value.arrows;
        const arrowsOfTypeControlAction = arrows.filter((arrow: Arrow) => arrow.source === controllerDTO.boxId && arrow.type === 'ControlAction');
        if (arrowsOfTypeControlAction.length > 0) {
          this.iterateControlActionByController(arrowsOfTypeControlAction, controllerDTO, dependentElementNode);
        } else {
          this.getAllOutputByControllerId(controllerDTO, dependentElementNode);
        }
      }).catch((reason: HttpErrorResponse) => {
      console.error(reason);
    });
  }

  private iterateControlActionByController(arrows?: Arrow[], controllerDTO?: ControllerDTO, dependentElementNode?: DependentElementNode): void {
    arrows.forEach((arrow: Arrow) => {
      this.getControlActionByController(arrow, controllerDTO, dependentElementNode);
    });
  }

  protected orderControllerControlStructureElementes(dependentElementNode?: DependentElementNode,
                                                     controllerDTO?: ControllerDTO,
                                                     controlActionDTOs?: ControlActionDTO[],
                                                     outputDTOs?: OutputDTO[]): void {
    if (controlActionDTOs) {
      this.addControlActionNode(controlActionDTOs, dependentElementNode, true);
      this.getAllOutputByControllerId(controllerDTO, dependentElementNode);
    } else if (outputDTOs) {
      this.addOutputNode(outputDTOs, dependentElementNode);
    }
  }

  private getOutputByArrowId(arrow?: Arrow, dependentElementNode?: DependentElementNode): void {
    this.outputDataService.getOutputsByArrowId(this.projectId, arrow.id)
      .then((outputs: OutputDTO[]) => {
        this.orderControllerControlStructureElementes(dependentElementNode, null, null, outputs);
      }).catch((reason: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: null, detail: reason.message });
    });
  }

  private getAllOutputByControllerId(controllerDTO?: ControllerDTO, dependentElementNode?: DependentElementNode): void {
    this.controlStructureDataService.loadRootControlStructure(this.projectId)
      .then((value: ControlStructureDTO): void => {
        const arrows: Arrow[] = value.arrows;
        const arrowsOfTypeOutput = arrows.filter((arrow: Arrow) => arrow.source === controllerDTO.boxId && arrow.type === 'Output');
        if (arrowsOfTypeOutput.length > 0) {
          arrowsOfTypeOutput.forEach((arrow: Arrow) => {
            this.getOutputByArrowId(arrow, dependentElementNode);
          });
        }
      }).catch((reason: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: null, detail: reason.message });
    });
  }

  private getControlActionByControllerBoxId(controllerDTO?: ControllerDTO, dependentElementNode?: DependentElementNode): void {
    this.controlActionDataService.getControlActionBySourceBoxId(controllerDTO.projectId, controllerDTO.boxId)
      .then((controlActionDTO: ControlActionDTO[]) => {
        if (controlActionDTO.length > 0) {
          this.orderControlStructureElements(controllerDTO, dependentElementNode, controlActionDTO);
        } else {
          this.getFeedbackByControllerBoxId(controllerDTO, dependentElementNode);
        }
      }).catch((response: HttpErrorResponse) => {
      this.getFeedbackByControllerBoxId(controllerDTO, dependentElementNode);
    });
  }

  private addControlActionControlStructureNode(controlActions: ControlActionDTO[],
                                               dependentElementNode?: DependentElementNode,
                                               controllerDTO?: ControllerDTO,
                                               controlStructureElement?: boolean): void {

    if (entitiesExist<ControlActionDTO>(controlActions)) {
      dependentElementNode.children.push(controlActionTitleNode);

      controlActions.forEach((controlActionDTO: ControlActionDTO) => {
        let controlActionNode: DependentElementNode = {
          entity: controlActionDTO,
          name: controlActionDTO.name,
          children: [],
          titleElement: false,
          rootElement: false,
          entityTitle: controlActionTitle,
          controlStructureElement: controlStructureElement
        };

        if (!this.inputIsRoot) {
          this.getUCAs(controlActionDTO, controlActionNode, true);
        }

        if (!this.inputIsRoot && !this.controllerIsRoot) {
          this.getActuatorByControlActionDestination(controlActionDTO, controlActionNode);
        }

        dependentElementNode.children.push(controlActionNode);
        this.dataSource.data = this.dependentElementNodes;
        this.treeControl.expandAll();
      });
    }
  }

  private orderControlStructureElements(controllerDTO: ControllerDTO,
                                        dependentElementNode?: DependentElementNode,
                                        controlActions?: ControlActionDTO[],
                                        feedbacks?: FeedbackDTO[],
                                        inputs?: InputDTO[],
                                        outputs?: OutputDTO[]): void {
    let linkedControlStructure: DependentElementNode = {
      name: stepTwoTitleElement,
      entityTitle: stepTwoTitleElement,
      children: [],
      titleElement: true,
    };

    if (entityExist<ControllerDTO>(controllerDTO)) {
      if (!dependentElementNode.titleElement) {
        dependentElementNode.children.push(linkedControlStructure);
      }

      if (controlActions) {
        this.addControlActionControlStructureNode(controlActions, linkedControlStructure, controllerDTO, true);
        this.getFeedbackByControllerBoxId(controllerDTO, linkedControlStructure);
      } else if (feedbacks) {
        if (dependentElementNode.titleElement) {
          this.addFeedbackNode(feedbacks, dependentElementNode);
          if (!this.inputIsRoot) {
            this.getInputByController(controllerDTO, dependentElementNode);
          }
        } else {
          this.addFeedbackNode(feedbacks, linkedControlStructure);
          if (!this.inputIsRoot) {
            this.getInputByController(controllerDTO, linkedControlStructure);
          }
        }
      } else if (inputs) {
        if (!this.inputIsRoot) {
          if (dependentElementNode.titleElement) {
            this.addInputNode(inputs, dependentElementNode);
            this.getOutputByController(controllerDTO, dependentElementNode);
          } else {
            this.addInputNode(inputs, linkedControlStructure);
            this.getOutputByController(controllerDTO, linkedControlStructure);
          }
        }
      } else if (outputs) {
        if (dependentElementNode.titleElement) {
          this.addOutputNode(outputs, dependentElementNode);
        } else {
          this.addOutputNode(outputs, linkedControlStructure);
        }
      }
    }
  }

  private getFeedbackByControllerBoxId(controllerDTO?: ControllerDTO, dependentElementNode?: DependentElementNode): void {
    this.feedbackDataService.getFeedbackByDestinationBoxId(controllerDTO.projectId, controllerDTO.boxId)
      .then((feedbacks: FeedbackDTO[]) => {
        if (feedbacks.length > 0) {
          this.orderControlStructureElements(controllerDTO, dependentElementNode, null, feedbacks);
        }
      }).catch((response: HttpErrorResponse) => {
      if (!this.inputIsRoot) {
        this.getInputByController(controllerDTO, dependentElementNode);
      }
    });
  }

  private getInputByController(controllerDTO: ControllerDTO, dependentElementNode?: DependentElementNode): void {
    this.inputDataService.getInputsByBoxDestinationId(controllerDTO.projectId, controllerDTO.boxId)
      .then((inputs: InputDTO[]) => {
        if (inputs.length > 0) {
          this.orderControlStructureElements(controllerDTO, dependentElementNode, null, null, inputs);
        }
      }).catch((response: HttpErrorResponse) => {
      this.getOutputByController(controllerDTO, dependentElementNode);
    });
  }

  private addInputNode(inputs: InputDTO[], dependentElementNode?: DependentElementNode): void {
    if (entitiesExist<InputDTO>(inputs)) {
      if (!this.inputIsRoot) {
        dependentElementNode.children.push(inputTitleNode);
      }

      inputs.forEach((input: InputDTO): void => {
        let inputNode: DependentElementNode = {
          entity: input,
          name: input.name,
          entityTitle: inputTitle,
          children: [],
          controlStructureElement: true
        };
        if (!this.controllerIsRoot) {
          this.getAllBoxesByInput(input, inputNode);
        }
        if (this.inputIsRoot) {
          this.dependentElementNodes.push(inputNode);
        } else {
          dependentElementNode.children.push(inputNode);
        }
        this.dataSource.data = this.dependentElementNodes;
        this.treeControl.expandAll();
      });
    }
  }

  private getAllBoxesByInput(input: InputDTO, dependentElementNode?: DependentElementNode): void {
    this.inputDataService.getInputsDestinations(this.projectId, input)
      .then((boxes: Box[]): void => {
        this.loadBoxByTypes(boxes, dependentElementNode);
      });
  }

  private loadBoxByTypes(boxes: Box[], dependentElementNode?: DependentElementNode): void {
    const controllerBoxes: Box[] = boxes.filter((box: Box) => box.type === 'Controller');
    const controlledProcessBoxes: Box[] = boxes.filter((box: Box) => box.type === 'ControlledProcess');
    controllerBoxes.forEach((controllerBox: Box): void => {
      this.getControllerByInput(controllerBox, dependentElementNode);
    });

    controlledProcessBoxes.forEach((controlledProcessBox: Box): void => {
      this.getControlledProcessByInput(controlledProcessBox, dependentElementNode);
    });
  }

  private getControllerByInput(controllerBox: Box, dependentElementNode?: DependentElementNode): void {
    this.controllerDataService.getControllerByBoxId(this.projectId, controllerBox.id)
      .then((controllerDTO: ControllerDTO): void => {
        this.addControllerNode([controllerDTO], dependentElementNode);
      });
  }

  private getControlledProcessByInput(controlledProcessBox: Box, dependentElementNode?: DependentElementNode): void {
    this.controlledProcessDataService.getControlledProcessByBoxId(this.projectId, controlledProcessBox.id)
      .then((controlledProcess: ControlledProcessDTO) => {
        this.addControlledProcessNode([controlledProcess], dependentElementNode);
      }).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: null, detail: response.message });
    });
  }

  private addControlledProcessNode(controlledProcesses: ControlledProcessDTO[], dependentElementNode?: DependentElementNode): void {
    if (!this.controlledProcessIsRoot) {
      dependentElementNode.children.push(controlledProcessesTitleNode);
    }

    controlledProcesses.forEach((controlledProcess: ControlledProcessDTO) => {
      let processNode: DependentElementNode = {
        entity: controlledProcess,
        name: controlledProcess.name,
        entityTitle: controlledProcessTitle,
        children: [],
        rootElement: false,
        linkElement: false,
        controlStructureElement: false
      };

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

  private getOutputByController(controllerDTO: ControllerDTO, dependentElementNode?: DependentElementNode): void {
    this.outputDataService.getOutputsByBoxId(controllerDTO.projectId, controllerDTO.boxId)
      .then((outputs: OutputDTO[]) => {
        if (outputs.length > 0) {
          this.orderControlStructureElements(controllerDTO, dependentElementNode, null, null, null, outputs);
        }
      }).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: null, detail: response.message });
    });
  }

  private addOutputNode(outputs: OutputDTO[], dependentElementNode?: DependentElementNode): void {
    if (entitiesExist<OutputDTO>(outputs)) {
      dependentElementNode.children.push(outputTitleNode);

      outputs.forEach((outputDTO: OutputDTO): void => {
        let outputNode: DependentElementNode = {
          entity: outputDTO,
          name: outputDTO.name,
          entityTitle: outputTitle,
          children: [],
          controlStructureElement: true,
          rootElement: false
        };

        if (!this.outputIsRoot) {
          outputNode.controlStructureElement = true;
          dependentElementNode.children.push(outputNode);
        } else {
          outputNode.rootElement = true;
          this.dependentElementNodes.push(outputNode);
        }
        this.dataSource.data = this.dependentElementNodes;
        this.treeControl.expandAll();
      });
    }
  }

  private getActuatorByControlActionDestination(controlActionDTO: ControlActionDTO, dependentElementNode?: DependentElementNode): void {
    this.actuatorDataService.getActuatorByControlActionDestinationArrowId(controlActionDTO.projectId, controlActionDTO.arrowId)
      .then((actuatorDTO: ActuatorDTO) => {
        if (!this.actuatorIsRoot) {
          this.orderControlActionControlStructureNodes(controlActionDTO, dependentElementNode, null, [actuatorDTO]);
        }
      }).catch((response: HttpErrorResponse) => {
      if (this.controlActionIsRoot && !this.actuatorIsRoot) {
        this.getControlledProcessByControlActionDestination(controlActionDTO, dependentElementNode, true);
      }
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
          rootElement: false,
          controlStructureElement: false,
          linkElement: false,
          entityTitle: actuatorTitle
        };

        if (!this.controllerIsRoot && !this.controlActionIsRoot) {
          this.getConverationTablesByActuator(actuator, actuatorNode);
        }

        if (this.actuatorIsRoot) {
          actuatorNode.controlStructureElement = true;
          this.dependentElementNodes.push(actuatorNode);
        } else {
          actuatorNode.linkElement = true;
          dependentElementNode.children.push(actuatorNode);
        }
      });
      this.dataSource.data = this.dependentElementNodes;
      this.treeControl.expandAll();
    }
  }

  private getConverationTablesByActuator(actuator: ActuatorDTO, dependentElementNode: DependentElementNode): void {
    this.converationTableDataService.getAllConversions(this.projectId, actuator.id, {})
      .then((converation: ConversionResponseDTO[]) => {
        if (converation.length > 0) {
          this.orderActuatorNode(actuator, dependentElementNode, null, converation);
        } else {
          this.getControlActionByActuatorDestinationBoxId(actuator, dependentElementNode);
        }
      });
  }

  private addConversionNode(conversionDataResponse?: ConversionResponseDTO[], dependentElementNode?: DependentElementNode): void {
    if (!this.converationIsRoot) {
      dependentElementNode.children.push(ConverationTitleNode);
    }

    conversionDataResponse.forEach((conversion: ConversionResponseDTO): void => {
      let conversionNode: DependentElementNode = {
        entity: conversion,
        name: conversion.conversion,
        children: [],
        rootElement: false,
        moveElement: false,
        entityTitle: converationTitle
      };

      if (this.converationIsRoot) {
        conversionNode.rootElement = true;
        this.dependentElementNodes.push(conversionNode);
      } else {
        conversionNode.moveElement = true;
        dependentElementNode.children.push(conversionNode);
      }
      this.dataSource.data = this.dependentElementNodes;
      this.treeControl.expandAll();
    });
  }

  private orderActuatorNode(actuator?: ActuatorDTO,
                            dependentElementNode?: DependentElementNode,
                            controlActionDTOs?: ControlActionDTO[],
                            converationTable?: ConversionResponseDTO[]): void {

    if (converationTable) {
      this.addConversionNode(converationTable, dependentElementNode);
      this.getControlActionByActuatorDestinationBoxId(actuator, dependentElementNode);
    } else if (controlActionDTOs) {
      this.addControlActionNode(controlActionDTOs, dependentElementNode, true);
      this.getControlActionByActuatorSourceBoxId(actuator, dependentElementNode);
    }
  }

  private getControlActionByActuatorSourceBoxId(actuator: ActuatorDTO, dependentElementNode?: DependentElementNode): void {
    this.controlActionDataService.getControlActionByDestinationBoxId(this.projectId, actuator.boxId)
      .then((controlActions: ControlActionDTO[]) => {
        if (controlActions.length > 0) {
          this.addControlActionNode(controlActions, dependentElementNode, true);
        }
      }).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: null, detail: response.message });
    });
  }

  private getControlActionByActuatorDestinationBoxId(actuator: ActuatorDTO, dependentElementNode?: DependentElementNode): void {
    this.controlActionDataService.getControlActionBySourceBoxId(this.projectId, actuator.boxId)
      .then((controlActions: ControlActionDTO[]) => {
        if (controlActions.length > 0) {
          this.orderActuatorNode(actuator, dependentElementNode, controlActions);
        } else {
          this.getControlActionByActuatorSourceBoxId(actuator, dependentElementNode);
        }
      }).catch((response: HttpErrorResponse) => {
      this.getControlActionByActuatorSourceBoxId(actuator, dependentElementNode);
      this.messageService.add({ severity: 'error', summary: null, detail: response.message });
    });
  }

  private getSensorsByFeedBackSource(feedback: FeedbackDTO, dependentElementNode?: DependentElementNode): void {
    this.sensorDataService.getSensorByFeedbackSourceArrowId(feedback.projectId, feedback.arrowId)
      .then((sensorDTO: SensorDTO) => {
        if (sensorDTO) {
          this.orderFeedbackNodes(feedback, dependentElementNode, [sensorDTO]);
        } else {
          this.getControlledProcessByFeedbackSource(feedback, dependentElementNode);
        }
      }).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: null, detail: response.message });
    });
  }

  private orderFeedbackNodes(feedback?: FeedbackDTO,
                             dependentElementNode?: DependentElementNode,
                             sensors?: SensorDTO[],
                             controlledProcess?: ControlledProcessDTO[]): void {
    if (sensors) {
      this.addSensorNode(sensors, dependentElementNode);
      this.getControlledProcessByFeedbackSource(feedback, dependentElementNode);
    } else if (controlledProcess) {
      this.addControlledProcessNode(controlledProcess, dependentElementNode);
    }
  }

  private getControlledProcessByFeedbackSource(feedback: FeedbackDTO, dependentElementNode?: DependentElementNode): void {
    this.controlledProcessDataService.getControlledProcessByFeedbackSourceArrowId(this.projectId, feedback.arrowId)
      .then((controlledProcessDTO: ControlledProcessDTO) => {
        if (controlledProcessDTO) {
          this.orderFeedbackNodes(feedback, dependentElementNode, null, [controlledProcessDTO]);
        }
      }).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: null, detail: response.message });
    });
  }

  private addSensorNode(sensorDTOs: SensorDTO[], dependentElementNode?: DependentElementNode): void {
    if (entityExist<SensorDTO[]>(sensorDTOs)) {
      if (!this.sensorIsRoot) {
        dependentElementNode.children.push(sensorTitleNode);
      }

      sensorDTOs.forEach((sensorDTO: SensorDTO): void => {
        let sensorNode: DependentElementNode = {
          entity: sensorDTO,
          name: sensorDTO.name,
          children: [],
          rootElement: false,
          controlStructureElement: false,
          entityTitle: sensorTitle
        };

        if (this.sensorIsRoot) {
          sensorNode.rootElement = true;
          this.dependentElementNodes.push(sensorNode);
        } else {
          sensorNode.controlStructureElement = true;
          dependentElementNode.children.push(sensorNode);
        }
      });
      this.dataSource.data = this.dependentElementNodes;
      this.treeControl.expandAll();
    }
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
          controlStructureElement: false,
          rootElement: false,
          entityTitle: feedbackTitle
        };

        if (!this.controllerIsRoot) {
          this.getSensorsByFeedBackSource(feedbackDTO, feedbackNode);
        }

        if (this.feedbackIsRoot) {
          feedbackNode.rootElement = true;
          this.dependentElementNodes.push(feedbackNode);
        } else {
          feedbackNode.controlStructureElement = true;
          dependentElementNode.children.push(feedbackNode);
        }
      });
      this.dataSource.data = this.dependentElementNodes;
      this.treeControl.expandAll();
    }
  }
}









