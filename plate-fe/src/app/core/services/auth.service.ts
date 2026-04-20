import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { jwtDecode } from 'jwt-decode';

export interface AuthenticationResponse {
  token: string;
  refreshToken: string;
  role: string;
  email: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';
  private currentUserSubject = new BehaviorSubject<any>(null);

  constructor(private http: HttpClient) {
    const token = localStorage.getItem('token');
    if (token) {
       this.currentUserSubject.next(jwtDecode(token));
    }
  }

  login(credentials: any): Observable<AuthenticationResponse> {
    return this.http.post<AuthenticationResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap((response) => this.handleAuthentication(response))
    );
  }

  register(user: any): Observable<AuthenticationResponse> {
    return this.http.post<AuthenticationResponse>(`${this.apiUrl}/register`, user).pipe(
      tap((response) => this.handleAuthentication(response))
    );
  }

  private handleAuthentication(response: AuthenticationResponse) {
    const token = response.token;
    if (token) {
      localStorage.setItem('token', token);
      localStorage.setItem('user_role', response.role);
      localStorage.setItem('user_email', response.email);
      try {
        const decodedToken = jwtDecode(token);
        this.currentUserSubject.next(decodedToken);
      } catch (e) {
        console.error('Invalid token payload');
      }
    }
  }

  getRedirectUrl(role: string): string {
    switch (role) {
      case 'ADMIN':
        return '/admin';
      case 'TECHNICIEN':
      case 'TECHNICIAN':
        return '/technician';
      case 'DEMANDEUR':
      case 'USER':
        return '/tickets/new';
      default:
        return '/';
    }
  }

  logout() {
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
}

