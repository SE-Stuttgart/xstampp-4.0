import { DetailedSheetTableComponent } from './detailed-sheet/detailed-sheet-table/detailed-sheet-table.component';
import { NgModule } from '@angular/core';
import { ActionBarComponent, DeleteDialogComponent, ChangeEntityStateDialogComponent, QueryLanguageExplanationDialog } from './action-bar/action-bar.component';
import { MainTableComponent, GraphComponent, ErrorComponent, CutOnNewlinePipe } from './main-table/main-table.component';
import { NavigationTableComponent } from './navigation-table/navigation-table.component';
import { DetailedSheetComponent } from './detailed-sheet/detailed-sheet.component';
import {
  MatAutocompleteModule,
  MatButtonModule,
  MatMenuModule,
  MatCardModule,
  MatCheckboxModule,
  MatChipsModule,
  MatExpansionModule,
  MatFormFieldModule,
  MatIconModule,
  MatInputModule,
  MatListModule,
  MatPaginatorModule,
  MatSidenavModule,
  MatSortModule,
  MatTableModule,
  MatToolbarModule,
  MatRippleModule,
  MatDialogModule,
  MatSelectModule,
  MatButtonToggleModule,
  MatSlideToggleModule,
} from '@angular/material';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MentionModule } from 'angular2-mentions/mention';
import { ControlStructureEditorComponent } from './control-structure-editor/control-structure-editor.component';
import { CsToolbarComponent } from './control-structure-editor/cstoolbar/cs-toolbar.component';
import { CsContextMenuComponent } from './control-structure-editor/cscontextmenu/cs-context-menu.component';
import { ScrollDispatchModule } from '@angular/cdk/scrolling';
import { DetailOversheetComponent } from './control-structure-editor/detail-oversheet/detail-oversheet.component';
import { ProcessModelOverlayComponent } from './control-structure-editor/process-model-overlay/process-model-overlay.component';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { DetailedSheetDialogComponent } from './detailed-sheet/detailed-sheet-dialog/detailed-sheet-dialog.component';
import { SaveActionDialogComponent } from './save-action-dialog/save-action-dialog.component';
import { FilteringTableComponent } from './filtering-table/filtering-table.component';
import { FooterComponent } from './footer/footer.component';
import { RouterModule } from '@angular/router';

@NgModule({
  declarations: [
    ActionBarComponent,
    MainTableComponent,
    NavigationTableComponent,
    DetailedSheetComponent,
    DetailedSheetTableComponent,
    ControlStructureEditorComponent,
    CsToolbarComponent,
    CsContextMenuComponent,
    DeleteDialogComponent,
    QueryLanguageExplanationDialog,
    ChangeEntityStateDialogComponent,
    GraphComponent,
    ErrorComponent,
    DetailOversheetComponent,
    ProcessModelOverlayComponent,
    DetailedSheetDialogComponent,
    SaveActionDialogComponent,
    FilteringTableComponent,
    CutOnNewlinePipe,
    FooterComponent,
  ],
  entryComponents: [
    DetailedSheetDialogComponent,
    SaveActionDialogComponent,
    FilteringTableComponent,
  ],
  exports: [
    ActionBarComponent,
    MainTableComponent,
    NavigationTableComponent,
    DetailedSheetComponent,
    DetailedSheetTableComponent,
    ControlStructureEditorComponent,
    FilteringTableComponent,
    FooterComponent,
  ],
  imports: [
    CommonModule,
    MatToolbarModule,
    MatMenuModule,
    MatTableModule,
    MatSortModule,
    MatPaginatorModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    FormsModule,
    MatCheckboxModule,
    MatSidenavModule,
    MatExpansionModule,
    MatListModule,
    MatChipsModule,
    MatAutocompleteModule,
    MatRippleModule,
    ReactiveFormsModule,
    ScrollDispatchModule,
    MatButtonToggleModule,
    MatDialogModule,
    MatSelectModule,
    MatMenuModule,
    MentionModule,
    ConfirmDialogModule,
    MatSlideToggleModule,
    RouterModule,
  ],
  bootstrap: [DeleteDialogComponent, ChangeEntityStateDialogComponent, QueryLanguageExplanationDialog, GraphComponent, ErrorComponent],
  providers: [
    CutOnNewlinePipe,
  ],
})
export class CommonComponentsModule {
}
