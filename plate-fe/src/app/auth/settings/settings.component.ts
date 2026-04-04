import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { NotificationService } from '../../core/services/notification.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css'],
  standalone: false,
})
export class SettingsComponent implements OnInit {
  user: any;
  isLoading: boolean = false;

  constructor(
    private authService: AuthService,
    private notificationService: NotificationService,
    private router: Router
  ) {
    this.user = {};
  }

  ngOnInit(): void {
    this.loadUserData();
  }

  loadUserData(): void {
    this.user = this.authService.getUserData();
    if (!this.user || Object.keys(this.user).length === 0) {
      this.notificationService.warning('Informations utilisateur non disponibles');
    }
  }

  logout(): void {
    this.isLoading = true;
    this.authService.logout();
    this.notificationService.success('Déconnexion réussie');
    setTimeout(() => {
      this.router.navigate(['/home']);
    }, 500);
  }

  changePassword(): void {
    this.notificationService.info('Fonctionnalité en cours de développement');
  }

  updateProfile(): void {
    this.notificationService.info('Fonctionnalité en cours de développement');
  }

  deleteAccount(): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer votre compte? Cette action est irréversible.')) {
      this.notificationService.info('Suppression en cours...');
    }
  }
}

