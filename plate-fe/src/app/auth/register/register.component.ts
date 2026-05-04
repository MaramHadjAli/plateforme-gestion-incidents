import { CommonModule, NgOptimizedImage } from '@angular/common';
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
    private router: Router,
    private http: HttpClient
  ) {}

  registerForm = this.fb.group({
    nom: ['', [Validators.required, Validators.minLength(2)]],
    prenom: ['', [Validators.required, Validators.minLength(2)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    role: ['DEMANDEUR'],
    telephone: ['']
  });

  onSubmit() {
    this.submitted = true;
    this.errorMessage = '';

    console.log('Form values:', this.registerForm.value);

    if (this.registerForm.invalid || this.isSubmitting) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;

    const formValue = this.registerForm.value;
    const registerData = {
      nom: formValue.nom,
      prenom: formValue.prenom,
      email: formValue.email,
      password: formValue.password,
      role: formValue.role || 'DEMANDEUR',
      telephone: formValue.telephone || null
    };

    console.log('Sending data:', registerData);

    this.http
      .post('http://localhost:8080/api/auth/register', registerData)
      .pipe(finalize(() => (this.isSubmitting = false)))
      .subscribe({
        next: (response: any) => {
          console.log('Registration success:', response);
          this.router.navigate(['/login']);
        },
        error: (err: { error: { message: string; }; }) => {
          console.error('Registration error:', err);
          this.errorMessage = err.error?.message || "Erreur lors de l'inscription.";
        }
      });
  }
}