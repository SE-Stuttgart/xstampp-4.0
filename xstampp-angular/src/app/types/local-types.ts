import { FilteringTableElement } from '../common/filtering-table/filtering-table.component';

/* tslint:disable */

// TODO: Most Id's are strings for some reason. Can we use numbers instead? @Rico
// TODO: no we can't cause everything will explode -@Felix
// TODO: :( @Rico

// local data types
export interface ProjectRequestDTO {
  id: string;
  name: string;
  referenceNumber: string;
  description: string;
  groupId?: string;
}
export interface LoginRequestDTO {
  email: string;
  password: string;
}
export interface ProjectResponseDTO extends ProjectRequestDTO {
  createdAt: number;
}

export interface GroupRequestDTO {
  name: string;
  description: string;
}

export interface MemberDTO {
  uid: string;
  name: string;
  role: string;
  icon?: string;
  displayName?: string;

}

export interface AdminPasswordChangeRequestDTO {
  adminPassword: string;
  newPassword: string;
  newPasswordRepeat: string;
}


export interface UserDTO {
  uid: string;
  email: string;
  displayName: string;
}


// START of XSTAMPP Entities

export interface Theme{
  name: string;
  id: string;
  colors: string;

}

export interface Icon{
  id: string;
  path: string;
}

export interface TableEntity {
  id: string;
  name: string;
  description: string;
  state: string;
}

export type LossRequestDTO = TableEntity;

export interface MainTableEntity {
  id: string;
  referenceNumber?: string;
  name: string;
  description: string;
  state: string;
  lastEditor: string,
  lastEdited: number,
  subHazDisplayId?: string,
  subSafetyConstraintDisplayId?: string,
  subSafetyContraintId?: string,
  parentSubSafetyConstraintId?: string,
  subSafetyConstraintName?: string,
  controlActionId?: string,
  controlActionName?: string,
  conversion?: string,
  rule?: string,
  parentId?: string,
  constraintName?: string
}

export interface LossResponseDTO extends LossRequestDTO {
  lastEdited: number;
  lastEditor: string;
  lastEditorId: string;
  icon?: string;
  lockExpirationTime: number;
  lockHolderDisplayName: string;
  lockHolderId: string
}

export interface HazardRequestDTO {
  id: string;
  name: string;
  description: string;
  state: string;
}

export interface HazardResponseDTO extends HazardRequestDTO {
  lastEdited: number;
  lastEditor: string;
  lastEditorId: string;
  icon?: string;
  lockExpirationTime: number;
  lockHolderDisplayName: string;
  lockHolderId: string
}

export interface SubHazardRequestDTO {
  id: string;
  name: string;
  description: string;
  state: string;
}

export interface SubHazardResponseDTO extends SubHazardRequestDTO {
  lastEdited: number;
  lastEditor: string;
  lastEditorId: string;
  parentId: string;
  icon?: string;
  lockExpirationTime: number;
  lockHolderDisplayName: string;
  lockHolderId: string
}

export interface SystemConstraintRequestDTO {
  id: string;
  name: string;
  description: string;
  state: string;
}

export interface SystemConstraintResponseDTO extends SystemConstraintRequestDTO, FilteringTableElement {
  lastEdited: number;
  lastEditor: string;
  lastEditorId: string;
  icon?: string;
  lockExpirationTime: number;
  lockHolderDisplayName: string;
  lockHolderId: string
}

export interface SubSystemConstraintRequestDTO {
  id: string;
  name: string;
  description: string;
}

export interface SubSystemConstraintResponseDTO extends SubSystemConstraintRequestDTO {
  lastEdited: number;
  lastEditor: string;
  lastEditorId: string;
  parentId: string;
  lockExpirationTime: number;
  lockHolderDisplayName: string;
  lockHolderId: string
}

export interface SystemDescriptionRequestDTO {
  description: string;
}

export interface SystemDescriptionResponseDTO extends SystemDescriptionRequestDTO {
  projectId: string;
  lastEdited: number;
  lastEditorId: string;
  lastEditor: string;
  lockExpirationTime: number;
  lockHolderDisplayName: string;
  lockHolderId: string
}

export interface UcaRequestDTO {
  projectId: string;
  name: string;
  description: string;
  category: string;
  state?: string;


}

export interface UcaResponseDTO extends UcaRequestDTO {
  id?: string;
  parentId?: string;
  lastEditorId: string;
  lastEdited: number;
  lastEditor: string;
  icon?: string;
  lockExpirationTime: number;
  lockHolderDisplayName: string;
  lockHolderId: string;
}

export interface ControllerConstraintRequestDTO {
  projectId: string;
  controlActionId: number;
  name: string;
  description: string;
  state: string;
}

export interface ControllerConstraintResponseDTO extends ControllerConstraintRequestDTO {
  id: string;
  lastEdited: number;
  lastEditor: string;
  controlActionProjectId: string;
  lastEditorId: string;
  lockExpirationTime: number;
  lockHolderDisplayName: string;
  lockHolderId: string
}


export interface ResponsibilityRequestDTO {
  projectId: string;
  controllerId: number;
  name: string;
  description: string;
  state: string;
}

export interface ResponsibilityCreationDTO extends ResponsibilityRequestDTO {
  systemConstraintIds: number[];
}

export interface ResponsibilityResponseDTO extends ResponsibilityRequestDTO {
  id: string;
  controllerProjectId: string; // maybe delete ?
  lastEditorId: string;
  lastEdited: number;
  lastEditor: string;
  icon?: string;
  lockExpirationTime: number;
  lockHolderDisplayName: string;
  lockHolderId: string
}

export interface ImplementationConstraintRequestDTO {
  projectId: string;
  id: string;
  lossScenarioId: string;
  name: string;
  description: string;
  controllerConstraint: string;
  state: string;
}

export interface ImplementationConstraintResponseDTO extends ImplementationConstraintRequestDTO {
  lastEdited: number;
  lastEditor: string;
  lastEditorId: string;
  icon?: string;
  lockExpirationTime: number;
  lockHolderDisplayName: string;
  lockHolderId: string
}

export interface LossScenarioRequestDTO {
  //General information for a loss scenario
  name: string;
  id: string;
  description: string;
  projectId: string;
  ucaId: string;
  state: string;
  /*
  Information for the different category
  */
  headCategory: string;
  subCategory: string;
  controller1Id?: string;
  controller2Id?: string;
  //TODO: ADD type for controlAlogorithm
  controlAlgorithm?: number;
  description1?: string;
  description2?: string;
  description3?: string;
  controlActionId?: string;
  inputArrowId?: string;
  feedbackArrowId?: string;
  inputBoxId?: string;
  sensorId?: string;
  reason?: string;
}

export interface LossScenarioResponseDTO extends LossScenarioRequestDTO {
  controllerProjectId: string; // maybe delete ?
  lastEdited: number;
  lastEditedBy: string;
  lastEditorId: string;
  icon?: string;
}

/**
 * Represents a filter request for responsibilities. Only responsibilities that are linked to the
 * given entities can pass the filter, if an entity is undefined, it won't affect the filter.
 */
export interface ResponsibilityFilterRequestDTO {
  systemConstraintId?: string;
  controllerId?: string;
}

/**
 * Contains filter preview numbers. See the backend Javadoc for service.project.data.dto.ResponsibilityFilterPreviewResponseDTO
 * for more details.
 */
export interface ResponsibilityFilterPreviewResponseDTO {
  allSystemConstraintsPreview: number;
  systemConstraintIdToPreviewMap: Map<number, number>;
  allControllersPreview: number;
  controllerIdToPreviewMap: Map<number, number>;
}

export interface RuleRequestDTO {
  projectId: string;
  id: string;
  controlActionId: string;
  rule: string;
  state: string;
}

export interface RuleResponseDTO extends RuleRequestDTO {
  lastEdited: number;
  lastEditor: string;
  lastEditorId: string;
  controllerId: string;
  controlActionName: string;
  icon?: string;
  lockExpirationTime: number;
  lockHolderDisplayName: string;
  lockHolderId: string
}

export interface ConversionRequestDTO {
  projectId: string;
  controlActionId: string;
  conversion: string;
  state:string;

}

export interface ConversionResponseDTO extends ConversionRequestDTO {
  lastEdited: number;
  lastEditor: string;
  lastEditorId: string;
  actuatorId: string;
  controlActionName: string;
  icon?: string;
  lockExpirationTime: number;
  lockHolderDisplayName: string;
  lockHolderId: string
}

// END of XSTAMPP Entities

//START OF Types which are used for tables but also exists as another Type in controlStructure .......
export interface ControllerRequestDTO {
  id: string;
  name: string;
  description: string;
  state: string;
}

export interface ControllerResponseDTO extends ControllerRequestDTO {
  lastEdited: number;
  lastEditor: string;
  lastEditorId: string;
  icon?: string;
  lockExpirationTime: number;
  lockHolderDisplayName: string;
  lockHolderId: string
}

export interface ProcessVariableRequestDTO {
  id?: number;
  name: string;
  description: string;
  source: BoxRequestDTO;
  variable_type: string;
  variable_value: string;
  currentProcessModel: string;
  process_models: string[];
  valueStates: string[];
  responsibilityIds: string[];

}

export interface ProcessVariableResponseDTO extends ProcessVariableRequestDTO {
  lastEdited: number;
  lastEditor: string;
  lastEditorId: string;
  lockExpirationTime: number;
  lockHolderDisplayName: string;
  lockHolderId: string
}


export interface FeedbackRequestDTO {
  id: string;
  name: string;
  description: string;
  state: string;
}

export interface FeedbackResponseDTO extends FeedbackRequestDTO {
  lastEdited: number;
  lastEditor: string;
  lastEditorId: string;
  icon?: string;
  lockExpirationTime: number;
  lockHolderDisplayName: string;
  lockHolderId: string
}

export interface ControlActionRequestDTO {
  id: string;
  name: string;
  description: string;
  state: string;
}

export interface ControlActionResponseDTO extends ControlActionRequestDTO {
  lastEdited: number;
  lastEditor: string;
  lastEditorId: string;
  icon?: string;
  lockExpirationTime: number;
  lockHolderDisplayName: string;
  lockHolderId: string
}

export interface ControlledProcessRequestDTO {
  id: string;
  name: string;
  description: string;
  state: string;
}

export interface ControlledProcessResponseDTO extends ControlledProcessRequestDTO {
  lastEdited: number;
  lastEditor: string;
  lastEditorId: string;
  icon?: string;
  lockExpirationTime: number;
  lockHolderDisplayName: string;
  lockHolderId: string
}

// TODO: for new version use ArrowRequestDTO
export interface InputArrowRequestDTO {
  id: string;
  name: string;
  description: string;
  state: string;
}

export interface InputArrowResponseDTO extends InputArrowRequestDTO {
  lastEdited: number;
  lastEditor: string;
  lastEditorId: string;
  icon?: string;
  lockExpirationTime: number;
  lockHolderDisplayName: string;
  lockHolderId: string
}

export interface ArrowRequestDTO {
  id: string;
  name: string;
  description: string;
  state: string;
}

export interface ArrowResponseDTO extends ArrowRequestDTO {
  lastEdited: number;
  lastEditor: string;
  lastEditorId: string;
  icon?: string;
  lockExpirationTime: number;
  lockHolderDisplayName: string;
  lockHolderId: string
}

export interface SensorRequestDTO {
  id: string;
  name: string;
  description: string;
  state: string;
}

export interface SensorResponseDTO extends SensorRequestDTO {
  lastEdited: number;
  lastEditor: string;
  lastEditorId: string;
  icon?: string;
  lockExpirationTime: number;
  lockHolderDisplayName: string;
  lockHolderId: string
}

export interface ActuatorRequestDTO {
  id: string;
  name: string;
  description: string;
  state: string;
}

export interface ActuatorResponseDTO extends ActuatorRequestDTO {
  lastEdited: number;
  lastEditor: string;
  icon?: string;
  lastEditorId: string;
  lockExpirationTime: number;
  lockHolderDisplayName: string;
  lockHolderId: string
}

export interface BoxEntityRequestDTO {
  id: string;
  name: string;
  description: string;
  boxId?: string;
}

export interface BoxEntityResponseDTO extends BoxEntityRequestDTO {
  lastEdited: number;
  lastEditor: string;
  lastEditorId: string;
  lockExpirationTime: number;
  lockHolderDisplayName: string;
  lockHolderId: string
}

// END OF Types which are used for table but also exists as another Type  in controlStructure .......

export interface ControlStructureDTO {
  projectId: string;
  svg: string;
  blackAndWhiteSVG: string;
  boxes: Box[];
  arrows: Arrow[];
}

export interface SubBoxEntityDTO extends BoxEntityDTO {
  parentId: string; // todo maybe use just the BoxEntity
}

export interface ControlStructureEntityDTO {
  id?: string;
  name: string;
  projectId: string;
  description?: string;
  state?:string;
}

// dto for the first stage of the cs create requests
export interface PrepareRequestDTO {
  entityType: ArrowAndBoxType;
  boxEntity?: BoxEntityDTO;
  arrowEntity?: ArrowEntityDTO;
  shapeId: string;
}


// dto for the second stage of the cs create requests
export interface PendingRequestDTO {
  promise: Promise<any>;
  entityType: ArrowAndBoxType;
  shapeId: string;
}


export interface PendingRequest {
  boxEntity?: BoxEntityDTO;
  arrowEntity?: ArrowEntityDTO;
  requestType: RequestType;
  entityType: ArrowAndBoxType;
}

export interface BoxEntityDTO extends ControlStructureEntityDTO {
  boxId: string;
}

export interface ArrowEntityDTO extends ControlStructureEntityDTO {
  arrowId: string;
}

export interface ActuatorDTO extends BoxEntityDTO {
}

export interface ControlActionDTO extends ArrowEntityDTO {
}

export interface ControllerDTO extends BoxEntityDTO, FilteringTableElement {
}

export interface FeedbackDTO extends ArrowEntityDTO {
}

export interface InputDTO extends ArrowEntityDTO {
}

export interface OutputDTO extends ArrowEntityDTO {
}

export interface SensorDTO extends BoxEntityDTO {
}

export interface ControlledProcessDTO extends BoxEntityDTO {
}

export interface ProcessVariableDTO {
  id?: string;
  projectId?: string;
  name: string;
  description: string;
  source: BoxRequestDTO;
  variable_type: string;
  variable_value: string;
  currentProcessModel: string;
  process_models: string[];
  valueStates: string[];
  responsibilityIds: string[];
}


export interface ControlAlgorithmDTO extends BoxEntityDTO {

}

export interface BoxRequestDTO {
  id: string;
  name: string;
  projectId: string;
}


export interface Box extends BoxRequestDTO {
  type: string; // todo enum for types?
  x: number;
  y: number;
  height: number;
  width: number;
  parent: any; // typ?
}

export interface Arrow {
  id: string;
  source: string;
  destination: string;
  projectId: string;
  label: string;
  type: string; // todo enum for types?
  parts: Coordinates[];
  parent: string;
}

export interface Coordinates {
  x: number | string;
  y: number | string;
}

export interface CSCoordinateData {
  paperSize: Coordinates;
  position: Coordinates;
}

// Request specific types ...............
export interface SearchRequest {
  filter?: Filter;
  orderBy?: string;
  orderDirection?: string;
  from?: number;
  amount?: number;
}

export interface PageRequest {
  from: number;
  amount: number;
}

export interface Filter {
  type: string;
  fieldName?: string;
  fieldValue?: string;
  body?: Filter[];
}

export interface LockRequestDTO {
  id?: string;
  parentId?: string;
  expirationTime: string;
  entityName: string;
}

export interface UnlockRequestDTO {
  id?: string;
  parentId?: string;
  entityName: string;
}

export enum Type {
  ANY_LIKE,
  ANY_EQUALS,
  FIELD_EQUALS
}

export enum BoxType {
  Sensor = 'Sensor',
  Actuator = 'Actuator',
  Controller = 'Controller',
  ControlledProcess = 'ControlledProcess'
}

export enum ArrowType {
  ControlAction = 'ControlAction',
  Feedback = 'Feedback',
  Output = 'Output',
  Input = 'Input',
}

export enum ArrowAndBoxType {
  Sensor = 'Sensor',
  Actuator = 'Actuator',
  Controller = 'Controller',
  ControlledProcess = 'ControlledProcess',
  ControlAction = 'ControlAction',
  Feedback = 'Feedback',
  Output = 'Output',
  Input = 'Input',
  InputBox = 'InputBox',
  OutputBox = 'OutputBox',
  DashedBox = 'DashedBox',
  TextBox = 'TextBox'
}

export enum RequestType {
  Delete,
  Create,
  Alter
}

export enum Direction {
  NE,
  SE,
  NW,
  SW,
  N,
  E,
  S,
  W
}

export const PROJECT = 'project';
export const HAZARD = 'hazard';
export const LOSS = 'loss';
export const SUB_HAZARD = 'sub-hazard';
export const SYSTEM_CONSTRAINT = 'system-constraint';
export const SUB_SYSTEM_CONSTRAINT = 'sub-system-constraint';
export const RESPONSIBILITY = 'responsibility';
export const UNSAFE_CONTROL_ACTION = 'unsafe-control-action';
export const CONTROLLER_CONSTRAINT = 'controller-constraint';
export const SYSTEM_DESCRIPTION = 'system-description';
export const LOSS_SCENARIO = 'loss-scenario';

export const ARROW = 'arrow';
export const BOX = 'box';

export const CONTROL_STRUCTURE = 'control-structure';
export const ACTUATOR = 'actuator';
export const SENSOR = 'sensor';
export const CONTROLLED_PROCESS = 'controlled-process';
export const CONTROLLER = 'controller';
export const CONTROL_ACTION = 'control-action';
export const FEEDBACK = 'feedback';
export const INPUT = 'input';
export const OUTPUT = 'output';
export const PROCESS_VARIABLE = 'process-variable';
export const PROCESS_MODEL = 'process-model';
export const CONTROL_ALGORITHM = 'control-algorithm';
export const IMPLEMENTATION_CONSTRAINT = 'implementation-constraint';
export const RULE = 'rule';
export const CONVERSION_TABLE = 'conversion-table';
export const CONVERSION = 'conversion';

export const GROUP = 'group';
export const GROUP_MEMBERSHIP = 'group-membership';
export const USER = 'user';

