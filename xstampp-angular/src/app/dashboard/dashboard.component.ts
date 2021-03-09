import { CdkDragDrop, moveItemInArray, transferArrayItem } from '@angular/cdk/drag-drop';
import { DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Ng4LoadingSpinnerService } from 'ng4-loading-spinner';
import { SheetMode } from '../common/detailed-sheet/detailed-sheet.component';
import { DetailedField } from '../common/entities/detailed-field';
import { TableColumn } from '../common/entities/table-column';
import { MainTableComponent } from '../common/main-table/main-table.component';
import { ProjectRequestDTO, ProjectResponseDTO } from '../types/local-types';
import { AuthService } from './../services/auth.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
  providers: [DatePipe, MainTableComponent]
})
export class DashboardComponent implements OnInit {

  userName: string;
  userId: string;
  sysAdmin: boolean = false;
  change: boolean = false;
  dragVisible: boolean = false;
  change2: boolean = false;
  dragVisible2: boolean = false;
  change3: boolean = false;
  dragVisible3: boolean = false;
  today: any;
  panelOne: string[] = [
    'date wasnt updated',
  ];

  panelTwo: string[] = [
    'entity of the day: loss',
  ];

  panelThree: string[] = [
    'uesless card nr.1234435',
  ];

  // project selections

  SheetMode: typeof SheetMode = SheetMode;
  projects: ProjectResponseDTO[] = [];
  columns: Array<TableColumn>;
  project: ProjectRequestDTO;
  sheetMode: SheetMode = SheetMode.Closed;
  fields: DetailedField[];
  title: string = 'Project';
  userToken;
  private id: string;
  groups = [];
  selectedGroup;
  navigationTableDefinitions = [];
  disableSaveButton: boolean = false;

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly datePipe: DatePipe,
    private readonly spinnerService: Ng4LoadingSpinnerService
  ) {
    this.navigationTableDefinitions[0] = {
      // dataSource for Navigation only allows one Element
      dataSource: [],
      style: { width: '100%' },
      columnHeaderName: 'Your Groups'
    };
    this.spinnerService.show();

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

  ngOnInit(): void {
    this.userName = this.authService.getUserName();
    this.userId = this.authService.getUserID();
  }

  //to display buttons for delete and move
  react(): void {
    if (!this.change) {
      this.change = true;
      this.dragVisible = !this.dragVisible;
    } else {
      this.change = false;
      this.dragVisible = !this.dragVisible;
    }
  }
  react2(): void {
    if (!this.change2) {
      this.change2 = true;
      this.dragVisible2 = !this.dragVisible2;
    } else {
      this.change2 = false;
      this.dragVisible2 = !this.dragVisible2;
    }
  }
  react3(): void {
    if (!this.change3) {
      this.change3 = true;
      this.dragVisible3 = !this.dragVisible3;
    } else {
      this.change3 = false;
      this.dragVisible3 = !this.dragVisible3;
    }
  }

  /**
   * for drag material card from one to another container
   * @param event from drag and drop event
   */
  drop(event: CdkDragDrop<string[]>): void {
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      transferArrayItem(event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex);
    }
  }
}
