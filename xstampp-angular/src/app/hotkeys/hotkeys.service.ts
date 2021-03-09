import { Injectable, Inject } from '@angular/core';
import { EventManager } from '@angular/platform-browser';
import { Observable, Unsubscribable, Subscriber } from 'rxjs';
import { DOCUMENT } from '@angular/common';
import { MatDialog, MatDialogRef } from '@angular/material';
import { HotkeyCommandsComponent } from './hotkey-commands.component';

interface Options {
  element: any;
  description: string | undefined;
  keys: string;
}

@Injectable({
  providedIn: 'root'
})
export class Hotkeys {
  hotkeys: Map<string, string> = new Map();
  defaults: Partial<Options> = {
    element: this.document
  };

  private dialogRef: MatDialogRef<HotkeyCommandsComponent>;

  constructor(private eventManager: EventManager,
    private dialog: MatDialog,
    @Inject(DOCUMENT) private document: Document) {
    this.addShortcut({ keys: 'shift.control.h' }).subscribe(() => {
      this.showCommands();
    });
  }

  addShortcut(options: Partial<Options>): Observable<{ }> {
    const merged = { ...this.defaults, ...options };
    const event = `keydown.${merged.keys}`;

    merged.description && this.hotkeys.set(merged.keys, merged.description);

    return new Observable((observer: Subscriber<{}>) => {
      const handler = (e) => {
        e.preventDefault();
        observer.next(e);
      };

      const dispose = this.eventManager.addEventListener(merged.element, event, handler);

      return () => {
        dispose();
        this.hotkeys.delete(merged.keys);
      };
    });
  }

  showCommands(): void {
    if (this.dialogRef) {
      return;
    }

    this.dialogRef = this.dialog.open(HotkeyCommandsComponent, {
      width: '500px',
      data: this.hotkeys
    });

    this.dialogRef.afterClosed().subscribe(() => {
      this.dialogRef = undefined;
    });
  }

  newMap(): void {
    this.hotkeys = new Map();
  }
}
