import {Injectable} from '@angular/core';
import {
  ActuatorDTO, Arrow, Box, ControlStructureDTO, SearchRequest, SensorDTO, SensorResponseDTO
} from '../../../types/local-types';
import {ConfigService} from '../../config.service';
import {EXPECTED_BOOLEAN_NOT_NULL, EXPECTED_ENTITY_NOT_NULL, EXPECTED_ENTITY_ARRAY_NOT_NULL} from './../../../globals';
import { RequestService, PostRequestParameters } from '../../request.service';
import { HttpErrorResponse } from '@angular/common/http';
import { ControlStructureDataService } from '../control-structure-data.service';
import { MessageService } from 'primeng/api';

@Injectable({
  providedIn: 'root'
})
export class SensorDataService {

  constructor(private readonly requestService: RequestService,
              private readonly config: ConfigService,
              private readonly messageService: MessageService,
              private readonly controlStructureDataService: ControlStructureDataService) {
  }

  createSensor(projectId: string, sensor: SensorDTO): Promise<SensorDTO> {
    const postParameters: PostRequestParameters<SensorDTO> = {
      path: '/api/project/' + projectId + '/sensor/',
      json: JSON.stringify(sensor),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<SensorDTO>((resolve: (value?: SensorDTO | PromiseLike<SensorDTO>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<SensorDTO>(postParameters)
        .then((value: SensorDTO): void => {
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

  alterSensor(projectId: string, sensorId: string, sensor: SensorDTO): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPUTRequest<boolean>('/api/project/' + projectId + '/sensor/' + sensorId, JSON.stringify(sensor), this.config.useProjectToken())
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

  deleteSensor(projectId: string, sensorId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performDELETERequest<boolean>('/api/project/' + projectId + '/sensor/' + sensorId, this.config.useProjectToken())
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

  getSensorById(projectId: string, sensorId: string): Promise<SensorDTO> {
    return new Promise<SensorDTO>((resolve: (value?: SensorDTO | PromiseLike<SensorDTO>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performGETRequest<SensorDTO>('/api/project/' + projectId + '/sensor/' + sensorId, this.config.useProjectToken())
        .then((value: SensorDTO): void => {
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

  getAllSensors(projectId: string, options: SearchRequest): Promise<SensorDTO[]> {
    const postParameters: PostRequestParameters<SensorDTO[]> = {
      path: '/api/project/' + projectId + '/sensor/search',
      json: JSON.stringify(options),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<SensorDTO[]>((resolve: (value?: SensorDTO[] | PromiseLike<SensorDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<SensorDTO[]>(postParameters)
        .then((value: SensorDTO[]): void => {
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


  getAllSensorsForTable(projectId: string, options: SearchRequest): Promise<SensorResponseDTO[]> {
    const postParameters: PostRequestParameters<SensorResponseDTO[]> = {
      path: '/api/project/' + projectId + '/sensor/search',
      json: JSON.stringify(options),
      insertProjectToken: this.config.useProjectToken()
    };
    return new Promise<SensorResponseDTO[]>((resolve: (value?: SensorResponseDTO[] | PromiseLike<SensorResponseDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<SensorResponseDTO[]>(postParameters)
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

  getSensorByBoxId(projectId: string, boxId: string): Promise<SensorDTO> {
    return new Promise<SensorDTO>((resolve: (value?: SensorDTO | PromiseLike<SensorDTO>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performGETRequest<SensorDTO>('/api/project/' + projectId + '/sensor/box/' + boxId, this.config.useProjectToken())
        .then((value: SensorDTO): void => {
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

  setBoxId(projectId: string, sensorId: string, boxId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPUTRequest<boolean>('/api/project/' + projectId + '/sensor/' + sensorId + '/box/' + boxId, null, this.config.useProjectToken())
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

  getSensorByFeedbackDestintationArrowId(projectId: string, arrowId: string): Promise<SensorDTO>{
    return new Promise<SensorDTO>((resolve: (value?: SensorDTO | PromiseLike<SensorDTO>) => void, reject: (reason?: any) => void): void  => {
        this.controlStructureDataService.loadRootControlStructure(projectId)
          .then((value: ControlStructureDTO): void => {
            const boxes: Box[] = value.boxes;
            const arrows: Arrow[] = value.arrows;
            const feedbackArrows = arrows.filter((controlAction: Arrow) => controlAction.id === arrowId && controlAction.type === 'Feedback');

            let sensors = boxes.filter((box: Box) => box.type === 'Sensor');
            let selectedSensor: Box;

            sensors.forEach((sensor: Box) => {
              feedbackArrows.forEach((controlArrow: Arrow) => {
                  if (controlArrow.destination === sensor.id) {
                    selectedSensor = sensor;
                  }
                });
            });

            if (selectedSensor) {
              this.getSensorByBoxId(projectId, selectedSensor.id)
                .then((sensor: SensorDTO) => {
                  resolve(sensor);
                  return sensor;
                }).catch((reason: HttpErrorResponse) => {
                console.error(reason);
                reject(reason.error);
              });
            }

          }).catch((reason: HttpErrorResponse) => {
            console.error(reason);
            reject(reason.error);
          }
        );
      });
    }

  getSensorByFeedbackSourceArrowId(projectId: string, arrowId: string): Promise<SensorDTO> {
    return new Promise<SensorDTO>((resolve: (value?: SensorDTO | PromiseLike<SensorDTO>) => void, reject: (reason?: any) => void): void  => {
      this.controlStructureDataService.loadRootControlStructure(projectId)
        .then((value: ControlStructureDTO): void => {
          const boxes: Box[] = value.boxes;
          const arrows: Arrow[] = value.arrows;
          const feedbackArrows = arrows.filter((controlAction: Arrow) => controlAction.id === arrowId && controlAction.type === 'Feedback');

          let sensors = boxes.filter((box: Box) => box.type === 'Sensor');
          let selectedSensor: Box;

          sensors.forEach((sensor: Box) => {
            feedbackArrows.forEach((controlArrow: Arrow) => {
                if (controlArrow.source === sensor.id) {
                  selectedSensor = sensor;
                }
            });
          });

          if (selectedSensor) {
            this.getSensorByBoxId(projectId, selectedSensor.id)
              .then((sensor: SensorDTO) => {
                resolve(sensor);
                return sensor;
              }).catch((reason: HttpErrorResponse) => {
              console.error(reason);
              reject(reason.error);
            });
          }

        }).catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        }
      );
    });
  }
}
