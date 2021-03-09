import { Component, EventEmitter, HostListener, Input, OnChanges, OnInit, Output } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatAutocompleteSelectedEvent, MatAutocompleteTrigger, MatDialog, MatSelectChange, MatInput, MatChip, MatChipList } from '@angular/material';
import { ActivatedRoute } from '@angular/router';
import { MessageService } from 'primeng/api';
import { Observable, of, Subscription } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { CDSAddObject, CDSCanExecute, CDSReset } from 'src/app/services/change-detection/change-detection-decorator';
import { ChangeDetectionService, EditState } from 'src/app/services/change-detection/change-detection-service.service';
import { LockResponse, LockService } from '../../services/dataServices/lock.service';
import { CONTROLLER_CONSTRAINT, CONVERSION, LockRequestDTO, RULE, SUB_HAZARD, SUB_SYSTEM_CONSTRAINT, UNSAFE_CONTROL_ACTION } from '../../types/local-types';
import { DDEvent, DetailData } from '../entities/data';
import { DetailedField, FieldType } from '../entities/detailed-field';
import { TableColumn } from '../entities/table-column';
import { SaveActionDialogComponent, SaveActions } from '../save-action-dialog/save-action-dialog.component';
import { DetailedSheetDialogComponent } from './detailed-sheet-dialog/detailed-sheet-dialog.component';
import { DetailedSheetUtils } from './utils/detailed-sheet-utils';
import { ERROR_GET_LOCK } from 'src/app/globals';
import { Hotkeys } from 'src/app/hotkeys/hotkeys.service';

// TODO: add default value for Inputs if possible! - @Felix
@Component({
  selector: 'app-detailed-sheet',
  templateUrl: './detailed-sheet.component.html',
  styleUrls: ['./detailed-sheet.component.css']
})
export class DetailedSheetComponent<T extends EntityType> implements OnChanges, OnInit  {

  @CDSAddObject() @Input() entity: T; // RequestDTO of
  @Input() fields: Array<DetailedField>;
  @Input() title: string;
  @Input() sheetMode: SheetMode;
  @Input() detailedColumns: TableColumn[];
  @Input() disableSaveButton: boolean;
  @Input() needsLock: boolean = false;
  @Input() autoCompleteWords: any[] = [];
  @Input() hintHeaderClosed: string = '';
  @Input() hintHeaderOpen: string = '';
  @Input() hintLines: string[] = [];
  @Input() showStates: boolean = true;

  /* FIXME: This is a temporary workaround. Most components can only create entity links after
   * that entity has already been created. The Responsibility component is an exception, you
   * can create a Responsibility and its links to other entities at the same time. All
   * components should be like this and when all components do, you can remove this variable.
   * Don't forget to remove its usage in the html as well! @Rico */
  @Input() allowChipsInNewMode: boolean = false;
  @Output() closeSheet: EventEmitter<DetailData> = new EventEmitter();
  @Output() saveSheet: EventEmitter<DetailData> = new EventEmitter(); // TODO: use just one output - @master student
  @Output() editSheet: EventEmitter<DetailData> = new EventEmitter();
  @Output() selectedChips: EventEmitter<string[]> = new EventEmitter();
  @Output() removeChips: EventEmitter<string[]> = new EventEmitter;
  @Output() dropDownEvent: EventEmitter<DDEvent> = new EventEmitter();
  // for html
  FieldType: typeof FieldType = FieldType;
  SheetMode: typeof SheetMode = SheetMode;
  allLinksObject: { }; // TODO: assign and create type, currently contains for example { haz_links: [], sc_links: [] } - @master student
  filteredLinkedEntities: Map<string, Observable<T[]>>;
  linksCtrl: FormControl = new FormControl('');
  unmodifiedEntity: T;  // deep copy of passed entity
  projectId: string;
  panelOpenState = false;
  @Input() subscribeParams: boolean = false;
  subscriptions: Subscription[] = [];

  /**
   * string variables for ngIf's in html code
   */
  name: string = 'name';
  referenceNumber: string = 'referenceNumber';
  subSafetyConstraintName: string = 'subSafetyConstraintName';

  get entityIsNull(): boolean {
    return this.entity == null;
  }

  constructor(
    private readonly lockService: LockService,
    private readonly route: ActivatedRoute,
    private readonly cds: ChangeDetectionService,
    public dialog: MatDialog,
    private readonly hotkeys: Hotkeys,
    private readonly messageService: MessageService) {

      this.subscriptions.push(this.hotkeys.addShortcut({ keys: 'shift.control.s', description: 'Save' }).subscribe(() => {
         this.onSave();
       }));
       this.subscriptions.push(this.hotkeys.addShortcut({ keys: 'shift.control.backspace', description: 'Cancel' }).subscribe(() => {
        this.onCancel();
      }));
    this.route.parent.params.subscribe((value: any) => {
      this.projectId = value.id;
    });
  }

  ngOnChanges(): void {
    // take deep copy of the passed entity, which is later mainly used to append,delete chips (checking/comparing which chips are new and which are removed)
    // also sets an observable on chips input field which then suggest all posiple chips/linkings
    /**
     * TODO:
     * see todo at end of file.
     * After the todo for the Request it would be greate to have JSON interface for this!!
     *
     * - @Felix
     */

    /**
     * TODO:
     * workaround to use detailedsheet with drawer in the right way!
     * don't know if it could lead to erros. Till now it works fine
     * - @Felix
     */
    if (!this.entityIsNull) {
      this.unmodifiedEntity = JSON.parse(JSON.stringify(this.entity)); // deep copy
    }
    // this needs to be initialised else the hmtl part will throw errors in console because the filteredLinkedenEntiies.get() in HTML will be called on a null
    // object

    // Check whether state is empty and if so, set it to default value "doing"
    if (this.entity !== undefined && this.checkEntityHasProperty('state') && this.entity.state === '') {
      this.entity.state = 'DOING';
    }

    this.filteredLinkedEntities = new Map<string, Observable<T[]>>();
    if (this.sheetMode !== SheetMode.Closed) {
      // TODO: maybe use 'in' instead of 'hasOwnProperty'
      if (this.entity.hasOwnProperty('allLinksForAutocomplete')) {
        this.allLinksObject = this.entity['allLinksForAutocomplete'];
        for (const attribute of Object.keys(this.allLinksObject)) {
          const filteredElements: Observable<any> = this.linksCtrl.valueChanges.pipe(
            startWith(null),
            map((enteredString: string | null) => this._filter(enteredString, attribute)));
          this.filteredLinkedEntities.set(attribute, filteredElements);
        }
      }
    }
    // Check if this view actually wants a lock
    if (this.sheetMode === SheetMode.Edit) {
      this.getLock().then();
    }
  }

  ngOnInit(): void {

  }

  /**
   * used for html,
   * thows Error because T object has
   */
  checkEntityHasProperty(property: string): boolean {
    return this.entity.hasOwnProperty(property);
  }

  /**
   * closes the sheet.
   * If fired from ESC
   */
  @CDSCanExecute('startDialog')
  onCancel(): void {
    this.closeSheet.emit();
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
          this.closeSheet.emit();
          break;
        }
        case SaveActions.SAVE_AND_CLOSE: {
          this.onSave();
          break;
        }
      }
    });
  }

  edit(): void {
    this.editSheet.emit();
  }

  async getLock(): Promise<void> {
    let lockDTO: LockRequestDTO;
    const expires: string = DetailedSheetUtils.calculateLockExpiration();
    const entityName: string = this.title.toLowerCase();
    // TODO: what is super hack ? xD - @Felix
    let ent: any; // todo super hack
    ent = this.entity;
    console.log(ent);
    if (entityName === UNSAFE_CONTROL_ACTION) {
      lockDTO = { id: ent.id, expirationTime: expires, entityName: entityName, parentId: ent.parentId };
    } else if (entityName === RULE) {
      lockDTO = { id: ent.id, expirationTime: expires, entityName: entityName, parentId: ent.controllerId };
    } else if (entityName === CONVERSION) {
      lockDTO = { id: ent.id, expirationTime: expires, entityName: entityName, parentId: ent.actuatorId };
    } else if (entityName === SUB_HAZARD) {
      let getSubHazardLock: boolean;
      let getSubConstraintLock: boolean;

      const subHazLockDTO: LockRequestDTO = {
        id: ent.id,
        expirationTime: expires,
        entityName: entityName,
        parentId: ent.parentHazardId
      };

      if (ent.subConstraintEnt !== null) {
        const subScLockDTO: LockRequestDTO = {
          id: ent.subSafetyConstraintId,
          expirationTime: expires,
          entityName: SUB_SYSTEM_CONSTRAINT,
          parentId: ent.parentSubSafetyConstraintId
        };
        try {
          getSubHazardLock = (await this.lockService.lockEntity(this.projectId, subHazLockDTO)).status === 'SUCCESS';
          getSubConstraintLock = (await this.lockService.lockEntity(this.projectId, subScLockDTO)).status === 'SUCCESS';
        } catch (error) {
          getSubHazardLock = false;
          getSubConstraintLock = false;
        }
        if (getSubHazardLock && getSubConstraintLock) {
          this.sheetMode = SheetMode.EditWithLock;
        } else {
          // give back both locks and return
          try {
            await this.lockService.unlockEntity(this.projectId, { id: ent.id, entityName: entityName, parentId: ent.parentHazardId });
            await this.lockService.unlockEntity(this.projectId, {
              id: ent.subSafetyConstraintId,
              entityName: SUB_SYSTEM_CONSTRAINT,
              parentId: ent.parentSubSafetyConstraintId
            });
          } finally {
            this.sheetMode = SheetMode.View;
          }
        }
        return;
      } else {
        // just the subhazards need locking
        lockDTO = subHazLockDTO;
      }
    } else if (entityName === CONTROLLER_CONSTRAINT) {
      if (ent.constraintId === null) {
        // this is the cration of the constraint so we can't and don't need to lock
        this.needsLock = false;
      } else {
        lockDTO = { id: ent.constraintId, expirationTime: expires, entityName: entityName, parentId: ent.parentId };
      }
    } else {
      lockDTO = { id: ent.id, expirationTime: expires, entityName: entityName };
    }
    if (this.needsLock) {
      await this.lockService.lockEntity(this.projectId, lockDTO)
        .then(() => {
          this.sheetMode = SheetMode.EditWithLock;
        })
        .catch((error: Error) => {
          this.sheetMode = SheetMode.View;
          this.messageService.add({ severity: 'error', summary: ERROR_GET_LOCK, life: 4000, detail: error.message });
          console.error(error);
        });
    } else {
      // since we have no lock we can set the mode to edit
      this.sheetMode = SheetMode.EditWithLock;
    }
  }

  @CDSReset()
  onSave(): void {
    const ent: T = this.entity;
    const id: number = this.entity.id;
    const mode: SheetMode = this.sheetMode;
    const deletedMap: Map<string, any[]> = new Map<string, any[]>();
    const addedMap: Map<string, any[]> = new Map<string, any[]>();

    if (this.entity.hasOwnProperty('allLinksForAutocomplete')) {
      for (const linkKey of Object.keys(this.entity['allLinksForAutocomplete'])) {

        // all links which are inside the old entity but not in the modified/new Entity shall be deleted
        const removedElements: T[] = this.unmodifiedEntity[linkKey]
          .filter((oldLink: T) => this.entity[linkKey]
            .find((element: T) => element.id === oldLink.id && this.hasParent(element, oldLink)) === undefined);
        deletedMap.set(linkKey, removedElements);

        // all links which are inside the modified/new entity but not in the old Entity shall be added

        const newElements: T[] = this.entity[linkKey]
          .filter((newLink: T) => this.unmodifiedEntity[linkKey]
            .find((element: T) => element.id === newLink.id && this.hasParent(element, newLink)) === undefined);

        addedMap.set(linkKey, newElements);
      }
    }
    this.saveSheet.emit({ ent, id, mode, addedMap, deletedMap });
  }

  /**
   * gets the filtered entity with the given key or a empty Observable Array with type T
   */
  getFilteredLinkedEntity(key: string): Observable<T[]> {
    if (this.filteredLinkedEntities != null && this.filteredLinkedEntities.get(key) != null) {
      return this.filteredLinkedEntities.get(key);
    } else {
      return of([]);
    }
  }

  // TODO: thinking abt renaming chip to linkedEntity , currently chip is for example a HazardResponseDTO - @master student
  remove(chip, key: string, displayedInput: MatInput): void {
    const index: number = this.entity[key].indexOf(chip);
    if (index >= 0) {
      this.entity[key].splice(index, 1);
      displayedInput.value = '';
      // this.linksCtrl.setValue(''); is necessary to trigger this.linksCtrl.valueChanges which was initialized in ngChange
      // else after removing removed elements will not be shown in suggestion list anymore
      this.linksCtrl.setValue('');

    }
    this.removeChips.emit([key, chip]);
    this.onValueChange();
  }

  // this.selectedChips is needed to filter the chips
  selected(event: MatAutocompleteSelectedEvent, key: string, matChipRef?: MatChipList): void {
    // event.option.value is null if a mat-option with [value]=== null is selected typically chips which are used as hints
    if (event.option.value !== null) {
      if (!!matChipRef && matChipRef.chips.length > 0) {
        matChipRef.chips.forEach((chip: MatChip) => chip.remove());
      }
      this.entity[key].push(event.option.value);
      this.linksCtrl.setValue('');
      this.selectedChips.emit([key, event.option.value]);
    }
    this.onValueChange();
  }

  /**
   * filters the allLinksObjects lists for the chips
   */
  private _filter(input: string, attribute: string): string[] {
    let filterString: string = typeof input === 'string' ? input.trim() : '';
    filterString = filterString !== '' ? input.toLowerCase().trim() : '';

    return this.allLinksObject[attribute]
      .filter((elem: any) => this.entity[attribute].find(
        (e: any) => (e.id === elem.id) && this.hasParent(e, elem)) === undefined // filter all elems which currently are not already displayed as chip
        && ((elem.name.toLowerCase().indexOf(filterString) > -1) // filter if filterString is in name or filterString is in id
          || elem.id.toString().indexOf(filterString) > -1)
      );
  }

  // checks if an element has a parentId anf if yes compares them
  // is used for the uca table component because subhazards can have the same id but different parent Ids
  private hasParent(e: any, elem: any): boolean {
    if (!!e.parentId && !!elem.parentId) {
      if (e.parentId === elem.parentId) {
        return true;
      } else {
        return false;
      }
    } else {
      return true;
    }
  }

  openAutoComplete(trigger: MatAutocompleteTrigger): void {
    trigger.openPanel();
  }

  /**
   * Listener for the column header button, opens the specified
   * url/route in a new browser tab.
   * @param url The target url/route.
   */
  onClickedShortcutButton(url: string | (() => string)): void {
    if (typeof url === 'function') {
      url = url();
    }
    window.open(url, '_blank');
  }

  onDropdownChange(keyValue: MatSelectChange, title: string): void {
    if (!!keyValue) {
      this.dropDownEvent.emit({
        value: keyValue.value,
        title: title
      });
    }
  }
  ngOnDestroy(): void {
    this.subscriptions.forEach((sub: Subscription) => sub.unsubscribe());
  }
  // TODO: better performance ! but the normal way will work for now!
  // @CDSObserver()
  onValueChange(): void {
  }

  onDropdownGroupChange(ref: EditState): void {
    this.onValueChange();
  }

  onButtonGroupChange(ref: EditState): void {
    this.entity[ref.key] = ref.value;
    this.onValueChange();
  }

  editButton(field: DetailedField, value: string, placeHolder: string): void {
    const dialogRef = this.dialog.open(DetailedSheetDialogComponent, {
      width: '250px',
      data: {
        name: value,
        title: field.title,
        placeHolder: placeHolder,
      }
    });

    dialogRef.afterClosed().subscribe((result: any): void => {
      if (!!result) {
        let arr: Array<string> = (this.entity[field.listKey] as Array<string>);
        if (!!arr) {
          let i: number = arr.indexOf(value);
          if (i >= 0) {
            arr[i] = result;
          } else {
            arr.push(result);
          }
          this.entity[field.listKey] = arr;
          this.onValueChange(); // random value for CDS
        }
      }
    });
  }

  delButton(field: DetailedField, value: string, event: MouseEvent): void {
    event.stopPropagation();
    let arr: Array<string> = (this.entity[field.listKey] as Array<string>);
    if (!!arr) {
      let i: number = arr.indexOf(value);
      if (i >= 0) {
        arr.splice(i, 1);
        this.entity[field.listKey] = arr;
        this.onValueChange(); // random value for CDS
      }
    }
  }

  /**
   * FIXME: table broken after closing with esc
   * listens to esc keypress to close the sheet
   */
  @HostListener('document:keydown.escape', ['$event'])
  onKeydownHandler($event: KeyboardEvent): void {
    $event.preventDefault();
    $event.stopPropagation();
    this.onCancel();
  }

  /**
   * Returns whether the given detailed field should get displayed.
   * Some fields should not appear under some circumstances.
   */
  formFieldEnabled(detailedField: DetailedField): boolean {
    if (detailedField.hidden) {
      return false;
    }
    switch (detailedField.type) {
      case FieldType.SubTable:
        return this.sheetMode !== SheetMode.New;
      default:
        return true;
    }
  }
}

export enum SheetMode {
  Edit, // no lock but wants to edit
  View,
  New,
  Closed,
  Save,
  EditWithLock // got a lock and is ready to edit
}

export interface Role {
  value: string;
  viewValue: string;
}

/**
 * TODO:
 * the generic type is a  ***RequestDTO. The best would be to refactor the DTO's so, that
 * the EntityType can be set as something like:
 * export type EntityType = ProjectRequestDTO | HazardRequestDTO | ... ;
 * is not possible at the time because of the missing id in the standart request DTO of UCA's etc. or to
 * write a Generic Request Interface for the DetailSheet that.
 *
 * - @Felix
 */
export interface EntityType {
  id: number;
  name: string;
  state: string;
  allLinksForAutocomplete?: any[];
  subSafetyConstraintName?: string;
  links: any[]; // array vom typ HazardResponseDTO or SystemConstraintResponseDTO...
}
