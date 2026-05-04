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
  ranking: TechnicianRank[] = [];
  topThree: TechnicianRank[] = [];
  loading = true;
  currentTechnician: TechnicianScore | null = null;
  userRole: string = '';
  errorMessage: string = '';
  searchTerm: string = '';

  private apiUrl = 'http://localhost:8080/api';

  constructor(
    private http: HttpClient,
    private router: Router,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.checkAuthentication();
  }

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
    // Assuming averageNote is 0-100
    const stars = Math.round(averageNote / 20);
    const clampedStars = Math.min(Math.max(stars, 0), 5);
    return '★'.repeat(clampedStars) + '☆'.repeat(5 - clampedStars);
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
  }

  getBadgeIcon(badgeName: string): string {
    if (!badgeName) return '🏆';
    const name = badgeName.toUpperCase();
    if (name.includes('MAÎTRE') || name.includes('MAITRE')) return '🥇';
    if (name.includes('SUPER')) return '👑';
    if (name.includes('EXPERT')) return '💎';
    if (name.includes('SENIOR')) return '⚙️';
    if (name.includes('JUNIOR')) return '🔧';
    if (name.includes('STAGIAIRE')) return '🟢';
    return '🏆';
  }

  getBadgeColor(badgeName: string): string {
    if (!badgeName) return '#6b7280';
    const name = badgeName.toUpperCase();
    if (name.includes('MAÎTRE') || name.includes('MAITRE')) return 'linear-gradient(135deg, #FFD700 0%, #FFA500 100%)';
    if (name.includes('SUPER')) return 'linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%)';
    if (name.includes('EXPERT')) return 'linear-gradient(135deg, #8b5cf6 0%, #6d28d9 100%)';
    if (name.includes('SENIOR')) return 'linear-gradient(135deg, #3b82f6 0%, #2563eb 100%)';
    if (name.includes('JUNIOR')) return 'linear-gradient(135deg, #10b981 0%, #059669 100%)';
    if (name.includes('STAGIAIRE')) return 'linear-gradient(135deg, #9ca3af 0%, #6b7280 100%)';
    return '#6b7280';
  }

  getInitials(nom: string, prenom: string): string {
    if (!nom || !prenom) return '??';
    return (prenom.charAt(0) + nom.charAt(0)).toUpperCase();
  }

  getProgressWidth(): string {
    if (!this.currentTechnician) return '0%';
    const total = this.currentTechnician.totalPoints;
    const pointsToNext = this.currentTechnician.pointsToNextBadge;
    if (pointsToNext <= 0) return '100%';
    const maxPoints = total + pointsToNext;
    const percentage = (total / maxPoints) * 100;
    return `${Math.min(percentage, 100)}%`;
  }

  retry(): void {
    this.checkAuthentication();
  }
}