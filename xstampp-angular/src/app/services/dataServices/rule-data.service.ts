import { Injectable } from '@angular/core';

import { EXPECTED_BOOLEAN_NOT_NULL, EXPECTED_ENTITY_NOT_NULL, EXPECTED_ENTITY_ARRAY_NOT_NULL } from './../../globals';
import { RequestService, PostRequestParameters } from '../request.service';
import { ConfigService } from './../config.service';
import { RuleRequestDTO, RuleResponseDTO, SearchRequest, ControlActionDTO } from './../../types/local-types';
import { ControlActionDataService } from './control-structure/control-action-data.service';
import { HttpErrorResponse } from '@angular/common/http';

interface RuleDataResponse {
  id: number;
  projectId: string;
  controlActionId: number;
  state: string;
  rule: string;
  lastEdited: number;
  lastEditor: string;
  lastEditorId: string;
  controllerId: number;
  lockExpirationTime: number;
  lockHolderDisplayName: string;
  lockHolderId: number;

}

@Injectable({
  providedIn: 'root'
})
export class RuleDataService {

  constructor(
    private readonly requestService: RequestService,
    private readonly config: ConfigService,
    private readonly controlActionDataService: ControlActionDataService
  ) { }

  public createRule(projectId: string, controllerId: string, rule: RuleRequestDTO): Promise<boolean> {
    const postParameters: PostRequestParameters<boolean> = {
      path: '/api/project/' + projectId + '/controller/' + controllerId + '/rule',
      json: JSON.stringify(rule),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<boolean>(
      (resolve: (value?: boolean | PromiseLike<boolean>) => void,
        reject: (reason?: any) => void): void => {
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

  public editRule(projectId: string, controllerId: string, ruleId: string, rule: RuleRequestDTO): Promise<boolean> {
    return new Promise<boolean>((
      resolve: (value?: boolean | PromiseLike<boolean>) => void,
      reject: (reason?: any) => void
    ): void => {
      this.requestService.performPUTRequest('/api/project/' + projectId + '/controller/' + controllerId + '/rule/' + ruleId, JSON.stringify(rule), this.config.useProjectToken())
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

  public deleteRule(projectId: string, controllerId: string, ruleId: string): Promise<boolean> {
    return new Promise<boolean>((
      resolve: (value?: boolean | PromiseLike<boolean>) => void,
      reject: (reason?: any) => void
    ): void => {
      this.requestService.performDELETERequest('/api/project/' + projectId + '/controller/' + controllerId + '/rule/' + ruleId, this.config.useProjectToken())
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

  public getAllRules(projectId: string, controllerId: string, options: SearchRequest): Promise<RuleResponseDTO[]> {
    const postParameters: PostRequestParameters<RuleDataResponse[]> = {
      path: '/api/project/' + projectId + '/controller/' + controllerId + '/rule/search',
      json: JSON.stringify(options),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<RuleResponseDTO[]>((
      resolve: (value?: RuleResponseDTO[] | PromiseLike<RuleResponseDTO[]>) => void,
      reject: (reason?: any) => void
    ): void => {
      this.requestService.performPOSTRequest(postParameters)
        .then((value: RuleDataResponse[]) => {
          if (value != null) {
            const values: RuleDataResponse[] = value as RuleDataResponse[];
            let rules = [];
            for (const elem of values) {
              const rule = {
                id: elem.id,
                projectId: elem.projectId,
                controlActionId: '' + elem.controlActionId,
                rule: elem.rule,
                state: elem.state,
                lastEdited: elem.lastEdited,
                lastEditor: elem.lastEditor,
                lastEditorId: elem.lastEditorId,
                controllerId: '' + elem.controllerId,
                controlActionName: '',
                lockExpirationTime: elem.lockExpirationTime,
                lockHolderDisplayName: elem.lockHolderDisplayName,
                lockHolderId: elem.lockHolderId
              };
              rules.push(rule);
            }
            for (const rule of rules) {
              if (rule.controlActionId !== '0') {
                this.controlActionDataService.getControlActionById(projectId, rule.controlActionId)
                  .then((value1: ControlActionDTO) => {
                    rule.controlActionName = value1.name;
                  }).catch((error: HttpErrorResponse) => {
                    if (error.message === EXPECTED_ENTITY_NOT_NULL) {
                      rule.controlActionId = '0';
                    } else {
                      rule.controlActionName = 'Error loading data';
                    }
                  });
              }
            }
            resolve(rules);
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

  public createControlActionLink(projectId: string, controllerId: string, ruleId: string, linkId: string, options: SearchRequest): Promise<boolean> {
    // implement correctly when required
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => { resolve(); });
  }

  public deleteControlActionLink(projectId: string, controllerId: string, ruleId: string, linkId: string, options: SearchRequest): Promise<boolean> {
    // implement correctly when required
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => { resolve(); });
  }
}
