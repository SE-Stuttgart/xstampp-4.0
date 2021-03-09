import { HttpErrorResponse } from '@angular/common/http';
import { Component, ElementRef, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { SheetMode } from '../../common/detailed-sheet/detailed-sheet.component';
import { DetailedSheetUtils } from '../../common/detailed-sheet/utils/detailed-sheet-utils';
import { DetailedField, FieldType } from '../../common/entities/detailed-field';
import { ColumnType, TableColumn } from '../../common/entities/table-column';
import { FilterService } from '../../services/filter-service/filter.service';
import { AuthService } from './../../services/auth.service';
import { GroupDataService } from './../../services/dataServices/group-data.service';
import { ThemeDataService } from './../../services/dataServices/theme-data.service';
import { UserDataService, UserDisplayRequestDTO } from './../../services/dataServices/user-data.service';
import { NO_LOGO, DELETE_MEMBER_SUCCESSFUL, ERROR_DELETE_MEMBER, EDIT_SUCCESSFUL, EDIT_FAILED, JOIN_SUCCESSFUL, JOIN_FAILED, ERROR_LOADING_MEMBER, DELETED_USER, NO_USER } from 'src/app/globals';
import { Hotkeys } from 'src/app/hotkeys/hotkeys.service';

@Component({
  selector: 'app-member',
  templateUrl: './member.component.html',
  styleUrls: ['./member.component.css']
})
export class MemberComponent implements OnInit, OnDestroy {

  @ViewChild ('logoBox', { static: true}) logoBoxRef: ElementRef<HTMLDivElement>;
  logoPath: string = '';
  @Input() stateNotNecessary: boolean;
  SheetMode: typeof SheetMode = SheetMode;
  members = [];
  columns: TableColumn[] = [];
  member;
  sheetMode: SheetMode = SheetMode.Closed;
  fields: DetailedField[];
  title: string = 'Member';
  userName: string;
  groupId: string;
  groupName: string;
  userToken;
  sysAdmin: boolean = false;
  disableSaveButton: boolean = false;
  OwnUserAffected: boolean = false;
  selectedAvatar: string = './../../assets/avatar/round default.svg';
  primaryColor: string = '';
   accentColor: string = '';
   warnColor = '';

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

    let dropValues: Map<string, string> = new Map<string, string>([
      ['GUEST', 'Guest'],
      ['DEVELOPER', 'Developer'],
      ['ANALYST', 'Analyst'],
      ['GROUP_ADMIN', 'Group-Admin'],
    ]);
    this.fields = [
      { title: 'EMail', key: 'email', type: FieldType.Text, readonly: false, hidden: false },
      { title: 'Role', key: 'role', type: FieldType.Dropdown, readonly: false, dropDownText: 'Choose role...', values: dropValues },
    ];
    this.deleteMembers = this.deleteMembers.bind(this);
    this.route.queryParams.subscribe((params: Params) => {
      this.groupId = params['groupId'];
      this.groupName = params['groupName'];
    });
    this.route.parent.params.subscribe(() => {
      this.initColumns();
      this.loadData();
    });

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
      }
    });
    this.themeDataService.getLogo().then((value: any) => {
      if (value !== null) {
        const doc = new DOMParser().parseFromString(value, 'image/svg+xml');
        doc.documentElement.setAttribute('height', '100%');

        this.logoBoxRef.nativeElement.appendChild(
          this.logoBoxRef.nativeElement.ownerDocument.importNode(doc.documentElement, true)
        );

        }

    }).catch((error: HttpErrorResponse) => {
      console.log(NO_LOGO);
    });

  }

  ngOnInit(): void {
    this.hotkeys.newMap();
    this.userName = this.authService.getUserName();
    //Get the icon & the theme of the user
    this.userDataService.getIcon(this.authService.getUserID(), this.userName).then(value => {
      if (value) {
        this.selectedAvatar = value;
      }

    });
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

    this.stateNotNecessary = false;
  }

  deleteMembers($event): void {
    const list: any[] = $event;
    if (list.length === 0) {
      return;
    }
    Promise.all(list.map((elem: any) => {
      return this.groupDataService.leaveGroup(this.groupId, elem.uid);
    })).then(() => {
      this.messageService.add({ severity: 'success', summary: DELETE_MEMBER_SUCCESSFUL });
    }).catch((response: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: ERROR_DELETE_MEMBER, detail: response.message });
    }).finally(() => {
      this.loadData();
    });
  }

  createMember(): void {
    this.sheetMode = SheetMode.New;
    this.member = { uid: '', email: '', role: '', groupId: this.groupId, dropdown: 'GUEST' };
  }

  saveMember(member): void {
    this.filterService.ClearSelectionEmitter.emit(true);
    this.disableSaveButton = true;
    if (member.mode === SheetMode.EditWithLock) {
      this.groupDataService.changeAccessLevel(this.groupId, member.ent.email, member.ent.role)
        .then(() => {
          this.messageService.add({ severity: 'success', summary: EDIT_SUCCESSFUL });
          this.closeSheet();
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: EDIT_FAILED, detail: response.message });
        }).finally(() => {
          this.disableSaveButton = false;
          // Check if own user changed GROUP_ADMIN role
          if (this.OwnUserAffected === true && member.ent.role !== 'GROUP_ADMIN') {
            this.router.navigate(['/groups-handling/groups/']);
          } else {
            this.loadData();
          }
          this.OwnUserAffected = false;
        });
    } else if (member.mode === SheetMode.New) {
      this.groupDataService.joinGroup(this.groupId, member.ent.email, member.ent.role)
        .then(() => {
          this.messageService.add({ severity: 'success', summary: JOIN_SUCCESSFUL });
          this.closeSheet();
        }).catch((response: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: JOIN_FAILED, detail: response.message });
        }).finally(() => {
          this.disableSaveButton = false;
          this.OwnUserAffected = false;
          this.loadData();
        });
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
        key: 'email',
        title: 'Member-EMail',
        type: ColumnType.Text,
        style: { width: '40%' }
      }, {
        key: 'icon',
        title: 'Member',
        type: ColumnType.Icon,
        style: { width: '4%' },
        userName: 'displayName',
      }, {
        key: 'accessLevel',
        title: 'Role',
        type: ColumnType.Text,
        style: { width: '19%' }
      }, {
        key: 'edit',
        title: 'Edit',
        type: ColumnType.Button,
        style: { width: '3%' }
      }, {
        key: 'show',
        title: 'Show',
        type: ColumnType.Button,
        style: { width: '3%' }
      }
    ];
  }

  private loadData(): void {
    let userRequest: UserDisplayRequestDTO = new UserDisplayRequestDTO;
    let userId: string[] = [];
    let valueIndex: string[]  = [];
    this.groupDataService.getAllMembersOfGroup(this.groupId).then(value => {
      if (value !== null) {
        value.forEach(element => {
          if(element.uid !== null && element.uid !== ''){
            userId.push(element.uid);
            valueIndex.push(value.indexOf(element).toString());

          }else{
            element.displayName = NO_USER;
            element.icon = this.userDataService.getDefaultOrDeleteIcon(element.displayName);
          }
      }
      );
      if(valueIndex.length !== 0){
        userRequest.userIds = userId;
        this.userDataService.getUserDisplay(userRequest).then((map:Map<string, string[]>) =>{
          valueIndex.forEach(element =>{

              let nameIcon = map.get(value[element].uid);

              if(nameIcon[0] !== null){

                value[element].displayName = nameIcon[0];
              }
              if(nameIcon[1] !== ''){
                value[element].icon = nameIcon[1];
              }else{
                value[element].icon = this.userDataService.getDefaultOrDeleteIcon(nameIcon[1]);
              }


          });
        });
      }
      this.members = value;
      }
    }).catch((response: HttpErrorResponse) => {
      this.messageService.add({
        severity: 'error', summary: ERROR_LOADING_MEMBER, detail: response.message
      });
    });
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
    let uid: string;
    if (this.member) {
      uid = this.member.uid;
    }
    this.sheetMode = DetailedSheetUtils.toggle(uid, data.entity.uid, this.sheetMode, data.mode);
    this.member = {
      uid: data.entity.uid, email: data.entity.email, role: data.entity.accessLevel, groupId: this.groupId, dropdown: 'GUEST'
    };

    // Check if own user is affected
    if (data.entity.uid === this.userToken.uid) {
      this.OwnUserAffected = true;
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

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login/']);
  }

  public setColors() {
    document.body.style.setProperty('--primary-color', this.primaryColor);
    document.body.style.setProperty('--accent-color', this.accentColor);
    document.body.style.setProperty('--warn-color', this.warnColor);
  }

  public home(): void {
    this.router.navigateByUrl('/project-overview');
  }
}
