import { Injectable } from '@angular/core';
import { RequestService, PostRequestParameters } from '../../request.service';
import {
  ControlledProcessDTO, SearchRequest, ControlledProcessResponseDTO, SensorDTO, ControlStructureDTO, Box, Arrow
} from '../../../types/local-types';
import { ConfigService } from '../../config.service';
import { EXPECTED_BOOLEAN_NOT_NULL, EXPECTED_ENTITY_NOT_NULL, EXPECTED_ENTITY_ARRAY_NOT_NULL } from './../../../globals';
import { HttpErrorResponse } from '@angular/common/http';
import { ControlStructureDataService } from '../control-structure-data.service';

@Injectable({
  providedIn: 'root'
})
export class ControlledProcessDataService {

  constructor(private readonly requestService: RequestService,
              private readonly config: ConfigService,
              private readonly controlStructureDataService: ControlStructureDataService) {
  }

  createControlledProcess(projectId: string, controlledProcess: ControlledProcessDTO): Promise<ControlledProcessDTO> {
    const postParameters: PostRequestParameters<ControlledProcessDTO> = {
      path: '/api/project/' + projectId + '/controlled-process/',
      json: JSON.stringify(controlledProcess),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<ControlledProcessDTO>((resolve: (value?: ControlledProcessDTO | PromiseLike<ControlledProcessDTO>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<ControlledProcessDTO>(postParameters)
        .then((value: ControlledProcessDTO): void => {
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

  alterControlledProcess(projectId: string, controlledProcessId: string, controlledProcess: ControlledProcessDTO): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPUTRequest<boolean>('/api/project/' + projectId + '/controlled-process/' + controlledProcessId,
        JSON.stringify(controlledProcess), this.config.useProjectToken())
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

  deleteControlledProcess(projectId: string, controlledProcessId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performDELETERequest<boolean>('/api/project/' + projectId + '/controlled-process/' + controlledProcessId, this.config.useProjectToken())
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

  getControlledProcessById(projectId: string, controlledProcessId: string): Promise<ControlledProcessDTO> {
    return new Promise<ControlledProcessDTO>((resolve: (value?: ControlledProcessDTO | PromiseLike<ControlledProcessDTO>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performGETRequest<ControlledProcessDTO>('/api/project/' + projectId + '/controlled-process/' + controlledProcessId, this.config.useProjectToken())
        .then((value: ControlledProcessDTO): void => {
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

  getAllControlledProcesses(projectId: string, options: SearchRequest): Promise<ControlledProcessDTO[]> {
    const postParameters: PostRequestParameters<ControlledProcessDTO[]> = {
      path: '/api/project/' + projectId + '/controlled-process/search',
      json: JSON.stringify(options),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<ControlledProcessDTO[]>((resolve: (value?: ControlledProcessDTO[] | PromiseLike<ControlledProcessDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<ControlledProcessDTO[]>(postParameters)
        .then((value: ControlledProcessDTO[]): void => {
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

  getAllControlledProcessesForTable(projectId: string, options: SearchRequest): Promise<ControlledProcessResponseDTO[]> {
    const postParameters: PostRequestParameters<ControlledProcessResponseDTO[]> = {
      path: '/api/project/' + projectId + '/controlled-process/search',
      json: JSON.stringify(options),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<ControlledProcessResponseDTO[]>((resolve: (value?: ControlledProcessResponseDTO[] | PromiseLike<ControlledProcessResponseDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<ControlledProcessResponseDTO[]>(postParameters)
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

  getControlledProcessByBoxId(projectId: string, boxId: string): Promise<ControlledProcessDTO> {
    return new Promise<ControlledProcessDTO>((resolve: (value?: ControlledProcessDTO | PromiseLike<ControlledProcessDTO>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performGETRequest<ControlledProcessDTO>('/api/project/' + projectId + '/controlled-process/box/' + boxId, this.config.useProjectToken())
        .then((value: ControlledProcessDTO): void => {
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

  setBoxId(projectId: string, controlledProcessId: string, boxId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPUTRequest<boolean>('/api/project/' + projectId + '/controlled-process/' +
        controlledProcessId + '/box/' + boxId, null, this.config.useProjectToken())
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

  getControlledProcessByFeedbackSourceArrowId(projectId: string, arrowId: string): Promise<ControlledProcessDTO> {
    return new Promise<ControlledProcessDTO>((resolve: (value?: ControlledProcessDTO | PromiseLike<ControlledProcessDTO>) => void, reject: (reason?: any) => void): void  => {
      this.controlStructureDataService.loadRootControlStructure(projectId)
        .then((value: ControlStructureDTO): void => {
          const boxes: Box[] = value.boxes;
          const arrows: Arrow[] = value.arrows;
          const feedbackArrows = arrows.filter((feedbackArrow: Arrow) => feedbackArrow.id === arrowId && feedbackArrow.type === 'Feedback');

          let controlledProcessBoxes = boxes.filter((box: Box) => box.type === 'ControlledProcess');
          let selectedControlledProcess: Box;

          controlledProcessBoxes.forEach((controlledProcess: Box) => {
            feedbackArrows.forEach((controlArrow: Arrow) => {
                if (controlArrow.source === controlledProcess.id) {
                  selectedControlledProcess = controlledProcess;
                }
              });
          });

          if (selectedControlledProcess) {
            this.getControlledProcessByBoxId(projectId, selectedControlledProcess.id)
              .then((controlledProcess: ControlledProcessDTO) => {
                resolve(controlledProcess);
                return controlledProcess;
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

  getControlledProcessByControlActionDestinationArrowId(projectId: string, arrowId: string): Promise<ControlledProcessDTO> {
    return new Promise<ControlledProcessDTO>((resolve: (value?: ControlledProcessDTO | PromiseLike<ControlledProcessDTO>) => void, reject: (reason?: any) => void): void  => {
      this.controlStructureDataService.loadRootControlStructure(projectId)
        .then((value: ControlStructureDTO): void => {

          const boxes: Box[] = value.boxes;
          const arrows: Arrow[] = value.arrows;
          const controlActionArrows = arrows.filter((controlAction: Arrow) => controlAction.id === arrowId && controlAction.type === 'ControlAction');

          let controlledProcesses = boxes.filter((box: Box) => box.type === 'ControlledProcess');
          let selectedControlledProcess: Box;

          controlledProcesses.forEach( (controlledProcess: Box) => {
            controlActionArrows.forEach((controlArrow: Arrow) => {
                if (controlArrow.destination === controlledProcess.id) {
                  selectedControlledProcess = controlledProcess;
                }
              });
          });

          if (selectedControlledProcess) {
            this.getControlledProcessByBoxId(projectId, selectedControlledProcess.id)
              .then((controlledProcess: ControlledProcessDTO) => {
                resolve(controlledProcess);
                return controlledProcess;
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
