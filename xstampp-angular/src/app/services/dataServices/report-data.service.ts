import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { PostRequestParameters, RequestService } from '../request.service';
import { EXPECTED_ENTITY_NOT_NULL } from '../../globals';
import { MessageService } from 'primeng/api';

@Injectable({
  providedIn: 'root'
})
export class ReportDataService {

  constructor(private readonly requestService: RequestService, private readonly messageService: MessageService) { }

  public getReport(projectId: string, config: ReportConfigDTO): Promise<Blob> {
    const postParameters: PostRequestParameters<Blob> = {
      path: '/api/project/' + projectId + '/report',
      json: JSON.stringify(config),
      insertProjectToken: true,
      acceptMime: 'application/pdf'
    };

    return new Promise((resolve: (value?: Blob | PromiseLike<Blob>) => void,
      reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequestBlob(postParameters).then((value: Blob) => {
        if (value != null) {
          this.messageService.add({ severity: 'success', summary: 'Report successfully generated!' });
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

export interface ReportConfigDTO {
  reportName: string;
  titlePage: boolean;
  tableOfContents: boolean;

  systemDescription: boolean;
  losses: boolean;
  hazards: boolean;
  subHazards: boolean;
  systemConstraints: boolean;
  subSystemConstraints: boolean;

  controlStructure: boolean;
  controlStructureHasColour: boolean;
  controllers: boolean;
  actuators: boolean;
  sensors: boolean;
  controlledProcesses: boolean;
  controlActions: boolean;
  feedback: boolean;
  inputs: boolean;
  output: boolean;
  responsibilities: boolean;

  ucas: boolean;
  controllerConstraints: boolean;

  processModels: boolean;
  processVariables: boolean;
  controlAlgorithms: boolean;
  conversions: boolean;
  lossScenarios: boolean;
  implementationConstraints: boolean;
}
