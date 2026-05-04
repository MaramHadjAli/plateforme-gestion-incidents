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
      webSocketFactory: () => {
        // Fallback for different import styles
        const SockJsConstructor = (SockJS as any).default || SockJS;
        if (typeof SockJsConstructor !== 'function') {
          console.error('SockJS is not a constructor. Falling back to native WebSocket.');
          return new WebSocket(this.backendUrl.replace('http', 'ws'));
        }
        return new SockJsConstructor(this.backendUrl);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      connectHeaders: {
        Authorization: `Bearer ${this.authService.getToken()}`
      }
    });

    this.client.onConnect = (frame) => {
      console.log('Connected to WS Broker: ' + frame);

      // Subscribe to global alerts (e.g. for Admins)
      if (this.authService.isAdmin()) {
        this.client.subscribe('/topic/admin/notifications', (msg: Message) => {
          this.notifications$.next(JSON.parse(msg.body));
        });
      }

      // Subscribe to targeted user alerts
      const user = this.authService.getCurrentUser();
      if (user && user.id) {
         this.client.subscribe(`/user/${user.id}/queue/notifications`, (msg: Message) => {
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
