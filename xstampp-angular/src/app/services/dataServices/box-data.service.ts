import { EXPECTED_ENTITY_ARRAY_NOT_NULL } from './../../globals';
import { Injectable } from '@angular/core';
import { RequestService } from '../request.service';
import { BoxEntityDTO } from '../../types/local-types';
import { ConfigService } from '../config.service';
import { HttpErrorResponse } from '@angular/common/http';

@Injectable({
    providedIn: 'root'
  })
  export class BoxDataService {

    constructor(private readonly requestService: RequestService, private readonly config: ConfigService) {
    }

    getAllInputBoxes(projectId: string): Promise<BoxEntityDTO[]> {
      return new Promise<BoxEntityDTO[]>((resolve: (value?: BoxEntityDTO[] | PromiseLike<BoxEntityDTO[]>) => void,
      reject: (reason?: any) => void): void => {
        this.requestService.performGETRequest('/api/project/' + projectId + '/box/input/search', this.config.useProjectToken())
          .then((value: BoxEntityDTO[]) => {
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
  }
