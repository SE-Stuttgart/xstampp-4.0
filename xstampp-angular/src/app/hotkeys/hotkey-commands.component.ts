import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

@Component({
  selector: './app-hotkey-commands.component.ts',
  templateUrl: './hotkey-commands.component.html',
  styleUrls: ['./hotkey-commands.component.scss']
})
export class HotkeyCommandsComponent implements OnInit {
  hotkeys: {}[] = Array.from(this.data);

  constructor(public dialogRef: MatDialogRef<HotkeyCommandsComponent>,
    @Inject(MAT_DIALOG_DATA) public data) { }

  ngOnInit(): void {
  }
}
