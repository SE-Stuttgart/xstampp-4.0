import { Component, Input } from '@angular/core';
import { MatCheckbox } from '@angular/material';
import { ReportFormCheckbox } from './ReportFormCheckbox';

@Component({
  selector: 'app-checkbox',
  templateUrl: './checkbox.component.html',
  styleUrls: ['./checkbox.component.css']
})
/**
 * Graphical checkbox element, used by the report-configuration Component.
 * Represents a checkbox.
 */
export class ReportConfigurationCheckboxComponent {

  /** This checkbox's object representative from the form structure tree. */
  @Input() contents: ReportFormCheckbox;

  constructor() { }

  /**
   * Triggered by the checkbox on user input, forwards the change to the
   * object representative.
   * @param $event The "checkbox changed" event
   */
  onFormChange($event: { source: MatCheckbox, checked: boolean }): void {
    this.contents.update($event.checked);
  }

}
