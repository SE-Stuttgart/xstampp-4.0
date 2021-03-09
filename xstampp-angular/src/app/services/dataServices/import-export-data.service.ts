import { Injectable } from '@angular/core';
import { RequestService, PostRequestParameters } from '../request.service';
import { ProjectRequestDTO, ProjectResponseDTO, GroupRequestDTO } from 'src/app/types/local-types';
import { EXPECTED_ENTITY_NOT_NULL } from 'src/app/globals';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../auth.service';

@Injectable({
  providedIn: 'root'
})
export class ImportExportDataService {

  constructor(private readonly requestService: RequestService) { }

  /**
   * Clone existing project
   * @param file
   */
  public cloneProject(projectId: string, project: ProjectRequestDTO): Promise<ProjectResponseDTO> {
    const postParameters: PostRequestParameters<ProjectResponseDTO> = {
      path: '/api/auth/project/clone/' + projectId,
      json: JSON.stringify(project),
      insertProjectToken: false,
      insertUserToken: true,
    };
    return new Promise<ProjectResponseDTO>((resolve, reject) => {
      this.requestService.performPOSTRequest(postParameters)
        .then((value: ProjectResponseDTO): void => {
          if (value != null) {
            resolve(value as ProjectResponseDTO);
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
   *  Method import a existing project from XSTAMPP 3.0 to XSTAMPP 4.0
   * @param file witch was imported
   */
  public importProject(importObject: JSONParent): Promise<ProjectResponseDTO> {
    const postParameters: PostRequestParameters<ProjectResponseDTO> = {
      path: '/api/auth/project/import',
      json: JSON.stringify(importObject),
      insertProjectToken: false,
      insertUserToken: true,
    };
    return new Promise<ProjectResponseDTO>((resolve, reject) => {
      this.requestService.performPOSTRequest(postParameters)
        .then((value): void => {
          if (value != null) {
            resolve(value as ProjectResponseDTO);
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
  *  Method import a existing project from XSTAMPP 3.0 to XSTAMPP 4.0
  * @param file witch was imported
  */
  public exampleProject(group: ProjectRequestDTO): Promise<ProjectResponseDTO> {
    const postParameters: PostRequestParameters<ProjectResponseDTO> = {
      path: '/api/auth/project/example',
      json: JSON.stringify(group),
      insertProjectToken: false,
      insertUserToken: true,
    };
    return new Promise<ProjectResponseDTO>((resolve, reject) => {
      this.requestService.performPOSTRequest(postParameters)
        .then((value: ProjectResponseDTO): void => {
          if (value != null) {
            resolve(value as ProjectResponseDTO);
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
   * Method export a existing project
   * @param projectId from the project witch was exported
   */
  public getProjectToExportById(projectId: string): Promise<JSON> {
    return new Promise<JSON>((resolve, reject) => {
      this.requestService.performGETRequest('/api/project/' + projectId + '/export', true)
        .then((value: JSON): void => {
          if (value != null) {
            const project: JSON = value as JSON;
            resolve(project);
          } else {
            const error: Error = new Error(EXPECTED_ENTITY_NOT_NULL);
            console.log(error);
            reject(error);
          }
        }).catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        });

    });
  }
}
export class JSONParent {
  type: string = null;
  projectRequest?: ProjectRequestDTO = null;
  file: string = null;
}

