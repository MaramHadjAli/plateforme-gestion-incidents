import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';
  private currentUserSubject: BehaviorSubject<any>;
  public currentUser: Observable<any>;
  private tokenKey = 'token';
  private userKey = 'currentUser';

  constructor(private http: HttpClient) {
    this.currentUserSubject = new BehaviorSubject<any>(JSON.parse(localStorage.getItem(this.userKey) || '{}'));
    this.currentUser = this.currentUserSubject.asObservable();
  }

  public get currentUserValue(): any {
    return this.currentUserSubject.value;
  }

  login(email: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/login`, { email, password })
      .pipe(
        tap(response => {
          const token = response?.accessToken ?? response?.token;
          if (token) {
            localStorage.setItem(this.tokenKey, token);
            if (response.user) {
              localStorage.setItem(this.userKey, JSON.stringify(response.user));
              this.currentUserSubject.next(response.user);
            } else {
              const role = this.getUserRoleFromToken(token);
              const userData = { email, role };
              localStorage.setItem(this.userKey, JSON.stringify(userData));
              this.currentUserSubject.next(userData);
            }
          }
          return response;
        })
      );
  }

  register(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, data);
  }

  requestPasswordReset(email: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/forgot-password`, { email });
  }

  resetPassword(email: string, code: string, newPassword: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/reset-password`, {
      email,
      code,
      newPassword
    });
  }

  verifyEmail(token: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/verify-email`, { token });
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userKey);
    this.currentUserSubject.next(null);
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem(this.tokenKey);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  private decodeJwtPayload(token: string): any | null {
    try {
      const payload = token.split('.')[1];
      if (!payload) {
        return null;
      }

      const normalized = payload.replace(/-/g, '+').replace(/_/g, '/');
      const padded = normalized.padEnd(Math.ceil(normalized.length / 4) * 4, '=');
      const json = decodeURIComponent(
        atob(padded)
          .split('')
          .map((char) => `%${(`00${char.charCodeAt(0).toString(16)}`).slice(-2)}`)
          .join('')
      );
      return JSON.parse(json);
    } catch {
      return null;
    }
  }

  getUserRoleFromToken(token: string | null = this.getToken()): string | null {
    if (!token) {
      return null;
    }

    const payload = this.decodeJwtPayload(token);
    return payload?.role ?? null;
  }

  isAdmin(): boolean {
    return this.getUserRoleFromToken() === 'ADMIN';
  }

  isTechnicien(): boolean {
    return this.getUserRoleFromToken() === 'TECHNICIEN';
  }

  isDemandeur(): boolean {
    return this.getUserRoleFromToken() === 'DEMANDEUR';
  }

  getUserData(): any {
    return this.currentUserValue;
  }
}
