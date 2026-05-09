import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MaintenanceService, Maintenance } from '../../../core/services/maintenance.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-maintenance-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './maintenance-list.component.html',
  styleUrls: ['./maintenance-list.component.css']
})
export class MaintenanceListComponent implements OnInit {
  maintenances: Maintenance[] = [];
  loading = true;
  errorMessage = '';

  constructor(
    private maintenanceService: MaintenanceService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.loadMaintenances();
  }

  loadMaintenances(): void {
    this.loading = true;
    this.maintenanceService.getAll().subscribe({
      next: (data) => {
        this.maintenances = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading maintenances', err);
        this.errorMessage = 'Erreur lors du chargement des maintenances';
        this.loading = false;
      }
    });
  }

  updateStatus(m: Maintenance, status: string): void {
    this.maintenanceService.updateStatus(m.idMaintenance, status).subscribe({
      next: (updated) => {
        this.toastService.showSuccess('Statut mis à jour et prochaine maintenance planifiée');
        this.loadMaintenances();
      },
      error: (err) => {
        this.toastService.showError('Erreur lors de la mise à jour');
      }
    });
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'TERMINEE': return 'badge badge--success';
      case 'PLANIFIEE': return 'badge badge--info';
      case 'EN_COURS': return 'badge badge--warning';
      case 'ANNULEE': return 'badge badge--danger';
      default: return 'badge badge--neutral';
    }
  }
}
