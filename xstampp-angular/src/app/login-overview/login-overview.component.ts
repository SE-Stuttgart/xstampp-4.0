import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { AuthService } from '../services/auth.service';
import { ThemeDataService } from '../services/dataServices/theme-data.service';
import { LOGIN_SUCCESSFUL, LOGIN_FAILED, NO_SERVER_CONNECTION } from '../globals';
import { Hotkeys } from '../hotkeys/hotkeys.service';
import { Subscription } from 'rxjs';


@Component({
  selector: 'app-root',
  templateUrl: './login-overview.component.html',
  styleUrls: ['./login-overview.component.css']
})

export class LoginOverviewComponent implements OnDestroy, OnInit {

  constructor(private readonly router: Router, private readonly authUserToken: AuthService, private readonly messageService: MessageService, private readonly hotkeys: Hotkeys) {
    document.body.style.setProperty('--accent-color', '#f0f0f0');
    document.body.style.setProperty('--primary-color', '#2699FB');
    this.subscriptions.push(this.hotkeys.addShortcut({ keys: 'shift.control.r', description: 'Register' }).subscribe(() => {
      this.router.navigate(['/register']);
    }));
  }
  subscriptions: Subscription[] = [];
  email: string;
  password: string;
  private token: string;

  ngOnInit(): void {
    this.authUserToken.getUserToken().then((userToken) => {
      if (userToken !== null && userToken !== undefined && userToken !== '') {
        this.goToProjecOverview();
      }
    });
  }

  login(): void {
    this.token = '';
    this.authUserToken.requestUserToken(this.email, this.password)
      .then((res: string) => {
        this.token = res;
        this.authUserToken.setUserToken(this.token);
        this.goToProjecOverview();
        this.messageService.add({ severity: 'success', summary: LOGIN_SUCCESSFUL });
      })
      .catch((response: HttpErrorResponse) => {
        if (response.error.message !== undefined) {
          console.log(response);
          this.messageService.add({ severity: 'error', summary: LOGIN_FAILED, detail: response.error.message });
        } else {
          this.messageService.add({ severity: 'error', summary: NO_SERVER_CONNECTION });
        }
      });
  }
  ngOnDestroy(): void {
    this.subscriptions.forEach((subscription: Subscription) => subscription.unsubscribe());
  }
  goToProjecOverview(): void {
    this.router.navigate(['/project-overview']);
  }
}
