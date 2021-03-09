import { ArrowAndBoxType, ControlledProcessDTO, ControlActionDTO, InputDTO, ControllerDTO, ActuatorDTO, SensorDTO, FeedbackDTO, OutputDTO } from 'src/app/types/local-types';

export enum CSDiaType {
    STEP2 = 'STEP2',
    STEP4 = 'STEP4'
}

export enum CSShape {
    BOX = 'box',
    ARROW = 'arrow'
}

export interface ContextMenuData {
    name?: string;
    type?: ArrowAndBoxType;
    shape?: CSShape;
    deleteEntityIds?: string[];
    alterEntityIds?: string[];
    labels?: string[];
    saveEntity?: boolean;
    projectId?: string;
    id: string;
}

export type CsCmDTO =
    | ControlledProcessDTO
    | ControlActionDTO
    | ControllerDTO
    | ActuatorDTO
    | SensorDTO
    | FeedbackDTO
    | OutputDTO
    | InputDTO;
