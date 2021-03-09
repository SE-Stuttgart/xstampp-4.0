import { Component, EventEmitter, Input, OnInit, Output, OnDestroy } from '@angular/core';
import { MatDialog } from '@angular/material';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { MessageService } from 'primeng/api';
import { CDSCanExecute, CDSReset } from 'src/app/services/change-detection/change-detection-decorator';
import { ChangeDetectionService } from 'src/app/services/change-detection/change-detection-service.service';
import { LockResponse, LockService } from '../../../services/dataServices/lock.service';
import { ArrowAndBoxType, CONTROL_STRUCTURE } from '../../../types/local-types';
import { SaveActionDialogComponent, SaveActions } from '../../save-action-dialog/save-action-dialog.component';
import { CSDiaType, CSShape } from '../cs-types';
import { DRAG_ARROW_CORRECT, DRAG_ELEMENTS_CORRECT, UNABLE_TO_CANCEL } from 'src/app/globals';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-cs-toolbar',
  templateUrl: './cs-toolbar.component.html',
  styleUrls: ['./cs-toolbar.component.scss']
})
export class CsToolbarComponent implements OnInit, OnDestroy {
  @Input() diaType: CSDiaType;
  @Output() zoomEvent: EventEmitter<any> = new EventEmitter();
  @Output() saveEvent: EventEmitter<void> = new EventEmitter();
  @Output() cancelEvent: EventEmitter<void> = new EventEmitter();
  projectId: string;

  subscriptions: Subscription[] = [];

  itsFishTime: boolean = false;

  getIconpath(icon: string): string {
    return this.itsFishTime ? 'assets/icons/toolbar_icons/fish/' + icon + '.png' : 'assets/icons/toolbar_icons/' + icon + '.svg';
  }

  constructor(
    private readonly lockService: LockService,
    private readonly messageService: MessageService,
    public dialog: MatDialog,
    private readonly cds: ChangeDetectionService,
    private readonly route: ActivatedRoute) {
    this.subscriptions.push(this.route.parent.params.subscribe((value: any) => {
      this.projectId = value.id;
    }));

    this.subscriptions.push(this.route.paramMap.subscribe((value: ParamMap) => {
      if (value.has('fish')) {
        this.itsFishTime = value.get('fish').toLowerCase() === 'fish' || value.get('fish').toLowerCase() === 'fisch';
      }
    }));
  }

  ngOnInit(): void {
    this.cds.initStates(new Map([
      ['CSObject', false]
    ]));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub: Subscription) => sub.unsubscribe());
  }

  get isStep2(): boolean {
    return this.diaType === CSDiaType.STEP2;
  }

  showHint(isArrow: boolean = false): void {
    if (isArrow) {
      this.messageService.add({ severity: 'info', detail: DRAG_ARROW_CORRECT });
    } else {
      this.messageService.add({ severity: 'info', detail: DRAG_ELEMENTS_CORRECT });
    }

  }

  @CDSReset()
  saveCS(): void {
    this.saveEvent.emit();
  }

  @CDSCanExecute('startDialog')
  cancelCS(): void {
    this.lockService.unlockEntity(this.projectId, {
      entityName: CONTROL_STRUCTURE, id: this.projectId
    }).then((success: LockResponse) => {
      this.cancelEvent.emit();
    }).catch((er: Error) => {
      console.log(er);
      this.messageService.add({ severity: 'error', summary: UNABLE_TO_CANCEL });
    }
    );
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
          this.cds.resetStates();
          this.cancelCS();
          break;
        }
        case SaveActions.SAVE_AND_CLOSE: {
          this.saveCS();
          break;
        }
      }
    });
  }

  drag(ev: DragEvent, type: ArrowAndBoxType, shape: CSShape): void {
    ev.dataTransfer.setData('shape', shape);
    ev.dataTransfer.setData('type', type);
  }

  zoom(zoomIn: boolean): void {
    this.zoomEvent.emit(zoomIn);
  }
}

export enum CsToolbarMode {
  READONLY, CLOSED, OPEN
}
