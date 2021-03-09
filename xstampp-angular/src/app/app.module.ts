import { LocationStrategy, PathLocationStrategy } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule, MatButtonToggleModule, MatCardModule, MatCheckboxModule, MatDialogModule, MatExpansionModule, MatFormFieldModule, MatGridListModule, MatIconModule, MatInputModule, MatListModule, MatMenuModule, MatOptionModule, MatPaginatorModule, MatSelectModule, MatSidenavModule, MatSortModule, MatTableModule, MatToolbarModule, MatTooltipModule } from '@angular/material';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';
import { MentionModule } from 'angular2-mentions/mention';
import { Ng4LoadingSpinnerModule } from 'ng4-loading-spinner';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CloneProjectDialogComponent } from './clone-project-dialog/clone-project-dialog.component';
import { CommonComponentsModule } from './common/common-components.module';
import { DashboardModule } from './dashboard/dashboard.module';
import { GroupsHandlingComponent } from './groups-handling/groups-handling.component';
import { GroupsComponent } from './groups-handling/groups/groups.component';
import { MemberComponent } from './groups-handling/member/member.component';
import { ImprintComponent } from './common/imprint/imprint.component';
import { LoginOverviewComponent } from './login-overview/login-overview.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { ProjectSelectionComponent } from './project-selection/project-selection.component';
import { RegisterViewComponent } from './register-view/register-view.component';
import { ChangePWService } from './services/change-pw.service';
import { ThemeDataService } from './services/dataServices/theme-data.service';
import { FilterService } from './services/filter-service/filter.service';
import { RuleParserService } from './services/rule-parser.service';
import { WebsocketService } from './services/websocket.service';
import { UserSettingsComponent, ConfirmUserDeletionDialogComponent } from './user-settings/user-settings.component';
import { CorporateThemeComponent } from './user-settings/corporate-theme/corporate-theme.component';
import { PrivacyComponent } from './common/privacy/privacy.component';
import { ChangePWDialogComponent, SystemAdministrationComponent } from './system-administration/system-administration.component';
import { HotkeyCommandsComponent } from './hotkeys/hotkey-commands.component';
import { MatTreeModule } from '@angular/material';


@NgModule({
   declarations: [
      AppComponent,
      LoginOverviewComponent,
      RegisterViewComponent,
      ProjectSelectionComponent,
      PageNotFoundComponent,
      GroupsHandlingComponent,
      GroupsComponent,
      MemberComponent,
      UserSettingsComponent,
      SystemAdministrationComponent,
      ChangePWDialogComponent,
      HotkeyCommandsComponent,
      ImprintComponent,
      PrivacyComponent,
      ConfirmUserDeletionDialogComponent,
      UserSettingsComponent,
      CorporateThemeComponent,
      CloneProjectDialogComponent,
   ],
   imports: [
      MatTreeModule,
      BrowserModule,
      BrowserAnimationsModule,
      CommonComponentsModule,
      AppRoutingModule,
      MatToolbarModule,
      MatTableModule,
      MatSortModule,
      MatPaginatorModule,
      MatCardModule,
      MatFormFieldModule,
      MatInputModule,
      MatButtonModule,
      MatIconModule,
      HttpClientModule,
      FormsModule,
      MatCheckboxModule,
      MatSidenavModule,
      MatExpansionModule,
      MatListModule,
      MatButtonModule,
      MatButtonToggleModule,
      MatDialogModule,
      ToastModule,
      Ng4LoadingSpinnerModule.forRoot(),
      MatSelectModule,
      MatMenuModule,
      MatGridListModule,
      MatTooltipModule,
      DashboardModule,
      MentionModule,
      MatOptionModule,
      RouterModule,
   ],
   providers: [
      FilterService,
      ChangePWService,
      MessageService,
      WebsocketService,
      RuleParserService,
      ThemeDataService,
      { provide: LocationStrategy, useClass: PathLocationStrategy }
   ],
   bootstrap: [
      AppComponent,
   ],
   entryComponents: [
      ChangePWDialogComponent,
      HotkeyCommandsComponent,
      ConfirmUserDeletionDialogComponent,
      CloneProjectDialogComponent,
   ],
})
export class AppModule {
}
