import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { DashboardService, AdminDashboardStats } from '../../core/services/dashboard.service';
import { AuthService } from '../../core/services/auth.service';

import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  standalone: true,
  imports: [CommonModule]
})
export class HomeComponent implements OnInit {
  stats?: AdminDashboardStats;
  isAdmin = false;

  constructor(
    private router: Router,
    private dashboardService: DashboardService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.isAdmin = this.authService.isAdmin();
    if (this.isAdmin) {
      this.dashboardService.getStats().subscribe({
        next: (data) => this.stats = data,
        error: (err) => console.error('Error fetching home stats', err)
      });
    }
  }

  navigateToLogin(): void {
    this.router.navigate(['/login']);
  }

  navigateToRegister(): void {
    this.router.navigate(['/register']);
  }

  scrollToFeatures(): void {
    const element = document.getElementById('features');
    if (element) {
      element.scrollIntoView({ behavior: 'smooth' });
    }
  }
}

