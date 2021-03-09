import { Injectable } from '@angular/core';
import { RequestService, PostRequestParameters } from '../request.service';
import { SearchRequest, SubHazardResponseDTO, SubSystemConstraintResponseDTO, PageRequest } from '../../types/local-types';
import { ConfigService } from '../config.service';
import { EXPECTED_BOOLEAN_NOT_NULL, EXPECTED_ENTITY_NOT_NULL, EXPECTED_ENTITY_ARRAY_NOT_NULL } from './../../globals';
import { HttpErrorResponse } from '@angular/common/http';
import { MessageService } from 'primeng/api';

@Injectable({
  providedIn: 'root'
})
export class SubHazardDataService {

  constructor(private readonly requestService: RequestService, private readonly config: ConfigService, private readonly messageService: MessageService) {
  }

  public createSubHazard(projectId: string, hazardId: string, subHazard: any): Promise<SubHazardResponseDTO> {
    const postParameters: PostRequestParameters<SubHazardResponseDTO> = {
      path: '/api/project/' + projectId + '/hazard/' + hazardId + '/sub-hazard',
      json: JSON.stringify(subHazard),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<SubHazardResponseDTO>((resolve: (value?: SubHazardResponseDTO | PromiseLike<SubHazardResponseDTO>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<SubHazardResponseDTO>(postParameters)
        .then((value: SubHazardResponseDTO): void => {
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

  public getSubHazardById(projectId: string, hazardId: string, subHazardId: string): Promise<SubHazardResponseDTO> {
    return new Promise<SubHazardResponseDTO>((resolve: (value?: SubHazardResponseDTO | PromiseLike<SubHazardResponseDTO>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performGETRequest<SubHazardResponseDTO>('/api/project/' + projectId + '/hazard/' + hazardId + '/sub-hazard/' + subHazardId, this.config.useProjectToken())
        .then((value: SubHazardResponseDTO): void => {
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

  public alterSubHazard(projectId: string, hazardId: string, subHazardId: string, subHazard: any): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPUTRequest<boolean>('/api/project/' + projectId + '/hazard/' + hazardId + '/sub-hazard/' + subHazardId, JSON.stringify(subHazard),
        this.config.useProjectToken())
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

  public deleteSubHazard(projectId: string, hazardId: string, subHazardId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performDELETERequest<boolean>('/api/project/' + projectId + '/hazard/' + hazardId + '/sub-hazard/' + subHazardId, this.config.useProjectToken())
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
  public getAllSubHazards(projectId: string, hazardId: string, options: SearchRequest): Promise<SubHazardResponseDTO[]> {
    const postParameters: PostRequestParameters<SubHazardResponseDTO[]> = {
      path: '/api/project/' + projectId + '/hazard/' + hazardId + '/sub-hazard/search',
      json: JSON.stringify(options),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<SubHazardResponseDTO[]>((resolve: (value?: SubHazardResponseDTO[] | PromiseLike<SubHazardResponseDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<SubHazardResponseDTO[]>(postParameters)
        .then((value: SubHazardResponseDTO[]): void => {
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

  public getSubConstraintBySubHazardId(projectId: string, hazardId: string, subHazardId: string): Promise<SubSystemConstraintResponseDTO> {
    return new Promise<SubSystemConstraintResponseDTO>((resolve: (value?: SubSystemConstraintResponseDTO |
      PromiseLike<SubSystemConstraintResponseDTO>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performGETRequest<SubSystemConstraintResponseDTO>('/api/project/' + projectId + '/system-constraint/hazard/' +
        hazardId + '/sub-hazard/' + subHazardId, this.config.useProjectToken())
        .then((value: SubSystemConstraintResponseDTO): void => {
          if (value != null) {
            resolve(value);
          } else {
            // Empty response is allowed here
            resolve(null);
          }
        }).catch((reason: HttpErrorResponse) => {
          reject(reason.error);
        }
        );
    });
  }

  public getHazardsByUCAId(projectId: string, controlActionId: string, ucaId: string, options: PageRequest): Promise<SubHazardResponseDTO[]> {
    const postParameters: PostRequestParameters<SubHazardResponseDTO[]> = {
      path: '/api/project/' + projectId + '/ControlAction/' + controlActionId + '/UCA/' + ucaId + '/link/subHazard',
      json: JSON.stringify(options),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<SubHazardResponseDTO[]>((resolve: (value?: SubHazardResponseDTO[] | PromiseLike<SubHazardResponseDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<SubHazardResponseDTO[]>(postParameters)
        .then((value: SubHazardResponseDTO[]): void => {
          if (value != null) {
            resolve(value);
          } else {
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
