import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { trigger, transition, style, animate } from '@angular/animations';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  animations: [
    trigger('fadeSlide', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(10px)' }),
        animate('400ms cubic-bezier(0.16, 1, 0.3, 1)', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ])
  ]
})
export class LoginComponent {
  loginForm: FormGroup;
  isLoading = false;
  errorMsg = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  onSubmit() {
    if (this.loginForm.invalid) return;
    this.isLoading = true;
    this.errorMsg = '';

    this.authService.login(this.loginForm.value).subscribe({
      next: () => {
        const role = this.authService.getUserRole();
        if (role === 'ADMIN') this.router.navigate(['/admin']);
        else if (role === 'TECHNICIAN') this.router.navigate(['/technician']);
        else this.router.navigate(['/tickets/new']);
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMsg = err.error?.message || 'Authentication failed.';
      }
    });
  }
}
