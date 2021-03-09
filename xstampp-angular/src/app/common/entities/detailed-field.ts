import { ShortcutButton } from './shortcut-button';

export enum FieldType {
  Text,
  Text_Variable,
  Text_Variable_With_Auto_Completion,
  Chips,
  Chips_Single,
  SubTable,
  Dropdown,
  ButtonGroup,
  StateButtonGroup,
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
