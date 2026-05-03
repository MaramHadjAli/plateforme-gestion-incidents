import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule, HttpHeaders } from '@angular/common/http';
import { Router, RouterModule } from '@angular/router';

interface TechnicienResponseDTO {
  idUser: number;
  nom: string;
  prenom: string;
  email: string;
  telephone?: string;
  dateInscription: string;
  active: boolean;
  desactivationDate?: string;
}

interface TechnicienDesactivationDTO {
  desactivationReason: string;
}

interface NewTechnicienDTO {
  nom: string;
  prenom: string;
  email: string;
  telephone?: string;
  role: string;
}

@Component({
  selector: 'app-technicians-list',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule, RouterModule],
  templateUrl: './technicians-list.component.html',
  styleUrls: ['./technicians-list.component.css']
})
export class TechniciansListComponent implements OnInit {
  
  techniciens: TechnicienResponseDTO[] = [];
  filteredTechniciens: TechnicienResponseDTO[] = [];
  
  searchTerm: string = '';
  statusFilter: string = 'all';
  
  currentPage: number = 1;
  itemsPerPage: number = 10;
  
  selectedTechnicien: TechnicienResponseDTO | null = null;
  showDesactivateModal: boolean = false;
  selectedTechnicienToDesactivate: TechnicienResponseDTO | null = null;
  desactivationReason: string = '';
  
  showAddModal: boolean = false;
  newTechnicien: NewTechnicienDTO = {
    nom: '',
    prenom: '',
    email: '',
    telephone: '',
    role: 'TECHNICIEN'
  };
  
  userRole: string = 'ADMIN';
  Math = Math;
  
  stats = {
    total: 0,
    actifs: 0,
    inactifs: 0
  };
  
  private apiUrl = 'http://localhost:8080/api';
  
  constructor(private http: HttpClient, private router: Router) {}
  
  ngOnInit(): void {
    this.getUserRole();
    this.getAllTechniciens();
  }
  
  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }
  
  private redirectToLogin(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    this.router.navigate(['/login']);
  }
  
  getUserRole(): void {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    this.userRole = user.role || 'DEMANDEUR';
  }
  
  getAllTechniciens(): void {
    const headers = this.getAuthHeaders();
    this.http.get<TechnicienResponseDTO[]>(`${this.apiUrl}/admin/techniciens`, { headers }).subscribe({
      next: (data) => {
        this.techniciens = data;
        this.updateStats();
        this.applyFilters();
      },
      error: (error) => {
        console.error('Error fetching technicians:', error);
        if (error.status === 401) {
          this.redirectToLogin();
        }
      }
    });
  }
  
  updateStats(): void {
    this.stats.total = this.techniciens.length;
    this.stats.actifs = this.techniciens.filter(t => t.active).length;
    this.stats.inactifs = this.techniciens.filter(t => !t.active).length;
  }
  
  applyFilters(): void {
    let filtered = [...this.techniciens];
    
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(t => 
        t.nom.toLowerCase().includes(term) ||
        t.prenom.toLowerCase().includes(term) ||
        t.email.toLowerCase().includes(term)
      );
    }
    
    if (this.statusFilter === 'active') {
      filtered = filtered.filter(t => t.active);
    } else if (this.statusFilter === 'inactive') {
      filtered = filtered.filter(t => !t.active);
    }
    
    this.filteredTechniciens = filtered;
    this.currentPage = 1;
  }
  
  getActivityRate(): number {
    if (this.stats.total === 0) return 0;
    return Math.round((this.stats.actifs / this.stats.total) * 100);
  }
  
  getInitials(name: string): string {
    if (!name) return '?';
    const parts = name.trim().split(' ');
    if (parts.length === 1) return parts[0].charAt(0).toUpperCase();
    return (parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
  }
  
  formatDate(dateString: string | undefined): string {
    if (!dateString) return '—';
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    });
  }
  
  openModal(technicien: TechnicienResponseDTO): void {
    this.selectedTechnicien = technicien;
  }
  
  openDesactivateModal(technicien: TechnicienResponseDTO): void {
    this.selectedTechnicienToDesactivate = technicien;
    this.desactivationReason = '';
    this.showDesactivateModal = true;
  }
  
  closeDesactivateModal(): void {
    this.showDesactivateModal = false;
    this.selectedTechnicienToDesactivate = null;
    this.desactivationReason = '';
  }
  
  confirmDesactivate(): void {
    if (!this.desactivationReason.trim() || !this.selectedTechnicienToDesactivate) {
      return;
    }
    
    const dto: TechnicienDesactivationDTO = {
      desactivationReason: this.desactivationReason
    };
    
    const headers = this.getAuthHeaders();
    this.http.put(
      `${this.apiUrl}/admin/techniciens/${this.selectedTechnicienToDesactivate.idUser}/desactivate`,
      dto,
      { headers }
    ).subscribe({
      next: () => {
        this.getAllTechniciens();
        this.closeDesactivateModal();
      },
      error: (error) => {
        console.error('Error desactivating technician:', error);
        if (error.status === 401) {
          this.redirectToLogin();
        } else {
          alert('Erreur lors de la désactivation du technicien');
        }
        this.closeDesactivateModal();
      }
    });
  }
  
  reactivateTechnicien(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir réactiver ce technicien ?')) {
      const headers = this.getAuthHeaders();
      this.http.put(`${this.apiUrl}/admin/techniciens/${id}/reactivate`, {}, { headers }).subscribe({
        next: () => {
          this.getAllTechniciens();
        },
        error: (error) => {
          console.error('Error reactivating technician:', error);
          if (error.status === 401) {
            this.redirectToLogin();
          } else {
            alert('Erreur lors de la réactivation du technicien');
          }
        }
      });
    }
  }
  
  openAddModal(): void {
    this.resetForm();
    this.showAddModal = true;
  }
  
  closeAddModal(): void {
    this.showAddModal = false;
    this.resetForm();
  }
  
  resetForm(): void {
    this.newTechnicien = {
      nom: '',
      prenom: '',
      email: '',
      telephone: '',
      role: 'TECHNICIEN'
    };
  }
  
  isFormValid(): boolean {
    return this.newTechnicien.nom.trim() !== '' &&
           this.newTechnicien.prenom.trim() !== '' &&
           this.newTechnicien.email.trim() !== '' &&
           this.isValidEmail(this.newTechnicien.email);
  }
  
  isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }
  
  saveTechnicien(): void {
    if (!this.isFormValid()) {
      return;
    }
    
    const headers = this.getAuthHeaders();
    this.http.post(`${this.apiUrl}/admin/techniciens`, this.newTechnicien, { headers }).subscribe({
      next: () => {
        this.getAllTechniciens();
        this.closeAddModal();
      },
      error: (error) => {
        console.error('Error creating technician:', error);
        if (error.status === 401) {
          this.redirectToLogin();
        } else if (error.status === 409) {
          alert('Un technicien avec cet email existe déjà');
        } else {
          alert('Erreur lors de la création du technicien');
        }
      }
    });
  }
  
  get pages(): (number | string)[] {
    const totalPages = this.totalPages;
    const currentPage = this.currentPage;
    const pages: (number | string)[] = [];
    
    if (totalPages <= 7) {
      for (let i = 1; i <= totalPages; i++) {
        pages.push(i);
      }
    } else {
      pages.push(1);
      if (currentPage > 3) {
        pages.push('...');
      }
      const start = Math.max(2, currentPage - 1);
      const end = Math.min(totalPages - 1, currentPage + 1);
      for (let i = start; i <= end; i++) {
        pages.push(i);
      }
      if (currentPage < totalPages - 2) {
        pages.push('...');
      }
      pages.push(totalPages);
    }
    return pages;
  }
  
  get totalPages(): number {
    return Math.ceil(this.filteredTechniciens.length / this.itemsPerPage);
  }
  
  getPageButtonClass(page: number): string {
    return this.currentPage === page ? 'page-btn active' : 'page-btn';
  }
  
  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
    }
  }
  
  previousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }
  
  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
    }
  }
}