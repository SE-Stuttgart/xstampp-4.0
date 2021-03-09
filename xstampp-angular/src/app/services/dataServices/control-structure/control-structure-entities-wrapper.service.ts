import { Injectable } from '@angular/core';
import { ActuatorDataService } from './actuator-data.service';
import { ControlActionDataService } from './control-action-data.service';
import { ControlledProcessDataService } from './controlled-process-data.service';
import { ControllerDataService } from './controller-data.service';
import { FeedbackDataService } from './feedback-data.service';
import { InputDataService } from './input-data.service';
import { OutputDataService } from './output-data.service';
import { ProcessVariableDataService } from './process-variable-data.service';
import { SensorDataService } from './sensor-data.service';

@Injectable({
  providedIn: 'root'
})
export class ControlStructureEntitiesWrapperService {

  constructor(
    public controlActionDataService: ControlActionDataService,
    public controlAlgorithmDataService: ControlActionDataService,
    public controlledProcessDataService: ControlledProcessDataService,
    public controllerDataService: ControllerDataService,
    public feedbackDataService: FeedbackDataService,
    public inputDataService: InputDataService,
    public outputDataService: OutputDataService,
    public processVariableDataService: ProcessVariableDataService,
    public sensorDataService: SensorDataService,
    public actuatorDataService: ActuatorDataService) {
  }
}
