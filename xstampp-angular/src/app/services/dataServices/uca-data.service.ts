import { UcaResponseDTO, UcaRequestDTO, SearchRequest, ControllerConstraintResponseDTO } from './../../types/local-types';
import { ConfigService } from './../config.service';
import { Injectable } from '@angular/core';
import { RequestService, PostRequestParameters } from '../request.service';
import { EXPECTED_BOOLEAN_NOT_NULL, EXPECTED_ENTITY_NOT_NULL, EXPECTED_ENTITY_ARRAY_NOT_NULL } from './../../globals';
import { HttpErrorResponse } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class UcaDataService {

  constructor(private readonly requestService: RequestService, private readonly config: ConfigService) { }
  // TODO set return type
  public createUCA(projectId: string, controlActionId: string, uca: any): Promise<boolean> {
    const postParameters: PostRequestParameters<boolean> = {
      path: '/api/project/' + projectId + '/ControlAction/' + controlActionId + '/UCA',
      json: JSON.stringify(uca),
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

  public deleteUca(projectId: string, controlActionId: string, ucaId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performDELETERequest<boolean>('/api/project/' + projectId + '/ControlAction/' + controlActionId + '/UCA/' + ucaId, this.config.useProjectToken())
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

  public getUnsafeControlActionByUcaId(projectId: string, controlActionId: string, ucaId: string): Promise<UcaResponseDTO[]> {
    return new Promise<UcaResponseDTO[]>((resolve: (value?: UcaResponseDTO[] | PromiseLike<UcaResponseDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performGETRequest<UcaResponseDTO[]>('/api/project/' + projectId + '/ControlAction/' + controlActionId + '/UCA/' + ucaId, this.config.useProjectToken())
        .then((value: UcaResponseDTO[]): void => {
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

  public getAllUcasByControlActionId(projectId: string, controlActionId: string, options: SearchRequest): Promise<UcaResponseDTO[]> {
    const postParameters: PostRequestParameters<UcaResponseDTO[]> = {
      path: '/api/project/' + projectId + '/ControlAction/' + controlActionId + '/UCAs',
      json: JSON.stringify(options),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<UcaResponseDTO[]>((resolve: (value?: UcaResponseDTO[] | PromiseLike<UcaResponseDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<UcaResponseDTO[]>(postParameters)
        .then((value: UcaResponseDTO[]): void => {
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

  public getUcasByControlActionIdAndUcaName(projectId: string, controlActionId: string, options: SearchRequest, ucaName: string): Promise<UcaResponseDTO[]> {
    const postParameters: PostRequestParameters<UcaResponseDTO[]> = {
      path: '/api/project/' + projectId + '/ControlAction/' + controlActionId + '/UCAs',
      json: JSON.stringify(options),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<UcaResponseDTO[]>((resolve: (value?: UcaResponseDTO[] | PromiseLike<UcaResponseDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<UcaResponseDTO[]>(postParameters)
        .then((value: UcaResponseDTO[]): void => {
          if (value != null) {
            let ucas = value.filter( uca => uca.name === ucaName);
            resolve(ucas);
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

  public getUnsafeControlActionsByControlActionIdAndType(projectId: string, controlActionId: string, options: SearchRequest): Promise<UcaResponseDTO[]> {
    const postParameters: PostRequestParameters<UcaResponseDTO[]> = {
      path: '/api/project/' + projectId + '/ControlAction/' + controlActionId + '/type/UCAs',
      json: JSON.stringify(options),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<UcaResponseDTO[]>((resolve: (value?: UcaResponseDTO[] | PromiseLike<UcaResponseDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<UcaResponseDTO[]>(postParameters)
        .then((value: UcaResponseDTO[]): void => {
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

  public getUnsafeControlActionsCountByControlActionIdAndType(projectId: string, controlActionId: string, options: SearchRequest): Promise<number> {
    const postParameters: PostRequestParameters<number> = {
      path: '/api/project/' + projectId + '/ControlAction/' + controlActionId + '/UCAs/count',
      json: JSON.stringify(options),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<number>((resolve: (value?: number | PromiseLike<number>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<number>(postParameters)
        .then((value: number): void => {
          if (value != null) {
            resolve(value);
          } else {
            // Expected Number not entity but just count number as entitiy
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

  public alterUca(projectId: string, controlActionId: string, ucaId: string, uca: UcaRequestDTO): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPUTRequest<boolean>('/api/project/' + projectId + '/ControlAction/' + controlActionId + '/UCA/' + ucaId,
        JSON.stringify(uca), this.config.useProjectToken())
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

  public createUCAHazLink(projectId: string, controlActionId: string, ucaId: string, hazardId: string, options: SearchRequest): Promise<boolean> {
    const postParameters: PostRequestParameters<boolean> = {
      path: '/api/project/' + projectId + '/ControlAction/' + controlActionId + '/UCA/' + ucaId + '/link/hazard/' + hazardId,
      json: JSON.stringify(options),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<boolean>(postParameters)
        .then((value: boolean): void => {
          if (value != null) {
            resolve(true);
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

  public deleteUCAHazLink(projectId: string, controlActionId: string, ucaId: string, hazardId: string, options: SearchRequest): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performDELETERequest<boolean>('/api/project/' + projectId + '/ControlAction/' + controlActionId + '/UCA/' + ucaId + '/link/hazard/' + hazardId,
        this.config.useProjectToken())
        .then((value: boolean): void => {
          if (value != null) {
            resolve(true);
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

  public createUCASubHazLink(projectId: string, controlActionId: string, ucaId: string, hazardId: string, subHazId: string, options: SearchRequest): Promise<boolean> {
    const postParameters: PostRequestParameters<boolean> = {
      path: '/api/project/' + projectId + '/ControlAction/' + controlActionId + '/UCA/' + ucaId + '/link/hazard/' + hazardId + '/subHazard/' + subHazId,
      json: JSON.stringify(options),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<boolean>(postParameters)
        .then((value: boolean): void => {
          if (value != null) {
            resolve(true);
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

  public deleteUCASubHazLink(projectId: string, controlActionId: string, ucaId: string, hazardId: string, subHazId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performDELETERequest<boolean>('/api/project/' + projectId + '/ControlAction/' + controlActionId + '/UCA/' + ucaId + '/link/hazard/' + hazardId +
        '/subHazard/' + subHazId, this.config.useProjectToken())
        .then((value: boolean): void => {
          if (value != null) {
            resolve(true);
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

  public getUcaByHazard(projectId: string, hazardId: string, options: SearchRequest): Promise<UcaResponseDTO[]> {
    const postParameters: PostRequestParameters<UcaResponseDTO[]> = {
      path: '/api/project/' + projectId + '/hazard/' + hazardId + '/link/UCA',
      json: JSON.stringify(options),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<UcaResponseDTO[]>((resolve: (value?: UcaResponseDTO[] | PromiseLike<UcaResponseDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<UcaResponseDTO[]>(postParameters)
        .then((value: UcaResponseDTO[]): void => {
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

  public getUcaBySubHazard(projectId: string, hazardId: string, subHazardId: string, options: SearchRequest): Promise<UcaResponseDTO[]> {
    const postParameters: PostRequestParameters<UcaResponseDTO[]> = {
      path: '/api/project/' + projectId + '/hazard/' + hazardId + '/subHazard/' + subHazardId + '/link/UCA/',
      json: JSON.stringify(options),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<UcaResponseDTO[]>((resolve: (value?: UcaResponseDTO[] | PromiseLike<UcaResponseDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<UcaResponseDTO[]>(postParameters)
        .then((value: UcaResponseDTO[]): void => {
          if (value != null) {
            resolve(value);
          }
        }).catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        }
        );
    });
  }

  /**
   * Method is only used to ensure that Detail Sheet is displayed correctly in system-level-hazard component
   * only difference is, that value will be returned no matter if its null or not
   *
   */
  public getUcaByHazardDetailSheet(projectId: string, hazardId: string, options: SearchRequest): Promise<UcaResponseDTO[]> {
    const postParameters: PostRequestParameters<UcaResponseDTO[]> = {
      path: '/api/project/' + projectId + '/hazard/' + hazardId + '/link/UCA',
      json: JSON.stringify(options),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<UcaResponseDTO[]>((resolve: (value?: UcaResponseDTO[] | PromiseLike<UcaResponseDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<UcaResponseDTO[]>(postParameters)
        .then((value: UcaResponseDTO[]): void => {
          resolve(value);
        }).catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        }
        );
    });
  }

  /**
   * Retrieve all ucas associated with current project
   * @param projectId project of current id
   * @param options search parameters
   */
  public getAllUcas(projectId: string, options: SearchRequest): Promise<UcaResponseDTO[]> {
    const postParameters: PostRequestParameters<UcaResponseDTO[]> = {
      path: '/api/project/' + projectId + '/uca/search',
      json: JSON.stringify(options),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<UcaResponseDTO[]>((resolve: (value?: UcaResponseDTO[] | PromiseLike<UcaResponseDTO[]>) => void,
      reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<UcaResponseDTO[]>(postParameters)
        .then((value: UcaResponseDTO[]): void => {
          if (value != null) {
            const ucaResponse: UcaResponseDTO[] = value as UcaResponseDTO[];
            resolve(ucaResponse);
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
  /**
  * Method is only used to ensure that Detail Sheet is displayed correctly in refine-hazard component
  * only difference is, that value will be returned no matter if its null or not
  *
  */
  public getUcaBySubhazardDetailSheet(projectId: string, hazardId: string, subHazardId: string, options: SearchRequest): Promise<UcaResponseDTO[]> {
    const postParameters: PostRequestParameters<UcaResponseDTO[]> = {
      path: '/api/project/' + projectId + '/hazard/' + hazardId + '/subHazard/' + subHazardId + '/link/UCA/',
      json: JSON.stringify(options),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<UcaResponseDTO[]>((resolve: (value?: UcaResponseDTO[] | PromiseLike<UcaResponseDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<UcaResponseDTO[]>(postParameters)
        .then((value: UcaResponseDTO[]): void => {
          resolve(value);
        }).catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        });
    });
  }

  /**
   * There is at most one controller constraint per UCA.
   * This method retrieves it.
   * @param projectId id of current project
   * @param controlActionId id of a a control action
   * @param ucaId has to be id of a UCA belonging to the one control action with id=controlActionId
   */
  public getControllerConstraintByUnsafeControlAction(projectId: string, controlActionId: string, ucaId: string): Promise<ControllerConstraintResponseDTO> {
    return new Promise<ControllerConstraintResponseDTO>((resolve: (value?: ControllerConstraintResponseDTO | PromiseLike<ControllerConstraintResponseDTO>) => void,
      reject: (reason?: any) => void): void => {
      this.requestService.performGETRequest('/api/project/' + projectId + '/ControlAction/' + controlActionId + '/UCA/' + ucaId + '/controller-constraint',
        this.config.useProjectToken())
        .then((value: ControllerConstraintResponseDTO) => {
          const controllerConstraint: ControllerConstraintResponseDTO = value as ControllerConstraintResponseDTO;
          resolve(controllerConstraint);
        }).catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        });
    });
  }
}
