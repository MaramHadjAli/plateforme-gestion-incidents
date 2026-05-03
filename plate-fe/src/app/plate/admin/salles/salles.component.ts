import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { SalleService } from '../../../core/services/salle.service';
import { Salle } from '../../../core/models/salle.model';

@Component({
  selector: 'app-salles',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './salles.component.html',
  styleUrls: ['./salles.component.css', '../dashboard/admin-dashboard.component.css', '../../technicien/ticket-list/ticket-list.component.css']
})
export class SallesComponent implements OnInit {
  salles: Salle[] = [];
  loading = true;
  errorMessage = '';

  showModal = false;
  editingSalle: Salle | null = null;
  salleForm: Salle = { idSalle: '', nomSalle: '', etage: '', batiment: '' };

  readonly sidebarItems = [
    { label: 'Dashboard', route: '/dashboard', icon: 'M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6' },
    { label: 'Gestion Incidents', route: '/ticket-list', icon: 'M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2' },
    { label: 'Salles', route: '/admin/salles', icon: 'M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4' },
    { label: 'Equipements', route: '/admin/equipements', icon: 'M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z' },
  ];

  constructor(private salleService: SalleService) {}

  ngOnInit(): void {
    this.loadSalles();
  }

  loadSalles(): void {
    this.loading = true;
    this.salleService.getAll().subscribe({
      next: (data) => {
        this.salles = data;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Erreur lors du chargement des salles.';
        this.loading = false;
      }
    });
  }

  openModal(salle?: Salle): void {
    if (salle) {
      this.editingSalle = salle;
      this.salleForm = { ...salle };
    } else {
      this.editingSalle = null;
      this.salleForm = { idSalle: '', nomSalle: '', etage: '', batiment: '' };
    }
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
    this.editingSalle = null;
  }

  saveSalle(): void {
    if (this.editingSalle) {
      this.salleService.update(this.salleForm.idSalle, this.salleForm).subscribe({
        next: () => {
          this.loadSalles();
          this.closeModal();
        },
        error: () => {
          alert('Erreur lors de la modification');
        }
      });
    } else {
      this.salleService.create(this.salleForm).subscribe({
        next: () => {
          this.loadSalles();
          this.closeModal();
        },
        error: () => {
          alert('Erreur lors de la création');
        }
      });
    }
  }

  deleteSalle(id: string): void {
    if (confirm('Voulez-vous vraiment supprimer cette salle ?')) {
      this.salleService.delete(id).subscribe({
        next: () => this.loadSalles(),
        error: () => alert('Erreur lors de la suppression')
      });
    }
  }
}
