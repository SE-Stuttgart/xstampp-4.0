import { HttpErrorResponse } from '@angular/common/http';
import { DetailedSheetUtils } from './../common/detailed-sheet/utils/detailed-sheet-utils';
import { GroupDataService } from './../services/dataServices/group-data.service';
import { FilterService } from './../services/filter-service/filter.service';
import { Component, Inject, OnInit, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import { SheetMode } from '../common/detailed-sheet/detailed-sheet.component';
import { AdminPasswordChangeRequestDTO, PageRequest, UserDTO, Theme, LoginRequestDTO } from '../types/local-types';
import { ColumnType, TableColumn } from '../common/entities/table-column';
import { FieldType, DetailedField } from '../common/entities/detailed-field';
import { MessageService } from 'primeng/api';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { UserDataService, AdminUserdataRequestDTO } from '../services/dataServices/user-data.service';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material';
import { ChangePWService } from '../services/change-pw.service';
import { ThemeDataService } from '../services/dataServices/theme-data.service';
import { NO_LOGO, ERROR_LOADING_TABLE, DELETE_SUCCESSFUL, ERROR_DELETE_USER, EDIT_SUCCESSFUL, EDIT_FAILED, NAME_FIELD_EMPTY, CREATE_SUCCESSFUL, CREATE_FAILED, PASSWORD_CHANGE_SUCCESSFUL, ERROR_PASSWORD_CHANGE } from '../globals';

const ALL_GROUPS: string = 'All groups';
const ALL_GROUPS_WITHOUT_LEADER: string = 'Groups without leader';
const ALL_USERS: string = 'All users';

@Component({
  selector: 'app-system-administration',
  templateUrl: './system-administration.component.html',
  styleUrls: ['./system-administration.component.css']
})
export class SystemAdministrationComponent implements OnInit, OnDestroy {
  @ViewChild('logoBox', { static: true }) logoBoxRef: ElementRef<HTMLDivElement>;
  logoPath: string = '';
  SheetMode: typeof SheetMode = SheetMode;
  users: any[] = []; // TODO TYPING and current users are an array of users but as well an array of groups depending on the selected naviagtion
  columns: Array<TableColumn>;
  private user: UserDTO;
  sheetMode: SheetMode = SheetMode.Closed;
  isGroup: boolean = true;
  _fields_group: DetailedField[];
  _fields_user: DetailedField[];
  title: string = 'User';
  userName: string;
  userId: string;
  private id: string;
  groups: any[] = [];
  selectedUserId: string;
  dialogRef: MatDialogRef<ChangePWDialogComponent>;
  navigationTableDefinitions: any[] = [];
  group;
  sel: boolean = false;
  private disableSaveButton: Boolean = false;
  selectedAvatar: string = './../../assets/avatar/round default.svg';

  readonly NAVIGATION_SELECTION: { name: string }[] = [{ name: ALL_GROUPS }, { name: ALL_GROUPS_WITHOUT_LEADER }, { name: ALL_USERS }];
  selectedNavigation: { name: string };

  get fields(): DetailedField[] {
    return this.isGroup ? this._fields_group : this._fields_user;
  }

  constructor(private readonly messageService: MessageService,
    private readonly auth: AuthService,
    private readonly changePwService: ChangePWService,
    private readonly router: Router,
    private readonly userDataService: UserDataService,
    private groupDataService: GroupDataService,
    private readonly authService: AuthService,
    private readonly filterService: FilterService,
    public readonly dialog: MatDialog,
    private readonly themeDataService: ThemeDataService, ) {
    // Colums and Fields for table  entity of type User

    // this.columns = [
    //   {title: 'Select', key: 'select', type: ColumnType.Checkbox, style: {width: '5%'}},
    //   {title: 'Display Name', key: 'displayName', type: ColumnType.Text, style: {width: '25%'}},
    //   {title: 'E-Mail', key: 'email', type: ColumnType.Text, style: {width: '40%'}},
    //   {title: 'UserId', key: 'uid', type: ColumnType.Text, style: {width: '30%'}},
    //   {title: 'ResetPW', key: 'reset_pw', type: ColumnType.Button, style: {width: '3%'}}
    // ];
    // this.fields = [
    //   {title: 'ID', key: 'uid', type: FieldType.Text, readonly: true},
    //   {title: 'Display Name', key: 'displayName', type: FieldType.Text, readonly: false},
    //   {title: 'E-Mail', key: 'email', type: FieldType.Text, readonly: false}
    // ];

    // Colums and Filed for table entities of type Group Per default Groups is always displayed first therefore use these columns and fiels as initial
    // TODO Check how this can made more robust and reliable for future changes/exstensions what if someone changes that users are displayed first
    this.columns = [
      { key: 'select', title: 'Select', type: ColumnType.Checkbox, style: { width: '10%' } },
      { key: 'name', title: 'Group-Name', type: ColumnType.Text, style: { width: '81%' } },
      { key: 'edit', title: 'Edit', type: ColumnType.Button, style: { width: '3%' } },
      { key: 'show', title: 'Show', type: ColumnType.Button, style: { width: '3%' } },
      { key: 'members', title: 'Members', type: ColumnType.Button, style: { width: '3%' } }
    ];
    this._fields_group = [
      { title: 'Name', key: 'name', type: FieldType.Text, readonly: false },
      { title: 'Description', key: 'description', type: FieldType.Text_Variable, readonly: false, minRows: 3, maxRows: 10 }
    ];
    this._fields_user = [
      { title: 'ID', key: 'uid', type: FieldType.Text, readonly: true },
      { title: 'Display Name', key: 'displayName', type: FieldType.Text, readonly: false },
      { title: 'E-Mail', key: 'email', type: FieldType.Text, readonly: false }
    ];
    this.navigationTableDefinitions = [];
    this.navigationTableDefinitions[0] = { dataSource: this.NAVIGATION_SELECTION, style: { width: '100%' }, columnHeaderName: 'Navigation' };
    this.changePwService.ChangePWEmitter.subscribe(event => this.changePW(event));
    this.userDataService.getIcon(this.authService.getUserID(), this.userName).then((value: string) => {
      if (value) {
        this.selectedAvatar = value;
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
        this.messageService.add({ severity: 'warn', summary: NO_LOGO});

      }

  }).catch((error: HttpErrorResponse) => {
    console.log(NO_LOGO);
  });
  }

  ngOnInit(): void {
    this.userName = this.auth.getUserName();
    this.userId = this.auth.getUserID();
    this.userDataService.getTheme(this.authService.getUserID()).then((value: string) => {

      this.themeDataService.getThemeByID(value).then((themeValue: Theme) => {
        if (themeValue !== null) {
          let color: string = themeValue.colors;
          let parts: string[] = color.split('_');
          document.body.style.setProperty('--primary-color', parts[0]);
          document.body.style.setProperty('--accent-color', parts[1]);
          document.body.style.setProperty('--warn-color', parts[2]);
        }
      });


    });
    this.loadData();
  }

  loadData(): void {
    const page: PageRequest = { from: 0, amount: 100 }; // TODO use consistent values in all components
    const promises = [];
    if (this.selectedNavigation === undefined || this.selectedNavigation.name === ALL_GROUPS) {
      promises.push(this.groupDataService.getAllGroupsSysAdmin(page));
    } else if (this.selectedNavigation.name === ALL_GROUPS_WITHOUT_LEADER) {
      promises.push((this.groupDataService.getAllGroupsWithoutLeaderSysAdmin(page)));
    } else {
      promises.push(this.userDataService.getAllUsers(page));
    }
    Promise.all(promises)
      .then((value) => {
        this.users = value[0];
      }).catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: ERROR_LOADING_TABLE, detail: response.message });
      });
  }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login/']);
  }

  // TODO currently users and groups are treated same therefore deleteUsers === deleteGroups because it delete everything which was selected in the table
  // which is defined by the variable this.users, but this.user sometimes is an array of Users and sometimes and array of Groups
  deleteUsers($event): Promise<boolean> {
    const list: any[] = $event;
    if (list.length === 0) {
      return;
    }
    if (this.selectedNavigation.name === ALL_USERS) {
      Promise.all(list.map((elem: any) => {
        return this.userDataService.deleteUser(elem.uid);
      })).then(() => {
        this.messageService.add({ severity: 'success', summary: DELETE_SUCCESSFUL });
      }).catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: ERROR_DELETE_USER, detail: response.message });
      }).finally(() => {
        this.loadData();
      });
    } else {
      Promise.all(list.map((elem: any) => {
        return this.groupDataService.deleteGroup(elem.id);
      })).then(() => {
        this.messageService.add({ severity: 'success', summary: DELETE_SUCCESSFUL });
      }).catch((response: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: ERROR_DELETE_USER, detail: response.message });
      }).finally(() => {
        this.loadData();
      });
    }
  }

  onClickedNavigation(tupleOfElemAndTableNumber): void {
    this.filterService.ClearSelectionEmitter.emit(true);
    this.selectedNavigation = tupleOfElemAndTableNumber[0];
    this.users = [];
    if (this.selectedNavigation.name === ALL_USERS) {
      this.columns = [
        { title: 'Select', key: 'select', type: ColumnType.Checkbox, style: { width: '5%' } },
        { title: 'Display Name', key: 'displayName', type: ColumnType.Text, style: { width: '25%' } },
        { title: 'E-Mail', key: 'email', type: ColumnType.Text, style: { width: '40%' } },
        { title: 'UserId', key: 'uid', type: ColumnType.Text, style: { width: '30%' } },
        { title: 'ResetPW', key: 'reset_pw', type: ColumnType.Button, style: { width: '3%' } }
      ];
      this.isGroup = false;
      // this fields are currently not used for users because we currently have now funtions to edit them maybe in the future it's possible to change name,... etc
    } else {
      this.columns = [
        { key: 'select', title: 'Select', type: ColumnType.Checkbox, style: { width: '10%' } },
        { key: 'name', title: 'Group-Name', type: ColumnType.Text, style: { width: '81%' } },
        { key: 'edit', title: 'Edit', type: ColumnType.Button, style: { width: '3%' } },
        { key: 'show', title: 'Show', type: ColumnType.Button, style: { width: '3%' } },
        { key: 'members', title: 'Members', type: ColumnType.Button, style: { width: '3%' } }
      ];
      this.isGroup = true;
    }
    this.loadData();
  }

  // Detailed sheet logic ....................
  createGroup(): void {
    this.sheetMode = SheetMode.New;
    this.group = { name: '', description: '' };
  }

  saveGroup(group): void {
    this.filterService.ClearSelectionEmitter.emit(true);
    this.disableSaveButton = true;
    if (group.mode === SheetMode.EditWithLock) {
      if (this.group.name === undefined) {
        let user: AdminUserdataRequestDTO = { displayName: group.ent.displayName, email: group.ent.email };
        this.userDataService.updateDisplayNameAndEmail(group.ent.uid, user).then((value: boolean) => {
          this.messageService.add({ severity: 'sucess', summary: 'User updated successfully' });
          this.loadData();
          this.closeSheet();
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: 'Update Userdata failed', detail: response.message });
        });
      }
      else if (this.group.name !== '') {
        const groupEntity: {
          name: string,
          description: string
        } = { name: group.ent.name, description: group.ent.description };
        this.groupDataService.alterGroup(group.id, groupEntity)
          .then(() => {
            this.messageService.add({ severity: 'success', summary: EDIT_SUCCESSFUL });
            this.closeSheet();
          }).catch((response: HttpErrorResponse) => {
            this.messageService.add({ severity: 'error', summary: EDIT_FAILED, detail: response.message });
          }).finally(() => {
            this.disableSaveButton = false;
            this.loadData();
          });
      } else {
        this.messageService.add({ severity: 'warning', summary: NAME_FIELD_EMPTY });
        this.disableSaveButton = false;
      }
    } else if (group.mode === SheetMode.New) {
      if (this.group.name !== '') {
        this.groupDataService.createGroup(group.ent)
          .then(() => {
            this.messageService.add({ severity: 'success', summary: CREATE_SUCCESSFUL });
            this.closeSheet();
          }).catch((response: HttpErrorResponse) => {
            this.messageService.add({ severity: 'error', summary: CREATE_FAILED, detail: response.message });
          }).finally(() => {
            this.disableSaveButton = false;
            this.loadData();
          });
      } else {
        this.messageService.add({ severity: 'warning', summary: NAME_FIELD_EMPTY });
        this.disableSaveButton = false;

      }
    }

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
    console.log(data)
    if (this.isGroup) {
      if (this.group) {
        id = this.group.id;
      }
      this.sheetMode = DetailedSheetUtils.toggle(id, data.entity.id, this.sheetMode, data.mode);
      this.group = {
        id: data.entity.id, description: data.entity.description, name: data.entity.name,
      };
    }
    if (!this.isGroup) {
      this.sheetMode = DetailedSheetUtils.toggle(id, data.entity.id, this.sheetMode, data.mode);
      this.group = {
        uid: data.entity.uid, email: data.entity.email, displayName: data.entity.displayName,
      };
    }
  }

  closeSheet(): void {
    this.sheetMode = SheetMode.Closed;
    this.disableSaveButton = false;
  }

  editSheet(): void {
    this.sheetMode = SheetMode.Edit;
  }

  ngOnDestroy(): void {
    this.filterService.ClearSelectionEmitter.emit(true);
  }

  // Password diialog logic ....................
  openChangePWDialog(userId: string): void {
    this.selectedUserId = userId;
    this.dialogRef = this.dialog.open(ChangePWDialogComponent, {
      width: '275px',
      data: { description: 'Change user\'s password' }
    });
  }

  changePW(pwChangeRequest: AdminPasswordChangeRequestDTO): void {
    this.userDataService.setNewUserPassword(pwChangeRequest, this.selectedUserId).then(() => {
      this.messageService.add({ severity: 'success', summary: PASSWORD_CHANGE_SUCCESSFUL });
      this.dialogRef.close();
    }).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: ERROR_PASSWORD_CHANGE, detail: response.message });
    });
  }
  dropMenu() {
    this.sel = true;
  }

}

@Component({
  selector: 'app-change-pw-dialogs',
  templateUrl: './change-pw-dialog.component.html',
  styleUrls: ['./system-administration.component.css']
})
export class ChangePWDialogComponent {

  password: string;
  passwordRepeat: string;
  adminPassword: string;

  constructor(public changePWService: ChangePWService, public dialogRef: MatDialogRef<ChangePWDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) {
  }

  onCancelClick(): void {
    this.dialogRef.close();
  }

  onChangeClick(): void {
    this.changePWService.ChangePWEmitter.emit({
      adminPassword: this.adminPassword,
      newPassword: this.password,
      newPasswordRepeat: this.passwordRepeat
    });
  }

}
