import {EventEmitter, Injectable} from '@angular/core';

/**
 * Service for the filtering of the MainTable
 */
@Injectable()
export class FilterService {
  FilterEmitter: EventEmitter<any>;
  AdvancedFilterEmitter: EventEmitter<any>;
  DeleteRequestEmitter: EventEmitter<any>;
  DeleteResponseEmitter: EventEmitter<any>;
  ChangeEntityStateRequestEmitter: EventEmitter<any>;
  ChangeEntityStateResponseEmitter: EventEmitter<any>;
  SelectedEntryExists: EventEmitter<number>;
  ClearSelectionEmitter: EventEmitter<any>;
  CancelDialogEmitter: EventEmitter<boolean>;
  constructor() {
    this.FilterEmitter = new EventEmitter();
    this.DeleteRequestEmitter = new EventEmitter();
    this.DeleteResponseEmitter = new EventEmitter();
    this.ChangeEntityStateRequestEmitter = new EventEmitter();
    this.ChangeEntityStateResponseEmitter = new EventEmitter();
    this.SelectedEntryExists = new EventEmitter();
    this.ClearSelectionEmitter = new EventEmitter();
    this.AdvancedFilterEmitter = new EventEmitter();
    this.CancelDialogEmitter = new EventEmitter();
  }

}
