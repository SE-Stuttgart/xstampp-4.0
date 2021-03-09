import { EXPECTED_BOOLEAN_NOT_NULL, EXPECTED_ENTITY_NOT_NULL, EXPECTED_ENTITY_ARRAY_NOT_NULL } from './../../globals';
import { Injectable } from '@angular/core';
import { RequestService, PostRequestParameters } from '../request.service';
import { HazardResponseDTO, PageRequest, SearchRequest, LossRequestDTO, LossResponseDTO } from '../../types/local-types';
import { ConfigService } from '../config.service';
import { HttpErrorResponse } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class LossDataService {

  constructor(private readonly requestService: RequestService, private readonly config: ConfigService) {
  }

  public createLoss(projectId: string, loss: LossRequestDTO): Promise<boolean> {
    const postParameters: PostRequestParameters<boolean> = {
      path: '/api/project/' + projectId + '/loss',
      json: JSON.stringify(loss),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void  => {
      this.requestService.performPOSTRequest<boolean>(postParameters)
        .then((value: boolean): void => {
          if (value != null) {
            resolve();
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
  // Currently never used
  public getLossById(projectId: string, lossId: string): Promise<LossResponseDTO> {
    return new Promise<LossResponseDTO>((resolve: (value?: LossResponseDTO | PromiseLike<LossResponseDTO>) => void, reject: (reason?: any) => void): void  => {
      this.requestService.performGETRequest<LossResponseDTO>('/api/project/' + projectId + '/loss/' + lossId, this.config.useProjectToken())
        .then((value: LossResponseDTO): void => {
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

  public alterLoss(projectId: string, lossId: string, loss: LossRequestDTO): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void  => {
      this.requestService.performPUTRequest<boolean>('/api/project/' + projectId + '/loss/' + lossId, JSON.stringify(loss), this.config.useProjectToken())
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

  public deleteLoss(projectId: string, lossId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void  => {
      this.requestService.performDELETERequest<boolean>('/api/project/' + projectId + '/loss/' + lossId, this.config.useProjectToken())
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

  public getHazardsByLossId(projectId: string, lossId: string, page: PageRequest): Promise<HazardResponseDTO[]> {
    const postParameters: PostRequestParameters<LossResponseDTO[]> = {
      path: '/api/project/' + projectId + '/loss/' + lossId + '/hazards',
      json: JSON.stringify(page),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<HazardResponseDTO[]>((resolve: (value?: HazardResponseDTO[] | PromiseLike<HazardResponseDTO[]>) => void, reject: (reason?: any) => void): void  => {
      this.requestService.performPOSTRequest<HazardResponseDTO[]>(postParameters)
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

  public getAllLosses(projectId: string, options: SearchRequest): Promise<LossResponseDTO[]> {
    const postParameters: PostRequestParameters<LossResponseDTO[]> = {
      path: '/api/project/' + projectId + '/loss/search',
      json: JSON.stringify(options),
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
        }
        );
    });
  }

  public createLossHazardLink(projectId: string, lossId: string, hazardId: string): Promise<boolean> {
    const postParameters: PostRequestParameters<boolean> = {
      path: '/api/project/' + projectId + '/loss/' + lossId + '/link/hazard/' + hazardId,
      json: '',
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void  => {
      this.requestService.performPOSTRequest<boolean>(postParameters)
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
        });
    });
  }

  public deleteLossHazardLink(projectId: string, lossId: string, hazardId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performDELETERequest<boolean>('/api/project/' + projectId + '/loss/' + lossId + '/link/hazards/' + hazardId,
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

}
