import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
      return false;
    }

    const allowedRoles = (route.data?.['roles'] as string[] | undefined) ?? [];
    if (allowedRoles.length === 0) {
      return true;
    }

    const rawRole = this.authService.getUserRoleFromToken() ?? '';
    const normalizedRole = rawRole.toUpperCase();
    const normalizedAllowed = allowedRoles.map((role) => role.toUpperCase());

    if (normalizedAllowed.includes(normalizedRole)) {
      return true;
    }

    this.router.navigate([this.getDefaultRouteForRole(normalizedRole)]);
    return false;
  }

  private getDefaultRouteForRole(role: string): string {
    if (role === 'ADMIN') {
      return '/admin/dashboard';
    }

    if (role === 'TECHNICIEN' || role === 'TECHNICIAN') {
      return '/ticket-list';
    }

    return '/create-ticket';
  }
}
