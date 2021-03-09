import { ReportFormElement } from '../ReportFormElement';

/**
 * The object representative of a Checkbox. Used in the form structure
 * tree and bound to a ReportConfigurationCheckboxComponent as its graphical representative.
 * This object is responsible for the entire logic of a Checkbox.
 */
export class ReportFormCheckbox extends ReportFormElement {

  readonly type: 'box' | 'group' = 'box';
  /** Sub-Checkboxes get disabled if their parent checkbox is not checked */
  disabled: boolean = false;

  /**
   * Create a new checkbox
   * @param name The name of this form element, it can be found by this name.
   * Also, the Report request DTO will name the attribute for the checkboxes value after this name.
   * @param label This text will be the label of the checkbox, shown to the user
   * @param defaultValue Wether the checkbox is checked by default
   * @param subboxes A list of checkboxes which will be subcheckboxes of this checkbox.
   */
  constructor(name: string, label: string, defaultValue: boolean,
    /** The subcheckboxes of this checkbox. Subcheckboxes are disabled when their parent is not checked. */
    public subboxes: ReportFormCheckbox[]) {
    super(name, label);
    // If a subcheckbox is checked, the parent will be checked too, overriding the default value
    let subChecks = false;
    subboxes.forEach((element: ReportFormCheckbox) => {
      if (element.selected === true) {
        subChecks = true;
      }
    });
    this.selected = subChecks ? true : defaultValue;
    if (!this.selected) {
      subboxes.forEach((element: ReportFormCheckbox) => {
        element.disabled = true;
      });
    }
  }

  initiateTree(currentLayer: number, indexList: ReportFormElement[], parentNode: ReportFormElement): number {
    indexList.push(this);
    this.parentNode = parentNode;
    this.layer = currentLayer;
    this.subboxes.forEach((element: ReportFormCheckbox) => {
      element.initiateTree(currentLayer, indexList, this);
    });
    return currentLayer;
  }

  announceMaxLayer(maxLayer: number): void {
    super.announceMaxLayer(maxLayer);
    this.subboxes.forEach((element: ReportFormCheckbox) => {
      element.announceMaxLayer(maxLayer);
    });
  }

  /**
   * Calculate the indent for this checkbox, so that
   * all checkboxes in the form structure are arranged
   * in a nice vertical line.
   */
  calculateIndent(): string {
    return (3 + 7 * (this.maxLayer - this.layer)) + 'px';
  }

  update(checked: boolean): void {
    this.selected = checked;
    this.subboxes.forEach((element: ReportFormCheckbox) => {
      element.parentUpdated();
    });
    if (this.parentNode != null) {
      this.parentNode.childUpdated();
    }
  }

  childUpdated(): void { }

  parentUpdated(): void {
    if (this.parentNode.type === 'group') {
      this.selected = this.parentNode.selected;
      this.subboxes.forEach((element: ReportFormCheckbox) => {
        element.parentUpdated();
      });
    } else if (this.parentNode.type === 'box') {
      if (!this.parentNode.selected) {
        this.selected = false;
        this.disabled = true;
        this.subboxes.forEach((element: ReportFormCheckbox) => {
          element.parentUpdated();
        });
      } else {
        this.disabled = false;
        this.selected = true;
      }
    }
  }

}
