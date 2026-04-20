import {CommonModule, NgOptimizedImage} from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, NgOptimizedImage]
})
export class RegisterComponent {
  isPasswordVisible = false;
  isSubmitting = false;
  submitted = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

  registerForm = this.fb.group({
    nom: ['', [Validators.required, Validators.minLength(2)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    role: ['DEMANDEUR', Validators.required]
  });


  onSubmit() {
    this.submitted = true;
    this.errorMessage = '';

    if (this.registerForm.invalid || this.isSubmitting) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;

    this.authService
      .register(this.registerForm.getRawValue())
      .pipe(finalize(() => (this.isSubmitting = false)))
      .subscribe({
        next: (response) => {
          const redirectUrl = this.authService.getRedirectUrl(response.role);
          this.router.navigate([redirectUrl]);
        },
        error: (err) => {
          console.error('Registration failed:', err);
          
          // Enhanced error parsing for production-grade debugging
          const backendError = err.error?.message || err.error?.error || err.message;
          this.errorMessage = typeof backendError === 'string' 
            ? backendError 
            : "Erreur de connexion au serveur. Veuillez vérifier votre connexion ou le statut du backend.";
          
          if (err.status === 0) {
            this.errorMessage = "Impossible de contacter le serveur (Network Error). Vérifiez que le backend tourne sur le port 8080 et que CORS est bien configuré.";
          }
        }
      });
  }
}
