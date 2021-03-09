import { EXPECTED_ENTITY_NOT_NULL, EXPECTED_BOOLEAN_NOT_NULL } from './../../globals';
import { Injectable } from '@angular/core';
import { RequestService, PostRequestParameters } from '../request.service';
import { ControlStructureDTO } from '../../types/local-types';
import { ConfigService } from '../config.service';
import { HttpErrorResponse } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ControlStructureDataService {

  constructor(private readonly requestService: RequestService, private readonly config: ConfigService) {
  }

  loadRootControlStructure(projectId: string): Promise<ControlStructureDTO> {
    return new Promise<ControlStructureDTO>(
      (resolve: (value?: ControlStructureDTO | PromiseLike<ControlStructureDTO>) => void,
        reject: (reason?: any) => void): void => {
        this.requestService.performGETRequest<ControlStructureDTO>('/api/project/' + projectId + '/control-structure/', this.config.useProjectToken())
          .then((value: ControlStructureDTO): void => {
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

  // TODO: Revision
  loadChildControlStructure(projectId: string, parentId: string): void {

  }

  saveRootControlStructure(projectId: string, controlStructure: ControlStructureDTO): Promise<Boolean> {
    const postParameters: PostRequestParameters<boolean> = {
      path: '/api/project/' + projectId + '/control-structure/',
      json: JSON.stringify(controlStructure),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<boolean>(postParameters).then((value: boolean): void => {
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

  // TODO: Revision
  saveChildControlStructure(projectId: string, parentId: string): void {

  }

}
