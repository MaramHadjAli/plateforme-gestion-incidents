import { Component, HostListener, OnInit, ElementRef, ViewChild, Renderer2 } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NavLink } from '../../core/models/nav-link.model';
import { ProfileMenuItem } from '../../core/models/profile-menu-item.model';
import { RouterModule, Router } from '@angular/router';

interface Notification {
  id: number;
  type: 'critical' | 'assign' | 'sla' | 'badge' | 'status' | 'maintenance';
  title: string;
  message: string;
  time: string;
  unread: boolean;
  ticketId?: string;
}

import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-bar',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './app-bar.component.html',
  styleUrls: ['./app-bar.component.css']
})
export class AppBarComponent implements OnInit {
  activeNav = 'Dashboard';
  notifOpen = false;
  profileOpen = false;
  searchOpen = false;
  mobileOpen = false;
  searchQuery = '';
  searchSuggestions: string[] = [];
  
  userName = 'Mohamed Amine O.';
  userInitials = 'MA';
  userEmail = 'm.ounelli@enicarthage.tn';
  userRole: 'Administrateur' | 'Technicien' | 'Utilisateur' = 'Technicien';
  userPoints = 680;
  pointsPercent = 68;

  navLinks: NavLink[] = [];

  notifications: Notification[] = [
    { id: 1, type: 'critical', title: 'Incident critique', message: 'Ticket #042 — Panne critique en Salle B12', time: '2 min', unread: true, ticketId: '042' },
    { id: 2, type: 'assign', title: 'Ticket assigné', message: 'Ticket #039 vous a été assigné', time: '15 min', unread: true, ticketId: '039' },
    { id: 3, type: 'sla', title: 'SLA dépassé', message: 'SLA dépassé pour Ticket #037', time: '1h', unread: true, ticketId: '037' },
    { id: 4, type: 'badge', title: 'Nouveau badge', message: 'Badge « Rapide » obtenu !', time: '3h', unread: false },
    { id: 5, type: 'status', title: 'Statut mis à jour', message: 'Ticket #031 — Statut changé : Résolu', time: '5h', unread: false, ticketId: '031' }
  ];

  profileMenuItems: ProfileMenuItem[] = [
    { label: 'Mon Profil', route: '/profile', icon: '' },
    { label: 'Mes Tickets', route: '/my-tickets', icon: '' },
    { label: 'Mes Badges', route: '/badges', icon: '' },
    { label: 'Paramètres', route: '/settings', icon: '' }
  ];

  typeColors: Record<Notification['type'], string> = {
    critical: 'bg-red-500',
    assign: 'bg-blue-500',
    sla: 'bg-amber-500',
    badge: 'bg-purple-500',
    status: 'bg-emerald-500',
    maintenance: 'bg-cyan-500'
  };

  typeIcons: Record<Notification['type'], string> = {
    critical: '⚠️',
    assign: '📌',
    sla: '⏱️',
    badge: '🏅',
    status: '✅',
    maintenance: '🛠️'
  };

  private searchTerms = ['Ticket #042', 'Salle B12', 'Projecteur Sony', 'Technicien Ounelli', 'Rapport mensuel'];

  constructor(private elRef: ElementRef, private router: Router, private authService: AuthService) {}

  ngOnInit(): void {
    const role = this.authService.getUserRoleFromToken();
    if (role === 'ADMIN') {
      this.userRole = 'Administrateur';
      this.navLinks = [
        { label: 'Dashboard', route: '/dashboard', icon: '' },
        { label: 'Tickets', route: '/ticket-list', icon: '' },
        { label: 'Salles', route: '/admin/salles', icon: '' },
        { label: 'Équipements', route: '/admin/equipements', icon: '' },
        { label: 'Techniciens', route: '/technicians', icon: '' }
      ];
    } else if (role === 'TECHNICIEN') {
      this.userRole = 'Technicien';
      this.navLinks = [
        { label: 'Mon Dashboard', route: '/technicien/dashboard', icon: '' },
        { label: 'Mes Tickets', route: '/ticket-list', icon: '' },
        { label: 'Maintenance', route: '/maintenance', icon: '' },
        { label: 'Classement', route: '/classement', icon: '' }
      ];
    } else {
      this.userRole = 'Utilisateur';
      this.navLinks = [
        { label: 'Nouveau Ticket', route: '/create-ticket', icon: '' },
        { label: 'Mes Tickets', route: '/ticket-list', icon: '' }
      ];
    }
    
    const user = this.authService.getUserData();
    if (user && Object.keys(user).length > 0) {
      this.userName = user.nom ? `${user.prenom || ''} ${user.nom}`.trim() : (user.email || 'Utilisateur');
      this.userEmail = user.email || '';
      const initials = ((user.prenom?.[0] || user.nom?.[0] || '') + (user.nom?.[1] || '')).toUpperCase();
      this.userInitials = initials || 'U';
    }
  }

  get unreadCount(): number {
    return this.notifications.filter((n) => n.unread).length;
  }


  toggleNotif(): void {
    this.notifOpen = !this.notifOpen;
    if (this.profileOpen) this.profileOpen = false;
  }

  toggleProfile(): void {
    this.profileOpen = !this.profileOpen;
    if (this.notifOpen) this.notifOpen = false;
  }

  closeSearch(): void {
    this.searchOpen = false;
    this.searchQuery = '';
  }

  markAllRead(): void {
    this.notifications = this.notifications.map((n) => ({ ...n, unread: false }));
  }

  markRead(id: number): void {
    this.notifications = this.notifications.map((n) => (n.id === id ? { ...n, unread: false } : n));
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  openNotification(notification: Notification): void {
    this.markRead(notification.id);

    if (notification.ticketId) {
      this.router.navigate(['/ticket-list'], { queryParams: { ticket: notification.ticketId } });
    }

    this.notifOpen = false;
  }

  trackNotification(index: number, notification: Notification): number {
    return notification.id;
  }

  onSearchInput(event: any): void {
    const query = event.target.value;
    if (query.length > 2) {
      this.searchSuggestions = [
        `Ticket: ${query}`,
        `Salle: ${query}`,
        `Équipement: ${query}`,
        `Utilisateur: ${query}`
      ];
    } else {
      this.searchSuggestions = [];
    }
  }

  selectSuggestion(suggestion: string): void {
    this.searchQuery = suggestion;
    this.closeSearch();
    console.log('Searching for:', suggestion);
  }



  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;

    const profileContainer = this.elRef.nativeElement.querySelector('[data-profile]');
    const notifContainer = this.elRef.nativeElement.querySelector('[data-notif]');
    const searchContainer = this.elRef.nativeElement.querySelector('[data-search]');

    if (profileContainer && !profileContainer.contains(target)) {
      this.profileOpen = false;
    }

    if (notifContainer && !notifContainer.contains(target)) {
      this.notifOpen = false;
    }

    if (searchContainer && !searchContainer.contains(target)) {
      this.searchOpen = false;
      this.searchQuery = '';
    }
  }
}
