import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-ticket-response',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <div class="response-container">
      <div class="response-card" *ngIf="!loading && !error">
        <div class="response-icon" [class.accepted]="interest === 'true'">
          {{ interest === 'true' ? '✅' : '❌' }}
        </div>
        <h2>{{ interest === 'true' ? 'Intérêt confirmé' : 'Participation refusée' }}</h2>
        <p>Vous avez {{ interest === 'true' ? 'accepté' : 'refusé' }} l'intervention pour le ticket</p>
        <h3>#{{ ticketId }}</h3>
        <div class="status" [class.success]="success" [class.error]="!success && submitted">
          <p *ngIf="!submitted">Enregistrement de votre réponse...</p>
          <p *ngIf="submitted && success">Votre réponse a été enregistrée avec succès !</p>
          <p *ngIf="submitted && !success">{{ errorMessage }}</p>
        </div>
        <div class="info-note" *ngIf="submitted && success && interest === 'true'">
          <p>L'administrateur vous contactera si vous êtes sélectionné pour cette intervention.</p>
        </div>
        <div class="actions" *ngIf="submitted">
          <button routerLink="/tickets" class="btn-primary">Voir mes tickets</button>
          <button routerLink="/dashboard" class="btn-secondary">Retour</button>
        </div>
      </div>
      <div class="response-card error-card" *ngIf="error && !loading">
        <h2>Erreur</h2>
        <p>{{ errorMessage }}</p>
        <div class="actions">
          <button routerLink="/tickets" class="btn-primary">Voir mes tickets</button>
          <button routerLink="/dashboard" class="btn-secondary">Retour</button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .response-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 100vh;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      padding: 20px;
    }
    .response-card {
      background: white;
      border-radius: 16px;
      padding: 40px;
      text-align: center;
      max-width: 500px;
      width: 100%;
      box-shadow: 0 20px 40px rgba(0,0,0,0.1);
    }
    .response-icon {
      font-size: 64px;
      margin-bottom: 20px;
    }
    .response-icon.accepted {
      color: #22c55e;
    }
    .status {
      margin: 20px 0;
      padding: 15px;
      border-radius: 8px;
    }
    .status.success {
      background: #dcfce7;
      color: #166534;
    }
    .status.error {
      background: #fee2e2;
      color: #991b1b;
    }
    .info-note {
      margin: 15px 0;
      padding: 12px;
      background: #fef3c7;
      border-radius: 8px;
      color: #92400e;
      font-size: 14px;
    }
    .actions {
      display: flex;
      gap: 10px;
      justify-content: center;
      margin-top: 20px;
    }
    .btn-primary, .btn-secondary {
      padding: 10px 20px;
      border-radius: 8px;
      text-decoration: none;
      cursor: pointer;
      border: none;
      font-size: 14px;
    }
    .btn-primary {
      background: #667eea;
      color: white;
    }
    .btn-secondary {
      background: #e2e8f0;
      color: #1e293b;
    }
  `]
})
export class TicketResponseComponent implements OnInit {
  ticketId: string = '';
  interest: string = '';
  loading = true;
  submitted = false;
  success = false;
  error = false;
  errorMessage = '';
  
  private apiUrl = 'http://localhost:8080/api';
  
  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private router: Router
  ) {}
  
  ngOnInit(): void {
    this.ticketId = this.route.snapshot.paramMap.get('id') || '';
    this.interest = this.route.snapshot.queryParamMap.get('interest') || '';
    
    const token = localStorage.getItem('token');
    if (!token) {
      this.loading = false;
      this.error = true;
      this.errorMessage = 'Veuillez vous connecter pour répondre à cette demande';
      return;
    }
    
    if (this.ticketId && this.interest) {
      this.submitResponse();
    } else {
      this.loading = false;
      this.error = true;
      this.errorMessage = 'Paramètres invalides';
    }
  }
  
  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }
  
  submitResponse(): void {
    const headers = this.getAuthHeaders();
    const request = {
      interested: this.interest === 'true'
    };
    
    this.http.post(`${this.apiUrl}/tickets/${this.ticketId}/interest`, request, { headers }).subscribe({
      next: () => {
        this.submitted = true;
        this.success = true;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error submitting response:', error);
        this.submitted = true;
        this.success = false;
        this.error = true;
        this.errorMessage = error.error?.message || 'Erreur lors de l\'enregistrement de votre réponse';
        this.loading = false;
      }
    });
  }
}