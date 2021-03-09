import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, CanActivateChild, Router, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class ProjectTokenAuthGuard implements CanActivate, CanActivateChild {

  constructor(private authService: AuthService, private router: Router) {

  }

  async canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Promise<boolean> {

    let projectToken: string = await this.authService.getProjectToken();
    const projectId: string = next.params['id'];

    if (projectId) {
      console.error('projectId not present');

      if (!projectToken || this.authService.validateProjectToken(projectToken, projectId)) {
        await this.authService.refetchProjectToken(projectId);
        projectToken = await this.authService.getProjectToken();
      }
    }

    if (projectToken) {
      return true;
    } else {
      this.router.navigate(['/project-overview']);
      return false;
    }
  }

  async canActivateChild(childRoute: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean> {
    return this.canActivate(childRoute, state);
  }
}
