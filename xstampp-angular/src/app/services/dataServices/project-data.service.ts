import { Injectable } from '@angular/core';
import { RequestService, PostRequestParameters } from '../request.service';
import { ProjectRequestDTO, ProjectResponseDTO } from '../../types/local-types';
import { EXPECTED_BOOLEAN_NOT_NULL, EXPECTED_ENTITY_NOT_NULL, EXPECTED_ENTITY_ARRAY_NOT_NULL } from './../../globals';
import { HttpErrorResponse } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ProjectDataService {

  constructor(private readonly requestService: RequestService) { }

  public alterProject(project: ProjectRequestDTO, projectId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPUTRequest<boolean>('/api/auth/project/' + projectId, JSON.stringify(project), false, true)
        .then((value: boolean): void => {
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

  public createProject(project: ProjectRequestDTO): Promise<ProjectResponseDTO> {
    const postParameters: PostRequestParameters<ProjectResponseDTO> = {
      path: '/api/auth/project/',
      json: JSON.stringify(project),
      insertProjectToken: false,
      insertUserToken: true
    };
    return new Promise<ProjectResponseDTO>((resolve: (value?: ProjectResponseDTO | PromiseLike<ProjectResponseDTO>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<ProjectResponseDTO>(postParameters)
        .then((value: ProjectResponseDTO): void => {
          if (value != null) {
            resolve(value);
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

  public deleteProject(projectId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performDELETERequest<boolean>('/api/auth/project/' + projectId, false, true)
        .then((value: boolean): void => {
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

  public getProjectById(projectId: string): Promise<ProjectResponseDTO> {
    return new Promise<ProjectResponseDTO>((resolve: (value?: ProjectResponseDTO | PromiseLike<ProjectResponseDTO>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performGETRequest<ProjectResponseDTO>('/api/auth/project/' + projectId, false, true)
        .then((value: ProjectResponseDTO): void => {
          if (value != null) {
            resolve(value);
          } else {
            const error = new Error(EXPECTED_ENTITY_NOT_NULL);
            console.log(error);
            reject(error);
          }
        }).catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        });

    });
  }

  public getAllProjects(): Promise<ProjectResponseDTO[]> {
    return new Promise<ProjectResponseDTO[]>((resolve: (value?: ProjectResponseDTO[] | PromiseLike<ProjectResponseDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performGETRequest<ProjectResponseDTO[]>('/api/auth/group/private/projects', false, true)
        .then((value: ProjectResponseDTO[]) => {
          if (value != null) {
            resolve(value);
          } else {
            const error = new Error(EXPECTED_ENTITY_ARRAY_NOT_NULL);
            console.log(error);
            reject(error);
          }
        }).catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        });
    });
  }


  public getProjectsForUser(): Promise<ProjectResponseDTO[]> {
    return new Promise<ProjectResponseDTO[]>((resolve: (value?: ProjectResponseDTO[] | PromiseLike<ProjectResponseDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performGETRequest<ProjectResponseDTO[]>('/api/auth/projects', false, true)
        .then((value: ProjectResponseDTO[]) => {
          if (value != null) {
            resolve(value);
          } else {
            const error = new Error(EXPECTED_ENTITY_ARRAY_NOT_NULL);
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
