import { Injectable } from '@angular/core';
import { RequestService } from '../../request.service';
import { ConfigService } from '../../config.service';

@Injectable({
  providedIn: 'root'
})
export class ControlAlgorithmDataService {

  constructor(private requestService: RequestService, private controlAlgorithmDataService: ControlAlgorithmDataService, private config: ConfigService) {
  }

}
