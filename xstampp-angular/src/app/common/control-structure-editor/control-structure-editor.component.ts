import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, HostListener, Input, OnInit, Output } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import * as joint from 'jointjs';
import * as $ from 'jquery';
import { Ng4LoadingSpinnerService } from 'ng4-loading-spinner';
import { MessageService } from 'primeng/api';
import { ChangeDetectionService } from 'src/app/services/change-detection/change-detection-service.service';
import { LockResponse, LockService } from 'src/app/services/dataServices/lock.service';
import * as SvgPanZoom from 'svg-pan-zoom';
import { AuthService } from '../../services/auth.service';
import { ControlStructureDataService } from '../../services/dataServices/control-structure-data.service';
import { ControlStructureEntitiesWrapperService } from '../../services/dataServices/control-structure/control-structure-entities-wrapper.service';
import { WebsocketService } from '../../services/websocket.service';
import { Arrow, ArrowAndBoxType, ArrowEntityDTO, Box, BoxEntityDTO, ControllerDTO, ControlStructureDTO, CONTROL_STRUCTURE, CSCoordinateData, Direction, PendingRequestDTO, PrepareRequestDTO } from '../../types/local-types';
import { DetailedSheetUtils } from '../detailed-sheet/utils/detailed-sheet-utils';
import { ContextMenuData, CSDiaType, CSShape } from './cs-types';
import { CsToolbarMode } from './cstoolbar/cs-toolbar.component';
import { CsUtils } from './csutils/cs-utils';
import { ShapeUtils } from './csutils/shape-utils';
import { ERROR_GET_PROJECT_ID, DRAG_ARROW_CORRECT, PLACEMENT_FAILED, UNABLE_TO_EDIT, PENDING_REQUEST_FAILED, ERROR_GET_CONTROL_STRUCTURE, SAVE_SUCCESSFUL } from 'src/app/globals';
import { CanDeactivateGuard, DeactivationMode } from 'src/app/services/change-detection/can-deactivate-guard.service';

@Component({
  selector: 'app-control-structure-editor',
  templateUrl: './control-structure-editor.component.html',
  styleUrls: ['./control-structure-editor.component.scss']
})
export class ControlStructureEditorComponent implements OnInit {
  @Input() diaType: CSDiaType; // sets the type of the dia, STEP2 or STEP4,
  @Output() changeDiaType: EventEmitter<string> = new EventEmitter<string>();

  refine: boolean = false;
  isSafari: boolean;
  isFirefox: boolean;
  cscontextmenu: boolean = false;
  cscontextmenuData: ContextMenuData;
  csCmTarget: joint.dia.CellView;
  defaultHeight: number = 100;
  defaultWidth: number = 200;
  response: ControlStructureDTO;
  graph: joint.dia.Graph;
  paper: joint.dia.Paper;
  arrowId: string | number;
  csCoorinateData: CSCoordinateData = { paperSize: null, position: null };
  arrowType: string;
  highlightMode: boolean = false;
  projectId: string;
  customHighlighter: any;
  panAndZoom: SvgPanZoom.Instance;
  pendingCSRequests: Promise<boolean>[] = [];
  pendingImportantCSRequests: Promise<boolean>[] = [];
  pendingCreateRequests: PendingRequestDTO[] = [];
  isEditable: boolean = false;
  promises: Promise<ControllerDTO>;
  utils: CsUtils;
  shapeUtils: ShapeUtils;
  private mouseDown: boolean = false;
  private last: MouseEvent;
  private resizeElement: any;
  private direction: Direction;
  CsToolbarMode: typeof CsToolbarMode = CsToolbarMode;
  mode: CsToolbarMode = CsToolbarMode.READONLY;

  get wasEdited(): boolean {
    return this.pendingImportantCSRequests.length > 0 ||
      this.pendingCreateRequests.length > 0 ||
      this.pendingCSRequests.length > 0;
  }

  constructor(
    private readonly route: ActivatedRoute,
    private readonly lockService: LockService,
    private readonly controlStructureDataService: ControlStructureDataService,
    private readonly messageService: MessageService,
    private readonly cds: ChangeDetectionService,
    private readonly spinnerService: Ng4LoadingSpinnerService,
    private readonly wsService: WebsocketService,
    private readonly authService: AuthService,
    private readonly canDeactivate: CanDeactivateGuard,
    csEntitiesWrapper: ControlStructureEntitiesWrapperService,
  ) {

    this.route.parent.params.subscribe((value: any) => {
      this.projectId = value.id;
      this.utils = new CsUtils(route, csEntitiesWrapper);
      this.shapeUtils = new ShapeUtils();
      this.isSafari = this.utils.isSafari;
      this.isFirefox = this.utils.isFirefox;
    });
    // Websocket implementation
    this.authService
      .getProjectToken()
      .then((token: string) => {
        this.wsService.connect('subscribe', CONTROL_STRUCTURE, token, (data: any) => {
          // if the user is the one who updated don't reload. Same if the cs is in edit mode
          if (
            !this.isEditable &&
            data.displayName !== this.authService.getUserName()
          ) {
            this.reloadControlStructure();
            this.messageService.add({
              severity: 'info',
              summary: 'User ' + data.displayName + ' updated the data!'
            });
          }
        });
      })
      .catch((err: Error) => {
        console.log(err);
      });
  }

  ngOnInit(): void {
    this.spinnerService.show();
    this.controlStructureDataService
      .loadRootControlStructure(this.projectId)
      .then((value: ControlStructureDTO) => {
        this.response = value;
        this.spinnerService.hide();
        this.initControlStructure();
      })
      .catch((error: string) => {
        this.messageService.add({
          severity: 'error',
          summary: ERROR_GET_PROJECT_ID,
          detail: error
        });
        this.spinnerService.hide();
      });

  }

  /**
   *  initializes the controlstructure and everything needed for the interaction, like event-listener
   */
  initControlStructure(): void {
    this.graph = this.shapeUtils.getGraph();
    this.paper = new joint.dia.Paper({
      el: $('#paper'),
      height: $('#drawer').height(),
      width: $('#drawer').width(),
      model: this.graph,
      perpendicularLinks: true,
      interactive: false,
      background: {
        color: 'white'
      }
    });

    this.csCoorinateData.paperSize = {
      x: this.paper.options.width,
      y: this.paper.options.height
    };
    this.csCoorinateData.position = { x: 0, y: 0 };
    this.customHighlighter = {
      highlighter: {
        name: 'stroke',
        options: {
          padding: 6,
          attrs: {
            'stroke-width': 6,
            stroke: '#000000',
            opacity: 0.5
          }
        }
      }
    };

    const options: SvgPanZoom.Options = {
      viewportSelector: '.joint-viewport',
      center: false,
      zoomEnabled: true,
      panEnabled: true,
      dblClickZoomEnabled: false,
      fit: false,
      minZoom: 0.5,
      maxZoom: 2,
      zoomScaleSensitivity: 0.5,
    };

    const svgId: string = document.getElementById('paper').children[2].id;
    this.panAndZoom = SvgPanZoom('#' + svgId, options);
    this.paper.on(
      'cell:contextmenu',
      (cellView: joint.dia.CellView, evt: JQuery.Event, x: number, y: number): void => {
        if ((this.paper.options.interactive || this.cscontextmenu) && cellView) {
          let type: string;
          let shape: CSShape;
          if (cellView.model.attributes.type === 'org.Arrow') {
            type = cellView.model.attributes.attrs.xstampp.type;
            shape = CSShape.ARROW;
          } else if (
            cellView.model.attributes.type.includes('Shape') ||
            cellView.model.attributes.type.includes('Box')
          ) {
            type = cellView.model.attributes.attrs.xstampp.type;
            shape = CSShape.BOX;
          }
          this.lockInteraction();
          const pan: SvgPanZoom.Point = this.panAndZoom.getPan();
          const zoom: number = this.panAndZoom.getZoom();
          if (pan.x !== 0 || pan.y !== 0 || zoom !== 1) {
            this.csCoorinateData.position.x = x * zoom + pan.x;
            this.csCoorinateData.position.y = y * zoom + pan.y;
          } else {
            this.csCoorinateData.position.x = x;
            this.csCoorinateData.position.y = y;
          }
          this.csCoorinateData.paperSize.x = this.paper.options.width;
          this.csCoorinateData.paperSize.y = this.paper.options.height;
          this.cscontextmenu = true;
          this.cscontextmenuData = {
            type: ArrowAndBoxType[type],
            shape: shape,
            projectId: this.projectId,
            id: cellView.model.id as string
          };
          // saves the ref of the clicked cell, for zooming in STEP4
          this.csCmTarget = cellView;
        }
      }
    );
    // this loads the actual data from teh backend
    this.parseJSONtoJoint();

    /**
     * close contextmenu on blank click
     */
    this.paper.on('blank:pointerclick', () => {
      if (this.cscontextmenu) {
        this.cscontextmenu = false;
        this.unlockInteraction();
      }
    });

    /**
     * close contextmenu on blank rightclick
     */
    this.paper.on('blank:contextmenu', () => {
      if (this.cscontextmenu) {
        this.cscontextmenu = false;
        this.unlockInteraction();
      }
    });

    /**
     * on link (arrow) click
     */
    this.paper.on(
      'link:pointerdown',
      (cellView: joint.dia.CellView, evt: JQuery.Event, x: number, y: number) => {
        if (this.isEditable) {
          this.lockInteraction(false);
        }
      }
    );

    this.paper.on(
      'link:pointerup',
      (cellView: joint.dia.CellView, evt: JQuery.Event, x: number, y: number) => {
        if (this.isEditable) {
          this.unlockInteraction();
        }
      }
    );

    /**
     * on any element click
     */
    this.paper.on(
      'element:pointerdown',
      (cellView: joint.dia.CellView, evt: JQuery.Event, x: number, y: number) => {
        if (this.isEditable) {
          this.lockInteraction(false);
        }
        if (this.highlightMode) {
          this.highlightCell(cellView.model);
        } else {
          if (this.paper.options.interactive) {
            this.highlightCell(cellView.model);
          }
        }
      }
    );

    // sets the arrow
    this.paper.on(
      'element:pointerup',
      (cellView: joint.dia.CellView, evt: JQuery.Event, x: number, y: number) => {
        if (this.isEditable) {
          this.unlockInteraction();
        }
        if (this.highlightMode && this.isEditable) {
          if (
            CsUtils.isValidPlacement(
              ArrowAndBoxType[this.arrowType],
              CsUtils.jointTypeToArrowAndBoxType(
                cellView.model.attributes.type
              ),
              false
            )
          ) {
            let target: string;
            let source: string;
            source = this.arrowId as string;
            target = cellView.model.id as string;

            if (target !== source) {
              this.unHighlightCell(this.graph.getCell(this.arrowId));
              this.link(
                source,
                target,
                new Array<joint.dia.Link.Vertex>(),
                '',
                this.arrowType,
                cellView.model.attributes.attrs.xstampp.projectId,
                undefined,
              );
              this.unHighlightCell(cellView.model);
              this.highlightMode = false;
            } else {
              this.unHighlightCell(this.graph.getCell(this.arrowId));
              this.highlightMode = false;
              this.messageService.add({
                severity: 'error',
                summary: 'Placement failed',
                detail: 'Source and Target are the same.',
                life: 4000
              });
            }

          } else {
            this.unHighlightCell(this.graph.getCell(this.arrowId));
            this.unHighlightCell(cellView.model);
            this.highlightMode = false;
            this.messageService.add({
              severity: 'error',
              summary: PLACEMENT_FAILED,
              detail: DRAG_ARROW_CORRECT,
              life: 4000
            });
          }
        } else {
          this.unHighlightCell(cellView.model);
        }
      }
    );
  }

  @HostListener('mouseup')
  onMouseup(): void {
    this.mouseDown = false;
  }

  @HostListener('mousemove', ['$event'])
  onMousemove(event: MouseEvent): void {
    /**
     * resizes the box with the dragevent of the graphandels
     */
    if (this.mouseDown) {
      event.stopPropagation(); // stops propagation, cause the "object" would be moved
      event.preventDefault(); // stops default, cause the "graphandelbox" would move instead of the resizing effect

      // min width and heigth for the Box-objects
      const MINWIDTH: number = 100;
      const MINHEIGTH: number = 50;
      switch (this.direction) {
        case Direction.SE: {
          const opt: { direction: string } = { direction: 'bottom-right' };
          const x: number = this.resizeElement.attributes.size.width + event.clientX - this.last.clientX;
          const y: number = this.resizeElement.attributes.size.height + event.clientY - this.last.clientY;
          this.resizeElement.resize(x > MINWIDTH ? x : MINWIDTH, y > MINHEIGTH ? y : MINHEIGTH, opt);
          break;
        }

        case Direction.NE: {
          const opt: { direction: string } = { direction: 'top-right' };
          const x: number = this.resizeElement.attributes.size.width + event.clientX - this.last.clientX;
          const y: number = this.resizeElement.attributes.size.height - (event.clientY - this.last.clientY);
          this.resizeElement.resize(x > MINWIDTH ? x : MINWIDTH, y > MINHEIGTH ? y : MINHEIGTH, opt);
          break;
        }

        case Direction.NW: {
          const opt: { direction: string } = { direction: 'top-left' };
          const x: number = this.resizeElement.attributes.size.width - (event.clientX - this.last.clientX);
          const y: number = this.resizeElement.attributes.size.height - (event.clientY - this.last.clientY);
          this.resizeElement.resize(x > MINWIDTH ? x : MINWIDTH, y > MINHEIGTH ? y : MINHEIGTH, opt);
          break;
        }

        case Direction.SW: {
          const opt: { direction: string } = { direction: 'bottom-left' };
          const x: number = this.resizeElement.attributes.size.width - (event.clientX - this.last.clientX);
          const y: number = this.resizeElement.attributes.size.height + event.clientY - this.last.clientY;
          this.resizeElement.resize(x > MINWIDTH ? x : MINWIDTH, y > MINHEIGTH ? y : MINHEIGTH, opt);
          break;
        }

        case Direction.N: {
          const opt: { direction: string } = { direction: 'top' };
          const y: number = this.resizeElement.attributes.size.height - (event.clientY - this.last.clientY);
          y > MINHEIGTH && this.resizeElement.resize(this.resizeElement.attributes.size.width, y, opt);
          break;
        }

        case Direction.E: {
          const opt: { direction: string } = { direction: 'right' };
          const x: number = this.resizeElement.attributes.size.width + (event.clientX - this.last.clientX);
          x > MINWIDTH && this.resizeElement.resize(x, this.resizeElement.attributes.size.height, opt);
          break;
        }

        case Direction.S: {
          const opt: { direction: string } = { direction: 'bottom' };
          const y: number = this.resizeElement.attributes.size.height + (event.clientY - this.last.clientY);
          y > MINHEIGTH && this.resizeElement.resize(
            this.resizeElement.attributes.size.width, y, opt);
          break;
        }

        case Direction.W: {
          const opt: { direction: string } = { direction: 'left' };
          const x: number = this.resizeElement.attributes.size.width - (event.clientX - this.last.clientX);
          x > MINWIDTH && this.resizeElement.resize(x, this.resizeElement.attributes.size.height, opt);
          break;
        }
      }

      this.last = event;
    }
  }

  zoom(zoomIn: boolean): void {
    this.cscontextmenu = false;
    this.unlockInteraction();
    if (zoomIn) {
      this.panAndZoom.zoomIn();
    } else {
      this.panAndZoom.zoomOut();
    }
  }

  allowDrop(ev: DragEvent): void {
    ev.preventDefault();
  }

  /**
   * Called when the controlstructure becomes editable
   */
  startEdit(): void {
    this.cds.updateState({
      key: 'CSObject',
      value: true
    });
    this.lockService.lockEntity(this.projectId, {
      entityName: CONTROL_STRUCTURE,
      expirationTime: DetailedSheetUtils.calculateLockExpiration(),
      id: this.projectId
    }).then((success: LockResponse) => {
      this.mode = CsToolbarMode.OPEN;
      this.reloadControlStructure();
      this.isEditable = true;
      this.unlockInteraction();
    }).catch((er: Error) => {
      console.log(er);
      this.messageService.add({ severity: 'error', summary: UNABLE_TO_EDIT });
    }
    );
  }

  /**
   * Called when something is dropepd from the toolbar. handels the creation of boxes and linking
   * @param ev: the event
   */
  drop(ev: any): void {
    ev.preventDefault();
    this.cscontextmenu = false;
    this.unlockInteraction();
    let shape: joint.dia.Element;
    const type: string = ev.dataTransfer.getData('type');
    if (ev.dataTransfer.getData('shape') === 'Box') {
      shape = this.shapeUtils.getShapeByName(type);
      const pan: SvgPanZoom.Point = this.panAndZoom.getPan();
      const zoom: number = this.panAndZoom.getZoom();
      if (pan.x !== 0 || pan.y !== 0 || zoom !== 1) {
        shape.position((ev.layerX - pan.x) / zoom, (ev.layerY - pan.y) / zoom);
      } else {
        shape.position(ev.layerX, ev.layerY);
      }
      shape.resize(this.defaultWidth, this.defaultHeight);
      shape.attr({
        xstampp: {
          type: type
        }
      });
      this.graph.addCell(shape);
    } else {
      const pan: SvgPanZoom.Point = this.panAndZoom.getPan();
      const zoom: number = this.panAndZoom.getZoom();
      const point = new joint.g.Point();
      if (this.isFirefox) {
        // why is firefox the only browser that calculates the layerposition relativ (with zoom and pan?!?!)
        point.x = ev.layerX;
        point.y = ev.layerY;
      } else {
        point.x = (ev.layerX - pan.x) / zoom;
        point.y = (ev.layerY - pan.y) / zoom;
      }
      const elemsUnderPointer: joint.dia.Element[] = this.graph.findModelsFromPoint(point);
      if (elemsUnderPointer.length > 0) {
        if (elemsUnderPointer[0].attributes.type.includes('xstampp')) {
          shape = elemsUnderPointer[0];
        } else {
          shape = elemsUnderPointer[1];
        }
        if (
          CsUtils.isValidPlacement(
            ArrowAndBoxType[type],
            CsUtils.jointTypeToArrowAndBoxType(shape.attributes.type),
            true
          )
        ) {
          this.highlightCell(shape);
          this.highlightMode = true;
          this.arrowType = ev.dataTransfer.getData('type');
          this.arrowId = shape.id;
        } else {
          this.messageService.add({
            severity: 'error',
            summary: PLACEMENT_FAILED,
            detail: DRAG_ARROW_CORRECT,
            life: 4000
          });
        }
      } else {
        this.messageService.add({
          severity: 'error',
          summary: PLACEMENT_FAILED,
          detail: DRAG_ARROW_CORRECT,
          life: 4000
        });
      }
    }
  }

  /**
   * Manual way to resize a box in Joint without using Rappid
   * @param data: dto from the contextmenu
   */
  resizeBox(data: ContextMenuData): void {
    console.log('fired');
    this.cscontextmenu = false;
    this.unlockInteraction();
    this.resizeElement = this.graph.getCell(data.id);
    const element: joint.dia.Element = new this.shapeUtils.GrabHandles();
    element.position(
      this.resizeElement.attributes.position.x,
      this.resizeElement.attributes.position.y
    );
    element.resize(
      this.resizeElement.attributes.size.width,
      this.resizeElement.attributes.size.height
    );
    element.attr({
      label: {
        pointerEvents: 'none',
        visibility: 'visible',
        text: ''
      },
      body: {
        cursor: 'default',
        visibility: 'hidden'
      },
      buttonNE: {
        event: 'element:resize-ne:pointerdown',
        fill: 'black',
        stroke: 'black',
        strokeWidth: 2
      },
      buttonLabelNE: {
        text: ' ',
        fill: 'black',
        fontSize: 8,
        fontWeight: 'bold'
      },
      buttonN: {
        event: 'element:resize-n:pointerdown',
        fill: 'black',
        stroke: 'black',
        strokeWidth: 2
      },
      buttonLabelN: {
        text: ' ',
        fill: 'black',
        fontSize: 8,
        fontWeight: 'bold'
      },
      buttonS: {
        event: 'element:resize-s:pointerdown',
        fill: 'black',
        stroke: 'black',
        strokeWidth: 2
      },
      buttonLabelS: {
        text: ' ',
        fill: 'black',
        fontSize: 8,
        fontWeight: 'bold'
      },
      buttonW: {
        event: 'element:resize-w:pointerdown',
        fill: 'black',
        stroke: 'black',
        strokeWidth: 2
      },
      buttonLabelW: {
        text: ' ',
        fill: 'black',
        fontSize: 8,
        fontWeight: 'bold'
      },
      buttonE: {
        event: 'element:resize-e:pointerdown',
        fill: 'black',
        stroke: 'black',
        strokeWidth: 2
      },
      buttonLabelE: {
        text: ' ',
        fill: 'black',
        fontSize: 8,
        fontWeight: 'bold'
      },
      buttonSE: {
        event: 'element:resize-se:pointerdown',
        fill: 'black',
        stroke: 'black',
        strokeWidth: 2
      },
      buttonLabelSE: {
        text: ' ',
        fill: 'black',
        fontSize: 8,
        fontWeight: 'bold'
      },
      buttonSW: {
        event: 'element:resize-sw:pointerdown',
        fill: 'black',
        stroke: 'black',
        strokeWidth: 2
      },
      buttonLabelSW: {
        text: ' ',
        fill: 'black',
        fontSize: 8,
        fontWeight: 'bold'
      },
      buttonNW: {
        event: 'element:resize-nw:pointerdown',
        fill: 'black',
        stroke: 'black',
        strokeWidth: 2
      },
      buttonLabelNW: {
        text: ' ',
        fill: 'black',
        fontSize: 8,
        fontWeight: 'bold'
      },
      buttonCancel: {
        event: 'element:resize-ok:pointerdown',
        fill: 'green',
        stroke: 'black',
        strokeWidth: 2
      },
      buttonLabelCancel: {
        text: 'OK',
        fill: 'black',
        fontSize: 8,
        fontWeight: 'bold'
      }
    });
    element.addTo(this.graph);
    this.resizeElement.on('change:size', (elemente: joint.dia.Element) => {
      element.position(
        this.resizeElement.attributes.position.x,
        this.resizeElement.attributes.position.y
      );
      element.resize(
        this.resizeElement.attributes.size.width,
        this.resizeElement.attributes.size.height
      );
    });
    this.resizeElement.embed(element);
    this.paper.on('element:resize-ne:pointerdown', (elementView: joint.dia.ElementView, evt: MouseEvent) => {
      this.mouseDown = true;
      this.last = evt;
      this.direction = Direction.NE;
    });
    this.paper.on('element:resize-se:pointerdown', (elementView: joint.dia.ElementView, evt: MouseEvent) => {
      this.mouseDown = true;
      this.last = evt;
      this.direction = Direction.SE;
    });
    this.paper.on('element:resize-sw:pointerdown', (elementView: joint.dia.ElementView, evt: MouseEvent) => {
      this.mouseDown = true;
      this.last = evt;
      this.direction = Direction.SW;
    });
    this.paper.on('element:resize-nw:pointerdown', (elementView: joint.dia.ElementView, evt: MouseEvent) => {
      this.mouseDown = true;
      this.last = evt;
      this.direction = Direction.NW;
    });
    this.paper.on('element:resize-n:pointerdown', (elementView: joint.dia.ElementView, evt: MouseEvent) => {
      this.mouseDown = true;
      this.last = evt;
      this.direction = Direction.N;
    });
    this.paper.on('element:resize-e:pointerdown', (elementView: joint.dia.ElementView, evt: MouseEvent) => {
      this.mouseDown = true;
      this.last = evt;
      this.direction = Direction.E;
    });
    this.paper.on('element:resize-s:pointerdown', (elementView: joint.dia.ElementView, evt: MouseEvent) => {
      this.mouseDown = true;
      this.last = evt;
      this.direction = Direction.S;
    });
    this.paper.on('element:resize-w:pointerdown', (elementView: joint.dia.ElementView, evt: MouseEvent) => {
      this.mouseDown = true;
      this.last = evt;
      this.direction = Direction.W;
    });
    this.paper.on('element:resize-ok:pointerdown', (elementView: joint.dia.ElementView, evt: MouseEvent) => {
      element.remove();
    });
  }

  highlightCell(cell: joint.dia.Cell): void {
    if (this.isEditable) {
      if (this.isSafari) {
        const cellView: joint.dia.ElementView = this.paper.findViewByModel(cell);
        cellView.highlight(null, this.customHighlighter);
      } else {
        cell.attr({
          body: {
            filter: {
              name: 'highlight',
              args: {
                color: 'black',
                width: 4,
                opacity: 0.5,
                blur: 5
              }
            }
          }
        });
      }
    }
  }

  unHighlightCell(cell: joint.dia.Cell): void {
    if (this.isSafari) {
      const cellView: joint.dia.ElementView = this.paper.findViewByModel(cell);
      cellView.unhighlight(null, this.customHighlighter);
    } else {
      cell.removeAttr('body/filter');
    }
  }

  link(source: string, target: string, parts: joint.dia.Link.Vertex[], label: string, type: string, projectId: string, parent: string, id?: string): joint.dia.Cell {
    const labels: string[] = label.split('@!?@'); // the seperator for the abels
    const dist: number = 100 / labels.length;
    const offset: number = dist / 2;
    let color: string;
    if (type === 'Feedback') {
      color = '#00ff00';
    } else if (type === 'ControlAction') {
      color = '#ffc700';
    } else {
      color = '#000000';
    }
    let cell: joint.shapes.org.Arrow;
    if (id) {
      cell = new joint.shapes.org.Arrow({
        id: id
      });
    } else {
      cell = new joint.shapes.org.Arrow();
    }
    cell.source({ id: source });
    cell.target({ id: target });
    cell.attr({
      '.connection': {
        fill: 'none',
        'stroke-linejoin': 'round',
        'stroke-width': '2',
        stroke: '#000000'
      },
      '.marker-target': {
        type: 'path',
        stroke: 'black',
        fill: color,
        d: 'M 10 -5 0 0 10 5 Z'
      },
      '.link-tools': {
        display: 'none'
      },
      'tool-remove': {
        display: 'none'
      },
    });

    labels.filter((ele: string) => ele.length > 0).forEach((ele: string, i: number) => {
      /**
       * FIXME: fucking stupid. Saves the postion of each arrow label in a string like:
       *
       * 'label'@?!@'distance'o'offset'
       * Would be better to use a link table for boxes and arrows with the id of the parentobject. But to do this
       * you would need to refactor the whole controlstructure in back- and frontend.
       */

      cell.label(i, {
        attrs: {
          text: {
            text: ele.split('@?!@')[0] ? this.utils.breakText(ele.split('@?!@')[0], 15) : '',
          }
        },
        position: {
          distance: ele.split('@?!@')[1] && ele.split('@?!@')[1].split('o')[0] ? +ele.split('@?!@')[1].split('o')[0] : (i * dist + offset) / 110,
          offset: ele.split('@?!@')[1] && ele.split('@?!@')[1].split('o')[1] && +ele.split('@?!@')[1].split('o')[1],
        }
      });
    });

    cell.attr({
      xstampp: {
        type: type,
        projectId: projectId,
        parent: parent,
      }
    });

    cell.vertices(parts);
    this.graph.addCell(cell);
    return cell;
  }

  /**
   * Creates new entities (called from contextmenu)
   * @param data: dto includes the new entity and other data to do the necessary requests
   */
  async createEntity(data: ContextMenuData): Promise<void> {
    this.unlockInteraction();
    this.cscontextmenu = false;
    let cell: joint.dia.Cell = this.graph.getCell(data.id);

    if (data.shape === 'box') {
      if (data.saveEntity) {
        try {
          const entityToDelete: BoxEntityDTO = await this.utils.getEntitiesToBox(
            data.type,
            data.id
          );
          // checks if there is was smth else linked and deletes it
          if (entityToDelete) {
            this.pendingImportantCSRequests.push(
              this.utils.removeBoxOrArrowId(entityToDelete.id, data.type)
            );
          }
        } catch (e) {
          console.log(e);
        }

        const entity: BoxEntityDTO = {
          name: data.name,
          boxId: data.id,
          projectId: this.projectId
        };
        const entityToCreate: PrepareRequestDTO = {
          entityType: data.type,
          boxEntity: entity,
          shapeId: data.id
        };
        this.pendingCreateRequests.push(
          this.utils.createEntity(entityToCreate)
        );
      }
      cell.attr({
        label: {
          text: data.name
        }
      });
    } else {
      if (data.saveEntity) {
        const entity: ArrowEntityDTO = {
          name: data.name,
          arrowId: data.id,
          projectId: this.projectId
        };
        const entityToCreate: PrepareRequestDTO = {
          entityType: data.type,
          arrowEntity: entity,
          shapeId: data.id
        };
        this.pendingCreateRequests.push(
          this.utils.createEntity(entityToCreate)
        );
      }

      // sets the labels for the arrows.
      if ((cell as joint.dia.Link).attributes.labels) {
        (cell as joint.dia.Link).label((cell as joint.dia.Link).attributes.labels.length, {
          attrs: {
            text: {
              text: this.utils.breakText(data.name, 15)
            },
          }
        });
      } else {
        (cell as joint.dia.Link).label(0, {
          attrs: {
            text: {
              text: this.utils.breakText(data.name, 15)
            },
          }
        });
      }
    }
  }

  /**
   * Does the requests from the requestqueues
   * "pendingImportantCSRequests" are the delete requests and need to be done first
   * "pendingCreateRequests" are the request for creating entities they need to be done bevore the normal alter requests
   * "pendingCSRequests" are the 'normal' request e.g. altering the boxId of a controller
   */
  async doPendingRequests(): Promise<void> {
    const impPromises: Promise<boolean>[] = [];

    try {
      await Promise.all(this.pendingImportantCSRequests);
    } catch (error) {

    }

    this.pendingCreateRequests.forEach(async (request: PendingRequestDTO) => {
      try {
        const response: any = await request.promise;
        impPromises.push(
          this.utils.alterBoxOrArrowId(
            response.id as string,
            request.entityType,
            request.shapeId
          )
        );
        if (
          request.entityType === ArrowAndBoxType.ControlAction ||
          request.entityType === ArrowAndBoxType.Feedback ||
          request.entityType === ArrowAndBoxType.Input ||
          request.entityType === ArrowAndBoxType.Output
        ) {
          this.setparentOfCell(request.shapeId, [response.id], []); // if cell is an arrow adds the parent to the existing one(s);
        } else {
          this.setparentOfCell(request.shapeId, [response.id]); // if cell is a box overwrites the exsiting parent
        }
      } catch (error) {
        this.messageService.add({
          severity: 'error',
          summary: PENDING_REQUEST_FAILED,
          detail: error.toString()
        });
      }
    });

    try {
      await Promise.all(impPromises);
    } catch (error) {
      this.messageService.add({
        severity: 'error',
        summary: PENDING_REQUEST_FAILED,
        detail: error.toString()
      });
    }

    try {
      await Promise.all(this.pendingCSRequests);
    } catch (error) {
      this.messageService.add({
        severity: 'error',
        summary: PENDING_REQUEST_FAILED,
        detail: error.toString()
      });
    } finally {
      this.clearRequestQueues();
    }
  }

  /**
   * clear the request queues
   */
  clearRequestQueues(): void {
    this.pendingImportantCSRequests = [];
    this.pendingCSRequests = [];
    this.pendingCreateRequests = [];
  }

  /**
   * Deletes a shape and all the connected entities
   * @param data: dto from the contextmenu
   */
  async deleteShape(data: ContextMenuData): Promise<any> {
    const cell: joint.dia.Cell = this.graph.getCell(data.id);
    data.type = ArrowAndBoxType[data.type];
    if (data.shape === 'box') {
      // todo modal to tell user that this links will also be deleted
      for (let i: number = 0; i < this.graph.getConnectedLinks(cell).length; i++) {
        const arrow: joint.dia.Link = this.graph.getConnectedLinks(cell)[i];
        this.utils
          .getEntitiesToArrows(
            CsUtils.jointTypeToArrowAndBoxType(
              arrow.attributes.attrs.xstampp.type
            ),
            arrow.attributes.id
          )
          .then((ent: ArrowEntityDTO[]) => {
            ent.forEach((arrowEntity: ArrowEntityDTO) => {
              this.pendingImportantCSRequests.push(
                this.utils.removeBoxOrArrowId(
                  arrowEntity.id,
                  CsUtils.jointTypeToArrowAndBoxType(
                    arrow.attributes.attrs.xstampp.type
                  )
                )
              );
            });
          });
      }
      // only delete if there is something linked
      if (data.deleteEntityIds.length > 0) {
        this.pendingImportantCSRequests.push(
          this.utils.removeBoxOrArrowId(data.deleteEntityIds[0], data.type)
        );
      }
    } else {
      data.deleteEntityIds.forEach((entityId: string) => {
        this.pendingImportantCSRequests.push(
          this.utils.removeBoxOrArrowId(entityId, data.type)
        );
      });
    }
    cell.remove();
    this.cscontextmenu = false;
    this.unlockInteraction();
  }

  /**
   * opens refine window and resets the CDS for editing
   */
  refineEntity($event: ContextMenuData): void {
    this.changeDiaType.emit('STEP4');
    // ruft detail oversheet in html auf
    this.refine = true;
  }

  /**
   * On refine window close: reset state to edit!
   */
  closeRefine(): void {
    this.refine = false;
    this.cds.resetStates();
    this.canDeactivate.setDeactivationMode(DeactivationMode.ALLOWED);
    this.cds.initStates(new Map([
      ['CSObject', false]
    ]));
    this.cds.updateState({
      key: 'CSObject',
      value: true
    });
  }

  closeContextmenu(data: ContextMenuData): void {
    // close menu and give access to graph back
    this.cscontextmenu = false;
    this.unlockInteraction();
    // checks if the contextmenu made any changes that need to be handled
    if (data) {
      const cell: joint.dia.Cell = this.graph.getCell(data.id);
      if (data.shape === 'box') {
        if (data.deleteEntityIds.length > 0) {
          this.pendingImportantCSRequests.push(
            this.utils.removeBoxOrArrowId(
              data.deleteEntityIds[0],
              CsUtils.jointTypeToArrowAndBoxType(data.type)
            )
          );
        }
        this.pendingCSRequests.push(
          this.utils.alterBoxOrArrowId(
            data.alterEntityIds[0],
            CsUtils.jointTypeToArrowAndBoxType(data.type),
            data.id
          )
        );
        this.setparentOfCell(data.id, data.alterEntityIds);
        cell.attr({
          label: {
            text: data.labels[0]
          }
        });
      } else {
        const dist: number = 100 / data.labels.length;
        const offset: number = dist / 2;
        const oldLables = (cell as joint.dia.Link).labels();
        (cell as joint.dia.Link).labels([]);
        data.labels.filter((ele: string) => ele.length > 0).forEach((ele: string, i: number) => {
          /**
           * FIXME: fucking stupid. Saves the postion of each arrow label in a string like:
           *
           * 'label'@?!@'distance'o'offset'
           * Would be better to use a link table for boxes and arrows with the id of the parentobject. But to do this
           * you would need to refactor the whole controlstructure in back- and frontend.
           */
          // TODO: get old labels from oldLabels array if they are there!
          (cell as joint.dia.Link).label(i, {
            attrs: {
              text: {
                text: ele.split('@?!@')[0] ? this.utils.breakText(ele.split('@?!@')[0], 15) : '',
              }
            },
            position: {
              distance: ele.split('@?!@')[1] && ele.split('@?!@')[1].split('o')[0] ? +ele.split('@?!@')[1].split('o')[0] : (i * dist + offset) / 110,
              offset: ele.split('@?!@')[1] && ele.split('@?!@')[1].split('o')[1] && +ele.split('@?!@')[1].split('o')[1],
            }
          });
        });
        let parentIds: string[] = [];
        let delIds: string[] = [];
        data.deleteEntityIds.forEach((deleteId: string) => {
          this.pendingImportantCSRequests.push(
            this.utils.removeBoxOrArrowId(
              deleteId,
              CsUtils.jointTypeToArrowAndBoxType(data.type)
            )
          );
          delIds.push(deleteId);
        });
        data.alterEntityIds.forEach((alterId: string) => {
          this.pendingCSRequests.push(
            this.utils.alterBoxOrArrowId(
              alterId,
              CsUtils.jointTypeToArrowAndBoxType(data.type),
              data.id
            )
          );
          parentIds.push(alterId);
        });
        this.setparentOfCell(data.id, parentIds, delIds);
      }
    }
  }

  lockInteraction(lockJointSVG: boolean = true): void {
    if (lockJointSVG) {
      this.paper.setInteractivity(false);
    }
    // this.panAndZoom.disableDblClickZoom();
    this.panAndZoom.disableZoom();
    this.panAndZoom.disablePan();
  }

  unlockInteraction(): void {
    this.paper.setInteractivity(true);
    // this.panAndZoom.enableDblClickZoom();
    this.panAndZoom.enableZoom();
    this.panAndZoom.enablePan();
  }

  reloadControlStructure(): void {
    this.controlStructureDataService
      .loadRootControlStructure(this.projectId)
      .then((value: ControlStructureDTO) => {
        this.response = value;
        this.graph.clear();
        this.parseJSONtoJoint();
        this.clearRequestQueues();
      })
      .catch((error: string) => {
        this.messageService.add({
          severity: 'error',
          summary: ERROR_GET_CONTROL_STRUCTURE,
          detail: error
        });
      });
  }

  /**
   * Cancels the editing, gives back lock, reloads the cs from the db, closes the toolbar
   */
  cancelEdit(): void {
    // todo fire modal
    this.mode = CsToolbarMode.READONLY;
    this.isEditable = false;
    this.cscontextmenu = false;
    this.paper.setInteractivity(false);
    this.controlStructureDataService
      .loadRootControlStructure(this.projectId)
      .then((value: ControlStructureDTO) => {
        this.response = value;
        this.graph.clear();
        this.parseJSONtoJoint();
        this.clearRequestQueues();
      })
      .catch((error: string) => {
        this.messageService.add({
          severity: 'error',
          summary: ERROR_GET_CONTROL_STRUCTURE,
          detail: error
        });
      });
  }

  closeOpenBar(): void {
    if (this.mode === CsToolbarMode.OPEN) {
      this.mode = CsToolbarMode.CLOSED;
    } else {
      this.mode = CsToolbarMode.OPEN;
    }
  }

  /**
   * Sets the parentId of the Cell. Can be an Arrow or a Box. Arrows can have more than one parent!
   *
   * @param shapeId the id of the Cell
   * @param parentIds the id to add (Arrow) or set (Box)
   * @param delParentIds the id's that gets deleted from the arrow
   */
  setparentOfCell(shapeId: string, parentIds: Array<string>, delParentIds?: Array<string>): void {
    /**
     * FIXME: refactor the whole CS in front- and backend and use a linktable, or better stringify the Joint-paper and sent it as
     * JSON to the BE!!!
     */
    if (!!delParentIds) {
      let parent = this.graph.getCell(shapeId).attr().xstampp.parent; // gets the current parentids
      let parents: string[] = parent ? parent.toString().split('@?!@') : []; // if current aprentids are null inits clean array
      let newParents = [
        ...parents.filter((label: string) =>
          delParentIds.findIndex((delLabel: string) => delLabel === label) === -1 // filters with the labels, that should be deleted
        ),
        ...parentIds, // adds the new labels for the arrow
      ];
      this.graph.getCell(shapeId).attr().xstampp.parent = newParents.join('@?!@'); // joins the labels with the selector
    } else {
      this.graph.getCell(shapeId).attr().xstampp.parent = parentIds[0]; // if it is for the box or the first time a arrow gets created
    }
  }

  async saveControlStructure(): Promise<void> {
    this.mode = CsToolbarMode.READONLY;
    this.cscontextmenu = false;
    this.isEditable = false;
    this.paper.setInteractivity(false);
    await this.doPendingRequests();
    this.parseJointToJSON();
    this.mouseDown = false;
  }

  parseJointToJSON(): void {
    const response: ControlStructureDTO = {
      projectId: this.projectId,
      svg: '',
      blackAndWhiteSVG: '',
      arrows: [],
      boxes: []
    } as ControlStructureDTO;
    const obj: any = this.graph.toJSON();
    response.svg = this.utils.parseSvgToString(this.paper.svg);
    response.blackAndWhiteSVG = this.utils.parseSvgToString(this.paper.svg, true);
    obj.cells.forEach((cell: any) => {
      if (cell.type === 'org.Arrow') {
        const arrow: Arrow = { } as Arrow;
        arrow.source = cell.source.id;
        arrow.destination = cell.target.id;
        arrow.projectId = cell.attrs.xstampp.projectId;
        let labelText: string = '';
        cell.labels && cell.labels.forEach((label: joint.dia.Link.Label) => {
          if (label.attrs.text.text.length > 0) {
            const dist: string = (label.position as joint.dia.Link.LabelPosition) ? (label.position as joint.dia.Link.LabelPosition).distance.toString() : '';
            const offset: string = (label.position as joint.dia.Link.LabelPosition) &&
              (label.position as joint.dia.Link.LabelPosition).offset ? (label.position as joint.dia.Link.LabelPosition).offset.toString() : '';
            labelText = labelText +
              label.attrs.text.text +
              '@?!@' + dist + 'o' + offset + '@!?@';
            labelText = this.utils.unBreakText(labelText);
          }
        });
        arrow.label = labelText.trim() || '';
        arrow.type = cell.attrs.xstampp.type;
        arrow.id = cell.id;
        arrow.parent = cell.attrs.xstampp.parent;
        if (cell.vertices) {
          arrow.parts = cell.vertices;
        }
        response.arrows.push(arrow);
      } else if (cell.type.includes('xstampp.')) {
        const box: Box = { } as Box;
        box.id = cell.id;
        if (cell.attrs.label) {
          box.name = this.utils.unBreakText(cell.attrs.label.text);
        } else {
          box.name = '';
        }
        box.type = cell.attrs.xstampp.type;
        box.projectId = cell.attrs.xstampp.projectId;
        box.parent = cell.attrs.xstampp.parent;
        box.x = cell.position.x;
        box.y = cell.position.y;
        box.height = cell.size.height;
        box.width = cell.size.width;
        response.boxes.push(box);
      }
    });
    this.controlStructureDataService
      .saveRootControlStructure(this.projectId, response)
      .then(() => {
        this.messageService.add({
          severity: 'success',
          summary: SAVE_SUCCESSFUL
        });
      })
      .catch((resp: HttpErrorResponse) => console.log(resp.error))
      .finally(() => {
        // TODO: consistency checks could be done here
      });
  }

  /**
   * Parses the box given from the service  to a shape object.
   */
  parseJSONtoJoint(): void {
    this.response.boxes.forEach((box: Box) => {
      let shape: any;
      switch (box.type) {
        case 'Controller': {
          shape = new this.shapeUtils.ControllerShape({ id: box.id });
          break;
        }
        case 'ControlledProcess': {
          shape = new this.shapeUtils.ProcessShape({ id: box.id });
          break;
        }
        case 'Sensor': {
          shape = new this.shapeUtils.SensorShape({ id: box.id });
          break;
        }
        case 'Actuator': {
          shape = new this.shapeUtils.ActuatorShape({ id: box.id });
          break;
        }
        case 'DashedBox': {
          shape = new this.shapeUtils.DashedBoxShape({ id: box.id });
          break;
        }
        case 'TextBox': {
          shape = new this.shapeUtils.TextBoxShape({ id: box.id });
          break;
        }
        case 'InputBox': {
          shape = new this.shapeUtils.InputBoxShape({ id: box.id });
          break;
        }
        case 'OutputBox': {
          shape = new this.shapeUtils.OutputBoxShape({ id: box.id });
          break;
        }
      }
      shape.position(box.x, box.y);
      shape.resize(box.width, box.height);
      const wrapText: string = this.utils.breakText(box.name, box.width / 10);
      // TODO: write CA, PM just in controller - @Felix
      shape.attr({
        label: {
          text: wrapText
        },
        xstampp: {
          projectId: box.projectId,
          parent: box.parent,
          type: box.type
        }
      });
      this.graph.addCell(shape);
    });
    this.response.arrows.forEach((arrow: Arrow) => {
      this.link(
        arrow.source,
        arrow.destination,
        arrow.parts as joint.dia.Link.Vertex[],
        arrow.label,
        arrow.type,
        arrow.projectId,
        arrow.parent,
        arrow.id,
      );
    });
  }

  editSheet(): void { }
  closeSheet(): void { }
  savePM($event): void { }
}
