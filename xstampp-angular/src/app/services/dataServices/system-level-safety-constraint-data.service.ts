import { Injectable } from '@angular/core';
import { RequestService, PostRequestParameters } from '../request.service';
import {
  HazardResponseDTO,
  PageRequest,
  SearchRequest,
  SystemConstraintRequestDTO,
  SystemConstraintResponseDTO,
  LossResponseDTO,
} from '../../types/local-types';
import { ConfigService } from '../config.service';
import { EXPECTED_BOOLEAN_NOT_NULL, EXPECTED_ENTITY_NOT_NULL, EXPECTED_ENTITY_ARRAY_NOT_NULL } from './../../globals';
import { HttpErrorResponse } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class SystemLevelSafetyConstraintDataService {

  constructor(private readonly requestService: RequestService, private readonly config: ConfigService) {
  }
  // TODO: RETURN TYPING
  public createSafetyConstraint(projectId: string, systemConstraintId: SystemConstraintRequestDTO): Promise<boolean> {
    const postParameters: PostRequestParameters<boolean> = {
      path: '/api/project/' + projectId + '/system-constraint',
      json: JSON.stringify(systemConstraintId),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
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

  public getSafetyConstraintById(projectId: string, systemConstraintId: string): Promise<SystemConstraintResponseDTO> {
    return new Promise<LossResponseDTO>((resolve: (value?: LossResponseDTO | PromiseLike<LossResponseDTO>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performGETRequest<SystemConstraintResponseDTO>('/api/project/' + projectId + '/system-constraint/' + systemConstraintId, this.config.useProjectToken())
        .then((value: SystemConstraintResponseDTO): void => {
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

  public alterSafetyConstraint(projectId: string, systemConstraintId: string, safetyConstraint: SystemConstraintRequestDTO): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPUTRequest<boolean>('/api/project/' + projectId + '/system-constraint/' + systemConstraintId,
        JSON.stringify(safetyConstraint), this.config.useProjectToken())
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

  public deleteSafetyConstraint(projectId: string, systemConstraintId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performDELETERequest<boolean>('/api/project/' + projectId + '/system-constraint/' + systemConstraintId, this.config.useProjectToken())
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

  public getHazardsBySafetyConstraintId(projectId: string, systemConstraintId: string, page: PageRequest): Promise<HazardResponseDTO[]> {
    const postParameters: PostRequestParameters<SystemConstraintResponseDTO[]> = {
      path: '/api/project/' + projectId + '/system-constraint/' + systemConstraintId + '/hazards',
      json: JSON.stringify(page),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<HazardResponseDTO[]>((resolve: (value?: HazardResponseDTO[] | PromiseLike<HazardResponseDTO[]>) => void, reject: (reason?: any) => void): void => {
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

  public getAllSafetyConstraints(projectId: string, options: SearchRequest): Promise<SystemConstraintResponseDTO[]> {
    const postParameters: PostRequestParameters<SystemConstraintResponseDTO[]> = {
      path: '/api/project/' + projectId + '/system-constraint/search',
      json: JSON.stringify(options),
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
        }
        );
    });
  }

  public getLinkedSystemConstraintByResponsibility(projectId: string, responsibilityId: string): Promise<SystemConstraintResponseDTO[]> {
    const postParameters: PostRequestParameters<SystemConstraintResponseDTO[]> = {
      path: '/api/project/' + projectId + '/responsibility/' + responsibilityId + '/systemconstraints',
      json: JSON.stringify({}),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<SystemConstraintResponseDTO[]>((resolve, reject) => {
      this.requestService.performPOSTRequest(postParameters)
      .then(value => {
        if (value != null) {
          resolve(value as SystemConstraintResponseDTO[]);
        } else {
          // null is a valid response
          resolve(null);
        }
      }).catch(reason => {
        console.error(reason);
        reject(reason.error);
      });
    });
  }

  public createSystemConstraintHazardLink(projectId: string, systemConstraintId: string, hazardId: string): Promise<boolean> {
    const postParameters: PostRequestParameters<boolean> = {
      path: '/api/project/' + projectId + '/system-constraint/' + systemConstraintId + '/link/hazard/' + hazardId,
      json: '',
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<boolean>(postParameters)
        .then((value: boolean): void => {
          if (value != null) {
            const res = value as boolean;
            resolve(res);
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

  public deleteSystemConstraintHazardLink(projectId: string, systemConstraintId: string, hazardId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performDELETERequest<boolean>('/api/project/' + projectId + '/system-constraint/' +
        systemConstraintId + '/link/hazard/' + hazardId, this.config.useProjectToken())
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

}
