# JSON Validator

Optional service that can be used to validate JSON-Resppnses from the backend. Example:

1. You need Classes as Interfaces for the JSON-Response

```typescript
export class ProcessModelDTO {
  id?: string = null;
  name: string = null;
  description: string = null;
  controllerId: string = null;
}

export class ProcessModelResponseDTO extends ProcessModelDTO {
  lastEdited: number = null;
  lastEditor: string = null;

  lockExpirationTime: number = null;
  lockHolderDisplayName: string = null;
  lockHolderId: string = null;
}
```

2. When you perform a PUT,POST or GET request and whant to use the new validator, the 4. (optional) parameter must be a new instance of the interface class

```typescript
create(projectId: string, value: ProcessModelDTO): Promise<ProcessModelResponseDTO> {
    return new Promise<ProcessModelResponseDTO>(
      (resolve: (value?: ProcessModelResponseDTO | PromiseLike<ProcessModelResponseDTO>) => void,
        reject: (reason?: any) => void): void => {
        this.requestService.performPOSTRequest<ProcessModelResponseDTO>(
          '/api/project/' + projectId + '/process-model',
          JSON.stringify(value),
          this.config.useProjectToken(),
          undefined,
          new ProcessModelResponseDTO(),
        )
          .then((pm: ProcessModelResponseDTO) => {
            if (pm != null) {
              resolve(pm);
            } else {
              const error: Error = new Error(EXPECTED_ENTITY_NOT_NULL);
              console.log(error);
              reject(error);
            }
          })
          .catch((reason: HttpErrorResponse) => {
            console.error(reason);
            reject(reason.error);
          });
      });
  }
```

3. The Validator will output missmatches in the console:

```bash
JSON file is missing property: last_Editor
```
