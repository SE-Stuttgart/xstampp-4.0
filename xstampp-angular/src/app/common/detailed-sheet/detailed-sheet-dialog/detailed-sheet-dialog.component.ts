import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

export interface DialogData {
  name: string;
  title: string;
  placeHolder: string;
}

@Component({
  selector: 'app-detailed-sheet-dialog',
  templateUrl: './detailed-sheet-dialog.component.html',
  styleUrls: ['./detailed-sheet-dialog.component.css']
})

export class DetailedSheetDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<DetailedSheetDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData) { }

  onNoClick(): void {
    this.dialogRef.close();
  }

  onOkClick(): void {
    this.dialogRef.close(this.data.name);
  }
}
