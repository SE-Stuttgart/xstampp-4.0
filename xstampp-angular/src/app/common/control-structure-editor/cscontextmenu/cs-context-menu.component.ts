import {SelectionModel} from '@angular/cdk/collections';
import {
  AfterContentChecked,
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnInit,
  Output,
  ViewChild
} from '@angular/core';
import {MatTableDataSource} from '@angular/material';
import {ActivatedRoute, Params} from '@angular/router';
import {MessageService} from 'primeng/api';
import {ControlStructureEntitiesWrapperService} from '../../../services/dataServices/control-structure/control-structure-entities-wrapper.service';
import {
  ActuatorDTO,
  ArrowAndBoxType,
  ControlActionDTO,
  ControlledProcessDTO,
  ControllerDTO,
  ControlStructureDTO,
  ControlStructureEntityDTO,
  CSCoordinateData,
  FeedbackDTO,
  InputDTO,
  OutputDTO,
  SensorDTO
} from '../../../types/local-types';
import {ContextMenuData, CsCmDTO, CSDiaType, CSShape} from '../cs-types';
import {FAILED_LOADING} from 'src/app/globals';
import {CsToolbarMode} from '../cstoolbar/cs-toolbar.component';
import {HttpErrorResponse} from '@angular/common/http';
import {DependentElementTreeComponent} from "../../../sub-app/dependent-element-tree/dependent-element-tree.component";
import {
  actuatorTitle, controlActionTitle, controlledProcessTitle,
  controllerTitle, feedbackTitle, inputTitle, outputTitle,
  SelectedEntity, sensorTitle
} from "../../../sub-app/dependent-element-tree/dependent-elements-types.component";
import {MatDialog} from "@angular/material/dialog";
import {DependentElementDetailsComponent} from "../../../sub-app/dependent-element-tree/dependent-element-details/dependent-element-details.component";

@Component({
  selector: 'app-cs-context-menu',
  templateUrl: './cs-context-menu.component.html',
  styleUrls: ['./cs-context-menu.component.css']
})

// FIXME: Generic type
export class CsContextMenuComponent implements OnInit, AfterContentChecked {
  @Input() data: ContextMenuData;
  @Input() coordData: CSCoordinateData;
  @Input() diaType: CSDiaType;
  @Input() wasEdited: boolean = false;
  @Output() closeMenu: EventEmitter<ContextMenuData> = new EventEmitter();
  @Output() refineEntity: EventEmitter<ContextMenuData> = new EventEmitter();
  @Output() deleteEntity: EventEmitter<ContextMenuData> = new EventEmitter();
  @Output() createEntity: EventEmitter<ContextMenuData> = new EventEmitter();
  @Output() resizeBox: EventEmitter<ContextMenuData> = new EventEmitter();
  text: string;
  displayedColumns: string[];
  selection: SelectionModel<ControlStructureEntityDTO> = new SelectionModel<ControlStructureEntityDTO>(true, []);
  dataSource: MatTableDataSource<CsCmDTO>;
  mode: ContextMenuMode = ContextMenuMode.Menu;
  ContextMenuMode: typeof ContextMenuMode = ContextMenuMode;
  boxType: typeof ArrowAndBoxType = ArrowAndBoxType;
  boxShape: typeof CSShape = CSShape;
  projectId: string;
  entitiesFromRequest: Array<CsCmDTO> = [];
  alreadyLinkedEntities: Array<CsCmDTO> = [];
  isInputOutput: boolean;
  path: string;
  hasEntity: boolean = true;

  get canRefine(): boolean {
    return this.data.type === this.boxType.Controller && !this.wasEdited && this.hasEntity;
  }

  @ViewChild('nameInput', {static: true}) inputRef: ElementRef<HTMLInputElement>;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly csEntitesWrapper: ControlStructureEntitiesWrapperService,
    private readonly messageService: MessageService,
    public createDependentElementDialog: MatDialog) {
    this.displayedColumns = ['select', 'name'];

  }

  ngOnInit(): void {
    this.text = this.data.type;
    if (this.text.includes('Input') || this.text.includes('Output')) {
      this.isInputOutput = true;
    } else {
      this.isInputOutput = false;
    }
    this.route.parent.params.subscribe((value: Params) => {
      this.projectId = value.id;
      this.loadData();
    });
  }

  ngAfterContentChecked(): void {
    if (this.mode === ContextMenuMode.New && this.inputRef !== undefined) {
      this.inputRef.nativeElement.focus();
    }
  }

  get isStep2(): boolean {
    return this.diaType === CSDiaType.STEP2;
  }

  /**
   *   loads the available elements and the already linked elements to highlight
   */
  async loadData(): Promise<void> {
    switch (this.data.type) {
      // loads the data to fill the linkTable
      case 'ControlledProcess': {
        await this.csEntitesWrapper.controlledProcessDataService.getAllControlledProcesses(this.projectId, {
          from: 0,
          amount: 100,
          filter: {type: 'FIELD_EQUALS', fieldName: 'isUnlinked', fieldValue: 'true', body: []}
        }).then((entities: ControlledProcessDTO[]) => {
          this.entitiesFromRequest = entities;
          this.dataSource = new MatTableDataSource<ControlledProcessDTO>(entities);
        }).catch((error: Error) => {
          this.messageService.add({
            severity: 'error',
            summary: FAILED_LOADING + ' Controlled Process',
            detail: error.toString()
          });
        });
        await this.csEntitesWrapper.controlledProcessDataService.getControlledProcessByBoxId(this.projectId, this.data.id)
          .then((entity: ControlledProcessDTO) => {
            if (entity) {
              this.alreadyLinkedEntities.push(entity);
              this.entitiesFromRequest.push(entity);
              this.dataSource = new MatTableDataSource<ControlledProcessDTO>(this.entitiesFromRequest as ControlledProcessDTO[]);
              this.selection.toggle(entity);
            }
          });
        break;
      }
      case 'Controller': {
        await this.csEntitesWrapper.controllerDataService.getAllControllers(this.projectId, {
          from: 0,
          amount: 100,
          filter: {type: 'FIELD_EQUALS', fieldName: 'isUnlinked', fieldValue: 'true', body: []}
        }).then((entities: ControllerDTO[]) => {
          this.entitiesFromRequest = entities;
          this.dataSource = new MatTableDataSource<ControllerDTO>(entities);
        }).catch((error: Error) => {
          this.messageService.add({
            severity: 'error',
            summary: FAILED_LOADING + ' Controller',
            detail: error.toString()
          });
        });
        await this.csEntitesWrapper.controllerDataService.getControllerByBoxId(this.projectId, this.data.id)
          .then((entity: ControllerDTO) => {
            if (entity) {
              this.alreadyLinkedEntities.push(entity);
              this.entitiesFromRequest.push(entity);
              this.dataSource = new MatTableDataSource<ControllerDTO>(this.entitiesFromRequest as ControllerDTO[]);
              this.selection.toggle(entity);
              this.hasEntity = true;
            }
          })
          .catch((error: Error) => {
            console.log('Controller is empty!');
            this.hasEntity = false;
          });
        break;
      }
      case 'Actuator': {
        await this.csEntitesWrapper.actuatorDataService.getAllActuators(this.projectId, {
          from: 0,
          amount: 100,
          filter: {type: 'FIELD_EQUALS', fieldName: 'isUnlinked', fieldValue: 'true', body: []}
        }).then((entities: ActuatorDTO[]) => {
          this.entitiesFromRequest = entities;
          this.dataSource = new MatTableDataSource<ActuatorDTO>(entities);
        }).catch((error: Error) => {
          this.messageService.add({severity: 'error', summary: FAILED_LOADING + ' Actuator', detail: error.toString()});
        });
        await this.csEntitesWrapper.actuatorDataService.getActuatorByBoxId(this.projectId, this.data.id)
          .then((entity: ActuatorDTO) => {
            if (entity) {
              this.alreadyLinkedEntities.push(entity);
              this.entitiesFromRequest.push(entity);
              this.dataSource = new MatTableDataSource<ActuatorDTO>(this.entitiesFromRequest as ActuatorDTO[]);
              this.selection.toggle(entity);
            }
          });
        break;
      }
      case 'Sensor': {
        await this.csEntitesWrapper.sensorDataService.getAllSensors(this.projectId, {
          from: 0,
          amount: 100,
          filter: {type: 'FIELD_EQUALS', fieldName: 'isUnlinked', fieldValue: 'true', body: []}
        }).then((entities: SensorDTO[]) => {
          this.entitiesFromRequest = entities;
          this.dataSource = new MatTableDataSource<SensorDTO>(entities);
        }).catch((error: Error) => {
          this.messageService.add({severity: 'error', summary: FAILED_LOADING + ' Sensor', detail: error.toString()});
        });
        await this.csEntitesWrapper.sensorDataService.getSensorByBoxId(this.projectId, this.data.id)
          .then((entity: SensorDTO) => {
            if (entity) {
              this.alreadyLinkedEntities.push(entity);
              this.entitiesFromRequest.push(entity);
              this.dataSource = new MatTableDataSource<SensorDTO>(this.entitiesFromRequest as SensorDTO[]);
              this.selection.toggle(entity);
            }
          });
        break;
      }
      case 'ControlAction': {
        await this.csEntitesWrapper.controlActionDataService.getAllControlActions(this.projectId, {
          from: 0,
          amount: 100,
          filter: {type: 'FIELD_EQUALS', fieldName: 'isUnlinked', fieldValue: 'true', body: []}
        }).then((entities: ControlActionDTO[]) => {
          this.entitiesFromRequest = entities;
          this.dataSource = new MatTableDataSource<ControlActionDTO>(entities);
        }).catch((error: Error) => {
          this.messageService.add({
            severity: 'error',
            summary: FAILED_LOADING + ' Control Action',
            detail: error.toString()
          });
        });
        await this.csEntitesWrapper.controlActionDataService.getControlActionsByArrowId(this.projectId, this.data.id)
          .then((entities: ControlActionDTO[]) => {
            if (entities) {
              this.alreadyLinkedEntities = entities;
              this.dataSource = new MatTableDataSource<ControlActionDTO>(this.entitiesFromRequest.concat(entities) as ControlActionDTO[]);
              entities.forEach((entity: ControlActionDTO) => {
                this.selection.toggle(entity);
              });
            }
          });
        break;
      }
      case 'Feedback': {
        await this.csEntitesWrapper.feedbackDataService.getAllFeedbacks(this.projectId, {
          from: 0,
          amount: 100,
          filter: {type: 'FIELD_EQUALS', fieldName: 'isUnlinked', fieldValue: 'true', body: []}
        }).then((entities: FeedbackDTO[]) => {
          this.entitiesFromRequest = entities;
          this.dataSource = new MatTableDataSource<FeedbackDTO>(entities);
        }).catch((error: Error) => {
          this.messageService.add({severity: 'error', summary: FAILED_LOADING + ' Feedback', detail: error.toString()});
        });
        await this.csEntitesWrapper.feedbackDataService.getFeedbacksByArrowId(this.projectId, this.data.id)
          .then((entities: FeedbackDTO[]) => {
            if (entities) {
              this.alreadyLinkedEntities = entities;
              this.dataSource = new MatTableDataSource<FeedbackDTO>(this.entitiesFromRequest.concat(entities) as FeedbackDTO[]);
              entities.forEach((entity: FeedbackDTO) => {
                this.selection.toggle(entity);
              });
            }
          });
        break;
      }
      case 'Output': {
        await this.csEntitesWrapper.outputDataService.getAllOutputs(this.projectId, {
          from: 0,
          amount: 100,
          filter: {type: 'FIELD_EQUALS', fieldName: 'isUnlinked', fieldValue: 'true', body: []}
        }).then((entities: OutputDTO[]) => {
          this.entitiesFromRequest = entities;
          this.dataSource = new MatTableDataSource<OutputDTO>(entities);
        }).catch((error: Error) => {
          this.messageService.add({severity: 'error', summary: FAILED_LOADING + ' Output', detail: error.toString()});
        });
        await this.csEntitesWrapper.outputDataService.getOutputsByArrowId(this.projectId, this.data.id)
          .then((entities: OutputDTO[]) => {
            if (entities) {
              this.alreadyLinkedEntities = entities;
              this.dataSource = new MatTableDataSource<OutputDTO>(this.entitiesFromRequest.concat(entities) as OutputDTO[]);
              entities.forEach((entity: OutputDTO) => {
                this.selection.toggle(entity);
              });
            }
          });
        break;
      }
      case 'Input': {
        await this.csEntitesWrapper.inputDataService.getAllInputs(this.projectId, {
          from: 0,
          amount: 100,
          filter: {type: 'FIELD_EQUALS', fieldName: 'isUnlinked', fieldValue: 'true', body: []}
        }).then((entities: InputDTO[]) => {
          this.entitiesFromRequest = entities;
          this.dataSource = new MatTableDataSource<InputDTO>(entities);
        }).catch((error: Error) => {
          this.messageService.add({severity: 'error', summary: FAILED_LOADING + ' Input', detail: error.toString()});
        });
        await this.csEntitesWrapper.inputDataService.getInputsByArrowId(this.projectId, this.data.id)
          .then((entities: InputDTO[]) => {
            if (entities) {
              this.alreadyLinkedEntities = entities;
              this.dataSource = new MatTableDataSource<InputDTO>(this.entitiesFromRequest.concat(entities) as InputDTO[]);
              entities.forEach((entity: InputDTO) => {
                this.selection.toggle(entity);
              });
            }
          });
        break;
      }
    }
  }

  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected(): boolean {
    const numSelected: number = this.selection.selected.length;
    const numRows: number = this.dataSource.data.length;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  masterToggle(): void {
    this.isAllSelected() ?
      this.selection.clear() :
      this.dataSource.data.forEach((row: CsCmDTO) => this.selection.select(row));
  }

  /** Selects a single row; boxes can only have one selected element. */
  select(event: JQuery.Event, row: CsCmDTO): void {
    if (this.data.shape === 'box') {
      const isSelected: boolean = this.selection.isSelected(row);
      this.selection.clear();
      if (isSelected) {
        this.selection.toggle(row);
      }
    }
    event.stopPropagation();
  }

  clearArrays(): void {
    this.entitiesFromRequest = [];
    this.alreadyLinkedEntities = [];
  }

  close(): void {
    this.closeMenu.emit();
    this.mode = ContextMenuMode.Menu;
    this.clearArrays();
  }

  onSave(): void {
    const labels: string[] = [];
    const alterIds: string[] = [];
    const deleteIds: string[] = [];
    let linksToAlter: ControlStructureEntityDTO[];
    if (this.alreadyLinkedEntities) {
      const linksToDelete: CsCmDTO[] = this.alreadyLinkedEntities.filter((el: CsCmDTO) => !this.selection.selected.includes(el));
      linksToAlter = this.entitiesFromRequest.filter((el: CsCmDTO) => this.selection.selected.includes(el));
      linksToDelete.forEach((entity: ControlStructureEntityDTO) => {
        deleteIds.push(entity.id);
      });
    } else {
      linksToAlter = this.selection.selected;
    }
    linksToAlter.forEach((entity: ControlStructureEntityDTO) => {
      alterIds.push(entity.id);
    });
    this.selection.selected.forEach((entity: ControlStructureEntityDTO) => {
      labels.push(entity.name);
    });
    this.closeMenu.emit({
      labels: labels,
      alterEntityIds: alterIds,
      deleteEntityIds: deleteIds,
      id: this.data.id,
      shape: this.data.shape,
      type: this.data.type
    } as ContextMenuData);
    this.mode = ContextMenuMode.Menu;
    this.clearArrays();
  }

  link(): void {
    this.mode = ContextMenuMode.Link;
  }

  delete(): void {
    this.mode = ContextMenuMode.Menu;
    const deleteEntityIds: string[] = [];
    let controllerTreeSelected: boolean = false;
    let stepOneSelected: boolean = false;
    let stepTwoSelected: boolean = false;
    let title: string = '';
    let entityDTO: any;
    if (this.alreadyLinkedEntities.length > 0) {
      if (this.alreadyLinkedEntities) {
        this.alreadyLinkedEntities.forEach((entity: ControlStructureEntityDTO) => {
          deleteEntityIds.push(entity.id);
          switch (this.data.type) {
            case ArrowAndBoxType.Controller: {
              entityDTO = {
                id: entity.id,
                name: entity.name,
                projectId: this.projectId,
                description: entity.description,
                state: entity.state,
                boxId: this.data.id
              } as ControllerDTO;

              title = controllerTitle;
              controllerTreeSelected = true;
              break;
            }
            case ArrowAndBoxType.Actuator: {
              entityDTO = {
                id: entity.id,
                name: entity.name,
                projectId: this.projectId,
                description: entity.description,
                state: entity.state,
                boxId: this.data.id
              } as ActuatorDTO;

              title = actuatorTitle;
              controllerTreeSelected = true;
              break;
            }
            case ArrowAndBoxType.Sensor: {
              entityDTO = {
                id: entity.id,
                name: entity.name,
                projectId: this.projectId,
                description: entity.description,
                state: entity.state,
                boxId: this.data.id
              } as SensorDTO;

              title = sensorTitle;
              stepTwoSelected = true;
              break;
            }
            case ArrowAndBoxType.ControlAction: {
              entityDTO = {
                id: entity.id,
                name: entity.name,
                projectId: this.projectId,
                description: entity.description,
                state: entity.state,
                arrowId: this.data.id
              } as ControlActionDTO;

              title = controlActionTitle;
              controllerTreeSelected = true;
              break;
            }
            case ArrowAndBoxType.ControlledProcess: {
              entityDTO = {
                id: entity.id,
                name: entity.name,
                projectId: this.projectId,
                description: entity.description,
                state: entity.state,
                boxId: this.data.id
              } as ControlledProcessDTO;

              title = controlledProcessTitle;
              stepTwoSelected = true;
              break;
            }
            case ArrowAndBoxType.Input: {
              entityDTO = {
                id: entity.id,
                name: entity.name,
                projectId: this.projectId,
                description: entity.description,
                state: entity.state,
                arrowId: this.data.id
              } as InputDTO;

              title = inputTitle;
              controllerTreeSelected = true;
              break;
            }
            case ArrowAndBoxType.Output: {
              entityDTO = {
                id: entity.id,
                name: entity.name,
                projectId: this.projectId,
                description: entity.description,
                state: entity.state,
                arrowId: this.data.id
              } as OutputDTO;

              title = outputTitle;
              stepTwoSelected = true;
              break;
            }
            case ArrowAndBoxType.Feedback: {
              entityDTO = {
                id: entity.id,
                name: entity.name,
                projectId: this.projectId,
                description: entity.description,
                state: entity.state,
                arrowId: this.data.id
              } as OutputDTO;

              title = feedbackTitle;
              stepTwoSelected = true;
              break;
            }
          }
        });
        let selectedEntity: SelectedEntity<Object> = {
          entityTitle: title,
          projectId: this.projectId,
          entity: [entityDTO],
          stepOneSelected: stepOneSelected,
          stepTwoSelected: stepTwoSelected,
          controllerTreeSelected: controllerTreeSelected,
          moveTree: false,
          controlStructureEntity: true
        }

        const dialogRef = this.createDependentElementDialog.open(DependentElementTreeComponent, {
          width: '60%',
          height: 'auto',
          data: {
            selectedEntityData: selectedEntity
          },
          disableClose: true
        });

        dialogRef.afterClosed().subscribe((value: boolean): void => {
          if (!value) {
            this.deleteEntity.emit({
              id: this.data.id,
              deleteEntityIds: deleteEntityIds,
              shape: this.data.shape,
              type: this.data.type
            } as ContextMenuData);
            this.clearArrays();
          } else {
            const detailsRef = this.createDependentElementDialog.open(DependentElementDetailsComponent, {
              width: 'auto',
              height: 'auto',
              data: {
                showDeleteModelMessage: true
              },
              disableClose: true
            });

            detailsRef.afterClosed().subscribe((deleteShape: boolean) => {
              if (deleteShape) {
                this.deleteEntity.emit({
                  id: this.data.id,
                  deleteEntityIds: deleteEntityIds,
                  shape: this.data.shape,
                  type: this.data.type
                } as ContextMenuData);
                this.clearArrays();
              }
            });
          }
        });
      }
    } else {
      this.deleteEntity.emit({
        id: this.data.id,
        deleteEntityIds: deleteEntityIds,
        shape: this.data.shape,
        type: this.data.type
      } as ContextMenuData);
      this.clearArrays();
    }
  }

  refine(): void {
    this.refineEntity.emit({id: this.data.id, shape: this.data.shape} as ContextMenuData);
    this.closeMenu.emit();
    this.mode = ContextMenuMode.Menu;
    this.clearArrays();
  }

  resize(): void {
    this.resizeBox.emit({id: this.data.id, type: this.data.type} as ContextMenuData);
  }

  startCreation(): void {
    this.mode = ContextMenuMode.New;
  }

  create(name: string): void {
    let saveEntity: boolean = true;
    if (this.data.type.includes('Box')) {
      saveEntity = false;
    }
    this.createEntity.emit({
      name: name,
      type: ArrowAndBoxType[this.data.type],
      id: this.data.id,
      shape: this.data.shape,
      saveEntity: saveEntity
    } as ContextMenuData);
    this.mode = ContextMenuMode.Menu;
    this.clearArrays();
  }
}

export enum ContextMenuMode {
  Link,
  Delete,
  Menu,
  New
}
