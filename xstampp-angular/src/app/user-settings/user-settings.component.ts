import { Component, Inject, OnInit, ViewChild, ElementRef } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { AuthService } from '../services/auth.service';
import { UserDataService } from '../services/dataServices/user-data.service';
import { ThemeDataService} from '../services/dataServices/theme-data.service';
import { Theme, Icon, LoginRequestDTO} from '../types/local-types';
import { GroupDataService } from '../services/dataServices/group-data.service';
import { HttpErrorResponse } from '@angular/common/http';



 

@Component({
  selector: 'app-user-settings',
  templateUrl: './user-settings.component.html',
  styleUrls: ['./user-settings.component.css']
})
export class UserSettingsComponent implements OnInit {
  @ViewChild('logoBox', {static: true}) logoBoxRef: ElementRef<HTMLDivElement>;
  logoPath:string = '';



  @ViewChild('password1', {static: true}) password1: ElementRef;

  @ViewChild('password2', {static: true}) password2: ElementRef;

  email: string;
  userName: string;
  userToken;
  sysAdmin : boolean;
  primaryColor:string = '';
   accentColor:string = '';
   warnColor: string='';
   imagePath:string = "";
   disableSaveButton: boolean = false;
    iconPath: string = "";
    editAvatarB: boolean = false;
   avatars: string[] = [];
   imagineChanged: boolean = false;
   dialogRef: MatDialogRef<ConfirmUserDeletionDialogComponent>;


   themes: Theme[] = [];
   selectedAvatar: string = './../../assets/avatar/round default.svg';
   editBoolean: boolean = false;
   selectedThemeId: string = '';


   fileForDropdown: FilesForDropdown[] =[];
    constructor(
    private readonly authService: AuthService,
    public readonly dialog: MatDialog,
    private readonly themeDataService: ThemeDataService,
    private readonly userDataService: UserDataService,
    private readonly messageService: MessageService,
    private readonly router: Router,
    private readonly groupDataService: GroupDataService) { // Decode UserToken to check for SysAdmin

    this.authService.getUserToken().then(value => {

      this.userToken = value;
      let parts: string[];
      if (this.userToken) {
        parts = this.userToken.split('.');
        this.userToken = JSON.parse(atob(parts[1]));
        if (this.userToken.isSystemAdmin === true) {
          this.sysAdmin = true;
        } else { this.sysAdmin = false; }
      }});

      this.themeDataService.getLogo().then((value: any) =>{
        if(value !== null){
          const doc = new DOMParser().parseFromString(value, 'image/svg+xml');
          doc.documentElement.setAttribute('height', '100%');

          this.logoBoxRef.nativeElement.appendChild(
            this.logoBoxRef.nativeElement.ownerDocument.importNode(doc.documentElement, true)
          );
        }
        else{
          this.messageService.add({severity:'warn', summary:'No Logo was updated'});

        }

    }).catch((error: HttpErrorResponse)=>{
      console.log('No logo to load')
    });

      this.userDataService.getIcon(this.authService.getUserID(), this.userName).then(value =>{
        if(value){
          this.selectedAvatar = value; }

      });




      this.imagePath = './../../assets/xstampp_logo.svg';
  }

  ngOnInit() {
    this.userName = this.authService.getUserName();

    this.userDataService.getTheme(this.authService.getUserID()).then(value =>{

      this.selectedThemeId=value;
        this.themeDataService.getThemeByID(value).then(themeValue =>{
          if(themeValue !== null){

            let color: string = themeValue.colors;
            let parts = color.split('_');
            this.primaryColor = parts[0];
            this.accentColor = parts[1];
            this.warnColor = parts[2];
            document.body.style.setProperty('--primary-color', this.primaryColor);
            document.body.style.setProperty('--accent-color', this.accentColor);
            document.body.style.setProperty('--warn-color', this.warnColor);
          }
      });


    });
    this.userDataService.getEmail(this.authService.getUserID()).then( (value:string)=>{
      if(value !== null){
        this.email = value;
      }
    });
   this.getAllThemes();
  }
  public getAllThemes(){
    this.themes = [];
     this.themeDataService.getAllThemes({}).then(value =>
     {
       if(value !== null){

         this.loadDropdown(value);
       }
     });

 }
  public edit(){
    this.loadAvatars();
    this.editBoolean  = true;

 }
 public changeImg(source:string){
    this.imagineChanged = true;
    this.iconPath = source;
  }


public loadAvatars(){
  for (let index = 1; index < 16; index++) {
    this.avatars.push('./../../assets/avatar/'+index+'_1.svg');

  }
  this.avatars.push('./../../assets/avatar/round default.svg')


}

public loadDropdown(value: Theme[]):void{
  this.fileForDropdown = [];
   this.themes = [];

  this.themes = this.themes.concat(value);

  this.themes.forEach((element:Theme) => {

    this.fileForDropdown.push({id: element.id, name:  element.name, colors: element.colors
    , viewValue: element.name});

  });

}

public changeTheme(value){

  let val: string[]= value.value


  let color: string = '';
  this.selectedThemeId = val[0];
  this.themes.forEach(element => {
    if(element.id === val[0] && element.name === val[1]){
      color = element.colors;

    }
  });

  let parts = color.split('_');
  this.primaryColor = parts[0];
  this.accentColor = parts[1];
  this.warnColor = parts[2];

  document.body.style.setProperty('--primary-color', this.primaryColor);
  document.body.style.setProperty('--accent-color', this.accentColor);
  document.body.style.setProperty('--warn-color', this.warnColor);



}

//icon and theme can always be saved because they always have a value
public save(){

  let password1: string = this.password1.nativeElement.value;
  let password2: string = this.password2.nativeElement.value;

  let icon: Icon = {id:'', path: this.selectedAvatar};
  let user: LoginRequestDTO = {email: this.email, password: password1};
if(!!this.validateEmail(this.email)){
  if(password1 !== password2){
          this.messageService.add({
            severity: 'error',
            life: 10000,
            closable: true,
            summary: 'New passwords do not match with each other'
          });
         } else {

        this.dialogRef = this.dialog.open(ConfirmUserDeletionDialogComponent,{
          width: '275 px',
          data: { description: 'If you want to update your Information , please confirm your current password', deleteUser: false, title: 'UPDATE USER INFORMATION'},

        });

          this.dialogRef.afterClosed().subscribe( result =>{
            if(result!== null && result !== undefined){
              console.log(this.email)
              console.log(this.password1.nativeElement.value)

             if(password1.length > 0 && this.email.length > 0){
                //save email and password
                Promise.all([
                  this.userDataService.setTheme( this.authService.getUserID() ,this.selectedThemeId),
                  this.userDataService.setIcon(this.authService.getUserID(),icon),
                 this.userDataService.setPasswordEmailByUser(this.authService.getUserID(), user),
                ])
              .then((value: [boolean, boolean, boolean]) =>{
                if(value){
                  this.userDataService.getEmail(this.authService.getUserID()).then((value: string) =>{
                    if(value !== null){
                      this.email = value;
                    }

                  }).catch((reason: HttpErrorResponse) =>{
                    this.messageService.add({ severity: 'error', summary: 'Get Email failed', detail: reason.message });
                  });

                  this.password1.nativeElement.value = '';
                  this.password2.nativeElement.value = '';
                  this.editBoolean = false;
                  this.messageService.add({severity: 'success', life: 10000, summary: 'Save successfull'});

                } else{
                  this.messageService.add({severity: 'error', life: 10000, summary: 'Save failed'});
                }
              }).catch((reason: HttpErrorResponse)=>{
                this.messageService.add({ severity: 'error', summary: 'Save failed. Please try new email adress', detail: reason.message });
              });}
              else if(password1.length ===0 && this.email.length >0){

                Promise.all([
                  this.userDataService.setTheme( this.authService.getUserID() ,this.selectedThemeId),
                  this.userDataService.setIcon(this.authService.getUserID(), icon),
                  this.userDataService.setEmail(this.authService.getUserID(), user),
                ])
              .then((value: [boolean, boolean, boolean]) =>{

                if(value){
                  this.userDataService.getEmail(this.authService.getUserID()).then((value: string) =>{
                    if(value !== null){
                      this.email = value;
                    }

                  }).catch((reason: HttpErrorResponse) =>{
                    this.messageService.add({ severity: 'error', summary: 'Get Email failed', detail: reason.message });
                  });

                  this.password1.nativeElement.value = '';
                  this.password2.nativeElement.value = '';
                  this.editBoolean = false;
                  this.messageService.add({severity: 'success', life: 10000, summary: 'Save successfull'});
                } else{

                  this.messageService.add({severity: 'error', life: 10000, summary: 'Save failed'});
                }
              }).catch((reason: HttpErrorResponse) =>{
                this.messageService.add({ severity: 'error', summary: 'Save failed. Please try new Email adress', detail: reason.message });
              });
              }else if(password1.length > 0 && this.email.length === 0){
                //save just new password
                Promise.all([
                  this.userDataService.setTheme( this.authService.getUserID() ,this.selectedThemeId),
                  this.userDataService.setIcon(this.authService.getUserID(), icon),
                  this.userDataService.setPasswordByUser(this.authService.getUserID(), user),
                ])
              .then((value: [boolean, boolean, boolean]) =>{
                if(value){
                  this.userDataService.getEmail(this.authService.getUserID()).then((value: string) =>{
                    if(value !== null){
                      this.email = value;
                    }

                  }).catch((reason: HttpErrorResponse) =>{
                    this.messageService.add({ severity: 'error', summary: 'Get Email failed', detail: reason.message });
                  });

                  this.password1.nativeElement.value = '';
                  this.password2.nativeElement.value = '';
                  this.editBoolean = false;
                  this.messageService.add({severity: 'success', life: 10000, summary: 'Save successfull'});
                } else{
                  this.messageService.add({severity: 'error', life: 10000, summary: 'Save failed'});
                }
              }).catch((reason: HttpErrorResponse)=>{
                this.messageService.add({ severity: 'error', summary: 'Save failed', detail: reason.message });
              });

                }else if(password1.length === 0 && this.email.length === 0){
                  //save just theme and icon
                  Promise.all([
                    this.userDataService.setTheme( this.authService.getUserID() ,this.selectedThemeId),
                    this.userDataService.setIcon(this.authService.getUserID(),icon),
                  ])
                .then((value: [boolean, boolean]) =>{
                  if(value){
                    this.userDataService.getEmail(this.authService.getUserID()).then((value: string) =>{
                      if(value !== null){
                        this.email = value;
                      }

                    }).catch((reason: HttpErrorResponse) =>{
                      this.messageService.add({ severity: 'error', summary: 'Get Email failed', detail: reason.message });
                    });

                    this.password1.nativeElement.value = '';
                    this.password2.nativeElement.value = '';
                    this.editBoolean = false;
                    this.messageService.add({severity: 'success', life: 10000, summary: 'Save successfull'});

                  } else{
                    this.messageService.add({severity: 'error', life: 10000, summary: 'Save failed'});
                  }
                }).catch((reason: HttpErrorResponse)=>{
                  this.messageService.add({ severity: 'error', summary: 'Save failed', detail: reason.message });
                });
                }
              }else{
                this.editBoolean = false;
              }
            });

          }
        }else{
          this.messageService.add({ severity:'error', summary:'The supplied email address is not valid!' });
        }

}

private validateEmail(email: String): boolean {
  let re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
  return re.test(String(email).toLowerCase());
}




public cancle(){
  this.userDataService.getEmail(this.authService.getUserID()).then((value: string) =>{
    if(value !== null){
      this.email= value;
    }

  });

  this.password1.nativeElement.value = '';
  this.password2.nativeElement.value = '';
this.editBoolean = false;
this.avatars = [];
}

public changeIcon(avatar:string){
this.selectedAvatar=avatar;
}
public logout(): void {
  this.authService.logout();
  this.router.navigate(['/login/']);
}

/**
 * Method does not really do anything yet, because of the password check. after that it should work
 */
public deleteUser(){
this.groupDataService.getAllBlockingGroupsOfUser(this.authService.getUserID()).then(value =>{
  if(value){
    if(value.ONLY_ADMIN_MULTI_USER.length > 0 || value.ONLY_USER_GROUP_WITH_PROJECTS.length > 0) {
      this.messageService.add({
        severity: 'warn',
        life: 10000,
        summary: 'Some groups block deletion.\nHover over the red warning symbol.'
      });
      setTimeout(()=>{
        this.router.navigate(['groups-handling/groups/true']);
      }, 1000);
    } else {
      this.dialogRef = this.dialog.open(ConfirmUserDeletionDialogComponent,{
        width: '275 px',
        data: { description: 'Do you really want to delete the User?', title: 'USER DELETION'}
      });
    }
  }
});
}
}

export interface FilesForDropdown {
  id: string;
  name:string;
  colors: string;
  viewValue: string;

}

export interface AvatarMenu {
  path: string;



}

/**
 * Dialog for User Deletion
 */

@Component({
  selector: 'app-confirm-user-deletion',
  templateUrl: './confirm-user-deletion-dialog.component.html',
  styleUrls: ['./user-settings.component.css']
})
export class ConfirmUserDeletionDialogComponent {



  constructor( public dialogRef: MatDialogRef<ConfirmUserDeletionDialogComponent>, public readonly userDataService: UserDataService,
    public readonly authService: AuthService, public readonly messageService: MessageService, public readonly router: Router,
    @Inject(MAT_DIALOG_DATA) public data: any) {
  }

  onCancelClick(): void {

    this.dialogRef.close();
  }

  //Method to check password
  onConfirmClick(passwordInput: string): void {
    this.userDataService.getEmail(this.authService.getUserID()).then((value: string) =>{
      if(value !== null){
        let user: LoginRequestDTO = {email: value, password: passwordInput};
      if(this.data.deleteUser !== false){
        this.userDataService.deleteUserByUser(this.authService.getUserID(), user).then((value: boolean)=>{
          if(value){
            this.messageService.add({
              closable: true,
              severity: 'success',
              summary: 'Password correct. User deleted.'
            });
            this.authService.logout();
            this.router.navigate(['/login/']);
            this.dialogRef.close();
          }
        }, (reason: Error | HttpErrorResponse) => {
          if(reason instanceof HttpErrorResponse && reason.status === 403) {
            this.messageService.add({
              closable: true,
              severity: 'error',
              summary: 'False password'
            });
          } else {
            this.messageService.add({
              closable: true,
              severity: 'error',
              summary: 'Unexpected response from server'
            });
          }
          this.dialogRef.close();
        });
          }else{
            this.userDataService.confirmUserForEdit(this.authService.getUserID(), user).then((value: boolean)=>{
              if(value){
                this.dialogRef.close('edit');
              }else{
                this.messageService.add({
                  closable: true,
                  severity: 'error',
                  summary: 'False password'
                });
              }
            });
          }
      }else{
        this.messageService.add({
          closable: true,
          severity: 'error',
          summary: 'Obtaining email not possible'
        })
      }

    });


  }

 }
