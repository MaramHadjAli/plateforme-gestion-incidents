import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { jwtDecode } from 'jwt-decode';
import { Router } from '@angular/router';

export interface AuthenticationResponse {
  token: string;
  refreshToken: string;
  user: {
    id: number;
    nom: string;
    email: string;
    role: string;
  };
}

export interface UserInfo {
  id: number;
  email: string;
  name: string;
  role: string;
  avatarUrl?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';
  private currentUserSubject = new BehaviorSubject<UserInfo | null>(null);

  public user$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    this.initializeUserFromToken();
  }

  private initializeUserFromToken(): void {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const decoded: any = jwtDecode(token);
        const user: UserInfo = {
          id: decoded.id || 0,
          email: decoded.email || '',
          name: decoded.username || '',
          role: decoded.role || ''
        };
        this.currentUserSubject.next(user);
      } catch (e) {
        this.clearAuthData();
      }
    }
  }

  login(credentials: { email: string, password: string }): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/login`, credentials)
      .pipe(
        tap(response => {
          const token = response?.accessToken ?? response?.token;
          if (token) {
            localStorage.setItem('token', token);
            if (response.user) {
              localStorage.setItem('user', JSON.stringify(response.user));
              this.currentUserSubject.next(response.user);
            } else {
              const role = this.getUserRoleFromToken(token);
              const decoded: any = jwtDecode(token);
              const userData: UserInfo = { 
                id: decoded.id || 0,
                email: credentials.email, 
                name: credentials.email, 
                role 
              };
              localStorage.setItem('user', JSON.stringify(userData));
              this.currentUserSubject.next(userData);
            }
          }
          return response;
        })
      );
  }

  register(user: any): Observable<AuthenticationResponse> {
    return this.http.post<AuthenticationResponse>(`${this.apiUrl}/register`, user).pipe(
      tap(res => this.handleAuthentication(res))
    );
  }

  private handleAuthentication(response: AuthenticationResponse): void {
    const token = response.token;
    if (token) {
      try {
        localStorage.setItem('token', token);
        const decodedToken: any = jwtDecode(token);
        const user: UserInfo = {
          id: decodedToken.id || 0,
          email: decodedToken.email || '',
          name: decodedToken.username || '',
          role: decodedToken.role || '',
          avatarUrl: localStorage.getItem('avatarUrl') || undefined
        };
        this.currentUserSubject.next(user);
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

  logout(): void {
    this.clearAuthData();
    this.router.navigate(['/login']);
  }

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

  getUserRoleFromToken(token?: string): string {
    const authToken = token || this.getToken();
    if (!authToken) return '';
    try {
      const decoded: any = jwtDecode(authToken);
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

  isDemandeur(): boolean {
    return this.getUserRole() === 'DEMANDEUR';
  }

  isTechnicien(): boolean {
    const role = this.getUserRole();
    return role === 'TECHNICIEN' || role === 'TECHNICIAN';
  }

  getCurrentUser(): UserInfo | null {
    return this.currentUserSubject.value;
  }

  updateAvatarUrl(url: string | undefined): void {
    const currentUser = this.currentUserSubject.value;
    if (currentUser) {
      if (url) {
        localStorage.setItem('avatarUrl', url);
      } else {
        localStorage.removeItem('avatarUrl');
      }
      this.currentUserSubject.next({
        ...currentUser,
        avatarUrl: url
      });
    }
  }

  updateUserDetails(name: string, telephone?: string): void {
    const currentUser = this.currentUserSubject.value;
    if (currentUser) {
      this.currentUserSubject.next({
        ...currentUser,
        name: name
      });
    }
  }

  getUserData(): any {
    const currentUser = this.currentUserSubject.value;
    return currentUser || {};
  }

  changePassword(oldPassword: string, newPassword: string): Observable<any> {
    return this.http.post(`http://localhost:8080/api/users/change-password`, {
      oldPassword,
      newPassword,
      confirmPassword: newPassword
    });
  }

  deleteAccount(): Observable<any> {
    return this.http.delete(`http://localhost:8080/api/users/delete-account`);
  }

  toggle2FA(): Observable<any> {
    return this.http.post(`http://localhost:8080/api/users/toggle-2fa`, {});
  }
}