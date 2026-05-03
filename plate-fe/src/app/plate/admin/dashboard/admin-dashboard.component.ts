import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CinematicHybridChartComponent } from '../../../shared/components/cinematic-hybrid-chart.component';
import { AdminDashboardService, AdminDashboardStats } from '../../../core/services/admin-dashboard.service';


type SeriesItem = {
  label: string;
  value: number;
  color: string;
  cssClass: string;
};

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, CinematicHybridChartComponent],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css', '../../technicien/ticket-list/ticket-list.component.css']
})
export class AdminDashboardComponent implements OnInit {
  stats: AdminDashboardStats | null = null;
  loading = true;
  errorMessage = '';
  cinematicChartSegments: any[] = [];
  darkMode = true;





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
    },
    {
      label: 'Mon Profil',
      route: '/profile',
      icon: 'M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z'
    },
    {
      label: 'Salles',
      route: '/admin/salles',
      icon: 'M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4'
    },
    {
      label: 'Equipements',
      route: '/equipements',
      icon: 'M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z'
    },
    {
      label: 'Paramètres',
      route: '/settings',
      icon: 'M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z'
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
        this.updateCinematicChartData();
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

  onCinematicSegmentSelected(segment: any): void {
    console.log('Segment cinématique sélectionné:', segment);
  }

  onCinematicSegmentHovered(segment: any): void {
    // Optionnel: Logique au survol
  }

  private updateCinematicChartData(): void {
    if (!this.stats) return;

    this.cinematicChartSegments = this.prioritySeries.map(item => ({
      label: item.label,
      value: item.value,
      color: item.color,
      glowColor: this.adjustBrightness(item.color, 150)
    }));
  }

  private adjustBrightness(hexColor: string, percent: number): string {
    const num = parseInt(hexColor.slice(1), 16);
    const amt = Math.round(2.55 * percent);
    const R = (num >> 16) + amt;
    const G = (num >> 8 & 0x00FF) + amt;
    const B = (num & 0x0000FF) + amt;

    return '#' + (
      0x1000000 + (R < 255 ? R < 1 ? 0 : R : 255) * 0x10000 +
      (G < 255 ? G < 1 ? 0 : G : 255) * 0x100 +
      (B < 255 ? B < 1 ? 0 : B : 255)
    ).toString(16).slice(1);
  }
}
