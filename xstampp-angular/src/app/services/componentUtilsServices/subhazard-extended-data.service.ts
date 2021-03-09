import { HazardDataService } from './../dataServices/hazard-data.service';
import { Injectable } from '@angular/core';
import { SubHazardDataService } from '../dataServices/sub-hazard-data.service';
import { SubSystemConstraintDataService } from '../dataServices/sub-system-constraint-data.service';
import { SystemLevelSafetyConstraintDataService } from '../dataServices/system-level-safety-constraint-data.service';
import { LockService } from '../dataServices/lock.service';
import { SUB_HAZARD } from '../../types/local-types';


@Injectable({
  providedIn: 'root'
})
export class SubhazardExtendedDataService {

  constructor(
    private readonly hazardDataService: HazardDataService,
    private readonly subHazardDataService: SubHazardDataService,
    private readonly subSystemConstraintDataService: SubSystemConstraintDataService,
    private readonly systemConstraintDataService: SystemLevelSafetyConstraintDataService) {
  }


  public editSubHazardAndSubSysCon(projectId, subHazard): Promise<boolean> {
    const parentHazardId = subHazard.ent.parentHazardId;
    const subHazardId = subHazard.ent.id;
    const parentSafetyConstraintId = subHazard.ent.parentSubSafetyConstraintId;
    const subSafetyConstraintId = subHazard.ent.subSafetyConstraintId;
    const subSafetyConstraintExist = subHazard.ent.subSafetyConstraintId !== '';
    const subHazardHackEnt = { projectId: projectId, description: '', name: subHazard.ent.name, state: subHazard.ent.state };
    const subSafetyConstHackEnt = { projectId: projectId, description: '', name: subHazard.ent.subSafetyConstraintName };

    // edit subHazard Only
    if (subHazard.ent.subSafetyConstraintId === '' && subHazard.ent.subSafetyConstraintName === '' && subHazard.ent.sc_links.length === 0) {
      return this.editSubHazardOnly(projectId, parentHazardId, subHazardId, subHazardHackEnt);

      // delete subsycons name existed but is now set to empty string and also sc_link was deleted
    } else if (subHazard.ent.subSafetyConstraintId !== '' && subHazard.ent.subSafetyConstraintName === '' && subHazard.ent.sc_links.length === 0) {
      return this.editSubHazardWithEmptySubSysConstraint(projectId, parentHazardId, subHazardId, subHazardHackEnt, subSafetyConstraintExist,
        parentSafetyConstraintId, subSafetyConstraintId);

      // if no subSafety existed but is now modified
    } else if (subHazard.ent.subSafetyConstraintId === '' && subHazard.ent.subSafetyConstraintName !== '') {
      const newParentSafetyConstraintId = subHazard.ent.sc_links[0].id;
      return this.editSubHazardWithUndefinedSubSysConstraint(projectId, parentHazardId, subHazardId, subHazardHackEnt, newParentSafetyConstraintId, subSafetyConstHackEnt);

    } else if (subHazard.ent.parentSubSafetyConstraintId !== subHazard.ent.sc_links[0].id) {
      const newParentSafetyConstraintId = subHazard.ent.sc_links[0].id;
      return this.editSubHazardWithDifferenParentSubSysConstraint(projectId, parentHazardId, subHazardId, subHazardHackEnt, parentSafetyConstraintId, newParentSafetyConstraintId,
        subSafetyConstraintId, subSafetyConstHackEnt);

    } else {
      return this.editSubHazardAndSubSysConstraint(projectId, parentHazardId, subHazardId, subHazardHackEnt, parentSafetyConstraintId, subSafetyConstraintId,
        subSafetyConstHackEnt)
        .then(() => {
          return true;
        }
        ).catch(error => {
          console.log(error);
          return false;
        });
    }
  }

  public createSubHazardAndSubSysCon(projectId, subHazard, selectedHazard) {
    // TODO Temporarly entity fix
    // 2 cases for create new -> always create SubHazard
    // 1.  subSysConsName === ''  and no Link -> create only subHazard
    // 2. subSysConName !== '' and link exist -> create subHaz and subSysCons, link both
    const subHazardHackEnt = { projectId: projectId, description: subHazard.ent.description, name: subHazard.ent.name, state: subHazard.ent.state };

    if (subHazard.ent.subSafetyConstraintName === '' && subHazard.ent.sc_links.length === 0) {
      return this.createOnlySubHaz(projectId, selectedHazard, subHazardHackEnt);
    } else {
      return this.createBoth(projectId, subHazard, selectedHazard);
    }
  }

  private createOnlySubHaz(projectId, selectedHazard, subHazardHackEnt): any {
    return this.subHazardDataService.createSubHazard(projectId, selectedHazard.id, subHazardHackEnt);
  }

  private createBoth(projectId, subHazard, selectedHazard) {
    const selectedHazardId = selectedHazard.id;
    const parentSysContraintId = subHazard.ent.sc_links[0].id;
    const subHazardHackEnt = { projectId: projectId, description: subHazard.ent.description, name: subHazard.ent.name, state: subHazard.ent.state };
    const safetyConstraintHackEnt = {
      projectId: projectId,
      description: subHazard.ent.description,
      name: subHazard.ent.subSafetyConstraintName,
    };
    let subHazId;
    let subSysId;

    return this.subHazardDataService.createSubHazard(projectId, selectedHazard.id, subHazardHackEnt)
      .then((createdSubhaz) => {
        subHazId = createdSubhaz.id;
        return this.subSystemConstraintDataService.createSubSystemConstraint(projectId, subHazard.ent.sc_links[0].id, safetyConstraintHackEnt);
      })
      .then((createdSubSysCons) => {
        subSysId = createdSubSysCons.id;
        return this.subSystemConstraintDataService.createSubHazardSubSystemConstraintLink(projectId, parentSysContraintId, subSysId, selectedHazardId, subHazId);
      }
      ).then(() => {
        return this.hazardDataService.createHazardSystemConstraintLink(projectId, selectedHazardId, parentSysContraintId);
      }
      );
  }

  private editSubHazardOnly(projectId, parentHazardId, subHazardId, subHazardEntity) {
    return this.subHazardDataService.alterSubHazard(projectId, parentHazardId, subHazardId, subHazardEntity);
  }

  private editSubHazardWithEmptySubSysConstraint(projectId, parentHazardId, subHazardId, subHazardEntity, subSafetyConstraintExist: boolean,
    systemConstraintId, subSystemConstraintId) {
    return this.subHazardDataService.alterSubHazard(projectId, parentHazardId, subHazardId, subHazardEntity)
      .then(() => {
        if (subSafetyConstraintExist) {
          return this.subSystemConstraintDataService.deleteSubSystemConstraint(projectId, systemConstraintId,
            subSystemConstraintId);
        }
      });
  }

  private editSubHazardWithUndefinedSubSysConstraint(projectId, parentHazardId, subHazardId, subHazardEntity, parentSystemConstraintId, subSystemConstraintEnt) {
    return this.subHazardDataService.alterSubHazard(projectId, parentHazardId, subHazardId, subHazardEntity)
      .then(isAlterHazardSuccesFull => {
        return this.subSystemConstraintDataService.createSubSystemConstraint(projectId, parentSystemConstraintId, subSystemConstraintEnt);
      })
      .then(createdSubSystemConstraint => {
        return this.subSystemConstraintDataService.createSubHazardSubSystemConstraintLink(projectId, parentSystemConstraintId, createdSubSystemConstraint.id,
          parentHazardId, subHazardId);
      }).then(isLinkSuccessfull => {
        return this.hazardDataService.createHazardSystemConstraintLink(projectId, parentHazardId, parentSystemConstraintId);
      });
  }

  private editSubHazardWithDifferenParentSubSysConstraint(projectId, parentHazardId, subHazardId, subHazardEntity, oldSysConsId, newSysconsId, subSystemConstraintId
    , subSystemConstraintEnt) {
    return this.subHazardDataService.alterSubHazard(projectId, parentHazardId, subHazardId, subHazardEntity)
      .then(isAlterHazardSuccesFullResponse => {
        return this.subSystemConstraintDataService.deleteSubSystemConstraint(projectId, oldSysConsId, subSystemConstraintId);
      })
      .then(isDeleteSuccesfullResponse => {
        return this.subSystemConstraintDataService.createSubSystemConstraint(projectId, newSysconsId, subSystemConstraintEnt);
      })
      .then(createdSubSystemConstraintResponse => {
        return this.subSystemConstraintDataService.createSubHazardSubSystemConstraintLink(projectId, newSysconsId, createdSubSystemConstraintResponse.id,
          parentHazardId, subHazardId);
      })
      .then(isLinkSuccessfullResponse => {
        return this.hazardDataService.createHazardSystemConstraintLink(projectId, parentHazardId, newSysconsId);
      });
  }

  private editSubHazardAndSubSysConstraint(projectId, parentHazardId, subHazardId, subHazardEntity, parentSystemConstraintId, subSystemConstraintId, subSystemConstraintEnt) {
    return this.subHazardDataService.alterSubHazard(projectId, parentHazardId, subHazardId, subHazardEntity)
      .then(isAlterHazardSuccesFull => {
        return this.subSystemConstraintDataService.alterSubSystemConstraint(projectId, parentSystemConstraintId, subSystemConstraintId, subSystemConstraintEnt);
      })
      .then(isAlterSubSysConsSuccess => {
        // TODO Check if this is necessary
        return this.subSystemConstraintDataService.createSubHazardSubSystemConstraintLink(projectId, parentSystemConstraintId, subSystemConstraintId,
          parentHazardId, subHazardId);
      });
  }

}
