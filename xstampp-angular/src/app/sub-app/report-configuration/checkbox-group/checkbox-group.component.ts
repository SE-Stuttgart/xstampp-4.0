import { Component, Input } from '@angular/core';
import { MatCheckbox } from '@angular/material';
import { ReportFormGroup } from './ReportFormGroup';

@Component({
  selector: 'app-checkbox-group',
  templateUrl: './checkbox-group.component.html',
  styleUrls: ['./checkbox-group.component.css']
})
/**
 * Graphical checkbox-group element, used by the report-configuration Component.
 * Represents a group containing more groups and/or checkboxes.
 */
export class ReportConfigurationGroupComponent {

  /** This checkbox-group's object representative from the form structure tree. */
  @Input() contents: ReportFormGroup;

  constructor() { }

  /**
   * Triggered by the group's checkbox, forwards the change to the
   * object representative.
   * @param $event The "checkbox changed" event
   */
  onFormChange($event: { source: MatCheckbox, checked: boolean }): void {
    this.contents.update($event.checked);
  }

}
