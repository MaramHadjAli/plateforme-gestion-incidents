import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TechnicienDashboardService } from '../../../core/services/technicien-dashboard.service';
import { TechnicienDashboardDTO } from '../../../core/models/technicien-dashboard.model';

@Component({
  selector: 'app-technicien-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './technicien-dashboard.component.html',
  styleUrls: ['./technicien-dashboard.component.css', '../../../plate/admin/dashboard/admin-dashboard.component.css', '../ticket-list/ticket-list.component.css']
})
export class TechnicienDashboardComponent implements OnInit {
  dashboardData: TechnicienDashboardDTO | null = null;
  loading = true;
  errorMessage = '';

  readonly sidebarItems = [
    { label: 'Mon Dashboard', route: '/technicien/dashboard', icon: 'M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6' },
    { label: 'Mes Tickets', route: '/ticket-list', icon: 'M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2' },
    { label: 'Classement', route: '/classement', icon: 'M13 7h8m0 0v8m0-8l-8 8-4-4-6 6' },
  ];

  constructor(private dashboardService: TechnicienDashboardService) {}

  ngOnInit(): void {
    this.loadDashboard();
  }

  loadDashboard(): void {
    this.loading = true;
    this.dashboardService.getDashboard().subscribe({
      next: (data) => {
        this.dashboardData = data;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Erreur lors du chargement du dashboard.';
        this.loading = false;
      }
    });
  }

  getPriorityClass(priority: string): string {
    switch (priority) {
      case 'CRITIQUE': return 'badge badge--critical';
      case 'HAUTE': return 'badge badge--high';
      case 'NORMALE': return 'badge badge--medium';
      case 'FAIBLE': return 'badge badge--low';
      default: return 'badge badge--neutral';
    }
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'OUVERT': return 'badge badge--open';
      case 'ASSIGNE': return 'badge badge--assigned';
      case 'EN_COURS': return 'badge badge--progress';
      case 'RESOLU': return 'badge badge--resolved';
      default: return 'badge badge--neutral';
    }
  }

  getPerformanceColor(note: number): string {
    if (note >= 4.5) return '#22c55e'; // Green
    if (note >= 3.5) return '#f59e0b'; // Yellow
    return '#ef4444'; // Red
  }
}
