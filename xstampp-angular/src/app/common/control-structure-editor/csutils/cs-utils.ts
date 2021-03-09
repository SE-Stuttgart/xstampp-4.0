import { ActivatedRoute } from '@angular/router';
import * as joint from 'jointjs';
import { ControlStructureEntitiesWrapperService } from '../../../services/dataServices/control-structure/control-structure-entities-wrapper.service';
import { ArrowAndBoxType, ArrowEntityDTO, BoxEntityDTO, ControlActionDTO, FeedbackDTO, InputDTO, OutputDTO, PendingRequestDTO, PrepareRequestDTO } from '../../../types/local-types';

declare var require: any;

// TODO: it could happen, that joint breaks. Then try with these 2 imports.  ? - @Felix
// import * as _ from 'lodash';
// import * as Backbone from 'backbone';

export class CsUtils {
  projectId: string;
  constructor(private readonly route: ActivatedRoute, private readonly csEntitesWrapper: ControlStructureEntitiesWrapperService) {

    this.route.parent.params.subscribe((value: any) => {
      this.projectId = value.id;
    });
  }

  static jointTypeToArrowAndBoxType(boxType: string): ArrowAndBoxType {
    let shape: string;
    shape = boxType.replace('xstampp.', '').replace('Shape', '');
    if (shape === 'Process') {
      shape = 'ControlledProcess';
    }
    return ArrowAndBoxType[shape];
  }

  /**
   * Checks if the placement of the arrow on a specific box is valid in STPA
   * @param arrowType: Type of the currently used arrow
   * @param boxType: Type of the box the user has dropped the arrow on
   * @param source: Is the current box the source of the arrow?
   */
  static isValidPlacement(arrowType: ArrowAndBoxType, boxType: ArrowAndBoxType, source: boolean): boolean {
    /*
    TextBox and DashedBox don't represent entities and can't be source or target for an arrow
     */
    if (boxType === ArrowAndBoxType.TextBox || boxType === ArrowAndBoxType.DashedBox) {
      return false;
    }
    /*
    No arrow can point to an InputBox
     */
    if (boxType === ArrowAndBoxType.InputBox && !source) {
      return false;
    }
    /*
    No arrow can originate from an OutputBox
     */
    if (boxType === ArrowAndBoxType.OutputBox && source) {
      return false;
    }
    switch (arrowType) {
      case ArrowAndBoxType.ControlAction: {
        if (source) {
          return boxType === ArrowAndBoxType.Controller || boxType === ArrowAndBoxType.Actuator;
        } else {
          return boxType === ArrowAndBoxType.Actuator || boxType === ArrowAndBoxType.ControlledProcess || boxType === ArrowAndBoxType.Controller;
        }
      }
      case ArrowAndBoxType.Feedback: {
        if (source) {
          return boxType === ArrowAndBoxType.ControlledProcess || boxType === ArrowAndBoxType.Sensor || boxType === ArrowAndBoxType.Controller;
        } else {
          return boxType === ArrowAndBoxType.Controller || boxType === ArrowAndBoxType.Sensor;
        }
      }
      case ArrowAndBoxType.Input: {
        if (source) {
          return boxType === ArrowAndBoxType.InputBox;
        } else {
          return boxType !== ArrowAndBoxType.InputBox;
        }
      }
      case ArrowAndBoxType.Output: {
        if (source) {
          return boxType !== ArrowAndBoxType.OutputBox;
        } else {
          return boxType === ArrowAndBoxType.OutputBox;
        }
      }
    }
  }

  getEntitiesToBox(type: ArrowAndBoxType, id: string): Promise<BoxEntityDTO> {
    switch (type) {
      case ArrowAndBoxType.ControlledProcess: {
        return this.csEntitesWrapper.controlledProcessDataService.getControlledProcessByBoxId(this.projectId, id);
      }
      case ArrowAndBoxType.Controller: {
        return this.csEntitesWrapper.controllerDataService.getControllerByBoxId(this.projectId, id);
      }
      case ArrowAndBoxType.Actuator: {
        return this.csEntitesWrapper.actuatorDataService.getActuatorByBoxId(this.projectId, id);
      }
      case ArrowAndBoxType.Sensor: {
        return this.csEntitesWrapper.sensorDataService.getSensorByBoxId(this.projectId, id);
      }
    }
  }

  async getEntitiesToArrows(type: ArrowAndBoxType, id: string): Promise<ArrowEntityDTO[]> {
    try {
      switch (type) {
        case ArrowAndBoxType.ControlAction: {
          const entity: ControlActionDTO[] = await this.csEntitesWrapper.controlActionDataService.getControlActionsByArrowId(this.projectId, id);
          return entity as ControlActionDTO[];
        }
        case ArrowAndBoxType.Feedback: {
          const entity: FeedbackDTO[] = await this.csEntitesWrapper.feedbackDataService.getFeedbacksByArrowId(this.projectId, id);
          return entity as FeedbackDTO[];
        }
        case ArrowAndBoxType.Input: {
          const entity: InputDTO[] = await this.csEntitesWrapper.inputDataService.getInputsByArrowId(this.projectId, id);
          return entity as InputDTO[];
        }
        case ArrowAndBoxType.Output: {
          const entity: OutputDTO[] = await this.csEntitesWrapper.outputDataService.getOutputsByArrowId(this.projectId, id);
          return entity as OutputDTO[];
        }
      }
    } catch (e) {
      return null;
    }
    return new Promise<ArrowEntityDTO[]>((resolve: any): void => {
      resolve(null);
    });
  }

  /**
   * Creates the entities from the cs
   * @param entityToCreate: given entity to create
   */
  createEntity(entityToCreate: PrepareRequestDTO): PendingRequestDTO {
    let promise: Promise<any>;
    switch (entityToCreate.entityType) {
      case ArrowAndBoxType.ControlledProcess: {
        promise = this.csEntitesWrapper.controlledProcessDataService.createControlledProcess(this.projectId, entityToCreate.boxEntity);
        break;
      }
      case ArrowAndBoxType.Controller: {
        promise = this.csEntitesWrapper.controllerDataService.createController(this.projectId, entityToCreate.boxEntity);
        break;
      }
      case ArrowAndBoxType.Actuator: {
        promise = this.csEntitesWrapper.actuatorDataService.createActuator(this.projectId, entityToCreate.boxEntity);
        break;
      }
      case ArrowAndBoxType.Sensor: {
        promise = this.csEntitesWrapper.sensorDataService.createSensor(this.projectId, entityToCreate.boxEntity);
        break;
      }
      case ArrowAndBoxType.ControlAction: {
        promise = this.csEntitesWrapper.controlActionDataService.createControlAction(this.projectId, entityToCreate.arrowEntity);
        break;
      }
      case ArrowAndBoxType.Feedback: {
        promise = this.csEntitesWrapper.feedbackDataService.createFeedback(this.projectId, entityToCreate.arrowEntity);
        break;
      }
      case ArrowAndBoxType.Input: {
        promise = this.csEntitesWrapper.inputDataService.createInput(this.projectId, entityToCreate.arrowEntity);
        break;
      }
      case ArrowAndBoxType.Output: {
        promise = this.csEntitesWrapper.outputDataService.createOutput(this.projectId, entityToCreate.arrowEntity);
        break;
      }
    }
    return { promise: promise, entityType: entityToCreate.entityType, shapeId: entityToCreate.shapeId };
  }

  removeBoxOrArrowId(id: string, entityType: ArrowAndBoxType): Promise<boolean> {
    return this.alterBoxOrArrowId(id, entityType, null);
  }

  alterBoxOrArrowId(id: string, entityType: ArrowAndBoxType, shapeId: string): Promise<boolean> {
    let promise: Promise<boolean>;
    switch (entityType) {
      case ArrowAndBoxType.ControlledProcess: {
        promise = this.csEntitesWrapper.controlledProcessDataService.setBoxId(this.projectId, id, shapeId);
        break;
      }
      case ArrowAndBoxType.Controller: {
        promise = this.csEntitesWrapper.controllerDataService.setBoxId(this.projectId, id, shapeId);
        break;
      }
      case ArrowAndBoxType.Actuator: {
        promise = this.csEntitesWrapper.actuatorDataService.setBoxId(this.projectId, id, shapeId);
        break;
      }
      case ArrowAndBoxType.Sensor: {
        promise = this.csEntitesWrapper.sensorDataService.setBoxId(this.projectId, id, shapeId);
        break;
      }
      case ArrowAndBoxType.ControlAction: {
        promise = this.csEntitesWrapper.controlActionDataService.setArrowId(this.projectId, id, shapeId);
        break;
      }
      case ArrowAndBoxType.Feedback: {
        promise = this.csEntitesWrapper.feedbackDataService.setArrowId(this.projectId, id, shapeId);
        break;
      }
      case ArrowAndBoxType.Input: {
        promise = this.csEntitesWrapper.inputDataService.setArrowId(this.projectId, id, shapeId);
        break;
      }
      case ArrowAndBoxType.Output: {
        promise = this.csEntitesWrapper.outputDataService.setArrowId(this.projectId, id, shapeId);
        break;
      }
    }
    return promise;
  }

  /**
   * Checks if the Browser is Safari (doesn't support all jointJS features)
   */
  get isSafari(): boolean {
    return navigator.vendor && navigator.vendor.indexOf('Apple') > -1 &&
      navigator.userAgent &&
      navigator.userAgent.indexOf('CriOS') === -1 &&
      navigator.userAgent.indexOf('FxiOS') === -1;
  }

  /**
   * removes hover elements from svg and replaces spaces.
   */
  parseSvgToString(svg: SVGElement, isBlackWhite: boolean = false): string {
    // fit svg to content
    this.setSVGDimentions(svg);

    // make black and white
    if (isBlackWhite) {
      this.makeSVGBlackAndWhite(svg);
    }

    // delete hover elements
    this.clearSVGHoverElements(svg);

    // set opacity of arrows with a path
    this.setSVGOpacity(svg);

    const returnSVG: string = svg.outerHTML.replace(new RegExp('&nbsp;', 'g'), ' '); // replace spaces
    this.resetSVGElement(svg, isBlackWhite);
    return returnSVG;
  }

  /**
   * Sests the dimentions of the print svg
   */
  private setSVGDimentions(svg: SVGElement): void {
    let bb = (svg as any).getBBox();
    let bbx = bb.x;
    let bby = bb.y - 10;
    let bbw = bb.width;
    let bbh = bb.height + 20;
    let vb = [bbx, bby, bbw, bbh];
    svg.setAttribute('viewBox', vb.join(' '));
  }

  /**
   * delets the hover elements in the print svg
   */
  private clearSVGHoverElements(svg: SVGElement): void {
    Array.from(svg.getElementsByClassName('link-tools')).forEach((ele: SVGElement) => ele.parentNode.removeChild(ele));
    Array.from(svg.getElementsByClassName('marker-arrowheads')).forEach((ele: SVGElement) => ele.parentNode.removeChild(ele));
    Array.from(svg.getElementsByClassName('marker-vertices')).forEach((ele: SVGElement) => ele.parentNode.removeChild(ele));
  }

  private setSVGOpacity(svg: SVGElement): void {
    Array.from(svg.getElementsByClassName('joint-type-org-arrow')).forEach((ele: SVGElement) => ele.setAttribute('fill-opacity', '0.0'));
    Array.from(svg.getElementsByClassName('marker-target')).forEach((ele: SVGElement) => ele.setAttribute('fill-opacity', '1.0'));
    Array.from(svg.getElementsByClassName('label')).forEach((ele: SVGElement) => ele.setAttribute('fill-opacity', '1.0'));
  }

  /**
   * changes colours to black and white and sets border stroke of boxes.
   */
  private makeSVGBlackAndWhite(svg: SVGElement): void {
    // stes arrow heads
    Array.from(svg.getElementsByClassName('marker-target')).forEach((ele: SVGElement) => {
      if (ele.getAttribute('fill') === '#ffc700') {
        ele.setAttribute('fill', 'black');
      } else {
        console.log('this gets fired');
        ele.setAttribute('fill', 'white');
      }
    });
    // sets border-stroke of Boxes
    Array.from(svg.getElementsByClassName('joint-type-xstampp-sensorshape')).forEach((ele: SVGElement) => {
      Array.from(ele.getElementsByTagName('rect')).forEach((subEle: SVGRectElement) => {
        subEle.setAttribute('stroke-dasharray', '10,10');
        subEle.setAttribute('stroke', 'black');
      });
    });
    Array.from(svg.getElementsByClassName('joint-type-xstampp-actuatorshape')).forEach((ele: SVGElement) => {
      Array.from(ele.getElementsByTagName('rect')).forEach((subEle: SVGRectElement) => {
        subEle.setAttribute('stroke', 'black');
      });
    });
    Array.from(svg.getElementsByClassName('joint-type-xstampp-processshape')).forEach((ele: SVGElement) => {
      Array.from(ele.getElementsByTagName('rect')).forEach((subEle: SVGRectElement) => {
        subEle.setAttribute('stroke-dasharray', '10,2,2,2');
        subEle.setAttribute('stroke', 'black');
      });
    });
    Array.from(svg.getElementsByClassName('joint-type-xstampp-controllershape')).forEach((ele: SVGElement) => {
      Array.from(ele.getElementsByTagName('rect')).forEach((subEle: SVGRectElement) => {
        // subEle.setAttribute('stroke-dasharray', '10,2,2,2');
        subEle.setAttribute('fill', 'white'); // was white / green / PaleTurquoise
        subEle.setAttribute('stroke', 'black'); // was #3700FF
      });
      Array.from(ele.getElementsByTagName('text')).forEach((subEle: SVGTextElement) => {
        console.log('set text');
        subEle.setAttribute('fill', 'black'); // was black / white / black
      });
    });
  }

  /**
   * resets the original svg
   */
  private resetSVGElement(svg: SVGElement, isBlackWhite: boolean): void {
    // resets border-stroke of Boxes
    Array.from(svg.getElementsByClassName('joint-type-xstampp-sensorshape')).forEach((ele: SVGElement) => {
      Array.from(ele.getElementsByTagName('rect')).forEach((subEle: SVGRectElement) => {
        subEle.removeAttribute('stroke-dasharray');
        subEle.setAttribute('stroke', '#00ff00');
      });
    });
    Array.from(svg.getElementsByClassName('joint-type-xstampp-actuatorshape')).forEach((ele: SVGElement) => {
      Array.from(ele.getElementsByTagName('rect')).forEach((subEle: SVGRectElement) => {
        subEle.setAttribute('stroke', '#ffc700');
      });
    });
    Array.from(svg.getElementsByClassName('joint-type-xstampp-processshape')).forEach((ele: SVGElement) => {
      Array.from(ele.getElementsByTagName('rect')).forEach((subEle: SVGRectElement) => {
        subEle.removeAttribute('stroke-dasharray');
        subEle.setAttribute('stroke', '#8d0083');
      });
    });

    Array.from(svg.getElementsByClassName('joint-type-xstampp-controllershape')).forEach((ele: SVGElement) => {
      Array.from(ele.getElementsByTagName('rect')).forEach((subEle: SVGRectElement, index: number) => {
        switch (index) {
          case (0): {
            subEle.setAttribute('fill', 'white');
            break;
          }
          case (1): {
            subEle.setAttribute('fill', 'green');
            break;
          }
          case (2): {
            subEle.setAttribute('fill', 'PaleTurquoise');
            break;
          }
        }
        subEle.setAttribute('stroke', '#3700FF');
      });
      Array.from(ele.getElementsByTagName('text')).forEach((subEle: SVGTextElement, index: number) => {
        subEle.setAttribute('fill', index === 1 ? 'white' : 'black');
      });
    });
    // resets arrow heads
    if (isBlackWhite) {
      Array.from(svg.getElementsByClassName('marker-target')).forEach((ele: Element) => {
        if (ele.getAttribute('fill') === 'white') {
          ele.setAttribute('fill', '#00ff00');
        } else {
          ele.setAttribute('fill', '#ffc700');
        }
      });
    }
    // resets viewBox
    svg.removeAttribute('viewBox');
  }
  /** checks if the browser is firefox
  * (firefox is the only browser, that calculates the layer position the right way);
  */
  get isFirefox(): boolean {
    return navigator.userAgent.toLocaleLowerCase().indexOf('firefox') > -1;
  }

  breakText(text: string, maxLineLength: number): string {
    let newText = '';
    let lineLength: number = 0;
    text.split(' ').forEach((subText: string, i: number, arr: Array<string>) => {
      newText += subText;
      lineLength += subText.length;
      if (i !== arr.length - 1) {
        if (lineLength >= maxLineLength) {
          newText += '\n';
          lineLength = 0;
        } else {
          newText += ' ';
        }
      }
    });
    return newText;
  }

  unBreakText(text: string): string {
    return text.replace(/(\r\n|\n|\r)/gm, ' ');
  }
}
