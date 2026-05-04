import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ProfileService } from '../../features/profile/profile.service';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';
import { ThemeService } from '../../core/services/theme.service';
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
  activeSection: string = 'account';
  private destroy$ = new Subject<void>();

  constructor(
    private authService: AuthService,
    private profileService: ProfileService,
    private toastService: ToastService,
    private themeService: ThemeService,
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
    this.profileService.getCurrentUser()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (profile) => {
          this.user = profile;
        },
        error: (error) => {
          console.error('Erreur lors du chargement des données utilisateur:', error);
          this.toastService.showError('Impossible de charger les paramètres');
        }
      });
  }

  logout(): void {
    this.isLoading = true;
    this.authService.logout();
    this.toastService.showSuccess('Déconnexion réussie');
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
      this.toastService.showWarning('Veuillez corriger les erreurs du formulaire');
      return;
    }

    this.isLoading = true;
    const passwordData = this.passwordForm.value;

    // Appel API pour changer le mot de passe
    this.authService.changePassword(passwordData.oldPassword, passwordData.newPassword)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.toastService.showSuccess('Mot de passe changé avec succès');
          this.passwordForm.reset();
          this.showPasswordForm = false;
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Erreur lors du changement de mot de passe:', error);
          this.toastService.showError('Erreur lors du changement de mot de passe');
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
              this.toastService.showSuccess('Compte supprimé avec succès');
              setTimeout(() => {
                this.router.navigate(['/home']);
              }, 500);
            },
            error: (error) => {
              console.error('Erreur lors de la suppression du compte:', error);
              this.toastService.showError('Erreur lors de la suppression du compte');
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

  toggle2FA(): void {
    this.isLoading = true;
    this.authService.toggle2FA().subscribe({
      next: (response) => {
        this.user.twoFactorEnabled = response.twoFactorEnabled;
        this.toastService.showSuccess(
          response.twoFactorEnabled 
            ? "L'authentification à deux facteurs a été activée." 
            : "L'authentification à deux facteurs a été désactivée."
        );
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Erreur lors de la mise à jour du 2FA:', error);
        this.toastService.showError("Erreur lors de la mise à jour de l'authentification à deux facteurs.");
        this.isLoading = false;
      }
    });
  }

  manageSessions(): void {
    this.toastService.showSuccess('Toutes les autres sessions ont été déconnectées.');
  }

  toggleEmailNotifications(event: Event): void {
    const target = event.target as HTMLInputElement;
    if (target.checked) {
      this.toastService.showSuccess('Notifications par email activées. Vous recevrez désormais des emails.');
    } else {
      this.toastService.showSuccess('Notifications par email désactivées.');
    }
  }

  toggleDarkMode(event: Event): void {
    this.themeService.toggleDarkMode();
    const isDark = this.themeService.isDarkMode();
    this.toastService.showSuccess(isDark ? 'Thème sombre activé.' : 'Thème clair activé.');
  }
}


