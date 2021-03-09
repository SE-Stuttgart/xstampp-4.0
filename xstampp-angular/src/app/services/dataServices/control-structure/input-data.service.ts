import { Injectable } from '@angular/core';
import { RequestService, PostRequestParameters } from '../../request.service';
import {
  InputDTO, SearchRequest, InputArrowResponseDTO, ControlActionDTO, ControlStructureDTO, Arrow, Box
} from '../../../types/local-types';
import { ConfigService } from '../../config.service';
import { EXPECTED_BOOLEAN_NOT_NULL, EXPECTED_ENTITY_NOT_NULL, EXPECTED_ENTITY_ARRAY_NOT_NULL } from './../../../globals';
import { HttpErrorResponse } from '@angular/common/http';
import { ControlStructureDataService } from '../control-structure-data.service';
import {MessageService} from "primeng/api";


@Injectable({
  providedIn: 'root'
})
export class InputDataService {

  constructor(private readonly requestService: RequestService,
              private readonly config: ConfigService,
              private readonly controlStructureDataService: ControlStructureDataService,
              private readonly messageService: MessageService) {
  }

  createInput(projectId: string, input: InputDTO): Promise<InputDTO> {
    const postParameters: PostRequestParameters<InputDTO> = {
      path: '/api/project/' + projectId + '/input/',
      json: JSON.stringify(input),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<InputDTO>((resolve: (value?: InputDTO | PromiseLike<InputDTO>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<InputDTO>(postParameters)
        .then((value: InputDTO): void => {
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

  alterInput(projectId: string, inputId: string, input: InputDTO): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPUTRequest<boolean>('/api/project/' + projectId + '/input/' + inputId, JSON.stringify(input), this.config.useProjectToken())
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

  deleteInput(projectId: string, inputId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performDELETERequest<boolean>('/api/project/' + projectId + '/input/' + inputId, this.config.useProjectToken())
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

  getInputById(projectId: string, inputId: string): Promise<InputDTO> {
    return new Promise<InputDTO>((resolve: (value?: InputDTO | PromiseLike<InputDTO>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performGETRequest<InputDTO>('/api/project/' + projectId + '/input/' + inputId, this.config.useProjectToken())
        .then((value: InputDTO): void => {
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

  getAllInputs(projectId: string, options: SearchRequest): Promise<InputDTO[]> {
    const postParameters: PostRequestParameters<InputDTO[]> = {
      path: '/api/project/' + projectId + '/input/search',
      json: JSON.stringify(options),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<InputDTO[]>((resolve: (value?: InputDTO[] | PromiseLike<InputDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<InputDTO[]>(postParameters)
        .then((value: InputDTO[]): void => {
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

  getAllInputsForTable(projectId: string, options: SearchRequest): Promise<InputArrowResponseDTO[]> {
    const postParameters: PostRequestParameters<InputArrowResponseDTO[]> = {
      path: '/api/project/' + projectId + '/input/search',
      json: JSON.stringify(options),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<InputArrowResponseDTO[]>((resolve: (value?: InputArrowResponseDTO[] | PromiseLike<InputArrowResponseDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<InputArrowResponseDTO[]>(postParameters)
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

  getInputsByArrowId(projectId: string, arrowId: string): Promise<InputDTO[]> {
    return new Promise<InputDTO[]>((resolve: (value?: InputDTO[] | PromiseLike<InputDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performGETRequest<InputDTO[]>('/api/project/' + projectId + '/input/arrow/' + arrowId, this.config.useProjectToken())
        .then((value: InputDTO[]): void => {
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

  setArrowId(projectId: string, inputId: string, arrowId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPUTRequest<boolean>('/api/project/' + projectId + '/input/' + inputId + '/arrow/' + arrowId, null, this.config.useProjectToken())
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

  getInputsByBoxDestinationId(projectId: string, boxId: string): Promise<InputDTO[]> {
    return new Promise<InputDTO[]>((resolve: (value?: InputDTO[] | PromiseLike<InputDTO[]>) => void, reject: (reason?: any) => void): void  => {
      this.controlStructureDataService.loadRootControlStructure(projectId)
        .then((value: ControlStructureDTO): void => {
          const arrows: Arrow[] = value.arrows;
          const arrowsOfTypeInput = arrows.filter((arrow: Arrow) => arrow.destination === boxId && arrow.type === 'Input');

          // TODO can a box has only one arrow or multiple ?
          if (arrowsOfTypeInput.length > 0) {
            arrowsOfTypeInput.forEach((input: Arrow) => {

            });
            this.getInputsByArrowId(projectId, arrowsOfTypeInput[0].id)
              .then((inputs: InputDTO[]) => {
                resolve(inputs);
                return inputs;
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

  getInputsDestinations(projectId: string, inputDTO: InputDTO): Promise<Box[]> {
    return new Promise<Box[]>((resolve: (value?: Box[] | PromiseLike<Box[]>) => void, reject: (reason?: any) => void): void  => {
      this.controlStructureDataService.loadRootControlStructure(projectId)
        .then((value: ControlStructureDTO): void => {
          const arrows: Arrow[] = value.arrows;
          const boxes: Box[] = value.boxes;
          const arrowsOfTypeInput = arrows.filter((arrow: Arrow) => arrow.id === inputDTO.arrowId && arrow.type === 'Input');
          let inputBoxes: Box[] = [];
          arrowsOfTypeInput.forEach((arrow: Arrow) => {
            boxes.forEach((box: Box) => {
              if (arrow.destination === box.id) {
                inputBoxes.push(box);
              }
            });
          });
          resolve(inputBoxes);
        }).catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        }
      );
    });
  }
}
