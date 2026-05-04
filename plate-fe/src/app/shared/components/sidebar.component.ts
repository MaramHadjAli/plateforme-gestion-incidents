import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { AppNotificationService, AppNotification } from '../../core/services/app-notification.service';
import { Subscription, Observable } from 'rxjs';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <aside class="admin-sidebar">
      <div class="admin-brand">
        <img src="/logos/475423667_1092274072699371_3696065154213442200_n-removebg-preview.png" class="admin-brand__logo" alt="Logo">
        <div class="brand-text">
          <strong>ENICarthage</strong>
          <span>Incidents</span>
        </div>
      </div>

      <nav class="admin-nav">
        <a *ngFor="let item of sidebarItems" 
           [routerLink]="item.route" 
           routerLinkActive="is-active" 
           class="admin-nav__item">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <path [attr.d]="item.icon"></path>
          </svg>
          <span>{{ item.label }}</span>
        </a>
      </nav>

      <div class="admin-sidebar__footer">
        <div class="notif-wrapper" (click)="$event.stopPropagation()">
          <button (click)="toggleNotif()" class="notif-btn" [class.has-unread]="((unreadCount$ | async) || 0) > 0">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M18 8A6 6 0 006 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
              <path d="M13.73 21a2 2 0 01-3.46 0"></path>
            </svg>
            <span *ngIf="((unreadCount$ | async) || 0) > 0" class="notif-badge">{{ unreadCount$ | async }}</span>
          </button>
          
          <div *ngIf="notifOpen" class="notif-popover">
            <div class="notif-header">
              <span>Notifications</span>
              <button (click)="markAllRead()" class="mark-all-btn">Tout marquer lu</button>
            </div>
            <div class="notif-list">
              <div *ngFor="let n of notifications" class="notif-item" [class.is-unread]="!n.isRead" (click)="openNotif(n)">
                <div class="notif-icon" [ngClass]="n.type"></div>
                <div class="notif-content">
                  <div class="notif-title">{{ n.title }}</div>
                  <div class="notif-msg">{{ n.message }}</div>
                  <div class="notif-time">{{ n.dateEnvoi | date:'short' }}</div>
                </div>
              </div>
              <div *ngIf="notifications.length === 0" class="notif-empty">Aucune notification</div>
            </div>
          </div>
        </div>

        <div class="admin-avatar">{{ userInitials }}</div>
        <div class="footer-user">
          <strong>{{ userName }}</strong>
          <span>{{ userRole }}</span>
        </div>
        <button (click)="logout()" class="logout-btn" title="Déconnexion">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4M16 17l5-5-5-5M21 12H9"></path>
          </svg>
        </button>
      </div>
    </aside>
  `,
  styles: [`
    .admin-sidebar {
      display: flex;
      flex-direction: column;
      width: 280px;
      height: 100vh;
      gap: 1.5rem;
      padding: 1.5rem 1.25rem;
      background: rgba(255, 255, 255, 0.95);
      border-right: 1px solid rgba(148, 163, 184, 0.15);
      backdrop-filter: blur(10px);
      position: sticky;
      top: 0;
    }

    :host-context(.dark) .admin-sidebar {
      background: rgba(15, 23, 42, 0.95);
      border-color: rgba(255, 255, 255, 0.1);
    }

    .admin-brand {
      display: flex;
      align-items: center;
      gap: 1rem;
      padding: 0.5rem;
    }

    .admin-brand__logo {
      width: 3.5rem;
      height: 3.5rem;
      object-fit: contain;
    }

    .brand-text strong {
      display: block;
      font-size: 1.2rem;
      font-weight: 800;
      background: linear-gradient(135deg, #2563eb, #9333ea);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
    }

    .brand-text span {
      font-size: 0.8rem;
      color: #64748b;
      text-transform: uppercase;
      letter-spacing: 0.1em;
    }

    .admin-nav {
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
      margin-top: 1rem;
    }

    .admin-nav__item {
      display: flex;
      align-items: center;
      gap: 1rem;
      padding: 0.85rem 1rem;
      border-radius: 1rem;
      color: #475569;
      text-decoration: none;
      font-weight: 600;
      transition: all 0.2s ease;
    }

    .admin-nav__item svg {
      width: 1.25rem;
      height: 1.25rem;
    }

    .admin-nav__item:hover {
      background: rgba(37, 99, 235, 0.05);
      color: #2563eb;
    }

    .admin-nav__item.is-active {
      background: linear-gradient(135deg, #2563eb, #9333ea);
      color: white;
      box-shadow: 0 10px 20px rgba(37, 99, 235, 0.2);
    }

    .admin-sidebar__footer {
      margin-top: auto;
      display: flex;
      align-items: center;
      gap: 1rem;
      padding: 1rem;
      background: rgba(255, 255, 255, 0.5);
      border-radius: 1.25rem;
      border: 1px solid rgba(148, 163, 184, 0.1);
    }

    :host-context(.dark) .admin-sidebar__footer {
      background: rgba(255, 255, 255, 0.05);
    }

    .admin-avatar {
      width: 2.5rem;
      height: 2.5rem;
      border-radius: 50%;
      background: linear-gradient(135deg, #2563eb, #9333ea);
      display: grid;
      place-items: center;
      color: white;
      font-weight: 700;
    }

    .footer-user strong {
      display: block;
      font-size: 0.95rem;
      font-weight: 700;
    }

    .footer-user span {
      font-size: 0.8rem;
      color: #64748b;
    }

    .logout-btn {
      margin-left: auto;
      background: none;
      border: none;
      color: #64748b;
      cursor: pointer;
      padding: 0.5rem;
      border-radius: 0.5rem;
      transition: all 0.2s;
    }

    .logout-btn:hover {
      background: #fee2e2;
      color: #dc2626;
    }

    .logout-btn svg {
      width: 1.25rem;
      height: 1.25rem;
    }

    .notif-wrapper {
      position: relative;
    }

    .notif-btn {
      background: none;
      border: none;
      color: #64748b;
      cursor: pointer;
      padding: 0.6rem;
      border-radius: 0.75rem;
      transition: all 0.2s;
      position: relative;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .notif-btn:hover {
      background: rgba(37, 99, 235, 0.05);
      color: #2563eb;
    }

    .notif-btn svg {
      width: 1.25rem;
      height: 1.25rem;
    }

    .notif-badge {
      position: absolute;
      top: 0;
      right: 0;
      background: #ef4444;
      color: white;
      font-size: 0.7rem;
      font-weight: 800;
      width: 1.1rem;
      height: 1.1rem;
      border-radius: 50%;
      display: grid;
      place-items: center;
      border: 2px solid white;
    }

    .notif-popover {
      position: absolute;
      bottom: 100%;
      left: 0;
      width: 320px;
      background: white;
      border-radius: 1rem;
      box-shadow: 0 20px 40px rgba(0,0,0,0.15);
      border: 1px solid rgba(0,0,0,0.05);
      margin-bottom: 0.75rem;
      z-index: 1000;
      overflow: hidden;
      display: flex;
      flex-direction: column;
      animation: slideUp 0.3s cubic-bezier(0.16, 1, 0.3, 1);
    }

    @keyframes slideUp {
      from { transform: translateY(10px); opacity: 0; }
      to { transform: translateY(0); opacity: 1; }
    }

    .notif-header {
      padding: 1rem;
      border-bottom: 1px solid #f1f5f9;
      display: flex;
      justify-content: space-between;
      align-items: center;
      background: #f8fbff;
    }

    .notif-header span {
      font-weight: 700;
      color: #1e293b;
    }

    .mark-all-btn {
      background: none;
      border: none;
      color: #2563eb;
      font-size: 0.8rem;
      font-weight: 600;
      cursor: pointer;
    }

    .notif-list {
      max-height: 400px;
      overflow-y: auto;
    }

    .notif-item {
      padding: 1rem;
      display: flex;
      gap: 1rem;
      cursor: pointer;
      transition: background 0.2s;
      border-bottom: 1px solid #f1f5f9;
    }

    .notif-item:hover {
      background: #f8fbff;
    }

    .notif-item.is-unread {
      background: rgba(37, 99, 235, 0.02);
      border-left: 3px solid #2563eb;
    }

    .notif-icon {
      width: 0.75rem;
      height: 0.75rem;
      border-radius: 50%;
      margin-top: 0.3rem;
      flex-shrink: 0;
    }

    .notif-icon.TICKET_ASSIGNED { background: #3b82f6; }
    .notif-icon.STATUS_CHANGED { background: #10b981; }
    .notif-icon.MAINTENANCE_REMINDER { background: #f59e0b; }
    .notif-icon.BADGE_AWARDED { background: #8b5cf6; }

    .notif-title {
      font-size: 0.9rem;
      font-weight: 700;
      margin-bottom: 0.15rem;
    }

    .notif-msg {
      font-size: 0.8rem;
      color: #64748b;
      margin-bottom: 0.25rem;
      line-height: 1.4;
    }

    .notif-time {
      font-size: 0.7rem;
      color: #94a3b8;
    }

    .notif-empty {
      padding: 2rem;
      text-align: center;
      color: #94a3b8;
      font-size: 0.9rem;
    }
  `]
})
export class SidebarComponent implements OnInit, OnDestroy {
  userName = 'Utilisateur';
  userInitials = 'UT';
  userRole = 'Administrateur';
  
  notifOpen = false;
  notifications: AppNotification[] = [];
  unreadCount$: Observable<number>;
  
  readonly sidebarItems = [
    { label: 'Dashboard', route: '/admin/dashboard', icon: 'M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6' },
    { label: 'Gestion Incidents', route: '/ticket-list', icon: 'M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2' },
    { label: 'Nouveau Ticket', route: '/create-ticket', icon: 'M12 4v16m8-8H4' },
    { label: 'Mon Profil', route: '/profile', icon: 'M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z' },
    { label: 'Salles', route: '/admin/salles', icon: 'M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4' },
    { label: 'Equipements', route: '/admin/equipements', icon: 'M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z' },
    { label: 'Paramètres', route: '/settings', icon: 'M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z' }
  ];

  constructor(
    private authService: AuthService, 
    private router: Router,
    private appNotifService: AppNotificationService
  ) {
    this.unreadCount$ = this.appNotifService.unreadCount$;
  }

  ngOnInit(): void {
    const user = this.authService.getUserData();
    if (user) {
      const fallbackName = user.role === 'ADMIN' ? 'Admin Enicarthage' : 'Utilisateur';
      this.userName = user.name || (user.nom ? `${user.prenom || ''} ${user.nom}`.trim() : fallbackName);
      this.userInitials = this.getInitials(this.userName);
      this.userRole = this.mapRole(user.role);
      
      this.appNotifService.refreshUnreadCount();
      this.appNotifService.startPolling();
      
      // Close popover on outside click
      document.addEventListener('click', () => {
        this.notifOpen = false;
      });
    }
  }

  ngOnDestroy(): void {
    this.appNotifService.stopPolling();
  }

  logout(): void {
    this.appNotifService.stopPolling();
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  private mapRole(role: string): string {
    if (role === 'ADMIN') return 'Administrateur';
    if (role === 'TECHNICIEN') return 'Technicien';
    return 'Demandeur';
  }

  toggleNotif(): void {
    this.notifOpen = !this.notifOpen;
    if (this.notifOpen) {
      this.appNotifService.getNotifications().subscribe(notifs => {
        this.notifications = notifs;
      });
    }
  }

  markAllRead(): void {
    this.appNotifService.markAllAsRead().subscribe(() => {
      this.notifications = this.notifications.map(n => ({ ...n, isRead: true }));
    });
  }

  openNotif(notif: AppNotification): void {
    this.appNotifService.markAsRead(notif.idNotification).subscribe();
    if (notif.ticketId) {
      this.router.navigate(['/ticket-list'], { queryParams: { ticket: notif.ticketId } });
    }
    this.notifOpen = false;
  }

  private getInitials(name: string): string {
    if (!name) return 'UT';
    const parts = name.trim().split(' ');
    if (parts.length >= 2) {
      return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
    }
    return name.substring(0, 2).toUpperCase();
  }
}
