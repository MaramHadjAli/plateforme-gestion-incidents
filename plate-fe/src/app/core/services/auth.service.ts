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
  name: string;
  role: string;
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
        const decoded: any = jwtDecode(token);

        const user: UserInfo = {
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

        const decodedToken: any = jwtDecode(token);

        // ✅ Extract username, email, and role from JWT claims (no 'sub' field)
        const user: UserInfo = {
          email: decodedToken.email || '',
          name: decodedToken.username || '',
          role: decodedToken.role || ''
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

  /**
   * Get user data from localStorage (backward compatibility)
   */
  getUserData(): any {
    const currentUser = this.currentUserSubject.value;
    return currentUser || {};
  }

  /**
   * Change user password
   */
  changePassword(oldPassword: string, newPassword: string): Observable<any> {
    return this.http.post(`http://localhost:8080/api/users/change-password`, {
      oldPassword,
      newPassword,
      confirmPassword: newPassword
    });
  }

  /**
   * Delete user account
   */
  deleteAccount(): Observable<any> {
    return this.http.delete(`http://localhost:8080/api/users/delete-account`);
  }
}

