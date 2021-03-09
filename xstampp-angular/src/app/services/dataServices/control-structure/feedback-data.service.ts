import { Injectable } from '@angular/core';
import { RequestService, PostRequestParameters } from '../../request.service';
import {
  FeedbackDTO, SearchRequest, FeedbackResponseDTO, ControlActionDTO, ControlStructureDTO, Arrow
} from '../../../types/local-types';
import { ConfigService } from '../../config.service';
import { EXPECTED_BOOLEAN_NOT_NULL, EXPECTED_ENTITY_NOT_NULL, EXPECTED_ENTITY_ARRAY_NOT_NULL } from './../../../globals';
import { HttpErrorResponse } from '@angular/common/http';
import { ControlStructureDataService } from '../control-structure-data.service';

@Injectable({
  providedIn: 'root'
})
export class FeedbackDataService {

  constructor(private readonly requestService: RequestService,
              private readonly config: ConfigService,
              private readonly controlStructureDataService: ControlStructureDataService,) {
  }

  public alterFeedback(projectId: string, feedbackId: string, feedback: FeedbackDTO): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPUTRequest<boolean>('/api/project/' + projectId + '/feedback/' + feedbackId, JSON.stringify(feedback), this.config.useProjectToken())
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

  public deleteFeedback(projectId: string, feedbackId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performDELETERequest<boolean>('/api/project/' + projectId + '/feedback/' + feedbackId, this.config.useProjectToken())
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

  public createFeedback(projectId: string, feedback: FeedbackDTO): Promise<FeedbackDTO> {
    const postParameters: PostRequestParameters<FeedbackDTO> = {
      path: '/api/project/' + projectId + '/feedback',
      json: JSON.stringify(feedback),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<FeedbackDTO>((resolve: (value?: FeedbackDTO | PromiseLike<FeedbackDTO>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<FeedbackDTO>(postParameters)
        .then((value: FeedbackDTO): void => {
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

  public getAllFeedbacks(projectId: string, options: SearchRequest): Promise<FeedbackDTO[]> {
    const postParameters: PostRequestParameters<FeedbackDTO[]> = {
      path: '/api/project/' + projectId + '/feedback/search',
      json: JSON.stringify(options),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<FeedbackDTO[]>((resolve: (value?: FeedbackDTO[] | PromiseLike<FeedbackDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<FeedbackDTO[]>(postParameters)
        .then((value: FeedbackDTO[]): void => {
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
        }
        );
    });
  }

  public getAllFeedbacksForTable(projectId: string, options: SearchRequest): Promise<FeedbackResponseDTO[]> {
    const postParameters: PostRequestParameters<FeedbackResponseDTO[]> = {
      path: '/api/project/' + projectId + '/feedback/search',
      json: JSON.stringify(options),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<FeedbackResponseDTO[]>((resolve: (value?: FeedbackResponseDTO[] | PromiseLike<FeedbackResponseDTO[]>) => void, reject: (reason?: any) => void): void => {
            this.requestService.performPOSTRequest<FeedbackResponseDTO[]>(postParameters)
        .then(value => {
          if (value != null) {
            resolve(value);
          } else {
            const error = new Error(EXPECTED_ENTITY_ARRAY_NOT_NULL);
            console.log(error);
            reject(error);
          }
        }).catch(reason => {
          console.error(reason);
          reject(reason.error);
        }
      );
    });
  }


  getFeedbackById(projectId: string, feedbackId: string): Promise<FeedbackDTO> {
    return new Promise<FeedbackDTO>((resolve: (value?: FeedbackDTO | PromiseLike<FeedbackDTO>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performGETRequest<FeedbackDTO>('/api/project/' + projectId + '/feedback/' + feedbackId, this.config.useProjectToken())
        .then((value: FeedbackDTO): void => {
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

  getFeedbacksByArrowId(projectId: string, arrowId: string): Promise<FeedbackDTO[]> {
    return new Promise<FeedbackDTO[]>((resolve: (value?: FeedbackDTO[] | PromiseLike<FeedbackDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performGETRequest<FeedbackDTO[]>('/api/project/' + projectId + '/feedback/arrow/' + arrowId, this.config.useProjectToken())
        .then((value: FeedbackDTO[]): void => {
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

  setArrowId(projectId: string, feedbackId: string, arrowId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPUTRequest<boolean>('/api/project/' + projectId + '/feedback/' + feedbackId + '/arrow/' + arrowId, null, this.config.useProjectToken())
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

  getFeedbackByDestinationBoxId(projectId: string, boxId: string): Promise<FeedbackDTO[]> {
    return new Promise<FeedbackDTO[]>((resolve: (value?: FeedbackDTO[] | PromiseLike<FeedbackDTO[]>) => void, reject: (reason?: any) => void): void  => {
      this.controlStructureDataService.loadRootControlStructure(projectId)
        .then((value: ControlStructureDTO): void => {
          const arrows: Arrow[] = value.arrows;
          const arrowsOfTypeFeedback = arrows.filter((arrow: Arrow) => arrow.destination === boxId && arrow.type === 'Feedback');

          // TODO can a box has only one arrow or multiple ?
          if (arrowsOfTypeFeedback.length > 0) {
            this.getFeedbacksByArrowId(projectId, arrowsOfTypeFeedback[0].id)
              .then((feedbacks: FeedbackDTO[]) => {
                resolve(feedbacks);
                return feedbacks;
              }).catch((reason: HttpErrorResponse) => {
              console.error(reason);
              reject(reason.error);
            });
          } else {
            reject();
          }
        }).catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        }
      );
    });
  }

  getFeedbackBySourceBoxId(projectId: string, boxId: string): Promise<FeedbackDTO[]> {
    return new Promise<FeedbackDTO[]>((resolve: (value?: FeedbackDTO[] | PromiseLike<FeedbackDTO[]>) => void, reject: (reason?: any) => void): void  => {
      this.controlStructureDataService.loadRootControlStructure(projectId)
        .then((value: ControlStructureDTO): void => {
          const arrows: Arrow[] = value.arrows;
          const arrowsOfTypeFeedback = arrows.filter((arrow: Arrow) => arrow.source === boxId && arrow.type === 'Feedback');

          // TODO can a box has only one arrow or multiple ?
          if (arrowsOfTypeFeedback.length > 0) {
            this.getFeedbacksByArrowId(projectId, arrowsOfTypeFeedback[0].id)
              .then((feedbacks: FeedbackDTO[]) => {
                resolve(feedbacks);
                return feedbacks;
              }).catch((reason: HttpErrorResponse) => {
              console.error(reason);
              reject(reason.error);
            });
          } else {
            reject();
          }
        }).catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        }
      );
    });
  }
}
