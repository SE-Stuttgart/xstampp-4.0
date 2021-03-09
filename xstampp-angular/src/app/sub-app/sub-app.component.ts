import { AfterViewInit, Component, ViewChild, ElementRef, AfterContentInit, OnInit, OnDestroy } from '@angular/core';
import { MatExpansionPanel } from '@angular/material';
import { DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { AuthService } from '../services/auth.service';
import { ImportExportDataService } from '../services/dataServices/import-export-data.service';
import { ProjectDataService } from '../services/dataServices/project-data.service';
import { ProjectResponseDTO, Theme } from '../types/local-types';
import { Hotkeys } from '../hotkeys/hotkeys.service';
import { Location } from '@angular/common';
import { ThemeDataService } from '../services/dataServices/theme-data.service';
import { UserDataService } from '../services/dataServices/user-data.service';
import { SvgDataService } from '../services/dataServices/control-structure/svg-data.service';
import { saveAs } from 'file-saver';
import { HttpErrorResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-sub-app',
  templateUrl: './sub-app.component.html',
  styleUrls: ['./sub-app.component.css']
})

export class SubAppComponent implements AfterContentInit, AfterViewInit, OnInit, OnDestroy {

  @ViewChild('logoBox', { static: true }) logoBoxRef: ElementRef<HTMLDivElement>;
  logoPath: string = '';
  @ViewChild('dropDown1', { static: true }) dropDown1: MatExpansionPanel;
  @ViewChild('dropDown2', { static: true }) dropDown2: MatExpansionPanel;

  subscriptions: Subscription[] = [];
  projectId: string;
  project: ProjectResponseDTO;
  name: string;
  userToken;
  sysAdmin: boolean = false;
  navigationUrls: string[] = ['/system-description', '/identify-losses', '/system-level-hazards', '/system-level-safety-constraints', '/refine-hazards', '/control-structure-diagram/STEP2',
    '/controller', '/actuator', '/sensor', '/controlled-process', '/control-action', '/feedback', '/input-arrow', '/output-arrow', '/responsibilities', '/uca-table', '/controller-constraints',
    '/control-structure-diagram/STEP4', '/processmodel-table', '/processvariable-table', '/control-algorithm', '/conversion-table', '/loss-scenario-table', '/implementation-constraints'];

  selectedAvatar: string = './../../assets/avatar/round default.svg';
  fileUrl;
  positionfound: boolean = false;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly data: ProjectDataService,
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly hotkeys: Hotkeys,
    private readonly userDataService: UserDataService,
    private readonly themeDataService: ThemeDataService,
    private readonly messageService: MessageService,
    private readonly importExportDataServive: ImportExportDataService,
    private readonly controlStructureDataService: SvgDataService,
    private sanitizer: DomSanitizer,
    private readonly location: Location) {
 
  }
  ngOnInit(): void {
    this.route.params.subscribe((params: any) => {
      this.projectId = params.id;
      if (this.projectId) {
        this.data.getProjectById(this.projectId).then((value) => {
          this.project = value;
        });
      }
    });
    this.name = this.authService.getUserName();

    // Decode UserToken to check for SysAdmin
    this.authService.getUserToken().then(value => {
      this.userToken = value;
      let parts: string[];
      if (this.userToken) {
        parts = this.userToken.split('.');
        this.userToken = JSON.parse(atob(parts[1]));
        if (this.userToken.isSystemAdmin === true) {
          this.sysAdmin = true;
        } else { this.sysAdmin = false; }
      }
    });
    this.hotkeys.newMap();
    this.subscriptions.push(this.hotkeys.addShortcut({ keys: 'shift.control.p', description: 'Project Selection' }).subscribe(() => {
      this.router.navigate(['/project-overview']);
    }));
    this.subscriptions.push(this.hotkeys.addShortcut({ keys: 'shift.control.ArrowDown', description: 'Switch down in navigation' }).subscribe(() => {
      this.switchfieldDown();
    }));
    this.subscriptions.push(this.hotkeys.addShortcut({ keys: 'shift.control.ArrowUp', description: 'Switch up in navigation' }).subscribe(() => {
      this.switchfieldUp();
    }));
  }

  ngAfterContentInit(): void {
    this.userDataService.getIcon(this.authService.getUserID(), this.authService.getUserName()).then(value => {
      if (value !== null) {
        this.selectedAvatar = value;
      } else {
        this.messageService.add({ severity: 'error', closable: true, summary: 'Error loading icon' });
      }
    });
    this.themeDataService.getLogo().then((value: any) => {
      if (value !== null) {
        const doc = new DOMParser().parseFromString(value, 'image/svg+xml');
        doc.documentElement.setAttribute('height', '100%');

        this.logoBoxRef.nativeElement.appendChild(
          this.logoBoxRef.nativeElement.ownerDocument.importNode(doc.documentElement, true)
        );

      } else {
        this.messageService.add({ severity: 'warn', summary: 'No Logo was updated' });

      }

    }).catch((error: HttpErrorResponse) => {
      console.log('No logo to load');
    });
    this.userDataService.getTheme(this.authService.getUserID()).then(value => {
      this.themeDataService.getThemeByID(value).then((iconValue: Theme) => {

        if (iconValue !== null) {

          let color: string = iconValue.colors;
          let parts = color.split('_');
          document.body.style.setProperty('--primary-color', parts[0]);
          document.body.style.setProperty('--accent-color', parts[1]);
          document.body.style.setProperty('--warn-color', parts[2]);
          console.log(parts);
        }
      });
    });
    /**
     * TODO: overload ngAfterContentInit of the Expention panel component
     * Fix for side-nav expantion by loading over url
     * */
    let url: string = this.router.url.toLocaleLowerCase();
    if (
      url.includes('controller') ||
      url.includes('actuator') ||
      url.includes('sensor') ||
      url.includes('controlled-process')) {
      this.dropDown1.open();
    } else if (
      url.includes('control-action') ||
      url.includes('feedback') ||
      url.includes('input-arrow') ||
      url.includes('output-arrow')
    ) {
      this.dropDown2.open();
    }
  }

  ngAfterViewInit(): void {
    this.route.params.subscribe((params: any) => {
      this.projectId = params.id;
      if (this.projectId) {
        this.data.getProjectById(this.projectId).then(value => {
          this.project = value;
        });
      }
    });
    this.name = this.authService.getUserName();

    // Decode UserToken to check for SysAdmin
    this.authService.getUserToken().then(value => {
      this.userToken = value;
      let parts: string[];
      if (this.userToken) {
        parts = this.userToken.split('.');
        this.userToken = JSON.parse(atob(parts[1]));
        if (this.userToken.isSystemAdmin === true) {
          this.sysAdmin = true;
        } else { this.sysAdmin = false; }
      }
    });
  }

  /**
   * Method check the url and then get one element from the sidenav up
   */
  private switchfieldUp(): void {
    let url: string = this.location.path();
    this.ngAfterViewInit();
    let num: number = 0;
    let position: number;

    if (!url.includes('/project/')) {
      // currently not in the sub app
    } else {
      console.log('BBBBBBBBBB');
      for (num = 0; num <= this.navigationUrls.length - 1; num++) {
        if (url.includes(this.navigationUrls[num])) {
          position = num;
          this.positionfound = true;
          console.warn(position);
        }
      }
      if (position - 1 <= this.navigationUrls.length - 1 && position > 0) {
        if (position === 6) {
          this.dropDown1.close();
        }
        if ((position === 10)) {
          this.dropDown1.open();
        }
        if (position === 10) {
          this.dropDown2.close();
        }
        if (position === 14) {
          this.dropDown2.open();
        }
        if (this.positionfound) {
          this.router.navigate(['./project/' + this.projectId + this.navigationUrls[position - 1]]);
          this.positionfound = false;
        }
      }
    }
  }

  /**
   * Method check the url and then get one element from the sidenav down
   */
  private switchfieldDown(): void {
    let url: string = this.location.path();

    let num: number = 0;
    let position: number;

    if (!url.includes('/project/')) {
    } else {
      for (num = 0; num <= this.navigationUrls.length - 1; num++) {
        if (url.includes(this.navigationUrls[num])) {
          position = num;
          this.ngAfterViewInit();
          this.positionfound = true;
        }
      }
      if (position + 1 <= this.navigationUrls.length - 1) {
        if (position === 5 || position === 6 || position === 7 || position === 8) {
          this.dropDown1.open();
        }
        if (!(position === 5 || position === 6 || position === 7 || position === 8)) {
          this.dropDown1.close();
        }
        if (position === 9 || position === 10 || position === 11 || position === 12) {
          this.dropDown2.open();
        }
        if (!(position === 9 || position === 10 || position === 11 || position === 12)) {
          this.dropDown2.close();
        }
        if (this.positionfound) {
          this.router.navigate(['./project/' + this.projectId + this.navigationUrls[position + 1]]);
        }

      }

    }

  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login/']);
  }

  /**
   * Export the project to the download folder
   */
  exportAsFile(): void {
    this.importExportDataServive.getProjectToExportById(this.projectId).then((value) => {
      const blob: Blob = new Blob([JSON.stringify(value)], { type: 'application/json' });

      const fileName: string = this.project.name + '.hazx4';
      const objectUrl: string = URL.createObjectURL(blob);
      const a: HTMLAnchorElement = document.createElement('a') as HTMLAnchorElement;
      a.href = objectUrl;
      a.download = fileName;
      document.body.appendChild(a);
      a.click();

      document.body.removeChild(a);
      URL.revokeObjectURL(objectUrl);

    });

  }

  exportControlStructure(coloured: boolean): void {
    this.controlStructureDataService.getReport(this.projectId, coloured).then((value: Blob) => {
      saveAs(value, this.project.name + '_controlStructure.svg');
    });
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub: Subscription) => sub.unsubscribe());
  }

  shortcutHelp(): void {
    this.hotkeys.showCommands();
  }
}
