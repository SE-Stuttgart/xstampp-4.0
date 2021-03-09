import { HttpErrorResponse } from '@angular/common/http';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { ThemeDataService } from 'src/app/services/dataServices/theme-data.service';
import { UserDataService } from 'src/app/services/dataServices/user-data.service';
import { ColumnType, TableColumn } from '../../common/entities/table-column';
import { AuthService } from '../../services/auth.service';
import { Theme } from './../../types/local-types';


@Component({
  selector: 'app-root',
  templateUrl: './corporate-theme.component.html',
  styleUrls: ['./corporate-theme.component.css']
})
export class CorporateThemeComponent implements OnInit {

  @ViewChild('logoBox', {static: true}) logoBoxRef: ElementRef<HTMLDivElement>;
  @ViewChild('logo', {static: true}) logo: ElementRef<HTMLDivElement>;
  @ViewChild('primary', {static: true}) primary: ElementRef;
  @ViewChild('accent', {static: true}) accent: ElementRef;
  @ViewChild('warn', {static: true}) warn: ElementRef;
  @ViewChild('themeName', {static: true}) themeName: ElementRef;
  @ViewChild('selectedTheme', {static: true}) themeSpinner: ElementRef;


  edit: boolean = false;
  sysAdmin: boolean = false;
   primaryColor: string = '';
   accentColor: string = '';
   warnColor: string = '';
   upload: boolean = false;
   file: File = null;
  logoPreview: SafeUrl = '';
  theme: string = '';
  columns: TableColumn[] = [];
  themes: Theme[] = [];
  newTheme: boolean = true;
  selectedAvatar: string = './../../assets/avatar/round default.svg';
  fileForDropdown: FilesForDropdown[] =[];
  logoPath = '';
  userToken;
  userName: string = '';
  logoBoolean: boolean = false;


  constructor(private readonly themeDataService: ThemeDataService,
    private readonly messageService: MessageService,
    private readonly sanitizer: DomSanitizer,
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly userDataService: UserDataService,) {


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

      this.themeDataService.getLogo().then((value: any) =>{
        if(value !== null){

          const doc = new DOMParser().parseFromString(value, 'image/svg+xml');
          doc.documentElement.setAttribute('height', '100%');

          this.logoBoxRef.nativeElement.appendChild(
            this.logoBoxRef.nativeElement.ownerDocument.importNode(doc.documentElement, true)
          );

          if(this.sysAdmin === true){
            this.logoBoolean = true;
          }



          }
          else{
            this.messageService.add({severity:'warn', summary:'No Logo was updated'});

          }

      }).catch((error: HttpErrorResponse)=>{

      });


     }



   public ngOnInit() {
    this.userName = this.authService.getUserName();

    this.initColumns();
    this.userDataService.getTheme(this.authService.getUserID()).then(value =>{
      if(value !== null){

        this.themeDataService.getThemeByID(value).then(value =>{

          if(value!== null){
            let color: string = value.colors;
            let parts = color.split('_');
            this.setColors(parts[0], parts[1], parts[2]);

          }
        });
      }
    });
    document.body.style.setProperty('--prevPrim-color', '#2699FB');
    document.body.style.setProperty('--prevAcc-color', '#F0F0F0');




    this.userDataService.getIcon(this.authService.getUserID(), this.userName).then(value =>{
      if(value){
        this.selectedAvatar = value; }

    });


    this.getAllThemes();


  }

   public onFileChange(file){



    let url = window.URL.createObjectURL(file[0]);
    this.logoPreview = this.sanitizer.bypassSecurityTrustUrl(url);

    this.upload = true;
    this.file = file[0];

  }

  public deleteLogo(){
    this.themeDataService.deleteLogo().then( value =>{
        if(value){
          this.messageService.add({severity:'success', summary:'The Logo was deleted'});
          this.logoBoxRef.nativeElement.innerHTML='';
          this.logoBoolean = false;
        }
        else{
          this.messageService.add({severity:'error', summary:'Delete not possible'});
        }
    });
  }

  //method gets the color from user input & sets the value in css
  public setNewColor(newColor:any, name: string){

    if(name === 'primary'){

      document.body.style.setProperty('--prevPrim-color', newColor);
    }else if(name === 'accent'){

      document.body.style.setProperty('--prevAcc-color', newColor);
    } else if(name === 'warn'){
      document.body.style.setProperty('--warn-color', newColor);


  }
}
public getAllThemes(){
   this.themes = [];

    this.themeDataService.getAllThemes({}).then(value =>
    {
      if(value !== null){

        this.loadDropdown(value);
      }else{
        this.fileForDropdown.push({name:'newTheme',id:'', colors:'', viewValue:'Create new theme'});
      }
    })

}



private initColumns(): void {
  this.columns = [
    {
      key: 'select',
      title: 'Select',
      type: ColumnType.Checkbox,
      style: {width: '5%'}
    }, {
      key: 'id',
      title: 'ID',
      type: ColumnType.Text,
      style: {width: '5%'}
    }, {
      key: 'name',
      title: 'esempio',
      type: ColumnType.Text,
      style: {width: '61%'}
    }, {
      key: 'lastEditor',
      title: 'Edited-By',
      type: ColumnType.Icon,
      style: {width: '10%'}
    }, {
      key: 'lastEdited',
      title: 'Last-Edited',
      type: ColumnType.Date_Time,
      style: {width: '13%'}
    }, {
      key: 'edit',
      title: 'Edit',
      type: ColumnType.Button,
      style: {width: '3%'}
    }, {
      key: 'show',
      title: 'Show',
      type: ColumnType.Button,
      style: {width: '3%'}
    }
  ];

}
 public uploadFile(){


    this.themeDataService.createIcon(this.file).then(()=>{

      this.upload = false;
      this.messageService.add({severity: 'success', summary:'Create succesfull'});
      this.themeDataService.getLogo().then((value: string) =>{
        if(value !== null){
         this.logoBoxRef.nativeElement.innerHTML = value;
        }
        else{
          this.messageService.add({severity:'warn', summary:'No Logo was updated'});
        }


      });

    }).catch((response: HttpErrorResponse) => {
      this.messageService.add({severity:'error', summary:'Create failed', detail:response.message});
    });


  }

  /**
   * method to save the theme which was created or to alter an existing theme
   */
public saveTheme(selectedTheme,name: string, primary, accent, warn){

  let colors = primary +"_"+accent+"_"+warn;
  if(name.length ===0 && this.theme !== 'newTheme'){
    name = this.theme;
  }
  if(name.length >0){
    if(selectedTheme.value[0] === ''){

      const themeCreateEnt = {id:"" ,name: name, colors: colors};
      this.themeDataService.createTheme(themeCreateEnt).then(() => {
        this.messageService.add({severity: 'success', summary: 'Create successful'});
        this.edit = false;
        this.themeName.nativeElement.value ='';
        this.getAllThemes();


      }).catch((response: HttpErrorResponse)  => {
        this.messageService.add({severity: 'error', summary: 'Create failed', detail: response.message});
    }

      );

    }else{
      const themeCreateEnt = {id: selectedTheme.value[0] ,name: name, colors: colors};
      this.themeDataService.alterTheme(selectedTheme.value[0], themeCreateEnt).then(()=>
      {
        this.messageService.add({severity: 'success', summary: 'Change successful'});
        this.edit = false;
        this.themeName.nativeElement.value ='';
        this.getAllThemes();


      }).catch((response: HttpErrorResponse)  => {
        this.messageService.add({severity: 'error', summary: 'Change failed', detail: response.message});
    }

    );}

  }else{
    this.messageService.add({severity:'error', summary: 'Pleas add a name and/or colors in order to save the theme'});
  }


}
public cancle(){
  this.edit = false;
    this.primary.nativeElement.value = this.primaryColor;
    this.accent.nativeElement.value = this.accentColor;
    this.warn.nativeElement.value = this.warnColor;
    document.body.style.setProperty('--prevPrim-color', this.primaryColor);
    document.body.style.setProperty('--prevAcc-color', this.accentColor);
    document.body.style.setProperty('--warn-color', this.warnColor);
    if(this.theme === 'newTheme'){
      this.themeName.nativeElement.value ='';
    }else {this.themeName.nativeElement.value = this.theme;}

}

 public loadDropdown(value: Theme[]):void{
   if(this.sysAdmin === true){
    this.fileForDropdown = [];
     this.themes = [];
     this.fileForDropdown.push({name:'newTheme',id:'', colors:'', viewValue:'Create new theme'});
    this.themes = this.themes.concat(value);
    console.log(this.themes, 'theme');
    this.themes.forEach((element:Theme) => {

      this.fileForDropdown.push({id: element.id, name:  element.name, colors: element.colors
      , viewValue: element.name});

    });
   }else{
     this.messageService.add({severity:'warn', closable: true, summary: 'You do not have the rights to create or alter themes'})
   }


  }

  public changeTheme(value){

    this.edit = true;
    let val: string[]= value.value

    this.theme = val[1];

    if(val[1] !=='newTheme' || val[1] !== 'default'){
      this.newTheme = false;
    }else{
      this.newTheme = true;
    }
    let color: string = '';

    this.themes.forEach(element => {
      if(element.id === val[0] && element.name === val[1]){
        color = element.colors;

      }
    });

    let parts = color.split('_');
    this.setPrevColors(parts[0], parts[1], parts[2]);


  }

  public deleteTheme(value){
    let id:string = value.value[0] as string;
    let defaultThemeId = '0';

    if(!(defaultThemeId.match(id))){
      this.themeDataService.deleteTheme(value.value[0]).then(() =>{
        this.messageService.add({severity: 'success', summary: 'Delete successful'});
        this.getAllThemes();


      }).catch((response: HttpErrorResponse)  => {
        this.messageService.add({severity: 'error', summary: 'Delete failed', detail: response.message});

      }
    );
    }else{
      this.messageService.add({severity:'warn', summary:'Default theme can not be deleted'});
    }

}

  public getPrimaryColor(): string{
    return this.primaryColor;
  }

  public getAccentColor(): string{
    return this.accentColor;
  }
  public getWarnColor(): string{
    return this.warnColor;
  }


  public setColors(primary: string, accent: string, warn: string){
    this.primaryColor = primary;
    this.accentColor = accent;
    this.warnColor = warn;
    document.body.style.setProperty('--primary-color', primary);
    document.body.style.setProperty('--accent-color', accent);
    document.body.style.setProperty('--warn-color', warn);

  }
  public setPrevColors(primary: string, accent: string, warn: string){
    this.primary.nativeElement.value = primary;
    this.accent.nativeElement.value = accent;
    this.warn.nativeElement.value = warn;
    document.body.style.setProperty('--prevPrim-color', primary);
    document.body.style.setProperty('--prevAcc-color', accent);
    document.body.style.setProperty('--warn-color', warn);

  }
  public logout(): void {
    this.authService.logout();
    this.router.navigate(['/login/']);
  }

  changeRoute(){
    if(this.edit === false && this.upload === false){
      this.router.navigate(['/user-settings']);
    }
  }


  }






export interface FilesForDropdown {
  id: string;
  name:string;
  colors: string;
  viewValue: string;

}




