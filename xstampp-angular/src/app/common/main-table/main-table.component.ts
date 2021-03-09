import { SelectionModel } from '@angular/cdk/collections';
import { Component, EventEmitter, Inject, Input, OnChanges, OnInit, Output, Pipe, PipeTransform, SimpleChange, SimpleChanges, ViewChild, ElementRef } from '@angular/core';
import { MatDialog, MatDialogRef, MatPaginator, MatSort, MatTableDataSource, MAT_DIALOG_DATA } from '@angular/material';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ConfigService } from '../../services/config.service';
import { FilterService } from '../../services/filter-service/filter.service';
import { RuleParserService } from '../../services/rule-parser.service';
import { ControllerDTO, Filter, ProjectResponseDTO, Type } from '../../types/local-types';
import { SheetMode } from '../detailed-sheet/detailed-sheet.component';
import { ColumnType, TableColumn } from '../entities/table-column';
import { MessageService } from 'primeng/api';
import { UNABLE_TO_PLOT_GRAPH, ERROR_MESSAGE } from 'src/app/globals';
import { FilterPredicate } from 'src/app/services/filter-parser-service/filter-types';
import { FilterParserService } from '../../services/filter-parser-service/filter-parser.service';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { Observable } from 'rxjs';

/**
* @title Data table with sorting, pagination, and filtering.
*/
@Component({
  selector: 'app-main-table',
  templateUrl: './main-table.component.html',
  styleUrls: ['./main-table.component.css']
})
export class MainTableComponent<T> implements OnChanges, OnInit {
  @Input() columns: Array<TableColumn>;
  @Input() data: Array<T>;
  @Input() sheetMode: SheetMode; // is the detailedSheet open?
  @Input() sysAdmin: boolean = false; // checks if request comes from sysAdmin view
  @Output() toggleEvent: EventEmitter<any> = new EventEmitter();
  @Output() openChangePWDialog: EventEmitter<string> = new EventEmitter();
  @Output() entitySelectedEvent: any = new EventEmitter<T[]>();
  @Input() onSort: (s: string) => void;
  @Input() onFilter: (filter: Filter) => void;
  @Input() onPage: (skip: number, first: number) => void;
  filterPredicate: (obj: any, filter: string) => boolean;
  SheetMode: typeof SheetMode = SheetMode;
  ColumnType: typeof ColumnType = ColumnType;
  displayedColumns: string[];
  dataSource: MatTableDataSource<T>;
  selection: SelectionModel<T> = new SelectionModel<T>(true, []);
  filterSubscription = null;
  deleteSubscription = null;
  changeEntityStateSubscription = null;
  clearSelectionSubscription = null;
  lastSelectionState = true;
  selectedEntity;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  year: number = new Date().getFullYear();

  constructor(
    private filterService: FilterService,
    private authService: AuthService,
    private config: ConfigService,
    private router: Router,
    public graph: MatDialog,
    public error: MatDialog,
    private ruleParserService: RuleParserService,
    private filterParserService: FilterParserService)
    {
      this.dataSource = new MatTableDataSource();
    }

    /*
    filterPredicate(obj: any, filter: string): boolean{
      if (!filter) {
        return true;
      }
      ast = parse(filter);
      showPredicate = compile(ast);
      return showPredicate(obj);
    }
    */

    ngOnChanges(changes: SimpleChanges): void {
      const data: SimpleChange = changes.data;
      if (data) {
        //this.dataSource = new MatTableDataSource(data.currentValue);
        this.dataSource.data = data.currentValue;
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
        //this.dataSource.filterPredicate = this.dataSource.filterPredicate;
        //this.dataSource.filter =
        /*
        this.dataSource.filterPredicate = (obj: any, filter: string) => {
          return !filter ||
          (obj.description.indexOf(filter)>-1)||
          (obj.name && obj.name.indexOf(filter) > -1) ||
          (obj.id && obj.id.toString() === filter);
        };
        */
        this.displayedColumns = this.columns.map((item: TableColumn) => {
          return item.key;
        });
      }
    }

    ngOnInit(): void {
      // Assign the data to the data source for the table to render
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
      this.displayedColumns = this.columns.map((item: TableColumn) => {
        return item.key;
      });
      this.filterSubscription = this.filterService.FilterEmitter.pipe(debounceTime(200),
      distinctUntilChanged(),
      switchMap((event) => Observable.create(() => this.applyFilter(event)))).subscribe();
      this.deleteSubscription = this.filterService.DeleteRequestEmitter.subscribe((event) => this.getSelectedRows());
      this.changeEntityStateSubscription = this.filterService.ChangeEntityStateRequestEmitter.subscribe((state: string) => this.getSelectedRows1(state));
      this.clearSelectionSubscription = this.filterService.ClearSelectionEmitter.subscribe((event) => {
        this.clearSelection();
        this.checkSelectionState();
      });
    }

    ngOnDestroy(): void {
      this.dataSource.disconnect();
      //unsubscribe to avoid memory leaks
      this.filterSubscription.unsubscribe();
      this.deleteSubscription.unsubscribe();
      this.changeEntityStateSubscription.unsubscribe();
      this.clearSelectionSubscription.unsubscribe();
    }

    applyFilter(filterValue: string): void {
      let filterPredicate: FilterPredicate = this.filterParserService.parseFilterQuery(filterValue);
      if(filterPredicate) {
        this.dataSource.filterPredicate = (obj: any, filter: string) => {
          return !filter || filterPredicate(obj, filter);
        };
        this.dataSource.filter = filterValue;
      } else {
        this.dataSource.filter = null;
        this.dataSource.filterPredicate = null;
      }
      if (this.dataSource.paginator) {
        this.dataSource.paginator.firstPage();
      }
    }

  sendSelectedEntity(): void {
    this.entitySelectedEvent.emit(this.selection.selected);
  }

    getSelectedRows(): void {
      this.filterService.DeleteResponseEmitter.emit(this.selection.selected);
      this.selection.clear();
    }

    getSelectedRows1(state: string): void {
      this.filterService.ChangeEntityStateResponseEmitter.emit(this.selection.selected);
      this.selection.clear();
    }

    /** Whether the number of selected elements matches the total number of rows. */
    isAllSelected(): boolean {
      const numSelected = this.selection.selected.length;
      const numRows = this.dataSource.data.length;
      return numSelected === numRows;
    }

    /** Selects all rows if they are not all selected; otherwise clear selection. */
    masterToggle(): void {
      this.isAllSelected() ?
      this.selection.clear() :
      this.dataSource.data.forEach((row: T) => this.selection.select(row));
      this.checkSelectionState();
    }

    rowToggle(): void {
      this.checkSelectionState();
    }

    // Checks if any row is selected for enabling the rubbish bin - OLD VARIANT
    // checkSelectionState() {
    //   const selectionState = this.selection.selected.length === 0;
    //   if (this.lastSelectionState === false && selectionState === true) {
    //     this.filterService.SelectedEntryExists.emit(false);
    //   } else if (this.lastSelectionState === true && selectionState === false) {
    //     this.filterService.SelectedEntryExists.emit(true);
    //   }
    //   this.lastSelectionState = selectionState;
    // }

    // Checks if and how many rows are selected for enabling the rubbish bin
    checkSelectionState(): void {
      const selectionAmount = this.selection.selected.length;
      const selectionState = this.selection.selected.length === 0;
      if (this.lastSelectionState === false && selectionState === true) {
        this.filterService.SelectedEntryExists.emit(0);
      } else {
        this.filterService.SelectedEntryExists.emit(selectionAmount);
      }
      this.lastSelectionState = selectionState;
    }

    async projectSelection(project: ProjectResponseDTO): Promise<void> {
      if (this.config.useProjectToken()) {
        const token = await this.authService.getUserToken();
        this.authService.requestProjectToken(project.id, token)
        .then((value: string) => {
          this.authService.setProjectToken(value);
          this.router.navigate(['/project/' + project.id + '/system-description']);
        })
        .catch(() => {
          console.error('error on fetching project token for project: ' + project.id);
        });
      } else {
        this.router.navigate(['/project/' + project.id]);
      }
    }

    onClickEdit(entity: T, mode: SheetMode): void {
      this.selectedEntity = entity;
      this.toggleEvent.emit({ entity, mode });
    }

    onClickRow(entity: T): void {
      console.log(entity);
      this.onClickEdit(entity, SheetMode.View);
    }

    // TODO: ?!?! -@Felix
    over(row) {
      // this.show = !this.show;
      // this.cur_row = row;
    }

    clearSelection(): void {
      this.selection.clear();
    }

    routeToMembers(selectedGroup): void {
      this.router.navigate(['/groups-handling/members/'], { queryParams: { groupId: selectedGroup.id, groupName: selectedGroup.name } });
    }

    changeUserPW(entity): void {
      this.openChangePWDialog.emit(entity.uid);
    }

    showGraph(entity): void {
      let formula = 'rule';
      if (entity.hasOwnProperty('conversion')) { entity.rule = entity.conversion; formula = 'conversion'; }
      let syntaxCheck = this.ruleParserService.checkSyntax(entity.rule);
      if (syntaxCheck !== '') {
        const errorRef: MatDialogRef<ErrorComponent, any> = this.error.open(ErrorComponent, {
          width: '550px',
          height: '280px',
          data: { formula: formula, errorMessage: syntaxCheck }
        });

      } else {
        this.ruleParserService.init(entity.rule, entity.controlActionName);
        const graphRef: MatDialogRef<GraphComponent, any> = this.graph.open(GraphComponent, {
          width: '600px',
          height: Math.min(800, 450 + 38 * this.ruleParserService.getVariables().length) + 'px',
          data: { variables: this.ruleParserService.getVariables() }
        });
      }
    }
  }

  declare const functionPlot: any;
  @Component({
    selector: 'app-graph',
    templateUrl: './graph.component.html',
    styleUrls: ['./graph.component.css']
  })
  export class GraphComponent implements OnInit {

    constructor(
      public graphRef: MatDialogRef<GraphComponent>,
      @Inject(MAT_DIALOG_DATA) public data: any,
      private readonly messageService: MessageService,
      private ruleParserService: RuleParserService
      ) { }

      values: string[] = [];

      @ViewChild('wrapper', { static: true }) wrapper: ElementRef<HTMLDivElement>;

      onCloseClick(): void {
        this.graphRef.close();
      }

      onUpdate(input: HTMLInputElement): void {
        this.ruleParserService.update(this.values);
        input.setCustomValidity('');
        if (!this.drawGraph()) {
          input.setCustomValidity('Invalid field.');
        }
      }

      ngOnInit(): void {
        this.values = Array.from('0'.repeat(this.data.variables.length));
        if (this.values.length > 0) {
          this.values[0] = 'x';
        }
        this.drawGraph();
      }

      drawGraph(): boolean {
        try {
          functionPlot({
            target: '#graphRoot',
            title: this.ruleParserService.getTitle(),
            grid: true,
            data: [{
              fn: this.ruleParserService.getFunctions(),
              sampler: 'builtIn',
              graphType: 'polyline'
            }],
            xAxis: {
              label: this.ruleParserService.getXNames(),
            },
            yAxis: {
              label: this.ruleParserService.getYName(),
            }
          });
          return true;
        } catch (err) {
          return false;
        }
      }

    }

    @Component({
      selector: 'app-graph',
      templateUrl: './error.component.html',
      styleUrls: ['./main-table.component.css']
    })
    export class ErrorComponent {
      constructor(public errorRef: MatDialogRef<ErrorComponent>, @Inject(MAT_DIALOG_DATA) public data: any) { }

      onCloseClick(): void {
        this.errorRef.close();
      }
    }
    /**
    * Accepts a string and returns the part up to the first newline.
    */
    @Pipe({ name: 'cutOnNewline' })
    export class CutOnNewlinePipe implements PipeTransform {
      transform(value: string): string {
        // Angular might not insert a string into this pipe
        if (typeof value !== 'string') {
          value = String(value);
        }
        return value.split('\n', 1)[0];
      }
    }
