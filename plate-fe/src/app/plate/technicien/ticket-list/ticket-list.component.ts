import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { initFlowbite } from 'flowbite';
import { Ticket } from '../../../core/models/ticket.model';

@Component({
  selector: 'app-ticket-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ticket-list.component.html',
  styleUrl: './ticket-list.component.css'
})
export class TicketListComponent {
  searchTerm: string = '';
  selectedTicket: Ticket | null = null;

  tickets: Ticket[] = [
    {
      id: 'TICK-001',
      titre: 'Imprimante hors service',
      description: "L'imprimante du bureau 204 ne répond plus, impossible d'imprimer des documents depuis ce matin. L'écran affiche une erreur de bourrage papier mais aucun papier bloqué détecté.",
      priorite: 'Élevée',
      statut: 'En cours',
      dateCreation: '2025-03-20',
      dateLimite: '2025-03-27',
      dateCloture: null,
      technicien: 'Karim Benali'
    },
    {
      id: 'TICK-002',
      titre: 'Connexion VPN impossible',
      description: "Impossible de se connecter au VPN de l'entreprise depuis mon ordinateur portable. Le message d'erreur indique 'Authentification échouée' alors que mes identifiants sont corrects.",
      priorite: 'Critique',
      statut: 'Assigné',
      dateCreation: '2025-03-21',
      dateLimite: '2025-03-24',
      dateCloture: null,
      technicien: 'Sophia El Mansouri'
    },
    {
      id: 'TICK-003',
      titre: 'Problème de son sur PC',
      description: 'Après la mise à jour Windows, le son ne fonctionne plus sur mon poste de travail. Les haut-parleurs sont détectés mais aucun son ne sort.',
      priorite: 'Moyenne',
      statut: 'Résolu',
      dateCreation: '2025-03-18',
      dateLimite: '2025-03-25',
      dateCloture: '2025-03-23',
      technicien: 'Mehdi Laaroussi'
    },
    {
      id: 'TICK-004',
      titre: 'Logiciel ERP ne démarre pas',
      description: "Le logiciel de gestion ERP affiche une erreur au lancement : 'Database connection failed'. Impossible d'accéder aux données clients depuis ce matin.",
      priorite: 'Élevée',
      statut: 'En cours',
      dateCreation: '2025-03-22',
      dateLimite: '2025-03-29',
      dateCloture: null,
      technicien: 'Nadia Tazi'
    },
    {
      id: 'TICK-005',
      titre: 'Demande de nouvel équipement',
      description: 'Besoin d\'un nouvel ordinateur portable pour le nouveau stagiaire qui arrive la semaine prochaine. Configuration standard avec 16Go RAM et SSD 512Go.',
      priorite: 'Basse',
      statut: 'Ouvert',
      dateCreation: '2025-03-23',
      dateLimite: '2025-04-06',
      dateCloture: null,
      technicien: 'À assigner'
    },
    {
      id: 'TICK-006',
      titre: "Problème d'accès au serveur",
      description: 'Plusieurs utilisateurs signalent ne pas pouvoir accéder au serveur partagé Z:. Le dossier de travail est inaccessible depuis 14h.',
      priorite: 'Critique',
      statut: 'En cours',
      dateCreation: '2025-03-21',
      dateLimite: '2025-03-26',
      dateCloture: null,
      technicien: 'Rachid El Fassi'
    }
  ];

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
    const classes = {
      'Critique': 'px-2 py-1 text-xs font-semibold rounded-full bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400',
      'Élevée': 'px-2 py-1 text-xs font-semibold rounded-full bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400',
      'Moyenne': 'px-2 py-1 text-xs font-semibold rounded-full bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400',
      'Basse': 'px-2 py-1 text-xs font-semibold rounded-full bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400'
    };
    return classes[priority as keyof typeof classes] || classes['Moyenne'];
  }

  getStatusClass(status: string): string {
    const classes = {
      'En cours': 'px-2 py-1 text-xs font-semibold rounded-full bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400',
      'Assigné': 'px-2 py-1 text-xs font-semibold rounded-full bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-400',
      'Résolu': 'px-2 py-1 text-xs font-semibold rounded-full bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400',
      'Ouvert': 'px-2 py-1 text-xs font-semibold rounded-full bg-purple-100 text-purple-800 dark:bg-purple-900/30 dark:text-purple-400'
    };
    return classes[status as keyof typeof classes] || classes['Ouvert'];
  }

  openModal(ticket: Ticket) {
    this.selectedTicket = ticket;
    setTimeout(() => {
      if (typeof initFlowbite === 'function') {
        
      }
    }, 0);
  }
}
