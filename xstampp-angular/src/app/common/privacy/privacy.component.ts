import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { Theme } from 'src/app/types/local-types';
import { AuthService } from 'src/app/services/auth.service';
import { UserDataService } from 'src/app/services/dataServices/user-data.service';
import { ThemeDataService } from 'src/app/services/dataServices/theme-data.service';

@Component({
  selector: 'app-privacy',
  templateUrl: './privacy.component.html',
  styleUrls: ['./privacy.component.css']
})

export class PrivacyComponent implements OnInit {

  title: string = 'Group';
  userName: string;
  userNameString: string;
  userToken;
  sysAdmin: boolean = false;
  primaryColor: string ='#2699FB';
  accentColor: string = '#f0f0f0';

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly authService: AuthService,
    private readonly userDataService: UserDataService,
     private readonly themeDataService: ThemeDataService) {
    // Decode UserToken to check for SysAdmin
    this.authService.getUserToken().then(value => {
      this.userToken = value;
      let parts: string[];
      if (this.userToken) {
        parts = this.userToken.split('.');
        this.userToken = JSON.parse(atob(parts[1]));
        this.userDataService.getTheme(this.authService.getUserID()).then((value: string) =>{
          this.themeDataService.getThemeByID(value).then((themeValue: Theme) =>{
            let color: string = themeValue.colors;
            let parts = color.split('_');
            this.primaryColor = parts[0];
            this.accentColor = parts[1];
          });
        });
        if (this.userToken.isSystemAdmin === true) {
          this.sysAdmin = true;
        } else { this.sysAdmin = false; }
      }
    });

  }

  ngOnInit(): void {
    document.body.style.setProperty('--primary-color', this.primaryColor);
    document.body.style.setProperty('--accent-color', this.accentColor);

    const userName = this.authService.getUserName();
    if (userName !== undefined && userName !== null && userName !== '') {
      this.userNameString = ': ' + userName;
    } else {
      this.userNameString = '';
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login/']);
  }
}
