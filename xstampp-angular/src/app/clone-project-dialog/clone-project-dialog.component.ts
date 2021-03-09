import { Component, OnInit, Inject } from '@angular/core';
import { ProjectDataService } from '../services/dataServices/project-data.service';
import { ProjectResponseDTO, GroupRequestDTO } from '../types/local-types';
import { MessageService } from 'primeng/api';
import { GroupDataService } from '../services/dataServices/group-data.service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { HttpErrorResponse } from '@angular/common/http';
import { ImportExportDataService } from '../services/dataServices/import-export-data.service';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-clone-project-dialog',
  templateUrl: './clone-project-dialog.component.html',
  styleUrls: ['./clone-project-dialog.component.css']
})
export class CloneProjectDialogComponent implements OnInit {

  selectedProject: string;
  allProjects;
  userId: string;
  allGroups;


  constructor(private readonly projectDataService: ProjectDataService,
    private readonly messageService: MessageService,
    private readonly groupDataService: GroupDataService,
    public dialogRef: MatDialogRef<CloneProjectDialogComponent>,
    public readonly importExportService: ImportExportDataService,
    public readonly authService: AuthService,
    @Inject(MAT_DIALOG_DATA) public data) {
    this.userId = this.authService.getUserID();

  }

  ngOnInit(): void {
    this.loadAllProjects();
    this.allProjects = [];
  }

  loadAllProjects(): void {
    this.groupDataService.getAllGroupsOfUser(this.userId).then(value => {
      this.allGroups = value;
      this.allGroups.forEach((element) => {
        this.groupDataService.getAllProjectsForGroup(element.id).then((value) => {
          for (let i = 0; i < value.length; i++) {
            this.allProjects.push(value[i]);
          }

        }
        ).catch((reason: HttpErrorResponse) => {
          this.messageService.add({ severity: 'error', summary: 'get Projects for Group failed', detail: reason.message });
        });

      });
    }).catch((reason: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: 'No project found', detail: reason.message });
    });
  }

  clone(data): void {
    this.projectDataService.getProjectById(this.selectedProject).then((value) => {
      value.groupId = this.data.selectedGroup.id;
      this.importExportService.cloneProject(this.selectedProject, value).then((value) => {
        this.dialogRef.close(true);
      }).catch((reason: HttpErrorResponse) => {
        this.messageService.add({ severity: 'error', summary: 'Clone Project failed', detail: reason.message });
        this.dialogRef.close(false);
      });
    }).catch((reason: HttpErrorResponse) => {
      this.messageService.add({ severity: 'error', summary: 'no Projectid', detail: reason.message });
      this.dialogRef.close(false);
    });
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }

}
