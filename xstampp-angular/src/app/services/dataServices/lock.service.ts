import { Injectable } from '@angular/core';
import { EXPECTED_ENTITY_NOT_NULL } from '../../globals';
import { LockRequestDTO, UnlockRequestDTO } from '../../types/local-types';
import { ConfigService } from '../config.service';
import { RequestService, PostRequestParameters } from '../request.service';
import { HttpErrorResponse } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class LockService {

  constructor(private readonly requestService: RequestService, private readonly config: ConfigService) { }

  public lockEntity(projectId: string, lock: LockRequestDTO): Promise<LockResponse> {
    const postParameters: PostRequestParameters<LockResponse> = {
      path: '/api/project/' + projectId + '/lock',
      json: JSON.stringify(lock),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<LockResponse>((
      resolve: (value?: LockResponse | PromiseLike<LockResponse>) => void,
      reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<LockResponse>(postParameters)
        .then((lockResponse: LockResponse) => {
          if (lockResponse.status === 'SUCCESS') {
            resolve(lockResponse);
          } else {
            const error = new Error('Could not get lock for ' + lock.entityName + ' with ID ' + lock.id);
            console.error(error);
            reject(error);
          }
        })
        .catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        }
        );
    });
  }

  public unlockEntity(projectId: string, lock: UnlockRequestDTO): Promise<LockResponse> {
    const postParameters: PostRequestParameters<LockResponse> = {
      path: '/api/project/' + projectId + '/unlock',
      json: JSON.stringify(lock),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<LockResponse>((
      resolve: (value?: LockResponse | PromiseLike<LockResponse>) => void,
      reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<LockResponse>(postParameters)
        .then((unlockResponse: LockResponse) => {
          if (unlockResponse.status === 'SUCCESS') {
            resolve(unlockResponse);
          } else {
            const error = new Error(EXPECTED_ENTITY_NOT_NULL);
            console.error(error);
            reject(error);
          }
        })
        .catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        }
        );
    });
  }
}

export interface LockResponse {
  status: string;
}
