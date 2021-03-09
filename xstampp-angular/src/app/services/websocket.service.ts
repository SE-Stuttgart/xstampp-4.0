import { ConfigService } from './config.service';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {
  private ws: WebSocket;
  private readonly wsPath: string = '/api/push/ws';
  private displayName: string;

  constructor(private readonly configService: ConfigService) {
  }

  public connect(type: string, topic: string, token: string, callback: (data: any) => void): string {
    this.create(type, topic, token, callback);
    console.log('Successfully Connect' + topic + ' To ' + this.configService.getWebsocketIp() + this.wsPath);
    return this.displayName;
  }

  public create(type: string, topic: string, token: string, callback: (data: any) => void): void {

    const self = this;
    if (self.ws && self.ws.readyState === self.ws.OPEN) {
      self.ws.close();
      console.log('Before opened websocket closed');
    }
    self.ws = new WebSocket(this.configService.getWebsocketIp() + this.wsPath);
    self.ws.onopen = function () { self.ws.send(JSON.stringify({ type, topic, token })); };

    self.ws.onmessage = function (message) {
      const temp = JSON.parse(message.data);
      if (message.data.error) {
        console.log(message.data.console.error());
      } else if (temp.type === 'update') {
        // console.log('Got message: ', message);
        self.displayName = temp.displayName;
        callback(temp);
      } else {
        // console.log('Nothing happend, message for keep alive');
        // console.log(message.data);
      }
    };

    self.ws.onerror = function (error) { console.error(error); };
    self.ws.onclose = function () { console.log('Websocket connection closed'); };

    console.log(self.ws);
  }
}
