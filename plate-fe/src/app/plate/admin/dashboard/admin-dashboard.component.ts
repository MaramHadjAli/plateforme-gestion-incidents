import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AdminDashboardStats } from '../models/admin-dashboard.model';
import { AdminDashboardService } from '../services/admin-dashboard.service';

type SeriesItem = {
  label: string;
  value: number;
  color: string;
  cssClass: string;
};

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
  stats: AdminDashboardStats | null = null;
  loading = true;
  errorMessage = '';

  readonly sidebarItems = [
    {
      label: 'Dashboard',
      route: '/dashboard',
      icon: 'M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6'
    },
    {
      label: 'Gestion Incidents',
      route: '/ticket-list',
      icon: 'M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2'
    },
    {
      label: 'Nouveau Ticket',
      route: '/create-ticket',
      icon: 'M12 4v16m8-8H4'
    }
  ];

  readonly priorityLegend = [
    { key: 'CRITIQUE', label: 'Critique', color: '#dc2626', cssClass: 'is-critical' },
    { key: 'HAUTE', label: 'Élevée', color: '#fb923c', cssClass: 'is-high' },
    { key: 'NORMALE', label: 'Moyenne', color: '#eab308', cssClass: 'is-medium' },
    { key: 'FAIBLE', label: 'Faible', color: '#3b82f6', cssClass: 'is-low' }
  ];

  readonly statusLegend = [
    { key: 'OUVERT', label: 'Ouvert', color: '#2563eb', cssClass: 'is-open' },
    { key: 'ASSIGNE', label: 'Assigné', color: '#8b5cf6', cssClass: 'is-assigned' },
    { key: 'EN_COURS', label: 'En cours', color: '#f59e0b', cssClass: 'is-progress' },
    { key: 'RESOLU', label: 'Résolu', color: '#22c55e', cssClass: 'is-resolved' }
  ];

  constructor(private dashboardService: AdminDashboardService) {}

  ngOnInit(): void {
    this.loadStats();
  }

  loadStats(): void {
    this.loading = true;
    this.errorMessage = '';

    this.dashboardService.getStats().subscribe({
      next: (stats) => {
        this.stats = stats;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Impossible de charger les indicateurs administrateur.';
        this.loading = false;
      }
    });
  }

  get totalTickets(): number {
    return this.stats?.totalTickets ?? 0;
  }

  get urgentTickets(): number {
    return (this.stats?.ticketsByPriority?.['CRITIQUE'] ?? 0) + (this.stats?.ticketsByPriority?.['HAUTE'] ?? 0);
  }

  get resolvedRate(): number {
    if (!this.totalTickets) {
      return 0;
    }

    return Math.round(((this.stats?.resolvedTickets ?? 0) / this.totalTickets) * 100);
  }

  get prioritySeries(): SeriesItem[] {
    return this.priorityLegend.map((entry) => ({
      label: entry.label,
      value: this.stats?.ticketsByPriority?.[entry.key] ?? 0,
      color: entry.color,
      cssClass: entry.cssClass
    }));
  }

  get statusSeries(): SeriesItem[] {
    return this.statusLegend.map((entry) => ({
      label: entry.label,
      value: this.stats?.ticketsByStatus?.[entry.key] ?? 0,
      color: entry.color,
      cssClass: entry.cssClass
    }));
  }

  get priorityGradient(): string {
    const total = this.prioritySeries.reduce((sum, item) => sum + item.value, 0) || 1;
    let cursor = 0;

    return this.prioritySeries
      .map((item) => {
        const start = cursor;
        cursor += (item.value / total) * 100;
        return `${item.color} ${start.toFixed(2)}% ${cursor.toFixed(2)}%`;
      })
      .join(', ');
  }

  get recentTickets() {
    return this.stats?.recentTickets ?? [];
  }

  getKpis() {
    return [
      {
        label: 'Total incidents',
        value: this.totalTickets,
        note: `${this.stats?.openTickets ?? 0} ouverts`,
        accent: 'blue',
        icon: 'M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2'
      },
      {
        label: 'En cours',
        value: this.stats?.inProgressTickets ?? 0,
        note: `${this.resolvedRate}% déjà résolus`,
        accent: 'violet',
        icon: 'M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z'
      },
      {
        label: 'Urgents',
        value: this.urgentTickets,
        note: `${this.stats?.overdueTickets ?? 0} en retard`,
        accent: 'red',
        icon: 'M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z'
      },
      {
        label: 'Résolus',
        value: this.stats?.resolvedTickets ?? 0,
        note: `${this.stats?.totalTechnicians ?? 0} techniciens`,
        accent: 'green',
        icon: 'M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z'
      }
    ];
  }

  getPriorityClass(priority: string | null | undefined): string {
    switch (priority) {
      case 'CRITIQUE':
        return 'badge badge--critical';
      case 'HAUTE':
        return 'badge badge--high';
      case 'NORMALE':
        return 'badge badge--medium';
      case 'FAIBLE':
        return 'badge badge--low';
      default:
        return 'badge badge--neutral';
    }
  }

  getStatusClass(status: string | null | undefined): string {
    switch (status) {
      case 'OUVERT':
        return 'badge badge--open';
      case 'ASSIGNE':
        return 'badge badge--assigned';
      case 'EN_COURS':
        return 'badge badge--progress';
      case 'RESOLU':
        return 'badge badge--resolved';
      default:
        return 'badge badge--neutral';
    }
  }

  formatPriorityLabel(priority: string | null | undefined): string {
    switch (priority) {
      case 'CRITIQUE': return 'Critique';
      case 'HAUTE': return 'Élevée';
      case 'NORMALE': return 'Moyenne';
      case 'FAIBLE': return 'Faible';
      default: return '—';
    }
  }

  formatStatusLabel(status: string | null | undefined): string {
    switch (status) {
      case 'OUVERT': return 'Ouvert';
      case 'ASSIGNE': return 'Assigné';
      case 'EN_COURS': return 'En cours';
      case 'RESOLU': return 'Résolu';
      default: return '—';
    }
  }
}

