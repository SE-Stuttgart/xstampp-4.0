import { Component } from '@angular/compiler/src/core';
import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material';
import { ActivatedRouteSnapshot, RouterStateSnapshot, CanDeactivate } from '@angular/router';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { SaveActionDialogComponent, SaveActions } from 'src/app/common/save-action-dialog/save-action-dialog.component';
import { ChangeDetectionService } from './change-detection-service.service';

export interface DeactivationGuarded {
  canDeactivate(nextState: string): Observable<boolean> | Promise<boolean> | boolean;
}

@Injectable({
  providedIn: 'root'
})
export class CanDeactivateGuard implements CanDeactivate<any> {
  component: Object;
  route: ActivatedRouteSnapshot;
  dialogIsOpen: boolean = false;
  private mode: DeactivationMode = DeactivationMode.ALLOWED;

  constructor(
    private readonly cds: ChangeDetectionService,
    private readonly dialog: MatDialog,
  ) { }

  // don't delete unused parameters cause it will crash
  canDeactivate(
    component: Component,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState: RouterStateSnapshot
  ): Observable<boolean> | Promise<boolean> | boolean {
    if (this.mode === DeactivationMode.DENIED) {
      return this.openDialog();
    }
    if (this.cds.wasEdited()) {
      // if for exceptions
      if (
        (currentState.url.includes('STEP2') && nextState.url.includes('STEP4')) ||
        (currentState.url.includes('STEP4') && nextState.url.includes('STEP2'))
      ) {
        return of(true);
      } else {
        return this.openDialog();
      }
    } else {
      this.cds.resetStates();
      return of(true);
    }
  }

  openDialog(): Observable<boolean> {
    if (!this.dialogIsOpen) {
      this.dialogIsOpen = true;
      const dialogRef = this.dialog.open(SaveActionDialogComponent, {
        width: '450px',
        disableClose: true,
        data: {
          isRouting: true,
        }
      });

      return dialogRef.afterClosed().pipe(
        map((value: SaveActions): boolean => {
          switch (value) {
            case SaveActions.CANCEL: {
              console.log('called');
              this.dialogIsOpen = false;
              return false;
            }
            case SaveActions.CLOSE: {
              this.cds.resetStates();
              this.mode = DeactivationMode.ALLOWED;
              console.log('called2');
              this.dialogIsOpen = false;
              return true;
            }
            case SaveActions.SAVE_AND_CLOSE: {
              this.mode = DeactivationMode.ALLOWED;
              this.dialogIsOpen = false;
              return true;
            }
          }
        })
      );
    }
  }

  setDeactivationMode(mode: DeactivationMode): void {
    this.mode = mode;
  }
}

export enum DeactivationMode {
  ALLOWED,
  DENIED,
}
