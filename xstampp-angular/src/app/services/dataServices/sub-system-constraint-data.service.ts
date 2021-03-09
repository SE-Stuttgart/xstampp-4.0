import { Injectable } from '@angular/core';
import { RequestService, PostRequestParameters } from '../request.service';
import { SearchRequest, SubSystemConstraintResponseDTO } from '../../types/local-types';
import { ConfigService } from '../config.service';
import { EXPECTED_BOOLEAN_NOT_NULL, EXPECTED_ENTITY_NOT_NULL, EXPECTED_ENTITY_ARRAY_NOT_NULL } from './../../globals';
import { HttpErrorResponse } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class SubSystemConstraintDataService {

  constructor(private readonly requestService: RequestService, private readonly config: ConfigService) {
  }

  public createSubSystemConstraint(projectId: string, systemConstraintId: string, subSystemConstraint: any): Promise<SubSystemConstraintResponseDTO> {
    const postParameters: PostRequestParameters<SubSystemConstraintResponseDTO> = {
      path: '/api/project/' + projectId + '/system-constraint/' + systemConstraintId + '/sub-system-constraint',
      json: JSON.stringify(subSystemConstraint),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<SubSystemConstraintResponseDTO>((resolve: (value?: SubSystemConstraintResponseDTO |
      PromiseLike<SubSystemConstraintResponseDTO>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<SubSystemConstraintResponseDTO>(postParameters)
        .then((value: SubSystemConstraintResponseDTO): void => {
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
        });
    });
  }

  public getSubSystemConstraintById(projectId: string, systemConstraintId: string, subSystemConstraintId: string): Promise<SubSystemConstraintResponseDTO> {
    return new Promise<SubSystemConstraintResponseDTO>((resolve: (value?: SubSystemConstraintResponseDTO |
      PromiseLike<SubSystemConstraintResponseDTO>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performGETRequest<SubSystemConstraintResponseDTO>('/api/project/' + projectId + '/system-constraint/' +
        systemConstraintId + '/sub-system-constraint/' + subSystemConstraintId, this.config.useProjectToken()).then((value: SubSystemConstraintResponseDTO): void => {
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
        });
    });
  }

  public alterSubSystemConstraint(projectId: string, systemConstraintId: string, subSystemConstraintId: string, subSystemConstraint: any): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPUTRequest<boolean>('/api/project/' + projectId + '/system-constraint/' +
        systemConstraintId + '/sub-system-constraint/' + subSystemConstraintId, JSON.stringify(subSystemConstraint), this.config.useProjectToken()).then((value: boolean): void => {
          if (value != null) {
            resolve(true);
          } else {
            const error = new Error(EXPECTED_BOOLEAN_NOT_NULL);
            console.error(error);
            reject(error);
          }
        }).catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        });
    });
  }

  public deleteSubSystemConstraint(projectId: string, systemConstraintId: string, subSystemConstraintId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performDELETERequest<boolean>('/api/project/' + projectId + '/system-constraint/' +
        systemConstraintId + '/sub-system-constraint/' + subSystemConstraintId, this.config.useProjectToken()).then((value: boolean): void => {
          if (value != null) {
            resolve(true);
          } else {
            const error = new Error(EXPECTED_BOOLEAN_NOT_NULL);
            console.error(error);
            reject(error);
          }
        }).catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        });
    });
  }

  public getAllSubSystemConstraints(projectId: string, systemConstraintId: string, options: SearchRequest): Promise<SubSystemConstraintResponseDTO[]> {
    const postParameters: PostRequestParameters<SubSystemConstraintResponseDTO[]> = {
      path: '/api/project/' + projectId + '/system-constraint/' + systemConstraintId + '/sub-system-constraint/search',
      json: JSON.stringify(options),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<SubSystemConstraintResponseDTO[]>((resolve: (value?: SubSystemConstraintResponseDTO[] |
      PromiseLike<SubSystemConstraintResponseDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<SubSystemConstraintResponseDTO[]>(postParameters)
        .then((value: SubSystemConstraintResponseDTO[]): void => {
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

  public createSubHazardSubSystemConstraintLink(projectId: string, systemConstraintId: string, subSystemConstraintId: string,
      hazardId: string, subHazardId: string): Promise<boolean> {
    const postParameters: PostRequestParameters<boolean> = {
      path: '/api/project/' + projectId + '/hazard/' + hazardId + '/sub-hazard/' + subHazardId +
        '/link/system-constraint/' + systemConstraintId + '/sub-system-constraint/' + subSystemConstraintId,
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

  public deleteSubSystemConstraintSubHazardLink(projectId: string, systemConstraintId: string, subSystemConstraintId: string,
    hazardId: string, subHazardId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performDELETERequest<boolean>('/api/project/' + projectId + '/hazard/' + hazardId + '/sub-hazard/' +
        subHazardId + '/link/system-constraint/' + systemConstraintId + '/sub-system-constraint/' + subSystemConstraintId,
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
