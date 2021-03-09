import { Injectable } from '@angular/core';
import { RequestService, PostRequestParameters } from '../../request.service';
import {
  ActuatorDTO, SearchRequest, ActuatorResponseDTO, ControlActionDTO, ControlStructureDTO, Arrow, Box
} from '../../../types/local-types';
import { ConfigService } from '../../config.service';
import { EXPECTED_BOOLEAN_NOT_NULL, EXPECTED_ENTITY_NOT_NULL, EXPECTED_ENTITY_ARRAY_NOT_NULL } from './../../../globals';
import { HttpErrorResponse } from '@angular/common/http';
import { ControlStructureDataService } from '../control-structure-data.service';
import { BoxDataService } from '../box-data.service';

@Injectable({
  providedIn: 'root'
})
export class ActuatorDataService {

  constructor(private readonly requestService: RequestService,
              private readonly config: ConfigService,
              private readonly controlStructureDataService: ControlStructureDataService) {
  }

  createActuator(projectId: string, actuator: ActuatorDTO): Promise<ActuatorDTO> {
    const postParameters: PostRequestParameters<ActuatorDTO> = {
      path: '/api/project/' + projectId + '/actuator/',
      json: JSON.stringify(actuator),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<ActuatorDTO>((resolve: (value?: ActuatorDTO | PromiseLike<ActuatorDTO>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<ActuatorDTO>(postParameters)
        .then((value: ActuatorDTO): void => {
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

  alterActuator(projectId: string, actuatorId: string, actuator: ActuatorDTO): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPUTRequest<boolean>('/api/project/' + projectId + '/actuator/' + actuatorId, JSON.stringify(actuator), this.config.useProjectToken())
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

  deleteActuator(projectId: string, actuatorId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performDELETERequest<boolean>('/api/project/' + projectId + '/actuator/' + actuatorId, this.config.useProjectToken())
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

  getActuatorById(projectId: string, actuatorId: string): Promise<ActuatorDTO> {
    return new Promise<ActuatorDTO>((resolve: (value?: ActuatorDTO | PromiseLike<ActuatorDTO>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performGETRequest<ActuatorDTO>('/api/project/' + projectId + '/actuator/' + actuatorId, this.config.useProjectToken())
        .then((value: ActuatorDTO): void => {
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

  getAllActuators(projectId: string, options: SearchRequest): Promise<ActuatorDTO[]> {
    const postParameters: PostRequestParameters<ActuatorDTO[]> = {
      path: '/api/project/' + projectId + '/actuator/search',
      json: JSON.stringify(options),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<ActuatorDTO[]>((resolve: (value?: ActuatorDTO[] | PromiseLike<ActuatorDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<ActuatorDTO[]>(postParameters)
        .then((value: ActuatorDTO[]): void => {
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
  getAllActuatorsForTable(projectId: string, options: SearchRequest): Promise<ActuatorResponseDTO[]> {
    const postParameters: PostRequestParameters<ActuatorResponseDTO[]> = {
      path: '/api/project/' + projectId + '/actuator/search',
      json: JSON.stringify(options),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<ActuatorResponseDTO[]>((resolve: (value?: ActuatorResponseDTO[] | PromiseLike<ActuatorResponseDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<ActuatorResponseDTO[]>(postParameters)
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

  getActuatorByBoxId(projectId: string, boxId: string): Promise<ActuatorDTO> {
    return new Promise<ActuatorDTO>((resolve: (value?: ActuatorDTO | PromiseLike<ActuatorDTO>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performGETRequest<ActuatorDTO>('/api/project/' + projectId + '/actuator/box/' + boxId, this.config.useProjectToken())
        .then((value: ActuatorDTO): void => {
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

  setBoxId(projectId: string, actuatorId: string, boxId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPUTRequest<boolean>('/api/project/' + projectId + '/actuator/' + actuatorId + '/box/' + boxId, null, this.config.useProjectToken())
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

  getActuatorByControlActionDestinationArrowId(projectId: string, arrowId: string): Promise<ActuatorDTO> {
    return new Promise<ActuatorDTO>((resolve: (value?: ActuatorDTO | PromiseLike<ActuatorDTO>) => void, reject: (reason?: any) => void): void  => {
      this.controlStructureDataService.loadRootControlStructure(projectId)
        .then((value: ControlStructureDTO): void => {

          const boxes: Box[] = value.boxes;
          const arrows: Arrow[] = value.arrows;
          const controlActionArrows = arrows.filter((controlAction: Arrow) => controlAction.id === arrowId && controlAction.type === 'ControlAction');
          let actuators = boxes.filter((actuatorBox: Box) => actuatorBox.type === 'Actuator');
          let selectedActuator: Box;

          actuators.forEach( (actuator: Box) => {
            controlActionArrows.forEach((controlArrow: Arrow) => {
              if (controlArrow.destination === actuator.id) {
                selectedActuator = actuator;
              }
            });
          });

          if (selectedActuator) {
            this.getActuatorByBoxId(projectId, selectedActuator.id)
              .then((actuator: ActuatorDTO) => {
                resolve(actuator);
                return actuator;
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
