import { RequestService, PostRequestParameters } from '../request.service';
import { ResponsibilityRequestDTO, ResponsibilityResponseDTO, SearchRequest, ResponsibilityFilterRequestDTO, ResponsibilityFilterPreviewResponseDTO } from '../../types/local-types';
import { Injectable } from '@angular/core';
import { ConfigService } from '../config.service';
import { EXPECTED_BOOLEAN_NOT_NULL, EXPECTED_ENTITY_NOT_NULL, EXPECTED_ENTITY_ARRAY_NOT_NULL } from './../../globals';
import { HttpErrorResponse } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ResponsibilityDataService {

  constructor(private readonly requestService: RequestService, private readonly config: ConfigService) {
  }

  createResponsibility(projectId: string, responsibility: ResponsibilityRequestDTO): Promise<ResponsibilityResponseDTO> {
    const postParameters: PostRequestParameters<ResponsibilityResponseDTO> = {
      path: '/api/project/' + projectId + '/responsibility',
      json: JSON.stringify(responsibility),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<ResponsibilityResponseDTO>((resolve: (value?: ResponsibilityResponseDTO |
      PromiseLike<ResponsibilityResponseDTO>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<ResponsibilityResponseDTO>(postParameters)
        .then((value: ResponsibilityResponseDTO): void => {
          if (value != null) {
            resolve(value);
          } else {
            const error: Error = new Error(EXPECTED_ENTITY_NOT_NULL);
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

  alterResponsibility(projectId: string, responsibilityId: string, responsibility: ResponsibilityRequestDTO): Promise<ResponsibilityResponseDTO> {
    return new Promise<ResponsibilityResponseDTO>((resolve: (value?: ResponsibilityResponseDTO |
      PromiseLike<ResponsibilityResponseDTO>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPUTRequest<ResponsibilityResponseDTO>('/api/project/' + projectId + '/responsibility/' + responsibilityId,
        JSON.stringify(responsibility), this.config.useProjectToken())
        .then((value: ResponsibilityResponseDTO): void => {
          if (value != null) {
            resolve();
          } else {
            const error: Error = new Error(EXPECTED_ENTITY_NOT_NULL);
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

  deleteResponsibility(projectId: string, responsibilityId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performDELETERequest<ResponsibilityResponseDTO>('/api/project/' + projectId + '/responsibility/' + responsibilityId, this.config.useProjectToken())
        .then((value: ResponsibilityResponseDTO): void => {
          if (value != null) {
            resolve();
          } else {
            const error: Error = new Error(EXPECTED_BOOLEAN_NOT_NULL);
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

  getResponsibilityById(projectId: string, responsibilityId: string): Promise<ResponsibilityResponseDTO> {
    return new Promise<ResponsibilityResponseDTO>((resolve: (value?: ResponsibilityResponseDTO |
      PromiseLike<ResponsibilityResponseDTO>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performGETRequest<ResponsibilityResponseDTO>('/api/project/' + projectId + '/responsibility/' + responsibilityId, this.config.useProjectToken())
        .then((value: ResponsibilityResponseDTO): void => {
          if (value != null) {
            resolve(value);
          } else {
            const error: Error = new Error(EXPECTED_ENTITY_NOT_NULL);
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

  public createResponsibilitySCLink(projectId: string, responsibilityId: string, systemConstraintId: string): Promise<boolean> {
    const postParameters: PostRequestParameters<Boolean> = {
      path: '/api/project/' + projectId + '/responsibility/' + responsibilityId + '/system-constraint/' + systemConstraintId,
      json: '',
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<boolean>((resolve: (value?: boolean | Promise<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest(postParameters).then((value: Boolean) => {
        if (value != null) {
          resolve();
        } else {
          const error: Error = new Error(EXPECTED_BOOLEAN_NOT_NULL);
          console.log(error);
          reject(error);
        }
      }).catch((reason: HttpErrorResponse) => {
        console.error(reason);
        reject(reason.error);
      });
    });
  }

  public deleteResponsibilitySCLink(projectId: string, responsibilityId: string, systemConstraintId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | Promise<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performDELETERequest('/api/project/' + projectId + '/responsibility/' + responsibilityId + '/system-constraint/'
          + systemConstraintId, this.config.useProjectToken()).then((value: Boolean) => {
        if (value != null) {
          resolve();
        } else {
          const error: Error = new Error(EXPECTED_BOOLEAN_NOT_NULL);
          console.log(error);
          reject(error);
        }
      }).catch((reason: HttpErrorResponse) => {
        console.error(reason);
        reject(reason.error);
      });
    });
  }

  getAllResponsibilities(projectId: string, options: SearchRequest): Promise<ResponsibilityResponseDTO[]> {
    const postParameters: PostRequestParameters<ResponsibilityResponseDTO[]> = {
      path: '/api/project/' + projectId + '/responsibility/search',
      json: JSON.stringify(options),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<ResponsibilityResponseDTO[]>((resolve: (value?: ResponsibilityResponseDTO[] |
      PromiseLike<ResponsibilityResponseDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<ResponsibilityResponseDTO[]>(postParameters)
        .then((value: ResponsibilityResponseDTO[]): void => {
          if (value != null) {

            const receivedResponsibilities: ResponsibilityResponseDTO[] = value as ResponsibilityResponseDTO[];

            resolve(receivedResponsibilities);
          } else {
            const error: Error = new Error(EXPECTED_ENTITY_ARRAY_NOT_NULL);
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

  getResponsibilitiesByControllerId(projectId: string, controllerId: string): Promise<ResponsibilityResponseDTO[]> {
    return new Promise<ResponsibilityResponseDTO[]>((resolve: (value?: ResponsibilityResponseDTO[] |
      PromiseLike<ResponsibilityResponseDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performGETRequest<ResponsibilityResponseDTO[]>('/api/project/' + projectId + '/responsibility/controller/' + controllerId, this.config.useProjectToken())
        .then((value: ResponsibilityResponseDTO[]): void => {
          if (value != null) {
            resolve(value);
          } else {
            const error: Error = new Error(EXPECTED_ENTITY_ARRAY_NOT_NULL);
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

  getResponsibilitiesByControllerAndSystemConstraintID(projectId: string, options: ResponsibilityFilterRequestDTO): Promise<ResponsibilityResponseDTO[]> {
    const postParameters: PostRequestParameters<ResponsibilityResponseDTO[]> = {
      path: '/api/project/' + projectId + '/responsibility/system-constraint-and-controller',
      json: JSON.stringify(options),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<ResponsibilityResponseDTO[]>((resolve: (value?: ResponsibilityResponseDTO[] | Promise<ResponsibilityResponseDTO[]>) => void,
        reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest(postParameters)
      .then((value: ResponsibilityResponseDTO[]) => {
        if (value != null) {
          const receivedResponsibility: ResponsibilityResponseDTO[] = value as ResponsibilityResponseDTO[];
          resolve(receivedResponsibility);
        } else {
          const error: Error = new Error(EXPECTED_ENTITY_ARRAY_NOT_NULL);
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

  getResponsibilityFilterPreview(projectId: string, options: ResponsibilityFilterRequestDTO): Promise<ResponsibilityFilterPreviewResponseDTO> {
    const postParameters: PostRequestParameters<ResponsibilityFilterPreviewResponseDTO> = {
      path: '/api/project/' + projectId + '/responsibility/filter-preview/complete',
      json: JSON.stringify(options),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<ResponsibilityFilterPreviewResponseDTO>((resolve: (value?: ResponsibilityFilterPreviewResponseDTO | Promise<ResponsibilityFilterPreviewResponseDTO>) => void,
        reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest(postParameters)
      .then((value: ResponsibilityFilterPreviewResponseDTO) => {
        if (value != null) {
          const receivedResponsibility: ResponsibilityFilterPreviewResponseDTO = value as ResponsibilityFilterPreviewResponseDTO;
          resolve(receivedResponsibility);
        } else {
          const error: Error = new Error(EXPECTED_ENTITY_NOT_NULL);
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
