import { EventEmitter, Injectable } from '@angular/core';
import { AdminPasswordChangeRequestDTO } from '../types/local-types';

@Injectable()
export class ChangePWService {
  ChangePWEmitter: EventEmitter<AdminPasswordChangeRequestDTO>;

  constructor() {
    this.ChangePWEmitter = new EventEmitter();
  }
}
