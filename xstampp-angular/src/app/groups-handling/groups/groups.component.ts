import { HttpErrorResponse } from '@angular/common/http';
import { Component, ElementRef, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { Subscription } from 'rxjs';
import { ThemeDataService } from 'src/app/services/dataServices/theme-data.service';
import { UserDataService } from 'src/app/services/dataServices/user-data.service';
import { SheetMode } from '../../common/detailed-sheet/detailed-sheet.component';
import { DetailedSheetUtils } from '../../common/detailed-sheet/utils/detailed-sheet-utils';
import { Hotkeys } from 'src/app/hotkeys/hotkeys.service';
import { DetailedField, FieldType } from '../../common/entities/detailed-field';
import { ColumnType, TableColumn } from '../../common/entities/table-column';
import { FilterService } from '../../services/filter-service/filter.service';
import { AuthService } from './../../services/auth.service';
import { BlockingGroupResponseDTO, GroupDataService, GroupResponseDTO } from './../../services/dataServices/group-data.service';
import { NO_LOGO, DELETE_SUCCESSFUL, ERROR_DELETE_GROUP, EDIT_SUCCESSFUL, EDIT_FAILED, NAME_FIELD_EMPTY, CREATE_SUCCESSFUL, CREATE_FAILED, ERROR_LOADING_GROUP } from 'src/app/globals';

@Component({
  selector: 'app-groups',
  templateUrl: './groups.component.html',
  styleUrls: ['./groups.component.css']
})
export class GroupsComponent implements OnInit, OnDestroy {
  @ViewChild('logoBox', { static: true}) logoBoxRef: ElementRef<HTMLDivElement>;
  subscriptions: Subscription = new Subscription();

  @Input() stateNotNecessary: boolean;
  SheetMode: typeof SheetMode = SheetMode;
  groups: GroupResponseDTO[] = [];
  columns: TableColumn[] = [];
  group: {
    id?: string,
    name: string,
    description: string
  };

  deletionState: boolean = false;

  primaryColor: string = '';
  accentColor: string = '';
  warnColor = '';
  sheetMode: SheetMode = SheetMode.Closed;
  fields: DetailedField[];
  title: string = 'Group';
  userName: string;
  userToken: any;
  private memberID: string;
  sysAdmin: boolean = false;
  disableSaveButton: boolean = false;
  showBlocks: boolean = false;
  blockingGroupsLists: BlockingGroupResponseDTO;
  selectedAvatar: string = './../../assets/avatar/round default.svg';
  logoPath: string = '';
  subscriptionHotkey: Subscription[] = [];

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly filterService: FilterService,
    private readonly messageService: MessageService,
    private readonly groupDataService: GroupDataService,
    private readonly authService: AuthService,
    private readonly userDataService: UserDataService,
    private readonly themeDataService: ThemeDataService,
    private readonly hotkeys: Hotkeys) {

    this.fields = [
      { title: 'Name', key: 'name', type: FieldType.Text, readonly: false },
      { title: 'Description', key: 'description', type: FieldType.Text_Variable, readonly: false, minRows: 3, maxRows: 10 }
    ];
    this.deleteGroups = this.deleteGroups.bind(this);

    // Decode UserToken to check for SysAdmin
    this.authService.getUserToken().then((value: string) => {
      this.userToken = value;
      let parts: string[];
      if (this.userToken) {
        parts = this.userToken.split('.');
        this.userToken = JSON.parse(atob(parts[1]));
        if (this.userToken.isSystemAdmin === true) {
          this.sysAdmin = true;
        } else { this.sysAdmin = false; }
      }});

      // get the icon from backend
      this.userDataService.getIcon(this.authService.getUserID(), this.userName).then(value => {
        if (value) {
          this.selectedAvatar = value; }

      });
      this.themeDataService.getLogo().then((value: any) => {
        if (value !== null) {
          const doc = new DOMParser().parseFromString(value, 'image/svg+xml');
          console.log(doc);
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
    this.userName = this.authService.getUserName();
    this.userDataService.getTheme(this.authService.getUserID()).then(value => {
      if (value !== null) {
        this.themeDataService.getThemeByID(value).then(value => {
          if (value !== null) {
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
    this.subscriptions.add(this.route.paramMap.subscribe((params: ParamMap) => {
      this.memberID = this.authService.getUserID();

      if (params.get('showBlocks') && params.get('showBlocks').toLowerCase() === 'true') {
        this.groupDataService.getAllBlockingGroupsOfUser(this.memberID).then((value: BlockingGroupResponseDTO) => {
          this.deletionState = true;
          this.blockingGroupsLists = value;
          this.initColumns();
          this.loadData();
          console.log(this.blockingGroupsLists);
        });
      } else {
        this.initColumns();
        this.loadData();
      }
    }));
    this.hotkeys.newMap();
    this.subscriptionHotkey.push(this.hotkeys.addShortcut({ keys: 'shift.control.n', description: 'Create Group' }).subscribe(() => {
      this.createGroup();
    }));
    this.subscriptionHotkey.push(this.hotkeys.addShortcut({ keys: 'shift.control.p', description: 'Project Selection' }).subscribe(() => {
      this.router.navigate(['/project-overview']);
    }));

    this.stateNotNecessary = false;
  }

  deleteGroups($event): void {
    const list: any[] = $event;
    if (list.length === 0) {
      return;
    }
    Promise.all(list.map((elem: any) => {
      return this.groupDataService.deleteGroup(elem.id);
    })).then(() => {
      this.messageService.add({ severity: 'success', summary: DELETE_SUCCESSFUL });
    }).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: ERROR_DELETE_GROUP, detail: response.message });
    }).finally(() => {
      this.loadData();
    });
  }

  createGroup(): void {
    this.sheetMode = SheetMode.New;
    this.group = { name: '', description: '' };
  }

  saveGroup(group): void {
    this.filterService.ClearSelectionEmitter.emit(true);
    this.disableSaveButton = true;
    if (group.mode === SheetMode.EditWithLock) {
      if (this.group.name !== '') {
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
        this.messageService.add({ severity: 'error', summary: NAME_FIELD_EMPTY });
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
        this.messageService.add({ severity: 'error', summary: NAME_FIELD_EMPTY });
        this.disableSaveButton = false;

      }
    }
  }

  private initColumns(): void {
    this.columns = [
      {
        key: 'select',
        title: 'Select',
        type: ColumnType.Checkbox,
        style: { width: '10%' }
      }, {
        key: 'name',
        title: 'Group-Name',
        type: ColumnType.Text,
        style: { width: '62%' }
      }, {
        key: 'accessLevel',
        title: 'Role',
        type: ColumnType.Text,
        style: { width: '19%' }
      },
      {
        key: 'edit',
        title: 'Edit',
        type: ColumnType.Button,
        style: { width: '3%' }
      }, {
        key: 'show',
        title: 'Show',
        type: ColumnType.Button,
        style: { width: '3%' }
      }, {
        key: 'members',
        title: 'Members',
        type: ColumnType.Button,
        style: { width: '3%' }
      }
    ];
    if (this.deletionState) {
      this.columns[1].style = { width: '52%' };
      this.columns.splice(2, 0,
        {
          key: 'deletion',
          title: 'Allows Deletion',
          type: ColumnType.Group_Deletion,
          style: { 'width': '10%'}
        });
    }
  }

  private loadData(): void {
    this.groupDataService.getAllGroupsOfUser(this.memberID).then((value: GroupResponseDTO[]) => {
      this.groups = value;
      if (this.deletionState) {
        this.groups = this.groups.map((ele: GroupResponseDTO) => ({
          ...ele,
          deletion: this.getGroupDeletionTooltipp(ele.id),
        }));
      }
    }).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: ERROR_LOADING_GROUP, detail: response.message });
    });
  }

  private getGroupDeletionTooltipp(groupId: string): string {
    if (this.blockingGroupsLists.ONLY_ADMIN_MULTI_USER.includes(groupId)) {
      return 'Make someone admin or remove all other users';
    } else if (this.blockingGroupsLists.ONLY_USER_GROUP_WITH_PROJECTS.includes(groupId)) {
      return 'Delete all projects or add users';
    }
    return undefined;
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
    if (this.group) {
      id = this.group.id;
    }
    this.sheetMode = DetailedSheetUtils.toggle(id, data.entity.id, this.sheetMode, data.mode);
    this.group = {
      id: data.entity.id, description: data.entity.description, name: data.entity.name
    };
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
    this.subscriptions.unsubscribe();
    this.subscriptionHotkey.forEach((sub: Subscription) => sub.unsubscribe());
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login/']);
  }

  public setColors() {
    document.body.style.setProperty('--primary-color', this.primaryColor);
    document.body.style.setProperty('--accent-color', this.accentColor);
    document.body.style.setProperty('--warn-color', this.warnColor);
  }
}
