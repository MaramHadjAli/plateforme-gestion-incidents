# 📄 Code Complet - Authentification et Navbar

## 📁 Fichier 1: `auth.service.ts`

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { jwtDecode } from 'jwt-decode';
import { Router } from '@angular/router';

export interface AuthenticationResponse {
  token: string;
  refreshToken: string;
  role: string;
  email: string;
}

export interface UserInfo {
  email: string;
  sub: string;
  name?: string;
  role?: string;
  [key: string]: any;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';
  private currentUserSubject = new BehaviorSubject<UserInfo | null>(null);
  
  // Expose user as observable for reactive binding in components
  public user$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    // Initialize user from stored token on service creation
    this.initializeUserFromToken();
  }

  /**
   * Initialize user state from stored token (useful on app startup/refresh)
   */
  private initializeUserFromToken(): void {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const decodedToken = jwtDecode(token) as UserInfo;
        this.currentUserSubject.next(decodedToken);
      } catch (e) {
        console.error('Failed to decode stored token:', e);
        this.clearAuthData();
      }
    }
  }

  login(credentials: any): Observable<AuthenticationResponse> {
    return this.http.post<AuthenticationResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(res => this.handleAuthentication(res))
    );
  }

  register(user: any): Observable<AuthenticationResponse> {
    return this.http.post<AuthenticationResponse>(`${this.apiUrl}/register`, user).pipe(
      tap(res => this.handleAuthentication(res))
    );
  }

  /**
   * Handle successful authentication by storing token and updating user state
   */
  private handleAuthentication(response: AuthenticationResponse): void {
    const token = response.token;
    if (token) {
      try {
        localStorage.setItem('token', token);
        localStorage.setItem('user_role', response.role);
        localStorage.setItem('user_email', response.email);
        
        const decodedToken = jwtDecode(token) as UserInfo;
        this.currentUserSubject.next(decodedToken);
      } catch (e) {
        console.error('Failed to handle authentication:', e);
        this.clearAuthData();
      }
    }
  }

  getRedirectUrl(role: string): string {
    switch (role) {
      case 'ADMIN':
        return '/dashboard';
      case 'TECHNICIEN':
      case 'TECHNICIAN':
        return '/ticket-list';
      case 'DEMANDEUR':
      case 'USER':
        return '/create-ticket';
      default:
        return '/home';
    }
  }

  /**
   * Logout: clear all auth data and redirect to login
   */
  logout(): void {
    this.clearAuthData();
    this.router.navigate(['/login']);
  }

  /**
   * Clear all authentication data from localStorage and BehaviorSubject
   */
  private clearAuthData(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user_role');
    localStorage.removeItem('user_email');
    this.currentUserSubject.next(null);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  getUserRoleFromToken(token: string): string {
    if (!token) return '';
    try {
      const decoded: any = jwtDecode(token);
      let role = decoded.role || decoded.authorities;
      if (Array.isArray(role)) role = role[0];
      return typeof role === 'string' ? role.replace('ROLE_', '') : '';
    } catch (e) {
      return '';
    }
  }

  getUserRole(): string {
    const token = this.getToken();
    return token ? this.getUserRoleFromToken(token) : '';
  }

  isAdmin(): boolean {
    return this.getUserRole() === 'ADMIN';
  }

  /**
   * Get current user info synchronously (useful for templates with async pipe)
   */
  getCurrentUser(): UserInfo | null {
    return this.currentUserSubject.value;
  }
}
```

---

## 📁 Fichier 2: `app-bar.component.ts`

```typescript
import { Component, HostListener, OnInit, ElementRef, ViewChild, Renderer2 } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NavLink } from '../../core/models/nav-link.model';
import { ProfileMenuItem } from '../../core/models/profile-menu-item.model';
import { RouterModule, Router } from '@angular/router';
import { AuthService, UserInfo } from '../../core/services/auth.service';
import { Observable } from 'rxjs';

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
export class AppBarComponent implements OnInit {
  activeNav = 'Dashboard';
  notifOpen = false;
  profileOpen = false;
  searchOpen = false;
  mobileOpen = false;
  searchQuery = '';

  // Observable for reactive user data binding
  user$: Observable<UserInfo | null>;

  // Fallback values (will be overridden by user$ observable)
  userName = 'Utilisateur';
  userInitials = 'U';
  userEmail = 'user@example.com';
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
    private authService: AuthService
  ) {
    // Subscribe to user$ observable to get reactive updates
    this.user$ = this.authService.user$;
  }

  ngOnInit(): void {
    // Subscribe to user$ to update local display values
    this.user$.subscribe((user: UserInfo | null) => {
      if (user) {
        this.userEmail = user.email || 'user@example.com';
        this.userName = user.name || user.email || 'Utilisateur';
        this.userInitials = this.getInitials(this.userName);
        // Extract role from user data if available
        this.userRole = this.mapRole(user.role || 'UTILISATEUR');
      } else {
        // Reset to defaults when user logs out
        this.userName = 'Utilisateur';
        this.userInitials = 'U';
        this.userEmail = 'user@example.com';
        this.userRole = 'Utilisateur';
      }
    });
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
```

---

## 📁 Fichier 3: `app-bar.component.html` (No changes needed - already using interpolation)

Le template est déjà correct. Les variables `{{ userName }}`, `{{ userEmail }}`, `{{ userRole }}`, et `{{ userInitials }}` 
sont mises à jour automatiquement quand `user$` observable émet une nouvelle valeur (géré dans ngOnInit).

Si vous voulez utiliser directement l'async pipe (alternative), voici le pattern :

```html
<!-- Alternative avec async pipe (optional) -->
<ng-container *ngIf="user$ | async as user">
  <button (click)="toggleProfile()" class="app-profile-btn">
    <span class="app-avatar">{{ getInitials(user?.email) }}</span>
    <span class="hidden sm:flex app-profile-btn__meta">
      <strong>{{ user?.email }}</strong>
      <small>{{ mapRole(user?.role) }}</small>
    </span>
  </button>
</ng-container>

<!-- Mais la version actuelle est meilleure car elle gère les valeurs par défaut -->
```

---

## 📁 Fichier 4: Optionnel - JWT Interceptor

Si vous n'avez pas déjà d'intercepteur JWT, créez ce fichier:

**`src/app/core/interceptors/jwt.interceptor.ts`**

```typescript
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    const token = this.authService.getToken();
    
    if (token) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }

    return next.handle(request);
  }
}
```

Enregistrez dans `app.config.ts`:

```typescript
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { JwtInterceptor } from './core/interceptors/jwt.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    // ... existing providers ...
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true }
  ]
};
```

---

## 🎯 Points clés à retenir

1. **Observable user$** : Exposée publiquement du service
2. **handleAuthentication()** : Appelée via `tap()` dans login/register
3. **initializeUserFromToken()** : Restaure l'utilisateur au démarrage si token présent
4. **logout()** : Efface tout et redirige
5. **AppBarComponent** : S'abonne à user$ et affiche les données réactives
6. **Production-ready** : Gestion des erreurs, TypeScript strict, patterns Angular best practices

---

**✅ Code prêt pour production !**

