import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WebSocketService } from '../../../core/services/websocket.service';
import { ToastService } from '../../../core/services/toast.service';
import { Subscription } from 'rxjs';
import { trigger, transition, style, animate } from '@angular/animations';

interface AppNotification {
  title: string;
  message: string;
  read: boolean;
  timestamp: Date;
}

@Component({
  selector: 'app-notification-panel',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notification-panel.component.html',
  animations: [
    trigger('dropdownFade', [
      transition(':enter', [
        style({ opacity: 0, transform: 'scale(0.95) translateY(-10px)' }),
        animate('200ms ease-out', style({ opacity: 1, transform: 'scale(1) translateY(0)' }))
      ]),
      transition(':leave', [
        animate('150ms ease-in', style({ opacity: 0, transform: 'scale(0.95) translateY(-10px)' }))
      ])
    ])
  ]
})
export class NotificationPanelComponent implements OnInit, OnDestroy {
  notifications: AppNotification[] = [];
  isOpen = false;
  unreadCount = 0;
  private wsSub!: Subscription;

  constructor(private wsService: WebSocketService, private toast: ToastService) {}

  ngOnInit() {
    this.wsService.connect();
    
    this.wsSub = this.wsService.notifications$.subscribe(payload => {
      if (payload) {
        const newNotif: AppNotification = {
          title: payload.title || 'New Alert',
          message: payload.message || 'You have a new system alert.',
          read: false,
          timestamp: new Date()
        };
        
        this.notifications.unshift(newNotif);
        this.unreadCount++;
        
        // Trigger Toast globally on arrival
        this.toast.showWarning(newNotif.message, newNotif.title);
      }
    });
  }

  togglePanel() {
    this.isOpen = !this.isOpen;
  }

  markAllRead() {
    this.notifications.forEach(n => n.read = true);
    this.unreadCount = 0;
  }

  ngOnDestroy() {
    if (this.wsSub) this.wsSub.unsubscribe();
    this.wsService.disconnect();
  }
}
