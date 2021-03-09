function _validate<K extends Object>(obj: any, instanceObj: K): K {
    let isValid: boolean = true;
    for (let key in instanceObj) {
        if (key in obj) {
            instanceObj[key] = obj[key];
        } else {
            console.error(`JSON file is missing property: ${key}`);
            isValid = false;
        }
    }

    if (isValid) {
        if (Object.keys(obj).length > Object.keys(instanceObj).length) {
            console.warn('JSON file has more properties than expected.');
            console.warn(obj);
            return obj;
        }
    } else {
        if (Object.keys(obj).length !== Object.keys(instanceObj).length) {
            console.error('JSON validator missmatch');
            console.log(obj);
            return obj;
        }
    }
    return instanceObj;
}

/**
 * validates the JSOn Object with a calss
 * If the Object is an Array tries to validated each item in the array with the given calss
 * @param obj the Json as object or array of object
 * @param instanceObj an instance of the interface class
 */
export function validate<T extends Object, K extends Object = T>(obj: any, instanceObj: K | T): T {
    if (Array.isArray(obj)) {
        let arr: Array<K> = new Array<K>();
        for (let ele of obj) {
            arr.push(_validate(ele, instanceObj as K));
        }
        return arr as any as T;
    } else {
        return _validate(obj, instanceObj as T);
    }
}
