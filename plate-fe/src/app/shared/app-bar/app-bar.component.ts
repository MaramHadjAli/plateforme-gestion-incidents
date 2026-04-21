import { Component, HostListener, OnInit, OnDestroy, ElementRef, ViewChild, Renderer2 } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NavLink } from '../../core/models/nav-link.model';
import { ProfileMenuItem } from '../../core/models/profile-menu-item.model';
import { RouterModule, Router } from '@angular/router';
import { AuthService, UserInfo } from '../../core/services/auth.service';
import { ThemeService } from '../../core/services/theme.service';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

interface Notification {
  id: number;
  type: 'critical' | 'assign' | 'sla' | 'badge' | 'status' | 'maintenance';
  title: string;
  message: string;
  time: string;
  unread: boolean;
  ticketId?: string;
}

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
  isDarkMode = false;
  private destroy$ = new Subject<void>();

  // Observable for reactive user data binding
  user$: Observable<UserInfo | null>;

  // Fallback values (will be overridden by user$ observable)
  userName = 'Utilisateur';
  userInitials = 'U';
  userEmail = 'user@example.com';
  avatarUrl: string | undefined = undefined;
  userRole: 'Administrateur' | 'Technicien' | 'Utilisateur' = 'Utilisateur';

  navLinks: NavLink[] = [
    { label: 'Dashboard', route: '/dashboard', icon: 'M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6' },
    { label: 'Tickets', route: '/ticket-list', icon: 'M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2' },
    { label: 'Équipements', route: '/equipements', icon: 'M9 3H5a2 2 0 00-2 2v4m6-6h10a2 2 0 012 2v4M9 3v18m0 0h10a2 2 0 002-2v-4M9 21H5a2 2 0 01-2-2v-4m0 0h18' },
    { label: 'Maintenance', route: '/maintenance', icon: 'M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z M15 12a3 3 0 11-6 0 3 3 0 016 0z' },
    { label: 'Classement', route: '/classement', icon: 'M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z' }
  ];

  notifications: Notification[] = [
    { id: 1, type: 'critical', title: 'Incident critique', message: 'Ticket #042 — Panne critique en Salle B12', time: '2 min', unread: true, ticketId: '042' },
    { id: 2, type: 'assign', title: 'Ticket assigné', message: 'Ticket #039 vous a été assigné', time: '15 min', unread: true, ticketId: '039' },
    { id: 3, type: 'sla', title: 'SLA dépassé', message: 'SLA dépassé pour Ticket #037', time: '1h', unread: true, ticketId: '037' },
    { id: 4, type: 'badge', title: 'Nouveau badge', message: 'Badge « Rapide » obtenu !', time: '3h', unread: false },
    { id: 5, type: 'status', title: 'Statut mis à jour', message: 'Ticket #031 — Statut changé : Résolu', time: '5h', unread: false, ticketId: '031' }
  ];

  profileMenuItems: ProfileMenuItem[] = [
    { label: 'Mon Profil', route: '/profile', icon: 'M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z' },
    { label: 'Mes Tickets', route: '/my-tickets', icon: 'M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2' },
    { label: 'Mes Badges', route: '/badges', icon: 'M5 3v4M3 5h4M6 17v4m-2-2h4m5-16l2.286 6.857L21 12l-5.714 2.143L13 21l-2.286-6.857L5 12l5.714-2.143L13 3z' },
    { label: 'Paramètres', route: '/settings', icon: 'M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z M15 12a3 3 0 11-6 0 3 3 0 016 0z' }
  ];

  typeColors: Record<Notification['type'], string> = {
    critical: 'bg-red-500',
    assign: 'bg-blue-500',
    sla: 'bg-amber-500',
    badge: 'bg-purple-500',
    status: 'bg-emerald-500',
    maintenance: 'bg-cyan-500'
  };

  typeIcons: Record<Notification['type'], string> = {
    critical: '⚠️',
    assign: '📌',
    sla: '⏱️',
    badge: '🏅',
    status: '✅',
    maintenance: '🛠️'
  };

  private searchTerms = ['Ticket #042', 'Salle B12', 'Projecteur Sony', 'Technicien Ounelli', 'Rapport mensuel'];

  constructor(
    private elRef: ElementRef,
    private router: Router,
    private authService: AuthService,
    private themeService: ThemeService
  ) {
    // Subscribe to user$ observable to get reactive updates
    this.user$ = this.authService.user$;
  }

  ngOnInit(): void {
    // Subscribe to user$ to update local display values
    this.user$.subscribe((user: UserInfo | null) => {
      if (user) {
        // ✅ Use username for display, email for secondary display
        this.userName = user.name || 'Utilisateur';
        this.userEmail = user.email || 'user@example.com';
        this.userInitials = this.getInitials(this.userName);
        this.avatarUrl = user.avatarUrl;
        // Extract role from user data if available
        this.userRole = this.mapRole(user.role || 'UTILISATEUR');
      } else {
        // Reset to defaults when user logs out
        this.userName = 'Utilisateur';
        this.userInitials = 'U';
        this.userEmail = 'user@example.com';
        this.userRole = 'Utilisateur';
        this.avatarUrl = undefined;
      }
    });

    // Subscribe to dark mode changes
    this.themeService.darkMode$
      .pipe(takeUntil(this.destroy$))
      .subscribe((isDark: boolean) => {
        this.isDarkMode = isDark;
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Extract initials from user name
   */
  private getInitials(name: string): string {
    if (!name) return 'U';
    const parts = name.trim().split(' ');
    if (parts.length >= 2) {
      return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
    }
    return name.substring(0, 2).toUpperCase();
  }

  /**
   * Map backend roles to display roles
   */
  private mapRole(role: string): 'Administrateur' | 'Technicien' | 'Utilisateur' {
    const normalized = role.toUpperCase();
    if (normalized.includes('ADMIN')) return 'Administrateur';
    if (normalized.includes('TECHNIC')) return 'Technicien';
    return 'Utilisateur';
  }

  get unreadCount(): number {
    return this.notifications.filter((n) => n.unread).length;
  }

  get searchSuggestions(): string[] {
    if (!this.searchQuery) return [];
    return this.searchTerms.filter((s) => s.toLowerCase().includes(this.searchQuery.toLowerCase()));
  }

  toggleNotif(): void {
    this.notifOpen = !this.notifOpen;
    if (this.profileOpen) this.profileOpen = false;
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
    this.notifications = this.notifications.map((n) => ({ ...n, unread: false }));
  }

  markRead(id: number): void {
    this.notifications = this.notifications.map((n) => (n.id === id ? { ...n, unread: false } : n));
  }

  /**
   * Logout: call auth service logout which will clear auth data and redirect to login
   */
  logout(): void {
    this.authService.logout();
  }

  /**
   * Toggle dark mode theme
   */
  toggleTheme(): void {
    this.themeService.toggleDarkMode();
  }

  openNotification(notification: Notification): void {
    this.markRead(notification.id);

    if (notification.ticketId) {
      this.router.navigate(['/ticket-list'], { queryParams: { ticket: notification.ticketId } });
    }

    this.notifOpen = false;
  }

  trackNotification(index: number, notification: Notification): number {
    return notification.id;
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;

    const profileContainer = this.elRef.nativeElement.querySelector('[data-profile]');
    const notifContainer = this.elRef.nativeElement.querySelector('[data-notif]');
    const searchContainer = this.elRef.nativeElement.querySelector('[data-search]');

    if (profileContainer && !profileContainer.contains(target)) {
      this.profileOpen = false;
    }

    if (notifContainer && !notifContainer.contains(target)) {
      this.notifOpen = false;
    }

    if (searchContainer && !searchContainer.contains(target)) {
      this.searchOpen = false;
      this.searchQuery = '';
    }
  }
}

