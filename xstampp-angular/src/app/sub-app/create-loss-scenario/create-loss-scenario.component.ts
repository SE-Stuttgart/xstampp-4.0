import { HttpErrorResponse } from '@angular/common/http';
import { Component, Inject } from '@angular/core';
import { MatDialog, MatDialogRef, MatSelectChange, MAT_DIALOG_DATA } from '@angular/material';
import { MessageService } from 'primeng/api';
import { SheetMode } from 'src/app/common/detailed-sheet/detailed-sheet.component';
import { DetailedSheetUtils } from 'src/app/common/detailed-sheet/utils/detailed-sheet-utils';
import { DetailedField } from 'src/app/common/entities/detailed-field';
import { BoxDataService } from 'src/app/services/dataServices/box-data.service';
import { ControlActionDataService } from 'src/app/services/dataServices/control-structure/control-action-data.service';
import { ControllerDataService } from 'src/app/services/dataServices/control-structure/controller-data.service';
import { FeedbackDataService } from 'src/app/services/dataServices/control-structure/feedback-data.service';
import { InputDataService } from 'src/app/services/dataServices/control-structure/input-data.service';
import { SensorDataService } from 'src/app/services/dataServices/control-structure/sensor-data.service';
import { LockResponse, LockService } from 'src/app/services/dataServices/lock.service';
import { LossScenarioDataService } from 'src/app/services/dataServices/loss-scenario.service';
import { BoxEntityDTO, ControlActionDTO, ControllerDTO, FeedbackDTO, HazardResponseDTO, InputDTO, LockRequestDTO, LossScenarioRequestDTO, LOSS_SCENARIO, SensorDTO, UnlockRequestDTO } from 'src/app/types/local-types';
import { ChangeDetectionService } from 'src/app/services/change-detection/change-detection-service.service';
import { CDSAddObject, CDSReset, CDSCanExecute } from 'src/app/services/change-detection/change-detection-decorator';
import { SaveActionDialogComponent, SaveActions } from 'src/app/common/save-action-dialog/save-action-dialog.component';
import { DETAILEDSHEET_SUCCESSFUL_EDIT, DETAILEDSHEET_FAILED_EDIT, DETAILEDSHEET_SUCCESSFUL_CREATION, DETAILEDSHEET_FAILED_CREATION } from 'src/app/globals';

interface UcaForDetailedSheet {
  id: string;
  name: string;
  description: string;
  parentId: string;
  parentName: string;
  category: any[];
  haz_links: HazardResponseDTO[];
  allLinksForAutocomplete: UcaLinksForAutoComplete;
}

interface UcaLinksForAutoComplete {
  haz_links: HazardResponseDTO[];
}

/**
 * This class is used to keep track of all strings displayed in Dropdown Menus
 */
class Categories {

  // Head Categories
  static readonly head_category1: string = 'Failures related to the controller (for physical controllers)';
  static readonly head_category2: string = 'Inadequate control algorithm';
  static readonly head_category3: string = 'Unsafe control input';
  static readonly head_category4: string = 'Inadequate process model';

  // Sub Categories
  static readonly sub_category1_1: string = 'No sub category available';

  static readonly sub_category2_1: string = 'Flawed implementation of the specified control algorithm';
  static readonly sub_category2_2: string = 'The specified control algorithm is flawed';
  static readonly sub_category2_3: string = 'The specified control algorithm becomes inadequate over time due to changes or degradation';
  static readonly sub_category2_4: string = 'The control algorithm is changed by an attacker';

  static readonly sub_category3_1: string = 'UCA received from another controller (already addressed when considering UCAs from other controllers)';

  static readonly sub_category4_1: string = 'Controller receives incorrect feedback/information ';
  static readonly sub_category4_2: string = 'Controller receives correct feedback/information but interprets it incorrectly or ignores it';
  static readonly sub_category4_3: string = 'Controller does not receive feedback/information when needed (delayed or never received)';

  // Reasons
  static readonly reason_1_1: string = 'Feedback/info sent by sensors(s) but not received by controller';
  static readonly reason_1_2: string = 'Feedback/info is not sent by sensor(s) but is received or applied to sensor(s)';
  static readonly reason_1_3: string = 'Feedback/info is not received or applied to sensor(s)';
  static readonly reason_1_4: string = 'Feedback/info does not exist in control structure or sensor(s) do not exist';

  static readonly reason_3_1: string = 'Sensor(s) respond adequately but controller receives inadequate feedback/info';
  static readonly reason_3_2: string = 'Sensor(s) respond inadequately to feedback/info that is received or applied to sensor(s)';
  static readonly reason_3_3: string = 'Sensor(s) are not capable or not designed to provide necessary feedback/info';
}

@Component({
  selector: 'app-create-loss-scenario',
  templateUrl: './create-loss-scenario.component.html',
  styleUrls: ['./create-loss-scenario.component.css']
})

/**
 * Class witch the mat-dialog was open with the different elements from the speciall categories.
 * Here also the scenario was saved
 */
export class CreateLossScenarioComponent {

  private projectId: string;
  fields: DetailedField[];
  secondDropDownID: number;
  reason: string[];
  beginDropDownID: boolean = true;
  firstDropDownID: number;

  headCategoryString: string = '';
  subCategoryString: string = '';

  selectedControlActionFromTable: ControlActionDTO;
  selectedUcaFromTable: UcaForDetailedSheet;

  SheetMode: typeof SheetMode = SheetMode;
  disableSaveButton: boolean = false;

  title: string = DetailedSheetUtils.generateSheetTitle(LOSS_SCENARIO);

  // Attributes were used for loading the information from the backend to the frontend
  allControllers: ControllerDTO[];
  allControlActionsByController: ControlActionDTO[];
  allControlActions: ControlActionDTO[];
  allInput: InputDTO[];
  allFeedback: FeedbackDTO[];
  allSensor: SensorDTO[];
  allInputBox: BoxEntityDTO[];

  sheetMode: SheetMode;

  @CDSAddObject() lossScenarioEntity: LossScenarioRequestDTO;

  constructor(
    public dialog: MatDialog,
    public dialogRef: MatDialogRef<CreateLossScenarioComponent>,
    private readonly controllerDataService: ControllerDataService,
    private readonly controlActionDataService: ControlActionDataService,
    private readonly feedbackDataService: FeedbackDataService,
    private readonly sensorDataService: SensorDataService,
    private readonly inputDataService: InputDataService,
    private readonly lossScenarioDataService: LossScenarioDataService,
    private readonly messageService: MessageService,
    private readonly boxDataService: BoxDataService,
    private readonly lockService: LockService,
    private readonly cds: ChangeDetectionService,
    @Inject(MAT_DIALOG_DATA) public data,
  ) {
    dialogRef.disableClose = true;

    // The data which we receive from the loss-scenario-table

    this.selectedControlActionFromTable = data.selectedControlAction;
    this.selectedUcaFromTable = data.selectedUca;
    this.projectId = data.projectId;
    this.sheetMode = data.sheetMode;

    if (this.sheetMode === SheetMode.Edit) {
      this.getLock(data.lossScenario);
    }
    this.lossScenarioEntity = data.lossScenario;
    this.disableSaveButton = false;

    // Check if the SheetMode is edit and data must be loaded
    if (this.sheetMode === SheetMode.EditWithLock || this.sheetMode === SheetMode.View || this.sheetMode === SheetMode.Edit) {
      let event: MatSelectChange = {
        source: undefined,
        value: this.lossScenarioEntity.headCategory,
      };

      this.firstDropDownChanged(event, false);

      if (this.lossScenarioEntity.subCategory !== '') {
        event.value = this.lossScenarioEntity.subCategory;
        this.secondDropDownChanged(event, false);
      }
    }
  }

  // The four head categories
  public headCategory: string[] = [
    // firstDropDownID 0,1,2,3 -> index of array
    Categories.head_category1,
    Categories.head_category2,
    Categories.head_category3,
    Categories.head_category4
  ];
  public subCategory: string[];

  /**
   * Depending on which category is displayed in the first dropdown,
     *  the id is set first and then filled with certain ids the array subCategory,
     *  which will read out in the dropdown
   * @param event from the first dropdown with the head categorie
   */
  firstDropDownChanged(event: MatSelectChange, deleteFields: boolean = true): void {
    const headcategory: string = event.value;

    if (deleteFields) {
      // Reset all fields if user changes dropdown selection
      this.subCategoryString = '';
      this.deleteFieldsFromLossScenarioEntity(headcategory, '');
    }

    if (!headcategory || headcategory.trim() === '') {
      return;
    }

    this.headCategoryString = headcategory;
    this.secondDropDownID = 0;

    if (headcategory.trim().toLowerCase() === Categories.head_category1.trim().toLowerCase()) {
      this.subCategory = [
        Categories.sub_category1_1
      ];
      this.firstDropDownID = 0;
      this.beginDropDownID = true;
      this.secondDropDownID = 0;
    } else if (headcategory.trim().toLowerCase() === Categories.head_category2.trim().toLowerCase()) {
      this.subCategory = [
        // secondDropDownID: 10,11,12,13
        Categories.sub_category2_1,
        Categories.sub_category2_2,
        Categories.sub_category2_3,
        Categories.sub_category2_4
      ];
      this.firstDropDownID = 1;
      this.beginDropDownID = false;
    } else if (headcategory.trim().toLowerCase() === Categories.head_category3.trim().toLowerCase()) {
      this.subCategory = [
        // secondDropDownID: 20
        Categories.sub_category3_1
      ];
      this.firstDropDownID = 2;
      this.beginDropDownID = false;
    } else if (headcategory.trim().toLowerCase() === Categories.head_category4.trim().toLowerCase()) {
      this.subCategory = [
        // secondDropDownID: 30,31,32
        Categories.sub_category4_1,
        Categories.sub_category4_2,
        Categories.sub_category4_3
      ];
      this.firstDropDownID = 3;
      this.beginDropDownID = false;
    }
    this.loadAllController();
    this.loadAllControlActions();
    this.loadFeeback();
    this.loadInputBox();
    this.loadSensor();
    this.laodInput();
  }

  /**
   * Depending on which category is displayed in the second dropdown,
   *  the id is set first and then filled with certain ids the array ground,
   *  which will read out in the dropdown
   * @param event from the second dropdown
   */
  secondDropDownChanged(event: MatSelectChange, deleteFields: boolean = true): void {
    this.secondDropDownID = this.headCategory.indexOf(this.headCategoryString) * 10 + this.subCategory.indexOf(event.value);
    this.lossScenarioEntity.subCategory = event.value;

    this.subCategoryString = event.value;

    if (deleteFields) {
      // Reset all fields if user changes dropdown selection
      this.deleteFieldsFromLossScenarioEntity(this.lossScenarioEntity.headCategory, this.lossScenarioEntity.subCategory);
    }

    if (this.headCategoryString.trim().toLowerCase() === Categories.head_category4.trim().toLowerCase() && this.subCategoryString.trim().toLowerCase() === Categories.sub_category4_1.trim().toLowerCase()) {
      this.reason = [
        // Reasons: 300,301,302,303 -> last digit represents index of array
        Categories.reason_1_1,
        Categories.reason_1_2,
        Categories.reason_1_3,
        Categories.reason_1_4
      ];
    } else if (this.headCategoryString.trim().toLowerCase() === Categories.head_category4.trim().toLowerCase() && this.subCategoryString.trim().toLowerCase() === Categories.sub_category4_3.trim().toLowerCase()) {
      // Reasons 321,322,323  -> last digit represents index of array
      this.reason = [
        Categories.reason_3_1,
        Categories.reason_3_2,
        Categories.reason_3_3
      ];
    }
  }

  /**
   * Validates that every field that is loaded in the given category is filled
   * @param headCategory The selected headcategory
   * @param subCategory The selected subcategory
   */
  private validateForm(headCategory: string, subCategory: string): boolean {
    // Check whether name & description is set
    if (!this.isValidString(this.lossScenarioEntity.name) || !this.isValidString(this.lossScenarioEntity.description)) {
      return false;
    }

    // Head Category 1
    if (headCategory === Categories.head_category1) {

      return this.isValidString(this.lossScenarioEntity.controller1Id) && this.isValidString(this.lossScenarioEntity.description1);
    } else if (headCategory === Categories.head_category2) {

      // Sub Category 1 and 2
      if (subCategory === Categories.sub_category2_1 || subCategory === Categories.sub_category2_2) {

        return this.isValidString(this.lossScenarioEntity.controller1Id) && this.isValidString(this.lossScenarioEntity.controlActionId)
          && this.isValidString(this.lossScenarioEntity.description1);
      } else if (subCategory === Categories.sub_category2_3) {

        return this.isValidString(this.lossScenarioEntity.controller1Id) && this.isValidString(this.lossScenarioEntity.controlActionId)
          && this.isValidString(this.lossScenarioEntity.description1) && this.isValidString(this.lossScenarioEntity.description2);
      } else if (subCategory === Categories.sub_category2_4) {

        return this.isValidString(this.lossScenarioEntity.controller1Id) && this.isValidString(this.lossScenarioEntity.controlActionId)
          && this.isValidString(this.lossScenarioEntity.description1) && this.isValidString(this.lossScenarioEntity.description3)
          && this.isValidString(this.lossScenarioEntity.description2);
      }
    } else if (headCategory === Categories.head_category3 && subCategory === Categories.sub_category3_1) {

      return this.isValidString(this.lossScenarioEntity.controlActionId) && this.isValidString(this.lossScenarioEntity.controller1Id)
        && this.isValidString(this.lossScenarioEntity.controller2Id);
    } else if (headCategory === Categories.head_category4) {
      // Sub Category 1
      if (subCategory === Categories.sub_category4_1) {

        return this.isValidString(this.lossScenarioEntity.controller1Id) && this.isValidString(this.lossScenarioEntity.inputArrowId)
          && this.isValidString(this.lossScenarioEntity.feedbackArrowId) && this.isValidString(this.lossScenarioEntity.sensorId)
          && this.isValidString(this.lossScenarioEntity.reason) && this.isValidString(this.lossScenarioEntity.description2);
      } else if (subCategory === Categories.sub_category4_2) {

        return this.isValidString(this.lossScenarioEntity.controller1Id) && this.isValidString(this.lossScenarioEntity.inputArrowId)
          && this.isValidString(this.lossScenarioEntity.feedbackArrowId) && this.isValidString(this.lossScenarioEntity.inputBoxId)
          && this.isValidString(this.lossScenarioEntity.sensorId) && this.isValidString(this.lossScenarioEntity.description1)
          && this.isValidString(this.lossScenarioEntity.description2);
      } else if (subCategory === Categories.sub_category4_3) {

        return this.isValidString(this.lossScenarioEntity.controller1Id) && this.isValidString(this.lossScenarioEntity.inputArrowId)
          && this.isValidString(this.lossScenarioEntity.feedbackArrowId) && this.isValidString(this.lossScenarioEntity.inputBoxId)
          && this.isValidString(this.lossScenarioEntity.sensorId) && this.isValidString(this.lossScenarioEntity.description1)
          && this.isValidString(this.lossScenarioEntity.reason) && this.isValidString(this.lossScenarioEntity.description2);
      }
    }

    return false;
  }

  /**
   * Checks whether the given string is not undefined and not empty
   * @param value The string to check
   */
  private isValidString(value: string): boolean {
    return !!value && String(value).trim().length > 0;
  }

  /**
   * Clears the fields in 2. input column (used when user changes dropdowns & new fields are loaded)
   * @param headCategory The new headcategory
   * @param subCategory The new subcategory
   */
  deleteFieldsFromLossScenarioEntity(headCategory: string, subCategory: string): void {
    this.lossScenarioEntity = {
      name: this.lossScenarioEntity.name,
      id: this.lossScenarioEntity.id,
      description: this.lossScenarioEntity.description,
      projectId: this.lossScenarioEntity.projectId,
      ucaId: this.lossScenarioEntity.ucaId,
      state: this.lossScenarioEntity.state,
      headCategory: headCategory,
      subCategory: subCategory,
      controller1Id: undefined,
      controller2Id: undefined,
      description1: undefined,
      description2: undefined,
      description3: undefined,
      controlActionId: undefined,
      inputArrowId: undefined,
      feedbackArrowId: undefined,
      inputBoxId: undefined,
      sensorId: undefined,
      reason: undefined,
    };
  }

  /**
   * Saves the given loss scenario in backend
   * @param data: The loss scenario to save
   */
  @CDSReset()
  save(data: LossScenarioRequestDTO): void {
    if (!this.validateForm(this.lossScenarioEntity.headCategory, this.lossScenarioEntity.subCategory)) {
      this.messageService.add({ severity: 'error', summary: 'Edit failed', detail: 'Please fill out all required fields' });
      this.disableSaveButton = false;
      return;
    }

    // Copy all information from data object to LossScenarioRequestDTO
    // object and send to backend
    this.disableSaveButton = true;

    let lsEntity: LossScenarioRequestDTO = {
      name: data.name,
      id: data.id,
      description: data.description,
      projectId: data.projectId,
      ucaId: this.selectedUcaFromTable.id,
      state: data.state,
      headCategory: data.headCategory,
      subCategory: data.subCategory,
      controller1Id: data.controller1Id,
      controller2Id: data.controller2Id,
      // controlAlgorithm: ???
      description1: data.description1,
      description2: data.description2,
      description3: data.description3,
      controlActionId: this.selectedControlActionFromTable.id,
      inputArrowId: data.inputArrowId,
      feedbackArrowId: data.feedbackArrowId,
      inputBoxId: data.inputBoxId,
      sensorId: data.sensorId,
      reason: data.reason,
    };

    if (this.sheetMode === SheetMode.EditWithLock) {
      const promises = [];
      promises.push(this.lossScenarioDataService.alterLossScenario(this.projectId, data.id, lsEntity));

      Promise.all(promises)
        .then(() => {
          this.messageService.add({ severity: 'success', summary: DETAILEDSHEET_SUCCESSFUL_EDIT });
          this.dialogRef.close();
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: DETAILEDSHEET_FAILED_EDIT, detail: response.message });
        });

    } else if (this.sheetMode === SheetMode.New) {
      this.lossScenarioDataService.createLossScenario(this.projectId, lsEntity)
        .then(() => {
          this.messageService.add({ severity: 'success', summary: DETAILEDSHEET_SUCCESSFUL_CREATION});
          this.dialogRef.close();
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: DETAILEDSHEET_FAILED_CREATION, detail: response.message });
        });
    }

    this.disableSaveButton = false;
  }

  /**
   * load all controller from the project
   */
  loadAllController(): void {
    this.controllerDataService.getAllControllers(this.projectId, { })
      .then((value: ControllerDTO[]) => {
        this.allControllers = value;
      }).catch((reason: HttpErrorResponse) => {
        // handle error
        console.log(reason);
      });
  }

  /**
   * Load all control actions from the project
   */
  loadAllControlActions(): void {
    this.controlActionDataService.getAllControlActions(this.projectId, { })
      .then((value: ControlActionDTO[]) => {
        this.allControlActions = value;
      }).catch((reason: HttpErrorResponse) => {
        // handle error
        console.log(reason);
      });
  }

  /**
   * load all control action by the selected controller
   */
  loadAllControlActionsByController(selectedController: ControllerDTO): void {
    this.controlActionDataService.getControlActionBySourceBoxId(this.projectId, selectedController.boxId)
      .then((value: ControlActionDTO[]) => {
        this.allControlActionsByController = value;
      }).catch((reason: HttpErrorResponse) => {
        // handle error
        console.log(reason);
      });
  }

  /**
   * load all input
   */
  laodInput(): void {
    this.inputDataService.getAllInputs(this.projectId, { })
      .then(value => {
        this.allInput = value;
      }).catch((reason: HttpErrorResponse) => {
        // handle error
        console.log(reason);
      });
  }

  /**
   * load all feedback
   */
  loadFeeback(): void {
    this.feedbackDataService.getAllFeedbacks(this.projectId, { })
      .then(value => {
        this.allFeedback = value;
      }).catch((reason: HttpErrorResponse) => {
        // handle error
        console.log(reason);
      });
  }

  /**
   * load all sensor from the project in the attribute all Sensor
   */
  loadSensor(): void {
    this.sensorDataService.getAllSensors(this.projectId, { })
      .then(value => {
        this.allSensor = value;
      }).catch((reason: HttpErrorResponse) => {
        // handle error
        console.log(reason);
      });
  }

  /**
   * load all the input boxes from the project in the attribute allInputBx
   */
  loadInputBox(): void {
    this.boxDataService.getAllInputBoxes(this.projectId)
      .then(value => {
        this.allInputBox = value;
      }).catch((reason: HttpErrorResponse) => {
        // handle error
        console.log(reason);
      });
  }

  /**
   * Called when the user presses edit button when in view mode
   */
  edit(): void {
    this.sheetMode = SheetMode.Edit;
    this.getLock(this.lossScenarioEntity);
  }

  /**
   * Called when the user cancel the dialog
   */
  @CDSCanExecute('showDialog')
  onCancel(): void {
    this.closeSheet();
  }

  showDialog(): void {
    const dialogRef = this.dialog.open(SaveActionDialogComponent, {
      width: '450px',
      disableClose: true,
    });

    dialogRef.afterClosed().subscribe((result: SaveActions): void => {
      switch (result) {
        case SaveActions.CANCEL: {
          break;
        }
        case SaveActions.CLOSE: {
          this.cds.resetStates();
          this.closeSheet();
          break;
        }
        case SaveActions.SAVE_AND_CLOSE: {
          this.save(this.lossScenarioEntity);
          break;
        }
      }
    });
  }

  async getLock(lossScenario: LossScenarioRequestDTO): Promise<void> {
    const expires: string = DetailedSheetUtils.calculateLockExpiration();

    let lockDTO: LockRequestDTO = {
      id: lossScenario.id,
      expirationTime: expires,
      entityName: this.title.toLowerCase()
    };

    await this.lockService.lockEntity(this.projectId, lockDTO)
      .then((success: LockResponse) => {
        this.sheetMode = SheetMode.EditWithLock;
        console.log(success);
      }).catch((error: Error) => {
        this.sheetMode = SheetMode.View;
        this.messageService.add({ severity: 'error', summary: 'Error on getting lock', life: 4000, detail: error.message });
        console.error(error);
      });
  }

  /**
   * Unlocks the opened loss scenario entity and then closes the detailed window
   */
  closeSheet(): void {
    if (this.sheetMode === SheetMode.EditWithLock) {
      const unlockDTO: UnlockRequestDTO = { id: this.lossScenarioEntity.id, entityName: this.title.toLowerCase() };
      this.lockService.unlockEntity(this.projectId, unlockDTO).then(() => {
        this.sheetMode = SheetMode.Closed;
      }).catch((error: Error) => {
        this.sheetMode = SheetMode.Closed;
        this.messageService.add({ severity: 'error', summary: 'Error on unlocking entity', life: 4000, detail: error.message });
        console.error(error);
      });
    } else {
      this.sheetMode = SheetMode.Closed;
    }
    this.disableSaveButton = false;
    this.dialog.closeAll();
  }
}
