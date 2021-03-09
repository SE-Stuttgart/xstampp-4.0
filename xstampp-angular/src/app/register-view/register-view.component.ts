import { HttpErrorResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { AuthService } from './../services/auth.service';
import { REGISTER_SUCCESSFUL, GO_TO_LOGIN, NO_SERVER_CONNECTION, REGISTER_FAILED } from '../globals';


@Component({
  selector: 'app-register-view',
  templateUrl: './register-view.component.html',
  styleUrls: ['./register-view.component.css']
})

export class RegisterViewComponent {

  email: string;
  displayName: string;
  password: string;
  passwordRepeat: string;

  constructor(private readonly router: Router, private readonly authUserToken: AuthService, private readonly messageService: MessageService) {
  }

  register(): void {
    console.log(this.displayName);
    if (!this.validateEmail(this.email) || this.displayName === undefined || this.displayName.trim() === '') {
      this.messageService.add({
        severity: 'error', summary: REGISTER_FAILED, detail: 'The supplied email address is not valid or you must enter a display name!'
      });
    } else {
      this.authUserToken.register(this.email, this.password, this.passwordRepeat, this.displayName)
        .then((res: any) => {
          this.messageService.add({
            severity: 'success', summary: REGISTER_SUCCESSFUL, detail: 'You will be forwarded to login in a moment...'
          });

          setTimeout(() => {
            this.goToLoginView();
          },
            3000);
        })
        .catch((response: HttpErrorResponse) => {
          if (response.error.message !== undefined) {
            console.log(response);
            this.messageService.add({
              severity: 'error', summary: REGISTER_FAILED, detail: response.error.message
            });
          } else {
            this.messageService.add({ severity: 'error', summary: NO_SERVER_CONNECTION });
          }
        });
    }
  }

  goToLoginView(): void {
    this.router.navigate(['/login']);
  }

  validateEmail(email: String): boolean {
    let re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(String(email).toLowerCase());
  }

}
