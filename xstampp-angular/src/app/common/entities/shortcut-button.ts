/** Represents a button in the column header of a table.
 * When clicked, it opens the specified view in a new browser tab. */
export interface ShortcutButton {
  /** The title of the button, shown on mouseover */
  title: string;
  /** The routing link of the view, this button links to */
  routerLink: string | (() => string);
}
