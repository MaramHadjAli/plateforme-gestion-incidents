import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { EquipementService } from '../../../core/services/equipement.service';
import { SalleService } from '../../../core/services/salle.service';
import { Equipement } from '../../../core/models/equipement.model';
import { Salle } from '../../../core/models/salle.model';

@Component({
  selector: 'app-equipements',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './equipements.component.html',
  styleUrls: ['./equipements.component.css', '../dashboard/admin-dashboard.component.css', '../salles/salles.component.css', '../../technicien/ticket-list/ticket-list.component.css']
})
export class EquipementsComponent implements OnInit {
  equipements: Equipement[] = [];
  salles: Salle[] = [];
  loading = true;
  errorMessage = '';

  showModal = false;
  editingEquipement: Equipement | null = null;
  equipementForm: Partial<Equipement> = { 
    idEquipement: '', nomEquipement: '', type: '', modele: '', numSerie: '', etat: 'FONCTIONNELLE', idSalle: '' 
  };

  readonly sidebarItems = [
    { label: 'Dashboard', route: '/dashboard', icon: 'M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6' },
    { label: 'Gestion Incidents', route: '/ticket-list', icon: 'M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2' },
    { label: 'Salles', route: '/admin/salles', icon: 'M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4' },
    { label: 'Equipements', route: '/admin/equipements', icon: 'M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z' },
  ];

  constructor(
    private equipementService: EquipementService,
    private salleService: SalleService
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    this.salleService.getAll().subscribe(salles => {
      this.salles = salles;
      this.equipementService.getAll().subscribe({
        next: (data) => {
          this.equipements = data;
          this.loading = false;
        },
        error: () => {
          this.errorMessage = 'Erreur lors du chargement.';
          this.loading = false;
        }
      });
    });
  }

  getNomSalle(idSalle: string): string {
    return this.salles.find(s => s.idSalle === idSalle)?.nomSalle || 'Non assigné';
  }

  openModal(equipement?: Equipement): void {
    if (equipement) {
      this.editingEquipement = equipement;
      this.equipementForm = { ...equipement };
    } else {
      this.editingEquipement = null;
      this.equipementForm = { 
        idEquipement: '', nomEquipement: '', type: '', modele: '', numSerie: '', etat: 'FONCTIONNELLE', idSalle: '' 
      };
    }
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
    this.editingEquipement = null;
  }

  saveEquipement(): void {
    const dataToSave = this.equipementForm as Equipement;
    if (this.editingEquipement) {
      this.equipementService.update(dataToSave.idEquipement, dataToSave).subscribe({
        next: () => { this.loadData(); this.closeModal(); },
        error: () => alert('Erreur lors de la modification')
      });
    } else {
      this.equipementService.create(dataToSave).subscribe({
        next: () => { this.loadData(); this.closeModal(); },
        error: () => alert('Erreur lors de la création')
      });
    }
  }

  deleteEquipement(id: string): void {
    if (confirm('Voulez-vous vraiment supprimer cet équipement ?')) {
      this.equipementService.delete(id).subscribe({
        next: () => this.loadData(),
        error: () => alert('Erreur lors de la suppression')
      });
    }
  }

  getEtatClass(etat: string): string {
    switch (etat) {
      case 'FONCTIONNELLE': return 'badge badge--resolved';
      case 'EN_PANNE': return 'badge badge--critical';
      case 'EN_MAINTENANCE': return 'badge badge--progress';
      default: return 'badge badge--neutral';
    }
  }
}
