import { Injectable } from '@angular/core';

import { EXPECTED_BOOLEAN_NOT_NULL, EXPECTED_ENTITY_NOT_NULL, EXPECTED_ENTITY_ARRAY_NOT_NULL } from './../../globals';
import { RequestService, PostRequestParameters } from '../request.service';
import { ConfigService } from './../config.service';
import { ConversionRequestDTO, ConversionResponseDTO, SearchRequest, ControlActionDTO } from './../../types/local-types';
import { ControlActionDataService } from './control-structure/control-action-data.service';
import { HttpErrorResponse } from '@angular/common/http';

interface ConversionDataResponse {
  id: number;
  projectId: string;
  controlActionId: number;
  conversion: string;
  lastEdited: number;
  lastEditor: string;
  lastEditorId: string;
  actuatorId: number;
  state: string;
  lockExpirationTime: number;
  lockHolderDisplayName: string;
  lockHolderId: number;
}

@Injectable({
  providedIn: 'root'
})
export class ConversionDataService {

  constructor(
    private readonly requestService: RequestService,
    private readonly config: ConfigService,
    private readonly controlActionDataService: ControlActionDataService
  ) { }

  public createConversion(projectId: string, actuatorId: string, conversion: ConversionRequestDTO): Promise<boolean> {
    const postParameters: PostRequestParameters<boolean> = {
      path: '/api/project/' + projectId + '/actuator/' + actuatorId + '/conversion',
      json: JSON.stringify(conversion),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest(postParameters)
        .then((value: boolean) => {
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

  public editConversion(projectId: string, actuatorId: string, conversionId: string, conversion: ConversionRequestDTO): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPUTRequest(
        '/api/project/' + projectId + '/actuator/' + actuatorId + '/conversion/' + conversionId,
        JSON.stringify(conversion),
        this.config.useProjectToken()
      )
        .then((value: boolean) => {
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

  public deleteConversion(projectId: string, actuatorId: string, conversionId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performDELETERequest('/api/project/' + projectId + '/actuator/' + actuatorId + '/conversion/' + conversionId, this.config.useProjectToken())
        .then((value: boolean) => {
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

  public getAllConversions(projectId: string, actuatorId: string, options: SearchRequest): Promise<ConversionResponseDTO[]> {
    const postParameters: PostRequestParameters<ConversionDataResponse[]> = {
      path: '/api/project/' + projectId + '/actuator/' + actuatorId + '/conversion/search',
      json: JSON.stringify(options),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<ConversionResponseDTO[]>(
      (resolve: (value?: ConversionResponseDTO[] | PromiseLike<ConversionResponseDTO[]>) => void,
        reject: (reason?: any) => void): void => {
        this.requestService.performPOSTRequest(postParameters)
          .then((value: ConversionDataResponse[]) => {
            if (value != null) {
              const values: ConversionDataResponse[] = value as ConversionDataResponse[];
              let conversions = [];
              for (const elem of values) {
                const conversion = {
                  id: elem.id,
                  projectId: elem.projectId,
                  controlActionId: '' + elem.controlActionId,
                  conversion: elem.conversion,
                  lastEdited: elem.lastEdited,
                  lastEditor: elem.lastEditor,
                  lastEditorId: elem.lastEditorId,
                  actuatorId: '' + elem.actuatorId,
                  controlActionName: '',
                  state: elem.state,
                  lockExpirationTime: elem.lockExpirationTime,
                  lockHolderDisplayName: elem.lockHolderDisplayName,
                  lockHolderId: elem.lockHolderId
                };
                conversions.push(conversion);
              }
              for (const conversion of conversions) {
                if (conversion.controlActionId !== '0') {
                  this.controlActionDataService.getControlActionById(projectId, conversion.controlActionId)
                    .then((value1: ControlActionDTO) => {
                      conversion.controlActionName = value1.name;
                    }).catch((error: HttpErrorResponse) => {
                      if (error.message === EXPECTED_ENTITY_NOT_NULL) {
                        conversion.controlActionId = '0';
                      } else {
                        conversion.controlActionName = 'Error loading data';
                      }
                    });
                }
              }
              resolve(conversions);
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

  public createControlActionLink(projectId: string, actuatorId: string, conversionId: string, linkId: string, options: SearchRequest): Promise<boolean> {
    // implement correctly when required
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => { resolve(); });
  }

  public deleteControlActionLink(projectId: string, actuatorId: string, conversionId: string, linkId: string, options: SearchRequest): Promise<boolean> {
    // implement correctly when required
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => { resolve(); });
  }
}
