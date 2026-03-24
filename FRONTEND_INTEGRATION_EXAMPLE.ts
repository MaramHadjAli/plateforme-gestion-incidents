// EXEMPLE D'INTÉGRATION FRONTEND ANGULAR
// Fichier: src/app/core/services/password-reset.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

interface ForgotPasswordRequest {
  email: string;
}

interface ResetPasswordRequest {
  token: string;
  newPassword: string;
  confirmPassword: string;
}

interface PasswordResetResponse {
  success: boolean;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class PasswordResetService {
  private apiUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) { }

  // Demander réinitialisation du mot de passe
  forgotPassword(email: string): Observable<PasswordResetResponse> {
    const request: ForgotPasswordRequest = { email };
    return this.http.post<PasswordResetResponse>(
      `${this.apiUrl}/forgot-password`,
      request
    );
  }

  // Réinitialiser le mot de passe
  resetPassword(
    token: string,
    newPassword: string,
    confirmPassword: string
  ): Observable<PasswordResetResponse> {
    const request: ResetPasswordRequest = {
      token,
      newPassword,
      confirmPassword
    };
    return this.http.post<PasswordResetResponse>(
      `${this.apiUrl}/reset-password`,
      request
    );
  }

  // Valider le token (optionnel - pour vérifier avant afficher le formulaire)
  validateResetToken(token: string): Observable<{ valid: boolean }> {
    return this.http.get<{ valid: boolean }>(
      `${this.apiUrl}/validate-token/${token}`
    );
  }
}

// ============================================
// EXEMPLE: Composant "Mot de Passe Oublié"
// ============================================

import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PasswordResetService } from '@core/services/password-reset.service';
import { ToastrService } from 'ngx-toastr'; // ou votre service de notifications

@Component({
  selector: 'app-forgot-password',
  template: `
    <div class="forgot-password-container">
      <h2>Réinitialiser votre mot de passe</h2>

      <!-- Formulaire de demande -->
      <form [formGroup]="forgotForm" (ngSubmit)="onSubmit()" *ngIf="!emailSent">
        <div class="form-group">
          <label for="email">Adresse Email</label>
          <input
            type="email"
            id="email"
            formControlName="email"
            class="form-control"
            placeholder="Entrez votre email"
          />
          <div *ngIf="forgotForm.get('email')?.errors" class="error-message">
            Veuillez entrer une adresse email valide
          </div>
        </div>

        <button
          type="submit"
          class="btn btn-primary"
          [disabled]="forgotForm.invalid || isLoading"
        >
          {{ isLoading ? 'Envoi en cours...' : 'Envoyer' }}
        </button>
      </form>

      <!-- Message de confirmation -->
      <div *ngIf="emailSent" class="alert alert-success">
        <p>✅ Un email de réinitialisation a été envoyé à votre adresse.</p>
        <p>Veuillez vérifier votre boîte mail et cliquer sur le lien fourni.</p>
        <p><strong>Le lien expire dans 15 minutes.</strong></p>
      </div>

      <!-- Message d'erreur -->
      <div *ngIf="errorMessage" class="alert alert-danger">
        {{ errorMessage }}
      </div>
    </div>
  `,
  styles: [`
    .forgot-password-container {
      max-width: 400px;
      margin: 50px auto;
      padding: 20px;
      border: 1px solid #ddd;
      border-radius: 8px;
    }
    .form-group {
      margin-bottom: 15px;
    }
    .error-message {
      color: #d32f2f;
      font-size: 12px;
      margin-top: 5px;
    }
  `]
})
export class ForgotPasswordComponent {
  forgotForm: FormGroup;
  isLoading = false;
  emailSent = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private passwordResetService: PasswordResetService,
    private toastr: ToastrService
  ) {
    this.forgotForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  onSubmit() {
    if (this.forgotForm.invalid) return;

    this.isLoading = true;
    this.errorMessage = '';
    const email = this.forgotForm.get('email')?.value;

    this.passwordResetService.forgotPassword(email).subscribe(
      (response) => {
        this.isLoading = false;
        if (response.success) {
          this.emailSent = true;
          this.toastr.success(response.message);
        } else {
          this.errorMessage = response.message;
          this.toastr.error(response.message);
        }
      },
      (error) => {
        this.isLoading = false;
        this.errorMessage = 'Une erreur est survenue. Veuillez réessayer.';
        this.toastr.error(this.errorMessage);
      }
    );
  }
}

// ============================================
// EXEMPLE: Composant "Réinitialiser le Mot de Passe"
// ============================================

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { PasswordResetService } from '@core/services/password-reset.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-reset-password',
  template: `
    <div class="reset-password-container">
      <h2>Réinitialiser votre mot de passe</h2>

      <!-- Message d'erreur (token invalide/expiré) -->
      <div *ngIf="tokenInvalid" class="alert alert-danger">
        <p>❌ Le lien de réinitialisation est invalide ou a expiré.</p>
        <p>
          <a routerLink="/forgot-password">Demander un nouveau lien</a>
        </p>
      </div>

      <!-- Formulaire de réinitialisation -->
      <form [formGroup]="resetForm" (ngSubmit)="onSubmit()" *ngIf="!tokenInvalid && !isSubmitted">
        <div class="form-group">
          <label for="newPassword">Nouveau Mot de Passe</label>
          <input
            type="password"
            id="newPassword"
            formControlName="newPassword"
            class="form-control"
            placeholder="Entrez votre nouveau mot de passe"
          />
          <div *ngIf="resetForm.get('newPassword')?.errors" class="error-message">
            <p *ngIf="resetForm.get('newPassword')?.errors?.['required']">
              Le mot de passe est requis
            </p>
            <p *ngIf="resetForm.get('newPassword')?.errors?.['minlength']">
              Le mot de passe doit avoir au moins 6 caractères
            </p>
          </div>
        </div>

        <div class="form-group">
          <label for="confirmPassword">Confirmer le Mot de Passe</label>
          <input
            type="password"
            id="confirmPassword"
            formControlName="confirmPassword"
            class="form-control"
            placeholder="Confirmez votre mot de passe"
          />
          <div *ngIf="resetForm.get('confirmPassword')?.errors" class="error-message">
            <p *ngIf="resetForm.get('confirmPassword')?.errors?.['required']">
              La confirmation est requise
            </p>
            <p *ngIf="resetForm.errors?.['passwordMismatch']">
              Les mots de passe ne correspondent pas
            </p>
          </div>
        </div>

        <button
          type="submit"
          class="btn btn-primary"
          [disabled]="resetForm.invalid || isLoading"
        >
          {{ isLoading ? 'Réinitialisation...' : 'Réinitialiser' }}
        </button>
      </form>

      <!-- Message de succès -->
      <div *ngIf="isSubmitted && !errorMessage" class="alert alert-success">
        <p>✅ Votre mot de passe a été réinitialisé avec succès!</p>
        <p>
          <a routerLink="/login" class="btn btn-primary">Se connecter</a>
        </p>
      </div>

      <!-- Message d'erreur -->
      <div *ngIf="errorMessage" class="alert alert-danger">
        {{ errorMessage }}
      </div>
    </div>
  `,
  styles: [`
    .reset-password-container {
      max-width: 400px;
      margin: 50px auto;
      padding: 20px;
      border: 1px solid #ddd;
      border-radius: 8px;
    }
    .form-group {
      margin-bottom: 15px;
    }
    .error-message {
      color: #d32f2f;
      font-size: 12px;
      margin-top: 5px;
    }
  `]
})
export class ResetPasswordComponent implements OnInit {
  resetForm: FormGroup;
  isLoading = false;
  isSubmitted = false;
  tokenInvalid = false;
  errorMessage = '';
  token = '';

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private passwordResetService: PasswordResetService,
    private toastr: ToastrService
  ) {
    this.resetForm = this.fb.group(
      {
        newPassword: ['', [Validators.required, Validators.minLength(6)]],
        confirmPassword: ['', [Validators.required]]
      },
      { validators: this.passwordMatchValidator }
    );
  }

  ngOnInit() {
    // Récupérer le token depuis l'URL
    this.route.queryParams.subscribe(params => {
      this.token = params['token'];
      if (!this.token) {
        this.tokenInvalid = true;
      }
    });
  }

  // Validateur personnalisé pour vérifier que les mots de passe correspondent
  passwordMatchValidator(group: AbstractControl): ValidationErrors | null {
    const password = group.get('newPassword')?.value;
    const confirmPassword = group.get('confirmPassword')?.value;

    if (password && confirmPassword && password !== confirmPassword) {
      return { passwordMismatch: true };
    }
    return null;
  }

  onSubmit() {
    if (this.resetForm.invalid) return;

    this.isLoading = true;
    this.errorMessage = '';
    const { newPassword, confirmPassword } = this.resetForm.value;

    this.passwordResetService.resetPassword(
      this.token,
      newPassword,
      confirmPassword
    ).subscribe(
      (response) => {
        this.isLoading = false;
        if (response.success) {
          this.isSubmitted = true;
          this.toastr.success(response.message);
          // Redirection automatique après 3 secondes
          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 3000);
        } else {
          this.errorMessage = response.message;
          this.toastr.error(response.message);
        }
      },
      (error) => {
        this.isLoading = false;
        this.errorMessage = error.error?.message || 'Une erreur est survenue.';
        this.toastr.error(this.errorMessage);
      }
    );
  }
}

// ============================================
// ROUTING (app-routing.module.ts)
// ============================================

import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ForgotPasswordComponent } from './auth/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './auth/reset-password/reset-password.component';

const routes: Routes = [
  {
    path: 'auth',
    children: [
      // ... autres routes
      { path: 'forgot-password', component: ForgotPasswordComponent },
      { path: 'reset-password', component: ResetPasswordComponent }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

// ============================================
// APPEL DEPUIS LE FORMULAIRE DE LOGIN
// ============================================

<div class="login-form">
  <!-- ... Champs login ... -->
  <p class="forgot-password">
    <a routerLink="/auth/forgot-password">Mot de passe oublié?</a>
  </p>
</div>

