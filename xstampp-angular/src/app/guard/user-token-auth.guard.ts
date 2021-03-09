import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, CanActivateChild, Router, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class UserTokenAuthGuard implements CanActivate, CanActivateChild {

  constructor(private authService: AuthService, private router: Router) {

  }

  async canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Promise<boolean> {

    const userToken: string = await this.authService.getUserToken();

    if (userToken) {
      return true;
    } else {
      this.router.navigate(['/login']);
      return false;
    }
  }

  async canActivateChild(childRoute: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean> {
    return this.canActivate(childRoute, state);
  }
}
