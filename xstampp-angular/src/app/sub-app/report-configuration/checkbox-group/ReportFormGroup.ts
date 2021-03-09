import { ReportFormElement } from '../ReportFormElement';

/**
 * The object representative of a Checkbox-Group. Used in the form structure
 * tree and bound to a ReportConfigurationGroupComponent as its graphical representative.
 * This object is responsible for the entire logic of a Checkbox-group.
 */
export class ReportFormGroup extends ReportFormElement {

  readonly type: 'box' | 'group' = 'group';
  /** This group's master checkbox can assume an indeterminate state, when
   * it's childs have mixed states. */
  indeterminate: boolean;

  /**
   * Create a new group of checkboxes
   * @param name The name of this form element, it can be found by this name.
   * @param label This text will be the header of this group, shown to the user.
   * @param content The group's contents. Can be checkboxes or more groups.
   */
  constructor(name: string, label: string,
    public content: ReportFormElement[]) {
    super(name, label);
    this.adaptToChilds();
  }

  initiateTree(currentLayer: number, indexList: ReportFormElement[], parentNode: ReportFormElement): number {
    indexList.push(this);
    this.parentNode = parentNode;
    this.layer = currentLayer;
    let maxLayer = currentLayer + 1;
    this.content.forEach((element: ReportFormElement) => {
      let eLayer = element.initiateTree(currentLayer + 1, indexList, this);
      if (eLayer > maxLayer) {
        maxLayer = eLayer;
      }
    });
    return maxLayer;
  }

  announceMaxLayer(maxLayer: number): void {
    super.announceMaxLayer(maxLayer);
    this.content.forEach((element: ReportFormElement) => {
      element.announceMaxLayer(maxLayer);
    });
  }

  /**
   * Calculate the indent for this group's master checkbox,
   * so that all checkboxes in the form structure are arranged
   * in a nice vertical line.
   */
  calculateIndent(): string {
    return (3 + 7 * (this.maxLayer - this.layer - 1)) + 'px';
  }

  update(checked: boolean): void {
    this.selected = checked;
    this.indeterminate = false;
    this.content.forEach((element: ReportFormElement) => {
      element.parentUpdated();
    });
    if (this.parentNode != null) {
      this.parentNode.childUpdated();
    }
  }

  childUpdated(): void {
    this.adaptToChilds();
    if (this.parentNode != null) {
      this.parentNode.childUpdated();
    }
  }

  parentUpdated(): void {
    this.selected = this.parentNode.selected;
    this.indeterminate = false;
    this.content.forEach((element: ReportFormElement) => {
      element.parentUpdated();
    });
  }

  /**
   * Looks at the group's contents and determines + sets the master checkbox's state.
   * If all checkboxes inside this group are checked, the master checkbox will get checked
   * as well. If all checkboxes are unchecked, the master checkbox will get unchecked as well.
   * In any other case, the master checkbox will assume the 'indetermined' state.
   */
  private adaptToChilds(): void {
    // How many child checkboxes and groups are checked?
    let subChecks = 0;
    // Are there any child groups that are indetermined (mixed)?
    let indeterminates = false;

    // Gather information
    this.content.forEach((element: ReportFormElement) => {
      if (element.selected) {
        subChecks++;
      }
      if (element.type === 'group') {
        let group = element as ReportFormGroup;
        if (group.indeterminate) {
          indeterminates = true;
        }
      }
    });
    // Determine result
    if (subChecks === 0 && !indeterminates) {
      this.indeterminate = false;
      this.selected = false;
    } else if (subChecks === this.content.length && !indeterminates) {
      this.indeterminate = false;
      this.selected = true;
    } else {
      this.indeterminate = true;
      this.selected = false;
    }
  }

}
