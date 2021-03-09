# Change Detection Service (CDS)

## What is the CDS?
As user when I edit an object, I want to see a warning if I try to leave without saving.  
To detect when such a dialog is needed the CDS was implemented.

The CDS is split in 4 parts:
- In `services -> change detection`
  1. `change-detection-service.service.ts` the actual service
  2. `change-detection-decorator.ts` decorators
  3. `can-deactivate-guard.service.ts` canDeactivate service
- `common -> detailed-sheet -> detailed-sheet-dialog` the Dialog

π
## 1. ChangeDetectionService
The service works together with the decorators (anotations, see below) to observe an Object.  
When the Service gets initialized it has 2 lists:
- `initList: Map<string, any>` (propertyKey -> propertyValue)
- `editList: Map<string, any>` (propertyKey -> propertyValue)

In this lists the service represent the properties of the observed object and their values.

| Function                                   | Parameters                            | Description                                                                     |
| ------------------------------------------ | ------------------------------------- | ------------------------------------------------------------------------------- |
| `initStates(list: Map<string, any>): void` | __list:__ a list of object-properties | inits the 2 lists                                                               |
| `wasInit(): boolean`                       | -                                     | returns true if the CDS was initialized                                         |
| `addToStates(state: EditState): void`      | __state:__ state to add               | Adds a new property to the lists. Used if not all properties can be sen on init |
| `updateState(state: EditState): void`      | __state:__ state for update           | Used to update a property-value.                                                |
| `delState(state: EditState): void`         | __state:__ state to delete            | Delets a property from the `editList`                                           |
| `resetStates(): void`                      | -                                     | Resets the CDS.                                                                 |

## 2. Decorators
To simplify the use of the service decorator functions where implemented.  
The `ChangeDetectionService` must be injected in the component, or an error will be thrown. 

| Function                       | Type              | Description                                                                                                                                                                                                                                                  |
| ------------------------------ | ----------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `@CDSAddObject()`              | PropertyDecorator | Decorates an Object/Type (function). All properties of this Object are added to the CDS and the getter/setter function of the Object will be observed for changes. If you want to just observe the setter function call it with `@CDSAddObject(false);`      |
| `@CDSReset()`                  | MethodDecorator   | If the decorated function is called the CDS will be reseted after the call. E.g. decorate the save-function with this.                                                                                                                                       |
| `@CDSCanExecute(call: string)` | MethodDecorator   | Checks if the decorated function has the permission to execute. If the CDS registered a change the execution is denied. <br/><br/> `call:` the name of the function in the component that the decorater tries to execute if the CDS has registered a change. E.g. call a Dialog here! |
| `@CDSObserver()`               | MethodDecorator   | Can be used to decorate a function and register changes. (Used if the `@CDSAddObject()` can not be used for change-detection). The first argument of the decorated function must be of the type `EditState`                                                  |

```typescript
export interface EditState {
  key: string;
  value: any;
}
```

## CanDeactivateGuard
Checks if the user can leave a site. Calls the `wasEdited()` function of the CDS.  
If the current site is a ControlStructure and the target site is a ControlStructure the user can leave the site even if `wasEdited()` retruns `true`,

## SaveActionDialogComponent
A possible Dialog that can be called if the `@CDSCanExecute(call: string)` decorator denies a function call.  
For an Example see `SystemDescriptionComponent`.





