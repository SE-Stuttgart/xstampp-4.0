import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CSDiaType } from 'src/app/common/control-structure-editor/cs-types';
import { Location } from '@angular/common';
import { DeactivationMode, DeactivationGuarded, CanDeactivateGuard } from 'src/app/services/change-detection/can-deactivate-guard.service';

@Component({
  selector: 'app-control-structure-step2',
  templateUrl: './control-structure-step2.component.html',
  styleUrls: ['./control-structure-step2.component.css']
})
export class ControlStructureStep2Component implements OnInit {
  diaType: CSDiaType;

  constructor(
    private location: Location,
    private route: ActivatedRoute,
    private readonly canDeactivate: CanDeactivateGuard,
    private router: Router) { }

  ngOnInit(): void {
    this.diaType = CSDiaType[this.route.snapshot.paramMap.get('step')];
    this.router.events.subscribe(() => {
      this.diaType = CSDiaType[this.route.snapshot.paramMap.get('step')];
    });
  }

  /**
   * cahnges the dia to STEP4 dia
   */
  changeDiaRoute($event: string): void {
    let path: string = this.location.path();
    path = path.replace('control-structure-diagram/STEP2', 'control-structure-diagram/STEP4');
    this.router.navigateByUrl(path).then(() => this.canDeactivate.setDeactivationMode(DeactivationMode.DENIED));
  }
}
