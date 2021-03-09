import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { GroupsComponent } from './groups-handling/groups/groups.component';
import { MemberComponent } from './groups-handling/member/member.component';
import { UserTokenAuthGuard } from './guard/user-token-auth.guard';
import { LoginOverviewComponent } from './login-overview/login-overview.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { ProjectSelectionComponent } from './project-selection/project-selection.component';
import { RegisterViewComponent } from './register-view/register-view.component';
import { SystemAdministrationComponent } from './system-administration/system-administration.component';
import { ImprintComponent } from './common/imprint/imprint.component';
import { CanDeactivateGuard } from './services/change-detection/can-deactivate-guard.service';
import { CorporateThemeComponent } from './user-settings/corporate-theme/corporate-theme.component';
import { UserSettingsComponent } from './user-settings/user-settings.component';
import { PrivacyComponent } from './common/privacy/privacy.component';

const routes: Routes = [
  // { path: '', component: LoginOverviewComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'register', component: RegisterViewComponent },
  { path: 'login', component: LoginOverviewComponent },
  { path: 'project', loadChildren: './sub-app/sub-app.module#SubAppModule', canActivate: [UserTokenAuthGuard] },
  { path: 'project-overview', component: ProjectSelectionComponent, canActivate: [UserTokenAuthGuard], canDeactivate: [CanDeactivateGuard] },
  { path: 'project-overview/:dashboard', component: ProjectSelectionComponent, canActivate: [UserTokenAuthGuard], canDeactivate: [CanDeactivateGuard] },
  { path: 'system-administration/users', component: SystemAdministrationComponent, canDeactivate: [CanDeactivateGuard] },
  { path: 'groups-handling/groups/:showBlocks', component: GroupsComponent, canDeactivate: [CanDeactivateGuard] },
  { path: 'groups-handling/groups', component: GroupsComponent, canDeactivate: [CanDeactivateGuard] },
  { path: 'groups-handling/members', component: MemberComponent, canDeactivate: [CanDeactivateGuard] },
  { path: 'imprint', component: ImprintComponent },
  { path: 'privacy', component: PrivacyComponent },
  { path:  'user-settings', component: UserSettingsComponent },
  { path: 'corporate-theme', component: CorporateThemeComponent },
  { path: '**', component: PageNotFoundComponent },

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppRoutingModule {
}
