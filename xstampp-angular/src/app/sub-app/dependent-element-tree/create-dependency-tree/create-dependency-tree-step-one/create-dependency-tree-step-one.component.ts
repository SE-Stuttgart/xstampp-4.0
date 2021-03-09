import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  HazardResponseDTO,
  LossResponseDTO,
  PageRequest,
  SubHazardResponseDTO,
  SubSystemConstraintResponseDTO,
  SystemConstraintResponseDTO,
  UcaResponseDTO,
} from '../../../../types/local-types';
import {
  DependentElementNode,
  DependentTreeNode, entitiesExist, entityExist,
  hazardTitleNode,
  hazardTitle,
  losseTitleNode, lossTitle,
  SelectedEntity,
  subHazardTitleNode,
  subHazardTitle,
  subSystemConstraintsNode,
  subSystemConstraintTitle,
  systemConstraintTitleNode,
  systemConstraintTitle,
  ucaTitle, ucaLinkNode
} from '../../dependent-elements-types.component';
import { HttpErrorResponse } from '@angular/common/http';
import {
  ProcessModelDataService,
} from '../../../../services/dataServices/control-structure/process-model-data.service';
import { FilterService } from '../../../../services/filter-service/filter.service';
import { MatDialog, MatDialogRef, MatTreeFlatDataSource, MatTreeFlattener } from '@angular/material';
import { ControlActionDataService } from '../../../../services/dataServices/control-structure/control-action-data.service';
import { RuleDataService } from '../../../../services/dataServices/rule-data.service';
import { ResponsibilityDataService } from '../../../../services/dataServices/responsibility-data.service';
import { ControllerDataService } from '../../../../services/dataServices/control-structure/controller-data.service';
import { FeedbackDataService } from '../../../../services/dataServices/control-structure/feedback-data.service';
import { SensorDataService } from '../../../../services/dataServices/control-structure/sensor-data.service';
import { InputDataService } from '../../../../services/dataServices/control-structure/input-data.service';
import { LossScenarioDataService } from '../../../../services/dataServices/loss-scenario.service';
import { MessageService } from 'primeng/api';
import { BoxDataService } from '../../../../services/dataServices/box-data.service';
import { LockService } from '../../../../services/dataServices/lock.service';
import { ChangeDetectionService } from '../../../../services/change-detection/change-detection-service.service';
import { UcaDataService } from '../../../../services/dataServices/uca-data.service';
import { ControllerConstraintDataService } from '../../../../services/dataServices/controller-constraint-data.service';
import { ImplementationConstraintDataService } from '../../../../services/dataServices/implementation-constraint-data.service';
import { SystemLevelSafetyConstraintDataService } from '../../../../services/dataServices/system-level-safety-constraint-data.service';
import { SubSystemConstraintDataService } from '../../../../services/dataServices/sub-system-constraint-data.service';
import { HazardDataService } from '../../../../services/dataServices/hazard-data.service';
import { SubHazardDataService } from '../../../../services/dataServices/sub-hazard-data.service';
import { ProcessVariableDataService } from '../../../../services/dataServices/control-structure/process-variable-data.service';
import { ActuatorDataService } from '../../../../services/dataServices/control-structure/actuator-data.service';
import { ControlStructureDataService } from '../../../../services/dataServices/control-structure-data.service';
import { ControlledProcessDataService } from '../../../../services/dataServices/control-structure/controlled-process-data.service';
import { LossDataService } from '../../../../services/dataServices/loss-data.service';
import { FlatTreeControl } from '@angular/cdk/tree';
import { DependentElementDetailsComponent } from '../../dependent-element-details/dependent-element-details.component';
import { MoveEntityComponent } from '../../move-entity/move-entity.component';

/**
 * This component creates the dependency tree of the first step.
 */
@Component({
  selector: 'app-create-dependency-tree-step-one',
  templateUrl: './create-dependency-tree-step-one.component.html',
  styleUrls: ['./create-dependency-tree-step-one.component.css']
})
export class CreateDependencyTreeStepOneComponent {

  @Input() selectedEntity: SelectedEntity<Object>;
  @Output() entitySelectedEvent: any = new EventEmitter<DependentElementNode[]>();

  private projectId: string;
  private page: PageRequest = { from: 0, amount: 100};
  allSubHazards: SubHazardResponseDTO[];
  dependencyTree: DependentElementNode[];
  displayedColumns: string[] = ['name'];
  hazardIsRoot: boolean = false;
  subHazardIsRoot: boolean = false;
  systemConstraintIsRoot: boolean = false;
  lossIsRoot: boolean = false;
  subSystemConstraintIsRoot: boolean = false;
  alreadyLoaded: boolean = false;

  constructor(public filterService: FilterService,
              public moveEntityDialog: MatDialog,
              public dependentElementDetails: MatDialog,
              public dialogRef: MatDialogRef<CreateDependencyTreeStepOneComponent>,
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
              private readonly lossDataService: LossDataService) {
  }

  treeControl: FlatTreeControl<DependentElementNode> = new FlatTreeControl<DependentTreeNode>((node: any): number => node.level, (node: any): boolean => node.expandable);
  treeFlattener: any = new MatTreeFlattener((node: DependentElementNode, level: number): any => {
    return {
      expandable: !!node.children && node.children.length > 0,
      name: node.name,
      children: node.children,
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

  selectedEntityToMove(node?: DependentElementNode): void {
    let entityTitle: string = node.entityTitle;
    switch (entityTitle) {
      case subHazardTitle: {
        this.hazardDataService.getAllHazards(this.projectId, {})
          .then((hazards: HazardResponseDTO[]) => {
            this.openMoveDialog<HazardResponseDTO>(hazards, node);
          });
        break;
      }
      case subSystemConstraintTitle: {
        if (this.systemConstraintIsRoot || this.subSystemConstraintIsRoot) {
          this.systemConstraintService.getAllSafetyConstraints(this.projectId, {})
            .then((systemConstraints: SystemConstraintResponseDTO[]): void => {
              this.openMoveDialog(systemConstraints, node);
            });
        } else {
          this.openMoveDialog(this.allSubHazards, node);
        }
        break;
      }
      default: {
        this.dialogRef.close();
        break;
      }
    }
  }

  private getAllHazards(): void {
    this.hazardDataService.getAllHazards(this.projectId, {})
      .then((hazards: HazardResponseDTO[]) => {
        if (hazards) {
          hazards.forEach((hazard: HazardResponseDTO): void => {
            this.getSubHazards(hazard);
          });
        }
      });
  }

  private getSubHazards(hazard: HazardResponseDTO): void {
    this.subHazardDataService.getAllSubHazards(this.projectId, hazard.id, {})
      .then((subHazards: SubHazardResponseDTO[]): void => {
        subHazards.forEach((subHazard: SubHazardResponseDTO): void => {
          this.allSubHazards.push(subHazard);
        });
      });
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

  detailedInformation(node?: DependentElementNode): void {
    this.dependentElementDetails.open(DependentElementDetailsComponent, {
      width: 'auto',
      height: 'auto',
      data: {
        node: node
      }
    });
  }

  cancelDialog(): void {
    this.dialogRef.close(true);
  }

  deleteDependentElementTree(): void {
    this.entitySelectedEvent.emit(this.dependencyTree);
  }

  loadData(): void {
    this.dependencyTree = [];
    this.allSubHazards = [];
    this.alreadyLoaded = false;

    if (entityExist<string>(this.selectedEntity.projectId)) {
      this.projectId = this.selectedEntity.projectId;
    }
    switch (this.selectedEntity.entityTitle) {
      case lossTitle: {
        this.lossIsRoot = true;
        this.addLossNode(<LossResponseDTO[]> this.selectedEntity.entity);
        break;
      }
      case hazardTitle: {
        this.hazardIsRoot = true;
        this.addHazardNode(<HazardResponseDTO[]> this.selectedEntity.entity);
        break;
      }
      case systemConstraintTitle: {
        this.systemConstraintIsRoot = true;
        this.addSystemConstraintNode(<SystemConstraintResponseDTO[]> this.selectedEntity.entity);
        break;
      }
      case subHazardTitle: {
        this.subHazardIsRoot = true;
        this.addSubHazardNode(<SubHazardResponseDTO[]> this.selectedEntity.entity);
        break;
      }
      case subSystemConstraintTitle: {
        this.subSystemConstraintIsRoot = true;
        this.addSubSystemConstraintNode(<SubSystemConstraintResponseDTO[]> this.selectedEntity.entity);
        break;
      }
      default: {
        this.dialogRef.close();
        break;
      }
    }
  }

  private addLossNode(lossResponseDTOs: LossResponseDTO[]): void {
    lossResponseDTOs.forEach((lossResponseDTO: LossResponseDTO) => {
      let lossNode: DependentElementNode = {
        entity: lossResponseDTO,
        name: lossResponseDTO.name,
        children: [],
        rootElement: true,
        entityTitle: lossTitle
      };

      this.getHazardByLoss(lossResponseDTO, lossNode);
      this.dependencyTree.push(lossNode);
      this.dataSource.data = this.dependencyTree;
      this.treeControl.expandAll();
    });
  }

  private getHazardByLoss(lossResponseDTO: LossResponseDTO, dependentElementNode?: DependentElementNode): void {
    this.lossDataService.getHazardsByLossId(this.projectId, lossResponseDTO.id, this.page)
      .then((hazards: HazardResponseDTO[]) => {
        this.addHazardNode(hazards, dependentElementNode);
      });
  }

  private addHazardNode(hazardResponseDTOs: HazardResponseDTO[], dependentElementNode?: DependentElementNode): void {
    if (entitiesExist<HazardResponseDTO>(hazardResponseDTOs)) {
      if (!this.hazardIsRoot) {
        dependentElementNode.children.push(hazardTitleNode);
      }

      hazardResponseDTOs.forEach((hazardResponseDTO: HazardResponseDTO) => {
        let hazardNode: DependentElementNode = {
          entity: hazardResponseDTO,
          name: hazardResponseDTO.name,
          children: [],
          rootElement: false,
          linkElement: false,
          entityTitle: hazardTitle
        };

        if (this.hazardIsRoot) {
          this.getSubHazard(hazardResponseDTO, hazardNode);
          hazardNode.rootElement = true;
          this.dependencyTree.push(hazardNode);
        } else {
          hazardNode.linkElement = true;
          dependentElementNode.children.push(hazardNode);
        }
        this.dataSource.data = this.dependencyTree;
        this.treeControl.expandAll();
      });
    }
  }

  private getSubHazard(hazardResponseDTO: HazardResponseDTO, dependentElementNode?: DependentElementNode): void {
    if (hazardResponseDTO.id !== null) {
      this.subHazardDataService.getAllSubHazards(this.projectId, hazardResponseDTO.id, {})
        .then((subHazardResponseDTOs: SubHazardResponseDTO[]) => {
          if (subHazardResponseDTOs.length > 0) {
            this.orderHazardNodes(hazardResponseDTO, dependentElementNode, subHazardResponseDTOs);
          } else {
            this.getSafetyConstraintByHazard(hazardResponseDTO, dependentElementNode);
          }
        }).catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: null, detail: response.message });
      });
    }
  }

  private orderHazardNodes(hazardResponseDTO: HazardResponseDTO,
                           dependentElementNode?: DependentElementNode,
                           subHazardResponseDTO?: SubHazardResponseDTO[],
                           systemConstraint?: SystemConstraintResponseDTO[],
                           uca?: UcaResponseDTO[]): void {
    if (subHazardResponseDTO) {
      this.addSubHazardNode(subHazardResponseDTO, dependentElementNode);
      this.getSafetyConstraintByHazard(hazardResponseDTO, dependentElementNode);
    } else if (systemConstraint) {
      this.addSystemConstraintNode(systemConstraint, dependentElementNode);
      this.getUcaByHazard(hazardResponseDTO, dependentElementNode);
    } else if (uca) {
      this.addUCAHazardLinkNode(uca, dependentElementNode);
      this.getLossByHazard(hazardResponseDTO, dependentElementNode);
    } else {
      this.getLossByHazard(hazardResponseDTO, dependentElementNode);
    }
  }

  private addSubHazardNode(subHazardResponseDTOs: SubHazardResponseDTO[],
                           dependentElementNode?: DependentElementNode,
                           hazardResponseDTO?: HazardResponseDTO): void {

    if (entitiesExist<SubHazardResponseDTO>(subHazardResponseDTOs)) {

      if (!this.subHazardIsRoot) {
        dependentElementNode.children.push(subHazardTitleNode);
      }

      if (!this.alreadyLoaded) {
        this.getAllHazards();
        this.alreadyLoaded = true;
      }

      subHazardResponseDTOs.forEach((subHazard: SubHazardResponseDTO): void => {
        let subHazNode: DependentElementNode;

        if (hazardResponseDTO) {
          subHazNode = {
            entity: {
              id: subHazard.id,
              hazardId: hazardResponseDTO.id,
              name: subHazard.name,
              description: subHazard.description,
              state: 'TODO'
            },
            entityId: subHazard.id,
            name: subHazard.name,
            children: [],
            rootElement: false,
            moveElement: false,
            linkElement: false,
            entityTitle: subHazardTitle
          };
        } else {
          subHazNode = {
            entity: {
              id: subHazard.id,
              hazardId: subHazard.parentId,
              name: subHazard.name,
              description: subHazard.description,
              state: 'TODO'
            },
            entityId: subHazard.id,
            name: subHazard.name,
            children: [],
            rootElement: false,
            moveElement: false,
            linkElement: false,
            entityTitle: subHazardTitle
          };
        }

        if (this.hazardIsRoot) {
          this.getSubSystemBySubHazard(subHazard, subHazNode);
        }

        if (this.subHazardIsRoot) {
          this.getSubSystemConstraintBySubHazard(subHazard, subHazNode);
        }

        if (this.subHazardIsRoot) {
          subHazNode.rootElement = true;
          this.dependencyTree.push(subHazNode);
        } else {
          if (this.hazardIsRoot) {
            subHazNode.moveElement = true;
          } else {
            subHazNode.linkElement = true;
          }
          dependentElementNode.children.push(subHazNode);
        }
        this.dataSource.data = this.dependencyTree;
        this.treeControl.expandAll();
      });
    }
  }

  private orderSubHazardNodes(subHazardResponseDTO: SubHazardResponseDTO,
                              dependentElementNode: DependentElementNode,
                              subSystemConstraintResponseDTO?: SubSystemConstraintResponseDTO): void {
    if (subSystemConstraintResponseDTO) {
      this.addSubSystemConstraintNode([subSystemConstraintResponseDTO], dependentElementNode, subHazardResponseDTO);
      this.getUcaBySubHazard(subHazardResponseDTO, dependentElementNode);
    } else {
      this.getUcaBySubHazard(subHazardResponseDTO, dependentElementNode);
    }
  }

  private getUcaBySubHazard(subHazardResponseDTO: SubHazardResponseDTO, dependentElementNode: DependentElementNode): void {
    this.ucaDataService.getUcaBySubHazard(this.projectId, subHazardResponseDTO.parentId, subHazardResponseDTO.id, {})
      .then((ucas: UcaResponseDTO[]) => {
        if (ucas.length > 0) {
          this.addUCAHazardLinkNode(ucas, dependentElementNode);
        }
      }).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: null, detail: response.message });
    });
  }

  private getSubSystemConstraintBySubHazard(subHazardResponseDTO: SubHazardResponseDTO, dependentElementNode: DependentElementNode): void {
    this.subHazardDataService.getSubConstraintBySubHazardId(this.projectId, subHazardResponseDTO.parentId, subHazardResponseDTO.id)
      .then((subSystemConstraint: SubSystemConstraintResponseDTO) => {
        if (subSystemConstraint) {
          this.orderSubHazardNodes(subHazardResponseDTO, dependentElementNode, subSystemConstraint);
        } else {
          this.getUcaBySubHazard(subHazardResponseDTO, dependentElementNode);
        }
      }).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: null, detail: response.message });
    });
  }

  private getSafetyConstraintByHazard(hazardResponseDTO: HazardResponseDTO, dependentElementNode: DependentElementNode): void {
    this.hazardDataService.getSystemConstraintsByHazardId(this.projectId, hazardResponseDTO.id, this.page)
      .then((systemConstraintResponseDTOs: SystemConstraintResponseDTO[]) => {
        if (systemConstraintResponseDTOs.length > 0) {
          this.orderHazardNodes(hazardResponseDTO, dependentElementNode, null, systemConstraintResponseDTOs);
        } else {
          this.getUcaByHazard(hazardResponseDTO, dependentElementNode);
        }
      }).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: null, detail: response.message });
    });
  }

  private addSystemConstraintNode(systemConstraints?: SystemConstraintResponseDTO[], dependentElementNode?: DependentElementNode): void {
    if (entitiesExist<SystemConstraintResponseDTO>(systemConstraints)) {
      if (!this.systemConstraintIsRoot) {
        dependentElementNode.children.push(systemConstraintTitleNode);
      }

      systemConstraints.forEach((systemConstraintDTO: SystemConstraintResponseDTO) => {
        let systemConstraintNode: DependentElementNode = {
          entity: systemConstraintDTO,
          name: systemConstraintDTO.name,
          children: [],
          rootElement: false,
          linkElement: false,
          entityTitle: systemConstraintTitle
        };

        if (this.systemConstraintIsRoot) {
          this.getSubConstraint(systemConstraintDTO, systemConstraintNode);
          systemConstraintNode.rootElement = true;
          this.dependencyTree.push(systemConstraintNode);
        } else {
          systemConstraintNode.linkElement = true;
          dependentElementNode.children.push(systemConstraintNode);
        }
        this.dataSource.data = this.dependencyTree;
        this.treeControl.expandAll();
      });
    }
  }

  private orderSystemConstraintNodes(systemConstraintResponseDTO: SystemConstraintResponseDTO,
                                     dependentElementNode: DependentElementNode,
                                     subSystemConstraintResponseDTO?: SubSystemConstraintResponseDTO[]): void {
    if (subSystemConstraintResponseDTO) {
      this.addSubSystemConstraintNode(subSystemConstraintResponseDTO, dependentElementNode);
      this.getHazardBySystemConstraints(systemConstraintResponseDTO, dependentElementNode);
    } else {
      this.getHazardBySystemConstraints(systemConstraintResponseDTO, dependentElementNode);
    }
  }

  private getHazardBySystemConstraints(systemConstraint: SystemConstraintResponseDTO, dependentElementNode?: DependentElementNode): void {
    this.systemConstraintService.getHazardsBySafetyConstraintId(this.projectId, systemConstraint.id, this.page)
      .then((hazards: HazardResponseDTO[]) => {
        this.addHazardNode(hazards, dependentElementNode);
      }).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: null, detail: response.message });
    });
  }

  private getSubConstraint(systemConstraintResponseDTO: SystemConstraintResponseDTO, dependentElementNode?: DependentElementNode): void {
    if (entityExist<SystemConstraintResponseDTO>(systemConstraintResponseDTO)) {
      this.subSystemConstraintDataService.getAllSubSystemConstraints(this.projectId, systemConstraintResponseDTO.id, {})
        .then((subSystemConstraintResponseDTOs: SubSystemConstraintResponseDTO[]) => {
          if (subSystemConstraintResponseDTOs.length > 0) {
            this.orderSystemConstraintNodes(systemConstraintResponseDTO, dependentElementNode, subSystemConstraintResponseDTOs);
          } else {
            this.getHazardBySystemConstraints(systemConstraintResponseDTO, dependentElementNode);
          }
        }).catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: null, detail: response.message });
      });
    }
  }

  private addSubSystemConstraintNode(subSystemConstraintResponseDTOs: SubSystemConstraintResponseDTO[],
                                   dependentElementNode?: DependentElementNode,
                                   subHazardResponseDTO?: SubHazardResponseDTO): void {
    if (entitiesExist<SubSystemConstraintResponseDTO>(subSystemConstraintResponseDTOs)) {
      if (!this.subSystemConstraintIsRoot) {
        dependentElementNode.children.push(subSystemConstraintsNode);
      }

      subSystemConstraintResponseDTOs.forEach((subSystemConstraint: SubSystemConstraintResponseDTO) => {
        let subSystemConstraintNode: DependentElementNode;

        subSystemConstraintNode = {
          entity: {
            id: subSystemConstraint.id,
            name: subSystemConstraint.name,
            parentId: subSystemConstraint.parentId,
            description: subSystemConstraint.description,
            lastEdited: subSystemConstraint.lastEdited,
            lastEditor: subSystemConstraint.lastEditor,
            lastEditorId: subSystemConstraint.lastEditorId,
            lockExpirationTime: subSystemConstraint.lockExpirationTime,
            lockHolderDisplayName: subSystemConstraint.lockHolderDisplayName,
            lockHolderId: subSystemConstraint.lockHolderId,
            hazardId: '',
            subHazardId: ''
          },
          name: subSystemConstraint.name,
          children: [],
          moveElement: true,
          entityTitle: subSystemConstraintTitle
        }

        if (this.subSystemConstraintIsRoot) {
          this.dependencyTree.push(subSystemConstraintNode);
        } else {
          if (!this.systemConstraintIsRoot) {
            subSystemConstraintNode.entity.subHazardId = subHazardResponseDTO.id;
            subSystemConstraintNode.entity.hazardId = subHazardResponseDTO.parentId;
          }

          dependentElementNode.children.push(subSystemConstraintNode);
        }

        this.dataSource.data = this.dependencyTree;
        this.treeControl.expandAll();
      });
    }
  }

  private getUcaByHazard(hazardResponseDTO: HazardResponseDTO, dependentElementNode?: DependentElementNode): void {
    if (hazardResponseDTO.id !== null) {
      this.ucaDataService.getUcaByHazard(this.projectId, hazardResponseDTO.id, {})
        .then((ucas: UcaResponseDTO[]) => {
          if (ucas.length > 0) {
            this.orderHazardNodes(hazardResponseDTO, dependentElementNode, null, null, ucas);
          } else {
            this.getLossByHazard(hazardResponseDTO, dependentElementNode);
          }
        });
    }
  }

  private addUCAHazardLinkNode(ucas: UcaResponseDTO[], dependentElementNode?: DependentElementNode): void {
    if (entitiesExist<UcaResponseDTO>(ucas)) {
      dependentElementNode.children.push(ucaLinkNode);
      ucas.forEach((uca: UcaResponseDTO): void => {
        let ucaNode: DependentElementNode = {
          entity: uca,
          name: uca.name,
          entityId: uca.id,
          children: [],
          entityTitle: ucaTitle,
          linkElement: true,
        };
        dependentElementNode.children.push(ucaNode);
        this.dataSource.data = this.dependencyTree;
        this.treeControl.expandAll();
      });
    }
  }

  private getLossByHazard(hazardResponseDTO: HazardResponseDTO, dependentElementNode?: DependentElementNode): void {
    this.hazardDataService.getLossesByHazardId(this.projectId, hazardResponseDTO.id, this.page)
      .then((losses: LossResponseDTO[]) => {
        this.addLossByHazardNode(losses, dependentElementNode);
      }).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: null, detail: response.message });
    });
  }

  private addLossByHazardNode(losses: LossResponseDTO[], dependentElementNode?: DependentElementNode): void {
    if (entitiesExist<LossResponseDTO>(losses)) {
      dependentElementNode.children.push(losseTitleNode);

      losses.forEach((lossResponseDTO: LossResponseDTO): void => {
        let lossNode: DependentElementNode = {
          entity: lossResponseDTO,
          name: lossResponseDTO.name,
          children: [],
          linkElement: true
        };
        dependentElementNode.children.push(lossNode);
        this.dataSource.data = this.dependencyTree;
        this.treeControl.expandAll();
      });
    }
  }

  private getSubSystemBySubHazard(subHazard: SubHazardResponseDTO, dependentElementNode?: DependentElementNode): void {
    this.subHazardDataService.getSubConstraintBySubHazardId(this.projectId, subHazard.parentId, subHazard.id)
      .then((subSystemConstraintResponseDTO: SubSystemConstraintResponseDTO) => {
        if (subSystemConstraintResponseDTO) {
          this.addSubSystemConstraintNode([subSystemConstraintResponseDTO], dependentElementNode, subHazard);
        }
      }).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: null, detail: response.message });
    });
  }
}
