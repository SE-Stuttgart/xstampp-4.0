XSTAMPP 4.0 Component Catalog
=================================

:house:[Home](README.md)

Content
-------
* [Backend](#backend)
* [Frontend](#frontend)

Backend
-------
The Java backend is implemented in several components (see [architecture description](architecture-description.md)). 
The class definitions and descriptions can be found in the javadoc of all java classes.
[HERE](javadoc.xstampp.de) are the current javadocs available.

Frontend
--------
For the STPA analysis the frontend views need similar functionality. Thus generic components are developed that can be used to display various information. In the following there is a description of all generic components

### Action Bar
The action bar is a bar that is located in the top of a content view that contains a main table. It contains a filter bar which is needed to filter the entries in the table. It also provides several tools in the right corner. 

| Parameter                                 | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| ----------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| @Input() additionalDeleteMessage: string; | additionalDeleteMessage can be appended to the generic delete Message of this component  **"Really want to delete the X elements ?"** + **additionalDeleteMessageMessage** which appears after delete bin was clicked                                                                                                                                                                                                                                                                                      |
| @Input() showDelete: boolean;             | if delete bin is shown or not default is **true**                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| @Input() showAdd: boolean;                | if delete add/plus symbol is shown or not default is **true**                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| @Input() disableAdd: boolean;             | if delete bin is greyed out or not default is **false**                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| @Input() disableDelete: boolean;          | if add/plus symbol is greyed out or not default is **false**                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| @Input() showBack: boolean;               | if return arrow is shown or not default is **false**                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| @Input() showUndoRedo: boolean;           | if return undo AND redo are shown or not default is **false**                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| @Input() showChipsFilter: boolean;        | if chipsFilter is shown or not default is **false**. This has to be set to **true** if the chipsFilter is needed.                                                                                                                                                                                                                                                                                                                                                                                          |
| @Input() suggestedFilterElements: any[];  | if chipsFilter is shown this property has to be filled with an Array of Objects, which then can be selected. Currently these passed objects needs to have an *id* and *name* as attribute, else they will not be displayed correctly. After a chip was selected the  *filterService.AdvancedFilterEmitter.emit(this.chips);* will be triggered and every component which subscribes to the *filterService.AdvancedFilterEmitter* can receive an array of all chips which were selected by the user before. |
| @Output() create: EventEmitter            | the parent component have to add a callback to this emitter. This Emitter will emit and call the defined callback when the add/plus symbol of this Action Bar is clicked.                                                                                                                                                                                                                                                                                                                                  |
| @Output() delete: EventEmitter            | the parent component have to add a callback to this emitter. This Emitter will emit and call the defined callback when the add/plus symbol of this Action Bar is clicked.                                                                                                                                                                                                                                                                                                                                  |

All @Input are optional and have a default value if they are not set. Each ParentComponent who uses this Action Bar has to define callbacks for the two @Output() EventEmitters. 
                 
<br>
<br>

### ControlStructureEditorComponent
TODO: add doc

<br>
<br>

### Detailed Sheet
The detailed sheet is located on the right of a view. It is used to edit or to view data. <br>
The Fields of the DS:
(when the parent component manipulates the values of the Fieldattributes, the DS will react. e.g. the hidden Value of a Filed can be set to true, so that this Field will not longer be visible.)

```typescript
export class DetailedField {
  /**
   * Class to represent a field of the detail-view
   * @param title - Name of the field, title of card
   * @param key - Name of the attribute that will be displayed in field
   * @param type - Type of the field of {Text, Text_Variable, Chips}
   * @param listKey - Name of the attribute from the list that will be used as the chip identifier
   * @param displayShortName - the identifier for the chips
   * @param dropDownValues - keys are the values of the dropdown and the values of the Map are the vieValues of the dropdown
   */
  title: string;
  key: string;
  type: FieldType;
  readonly?: boolean;
  minRows?: number;
  maxRows?: number;
  listKey?: string;
  displayShortName?: string;
  dropDownText?: string;
  values?: Map<string, string>;
  hidden?: boolean;
}
```

The Fields in the `DetailedSheet` are of these types: <br>

```typescript
export enum FieldType {
  Text,
  Text_Variable,
  Text_Variable_With_Auto_Completion,
  Chips,
  Chips_Single,
  SubTable,
  Dropdown,
  ButtonGroup
}
```

The `DetailedField` has an attribute `key` it is the name (string) of the attribute from the `entity`, that should be displayed in this field. e.g. <br>
```typescript
// entity
interface UcaForDetailedSheet {
  id: string;
  name: string;
  description: string; // key in the fields array points on this attribute
}

this.fields = [
  {title: 'Description', key: 'description', type: FieldType.Text_Variable, readonly: false, minRows: 3, maxRows: 10},
];
```
For now see `UcaTableComponent` for how to use Chips (or `SystemLevelHazardsComponent` for how to use multiple Chip Elements). <br>

The DDEvent for the Dropdown:

```typescript
export interface DDEvent {
    title: string; // the title of the dropdown field
    value: string;  // the value of the dropdown field
}
```

| Parameter                                         | Description                                                                                                                                                                                                                                            |
| ------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `@Input() entity:  T;`                            | The entity which should be diplayed with the detailSheet T extends `EntityType` see below.                                                                                                                                                             |
| `@Input() fields: Array<DetailedField>;`          | Defines which fields should be displayed in the detailed sheet for example [{title:  'ID', key:  'id', type:  FieldType.Text, readonly:  true}, {title:  'Name', key:  'name', type:  FieldType.Text, readonly:  false}] will show an two text fields. |
| `@Input() title:  string;`                        | The title which displayed at the top of the detail sheet                                                                                                                                                                                               |
| `@Input() sheetMode:  SheetMode;`                 | is **necessary** to set to the SheetMode enum the html part of this component needs this variable                                                                                                                                                      |
| `@Input() detailedColumns: TableColumn[];`        | Definition of the subTable e.g. this.detailedColumns  = [{key:  'id',title:  'ID',type:  ColumnType.Text,style: {width:  '15%'}}, {key:  'name',title:  'Sub-SafetyConstraint-Name',type:  ColumnType.Text,style: {width:  '85%'}}];                   |
| `@Input() disableSaveButton: boolean;`            | sets if SaveButton is disabled or default value is **false**                                                                                                                                                                                           |
| `@Input() needsLock: boolean;`                    | sets if entity needs lock before editing default is false                                                                                                                                                                                              |
| `@Output() closeSheet: EventEmitter<DetailData>;` | the parent component have to add a callback to this emitter. This Emitter will emit and call the defined callback when cancel is clicked.                                                                                                              |
| `@Output() saveSheet:  EventEmitter<DetailData>;` | the parent component have to add a callback to this emitter. This Emitter will emit and call the defined callback when save is clicked.                                                                                                                |
| `@Output() editSheet:  EventEmitter<DetailData>`  | the parent component have to add a callback to this emitter. This Emitter will emit and call the defined callback when edit is clicked.                                                                                                                |
| `@Output() dropDownEvent: EventEmitter<DDEvent>`  | Fires when the value of the Dropdown is changed.                                                                                                                                                                                                       |
| `@Input() autoCompleteWords: string[]`            | the list of words to be auto-completed in text fields with auto-completion.                                                                                                                                                                            |

```typescript
interface EntityType {
  id: number;
  name: string;
  allLinksForAutocomplete?: any[]; // for the chip fields
  subSafetyConstraintName?: string;
  links: any[]; // array vom typ HazardResponseDTO or SystemConstraintResponseDTO...
}

export class DetailedField {
  // TODO: Write documentation for undocumented attributes @Rico
  /**
   * Class to represent a field of the detail-view
   * @var title - Name of the field, title of card
   * @var key - Name of the attribute that will be displayed in field
   * @var type - Type of the field of {Text, Text_Variable, Chips}
   * @var listKey - Name of the attribute from the list that will be used as the chip identifier or the string for the array from the button group
   * @var displayShortName - the identifier for the chips
   * @var shortcutButton - If specified, a shortcut button gets placed next to the form field.
   * @var values - the values for the Dropdown
   */
  title: string;
  key: string;
  type: FieldType;
  readonly?: boolean;
  minRows?: number;
  maxRows?: number;
  listKey?: string;
  displayShortName?: string;
  shortcutButton?: ShortcutButton;
  dropDownText?: string;
  values?: Map<string, string>;
  hidden?: boolean;
}

export interface DetailData {
    ent: any;
    id: number;
    mode: SheetMode;
    addedMap: Map<string, any>; // add type for values
    deletedMap: Map<string, any>;
}
```

<br>
<br>

### Main Table
The main table 

| Parameter                                                               | Description                                                                                                                                                                                                                                                                                                                                                                                       |
| ----------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| @Input() columns:  Array&lt;TableColumn&gt;;                            | Defines which columns should be displayed **TableColumn** defines the title,width,cell type and the displayed string inside the cell of a single column e.g. [{key:  'select', title:  'Select', type:  ColumnType.Checkbox, style: {width:  '5%'}}, {key:  'id', title:  'ID', type:  ColumnType.Text, style: {width:  '5%'}}] which will display two columns. The type will be explained below. |
| @Input() data:  Array&lt;T>;                                            | an array of entities which will be displayed in the table. The displayed string in the columns are defined by the **key** and **type** properties of the **@Input() columns**                                                                                                                                                                                                                     |
| @Input() sheetMode:  SheetMode;                                         | is **necessary** to set to the SheetMode enum the html part of this component needs this variable                                                                                                                                                                                                                                                                                                 |
| @Input() sysAdmin: boolean;                                             | is **necessary** for member adminstation views (for the passwordResetSymbol if false is set then the editSymbol is shown instead)                                                                                                                                                                                                                                                                 |
| @Input() onSort: (string) =>  void;                                     | stub for backend sort not fully implemented in frontend                                                                                                                                                                                                                                                                                                                                           |
| @Input() onFilter: (filter:  Filter) =>  void;                          | stub for backend sort not fully implemented in frontend                                                                                                                                                                                                                                                                                                                                           |
| @Input() onPage: (skip:  number, first:  number) =>  void;              | stub for backend sort not fully implemented in frontend                                                                                                                                                                                                                                                                                                                                           |
| @Output() toggleEvent:  EventEmitter&lt;any&gt; =  new  EventEmitter(); | the parent component have to add a callback to this emitter. This Emitter will emit and call the defined callback when the row, editSymbol or viewSymbol is clicked.                                                                                                                                                                                                                              |
| @Output() openChangePWDialog:  EventEmitter&lt;string&gt;;              | This Emitter will emit and call the changePwSymbol is clicked (Currently only in Member adminstration view).                                                                                                                                                                                                                                                                                      |

The type can have following values: 
<br>**ColumnType.Text**   (will display the string in the cell)
<br>**ColumnType.Project_Selection**, (will display a project_selection button  in the cell)
<br>**ColumnTypeCheckbox**, (will display a chckbox button  in the cell)
<br>**ColumnType.Button**, (will display a edit, view and graph button  in the cell)
<br>**ColumnType.Date_Time** (will display a the date and time  in the cell, the passed data is in milliseconds)

<br>
<br>


### Navigation Table
The navigation table are located in the left of a view. The differents from the side navigation are that alle the selectable elements inside the navigation table can dynamically set.
Also it is currently possible to have two navigation tables next to each other. 

| Parameter                                                        | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| ---------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| @Input() navigationTableDefinitions:  any[];                     | defines the displayed data inside the navigation table, also defines the style (width) and highlighted the element  the navigation table. <br> the array contains **one object per nav column** the object is defined as follows : <br><br> **dataSource:  any[]** the array of elements which should be displayed the **.name** attribute of the objects will displayed<br>**style: {'width':  '100%', 'min-width':  '200px'}** width of the navTable if two navTables are used you should use for example *style: {'width':  '60%', ... }* for the first object and *style: {'width':  '40%', ... }* for the second object. All widths of the passed objects should be added to 100% <br> **columnHeaderName: string** The title of navigation column<br>**columnHeaderButton?: ShortcutButton** If defined, a button is placed in the column header. Use this button to provide a shortcut to the entity category's main view.<br>**selectedElement?: any** per default the navtable *always* highlight the first element of the navTable (or the element which was clicked on. Sometimes its necessary to set the Element manually e.g. if something was selected before but the **dataSource** is updated. This causes the navTable to highlight the first element again regardless of the real selected element <br>**allowLineBreak?:  boolean** default is undefined. This allows line break inside the cells |
| @Output() clickedNavigationEmitter:  EventEmitter<[any, number]> | the parent component have to add a callback to this emitter. This Emitter will emit and call the defined callback when the an element inside the navTable is clicked. It will emit an tuple( array of size two) which containts YOUR_DEFINED_NAME.[0] the clicked on item YOUR_DEFINED_NAME.[1] the clicked on column (0 = most left, 1 = secound column)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |

<br>
<br>

### Filtering Table
Typically located on the left next to the main table. This component consists of one or more dynamic columns. Each element in each column is selectable but only one element can be selected at a time in per column. This component is designed to provide filtering options for the main table, however, this component only emits events when elements get clicked, it cannot provide any filtering operations by itself.

Additionally, you can dynamically assign a preview number to each element, which then gets displayed on the right side of the element. This number is supposed to show how many results a certain filtering operation would yield. This component can only receive those numbers directly and is not able to calculate them by itself.

| Parameter                                                                                               | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| ------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| @Input() navigationTableDefinitions: FilteringTableColumn[]                                             | Defines the columns and their contents. One array object is one column, starting from the left. The number of columns is not limited. Each object in the array has to have the following attributes: <br/><br/> **dataSource: FilteringTableElement[]** Each object in this array represents one element. Those objects only require a **name: string** attribute, which will be the element's content. <br/> **style: any** This object can have any CSS attributes for tables but recommended is **width: '100%'** or any lower percentage, so that the widths of all columns add up to 100%. <br/> **columnHeaderName: string** The title of the column which will be displayed at the top of the column. <br> **columnHeaderButton?: ShortcutButton** If defined, a button is placed in the column header. Use this button to provide a shortcut to the entity category's main view. <br/> **allowLineBreak?: boolean** By default undefined (effectively false). This allows line breaks inside the elements. |
| @Input() updateSelectedElementEvent: Observable<{element: FilteringTableElement, columnNumber: number}> | Passing an **{element: FilteringTableElement, columnNumber: number}** object via this Observable will visually select the referenced element in the specified column (columnNumber being the index of **navigationTableDefinitions**). One use of this would be to configure elements that are already selected from the start. No elements are selected from the start by default.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| @Input() previewNumbers: Map<FilteringTableElement, number>[]                                           | This feature is entirely optional. Each object in the array represents a column that is already defined by **navigationTableDefinitions**. You can assign preview numbers to each element by inserting key-value pairs into the map, the key being the desired element and the value being the corresponding preview number. If a preview number is undefined or an element doesn't have a key-value pair, no preview number gets displayed in that element.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| @Output() clickedElementEmitter: EventEmitter<[FilteringTableElement, number]>                          | If you add a callback to this emitter, it will emit and call the defined callback when an element is clicked. The attached tuple contains the element that got clicked on and the number of the column, the element was in (same as its index in **navigationTableDefinitions**).                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
