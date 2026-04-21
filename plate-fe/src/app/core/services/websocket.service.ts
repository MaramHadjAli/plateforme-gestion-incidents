import { Injectable } from '@angular/core';
import { Client, Message } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';
import { BehaviorSubject } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private client!: Client;
  private backendUrl = 'http://localhost:8080/ws-notifications';
  
  // Expose notification stream to components
  public notifications$ = new BehaviorSubject<any>(null);

  constructor(private authService: AuthService) {}

  public connect() {
    this.client = new Client({
      // We use SockJS since direct WS might fail over proxies
      webSocketFactory: () => new (SockJS as any)(this.backendUrl),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    this.client.onConnect = (frame) => {
      console.log('Connected to WS Broker: ' + frame);

      // Subscribe to global alerts (e.g. for Admins)
      if (this.authService.getUserRole() === 'ADMIN') {
        this.client.subscribe('/topic/admin-alerts', (msg: Message) => {
          this.notifications$.next(JSON.parse(msg.body));
        });
      }

      // Subscribe to targeted user alerts
      const username = this.authService.getToken() ? this.authService.getUserRole() : null; // Typically username from JWT
      if (username) {
         this.client.subscribe(`/user/${username}/queue/notifications`, (msg: Message) => {
            this.notifications$.next(JSON.parse(msg.body));
         });
      }
    };

    this.client.onStompError = (frame) => {
      console.error('Broker reported error: ' + frame.headers['message']);
      console.error('Additional details: ' + frame.body);
    };

    this.client.activate();
  }

  public disconnect() {
    if (this.client) {
      this.client.deactivate();
    }
  }
}
