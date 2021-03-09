import { SheetMode } from 'src/app/common/detailed-sheet/detailed-sheet.component';
import { ChangeDetectionService, EditState } from './change-detection-service.service';

/**
 * Checks if the given target has a member of the type ChangeDetectionService
 * @returns the ChangeDetectionService if possible
 */
function getChangeDetectionService(self: Object): ChangeDetectionService | undefined {
  for (let prop of Object.entries(self)) {
    if (!!prop && typeof prop[1] === 'object' && prop[1] instanceof ChangeDetectionService) {
      return prop[1] as ChangeDetectionService;
    }
  }
  return undefined;
}

/**
 * Observes the first parameter of the decorated function.
 * The parameter must be of the type 'EditState'
 */
export function CDSObserver(): MethodDecorator {
  return (target: Object, propertyKey: string, descriptor: PropertyDescriptor): PropertyDescriptor => {
    let orgMethod = descriptor.value as Function;

    descriptor.value = function (...args: any[]): Function {
      let cds: ChangeDetectionService = getChangeDetectionService(this);
      if (!!cds) {
        // "sort" object keys (for mozilla and sometimes safari)
        let ele: EditState = args[0] as EditState;
        if (typeof ele.value === 'object') {
          if (!!ele.value) {
            const obj: Object = {};
            Object.keys(ele.value).sort().forEach((key: string) => {
              obj[key] = ele.value[key];
            });
            ele.value = JSON.stringify(obj);
          } else {
            ele.value = undefined;
          }
          cds.updateState(ele);
        } else {
          cds.updateState(ele);
        }
      } else {
        console.error('ChangeDetectionService not found!');
      }
      return orgMethod.apply(this, args);
    };
    return descriptor;
  };
}

/**
 * Checks if the decorated function can be executet.
 * If the CDS returns true 'wasEdited' tries to call the given
 * function.
 * @param call the name of the function in the parentclass that will be called if wasEdited is true
 */
export function CDSCanExecute(call: string): MethodDecorator {
  return (target: Object, propertyKey: string, descriptor: PropertyDescriptor): PropertyDescriptor => {
    let orgMethod = descriptor.value as Function;

    descriptor.value = function (...args: any[]): Function {
      let newMethod: Function;
      let cds: ChangeDetectionService = getChangeDetectionService(this);
      if (!!cds) {
        if (!!this['sheetMode'] && this['sheetMode'] === SheetMode.View) {
          cds.resetStates();
          newMethod = orgMethod.apply(this, args);
        } else {
          if (cds.wasEdited()) {
            (this[call] as Function).call(this);
          } else {
            cds.resetStates();
            newMethod = orgMethod.apply(this, args);
          }
        }
      } else {
        console.error('ChangeDetectionService not found!');
      }
      return newMethod;
    };
    return descriptor;
  };
}

/**
 * resets the CDS
 */
export function CDSReset(): MethodDecorator {
  return (target: Object, propertyKey: string, descriptor: PropertyDescriptor): PropertyDescriptor => {
    let orgMethod = descriptor.value as Function;

    descriptor.value = function (...args: any[]): Function {
      let cds: ChangeDetectionService = getChangeDetectionService(this);
      let newMethod: Function = orgMethod.apply(this, args);
      if (!!cds) {
        cds.resetStates();
      } else {
        console.error('ChangeDetectionService not found!');
      }
      return newMethod;
    };
    return descriptor;
  };
}

/**
 * adds the property to the CDS
 * also will observe the setter and getter function of the property.
 * To observe the getter function is bad for the performance.
 * If this is a problem set getObserver to false and use CDSObserver
 * @param getObserver if false doesn't observe the getter function
 */
export function CDSAddObject(getObserver: boolean = true): PropertyDecorator {
  return function (target: any, propertyKey: string): void {
    let val = target[propertyKey];
    const getter = function (): any {
      if (getObserver) {
        let cds: ChangeDetectionService = getChangeDetectionService(this);
        if (!!cds && cds.wasInit()) {
          setObjectInCDS(cds, val, propertyKey);
        }
      }
      return val;
    };

    const setter = function (next: any): any {
      let cds: ChangeDetectionService = getChangeDetectionService(this);
      /**
       * TODO: fires with hardreset on true, needed cause DetailedSheet will not reset on klicking next element in table
       * if it resets more often then needed check in setObjectFunction if id of entity was changed and then do a reset
       */
      setObjectInCDS(cds, next, propertyKey, typeof next === 'object'); // resets if object (till now just in detailed sheet)
      val = next;
    };

    Object.defineProperty(target, propertyKey, {
      get: getter,
      set: setter,
      enumerable: true,
      configurable: true,
    });
  };
}

/**
 * updates or set the object in the CDS
 * @param cds the ChangedtectionService
 * @param val the given object
 * @param propertyKey the name of the given object in the parent
 */
function setObjectInCDS(cds: ChangeDetectionService, val: any, propertyKey: string, hardReset: boolean = false): void {
  if (!!cds) {
    let itemMap: Map<string, any> = new Map<string, any>();
    if (!!val && val !== undefined && val !== null && typeof val === 'object') {
      for (let ele of Object.entries(val)) {
        if (!!ele[1] && typeof ele[1] === 'object') {
          // "sort" object keys
          if (!!ele[1]) {
            const obj: Object = {};
            Object.keys(ele[1]).sort().forEach((key: string) => {
              obj[key] = ele[1][key];
            });
            itemMap.set(ele[0], JSON.stringify(obj));
          } else {
            itemMap.set(ele[0], undefined);
          }
        } else {
          itemMap.set(ele[0], ele[1]);
        }
      }
    } else {
      itemMap.set(propertyKey, val);
    }
    if (cds.wasInit()) {
      if (hardReset) {
        cds.resetStates();
        cds.initStates(itemMap); // TODO: insert observer for id here if needed
      } else {
        cds.updateStateWithList(itemMap);
      }
    } else {
      cds.initStates(itemMap);
    }
  } else {
    console.error('ChangeDetectionService not found!');
  }
}
