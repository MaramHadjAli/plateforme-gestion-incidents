import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, interval, Subscription } from 'rxjs';
import { environment } from '../../../environments/environment';
import { switchMap, tap, catchError } from 'rxjs/operators';

export interface AppNotification {
  idNotification: number;
  type: string;
  title: string;
  message: string;
  severity: string;
  dateEnvoi: string;
  isRead: boolean;
  ticketId?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AppNotificationService {
  private apiUrl = `${environment.apiUrl}/notifications`;
  private unreadCountSubject = new BehaviorSubject<number>(0);
  public unreadCount$ = this.unreadCountSubject.asObservable();
  
  private pollingSubscription?: Subscription;

  constructor(private http: HttpClient) {
    this.refreshUnreadCount();
  }

  getNotifications(limit: number = 20): Observable<AppNotification[]> {
    return this.http.get<AppNotification[]>(`${this.apiUrl}?limit=${limit}`);
  }

  getUnreadCount(): Observable<{ unreadCount: number }> {
    return this.http.get<{ unreadCount: number }>(`${this.apiUrl}/unread/count`);
  }

  refreshUnreadCount(): void {
    this.getUnreadCount().subscribe({
      next: (res) => this.unreadCountSubject.next(res.unreadCount),
      error: (err) => console.error('Error fetching unread count', err)
    });
  }

  markAsRead(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/read`, {}).pipe(
      tap(() => this.refreshUnreadCount())
    );
  }

  markAllAsRead(): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/read-all`, {}).pipe(
      tap(() => this.unreadCountSubject.next(0))
    );
  }

  // Start polling for new notifications
  startPolling(intervalMs: number = 30000): void {
    this.stopPolling();
    this.pollingSubscription = interval(intervalMs).pipe(
      switchMap(() => this.getUnreadCount())
    ).subscribe({
      next: (res) => this.unreadCountSubject.next(res.unreadCount),
      error: (err) => console.error('Polling error', err)
    });
  }

  stopPolling(): void {
    if (this.pollingSubscription) {
      this.pollingSubscription.unsubscribe();
    }
  }
}
