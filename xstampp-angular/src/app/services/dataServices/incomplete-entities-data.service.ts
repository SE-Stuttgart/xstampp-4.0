import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { EXPECTED_ENTITY_NOT_NULL } from 'src/app/globals';
import { ConfigService } from '../config.service';
import { RequestService } from '../request.service';

@Injectable({
  providedIn: 'root'
})
export class IncompleteEntitiesService {

  constructor(
    private readonly requestService: RequestService, private readonly config: ConfigService,
  ) { }
  //name: entity name - bspw Loss, Process Variable
updateState(projectId: string, entityId: string, entity: incompleteEntityDTO): Promise<boolean> {
  return new Promise<boolean>((resolve: (value?: boolean | Promise<boolean>) => void, reject: (reason?: any) => void): void => {
    this.requestService.performPUTRequest<boolean>('/api/project/' + projectId + '/mark-entity/' + entityId + '/', JSON.stringify(entity), this.config.useProjectToken())
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

}
export class incompleteEntityDTO {
  entityName: string = null;
  state: string = null;
  parentId: string = null;
}
