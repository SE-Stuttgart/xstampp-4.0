import { Component } from '@angular/core';
import { DetailedSheetUtils } from '../../common/detailed-sheet/utils/detailed-sheet-utils';
import {
  ACTUATOR,
  CONTROL_ACTION,
  CONTROLLED_PROCESS,
  CONTROLLER,
  CONTROLLER_CONSTRAINT,
  CONVERSION,
  FEEDBACK,
  HAZARD,
  IMPLEMENTATION_CONSTRAINT,
  INPUT,
  LOSS,
  LOSS_SCENARIO,
  OUTPUT,
  PROCESS_MODEL,
  PROCESS_VARIABLE,
  RESPONSIBILITY,
  RULE,
  SENSOR,
  SUB_HAZARD,
  SUB_SYSTEM_CONSTRAINT,
  SYSTEM_CONSTRAINT,
  UNSAFE_CONTROL_ACTION
} from '../../types/local-types';

export const controllerTitle: string = DetailedSheetUtils.generateSheetTitle(CONTROLLER);
export const ruleTitle: string = DetailedSheetUtils.generateSheetTitle(RULE);
export const controlActionTitle: string = DetailedSheetUtils.generateSheetTitle(CONTROL_ACTION);
export const ucaTitle: string = DetailedSheetUtils.generateSheetTitle(UNSAFE_CONTROL_ACTION);
export const lossScenarioTitle: string = DetailedSheetUtils.generateSheetTitle(LOSS_SCENARIO);
export const implementationConstraintTitle: string = DetailedSheetUtils.generateSheetTitle(IMPLEMENTATION_CONSTRAINT);
export const controllerConstraintTitle: string = DetailedSheetUtils.generateSheetTitle(CONTROLLER_CONSTRAINT);
export const processModelTitle: string = DetailedSheetUtils.generateSheetTitle(PROCESS_MODEL);
export const responsibilityTitle: string = DetailedSheetUtils.generateSheetTitle(RESPONSIBILITY);
export const processVariableTitle: string = DetailedSheetUtils.generateSheetTitle(PROCESS_VARIABLE);
export const actuatorTitle: string = DetailedSheetUtils.generateSheetTitle(ACTUATOR);
export const feedbackTitle: string = DetailedSheetUtils.generateSheetTitle(FEEDBACK);
export const sensorTitle: string = DetailedSheetUtils.generateSheetTitle(SENSOR);
export const controlledProcessTitle: string = DetailedSheetUtils.generateSheetTitle(CONTROLLED_PROCESS);
export const inputTitle: string = DetailedSheetUtils.generateSheetTitle(INPUT);
export const outputTitle: string = DetailedSheetUtils.generateSheetTitle(OUTPUT);
export const systemConstraintTitle: string = DetailedSheetUtils.generateSheetTitle(SYSTEM_CONSTRAINT);
export const hazardTitle: string = DetailedSheetUtils.generateSheetTitle(HAZARD);
export const subHazardTitle: string = DetailedSheetUtils.generateSheetTitle(SUB_HAZARD);
export const lossTitle: string = DetailedSheetUtils.generateSheetTitle(LOSS);
export const subSystemConstraintTitle: string = DetailedSheetUtils.generateSheetTitle(SUB_SYSTEM_CONSTRAINT);
export const converationTitle: string = DetailedSheetUtils.generateSheetTitle(CONVERSION);
export const stepTwoTitleElement: string = 'Control Structure';

/**
 * Defines local types for dependency tree.
 */
export class SelectedEntity<T> {
  entityTitle: string;
  entity: T[];
  projectId: string;
  stepOneSelected: boolean;
  stepTwoSelected: boolean;
  controllerTreeSelected: boolean;
  controlStructureEntity?: boolean;
  moveTree: boolean;

  constructor(selectedEntity: T[],
              entityTitle: string,
              projectId?: string,
              stepOneSelected?: boolean,
              stepTwoSelected?: boolean,
              controllerTreeSelected?: boolean,
              moveTree?: boolean) {
    this.entity = selectedEntity;
    this.entityTitle = entityTitle;
    this.projectId = projectId;
    this.stepOneSelected = stepOneSelected;
    this.stepTwoSelected = stepTwoSelected;
    this.controllerTreeSelected = controllerTreeSelected;
    this.moveTree = moveTree;
  }
}

export interface DependentElementNode {
  entity?: any;
  children?: DependentElementNode[];
  name?: string;
  entityId?: string;
  entityTitle?: string;
  rootElement?: boolean;
  titleElement?: boolean;
  linkElement?: boolean;
  moveElement?: boolean;
  controlStructureElement?: boolean;
  linkTitleElement?: boolean;
}

export interface DependentTreeNode {
  expandable: boolean;
  name: string;
  level: number;
}

export const linkedControlStructureTitleNode: DependentElementNode = {
  name: stepTwoTitleElement,
  entityTitle: stepTwoTitleElement,
  children: [],
  titleElement: true,
};

export const hazardTitleNode: DependentElementNode = {
  name: 'Hazard-Link',
  children: [],
  linkTitleElement: true
};

export const subHazardLiningTitle: DependentElementNode = {
  name: 'Sub-Hazard-Link',
  children: [],
  linkTitleElement: true,
};

export const losseTitleNode: DependentElementNode = {
  name: 'Loss-Link',
  children: [],
  linkTitleElement: true,
};

export const systemConstraintTitleNode: DependentElementNode = {
  name: 'System-Constraint-Link',
  children: [],
  linkTitleElement: true,
};

export const ucaTitleNode: DependentElementNode = {
  name: ucaTitle,
  children: [],
  linkTitleElement: true,
};

export const ucaLinkNode: DependentElementNode = {
  name: 'UCA-Link',
  children: [],
  linkTitleElement: true
}

export const responsibilityTitleNode: DependentElementNode = {
  name: responsibilityTitle,
  children: [],
  titleElement: true,
};

export const processsVariableNode: DependentElementNode = {
  name: processVariableTitle,
  children: [],
  titleElement: true,
};

export const processModelsNode: DependentElementNode = {
  name: processModelTitle,
  children: [],
  titleElement: true,
};

export const subSystemConstraintsNode: DependentElementNode = {
  name: subSystemConstraintTitle,
  children: [],
  titleElement: true,
};

export const subHazardTitleNode: DependentElementNode = {
  name: subHazardTitle,
  children: [],
  titleElement: true,
};

export const sensorTitleNode: DependentElementNode = {
  name: sensorTitle,
  children: [],
  titleElement: true,
};

export const feedbackTitleNode: DependentElementNode = {
  name: feedbackTitle,
  children: [],
  titleElement: true,
};

export const controllerTitleNode: DependentElementNode = {
  name: controllerTitle,
  children: [],
  titleElement: true,
};

export const ruleTitleNode: DependentElementNode = {
  name: ruleTitle,
  children: [],
  titleElement: true,
};

export const controlActionTitleNode: DependentElementNode = {
  name: controlActionTitle,
  children: [],
  titleElement: true,
};

export const implementationConstraintTitleNode: DependentElementNode = {
  name: implementationConstraintTitle,
  children: [],
  titleElement: true,
};

export const controlledProcessesTitleNode: DependentElementNode = {
  name: controlledProcessTitle,
  children: [],
  titleElement: true,
};

export const actuatorTitleNode: DependentElementNode = {
  name: actuatorTitle,
  children: [],
  titleElement: true,
};

export const controlledProcessTitleNode: DependentElementNode = {
  name: controlledProcessTitle,
  children: [],
  titleElement: true,
};

export const outputTitleNode: DependentElementNode = {
  name: outputTitle,
  children: [],
  titleElement: true,
};

export const inputTitleNode: DependentElementNode = {
  name: inputTitle,
  children: [],
  titleElement: true,
};

export const ConverationTitleNode: DependentElementNode = {
  name: converationTitle,
  entityTitle: converationTitle,
  children: [],
  titleElement: true,
};

export function entitiesExist<T>(entity: T[]): boolean {
  return entity && entity != null && entity.length > 0;
}

export function entityExist<T>(entity: T): boolean {
  return entity && entity != null;
}


