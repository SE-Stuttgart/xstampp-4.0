import { CommonModule } from '@angular/common';
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  MatButtonModule,
  MatCardModule,
  MatCheckboxModule,
  MatDialogModule,
  MatExpansionModule,
  MatFormFieldModule,
  MatIconModule,
  MatInputModule,
  MatListModule,
  MatPaginatorModule,
  MatRippleModule,
  MatSidenavModule,
  MatSortModule,
  MatTableModule,
  MatToolbarModule,
  MatSelectModule,
  MatMenuModule,
  MatButtonToggleModule,
  MatSlideToggleModule,
} from '@angular/material';
import { QuillModule } from 'ngx-quill';
import { CommonComponentsModule } from '../common/common-components.module';
import { ActuatorComponent } from './actuator/actuator.component';
import { ControlActionComponent } from './control-action/control-action.component';
import { ControlAlgorithmComponent } from './control-algorithm/control-algorithm.component';
import { ControlStructureStep2Component } from './control-structure-step2/control-structure-step2.component';
import { ControlledProcessComponent } from './controlled-process/controlled-process.component';
import { ControllerConstraintComponent } from './controller-constraint/controller-constraint.component';
import { ControllerComponent } from './controller/controller.component';
import { ConversionTableComponent } from './conversion-table/conversion-table.component';
import { CreateLossScenarioComponent } from './create-loss-scenario/create-loss-scenario.component';
import { DefaultViewComponent } from './default-view/default-view.component';
import { FeedbackComponent } from './feedback/feedback.component';
import { IdentifyLossesComponent } from './identify-losses/identify-losses.component';
import { ImplementationConstraintsComponent } from './implementation-constraints/implementation-constraints.component';
import { InputArrowComponent } from './input-arrow/input-arrow.component';
import { LossScenarioTableComponent } from './loss-scenario-table/loss-scenario-table.component';
import { OutputArrowComponent } from './output-arrow/output-arrow.component';
import { ProcessVariableTableComponent } from './process-variable-table/process-variable-table.component';
import { ProcessmodelTableComponent } from './processmodel-table/processmodel-table.component';
import { ResponsibilityComponent } from './responsibility/responsibility.component';
import { RouterLinkDirective } from './router-link.directive';
import { SensorComponent } from './sensor/sensor.component';
import { SubAppComponent } from './sub-app.component';
import { SubAppRoutes } from './sub-app.routing';
import { SubHazardsComponent } from './sub-hazards/sub-hazards.component';
import { CancelDialogComponent, SystemDescriptionComponent } from './system-description/system-description.component';
import { SystemLevelHazardsComponent } from './system-level-hazards/system-level-hazards.component';
import { SystemLevelSafetyConstraintsComponent } from './system-level-safety-constraints/system-level-safety-constraints.component';
import { UcaTableComponent } from './uca-table/uca-table.component';
import { ReportConfigurationComponent } from './report-configuration/report-configuration.component';
import { ReportConfigurationGroupComponent } from './report-configuration/checkbox-group/checkbox-group.component';
import { ReportConfigurationCheckboxComponent } from './report-configuration/checkbox/checkbox.component';
import { MatRadioModule } from '@angular/material/radio';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTreeModule } from '@angular/material';

import { MoveEntityComponent } from './dependent-element-tree/move-entity/move-entity.component';
import { DependentElementTreeComponent } from './dependent-element-tree/dependent-element-tree.component';
import { CreateDependencyTreeComponent } from './dependent-element-tree/create-dependency-tree/create-dependency-tree.component';
import { DependentElementDetailsComponent} from './dependent-element-tree/dependent-element-details/dependent-element-details.component';
import { CreateDependencyTreeStepOneComponent } from './dependent-element-tree/create-dependency-tree/create-dependency-tree-step-one/create-dependency-tree-step-one.component';
import { CreateDependencyTreeStepTwoComponent } from './dependent-element-tree/create-dependency-tree/create-dependency-tree-step-two/create-dependency-tree-step-two.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';




@NgModule({

    imports: [
        MatProgressBarModule,
        MatRadioModule,
        MatTreeModule,
        CommonModule,
        CommonComponentsModule,
        SubAppRoutes,
        MatToolbarModule,
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
        QuillModule.forRoot(),
        MatRippleModule,
        MatDialogModule,
        MatSelectModule,
        MatButtonToggleModule,
        MatMenuModule,
        MatSlideToggleModule,
        MatProgressSpinnerModule,
    ],
   declarations: [
      SubAppComponent,
      SystemDescriptionComponent,
      IdentifyLossesComponent,
      SystemLevelHazardsComponent,
      SystemLevelSafetyConstraintsComponent,
      SubHazardsComponent,
      DefaultViewComponent,
      SystemDescriptionComponent,
      ControlStructureStep2Component,
      ResponsibilityComponent,
      FeedbackComponent,
      ControlledProcessComponent,
      InputArrowComponent,
      OutputArrowComponent,
      ControllerComponent,
      SensorComponent,
      ActuatorComponent,
      ControlActionComponent,
      UcaTableComponent,
      ControllerConstraintComponent,
      CancelDialogComponent,
      ControlAlgorithmComponent,
      ConversionTableComponent,
      ProcessmodelTableComponent,
      RouterLinkDirective,
      ControlAlgorithmComponent,
      ProcessVariableTableComponent,
      LossScenarioTableComponent,
      CreateLossScenarioComponent,
      ControlAlgorithmComponent,
      ReportConfigurationComponent,
      ReportConfigurationGroupComponent,
      ReportConfigurationCheckboxComponent,
      ImplementationConstraintsComponent,

      CreateDependencyTreeComponent,
      MoveEntityComponent,
      DependentElementTreeComponent,
      DependentElementDetailsComponent,
      CreateDependencyTreeStepOneComponent,
      CreateDependencyTreeStepTwoComponent,
   ],
   entryComponents: [
      ControlAlgorithmComponent,
      CreateLossScenarioComponent,
      CreateDependencyTreeComponent,
      MoveEntityComponent,
      DependentElementTreeComponent,
      DependentElementDetailsComponent,
      CreateDependencyTreeStepOneComponent,
      CreateDependencyTreeStepTwoComponent,
   ],
   providers: [],
   bootstrap: [
     CancelDialogComponent,
     CreateDependencyTreeComponent,
     MoveEntityComponent,
     DependentElementTreeComponent,
     DependentElementDetailsComponent,
     CreateDependencyTreeStepOneComponent,
     CreateDependencyTreeStepTwoComponent,
   ],
   schemas: [
      CUSTOM_ELEMENTS_SCHEMA
   ]
})
export class SubAppModule {
}
