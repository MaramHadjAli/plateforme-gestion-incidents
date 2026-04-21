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
    { label: 'Dashboard', route: '/dashboard', icon: '' },
    { label: 'Tickets', route: '/ticket-list', icon: '' },
    { label: 'Équipements', route: '/equipements', icon: '' },
    { label: 'Maintenance', route: '/maintenance', icon: '' },
    { label: 'Classement', route: '/classement', icon: '' }
  ];

  notifications: Notification[] = [
    { id: 1, type: 'critical', title: 'Incident critique', message: 'Ticket #042 — Panne critique en Salle B12', time: '2 min', unread: true, ticketId: '042' },
    { id: 2, type: 'assign', title: 'Ticket assigné', message: 'Ticket #039 vous a été assigné', time: '15 min', unread: true, ticketId: '039' },
    { id: 3, type: 'sla', title: 'SLA dépassé', message: 'SLA dépassé pour Ticket #037', time: '1h', unread: true, ticketId: '037' },
    { id: 4, type: 'badge', title: 'Nouveau badge', message: 'Badge « Rapide » obtenu !', time: '3h', unread: false },
    { id: 5, type: 'status', title: 'Statut mis à jour', message: 'Ticket #031 — Statut changé : Résolu', time: '5h', unread: false, ticketId: '031' }
  ];

  profileMenuItems: ProfileMenuItem[] = [
    { label: 'Mon Profil', route: '/profile', icon: '' },
    { label: 'Mes Tickets', route: '/my-tickets', icon: '' },
    { label: 'Mes Badges', route: '/badges', icon: '' },
    { label: 'Paramètres', route: '/settings', icon: '' }
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

  onSearchInput(event: any): void {
    const query = event.target.value;
    if (query.length > 2) {
      this.searchSuggestions = [
        `Ticket: ${query}`,
        `Salle: ${query}`,
        `Équipement: ${query}`,
        `Utilisateur: ${query}`
      ];
    } else {
      this.searchSuggestions = [];
    }
  }

  selectSuggestion(suggestion: string): void {
    this.searchQuery = suggestion;
    this.closeSearch();
    console.log('Searching for:', suggestion);
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

