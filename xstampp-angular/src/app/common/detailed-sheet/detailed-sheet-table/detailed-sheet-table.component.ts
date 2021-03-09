import { TableColumn, ColumnType } from '../../entities/table-column';
import { SheetMode } from '../detailed-sheet.component';
import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-detailed-sheet-table',
  templateUrl: './detailed-sheet-table.component.html',
  styleUrls: ['./detailed-sheet-table.component.css']
})
export class DetailedSheetTableComponent<T> implements OnChanges {

  @Input() columns: Array<TableColumn>;
  @Input() data: Array<T>;
  ColumnType: typeof ColumnType = ColumnType;
  displayedColumns;
  dataSource;
  hoverDisplay; // Variable to save the content which should be shown by hovering over the subtable (full name of the element)

  constructor() { }

  ngOnChanges(changes: SimpleChanges): void {
    this.displayedColumns = this.columns.map(function (item: TableColumn): string {
      return item.key;
    });
    this.dataSource = this.data;
  }

  over(row): void {
    this.hoverDisplay = row['name'];
  }

}
