import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SearchRequest } from '../../../types/local-types';
import { ConfigService } from '../../config.service';
import { RequestService, PostRequestParameters } from '../../request.service';
import { EXPECTED_BOOLEAN_NOT_NULL, EXPECTED_ENTITY_ARRAY_NOT_NULL, EXPECTED_ENTITY_NOT_NULL } from './../../../globals';
import { BoxRequestDTO } from './../../../types/local-types';

@Injectable({
  providedIn: 'root'
})
export class ProcessVariableDataService {

  constructor(private readonly requestService: RequestService, private readonly config: ConfigService) {
  }

  createProcessVariable(projectId: string, controllerId: string, processVariable: ProcessVariableRequestDTO): Promise<ProcessVariableResponseDTO> {
    const postParameters: PostRequestParameters<ProcessVariableResponseDTO> = {
      path: '/api/project/' + projectId + '/controller/' + controllerId + '/process-variable/',
      json: JSON.stringify(processVariable),
      insertProjectToken: this.config.useProjectToken(),
      interfaceObj: new ProcessVariableResponseDTO()
    };
    return new Promise<ProcessVariableResponseDTO>(
      (resolve: (value?: ProcessVariableResponseDTO | PromiseLike<ProcessVariableResponseDTO>) => void, reject: (reason?: any) => void): void => {
        this.requestService.performPOSTRequest<ProcessVariableResponseDTO>(postParameters)
          .then((value: ProcessVariableResponseDTO) => {

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

  alterProcessVariable(projectId: string, controllerId: string, processModelId: string, processVariableId: string, processVariable: ProcessVariableRequestDTO): Promise<boolean> {
    return new Promise<boolean>(
      (resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
        this.requestService.performPUTRequest<boolean>(
          '/api/project/' + projectId + '/controller/' + controllerId + '/process-model/' + processModelId + '/process-variable/' + processVariableId,
          JSON.stringify(processVariable),
          this.config.useProjectToken()
        )
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

  // TODO: implement in backend if wolfgang thinks he needs it
  alterAllProcessVariableValues(projectId: string, processVariable: ProcessVariableRequestDTO[]) {
    /**
     * Just do an alter for all PV's
     */
    return null;
  }

  deleteProcessVariable(projectId: string, processVariableId: string): Promise<boolean> {
    return new Promise<boolean>(
      (resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
        this.requestService.performDELETERequest<boolean>(
          '/api/project/' + projectId + '/process-variable/' + processVariableId,
          this.config.useProjectToken()
        )
          .then((value: boolean): void => {
            if (value != null) {
              resolve(value);
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

  getProcessVariableById(projectId: string, processModelId: string, processVariableId: string): Promise<ProcessVariableResponseDTO> {
    return new Promise<ProcessVariableResponseDTO>(
      (resolve: (value?: ProcessVariableResponseDTO | PromiseLike<ProcessVariableResponseDTO>) => void, reject: (reason?: any) => void): void => {
        this.requestService.performGETRequest<ProcessVariableResponseDTO>(
          '/api/project/' + projectId + '/process-model/' + processModelId + '/process-variable/' + processVariableId,
          this.config.useProjectToken(),
          undefined,
          new ProcessVariableResponseDTO(),
        )
          .then((value: ProcessVariableResponseDTO): void => {
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

  getAllProcessVariables(projectId: string): Promise<ProcessVariableResponseDTO[]> {
    return new Promise<ProcessVariableResponseDTO[]>(
      (resolve: (value?: ProcessVariableResponseDTO[] | PromiseLike<ProcessVariableResponseDTO[]>) => void, reject: (reason?: any) => void): void => {
        this.requestService.performGETRequest<ProcessVariableResponseDTO[]>(
          '/api/project/' + projectId + '/process-variables/all',
          this.config.useProjectToken(),
          undefined,
          new ProcessVariableResponseDTO()
        )
          .then((value: ProcessVariableResponseDTO[]): void => {
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

  getProcessVariablesBySource(projectId: string, source: string, processModelId: string): Promise<ProcessVariableResponseDTO[]> {
    return new Promise<ProcessVariableResponseDTO[]>(
      (resolve: (value?: ProcessVariableResponseDTO[] | PromiseLike<ProcessVariableResponseDTO[]>) => void, reject: (reason?: any) => void): void => {
        this.requestService.performGETRequest<ProcessVariableResponseDTO[]>(
          '/api/project/' + projectId + '/process-model/' + processModelId + '/process-variable/source/' + source,
          this.config.useProjectToken(),
          undefined,
          new ProcessVariableResponseDTO(),
        )
          .then((value: ProcessVariableResponseDTO[]): void => {
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
          });
      });
  }

  getAllUnlinkedProcessModels(projectId: string, filter: string): Promise<ProcessVariableResponseDTO[]> {
    return new Promise<ProcessVariableResponseDTO[]>(
      (resolve: (value?: ProcessVariableResponseDTO[] | PromiseLike<ProcessVariableResponseDTO[]>) => void, reject: (reason?: any) => void): void => {
        this.requestService.performGETRequest<ProcessVariableResponseDTO[]>(
          '/api/project/' + projectId + '/process-variable/unlinked/' + filter,
          this.config.useProjectToken(),
          undefined,
          new ProcessVariableResponseDTO(),
        )
          .then((value: ProcessVariableResponseDTO[]) => {
            if (value != null) {
              resolve(value);
            } else {
              const error = new Error(EXPECTED_ENTITY_NOT_NULL);
              console.log(error);
              reject(error);
            }
          })
          .catch((reason: HttpErrorResponse) => {
            console.error(reason);
            reject(reason.error);
          });
      });
  }


}

/**
 * interface-classes for validation
 */

export class ProcessVariableRequestDTO {
  id?: string = null;
  name: string = null;
  description: string = null;
  source: BoxRequestDTO = null;
  variable_type: '' | 'DISCREET' | 'INDISCREET' = null;
  variable_value: string = null;
  currentProcessModel: string = null;
  process_models: string[] = null;
  valueStates: string[] = null;
  responsibilityIds: string[] = null;
  state: string = null;
}

export class ProcessVariableResponseDTO extends ProcessVariableRequestDTO {
  icon?: string;
  last_editor_id: string;
  last_edited: number = null;
  last_editor_displayname: string = null;
}
