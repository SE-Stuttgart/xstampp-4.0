import { HttpErrorResponse } from '@angular/common/http';
import { EXPECTED_ENTITY_NOT_NULL, EXPECTED_BOOLEAN_NOT_NULL } from './../../globals';
import { Injectable } from '@angular/core';
import { RequestService, PostRequestParameters } from '../request.service';
import { ControllerConstraintRequestDTO, ControllerConstraintResponseDTO } from '../../types/local-types';
import { ConfigService } from '../config.service';

@Injectable({
  providedIn: 'root'
})
export class ControllerConstraintDataService {

  constructor(private readonly requestService: RequestService, private readonly config: ConfigService) {
  }

  createControllerConstraint(projectId: string, controllerId: string, ucaId: string, controllerConstraint: ControllerConstraintRequestDTO):
    Promise<ControllerConstraintResponseDTO> {
    const postParameters: PostRequestParameters<ControllerConstraintResponseDTO> = {
      path: '/api/project/' + projectId + '/control-action/' + controllerId + '/UCA/' +
        ucaId + '/controller-constraint/',
      json: JSON.stringify(controllerConstraint),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<ControllerConstraintResponseDTO>((resolve: (value?: ControllerConstraintResponseDTO | PromiseLike<ControllerConstraintResponseDTO>) => void,
      reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<ControllerConstraintResponseDTO>(postParameters)
        .then((value: ControllerConstraintResponseDTO): void => {
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

  // Currently not used because createControllerConstraint accomplish the same thing
  alterControllerConstraint(projectId: string, controllerId: string, ucaId: string, controllerConstraintId: string, controllerConstraint: ControllerConstraintRequestDTO):
    Promise<ControllerConstraintResponseDTO> {
    return new Promise<ControllerConstraintResponseDTO>((resolve: (value?: ControllerConstraintResponseDTO | PromiseLike<ControllerConstraintResponseDTO>) => void,
      reject: (reason?: any) => void): void => {
      this.requestService.performPUTRequest<ControllerConstraintRequestDTO>('/api/project/' + projectId + '/control-action/' +
        controllerId + '/UCA/' + ucaId + '/controller-constraint/' + controllerConstraintId,
        JSON.stringify(controllerConstraint), this.config.useProjectToken())
        .then((value: ControllerConstraintRequestDTO): void => {
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

  deleteControllerConstraint(projectId: string, controllerId: string, ucaId: string, controllerConstraintId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performDELETERequest<boolean>('/api/project/' + projectId + '/control-action/' + controllerId
        + '/UCA/' + ucaId + '/controller-constraint/' + controllerConstraintId,
        this.config.useProjectToken())
        .then((value: boolean): void => {
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

  getControllerConstraintById(projectId: string, controllerId: string, ucaId: string, controllerConstraintId: string): Promise<ControllerConstraintResponseDTO> {
    return new Promise<ControllerConstraintResponseDTO>((resolve: (value?: ControllerConstraintResponseDTO | PromiseLike<ControllerConstraintResponseDTO>) => void,
      reject: (reason?: any) => void): void => {
      this.requestService.performGETRequest<ControllerConstraintResponseDTO>('/api/project/' + projectId + '/control-action/' +
        controllerId + '/UCA/' + ucaId + '/controller-constraint/' + controllerConstraintId,
        this.config.useProjectToken())
        .then((value: ControllerConstraintResponseDTO): void => {
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
