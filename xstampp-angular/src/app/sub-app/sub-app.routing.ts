import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProjectTokenAuthGuard } from '../guard/project-token-auth.guard';
import { UserTokenAuthGuard } from '../guard/user-token-auth.guard';
import { ActuatorComponent } from './actuator/actuator.component';
import { ControlActionComponent } from './control-action/control-action.component';
import { ControlAlgorithmComponent } from './control-algorithm/control-algorithm.component';
import { ImplementationConstraintsComponent } from './implementation-constraints/implementation-constraints.component';
import { ConversionTableComponent } from './conversion-table/conversion-table.component';
import { ControlStructureStep2Component } from './control-structure-step2/control-structure-step2.component';
import { ControlledProcessComponent } from './controlled-process/controlled-process.component';
import { ControllerConstraintComponent } from './controller-constraint/controller-constraint.component';
import { ControllerComponent } from './controller/controller.component';
import { DefaultViewComponent } from './default-view/default-view.component';
import { FeedbackComponent } from './feedback/feedback.component';
import { IdentifyLossesComponent } from './identify-losses/identify-losses.component';
import { InputArrowComponent } from './input-arrow/input-arrow.component';
import { OutputArrowComponent } from './output-arrow/output-arrow.component';
import { ProcessmodelTableComponent } from './processmodel-table/processmodel-table.component';
import { ResponsibilityComponent } from './responsibility/responsibility.component';
import { SensorComponent } from './sensor/sensor.component';
import { SubAppComponent } from './sub-app.component';
import { SubHazardsComponent } from './sub-hazards/sub-hazards.component';
import { SystemDescriptionComponent } from './system-description/system-description.component';
import { SystemLevelHazardsComponent } from './system-level-hazards/system-level-hazards.component';
import { SystemLevelSafetyConstraintsComponent } from './system-level-safety-constraints/system-level-safety-constraints.component';
import { UcaTableComponent } from './uca-table/uca-table.component';
import { CanDeactivateGuard } from '../services/change-detection/can-deactivate-guard.service';
import { ProcessVariableTableComponent } from './process-variable-table/process-variable-table.component';
import { LossScenarioTableComponent } from './loss-scenario-table/loss-scenario-table.component';
import { ReportConfigurationComponent } from './report-configuration/report-configuration.component';

const routes: Routes = [
  {
    path: ':id', component: SubAppComponent, canActivateChild: [ProjectTokenAuthGuard, UserTokenAuthGuard], children: [
      // { path: '', component: SystemDescriptionComponent },
      { path: '', redirectTo: 'system-description', pathMatch: 'full', canDeactivate: [CanDeactivateGuard] },
      { path: 'identify-losses', component: IdentifyLossesComponent, canDeactivate: [CanDeactivateGuard] },
      { path: 'system-description', component: SystemDescriptionComponent, canDeactivate: [CanDeactivateGuard] },
      { path: 'system-level-hazards', component: SystemLevelHazardsComponent, canDeactivate: [CanDeactivateGuard] },
      { path: 'system-level-safety-constraints', component: SystemLevelSafetyConstraintsComponent, canDeactivate: [CanDeactivateGuard] },
      { path: 'refine-hazards', component: SubHazardsComponent, canDeactivate: [CanDeactivateGuard] },
      { path: 'control-structure-diagram/:step', component: ControlStructureStep2Component, canDeactivate: [CanDeactivateGuard] },
      { path: 'control-structure-diagram/:step/:fish', component: ControlStructureStep2Component, canDeactivate: [CanDeactivateGuard] },
      { path: 'controller', component: ControllerComponent, canDeactivate: [CanDeactivateGuard] },
      { path: 'actuator', component: ActuatorComponent, canDeactivate: [CanDeactivateGuard] },
      { path: 'sensor', component: SensorComponent, canDeactivate: [CanDeactivateGuard] },
      { path: 'controlled-process', component: ControlledProcessComponent, canDeactivate: [CanDeactivateGuard] },
      { path: 'control-action', component: ControlActionComponent, canDeactivate: [CanDeactivateGuard] },
      { path: 'feedback', component: FeedbackComponent, canDeactivate: [CanDeactivateGuard] },
      { path: 'input-arrow', component: InputArrowComponent, canDeactivate: [CanDeactivateGuard] },
      { path: 'output-arrow', component: OutputArrowComponent, canDeactivate: [CanDeactivateGuard] },
      { path: 'responsibilities', component: ResponsibilityComponent, canDeactivate: [CanDeactivateGuard] },
      { path: 'uca-table', component: UcaTableComponent, canDeactivate: [CanDeactivateGuard] },
      { path: 'controller-constraints', component: ControllerConstraintComponent, canDeactivate: [CanDeactivateGuard] },
      { path: 'detailed-control-structure', component: DefaultViewComponent, canDeactivate: [CanDeactivateGuard] },
      { path: 'loss-scenario-table', component: LossScenarioTableComponent, canDeactivate: [CanDeactivateGuard] },
      { path: 'implementation-constraints', component: ImplementationConstraintsComponent, canDeactivate: [CanDeactivateGuard] },
      { path: 'processmodel-table', component: ProcessmodelTableComponent, canDeactivate: [CanDeactivateGuard] },
      { path: 'processvariable-table', component: ProcessVariableTableComponent, canDeactivate: [CanDeactivateGuard] },
      { path: 'control-algorithm', component: ControlAlgorithmComponent, canDeactivate: [CanDeactivateGuard] },
      { path: 'control-algorithm', component: ControlAlgorithmComponent, canDeactivate: [CanDeactivateGuard] },
      { path: 'conversion-table', component: ConversionTableComponent, canDeactivate: [CanDeactivateGuard] },
      { path: 'report-configuration', component: ReportConfigurationComponent, canDeactivate: [CanDeactivateGuard] },
    ]
  },

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})

export class SubAppRoutes {
}
