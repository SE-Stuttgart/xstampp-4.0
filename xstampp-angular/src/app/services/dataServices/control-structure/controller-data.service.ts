
import {Injectable} from '@angular/core';
import {RequestService} from '../../request.service';
import {
  ControllerDTO, SearchRequest, ControllerResponseDTO, ControlledProcessDTO, ControlStructureDTO, Box, Arrow
} from '../../../types/local-types';
import {ConfigService} from '../../config.service';
import {EXPECTED_BOOLEAN_NOT_NULL, EXPECTED_ENTITY_NOT_NULL, EXPECTED_ENTITY_ARRAY_NOT_NULL} from './../../../globals';
import { HttpErrorResponse } from '@angular/common/http';
import { PostRequestParameters } from '../../request.service';

import { BoxEntityResponseDTO } from './../../../types/local-types';
import { ControlStructureDataService } from '../control-structure-data.service';
import {MessageService} from "primeng/api";


@Injectable({
  providedIn: 'root'
})
export class ControllerDataService {

  constructor(private readonly requestService: RequestService,
              private readonly config: ConfigService,
              private readonly controlStructureDataService: ControlStructureDataService,
              private readonly messageService: MessageService) {
  }

  public alterController(projectId: string, controllerId: string, controller: ControllerDTO): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPUTRequest<boolean>('/api/project/' + projectId + '/controller/' + controllerId, JSON.stringify(controller), this.config.useProjectToken())
        .then((value: boolean): void => {
          if (value != null) {
            resolve();
          } else {
            const error = new Error(EXPECTED_BOOLEAN_NOT_NULL);
            console.log(error);
            reject(error);
          }
        }).catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        }
        );
    });
  }

  public deleteController(projectId: string, controllerId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performDELETERequest<boolean>('/api/project/' + projectId + '/controller/' + controllerId, this.config.useProjectToken())
        .then((value: boolean): void => {
          if (value != null) {
            resolve();
          } else {
            const error = new Error(EXPECTED_BOOLEAN_NOT_NULL);
            console.log(error);
            reject(error);
          }
        }).catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        }
        );
    });
  }

  public createController(projectId: string, controller: ControllerDTO): Promise<ControllerDTO> {
    const postParameters: PostRequestParameters<ControllerDTO> = {
      path: '/api/project/' + projectId + '/controller',
      json: JSON.stringify(controller),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<ControllerDTO>((resolve: (value?: ControllerDTO | PromiseLike<ControllerDTO>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<ControllerDTO>(postParameters)
        .then((value: ControllerDTO): void => {
          if (value != null) {
            resolve(value);
          } else {
            const error = new Error(EXPECTED_ENTITY_NOT_NULL);
            console.log(error);
            reject(error);
          }
        }).catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        }
        );
    });
  }

  public getAllControllers(projectId: string, options: SearchRequest): Promise<ControllerDTO[]> {
    const postParameters: PostRequestParameters<ControllerDTO[]> = {
      path: '/api/project/' + projectId + '/controller/search',
      json: JSON.stringify(options),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<ControllerDTO[]>((resolve: (value?: ControllerDTO[] | PromiseLike<ControllerDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<ControllerDTO[]>(postParameters)
        .then((value: ControllerDTO[]): void => {
          if (value != null) {
            resolve(value);
          } else {
            const error = new Error(EXPECTED_ENTITY_ARRAY_NOT_NULL);
            console.log(error);
            reject(error);
          }
        }).catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        }
        );
    });
  }

  public getAllControllersForTable(projectId: string, options: SearchRequest): Promise<ControllerResponseDTO[]> {
    const postParameters: PostRequestParameters<ControllerResponseDTO[]> = {
      path: '/api/project/' + projectId + '/controller/search',
      json: JSON.stringify(options),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<ControllerResponseDTO[]>((resolve: (value?: ControllerResponseDTO[] | PromiseLike<ControllerResponseDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<ControllerResponseDTO[]>(postParameters)
        .then(value => {
          if (value != null) {

            resolve(value);
          } else {
            const error = new Error(EXPECTED_ENTITY_ARRAY_NOT_NULL);
            console.log(error);
            reject(error);
          }
        }).catch(reason => {
          console.error(reason);
          reject(reason.error);
        }
      );
    });
  }


  getControllerByBoxId(projectId: string, boxId: string): Promise<ControllerDTO> {
    return new Promise<ControllerDTO>((resolve: (value?: ControllerDTO | PromiseLike<ControllerDTO>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performGETRequest<ControllerDTO>('/api/project/' + projectId + '/controller/box/' + boxId, this.config.useProjectToken())
        .then((value: ControllerDTO): void => {
          if (value != null) {
            resolve(value);
          } else {
            const error = new Error(EXPECTED_ENTITY_NOT_NULL);
            console.log(error);
            reject(error);
          }
        }).catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        }
        );
    });
  }

  getControllerById(projectId: string, controllerId: string): Promise<ControllerDTO> {
    return new Promise<ControllerDTO>((resolve: (value?: ControllerDTO | PromiseLike<ControllerDTO>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performGETRequest<ControllerDTO>('/api/project/' + projectId + '/process-variable/sourceEntities/' + controllerId, this.config.useProjectToken())
        .then((value: ControllerDTO): void => {
          if (value != null) {
            resolve(value);
          } else {
            const error = new Error(EXPECTED_ENTITY_NOT_NULL);
            console.log(error);
            reject(error);
          }
        }).catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        }
        );
    });
  }

  getControllerByResponsibilityId(projectId: string, responsibilityId: string): Promise<ControllerDTO> {
    return new Promise<ControllerDTO>((resolve, reject) => {
      this.requestService.performGETRequest<ControllerDTO>('/api/project/' + projectId + '/responsibility/' + responsibilityId + '/controller', this.config.useProjectToken())
        .then((value: ControllerDTO) => {
          if (value != null) {
            resolve(value);
          } else {
            // null is a valid response
            resolve(null);
          }
        }).catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        });
    });
  }

  setBoxId(projectId: string, controllerId: string, boxId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPUTRequest<boolean>('/api/project/' + projectId + '/controller/' + controllerId + '/box/' + boxId, null, this.config.useProjectToken())
        .then((value: boolean): void => {
          if (value != null) {
            resolve();
          } else {
            const error = new Error(EXPECTED_BOOLEAN_NOT_NULL);
            console.log(error);
            reject(error);
          }
        }).catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        }
        );
    });
  }

  getSourceBoxByControllerId(projectId: string, controllerId: string): Promise<BoxEntityResponseDTO[]> {
    return new Promise<BoxEntityResponseDTO[]>((resolve: (value?: BoxEntityResponseDTO[] | PromiseLike<BoxEntityResponseDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performGETRequest<BoxEntityResponseDTO[]>('/api/project/' + projectId + '/process-variable/sourceEntities/' + controllerId, this.config.useProjectToken())
        .then((value: BoxEntityResponseDTO[]): void => {
          if (value != null) {
            resolve(value);
          } else {
            const error = new Error(EXPECTED_ENTITY_ARRAY_NOT_NULL);
            console.log(error);
            reject(error);
          }
        }).catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        }
        );
    });
  }

  getControllerByFeedbackDestinationArrowId(projectId: string, arrowId: string): Promise<ControllerDTO> {
    return new Promise<ControllerDTO>((resolve: (value?: ControllerDTO | PromiseLike<ControllerDTO>) => void, reject: (reason?: any) => void): void  => {
      this.controlStructureDataService.loadRootControlStructure(projectId)
        .then((value: ControlStructureDTO): void => {

          const boxes: Box[] = value.boxes;
          const arrows: Arrow[] = value.arrows;
          const feedbackArrows = arrows.filter((feedback: Arrow) => feedback.id === arrowId && feedback.type === 'Feedback');

          let controllers = boxes.filter((box: Box) => box.type === 'Controller');
          let selectedControllerBox: Box;

          controllers.forEach((controller: Box) => {
            feedbackArrows.forEach((feedbackArrow: Arrow) => {
                if (feedbackArrow.destination === controller.id) {
                  selectedControllerBox = controller;
                }
              });
          });

          if (selectedControllerBox) {
            this.getControllerByBoxId(projectId, selectedControllerBox.id)
              .then((controller: ControllerDTO) => {
                resolve(controller);
                return controller;
              }).catch((reason: HttpErrorResponse) => {
              console.error(reason);
              reject(reason.error);
            });
          } else {
            reject();
          }
        }).catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        }
      );
    });
  }

  getControllerByControlActionSourceArrowId(projectId: string, arrowId: string): Promise<ControllerDTO> {
    return new Promise<ControllerDTO>((resolve: (value?: ControllerDTO | PromiseLike<ControllerDTO>) => void, reject: (reason?: any) => void): void  => {
      this.controlStructureDataService.loadRootControlStructure(projectId)
        .then((value: ControlStructureDTO): void => {

          const boxes: Box[] = value.boxes;
          const arrows: Arrow[] = value.arrows;
          const controlActionArrows = arrows.filter((controlAction: Arrow) => controlAction.id === arrowId && controlAction.type === 'ControlAction');

          let controllers = boxes.filter((box: Box) => box.type === 'Controller');
          let selectedController: Box;

          controllers.forEach((controller: Box): void => {
            controlActionArrows.forEach((controlActionArrow: Arrow): void => {
                if (controlActionArrow.source === controller.id) {
                  selectedController = controller;
                }
              });
          });

          if (selectedController) {
            this.getControllerByBoxId(projectId, selectedController.id)
              .then((controller: ControllerDTO) => {
                resolve(controller);
                return controller;
              }).catch((reason: HttpErrorResponse) => {
              console.error(reason);
              reject(reason.error);
            });
          }

        }).catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        }
      );
    });
  }
}
