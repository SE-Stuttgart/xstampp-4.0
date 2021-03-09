import { Location } from '@angular/common';
import {
  Component,
  ElementRef,
  EventEmitter,
  Inject,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
  SimpleChanges,
  ViewChild
} from '@angular/core';
import { FormControl, Validators, AbstractControl, ValidationErrors, AsyncValidatorFn } from '@angular/forms';
import { ENTER, COMMA } from '@angular/cdk/keycodes';
import { FilterService } from '../../services/filter-service/filter.service';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA, MatChipInputEvent, MatAutocomplete, MatAutocompleteSelectedEvent, MatAutocompleteTrigger, MatSelectChange, MatOption, MatIconRegistry } from '@angular/material';
import { Observable, Subscription } from 'rxjs';
import { startWith, map, switchMap } from 'rxjs/operators';
import { DomSanitizer } from '@angular/platform-browser';
import { FilterParserService } from 'src/app/services/filter-parser-service/filter-parser.service';
import { ImportExportDataService } from 'src/app/services/dataServices/import-export-data.service';
import { REALLY_TO_DELETE } from 'src/app/globals';
import { Hotkeys } from 'src/app/hotkeys/hotkeys.service';
import { SelectedEntity } from '../../sub-app/dependent-element-tree/dependent-elements-types.component';
import { CreateDependencyTreeComponent } from '../../sub-app/dependent-element-tree/create-dependency-tree/create-dependency-tree.component';
import { SheetMode } from '../detailed-sheet/detailed-sheet.component';
import { MainTableComponent } from '../main-table/main-table.component';
import { CONTROLLER, CONTROLLER_CONSTRAINT, ControllerDTO } from '../../types/local-types';
import { DetailedSheetUtils } from '../detailed-sheet/utils/detailed-sheet-utils';
import { DependentElementTreeComponent } from '../../sub-app/dependent-element-tree/dependent-element-tree.component';

@Component({
  selector: 'app-action-bar',
  templateUrl: './action-bar.component.html',
  styleUrls: ['./action-bar.component.css']
})
export class ActionBarComponent implements OnInit, OnChanges, OnDestroy {
  @Input() additionalDeleteMessage: string = ''; // Default delete Message which can be overwritten by Component
  @Input() additionalChangeEntityStateMessage: string = '';
  @Input() showDelete: boolean = true;
  @Input() showAdd: boolean = true;
  @Input() showChangeState: boolean = true;
  @Input() disableAdd: boolean = false;
  @Input() disableDelete: boolean = false;
  @Input() selectedEntity: SelectedEntity<Object>;
  @Input() disableChangeState: boolean = false;
  @Input() showMenu: boolean = false;
  @Input() disableMenu: boolean = false;
  @Input() showBack: boolean = false;
  @Input() showUndoRedo: boolean = false;
  @Input() showChipsFilter: boolean = false;
  @Input() suggestedFilterElements; // Todo Type to FilterOptionObject
  @Output() create: EventEmitter<void> = new EventEmitter();
  @Output() delete: EventEmitter<any[]> = new EventEmitter();
  @Output() changeEntityState: EventEmitter<{ list: any[], state: string }> = new EventEmitter();
  @Output() projectNew: EventEmitter<any[]> = new EventEmitter();
  @Output() projectImport: EventEmitter<any[]> = new EventEmitter();
  @Output() projectImportFromEclipsePlugin: EventEmitter<any[]> = new EventEmitter();
  @Output() projectCopy: EventEmitter<any[]> = new EventEmitter();
  @Output() projectExample: EventEmitter<any[]> = new EventEmitter();
  responseSubscription = null;
  responseEntityStateChangeSubscription = null;
  selectionSubscription = null;
  deleteDisabled: boolean = true; // Is true if there is no item selected -> true = Icon disabled
  stateDisabled: boolean = true;
  selectionCount: number;
  queryInvalid: boolean = false;
  deleteMessage: string; // Final delete message which is displayed in the delete dialog window
  changeEntityStateMessage: string;
  entityStateToBeAttained: string;
  parseSuccessSubscription: Subscription = null;
  parseErrorMsgSubscription: Subscription = null;
  parseErrorMsg$: Observable<string>;
  defaultFilterHintText: string = "Want <b>multi-word queries</b>? Press Help"
  filterHintText: string = "Want <b>multi-word queries</b>? Press Help";
  file: File;

  mainTableQuery: FormControl = new FormControl();

  linksCtrl: FormControl = new FormControl();
  chips = [];
  subscriptions: Subscription[] = [];
  filteredElements: Observable<any[]>;
  readonly separatorKeysCodes: number[] = [ENTER, COMMA];
  @ViewChild('displayedInput', { static: true }) displayedInput: ElementRef<HTMLInputElement>;
  @ViewChild('auto', { static: true }) matAutocomplete: MatAutocomplete;
  @ViewChild('trigger', { static: true }) matAutocompleteTrigger: MatAutocompleteTrigger;

  constructor(
    private readonly location: Location,
    private readonly filterService: FilterService,
    private readonly importExportDataServive: ImportExportDataService,
    public createDependentElementDialog: MatDialog,
    public dialog: MatDialog,
    iconRegistry: MatIconRegistry,
    sanitizer: DomSanitizer,
    private filterParserService: FilterParserService,
    private readonly hotkeys: Hotkeys) {

    iconRegistry.addSvgIcon(
      'assignment_clear',
      sanitizer.bypassSecurityTrustResourceUrl('assets/icons/assignment_clear.svg'));
      this.parseErrorMsg$ = this.filterParserService.parsingErrorMsg$;
  }

  ngOnInit(): void {
    this.mainTableQuery = new FormControl('', [this.checkQueryValid.bind(this)]);
    //this.mainTableQuery = new FormControl('', [], [this.checkQueryValidAsync(this.filterParserService)]);
    this.responseSubscription = this.filterService.DeleteResponseEmitter.subscribe(value => {
      this.callbackDelete(value);
    });
    this.responseEntityStateChangeSubscription = this.filterService.ChangeEntityStateResponseEmitter.subscribe(value => {
      this.callbackChangeEntityState(value, this.entityStateToBeAttained);
    });
    this.selectionSubscription = this.filterService.SelectedEntryExists.subscribe(value => {
      this.selectionCount = value;
      this.setDeleteButtonStateAndChangeEntityStateDropDownState(value);
    });
    this.parseSuccessSubscription = this.filterParserService.parsingSuccessSubject.subscribe(valid =>  {this.queryInvalid = !valid;
       console.log('queryInvalid:' + this.queryInvalid);
       this.mainTableQuery.updateValueAndValidity();
       this.mainTableQuery.markAsDirty();
       this.mainTableQuery.markAsTouched();
      console.log('T');
      if(valid) {
        this.filterHintText = this.defaultFilterHintText;
      } else {
        if(this.filterHintText === this.defaultFilterHintText) {
          this.filterHintText = "The provided query is invalid";
        }
      }
    });
    this.parseErrorMsgSubscription = this.filterParserService.parsingErrorMsgSubject.subscribe(msg => {
      if(msg && msg.trim() != '') {
        this.filterHintText = msg;
      }
    });
    this.subscriptions.push(this.hotkeys.addShortcut({ keys: 'shift.control.i', description: 'Import project' }).subscribe(() => {
      this.importProject();
    }));
  }

  goBack(): void {
    this.location.back();
  }

  logClick(): void {
    this.openQueryLangExplanationDialog();
  }
  // TODO: add hint if no suggestion are available
  ngOnChanges(_changes: SimpleChanges): void {
    this.filteredElements = this.linksCtrl.valueChanges
      .pipe(
        startWith(null),
        map((enteredString: string | null) => this.filterForOptions(enteredString))
      );
  }

  changeEntityStateDialog(event: MouseEvent, state: string): void {
    // Get the state that the analyst wants to put the entity in

    this.entityStateToBeAttained = state;

    if (this.selectionCount > 1) {
      if (this.additionalChangeEntityStateMessage !== '') {
        this.changeEntityStateMessage = 'Really want to change the state of the ' + this.selectionCount + ' elements to ' + this.entityStateToBeAttained.toLowerCase() + '? \n\n';
      } else {
        this.changeEntityStateMessage = 'Really want to change the state of the ' + this.selectionCount + ' elements to ' + this.entityStateToBeAttained.toLowerCase() + '?';
      }
    } else if (this.selectionCount === 1) {
      if (this.additionalChangeEntityStateMessage !== '') {
        this.changeEntityStateMessage = 'Really want to change the state of this element to ' + this.entityStateToBeAttained.toLowerCase() + '? \n\n';
      } else {
        this.changeEntityStateMessage = 'Really want to change the state of this element to ' + this.entityStateToBeAttained.toLowerCase() + '?';
      }
    }

    const dialogRef1: MatDialogRef<ChangeEntityStateDialogComponent, any> = this.dialog.open(ChangeEntityStateDialogComponent, {
      width: '275px',
      data: { description: this.changeEntityStateMessage }
    });
  }

  // delete logic -> click -> deleteDialog ->onClickDelete -> DeleteRequestEmitter.emit -> DeleteRequestEmitter in MainTable triggers ->
  // DeleteResponseEmitter.emit(selectedRows) ->  DeleteResponseEmitter in ActionbarComponent triggers -> calls callBackDelete ->
  // this.delete which is @Output und is visible to the parentComponent Project/hazard,Loss,... Componets emits rows -> Compnent (delete) callback will handle delete
  deleteDialog(moveTree?: boolean): void {
    if (this.selectionCount > 1) {
     if (this.additionalDeleteMessage !== '') {
        this.deleteMessage = 'Really want to delete the ' + this.selectionCount + ' elements? \n\n' + this.additionalDeleteMessage;
      } else {
        this.deleteMessage = 'Really want to delete the ' + this.selectionCount + ' elements?';
      }
    } else if (this.selectionCount === 1) {
      if (this.additionalDeleteMessage !== '') {
        this.deleteMessage = REALLY_TO_DELETE + ' \n\n' + this.additionalDeleteMessage;
      } else {
        this.deleteMessage = REALLY_TO_DELETE;
      }
    }

    if (this.selectedEntity) {
      this.selectedEntity.moveTree = moveTree;
      const dialogRef = this.createDependentElementDialog.open(DependentElementTreeComponent, {
        width: '60%',
        height: 'auto',
        data: {
          selectedEntityData: this.selectedEntity,
        },
        disableClose: true
      });
    } else {
      const dialogRef: MatDialogRef<DeleteDialogComponent, any> = this.dialog.open(DeleteDialogComponent, {
        width: '245px',
        data: { description: this.deleteMessage }
      });
    }
  }

    openQueryLangExplanationDialog(): void {
      const dialogRef = this.dialog.open(QueryLanguageExplanationDialog,
        {    autoFocus: false,
      });

      dialogRef.afterClosed().subscribe(result => {
        console.log('The dialog was closed. result:' + result);
      });
    }

  setDeleteButtonStateAndChangeEntityStateDropDownState(count: number): void {
    if (count === 0) {
      this.deleteDisabled = true;
      this.stateDisabled = true;
    } else {
      this.deleteDisabled = false;
      this.stateDisabled = false;
    }
  }

  onCreate(): void {
    this.create.emit();
  }

  newProject(): void {
    this.projectNew.emit();
  }

  importProject(): void {
    this.projectImport.emit();
  }

  copyProject(): void {
    this.projectCopy.emit();
  }
  importProjectFromEclipsePlugin(){
  this.projectImportFromEclipsePlugin.emit();
}
  exampleProject():  void {
    this.projectExample.emit();
  }

  fireFilterEvent(event): void {
    this.mainTableQuery.markAsDirty();
    this.filterService.FilterEmitter.emit(event);
  }

  remove(chip, key): void {
    const index = this.chips.indexOf(chip);
    if (index >= 0) {
      this.chips.splice(index, 1);
      this.displayedInput.nativeElement.value = '';
      this.linksCtrl.setValue('');
    }
    this.filterService.AdvancedFilterEmitter.emit(this.chips);
  }

  openAutoComplete(): void {
    this.matAutocompleteTrigger.openPanel();
  }

  selected(event: MatAutocompleteSelectedEvent): void {
    this.chips.push(event.option.value);
    this.displayedInput.nativeElement.value = '';
    this.linksCtrl.setValue('');
    this.filterService.AdvancedFilterEmitter.emit(this.chips);
  }

  ngOnDestroy(): void {
    if (this.responseSubscription) {
      this.responseSubscription.unsubscribe();
    }
    if (this.selectionSubscription) {
      this.selectionSubscription.unsubscribe();
    }
    if (this.parseSuccessSubscription) {
      this.parseSuccessSubscription.unsubscribe();
    }
    if(this.responseEntityStateChangeSubscription) {
      this.responseEntityStateChangeSubscription.unsubscribe() ;
    }
    this.filterParserService.disconnect();
    this.subscriptions.forEach((sub: Subscription) => sub.unsubscribe());
  }

  private  callbackDelete(list: any[]): void {
    if (this.delete) {
      this.delete.emit(list);
    }
  }

  private callbackChangeEntityState(list: any[], stateToBeAttained: string): void {
    this.changeEntityState.emit({ list: list, state: stateToBeAttained });
  }

  // Filter for the autoSuggestion inside the advanced Filter
  private filterForOptions(input: string): any {
    let filterString: string = typeof input === 'string' ? input.trim() : '';
    // let filterString = input.trim();
    filterString = filterString !== '' ? input.toLowerCase().trim() : '';
    return this.suggestedFilterElements.filter(elem =>
      this.chips.find(e => e.id === elem.id) === undefined  // filter all elems which currently are not already displayed as chip
      && ((elem.name.toLowerCase().indexOf(filterString) > -1) // filter if filterString is in name or filterString is in id
        || elem.id.toString().indexOf(filterString) > -1));
  }


  private checkQueryValid(control: AbstractControl): ValidationErrors | null {
    if(this.getQueryInvalid()) {
      return  {'queryInvalid': true};
    }
    return null;
  }


  private checkQueryValidAsync(filterPS: FilterParserService): AsyncValidatorFn {
    return (control: AbstractControl) => {
      let v = filterPS.parsingSuccess$.pipe(map(
        (succ: boolean) => {
          if (succ) return null;
          return  {'queryInvalid': true};
        }
      ));
      return v;
    };
  }

  private getQueryInvalid(): boolean{
    return this.queryInvalid;
  }
}

@Component({
  selector: 'app-delete-dialog',
  templateUrl: './delete-dialog.component.html',
  styleUrls: ['./action-bar.component.css']
})
export class DeleteDialogComponent {

  private delete: boolean = true;

  constructor(public filterService: FilterService, public dialogRef: MatDialogRef<DeleteDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) { }

  onNoClick(): void {
    this.dialogRef.close();
  }

  onDeleteClick(event: MouseEvent): void {
    if (this.data.tree) {
      this.dialogRef.close(this.delete);
    } else {
      this.filterService.DeleteRequestEmitter.emit();
      this.filterService.ClearSelectionEmitter.emit(true);
    }
  }
}

@Component({
  selector: 'app-change-entity-state-dialog',
  templateUrl: './change-entity-state.component.html',
  styleUrls: ['./action-bar.component.css']
})
export class ChangeEntityStateDialogComponent {

  constructor(public filterService: FilterService, public dialogRef1: MatDialogRef<ChangeEntityStateDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) { }

  onNoClick(): void {
    this.dialogRef1.close();
  }

  onChangeEntityStateClick(event: MouseEvent): void {
    this.filterService.ChangeEntityStateRequestEmitter.emit();
    this.filterService.ClearSelectionEmitter.emit(true);
  }
}


@Component({
  selector: 'query-language-explanation-dialog',
  templateUrl: 'query-language-explanation-dialog.html',
  styleUrls: ['./action-bar.component.css']

})
export class QueryLanguageExplanationDialog {

  valueExplanations: ExplanationTableData[];
  queryExplanations: ExplanationTableData[];
  queryColumns: string[];
  valueColumns: string[];
  constructor(
    public dialogRef: MatDialogRef<QueryLanguageExplanationDialog>,
    @Inject(MAT_DIALOG_DATA) public data: any) {
      this.queryExplanations = [
        {what: 'a single term - only double quotes " are allowed', explanation: '"sensors"'},
        {what: 'a sequence of words - only double quotes " are allowed', explanation: '"sensors may be destroyed"'},
        //and
        {what: 'elements with id from 1 to 10, not edited in last 2 days or done already', explanation: '[1,10] and (!last2 or done)'},
        //or
        {what: 'elements with state TODO or DOING', explanation: 'todo or doing'},
        //not
        {what: 'elements with last edit not within last two days and not last edited by coworker x', explanation: '!last2 and !"coworker x"'},
        //bracket
        {what: '"and" has higher precedence than "or", thus this is evaluated as "a" or ("b" and "c")' ,explanation: '"a" or "b" and "c"'},
        {what: 'Use of () to make precedence explicit', explanation: '("a" or "b") and "c"'},

        //string,number,interval, entity state, lastdays
      ];

      this.queryColumns = ['queryKind', 'example'];
      this.valueExplanations = [
        {what: 'string - only double quotes " are allowed', explanation: '"After 2nd incident", "Car driver"'},
        {what: 'number', explanation: '23, 1.2, 3.4, 44'},
        {what: 'interval', explanation: '[3, 5]  [1,10]'},
        {what: 'state', explanation: 'TODO, DOING, DONE - correspond to icon in state column'},
        {what: 'edited within a number of days', explanation: 'last3, last102'},
      ];
      this.valueColumns = ['valueKind', 'explanation'];
    }

  onNoClick(): void {
    this.dialogRef.close();
  }
}

interface ExplanationTableData {
  what: string;
  explanation: string;
}
