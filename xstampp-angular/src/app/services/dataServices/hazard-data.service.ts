import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { RequestService, PostRequestParameters } from '../request.service';
import { HazardResponseDTO, PageRequest, SearchRequest, SystemConstraintResponseDTO, LossResponseDTO } from '../../types/local-types';
import { ConfigService } from '../config.service';
import { EXPECTED_BOOLEAN_NOT_NULL, EXPECTED_ENTITY_ARRAY_NOT_NULL, EXPECTED_ENTITY_NOT_NULL } from './../../globals';
import { HazardRequestDTO } from './../../types/local-types';


@Injectable({
  providedIn: 'root'
})
export class HazardDataService {

  constructor(private readonly requestService: RequestService, private readonly config: ConfigService) {
  }

  public createHazard(projectId: string, hazard: HazardRequestDTO): Promise<HazardResponseDTO> {
    const postParameters: PostRequestParameters<HazardResponseDTO> = {
      path: '/api/project/' + projectId + '/hazard',
      json: JSON.stringify(hazard),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<HazardResponseDTO>((resolve: (value?: HazardResponseDTO | PromiseLike<HazardResponseDTO>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<HazardResponseDTO>(postParameters)
        .then((value: HazardResponseDTO): void => {
          if (value != null) {
            resolve(value);
          } else {
            const error = new Error(EXPECTED_ENTITY_NOT_NULL);
            console.error(error);
            reject(error);
          }
        }).catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        }
        );
    });
  }

  public getHazardById(projectId: string, hazardId: string): Promise<HazardResponseDTO> {
    return new Promise<HazardResponseDTO>((resolve: (value?: HazardResponseDTO | PromiseLike<HazardResponseDTO>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performGETRequest<HazardResponseDTO>('/api/project/' + projectId + '/hazard/' + hazardId, this.config.useProjectToken())
        .then((value: HazardResponseDTO): void => {
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

  public deleteHazard(projectId: string, hazardId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performDELETERequest<boolean>('/api/project/' + projectId + '/hazard/' + hazardId, this.config.useProjectToken())
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

  public alterHazard(projectId: string, hazardId: string, hazard: HazardRequestDTO): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPUTRequest<boolean>('/api/project/' + projectId + '/hazard/' + hazardId, JSON.stringify(hazard), this.config.useProjectToken())
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

  public createHazardLossLink(projectId: string, hazardId: string, lossId: string): Promise<boolean> {
    const postParameters: PostRequestParameters<boolean> = {
      path: '/api/project/' + projectId + '/hazard/' + hazardId + '/link/loss/' + lossId,
      json: '',
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<boolean>(postParameters).then((value: boolean): void => {
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
        });
    });
  }

  public deleteHazardLossLink(projectId: string, hazardId: string, lossId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performDELETERequest<boolean>('/api/project/' + projectId + '/hazard/' + hazardId + '/link/loss/' + lossId,
        this.config.useProjectToken()).then((value: boolean): void => {
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
        });
    });
  }

  public createHazardSystemConstraintLink(projectId: string, hazardId: string, constId: string): Promise<boolean> {
    const postParameters: PostRequestParameters<boolean> = {
      path: '/api/project/' + projectId + '/hazard/' + hazardId + '/link/system-constraint/' + constId,
      json: '',
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<boolean>(postParameters).then((value: boolean): void => {
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
        });
    });
  }

  public deleteHazardSystemConstraintLink(projectId: string, hazardId: string, constId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performDELETERequest<boolean>('/api/project/' + projectId + '/hazard/' + hazardId + '/link/system-constraint/' + constId, this.config.useProjectToken()
      ).then((value: boolean): void => {
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
      });
    });
  }

  public getLossesByHazardId(projectId: string, hazardId: string, page: PageRequest): Promise<LossResponseDTO[]> {
    const postParameters: PostRequestParameters<LossResponseDTO[]> = {
      path: '/api/project/' + projectId + '/hazard/' + hazardId + '/losses',
      json: JSON.stringify(page),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<LossResponseDTO[]>((resolve: (value?: LossResponseDTO[] | PromiseLike<LossResponseDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<LossResponseDTO[]>(postParameters)
        .then((value: LossResponseDTO[]): void => {
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
        });
    });
  }

  public getSystemConstraintsByHazardId(projectId: string, hazardId: string, page: PageRequest): Promise<SystemConstraintResponseDTO[]> {
    const postParameters: PostRequestParameters<SystemConstraintResponseDTO[]> = {
      path: '/api/project/' + projectId + '/hazard/' + hazardId + '/system-constraints',
      json: JSON.stringify(page),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<SystemConstraintResponseDTO[]>((resolve: (value?: SystemConstraintResponseDTO[] |
      PromiseLike<SystemConstraintResponseDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<SystemConstraintResponseDTO[]>(postParameters)
        .then((value: SystemConstraintResponseDTO[]): void => {
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
        });
    });
  }
  public getAllHazards(projectId: string, options: SearchRequest): Promise<HazardResponseDTO[]> {
    const postParameters: PostRequestParameters<HazardResponseDTO[]> = {
      path: '/api/project/' + projectId + '/hazard/search',
      json: JSON.stringify(options),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<HazardResponseDTO[]>((resolve: (value?: HazardResponseDTO[] | PromiseLike<HazardResponseDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<HazardResponseDTO[]>(postParameters)
        .then((value: HazardResponseDTO[]): void => {
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

  public getHazardsByUCAId(projectId: string, controlActionId: string, ucaId: string, page: PageRequest): Promise<HazardResponseDTO[]> {
    const postParameters: PostRequestParameters<HazardResponseDTO[]> = {
      path: '/api/project/' + projectId + '/ControlAction/' + controlActionId + '/UCA/' + ucaId + '/link/hazard',
      json: JSON.stringify(page),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<HazardResponseDTO[]>((resolve: (value?: HazardResponseDTO[] | PromiseLike<HazardResponseDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<HazardResponseDTO[]>(postParameters)
        .then((value: HazardResponseDTO[]): void => {
          if (value != null) {
            resolve(value);
          } else {
            // null is a valid response
            resolve(null);
          }
        }).catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        }
        );
    });
  }

}
