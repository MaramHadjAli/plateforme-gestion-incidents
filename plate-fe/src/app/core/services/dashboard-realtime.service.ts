import { Injectable } from '@angular/core';
import { Client } from '@stomp/stompjs';
import { Observable } from 'rxjs';
import { AdminDashboardStats } from '../models/admin-dashboard.model';
import SockJS from 'sockjs-client';

@Injectable({
  providedIn: 'root'
})
export class DashboardRealtimeService {
  private client: Client | null = null;

  connect(): Observable<AdminDashboardStats> {
    return new Observable<AdminDashboardStats>((observer) => {
      const client = new Client({
        webSocketFactory: () => new SockJS('http://localhost:8080/ws-notifications'),
        reconnectDelay: 5000
      });

      client.onConnect = () => {
        client.subscribe('/topic/admin-dashboard', (message) => {
          try {
            const payload = JSON.parse(message.body) as AdminDashboardStats;
            observer.next(payload);
          } catch {
            observer.error('Invalid dashboard payload');
          }
        });
      };

      client.onStompError = () => {
        observer.error('WebSocket error');
      };

      client.activate();
      this.client = client;

      return () => {
        client.deactivate();
      };
    });
  }
}

