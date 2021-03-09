import { ControlStructureDataService } from './../control-structure-data.service';
import { Injectable } from '@angular/core';
import { RequestService, PostRequestParameters } from '../../request.service';
import {
  Arrow, ControlActionDTO, SearchRequest, ControlStructureDTO, ControlStructureEntityDTO, ControlActionRequestDTO,
  ControlActionResponseDTO, ARROW, Box, ActuatorDTO
} from '../../../types/local-types';
import { ConfigService } from '../../config.service';
import { EXPECTED_BOOLEAN_NOT_NULL, EXPECTED_ENTITY_NOT_NULL, EXPECTED_ENTITY_ARRAY_NOT_NULL } from './../../../globals';
import { HttpErrorResponse } from '@angular/common/http';
import { BoxDataService } from '../box-data.service';
import { ActuatorDataService } from './actuator-data.service';
import { MessageService } from 'primeng/api';

@Injectable({
  providedIn: 'root'
})
export class ControlActionDataService {

  constructor(private readonly requestService: RequestService,
              private readonly config: ConfigService,
              private readonly controlStructureDataService: ControlStructureDataService,
              private readonly actuatorDataService: ActuatorDataService) {
  }

  createControlAction(projectId: string, controlAction: ControlActionDTO): Promise<ControlActionDTO> {
    const postParameters: PostRequestParameters<ControlActionDTO> = {
      path: '/api/project/' + projectId + '/control-action/',
      json: JSON.stringify(controlAction),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<ControlActionDTO>((resolve: (value?: ControlActionDTO | PromiseLike<ControlActionDTO>) => void, reject: (reason?: any) => void): void  => {
      this.requestService.performPOSTRequest<ControlActionDTO>(postParameters)
        .then((value: ControlActionDTO): void => {
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

  alterControlAction(projectId: string, controlActionId: string, controlAction: ControlActionDTO): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPUTRequest<boolean>('/api/project/' + projectId + '/control-action/' + controlActionId,
        JSON.stringify(controlAction), this.config.useProjectToken())
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

  deleteControlAction(projectId: string, controlActionId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void  => {
      this.requestService.performDELETERequest<boolean>('/api/project/' + projectId + '/control-action/' + controlActionId, this.config.useProjectToken())
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

  getControlActionById(projectId: string, controlActionId: string): Promise<ControlActionDTO> {
    return new Promise<ControlActionDTO>((resolve: (value?: ControlActionDTO | PromiseLike<ControlActionDTO>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performGETRequest<ControlActionDTO>('/api/project/' + projectId + '/control-action/' + controlActionId, this.config.useProjectToken())
        .then((value: ControlActionDTO): void => {
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

  getAllControlActions(projectId: string, options: SearchRequest): Promise<ControlActionDTO[]> {
    const postParameters: PostRequestParameters<ControlActionDTO[]> = {
      path: '/api/project/' + projectId + '/control-action/search',
      json: JSON.stringify(options),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<ControlActionDTO[]>((resolve: (value?: ControlActionDTO[] | PromiseLike<ControlActionDTO[]>) => void, reject: (reason?: any) => void): void  => {
      this.requestService.performPOSTRequest<ControlActionDTO[]>(postParameters)
        .then((value: ControlActionDTO[]): void => {
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

  getAllControlActionsForTable(projectId: string, options: SearchRequest): Promise<ControlActionResponseDTO[]> {

    const postParameters: PostRequestParameters<ControlActionResponseDTO[]> = {
      path: '/api/project/' + projectId + '/control-action/search',
      json: JSON.stringify(options),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<ControlActionResponseDTO[]>((resolve: (value?: ControlActionResponseDTO[] | PromiseLike<ControlActionResponseDTO[]>) => void, reject: (reason?: any) => void): void  => {
      this.requestService.performPOSTRequest<ControlActionResponseDTO[]>(postParameters)
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

  getControlActionsByArrowId(projectId: string, arrowId: string): Promise<ControlActionDTO[]> {
    return new Promise<ControlActionDTO[]>((resolve: (value?: ControlActionDTO[] | PromiseLike<ControlActionDTO[]>) => void, reject: (reason?: any) => void): void  => {
      this.requestService.performGETRequest<ControlActionDTO[]>('/api/project/' + projectId + '/control-action/arrow/' + arrowId, this.config.useProjectToken())
        .then((value: ControlActionDTO[]): void => {
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

  getControlActionBySourceBoxId(projectId: string, boxId: string): Promise<ControlActionDTO[]> {
    return new Promise<ControlActionDTO[]>((resolve: (value?: ControlActionDTO[] | PromiseLike<ControlActionDTO[]>) => void, reject: (reason?: any) => void): void  => {
      this.controlStructureDataService.loadRootControlStructure(projectId)
        .then((value: ControlStructureDTO): void => {
          const arrows: Arrow[] = value.arrows;
          const arrowsOfTypeControlAction = arrows.filter((arrow: Arrow) => arrow.source === boxId && arrow.type === 'ControlAction');

          // TODO can a box has only one arrow or multiple ?
          if (arrowsOfTypeControlAction.length > 0) {
            this.getControlActionsByArrowId(projectId, arrowsOfTypeControlAction[0].id)
              .then((controlActions: ControlActionDTO[]) => {
              resolve(controlActions);
              return controlActions;
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

  getControlActionByDestinationBoxId(projectId: string, boxId: string): Promise<ControlActionDTO[]> {
    return new Promise<ControlActionDTO[]>((resolve: (value?: ControlActionDTO[] | PromiseLike<ControlActionDTO[]>) => void, reject: (reason?: any) => void): void  => {
      this.controlStructureDataService.loadRootControlStructure(projectId)
        .then((value: ControlStructureDTO): void => {
          const arrows: Arrow[] = value.arrows;
          const arrowsOfTypeControlAction = arrows.filter((arrow: Arrow) => arrow.destination === boxId && arrow.type === 'ControlAction');

          // TODO can a box has only one arrow or multiple ?
          if (arrowsOfTypeControlAction.length > 0) {
            this.getControlActionsByArrowId(projectId, arrowsOfTypeControlAction[0].id)
              .then((controlActions: ControlActionDTO[]) => {
                resolve(controlActions);
                return controlActions;
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

  setArrowId(projectId: string, controlActionId: string, arrowId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void  => {
      this.requestService.performPUTRequest<boolean>('/api/project/' + projectId + '/control-action/' + controlActionId + '/arrow/' + arrowId, null, this.config.useProjectToken())
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
}
