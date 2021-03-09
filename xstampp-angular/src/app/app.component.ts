import { Component, OnDestroy } from '@angular/core';
import { MessageService } from 'primeng/api';
import { Observable, Subscription, fromEvent } from 'rxjs';
import { NO_INTERNET_CONNECTION, INTERNET_CONNECTION } from './globals';
import { Hotkeys } from './hotkeys/hotkeys.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnDestroy {
  title: string = 'xstampp-angular';

  year: number = new Date().getFullYear();

  onlineStatus: Observable<Event>;
  offlineStatus: Observable<Event>;

  subscriptions: Subscription[] = [];

  isConnected: boolean = true;

  connectionStatusMessage: string;
  connectionStatus: string;
  onlineEvent: Observable<Event>;
  offlineEvent: Observable<Event>;

  constructor(
    private readonly messageService: MessageService, private readonly hotkeys: Hotkeys
  ) {

    this.subscriptions.push(this.hotkeys.addShortcut({ keys: 'shift.control.s', description: 'Save current file' }).subscribe(() => {
    }));
    this.subscriptions.push(this.hotkeys.addShortcut({ keys: 'shift.control.n', description: 'New object' }).subscribe(() => {
    }));

    this.onlineEvent = fromEvent(window, 'online');
    this.offlineEvent = fromEvent(window, 'offline');

    this.subscriptions.push(this.onlineEvent.subscribe((e: Event) => {
      this.connectionStatusMessage = 'Back to online';
      this.connectionStatus = 'online';
      this.messageService.add({
        closable: true,
        severity: 'success',
        summary: NO_INTERNET_CONNECTION
      });
    }));

    this.subscriptions.push(this.offlineEvent.subscribe((e: Event) => {
      this.connectionStatusMessage = 'Connection lost! You are not connected to internet';
      this.connectionStatus = 'offline';
      this.messageService.add({
        closable: true,
        severity: 'error',
        summary: INTERNET_CONNECTION
      });
    }));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((subscription: Subscription) => subscription.unsubscribe());
  }
}
