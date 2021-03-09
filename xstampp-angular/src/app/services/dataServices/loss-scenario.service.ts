import { RequestService, PostRequestParameters } from '../request.service';
import { SearchRequest, LossScenarioResponseDTO, LossScenarioRequestDTO } from '../../types/local-types';
import { Injectable } from '@angular/core';
import { ConfigService } from '../config.service';
import { EXPECTED_BOOLEAN_NOT_NULL, EXPECTED_ENTITY_NOT_NULL, EXPECTED_ENTITY_ARRAY_NOT_NULL } from '../../globals';
import { HttpErrorResponse } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class LossScenarioDataService {

  constructor(private readonly requestService: RequestService, private readonly config: ConfigService) {
  }


  /**
   * When a loss scenario was created by a user then the params where given the method
   * @param projectId: From the special project
   * @param lossScenaio: was createt by the user
   */
  createLossScenario(projectId: string, lossScenaio: LossScenarioRequestDTO): Promise<LossScenarioResponseDTO> {
    const postParameters: PostRequestParameters<LossScenarioResponseDTO> = {
      path: '/api/project/' + projectId + '/loss-scenario',
      json: JSON.stringify(lossScenaio),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<LossScenarioResponseDTO>((resolve: (value?: LossScenarioResponseDTO | PromiseLike<LossScenarioResponseDTO>) => void,
      reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest(postParameters)
        .then((value: LossScenarioResponseDTO) => {
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

  /**
   * Method when the user chang a loss scenario
   * @param projectId from the selected project
   * @param lossScenarioId  from the selected loss scenario
   * @param lossScenario  all information from the scenario
   */
  alterLossScenario(projectId: string, lossScenarioId: string, lossScenario: LossScenarioRequestDTO): Promise<LossScenarioResponseDTO> {
    return new Promise<LossScenarioResponseDTO>((resolve: (value?: LossScenarioResponseDTO | PromiseLike<LossScenarioResponseDTO>) => void,
      reject: (reason?: any) => void): void => {
      this.requestService.performPUTRequest('/api/project/' + projectId + '/loss-scenario/' + lossScenarioId, JSON.stringify(lossScenario), this.config.useProjectToken())
        .then((value: LossScenarioResponseDTO) => {
          if (value != null) {
            resolve();
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

  /**
   * delete loss scenario
   * @param projectId  from the project
   * @param lossScenarioId the id from the loss scenario witch was deleted by the user
   */
  deleteLossScenario(projectId: string, lossScenarioId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void,
    reject: (reason?: any) => void): void => {
      this.requestService.performDELETERequest('/api/project/' + projectId + '/loss-scenario/' + lossScenarioId, this.config.useProjectToken())
        .then((value: boolean) => {
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

  /**
   * Get a Loss Scenario by an id
   * @param projectId the project id
   * @param lossScenarioId the special loss scenario id
   */
  getLossScenarioById(projectId: string, lossScenarioId: string): Promise<LossScenarioResponseDTO> {
    return new Promise<LossScenarioResponseDTO>((resolve: (value?: LossScenarioResponseDTO | PromiseLike<LossScenarioResponseDTO>) => void,
    reject: (reason?: any) => void): void => {
      this.requestService.performGETRequest('/api/project/' + projectId + '/loss-scenario/' + lossScenarioId, this.config.useProjectToken())
        .then((value: LossScenarioResponseDTO) => {
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

  /**
   * Method with get all Loss Scenarios from the project
   * @param projectId id from the project
   * @param options optionally search request
   */
  getAllLossScenarios(projectId: string, options: SearchRequest): Promise<LossScenarioResponseDTO[]> {
    const postParameters: PostRequestParameters<LossScenarioResponseDTO[]> = {
      path: '/api/project/' + projectId + '/loss-scenario/search',
      json: JSON.stringify(options),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<LossScenarioResponseDTO[]>((resolve: (value?: LossScenarioResponseDTO[] | PromiseLike<LossScenarioResponseDTO[]>) => void,
      reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest(postParameters)
        .then((value: LossScenarioResponseDTO[]) => {
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

  /**
   *  Get the loss scenarios by an uca id
   * @param projectId: string
   * @param ucaId the speciall ucaId
   */
  getLossScenariosByUcaAndCAId(projectId: string, ucaId: string, caId: string): Promise<LossScenarioResponseDTO[]> {
    return new Promise<LossScenarioResponseDTO[]>((resolve: (value?: LossScenarioResponseDTO[] | PromiseLike<LossScenarioResponseDTO[]>) => void,
      reject: (reason?: any) => void): void => {
      this.requestService.performGETRequest('/api/project/' + projectId + '/loss-scenario/control-action/' + caId + '/uca/' + ucaId, this.config.useProjectToken())
        .then((value: LossScenarioResponseDTO[]) => {
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
