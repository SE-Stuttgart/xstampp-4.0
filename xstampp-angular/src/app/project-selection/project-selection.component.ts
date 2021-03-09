import { HttpErrorResponse } from '@angular/common/http';
import { DetailedField } from './../common/entities/detailed-field';
import { GroupDataService } from '../services/dataServices/group-data.service';
import { FilterService } from '../services/filter-service/filter.service';
import { MessageService } from 'primeng/api';
import { Component, OnInit, OnDestroy, ViewChild, Output, ChangeDetectorRef, ElementRef } from '@angular/core';
import { MainTableComponent } from '../common/main-table/main-table.component';
import { ColumnType, TableColumn } from '../common/entities/table-column';
import { FieldType } from '../common/entities/detailed-field';
import { ProjectDataService } from '../services/dataServices/project-data.service';
import { ProjectRequestDTO, ProjectResponseDTO, Theme } from '../types/local-types';
import { SheetMode } from '../common/detailed-sheet/detailed-sheet.component';
import { AuthService } from '../services/auth.service';
import { Router, ActivatedRoute } from '@angular/router';
import { DetailedSheetUtils } from '../common/detailed-sheet/utils/detailed-sheet-utils';
import { Ng4LoadingSpinnerService } from 'ng4-loading-spinner';
import { UserDataService } from '../services/dataServices/user-data.service';
import { ThemeDataService } from '../services/dataServices/theme-data.service';
import { MatMenuTrigger, MatDialog, MatDialogRef } from '@angular/material';
import { ImportExportDataService, JSONParent } from '../services/dataServices/import-export-data.service';
import { CloneProjectDialogComponent } from '../clone-project-dialog/clone-project-dialog.component';
import { ChangeDetectionService } from '../services/change-detection/change-detection-service.service';
import { SelectionModel } from '@angular/cdk/collections';
import { NO_LOGO, PROJECT_LOADING_FAILED, ERROR_LOADING_GROUP, EDIT_SUCCESSFUL, EDIT_FAILED, CREATE_SUCCESSFUL, CREATE_FAILED, EMPTY_FIELDS, IMPORT_FAILED, EXAMPLE_PROJECT_FAILED, DELETE_SUCCESSFUL } from '../globals';
import { Hotkeys } from '../hotkeys/hotkeys.service';
import { Subscription } from 'rxjs';
import { HotkeyCommandsComponent } from '../hotkeys/hotkey-commands.component';

@Component({
  selector: 'app-root',
  templateUrl: './project-selection.component.html',
  styleUrls: ['./project-selection.component.scss'],
  providers: [MainTableComponent]
})
export class ProjectSelectionComponent implements OnInit, OnDestroy {
  @ViewChild('logoBox', { static: true }) logoBoxRef: ElementRef<HTMLDivElement>;
  logoPath: string = '';
  logoExists: boolean = false;
  primaryColor: string = '';
  accentColor: string = '';
  warnColor = '';
  @ViewChild(MatMenuTrigger, { static: true }) trigger: MatMenuTrigger;
  @Output() ChangeDetectionService;
  SheetMode: typeof SheetMode = SheetMode;
  projects: ProjectResponseDTO[] = [];
  columns: Array<TableColumn>;
  project: ProjectRequestDTO;
  sheetMode: SheetMode = SheetMode.Closed;
  fields: DetailedField[];
  title: string = 'Projekt';
  userName: string;
  userId: string;
  userToken;
  private id: string;
  groups = [];
  selectedGroup;
  navigationTableDefinitions = [];
  sysAdmin: boolean = false;
  disableSaveButton: boolean = false;
  dashboard: boolean = false;
  file: File;
  importObject: JSONParent;
  fileContent: string = '';
  splitArray = [];
  fileType: string;
  sheetMode2: SheetMode = SheetMode.Closed;
  title2: string = 'Import Project';
  fields2;
  importproject;

  selectedAvatar: string = './../../assets/avatar/round default.svg';
  subscriptions: Subscription[] = [];

  private dialogRef: MatDialogRef<HotkeyCommandsComponent>;

  constructor(private readonly projectDataService: ProjectDataService,
    private readonly messageService: MessageService,
    private readonly authService: AuthService,
    private readonly router: Router,
    private route: ActivatedRoute,
    private readonly filterService: FilterService,
    private readonly cdr: ChangeDetectorRef,
    private readonly groupDataService: GroupDataService,
    private readonly spinnerService: Ng4LoadingSpinnerService,
    private readonly userDataService: UserDataService,
    private readonly themeDataService: ThemeDataService,
    private readonly importExportDataService: ImportExportDataService,
    public dialog: MatDialog,
    private readonly hotkeys: Hotkeys) {
    this.columns = [
      { title: 'Select', key: 'select', type: ColumnType.Checkbox, style: { width: '5%' } },
      { title: 'Open', key: 'project-selection', type: ColumnType.Project_Selection, style: { width: '5%' } },
      { title: 'Name', key: 'name', type: ColumnType.Text, style: { width: '24%' } },
      { title: 'Reference Number', key: 'referenceNumber', type: ColumnType.Text, style: { width: '10%' } },
      { title: 'Description', key: 'description', type: ColumnType.Text, style: { width: '50%' } },
      { title: 'Edit', key: 'edit', type: ColumnType.Button, style: { width: '3%' } },
      { title: 'Show', key: 'show', type: ColumnType.Button, style: { width: '3%' } }
    ];
    this.fields = [
      { title: 'ID', key: 'id', type: FieldType.Text, readonly: true },
      { title: 'Name', key: 'name', type: FieldType.Text, readonly: false },
      { title: 'Reference Number', key: 'referenceNumber', type: FieldType.Text, readonly: false },
      { title: 'Description', key: 'description', type: FieldType.Text_Variable, readonly: false, minRows: 3, maxRows: 10 },
    ];

    this.navigationTableDefinitions[0] = {
      // dataSource for Navigation only allows one Element
      dataSource: [],
      style: { width: '100%' },
      columnHeaderName: 'Your Groups'
    };
    this.spinnerService.show();

    // Decode UserToken to check for SysAdmin
    this.authService.getUserToken().then(value => {
      this.userToken = value;
      let parts: string[];
      if (this.userToken) {
        parts = this.userToken.split('.');
        this.userToken = JSON.parse(atob(parts[1]));
        if (this.userToken.isSystemAdmin === true) {
          this.sysAdmin = true;
        } else { this.sysAdmin = false; }
      }
    });

    this.themeDataService.getLogo().then((value: any) => {
      if (value !== null) {
        const doc = new DOMParser().parseFromString(value, 'image/svg+xml');
        doc.documentElement.setAttribute('height', '100%');

        this.logoBoxRef.nativeElement.appendChild(
          this.logoBoxRef.nativeElement.ownerDocument.importNode(doc.documentElement, true)
        );
      } else {
        this.messageService.add({ severity: 'warn', summary: NO_LOGO });
      }

    }).catch((error: HttpErrorResponse) => {
      console.log(NO_LOGO);
    });
  }

  ngOnInit(): void {
    this.userName = this.authService.getUserName();
    this.userId = this.authService.getUserID();
    this.userDataService.getTheme(this.userId).then((value: string) => {
      if (value !== null) {
        this.themeDataService.getThemeByID(value).then((value: Theme) => {
          if (value !== null) {
            console.log(value);
            let color: string = value.colors;
            let parts = color.split('_');
            this.primaryColor = parts[0];
            this.accentColor = parts[1];
            this.warnColor = parts[2];
            this.setColors();
          }
        });
      }
    });
    this.userDataService.getIcon(this.userId, this.userName).then(value => {
      if (value) {
        this.selectedAvatar = value;
      }

    });
    if (this.route.snapshot.paramMap.get('dashboard')) {
      this.dashboard = true;
    }

    this.loadData();
    let url: string = this.router.url.toLocaleLowerCase();
    this.subscriptions.push(this.hotkeys.addShortcut({ keys: 'shift.control.n', description: 'New project' }).subscribe(() => {
      this.createProject();
    }));

    this.subscriptions.push(this.hotkeys.addShortcut({ keys: 'shift.control.e', description: 'Import example project' }).subscribe(() => {
      this.exampleProject();
    }));
    this.subscriptions.push(this.hotkeys.addShortcut({ keys: 'shift.control.c', description: 'Copy existing project' }).subscribe(() => {
      this.copyProject();
    }));
  }

  loadData(): void {
    if (typeof this.selectedGroup !== 'undefined') {
      this.groupDataService.getAllProjectsForGroup(this.selectedGroup.id)
        .then(value => {
          this.projects = value;
          this.spinnerService.hide();
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: PROJECT_LOADING_FAILED, detail: response.message });
          this.spinnerService.hide();
        });
    } else {
      this.groupDataService.getAllGroupsOfUser(this.userId).then(value => {
        this.navigationTableDefinitions = [];
        this.navigationTableDefinitions[0] = { dataSource: value, style: { width: '100%' }, columnHeaderName: 'Your Groups' };
        if (value !== null && value.length > 0) {
          this.selectedGroup = value[0];
          this.loadData();
        }
        this.spinnerService.hide();
      }).catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: ERROR_LOADING_GROUP, detail: response.message });
        this.spinnerService.hide();
      });
    }
  }

  saveProject(data): void {
    this.filterService.ClearSelectionEmitter.emit(true);
    this.disableSaveButton = true;
    if (data.mode === SheetMode.EditWithLock) {
      if (this.project.name !== '' && this.project.referenceNumber !== '') {
        this.projectDataService.alterProject(data.ent, data.id).then(() => {
          this.messageService.add({ severity: 'success', summary: EDIT_SUCCESSFUL });
          this.closeSheet();
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: EDIT_FAILED, detail: response.message });
        }).finally(() => {
          this.disableSaveButton = false;
          this.loadData();
        });
      } else {
        this.messageService.add({ severity: 'error', summary: EMPTY_FIELDS });
        this.disableSaveButton = false;
      }
    } else if (data.mode === SheetMode.New) {
      if (this.project.name !== '' && this.project.referenceNumber !== '') {
        this.projectDataService.createProject(data.ent).then((value) => {
          this.loadData();
          this.messageService.add({ severity: 'success', summary: CREATE_SUCCESSFUL });
          this.closeSheet();
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: CREATE_FAILED, detail: response.message });
        }).finally(() => {
          this.disableSaveButton = false;
          this.loadData();
        });
      } else {
        this.messageService.add({ severity: 'error', summary: EMPTY_FIELDS });
        this.disableSaveButton = false;
      }
    }
  }

  /**
   * Method open the Detail Sheet after the user click at "new project"
   */
  createProject(): void {
    this.sheetMode = SheetMode.New;
    this.project = { id: '', description: '', name: '', referenceNumber: '', groupId: this.selectedGroup.id };
  }

  importProject(): void {
    this.sheetMode2 = SheetMode.New;

    this.importproject = { id: '', description: '', name: '', referenceNumber: '', groupId: this.selectedGroup.id };
  }

  /**
   *
   * @param event: from the button click Import existing project
   * Import projects from hasx4
   */
  fileChange(fileList: FileList): void {
    if (fileList === null) { return; }
    this.file = fileList[0];
    let fileReader: FileReader = new FileReader();
    fileReader.onloadend = (x) => {
      this.fileContent = fileReader.result as string;

      this.fileType = this.file.name.split('.').pop();
      this.importObject = {
        type: this.fileType,
        file: this.fileContent,

      };
      this.importProject();

    };

    fileReader.readAsText(this.file);
  }
  closeSheetImport(): void {
    this.sheetMode2 = SheetMode.Closed;
  }
  saveProjectImport(entity) {
    this.importObject.projectRequest = { name: entity.ent.name, description: entity.ent.description, referenceNumber: entity.ent.referenceNumber,
       groupId: this.selectedGroup.id, id: '1' };
    console.log(this.importObject);
    this.importExportDataService.importProject(this.importObject).then((value: ProjectResponseDTO) => {
      this.sheetMode2 = SheetMode.Closed;
      this.loadData();
    }).catch((reason: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: IMPORT_FAILED });
    });
  }

  /**
  * Copy existing project
  */
  copyProject(): void {
    const dialogRef = this.dialog.open(CloneProjectDialogComponent, {
      width: '300px',
      height: '200px',
      data: { selectedGroup: this.selectedGroup }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadData();
      }
    });
  }

  /**
   * Import example Project
   */
  exampleProject(): void {
    let project: ProjectRequestDTO = {
      id: '', name: '', groupId: this.selectedGroup.id, referenceNumber: '', description: ''
    };
    this.importExportDataService.exampleProject(project).then((value) => {
      this.loadData();
    }).catch((reason: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: EXAMPLE_PROJECT_FAILED, detail: reason.message });
    });
  }

  async deleteProjects(projects: ProjectResponseDTO[]): Promise<void> {
    console.log(projects);
    /* collect all responses from failed promises */
    const failedResponses: Error[] = [];

    for (const project of projects) {
      await this.projectDataService.deleteProject(project.id)
        .then(() => {
          // delete was successful
          // this.messageService.add({severity: 'success', summary: 'Deletion successfully'});
        }).catch((response: HttpErrorResponse) => {
          failedResponses.push(response);
          this.messageService.add({ severity: 'error', summary: 'Deleting project ' + project.name + ' failed', detail: response.message });
        }).finally(() => {
          this.loadData();
        });
    }

    if (failedResponses.length < projects.length) {
      /* some must have worked, we have fewer errors than requests */
      this.messageService.add({ severity: 'success', summary: DELETE_SUCCESSFUL });
    }
  }

  // Route to the group-settings view
  userSettings(): void {
    this.router.navigate(['/groups-handling/groups/']);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login/']);
  }

  onClickedNavigation(tupleOfElemAndTableNumber): void {
    this.filterService.ClearSelectionEmitter.emit(true);
    this.selectedGroup = tupleOfElemAndTableNumber[0];
    this.loadData();
  }

  /**
   * Toggles the DetailedSheet
   * 1. Click Edit-/View button or on table row --> onClickEdit(...)  of main-Table is called
   * 2. onClickEdit fires toggleEvent EventEmitter
   * 3. toggleEvent emitter triggers toggleSheet function in the .html-part of the component
   * 4. In toggleSheet function the detailed sheet content can be accessed (data is the selected element)
   * @param data: the data coming from the mainTable
   */
  toggleSheet(data): void {
    this.disableSaveButton = false;
    let id: string;
    if (this.project) {
      id = this.project.id;
    }
    this.sheetMode = DetailedSheetUtils.toggle(id, data.entity.id, this.sheetMode, data.mode);
    this.project = {
      id: data.entity.id,
      description: data.entity.description,
      name: data.entity.name,
      referenceNumber: data.entity.referenceNumber
    };
    this.id = data.entity.id;
  }

  closeSheet(): void {
    this.sheetMode = SheetMode.Closed;
    this.disableSaveButton = false;
  }

  editSheet(): void {
    this.sheetMode = SheetMode.Edit;
  }
  public setColors() {
    document.body.style.setProperty('--primary-color', this.primaryColor);
    document.body.style.setProperty('--accent-color', this.accentColor);
    document.body.style.setProperty('--warn-color', this.warnColor);
  }

  ngOnDestroy(): void {
    this.filterService.ClearSelectionEmitter.emit(true);
    this.subscriptions.forEach((subscription: Subscription) => subscription.unsubscribe());
  }

  shortcutHelp(): void {
    this.hotkeys.showCommands();
  }
}
