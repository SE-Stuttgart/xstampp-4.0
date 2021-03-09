import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ConfigService {

  private readonly ip: string;
  private readonly websocketIp: string;
  private readonly isProjectTokenUsed: boolean = true;
  private readonly isUserTokenUsed: boolean = true;

  constructor() {
    this.ip = window.location.origin;

    this.websocketIp = window.location.host;
    this.websocketIp = 'wss://' + this.websocketIp;
    if (this.ip === 'http://localhost:4200') {
     this.ip = 'https://dev.xstampp.de';
     this.websocketIp = 'wss://dev.xstampp.de';

    // Uncomment if you want to use the backend running on your local PC:
    //this.ip = 'http://localhost:80';
    //this.websocketIp = 'ws://localhost';
  }
}

public getServerIp(): string {
  return this.ip;
}

  public useProjectToken(): boolean {
    return this.isProjectTokenUsed;
  }
  public useUserToken(): boolean {
    return this.isUserTokenUsed;
  }

public getWebsocketIp(): string {
  return this.websocketIp;
}
}
