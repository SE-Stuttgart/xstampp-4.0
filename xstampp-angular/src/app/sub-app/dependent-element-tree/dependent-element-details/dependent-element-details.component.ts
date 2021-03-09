import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { MessageService } from 'primeng/api';
import { DependentElementNode } from '../dependent-elements-types.component';

/**
 * This component shows detailed information of elements.
 */
@Component({
  selector: 'app-dependent-element-details',
  templateUrl: './dependent-element-details.component.html',
  styleUrls: ['./dependent-element-details.component.css']
})
export class DependentElementDetailsComponent implements OnInit {

  entityTitle: string;
  showRule: boolean = false;
  nodeToshow: DependentElementNode;
  maxAutoSize: number = 200;
  entity: any;
  removeShapeMessage: string = 'Remove Shape?';
  showInformation: boolean = false;
  showDeleteShapeInformation: boolean = false;

  constructor(@Inject(MAT_DIALOG_DATA) public data: any,
              public dialogRef: MatDialogRef<DependentElementDetailsComponent>,
              public readonly messageService: MessageService) {

  }

  ngOnInit(): void {
    if (this.data.node) {
      this.nodeToshow = this.data.node;
      this.entity = this.nodeToshow.entity;
    } else {
      if (this.data.showDeleteModelMessage) {
        this.showDeleteShapeInformation = true;
      } else {
        this.showInformation = true;
      }
    }
    this.entityTitle = this.data.node.entityTitle;
    if (this.entityTitle === 'Rule') {
      this.showRule = true;
    }
  }

  private removeShape(): void {
    this.dialogRef.close(true);
  }

  cancelDetails(): void {
    this.dialogRef.close();
  }
}
