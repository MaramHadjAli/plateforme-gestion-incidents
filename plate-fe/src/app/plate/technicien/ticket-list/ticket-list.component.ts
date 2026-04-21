import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Ticket } from '../../../core/models/ticket.model';
import { RouterModule, RouterLink } from '@angular/router';
import { TicketsService } from '../../../core/services/tickets.service';

@Component({
  selector: 'app-ticket-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, RouterLink],
  templateUrl: './ticket-list.component.html',
  styleUrl: './ticket-list.component.css'
})
export class TicketListComponent implements OnInit {

  searchTerm: string = '';
  selectedTicket: Ticket | null = null;
  currentPage: number = 1;
  pageSize: number = 10;
  totalPages: number = 0;
  pages: (number | string)[] = [];
  pdfLoading: boolean = false;  // spinner pendant la génération

  tickets: Ticket[] = [];

  constructor(private ticketsService: TicketsService) {}

  ngOnInit(): void {
    this.loadTickets();
  }

  loadTickets(): void {
    this.ticketsService.getAllTickets().subscribe({
      next: (data) => {
        this.tickets = data.map(t => ({
          id: t.idTicket,
          titre: t.titre,
          description: t.description,
          priorite: this.mapBackendPriority(t.priorite),
          statut: this.mapBackendStatus(t.status),
          dateCreation: t.dateCreation ? new Date(t.dateCreation).toISOString().split('T')[0] : '',
          dateLimite: t.dateLimite ? new Date(t.dateLimite).toISOString().split('T')[0] : '',
          dateCloture: null,
          technicien: t.demandeurNom || 'À assigner'
        }));
        this.updatePages();
      },
      error: (err) => console.error('Error fetching tickets:', err)
    });
  }

  generateAppelOffre(): void {
    if (!this.selectedTicket) return;
    this.pdfLoading = true;
    this.ticketsService.downloadDemandePrixPdf(
      this.selectedTicket.id,
      5 
    );
    setTimeout(() => {
      this.pdfLoading = false;
      this.selectedTicket = null;
    }, 1500);
  }


  private mapBackendPriority(priorite: string): string {
    const map: { [key: string]: string } = {
      'CRITIQUE': 'Critique',
      'HAUTE': 'Élevée',
      'NORMAL': 'Moyenne',
      'FAIBLE': 'Basse'
    };
    return map[priorite] || 'Moyenne';
  }

  private mapBackendStatus(status: string): string {
    const map: { [key: string]: string } = {
      'OUVERT': 'Ouvert',
      'EN_COURS': 'En cours',
      'RESOLU': 'Résolu',
      'FERME': 'Fermé'
    };
    return map[status] || 'Ouvert';
  }

  get filteredTickets(): Ticket[] {
    if (!this.searchTerm) return this.tickets;
    return this.tickets.filter(ticket =>
      ticket.id.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
      ticket.titre.toLowerCase().includes(this.searchTerm.toLowerCase())
    );
  }

  get stats() {
    return {
      total: this.tickets.length,
      enCours: this.tickets.filter(t => t.statut === 'En cours').length,
      urgents: this.tickets.filter(t => t.priorite === 'Critique' || t.priorite === 'Élevée').length,
      resolus: this.tickets.filter(t => t.statut === 'Résolu').length
    };
  }

  getPriorityClass(priority: string): string {
    const classes: { [key: string]: string } = {
      'Critique': 'Critique', 'Élevée': 'Élevée', 'Moyenne': 'Moyenne', 'Basse': 'Basse'
    };
    return classes[priority] || 'Moyenne';
  }

  getPriorityDotClass(priority: string): string {
    const classes: { [key: string]: string } = {
      'Basse': 'bg-green-500', 'Moyenne': 'bg-yellow-500',
      'Élevée': 'bg-orange-500', 'Critique': 'bg-red-500'
    };
    return classes[priority] || 'bg-gray-500';
  }

  getStatusClass(status: string): string {
    const classes: { [key: string]: string } = {
      'En cours': 'En cours', 'Assigné': 'Assigné', 'Résolu': 'Résolu', 'Ouvert': 'Ouvert'
    };
    return classes[status] || 'Ouvert';
  }

  getStatusDotClass(status: string): string {
    const classes: { [key: string]: string } = {
      'En cours': 'bg-yellow-500', 'Assigné': 'bg-blue-500',
      'Résolu': 'bg-green-500', 'Ouvert': 'bg-gray-500'
    };
    return classes[status] || 'bg-gray-500';
  }

  getDateLimitClass(dateLimite: string): string {
    if (!dateLimite) return '';
    const today = new Date();
    const limit = new Date(dateLimite);
    const diffDays = Math.ceil((limit.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));
    if (diffDays < 0) return 'text-red-600';
    if (diffDays <= 2) return 'text-orange-600';
    return '';
  }

  getProgressPercentage(): string {
    if (this.stats.total === 0) return '0%';
    return `${Math.round((this.stats.enCours / this.stats.total) * 100)}%`;
  }

  getResolutionRate(): number {
    if (this.stats.total === 0) return 0;
    return Math.round((this.stats.resolus / this.stats.total) * 100);
  }

  getInitials(name: string | undefined): string {
    if (!name) return 'NA';
    return name.split(' ').map(n => n[0]).join('').toUpperCase();
  }

  getPageButtonClass(page: number): string {
    return this.currentPage === page ? 'px-4 text-white' : 'px-4';
  }

  previousPage(): void {
    if (this.currentPage > 1) { this.currentPage--; this.updatePages(); }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) { this.currentPage++; this.updatePages(); }
  }

  goToPage(page: number): void {
    this.currentPage = page;
    this.updatePages();
  }

  updatePages(): void {
    this.totalPages = Math.ceil(this.filteredTickets.length / this.pageSize);
    this.pages = this.generatePageArray(this.currentPage, this.totalPages);
  }

  generatePageArray(currentPage: number, totalPages: number): (number | string)[] {
    const pages: (number | string)[] = [];
    const maxVisible = 5;
    if (totalPages <= maxVisible) {
      for (let i = 1; i <= totalPages; i++) pages.push(i);
    } else if (currentPage <= 3) {
      pages.push(1, 2, 3, 4, '...', totalPages);
    } else if (currentPage >= totalPages - 2) {
      pages.push(1, '...', totalPages - 3, totalPages - 2, totalPages - 1, totalPages);
    } else {
      pages.push(1, '...', currentPage - 1, currentPage, currentPage + 1, '...', totalPages);
    }
    return pages;
  }

  openModal(ticket: Ticket): void {
    this.selectedTicket = ticket;
  }
}