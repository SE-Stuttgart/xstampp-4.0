import { Component, OnInit, Input, Output, EventEmitter, OnChanges, SimpleChanges, OnDestroy } from '@angular/core';
import { Observable, Subscription } from 'rxjs';
import { LOADING } from 'src/app/globals';

export interface NavigationTableElement {
  name: string;
}
@Component({
  selector: 'app-navigation-table',
  templateUrl: './navigation-table.component.html',
  styleUrls: ['./navigation-table.component.css']
})
export class NavigationTableComponent implements OnInit, OnChanges, OnDestroy {

  @Input() navigationTableDefinitions: any[];
  @Input() updateSelectedElementEvent: Observable<{ element: NavigationTableElement, columnNumber: number }>;
  @Output() clickedNavigationEmitter: EventEmitter<[any, number]> = new EventEmitter();
  defaultDisplayedColumn: string[] = ['default-column'];
  private updateSelectedElementSubscription: Subscription;
  dataSourceWithSingleObjectForSingleRow: Object[][] = [[{}]];
  selectedNavigationMap: Map<number, any>;

  constructor() { }

  ngOnInit(): void {
    this.dataSourceWithSingleObjectForSingleRow = this.navigationTableDefinitions;
    if (this.selectedNavigationMap === undefined) {
      this.selectedNavigationMap = new Map();
    }

    if (this.updateSelectedElementEvent !== undefined) {
      this.updateSelectedElementSubscription = this.updateSelectedElementEvent.subscribe(
        (sel: { element: NavigationTableElement, columnNumber: number }) => this.updateSelectedNavigation(sel.element, sel.columnNumber));
    }
  }

  ngOnDestroy(): void {
    if (this.updateSelectedElementSubscription !== undefined) {
      this.updateSelectedElementSubscription.unsubscribe();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['navigationTableDefinitions']) {
      if (this.selectedNavigationMap === undefined) {
        this.selectedNavigationMap = new Map();
      }
      if (this.navigationTableDefinitions.length === 0) {
        this.navigationTableDefinitions[0] = {
          dataSource: [],
          style: { width: '100%' },
          columnHeaderName: LOADING
        };
      }
      if(this.navigationTableDefinitions[0].dataSource !== undefined) {
        if (this.navigationTableDefinitions[0].dataSource[0] !== undefined) {
          this.selectedNavigationMap.set(0, this.navigationTableDefinitions[0].dataSource[0]);
        }
        if (this.navigationTableDefinitions.length > 1 && this.navigationTableDefinitions[1].dataSource[0] !== undefined) {
          // auto select first elem of second table
          this.selectedNavigationMap.set(1, this.navigationTableDefinitions[1].dataSource[0]);
        }
      }
      if (this.navigationTableDefinitions[0].selectedElement !== undefined) {
        this.selectedNavigationMap.set(0, this.navigationTableDefinitions[0].selectedElement);
      }
      if (this.navigationTableDefinitions.length > 1 && this.navigationTableDefinitions[1].selectedElement !== undefined) {
        // auto select first elem of second table
        this.selectedNavigationMap.set(1, this.navigationTableDefinitions[1].selectedElement);
      }
    }

    this.dataSourceWithSingleObjectForSingleRow = this.navigationTableDefinitions;
  }

  // pass elem and tableNumber as Tuple with the defined new defined emiter @Output() clickedNavigationEmitter: EventEmitter<[any, number]> = new EventEmitter();
  onClickedNavigation(elem, tableNumber: number): void {
    this.clickedNavigationEmitter.emit([elem, tableNumber]);
  }

  updateSelectedNavigation(elem, tableNumber: number): void {
    // Delete(Unselect) all keys from the following tables
    for (const tableyKey of Array.from(this.selectedNavigationMap.keys())) {
      if (tableyKey > tableNumber) {
        this.selectedNavigationMap.delete(tableyKey);
      }
    }
    this.selectedNavigationMap.set(tableNumber, elem);
    // auto select first elem of second table
    if (tableNumber === 0) {
      if (this.navigationTableDefinitions.length > 1 && this.navigationTableDefinitions[1].dataSource !== undefined && this.navigationTableDefinitions[1].dataSource.length > 0) {
        this.selectedNavigationMap.set(1, this.navigationTableDefinitions[1].dataSource[0]);
      }
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
