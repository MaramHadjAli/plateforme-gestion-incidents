import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { NotificationService } from '../../core/services/notification.service';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule]
})
export class SettingsComponent implements OnInit, OnDestroy {
  user: any;
  isLoading: boolean = false;
  showPasswordForm: boolean = false;
  passwordForm!: FormGroup;
  private destroy$ = new Subject<void>();

  constructor(
    private authService: AuthService,
    private notificationService: NotificationService,
    private router: Router,
    private fb: FormBuilder
  ) {
    this.user = {};
    this.initPasswordForm();
  }

  ngOnInit(): void {
    this.loadUserData();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  initPasswordForm(): void {
    this.passwordForm = this.fb.group(
      {
        oldPassword: ['', [Validators.required, Validators.minLength(6)]],
        newPassword: ['', [Validators.required, Validators.minLength(8)]],
        confirmPassword: ['', [Validators.required]]
      },
      { validators: this.passwordMatchValidator }
    );
  }

  passwordMatchValidator(group: FormGroup): { [key: string]: boolean } | null {
    const password = group.get('newPassword')?.value;
    const confirmPassword = group.get('confirmPassword')?.value;
    return password === confirmPassword ? null : { passwordMismatch: true };
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

  togglePasswordForm(): void {
    this.showPasswordForm = !this.showPasswordForm;
    if (!this.showPasswordForm) {
      this.passwordForm.reset();
    }
  }

  changePassword(): void {
    if (!this.passwordForm.valid) {
      this.notificationService.warning('Veuillez corriger les erreurs du formulaire');
      return;
    }

    this.isLoading = true;
    const passwordData = this.passwordForm.value;

    // Appel API pour changer le mot de passe
    this.authService.changePassword(passwordData.oldPassword, passwordData.newPassword)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.notificationService.success('Mot de passe changé avec succès');
          this.passwordForm.reset();
          this.showPasswordForm = false;
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Erreur lors du changement de mot de passe:', error);
          this.notificationService.error('Erreur lors du changement de mot de passe');
          this.isLoading = false;
        }
      });
  }

  updateProfile(): void {
    this.router.navigate(['/profile']);
  }

  deleteAccount(): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer votre compte? Cette action est irréversible.')) {
      if (confirm('Tapez votre email pour confirmer la suppression: ' + this.user?.email)) {
        this.isLoading = true;
        this.authService.deleteAccount()
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: () => {
              this.notificationService.success('Compte supprimé avec succès');
              setTimeout(() => {
                this.router.navigate(['/home']);
              }, 500);
            },
            error: (error) => {
              console.error('Erreur lors de la suppression du compte:', error);
              this.notificationService.error('Erreur lors de la suppression du compte');
              this.isLoading = false;
            }
          });
      }
    }
  }

  getRoleLabel(): string {
    const roleMap: { [key: string]: string } = {
      'DEMANDEUR': 'Demandeur',
      'TECHNICIEN': 'Technicien',
      'ADMIN': 'Administrateur'
    };
    return roleMap[this.user?.role] || this.user?.role || 'Utilisateur';
  }
}


