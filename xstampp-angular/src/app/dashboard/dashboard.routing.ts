import { Routes, RouterModule } from '@angular/router';
import { DashboardComponent } from './dashboard.component';
import { NgModule } from '@angular/core';
import { ProjectTokenAuthGuard } from '../guard/project-token-auth.guard';
import { UserTokenAuthGuard } from '../guard/user-token-auth.guard';

const routes: Routes = [
  {
    path: 'test', component: DashboardComponent,
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})

export class DashboardRoutes {
}
