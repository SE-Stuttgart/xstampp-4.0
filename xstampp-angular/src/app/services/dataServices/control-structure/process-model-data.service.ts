import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { EXPECTED_BOOLEAN_NOT_NULL, EXPECTED_ENTITY_NOT_NULL } from 'src/app/globals';
import { ConfigService } from '../../config.service';
import { RequestService, PostRequestParameters } from '../../request.service';

@Injectable({
  providedIn: 'root'
})

export class ProcessModelDataService {

  constructor(
    private readonly requestService: RequestService,
    private readonly config: ConfigService
  ) { }

  /**
   * creates new PM
   */
  create(projectId: string, value: ProcessModelDTO): Promise<ProcessModelResponseDTO> {
    const postParameters: PostRequestParameters<ProcessModelResponseDTO> = {
      path: '/api/project/' + projectId + '/process-model',
      json: JSON.stringify(value),
      insertProjectToken: this.config.useProjectToken(),
      interfaceObj: new ProcessModelResponseDTO()
    };
    return new Promise<ProcessModelResponseDTO>(
      (resolve: (value?: ProcessModelResponseDTO | PromiseLike<ProcessModelResponseDTO>) => void, reject: (reason?: any) => void): void => {
        this.requestService.performPOSTRequest<ProcessModelResponseDTO>(postParameters)
          .then((pm: ProcessModelResponseDTO) => {
            if (pm != null) {
              resolve(pm);
            } else {
              const error: Error = new Error(EXPECTED_ENTITY_NOT_NULL);
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

  /**
   * gets the PM by id
   */
  getById(projectId: string, id: string): Promise<ProcessModelResponseDTO> {
    return new Promise<ProcessModelResponseDTO>(
      (resolve: (value?: ProcessModelResponseDTO | PromiseLike<ProcessModelResponseDTO>) => void, reject: (reason?: any) => void): void => {
        this.requestService.performGETRequest<ProcessModelResponseDTO>(
          '/api/project/' + projectId + '/process-model/' + id,
          this.config.useProjectToken(),
          undefined,
          new ProcessModelResponseDTO(),
        )
          .then((value: ProcessModelResponseDTO) => {
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

  /**
   * get's all PM's for the given controller
   */
  getAllByControllerId(projectId: string, controllerId: string): Promise<ProcessModelResponseDTO[]> {
    return new Promise<ProcessModelResponseDTO[]>(
      (resolve: (value?: ProcessModelResponseDTO[] | PromiseLike<ProcessModelResponseDTO[]>) => void, reject: (reason?: any) => void): void => {
        this.requestService.performGETRequest<ProcessModelResponseDTO[]>(
          '/api/project/' + projectId + '/process-model/all/controllerId/' + controllerId,
          this.config.useProjectToken(),
          undefined,
          new ProcessModelResponseDTO(),
        )
          .then((value: ProcessModelResponseDTO[]) => {
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

  /**
   * updates the PM
   */
  update(projectId: string, id: string, value: ProcessModelDTO): Promise<ProcessModelResponseDTO> {
    const postParameters: PostRequestParameters<ProcessModelResponseDTO> = {
      path: '/api/project/' + projectId + '/process-model/' + id,
      json: JSON.stringify(value),
      insertProjectToken: this.config.useProjectToken(),
      interfaceObj: new ProcessModelResponseDTO()
    };
    return new Promise<ProcessModelResponseDTO>(
      (resolve: (value?: ProcessModelResponseDTO | PromiseLike<ProcessModelResponseDTO>) => void, reject: (reason?: any) => void): void => {
        this.requestService.performPOSTRequest<ProcessModelResponseDTO>(postParameters)
          .then((pm: ProcessModelResponseDTO) => {
            if (pm != null) {
              resolve(pm);
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

  /**
     * get's all PM's for the given controller
     */
  getAllUnlinkedProcessModels(projectId: string): Promise<ProcessModelResponseDTO[]> {
    return new Promise<ProcessModelResponseDTO[]>(
      (resolve: (value?: ProcessModelResponseDTO[] | PromiseLike<ProcessModelResponseDTO[]>) => void, reject: (reason?: any) => void): void => {
        this.requestService.performGETRequest<ProcessModelResponseDTO[]>(
          '/api/project/' + projectId + '/process-model/unlinked',
          this.config.useProjectToken(),
          undefined,
          new ProcessModelResponseDTO(),
        )
          .then((value: ProcessModelResponseDTO[]) => {
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

  /**
   * delete the controller
   */
  delete(projectId: string, id: string): Promise<boolean> {
    return new Promise<boolean>(
      (resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
        this.requestService.performDELETERequest<boolean>(
          '/api/project/' + projectId + '/process-model/' + id,
          this.config.useProjectToken(),
        )
          .then((value: boolean) => {
            if (value != null) {
              resolve(value);
            } else {
              const error = new Error(EXPECTED_BOOLEAN_NOT_NULL);
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

export class ProcessModelDTO {
  id?: string = null;
  name: string = null;
  description: string = null;
  controllerId: string = null;
  state: string = null;
}

export class ProcessModelResponseDTO extends ProcessModelDTO {
  lastEdited: number = null;
  lastEditor: string = null;
  icon?: string;
  lastEditorId: string;
  lockExpirationTime: number = null;
  lockHolderDisplayName: string = null;
  lockHolderId: string = null;
}
