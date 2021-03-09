import { Component, Inject, Input, OnDestroy, OnInit, ViewChild, ElementRef } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { ActivatedRoute } from '@angular/router';
import { MessageService } from 'primeng/api';
import { SaveActionDialogComponent, SaveActions } from 'src/app/common/save-action-dialog/save-action-dialog.component';
import { CDSAddObject, CDSCanExecute, CDSReset } from 'src/app/services/change-detection/change-detection-decorator';
import { ChangeDetectionService } from 'src/app/services/change-detection/change-detection-service.service';
import { ProcessModelDataService } from 'src/app/services/dataServices/control-structure/process-model-data.service';
import { DetailedSheetUtils } from '../../common/detailed-sheet/utils/detailed-sheet-utils';
import { AuthService } from '../../services/auth.service';
import { LockResponse, LockService } from '../../services/dataServices/lock.service';
import { SystemDescriptionDataService } from '../../services/dataServices/system-description-data.service';
import { FilterService } from '../../services/filter-service/filter.service';
import { WebsocketService } from '../../services/websocket.service';
import { SystemDescriptionResponseDTO, SYSTEM_DESCRIPTION, UnlockRequestDTO } from '../../types/local-types';
import { Hotkeys } from 'src/app/hotkeys/hotkeys.service';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-system-description',
  templateUrl: './system-description.component.html',
  styleUrls: ['./system-description.component.css']
})

export class SystemDescriptionComponent implements OnInit, OnDestroy {
  @Input() deleteMessage: string = 'You did not save, unsaved changes will be lost?'; // Default delete Message which can be overwritten by Component
  systemDescriptionDTO: SystemDescriptionResponseDTO;
  @CDSAddObject() systemDescription: string;
  lastEdited: string = '';
  projectId: string;
  editable: boolean = false;
  cancelDialogSubscription = null;
  subscriptions: Subscription[] = [];

  @ViewChild('btnBolt', { static: false }) btnBoltRef: ElementRef<HTMLButtonElement>;
  @ViewChild('btnItalic', { static: false }) btnItalicRef: ElementRef<HTMLButtonElement>;
  @ViewChild('btnUdnerline', { static: false }) btnUnderlineRef: ElementRef<HTMLButtonElement>;
  @ViewChild('btnStrike', { static: false }) btnStrikeRef: ElementRef<HTMLButtonElement>;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly systemDescriptionDataService: SystemDescriptionDataService,
    private readonly messageService: MessageService,
    private readonly wsService: WebsocketService,
    private readonly authService: AuthService,
    private readonly cds: ChangeDetectionService,
    private readonly filterService: FilterService,
    private readonly lockService: LockService,
    private readonly router: Router,
    private readonly processModelService: ProcessModelDataService,
    public dialog: MatDialog,
    private readonly hotkeys: Hotkeys) {

    // Websocket implementation
    this.authService.getProjectToken().then((token: string) => {
      this.wsService.connect('subscribe', SYSTEM_DESCRIPTION, token, (data) => {
        this.loadText();
        this.messageService.add({ severity: 'info', summary: 'User ' + data.displayName + ' updated the data!' });
      });
    }).catch((err: Error) => {
      console.log(err);
    });
  }

  ngOnInit(): void {
    this.cancelDialogSubscription = this.filterService.CancelDialogEmitter.subscribe(() => {
      const unlockDTO: UnlockRequestDTO = { id: this.projectId, entityName: SYSTEM_DESCRIPTION };
      this.lockService.unlockEntity(this.projectId, unlockDTO).then(() => {
        this.editable = false;
        this.loadText();
      }).catch(() => {
        this.editable = false;
        this.loadText();
        this.messageService.add({ severity: 'error', summary: 'unable to cancel' });
      });
    });

    this.subscriptions.push(this.hotkeys.addShortcut({ keys: 'shift.control.s', description: 'Save' }).subscribe(() => {
      this.onSave();
    }));
    this.subscriptions.push(this.hotkeys.addShortcut({ keys: 'shift.control.backspace', description: 'Cancel' }).subscribe(() => {
      this.onCancel();
    }));
    this.route.parent.params.subscribe((value: any) => {
      this.projectId = value.id;
      this.loadText();
    });
    this.subscriptions.push(this.hotkeys.addShortcut({ keys: 'shift.control.n', description: 'Edit' }).subscribe(() => {
      this.onEdit();
    }));
  }

  onToggleButton(type: BtnType): void {
    switch (type) {
      case BtnType.BOLT: {
        this.btnBoltRef.nativeElement.click();
        break;
      }
      case BtnType.ITALIC: {
        this.btnItalicRef.nativeElement.click();
        break;
      }
      case BtnType.STRIKE: {
        this.btnStrikeRef.nativeElement.click();
        break;
      }
      case BtnType.UNDERLINE: {
        this.btnUnderlineRef.nativeElement.click();
        break;
      }
    }
  }

  loadText(): void {
    this.systemDescriptionDataService.getSystemDescription(this.projectId).then((sd: SystemDescriptionResponseDTO) => {
      this.systemDescriptionDTO = sd;
      this.systemDescription = sd.description;
      this.lastEdited = new Date(sd.lastEdited).toLocaleString('en-US');
      this.lastEdited = 'Last-Edited:   ' + this.lastEdited;
    }).catch((error: Error) => {
      console.log(error);
      if (error.message.startsWith('Cannot retrieve system description. There is no system description with the id: ')) {
        this.systemDescriptionDataService.createSystemDescription(this.projectId, { description: this.systemDescription });
      }
    });
  }

  @CDSReset()
  onSave(): void {
    this.systemDescriptionDataService.alterSystemDescription(this.projectId, { description: this.systemDescription })
      .catch((error: Error) => console.log(error)).finally(() => {
        this.editable = false;
        this.loadText();
        this.messageService.add({ severity: 'success', summary: 'description successfully saved' });
      });
  }

  @CDSCanExecute('startDialog')
  onCancel(): void {
    this.editable = false;
    this.loadText();
  }

  startDialog(): void {
    const dialogRef = this.dialog.open(SaveActionDialogComponent, {
      width: '450px',
      disableClose: true,
    });

    dialogRef.afterClosed().subscribe((result: SaveActions): void => {
      switch (result) {
        case SaveActions.CANCEL: {
          break;
        }
        case SaveActions.CLOSE: {
          this.editable = false;
          this.loadText();
          this.cds.resetStates();
          break;
        }
        case SaveActions.SAVE_AND_CLOSE: {
          this.onSave();
          break;
        }
      }
      console.log('result: ' + result);
    });
  }

  async onEdit(): Promise<void> {
    await this.lockService.lockEntity(this.projectId, {
      id: this.projectId,
      entityName: SYSTEM_DESCRIPTION,
      expirationTime: DetailedSheetUtils.calculateLockExpiration()
    }).then((success: LockResponse) => {
      console.log(success);
      this.editable = true;
    }).catch((er: Error) => {
      console.log(er);
      this.messageService.add({ severity: 'error', summary: 'the field is locked' });
    });
  }

  @CDSReset()
  ngOnDestroy(): void {
    this.systemDescription = '';
    this.systemDescriptionDTO = null;
    this.subscriptions.forEach((subscription: Subscription) => subscription.unsubscribe());
  }
}

@Component({
  selector: 'app-cancel-dialog',
  templateUrl: './cancel-dialog.component.html',
  styleUrls: ['./system-description.component.css']
})
export class CancelDialogComponent {

  constructor(public filterService: FilterService, public dialogRef: MatDialogRef<CancelDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) {
  }

  onCancelClick(): void {
    this.dialogRef.close();
  }

  onContinueClick(): void {
    this.filterService.CancelDialogEmitter.emit(true);
  }
}

export enum BtnType {
  STRIKE,
  ITALIC,
  UNDERLINE,
  BOLT,
}
