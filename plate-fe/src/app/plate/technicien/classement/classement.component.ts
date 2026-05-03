import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';

interface TechnicianRank {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  totalPoints: number;
  averageNote: number;
  rank: number;
  badge: string;
}

interface TechnicianScore {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  specialite: string;
  totalPoints: number;
  averageNote: number;
  rank: number;
  currentBadge: string;
  nextBadge: string;
  pointsToNextBadge: number;
  transactions: any[];
}

@Component({
  selector: 'app-classement',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './classement.component.html',
  styleUrls: ['./classement.component.css']
})
export class ClassementComponent implements OnInit {
  onSearchInput(event: any) {
    this.searchTerm = event.target.value;
  }

  ranking: TechnicianRank[] = [];
  topThree: TechnicianRank[] = [];
  restRanking: TechnicianRank[] = [];
  loading = true;
  currentTechnician: TechnicianScore | null = null;
  showMyRanking = false;
  userRole: string = '';
  errorMessage: string = '';

  private apiUrl = 'http://localhost:8080/api';
  searchTermControl: any;

  constructor(
    private http: HttpClient,
    private router: Router,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.checkAuthentication();
  }

  searchTerm: string = '';

  get filteredRanking(): TechnicianRank[] {
    if (!this.searchTerm) {
      return this.ranking;
    }
    const term = this.searchTerm.toLowerCase();
    return this.ranking.filter(tech =>
      tech.nom.toLowerCase().includes(term) ||
      tech.prenom.toLowerCase().includes(term) ||
      tech.email.toLowerCase().includes(term)
    );
  }

  getStarRating(averageNote: number): string {
    const stars = Math.round(averageNote / 20);
    return '★'.repeat(stars) + '☆'.repeat(5 - stars);
  }

  checkAuthentication(): void {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }

    this.userRole = this.authService.getUserRole();

    if (this.userRole === 'ADMIN') {
      this.loadRanking();
    } else if (this.userRole === 'TECHNICIEN' || this.userRole === 'TECHNICIAN') {
      this.loadCurrentTechnicianScore();
    } else {
      this.loading = false;
    }
  }

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    if (!token) {
      throw new Error('No token found');
    }
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  loadRanking(): void {
    this.loading = true;
    this.errorMessage = '';

    this.http.get<TechnicianRank[]>(`${this.apiUrl}/technicien/ranking`).subscribe({
      next: (data) => {
        this.ranking = data || [];
        this.topThree = this.ranking.slice(0, 3);
        this.restRanking = this.ranking.slice(3);
        this.loading = false;
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error loading ranking:', error);
        this.loading = false;
        this.errorMessage = 'Erreur lors du chargement du classement';
      }
    });
  }

  loadCurrentTechnicianScore(): void {
    this.loading = true;

    try {
      const headers = this.getAuthHeaders();

      this.http.get<TechnicianScore>(`${this.apiUrl}/technicien/my-score-details`, { headers }).subscribe({
        next: (data) => {
          this.currentTechnician = data;
          this.loading = false;
        },
        error: (error) => {
          console.error('Error loading technician score:', error);
          this.loading = false;
          this.errorMessage = 'Erreur lors du chargement de vos performances';
        }
      });
    } catch (error) {
      console.error('Error in loadCurrentTechnicianScore:', error);
      this.loading = false;
      this.errorMessage = 'Erreur de connexion au serveur';
    }
  }

  getBadgeIcon(badgeName: string): string {
    if (!badgeName) return '🏆';
    if (badgeName.includes('SUPER')) return '👑';
    if (badgeName.includes('EXPERT')) return '💎';
    if (badgeName.includes('SENIOR')) return '⚙️';
    if (badgeName.includes('JUNIOR')) return '🔧';
    if (badgeName.includes('STAGIAIRE')) return '🟢';
    return '🏆';
  }

  getBadgeColor(badgeName: string): string {
    if (!badgeName) return '#6b7280';
    if (badgeName.includes('SUPER')) return '#fbbf24';
    if (badgeName.includes('EXPERT')) return '#8b5cf6';
    if (badgeName.includes('SENIOR')) return '#3b82f6';
    if (badgeName.includes('JUNIOR')) return '#10b981';
    return '#6b7280';
  }

  getInitials(nom: string, prenom: string): string {
    if (!nom || !prenom) return '??';
    return (prenom.charAt(0) + nom.charAt(0)).toUpperCase();
  }

  toggleMyRanking(): void {
    this.showMyRanking = !this.showMyRanking;
  }

  getProgressWidth(): string {
    if (!this.currentTechnician) return '0%';
    const total = this.currentTechnician.totalPoints;
    const pointsToNext = this.currentTechnician.pointsToNextBadge;
    if (pointsToNext <= 0) return '100%';
    const maxPoints = total + pointsToNext;
    if (maxPoints <= 0) return '0%';
    const percentage = (total / maxPoints) * 100;
    return `${Math.min(percentage, 100)}%`;
  }

  retry(): void {
    if (this.userRole === 'ADMIN') {
      this.loadRanking();
    } else if (this.userRole === 'TECHNICIEN') {
      this.loadCurrentTechnicianScore();
    }
  }
}