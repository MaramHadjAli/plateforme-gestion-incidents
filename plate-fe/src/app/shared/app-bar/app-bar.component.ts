import { Component, HostListener, OnInit, OnDestroy, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NavLink } from '../../core/models/nav-link.model';
import { ProfileMenuItem } from '../../core/models/profile-menu-item.model';
import { RouterModule, Router } from '@angular/router';
import { AuthService, UserInfo } from '../../core/services/auth.service';
import { ThemeService } from '../../core/services/theme.service';
import { Observable, Subject, of } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AppNotificationService, AppNotification } from '../../core/services/app-notification.service';

@Component({
  selector: 'app-bar',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './app-bar.component.html',
  styleUrls: ['./app-bar.component.css']
})
export class AppBarComponent implements OnInit, OnDestroy {
  activeNav = 'Dashboard';
  notifOpen = false;
  profileOpen = false;
  searchOpen = false;
  mobileOpen = false;
  searchQuery = '';
  searchSuggestions: string[] = [];
  isDarkMode = false;
  private destroy$ = new Subject<void>();

  user$: Observable<UserInfo | null>;
  unreadCount$: Observable<number>;

  userName = 'Utilisateur';
  userInitials = 'U';
  userEmail = 'user@example.com';
  avatarUrl: string | undefined = undefined;
  userRole: 'Administrateur' | 'Technicien' | 'Utilisateur' = 'Utilisateur';

  navLinks: NavLink[] = [];

  notifications: AppNotification[] = [];

  profileMenuItems: ProfileMenuItem[] = [
    { label: 'Mon Profil', route: '/profile', icon: '' },
    { label: 'Mes Tickets', route: '/my-tickets', icon: '' },
    { label: 'Mes Badges', route: '/badges', icon: '' },
    { label: 'Paramètres', route: '/settings', icon: '' }
  ];

  typeColors: Record<string, string> = {
    'CRITICAL': 'bg-red-500',
    'TICKET_ASSIGNED': 'bg-blue-500',
    'SLA_EXCEEDED': 'bg-amber-500',
    'BADGE_AWARDED': 'bg-purple-500',
    'STATUS_CHANGED': 'bg-emerald-500',
    'MAINTENANCE_REMINDER': 'bg-cyan-500'
  };

  typeIcons: Record<string, string> = {
    'CRITICAL': '⚠️',
    'TICKET_ASSIGNED': '📌',
    'SLA_EXCEEDED': '⏱️',
    'BADGE_AWARDED': '🏅',
    'STATUS_CHANGED': '✅',
    'MAINTENANCE_REMINDER': '🛠️'
  };

  private searchTerms = ['Ticket #042', 'Salle B12', 'Projecteur Sony', 'Technicien Ounelli', 'Rapport mensuel'];

  constructor(
    private elRef: ElementRef, 
    private router: Router, 
    private authService: AuthService,
    private appNotifService: AppNotificationService,
    private themeService: ThemeService
  ) {
    // getCurrentUser() returns UserInfo | null, wrap in Observable
    this.user$ = of(this.authService.getCurrentUser());
    this.unreadCount$ = this.appNotifService.unreadCount$;
  }

  ngOnInit(): void {
    const token = this.authService.getToken();
    const role = token ? this.authService.getUserRoleFromToken(token) : null;
    if (role === 'ADMIN') {
      this.userRole = 'Administrateur';
      this.navLinks = [
        { label: 'Dashboard', route: '/dashboard', icon: '' },
        { label: 'Tickets', route: '/ticket-list', icon: '' },
        { label: 'Salles', route: '/admin/salles', icon: '' },
        { label: 'Équipements', route: '/admin/equipements', icon: '' },
        { label: 'Techniciens', route: '/technicians', icon: '' }
      ];
    } else if (role === 'TECHNICIEN') {
      this.userRole = 'Technicien';
      this.navLinks = [
        { label: 'Mon Dashboard', route: '/technicien/dashboard', icon: '' },
        { label: 'Mes Tickets', route: '/ticket-list', icon: '' },
        { label: 'Maintenance', route: '/maintenance', icon: '' },
        { label: 'Classement', route: '/classement', icon: '' }
      ];
    } else {
      this.userRole = 'Utilisateur';
      this.navLinks = [
        { label: 'Nouveau Ticket', route: '/create-ticket', icon: '' },
        { label: 'Mes Tickets', route: '/ticket-list', icon: '' }
      ];
    }
    
    const user = this.authService.getUserData();
    if (user && Object.keys(user).length > 0) {
      this.userName = user.nom ? `${user.prenom || ''} ${user.nom}`.trim() : (user.email || 'Utilisateur');
      this.userEmail = user.email || '';
      const initials = ((user.prenom?.[0] || user.nom?.[0] || '') + (user.nom?.[1] || '')).toUpperCase();
      this.userInitials = initials || 'U';
    }

    this.user$.pipe(takeUntil(this.destroy$)).subscribe((user: UserInfo | null) => {
      if (user) {
        this.userName = user.name || 'Utilisateur';
        this.userEmail = user.email || 'user@example.com';
        this.userInitials = this.getInitials(this.userName);
        this.avatarUrl = user.avatarUrl;
        this.userRole = this.mapRole(user.role || 'UTILISATEUR');
        
        const dashboardLink = this.navLinks.find(l => l.label === 'Dashboard' || l.label === 'Mon Dashboard');
        if (dashboardLink) {
          dashboardLink.route = this.userRole === 'Administrateur' ? '/dashboard' : '/technicien/dashboard';
        }

        this.appNotifService.refreshUnreadCount();
        this.appNotifService.startPolling();
      } else {
        this.userName = 'Utilisateur';
        this.userInitials = 'U';
        this.userEmail = 'user@example.com';
        this.userRole = 'Utilisateur';
        this.avatarUrl = undefined;
        this.appNotifService.stopPolling();
      }
    });

    this.themeService.darkMode$
      .pipe(takeUntil(this.destroy$))
      .subscribe((isDark: boolean) => {
        this.isDarkMode = isDark;
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.appNotifService.stopPolling();
  }

  get unreadCount(): number {
    return this.notifications.filter((n) => !n.isRead).length;
  }

  private getInitials(name: string): string {
    if (!name) return 'U';
    const parts = name.trim().split(' ');
    if (parts.length >= 2) {
      return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
    }
    return name.substring(0, 2).toUpperCase();
  }

  private mapRole(role: string): 'Administrateur' | 'Technicien' | 'Utilisateur' {
    const normalized = role.toUpperCase();
    if (normalized.includes('ADMIN')) return 'Administrateur';
    if (normalized.includes('TECHNIC')) return 'Technicien';
    return 'Utilisateur';
  }

  toggleNotif(): void {
    this.notifOpen = !this.notifOpen;
    if (this.notifOpen) {
      this.loadNotifications();
      if (this.profileOpen) this.profileOpen = false;
    }
  }

  loadNotifications(): void {
    this.appNotifService.getNotifications().subscribe({
      next: (data) => this.notifications = data,
      error: (err) => console.error('Error loading notifications', err)
    });
  }

  toggleProfile(): void {
    this.profileOpen = !this.profileOpen;
    if (this.notifOpen) this.notifOpen = false;
  }

  closeSearch(): void {
    this.searchOpen = false;
    this.searchQuery = '';
  }

  markAllRead(): void {
    this.appNotifService.markAllAsRead().subscribe(() => {
      this.notifications = this.notifications.map((n) => ({ ...n, isRead: true }));
    });
  }

  markRead(id: number): void {
    this.appNotifService.markAsRead(id).subscribe(() => {
      this.notifications = this.notifications.map((n) => (n.idNotification === id ? { ...n, isRead: true } : n));
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  toggleTheme(): void {
    this.themeService.toggleDarkMode();
  }

  openNotification(notification: AppNotification): void {
    this.markRead(notification.idNotification);
    if (notification.ticketId) {
      this.router.navigate(['/ticket-list'], { queryParams: { ticket: notification.ticketId } });
    }
    this.notifOpen = false;
  }

  trackNotification(index: number, notification: AppNotification): number {
    return notification.idNotification;
  }

  onSearchInput(event: any): void {
    const query = event.target.value;
    if (query.length > 2) {
      this.searchSuggestions = [`Ticket: ${query}`, `Salle: ${query}`, `Équipement: ${query}`, `Utilisateur: ${query}`];
    } else {
      this.searchSuggestions = [];
    }
  }

  selectSuggestion(suggestion: string): void {
    this.searchQuery = suggestion;
    this.closeSearch();
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    const profileContainer = this.elRef.nativeElement.querySelector('[data-profile]');
    const notifContainer = this.elRef.nativeElement.querySelector('[data-notif]');
    const searchContainer = this.elRef.nativeElement.querySelector('[data-search]');

    if (profileContainer && !profileContainer.contains(target)) this.profileOpen = false;
    if (notifContainer && !notifContainer.contains(target)) this.notifOpen = false;
    if (searchContainer && !searchContainer.contains(target)) {
      this.searchOpen = false;
      this.searchQuery = '';
    }
  }
}