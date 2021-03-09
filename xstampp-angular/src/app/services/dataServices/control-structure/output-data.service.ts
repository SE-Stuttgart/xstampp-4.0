import {Injectable} from '@angular/core';
import {
  OutputDTO, SearchRequest, ArrowResponseDTO, ControlActionDTO, ControlStructureDTO, Arrow, InputDTO, Box
} from '../../../types/local-types';
import { RequestService, PostRequestParameters } from '../../request.service';
import { ConfigService } from '../../config.service';
import { EXPECTED_BOOLEAN_NOT_NULL, EXPECTED_ENTITY_NOT_NULL, EXPECTED_ENTITY_ARRAY_NOT_NULL } from './../../../globals';
import { HttpErrorResponse } from '@angular/common/http';
import { ControlStructureDataService } from '../control-structure-data.service';
import {MessageService} from "primeng/api";


@Injectable({
  providedIn: 'root'
})
export class OutputDataService {

  constructor(private readonly requestService: RequestService,
              private readonly config: ConfigService,
              private readonly controlStructureDataService: ControlStructureDataService,
              private readonly messageService: MessageService) {
  }

  createOutput(projectId: string, output: OutputDTO): Promise<OutputDTO> {
    const postParameters: PostRequestParameters<OutputDTO> = {
      path: '/api/project/' + projectId + '/output/',
      json: JSON.stringify(output),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<OutputDTO>((resolve: (value?: OutputDTO | PromiseLike<OutputDTO>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<OutputDTO>(postParameters)
        .then((value: OutputDTO): void => {
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

  alterOutput(projectId: string, outputId: string, output: OutputDTO): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPUTRequest<boolean>('/api/project/' + projectId + '/output/' + outputId, JSON.stringify(output), this.config.useProjectToken())
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

  deleteOutput(projectId: string, outputId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performDELETERequest<boolean>('/api/project/' + projectId + '/output/' + outputId, this.config.useProjectToken())
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

  getOutputById(projectId: string, outputId: string): Promise<OutputDTO> {
    return new Promise<OutputDTO>((resolve: (value?: OutputDTO | PromiseLike<OutputDTO>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performGETRequest<OutputDTO>('/api/project/' + projectId + '/output/' + outputId, this.config.useProjectToken())
        .then((value: OutputDTO): void => {
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

  getAllOutputs(projectId: string, options: SearchRequest): Promise<OutputDTO[]> {
    const postParameters: PostRequestParameters<OutputDTO[]> = {
      path: '/api/project/' + projectId + '/output/search',
      json: JSON.stringify(options),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<OutputDTO[]>((resolve: (value?: OutputDTO[] | PromiseLike<OutputDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<OutputDTO[]>(postParameters)
        .then((value: OutputDTO[]): void => {
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

  getAllOutputsForTable(projectId: string, options: SearchRequest): Promise<ArrowResponseDTO[]> {
    const postParameters: PostRequestParameters<ArrowResponseDTO[]> = {
      path: '/api/project/' + projectId + '/output/search',
      json: JSON.stringify(options),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<ArrowResponseDTO[]>((resolve: (value?: ArrowResponseDTO[] | PromiseLike<ArrowResponseDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest(postParameters)
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

  getOutputsByArrowId(projectId: string, arrowId: string): Promise<OutputDTO[]> {
    return new Promise<OutputDTO[]>((resolve: (value?: OutputDTO[] | PromiseLike<OutputDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performGETRequest<OutputDTO[]>('/api/project/' + projectId + '/output/arrow/' + arrowId, this.config.useProjectToken())
        .then((value: OutputDTO[]): void => {
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

  setArrowId(projectId: string, outputId: string, arrowId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPUTRequest<boolean>('/api/project/' + projectId + '/output/' + outputId + '/arrow/' + arrowId, null, this.config.useProjectToken())
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

  getOutputsByBoxId(projectId: string, boxId: string): Promise<OutputDTO[]> {
    return new Promise<OutputDTO[]>((resolve: (value?: OutputDTO[] | PromiseLike<OutputDTO[]>) => void, reject: (reason?: any) => void): void  => {
      this.controlStructureDataService.loadRootControlStructure(projectId)
        .then((value: ControlStructureDTO): void => {
          const arrows: Arrow[] = value.arrows;
          const arrowsOfTypeOutput = arrows.filter((arrow: Arrow) => arrow.source === boxId && arrow.type === 'Output');

          // TODO can a box has only one arrow or multiple ?
          if (arrowsOfTypeOutput.length > 0) {
            this.getOutputsByArrowId(projectId, arrowsOfTypeOutput[0].id)
              .then((outputs: OutputDTO[]) => {
                resolve(outputs);
                return outputs;
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

  getOutputsByBoxSourceId(projectId: string, boxId: string): Promise<OutputDTO[]> {
    return new Promise<OutputDTO[]>((resolve: (value?: OutputDTO[] | PromiseLike<OutputDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.controlStructureDataService.loadRootControlStructure(projectId)
        .then((value: ControlStructureDTO): void => {
          const arrows: Arrow[] = value.arrows;
          const arrowsOfTypeOutput = arrows.filter((arrow: Arrow) => arrow.source === boxId && arrow.type === 'Output');

          // TODO can a box has only one arrow or multiple ?
          if (arrowsOfTypeOutput.length > 0) {
            this.getOutputsByArrowId(projectId, arrowsOfTypeOutput[0].id)
              .then((outputs: OutputDTO[]) => {
                resolve(outputs);
                return outputs;
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

  getOutputsSource(projectId: string, outputDTO: OutputDTO): Promise<Box[]> {
    return new Promise<Box[]>((resolve: (value?: Box[] | PromiseLike<Box[]>) => void, reject: (reason?: any) => void): void  => {
      this.controlStructureDataService.loadRootControlStructure(projectId)
        .then((value: ControlStructureDTO): void => {
          const arrows: Arrow[] = value.arrows;
          const boxes: Box[] = value.boxes;
          const arrowsOfTypeOutput = arrows.filter((arrow: Arrow) => arrow.id === outputDTO.arrowId && arrow.type === 'Output');
          let outputBoxes: Box[] = [];

          arrowsOfTypeOutput.forEach((arrow: Arrow) => {
            boxes.forEach((box: Box) => {
              if (arrow.source === box.id) {
                outputBoxes.push(box);
              }
            });
          });
          resolve(outputBoxes);
        }).catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        }
      );
    });
  }
}
