import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ChangeDetectionService {

  private initList: Map<string, any> = new Map();
  private editList: Map<string, any> = new Map();

  initStates(list: Map<string, any>): void {
    if (this.initList.size === 0) {
      this.initList = new Map<string, any>(list);
      this.editList = new Map<string, any>(list);
    }
  }

  wasInit(): boolean {
    return this.initList.size !== 0;
  }

  addToStates(state: EditState): void {
    this.initList.set(state.key, state.value);
    this.editList.set(state.key, state.value);
  }

  updateState(state: EditState): void {
    this.editList.set(state.key, state.value);
  }

  updateStateWithList(list: Map<string, any>): void {
    this.editList = list;
  }

  delState(state: EditState): void {
    this.editList.delete(state.key);
  }

  resetStates(): void {
    this.initList.clear();
    this.editList.clear();
  }

  wasEdited(): boolean {
    let retValue: boolean = false;
    if (this.editList.size !== this.initList.size) {
      return true;
    }
    this.editList.forEach((val: any, key: string) => {
      if (this.initList.get(key) !== val) {
        retValue = true;
        return;
      }
    });
    return retValue;
  }
}

export interface EditState {
  key: string;
  value: any;
}
