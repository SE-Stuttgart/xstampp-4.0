import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

export enum SaveActions {
  SAVE_AND_CLOSE = 'SAVE_AND_CLOSE',
  CLOSE = 'CLOSE',
  CANCEL = 'CANCEL',
}

export interface DialogData {
  isRouting: boolean;
}

@Component({
  selector: 'app-save-action-dialog',
  templateUrl: './save-action-dialog.component.html',
  styleUrls: ['./save-action-dialog.component.css']
})
export class SaveActionDialogComponent {
  isRouting: boolean;

  constructor(
    public dialogRef: MatDialogRef<SaveActionDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData,
  ) {
    if (!!data) {
      this.isRouting = !!data.isRouting;
    }
  }

  onCancel(): void {
    this.dialogRef.close(SaveActions.CANCEL);
  }

  onClose(): void {
    this.dialogRef.close(SaveActions.CLOSE);
  }

  onSave(): void {
    this.dialogRef.close(SaveActions.SAVE_AND_CLOSE);
  }
}
