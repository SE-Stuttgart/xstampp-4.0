/**
 * A form element can either be a Checkbox or a Box, containing Boxes and Checkboxes.
 * All objects that form the tree of form elements inherit this class.
 */
export abstract class ReportFormElement {

  /** The type identifier, so that the HTML templates can tell the different subtypes apart. */
  readonly type: 'box' | 'group';
  /** Wether this element is currently selected. Has different meanings, depending on the subtype.
   * Used by the DTO generator to determine the value of this element.*/
  selected: boolean = false;
  /** The layer determines how nested this element is. Checkboxes inside a Checkbox-Group are
   * layer 1, Checkboxes inside a Checkbox-Group inside a Checkbox-Group are layer 2.
   * The layer is used along with the maxLayer to coordinate Checkbox indentation, to arrange
   * them in a nice vertical line. */
  layer: number;
  /** The highest layer in the form structure */
  maxLayer: number;
  /** The parent element in the form tree */
  parentNode: ReportFormElement | null;

  constructor(
    /** The name of this element. Can be used to find this element in the form tree. The DTO generator
     * names the attribute, containing this element's value, after this name. */
    public name: string,
    /** The label of this form element. This is the name, the user is supposed to see. */
    public label: string) { }

  /**
   * Once the form tree is fully built, the whole tree needs initiation, setting some
   * mandatory attributes. This method is responsible for this process. All root elements
   * need to be called, which will in turn call all their childs. Returns the highest layer,
   * this element and all its childs have.
   *
   * @param currentLayer This element's layer
   * @param indexList The list of all entries. This node and all childs will add their reference to the list
   * @param parentNode The parent node of this node
   */
  abstract initiateTree(currentLayer: number, indexList: ReportFormElement[], parentNode: ReportFormElement): number;

  /**
   * Call this method, when a state change is directly requested by the user (e.g. he selects this checkbox).
   * It will change its own state and will ensure that all child and parent elements will adapt accordingly.
   *
   * @param checked The new "checked" value of this element
   */
  abstract update(checked: boolean): void;

  /**
   * Call this method, when a child of this element got changed by the user. It will adapt
   * its own state and will ensure that all parent elements adapt as well.
   */
  abstract childUpdated(): void;
  /**
   * Call this method, when a parent of this element got changed by the user. It will adapt
   * its own state and will ensure that all child elements adapt as well.
   */
  abstract parentUpdated(): void;

  /**
   * Saves the given maximal layer and hands the information to all its child elements as well.
   * Gets called after the tree initiation.
   *
   * @param maxLayer The higest layer in the form structure
   */
  announceMaxLayer(maxLayer: number): void {
    this.maxLayer = maxLayer;
  }
}
