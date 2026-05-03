import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule, RouterModule],
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent implements OnInit {
  
  token: string = '';
  newPassword: string = '';
  confirmPassword: string = '';
  isValidToken: boolean = false;
  success: boolean = false;
  loading: boolean = true;
  debugInfo: string = '';
  
  private apiUrl = 'http://localhost:8080/api';
  
  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private router: Router
  ) {}
  
  ngOnInit(): void {
    this.token = this.route.snapshot.queryParams['token'];
    this.debugInfo = `Token from URL: ${this.token}`;
    console.log(this.debugInfo);
    
    if (!this.token) {
      this.isValidToken = false;
      this.loading = false;
      this.debugInfo += '\nNo token found';
    } else {
      this.verifyToken();
    }
  }
  
  verifyToken(): void {
    this.debugInfo += '\nVerifying token...';
    console.log('Verifying token:', this.token);
    
    this.http.get(`${this.apiUrl}/auth/verify-reset-token?token=${this.token}`).subscribe({
      next: (response) => {
        console.log('Token valid response:', response);
        this.debugInfo += '\nToken is valid!';
        this.isValidToken = true;
        this.loading = false;
      },
      error: (error) => {
        console.error('Token invalid error:', error);
        this.debugInfo += `\nToken error: ${error.status}`;
        this.isValidToken = false;
        this.loading = false;
      }
    });
  }
  
  isPasswordValid(): boolean {
    const valid = this.newPassword.length >= 6 && 
           this.newPassword === this.confirmPassword;
    console.log('Password valid:', valid);
    return valid;
  }
  
  resetPassword(): void {
    console.log('Reset password called');
    if (!this.isPasswordValid()) {
      alert('Les mots de passe ne correspondent pas ou sont trop courts');
      return;
    }
    
    const request = {
      token: this.token,
      newPassword: this.newPassword,
      confirmPassword: this.confirmPassword
    };
    
    console.log('Resetting password with request:', request);
    this.http.post(`${this.apiUrl}/auth/reset-password`, request).subscribe({
      next: (response) => {
        console.log('Password reset success:', response);
        this.success = true;
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 3000);
      },
      error: (error) => {
        console.error('Error resetting password:', error);
        alert('Erreur lors de la réinitialisation du mot de passe: ' + (error.error?.message || error.message));
      }
    });
  }
}