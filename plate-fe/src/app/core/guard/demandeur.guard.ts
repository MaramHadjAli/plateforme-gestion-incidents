import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class DemandeurGuard implements CanActivate {
  
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    // Allow both Demandeur and Admin to create tickets
    if (this.authService.isDemandeur() || this.authService.isAdmin()) {
      return true;
    }
    
    // Redirect technicians to home — they cannot create tickets
    this.router.navigate(['/home']);
    return false;
  }
}
