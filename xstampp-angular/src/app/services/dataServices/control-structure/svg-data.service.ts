import { Injectable } from '@angular/core';
import { RequestService, PostRequestParameters } from '../../request.service';
import { EXPECTED_ENTITY_NOT_NULL } from 'src/app/globals';
import { HttpErrorResponse } from '@angular/common/http';
import { MessageService } from 'primeng/api';

@Injectable({
  providedIn: 'root'
})
export class SvgDataService {

  constructor(private readonly requestService: RequestService, private readonly messageService: MessageService) { }

  public getReport(projectId: string, coloured: boolean): Promise<Blob> {
    const postParameters: PostRequestParameters<Blob> = {
      path: '/api/project/' + projectId + '/control-structure-svg/' + coloured,
      json: '',
      insertProjectToken: true,
      acceptMime: 'image/svg+xml'
    };

    return new Promise((resolve: (value?: Blob | PromiseLike<Blob>) => void,
      reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequestBlob(postParameters).then((value: Blob) => {
        if (value != null) {
          resolve(value);
        } else {
          const error: Error = new Error(EXPECTED_ENTITY_NOT_NULL);
          console.log(error);
          reject(error);
        }
      }).catch(async (response: HttpErrorResponse) => {
        console.log(response);
        let blob: Blob = response.error;
        if (blob !== undefined) {
          this.messageService.add({ severity: 'error', summary: 'Report Failure', detail: await new Response(blob).text()});
        } else {
          this.messageService.add({ severity: 'error', summary: 'Server connection error' });
        }
      });
    });
  }
}
