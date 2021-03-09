import { Component, OnInit, Input, Output, EventEmitter, OnChanges, SimpleChanges, OnDestroy } from '@angular/core';
import { Observable, Subscription } from 'rxjs';
import { ShortcutButton } from '../entities/shortcut-button';
import { LOADING } from 'src/app/globals';

export interface FilteringTableElement {
  name: string;
}

export interface FilteringTableColumn {
  dataSource: FilteringTableElement[];
  style: any;
  columnHeaderName: string;
  columnHeaderButton?: ShortcutButton;
  allowLineBreak?: boolean;
}

/**
 * See xstampp-docs/component-catalog.md
 */
@Component({
  selector: 'app-filtering-table',
  templateUrl: './filtering-table.component.html',
  styleUrls: ['./filtering-table.component.css']
})
export class FilteringTableComponent implements OnInit, OnChanges, OnDestroy {

  @Input() navigationTableDefinitions: FilteringTableColumn[];
  @Input() updateSelectedElementEvent: Observable<{ element: FilteringTableElement, columnNumber: number }>;
  @Input() previewNumbers: Map<FilteringTableElement, number>[];
  @Output() clickedElementEmitter: EventEmitter<[FilteringTableElement, number]> = new EventEmitter();
  private updateSelectedElementSubscription: Subscription;
  defaultDisplayedColumn: string[] = ['default-column'];
  dataSourceWithSingleObjectForSingleRow: Object[] = [{}];
  selectedNavigationMap: Map<number, FilteringTableElement>;

  constructor() { }

  ngOnInit(): void {
    this.dataSourceWithSingleObjectForSingleRow = this.navigationTableDefinitions;
    if (this.selectedNavigationMap === undefined) {
      this.selectedNavigationMap = new Map();
    }
    this.updateSelectedElementSubscription = this.updateSelectedElementEvent.subscribe(
      (sel: { element: FilteringTableElement, columnNumber: number }) => this.updateSelectedNavigation(sel.element, sel.columnNumber));
  }

  ngOnDestroy(): void {
    this.updateSelectedElementSubscription.unsubscribe();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['navigationTableDefinitions']) {
      if (this.selectedNavigationMap === undefined) {
        this.selectedNavigationMap = new Map();
      }
      // Placeholder, if the table data hasn't been provided yet
      if (this.navigationTableDefinitions.length === 0) {
        this.navigationTableDefinitions[0] = {
          dataSource: [],
          style: { width: '100%' },
          columnHeaderName: LOADING
        };
      }
    }
    this.dataSourceWithSingleObjectForSingleRow = this.navigationTableDefinitions;
  }

  onClickedNavigation(elem: FilteringTableElement, tableNumber: number): void {
    this.clickedElementEmitter.emit([elem, tableNumber]);
  }
  updateSelectedNavigation(elem: FilteringTableElement, tableNumber: number): void {
    this.selectedNavigationMap.set(tableNumber, elem);
  }

  /**
   * Returns the preview number of the specified element or undefined if none is present.
   *
   * @param elem The element, the preview number is for
   * @param columnNumber The column, the element is in
   */
  getPreviewNumber(elem: FilteringTableElement, columnNumber: number): number {
    if (this.previewNumbers !== undefined && this.previewNumbers[columnNumber] !== undefined
      && this.previewNumbers[columnNumber].has(elem)) {
      return this.previewNumbers[columnNumber].get(elem);
    } else {
      return undefined;
    }
  }

  /**
   * Listener for the column header button, opens the specified
   * url/route in a new browser tab.
   * @param url The target url/route.
   */
  onClickedShortcutButton(url: string): void {
    window.open(url, '_blank');
  }
}
