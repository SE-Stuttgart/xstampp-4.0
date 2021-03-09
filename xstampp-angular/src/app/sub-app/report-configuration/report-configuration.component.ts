import { Component, AfterViewInit } from '@angular/core';
import { ReportDataService, ReportConfigDTO } from 'src/app/services/dataServices/report-data.service';
import { ActivatedRoute } from '@angular/router';
import { ReportFormCheckbox } from './checkbox/ReportFormCheckbox';
import { ReportFormGroup } from './checkbox-group/ReportFormGroup';
import { ReportFormElement } from './ReportFormElement';
import { ProjectResponseDTO } from 'src/app/types/local-types';
import { ProjectDataService } from 'src/app/services/dataServices/project-data.service';

@Component({
  selector: 'app-report-configuration',
  templateUrl: './report-configuration.component.html',
  styleUrls: ['./report-configuration.component.css']
})
/**
 * This component allows the user to generate a PDF-Report out of
 * the currently opened project. Beforehand, the user can select
 * which sections are to be included in the report.
 */
export class ReportConfigurationComponent implements AfterViewInit {

  /** Represents all form elements and their grouping as an object structure */
  formStructure: ReportFormGroup[];
  /** A reference map, allowing to find any form element by its name */
  indexMap: { [key: string]: ReportFormElement };
  /** The id of the currently opened project */
  projectId: string;
  /** Contains details about the currently opened project */
  project: ProjectResponseDTO;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly reportDataService: ReportDataService,
    private readonly data: ProjectDataService) { }

  ngAfterViewInit(): void {
    // Get details about the currently opened project
    this.route.parent.params.subscribe((value: any) => {
      this.projectId = value.id;
    });
    if (this.projectId) {
      this.data.getProjectById(this.projectId).then((value: ProjectResponseDTO) => {
        this.project = value;
      });
    }
  }

  /**
   * Triggered by the "Deselect All" Button.
   * Deselects all Checkboxes in the form.
   */
  deselectAll(): void {
    this.formStructure.forEach((element: ReportFormGroup) => {
      element.update(false);
    });
  }

  /**
   * Triggered by the "Select All" Button.
   * Selects all Checkboxes in the form.
   */
  selectAll(): void {
    this.formStructure.forEach((element: ReportFormGroup) => {
      element.update(true);
    });
  }

  /**
   * Triggered by the "Generate Report" Button.
   * Retrieves all information in the form and sends a
   * report request. The returned pdf is opened in a new
   * tab.
   */
  exportAsPDF(): void {
    let preDto = { };
    Object.keys(this.indexMap).forEach((key: string) => {
      let element: ReportFormElement = this.indexMap[key];
      if (element.type === 'box') {
        preDto[key] = element.selected;
      }
    });
    preDto['reportName'] = this.project.name + ' - ' + this.project.referenceNumber;
    const reportConfigDto: ReportConfigDTO = preDto as ReportConfigDTO;
    this.reportDataService.getReport(this.projectId, reportConfigDto).then((value: Blob) => {
      const url = window.URL.createObjectURL(value);
      var newWindow = window;
      newWindow.open(url);
    });
  }

  /**
   * Returns the structure of form elements
   */
  getFormStructure(): ReportFormGroup[] {
    if (this.formStructure !== undefined) {
      return this.formStructure;
    }
    /* When modifying the form structure, remember to adjust the DTOs in both
     * Frontend and Backend (only if you add, rename or remove checkboxes). */
    let formStructure = [
      new ReportFormGroup('general', 'General', [
        new ReportFormCheckbox('titlePage', 'Title Page', true, []),
        new ReportFormCheckbox('tableOfContents', 'Table of Contents', true, [])
      ]),
      new ReportFormGroup('step1', 'Step 1', [
        new ReportFormCheckbox('systemDescription', 'System Description', true, []),
        new ReportFormCheckbox('losses', 'Losses', true, []),
        new ReportFormCheckbox('hazards', 'Hazards', true, [
          new ReportFormCheckbox('subHazards', 'Subhazards', true, [])]),
        new ReportFormCheckbox('systemConstraints', 'System Constraints', true, [
          new ReportFormCheckbox('subSystemConstraints', 'Sub System Constraints', true, [])]),
      ]),
      new ReportFormGroup('step2', 'Step 2', [
        new ReportFormCheckbox('controlStructure', 'Control Structure', true, [
          new ReportFormCheckbox('controlStructureHasColour', 'with Colour', true, [])
        ]),
        new ReportFormGroup('systemComponents', 'System Components', [
          new ReportFormCheckbox('controllers', 'Controllers', true, []),
          new ReportFormCheckbox('actuators', 'Actuators', true, []),
          new ReportFormCheckbox('sensors', 'Sensors', true, []),
          new ReportFormCheckbox('controlledProcesses', 'Controlled Processes', true, [])]),
        new ReportFormGroup('informationFlow', 'Information Flow', [
          new ReportFormCheckbox('controlActions', 'Control Actions', true, []),
          new ReportFormCheckbox('feedback', 'Feedback', true, []),
          new ReportFormCheckbox('inputs', 'Inputs', true, []),
          new ReportFormCheckbox('outputs', 'Outputs', true, [])]),
        new ReportFormCheckbox('responsibilities', 'Responsibilities', true, []),
      ]),
      new ReportFormGroup('step3', 'Step 3', [
        new ReportFormCheckbox('ucas', 'UCAs', true, []),
        new ReportFormCheckbox('controllerConstraints', 'Controller Constraints', true, [])
      ]),
      new ReportFormGroup('step4', 'Step 4', [
        new ReportFormCheckbox('processModels', 'Process Models', true, []),
        new ReportFormCheckbox('processVariables', 'Process Variables', true, []),
        new ReportFormCheckbox('controlAlgorithms', 'Control Algorithms', true, []),
        new ReportFormCheckbox('conversions', 'Conversions', true, []),
        new ReportFormCheckbox('lossScenariosImplementationConstraintDependency', 'Loss Scenarios', true, []),
        new ReportFormCheckbox('implementationConstraints', 'Implementation Constraints', true, [])
      ])
    ];
    // Initiates the tree of form elements
    let maxLayer = 0;
    let indexList: ReportFormElement[] = [];
    formStructure.forEach((element: ReportFormGroup) => {
      let eLayer = element.initiateTree(0, indexList, null);
      if (eLayer > maxLayer) {
        maxLayer = eLayer;
      }
    });
    // Announces the highest layer to all form elements
    formStructure.forEach((element: ReportFormGroup) => {
      element.announceMaxLayer(maxLayer);
    });
    /* Turns the list of all elements into a map, allowing for
    finding form elements by their name */
    this.indexMap = { };
    indexList.forEach((element: ReportFormElement) => {
      this.indexMap[element.name] = element;
    });

    this.formStructure = formStructure;
    return formStructure;
  }
}
