import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-maintenance',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="tickets-page">
      <div class="page-header">
        <div class="header-left">
          <div class="header-eyebrow">
            <span class="eyebrow-dot"></span>
            <span>Planification</span>
          </div>
          <h1 class="page-title">Maintenance <span class="title-accent">Préventive</span></h1>
          <p class="page-subtitle">Suivez les maintenances planifiées pour vos équipements</p>
        </div>
      </div>

      <!-- Loading -->
      <div *ngIf="loading" style="text-align: center; padding: 60px;">
        <div style="font-size: 14px; color: var(--c-text-muted);">Chargement des maintenances...</div>
      </div>

      <!-- Maintenances list -->
      <ng-container *ngIf="!loading">
        <section *ngIf="maintenances.length > 0" class="card-panel" style="padding: 0; overflow: hidden;">
          <table style="width: 100%; border-collapse: collapse;">
            <thead>
              <tr style="border-bottom: 1px solid var(--c-border); background: var(--c-surface-raised, rgba(255,255,255,0.03));">
                <th style="padding: 14px 20px; text-align: left; font-size: 12px; text-transform: uppercase; letter-spacing: 0.05em; color: var(--c-text-muted); font-weight: 600;">Équipement</th>
                <th style="padding: 14px 20px; text-align: left; font-size: 12px; text-transform: uppercase; letter-spacing: 0.05em; color: var(--c-text-muted); font-weight: 600;">Date Programmée</th>
                <th style="padding: 14px 20px; text-align: left; font-size: 12px; text-transform: uppercase; letter-spacing: 0.05em; color: var(--c-text-muted); font-weight: 600;">Fréquence</th>
                <th style="padding: 14px 20px; text-align: left; font-size: 12px; text-transform: uppercase; letter-spacing: 0.05em; color: var(--c-text-muted); font-weight: 600;">Statut</th>
                <th style="padding: 14px 20px; text-align: left; font-size: 12px; text-transform: uppercase; letter-spacing: 0.05em; color: var(--c-text-muted); font-weight: 600;">Description</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let m of maintenances" style="border-bottom: 1px solid var(--c-border); transition: background 0.2s;"
                  onmouseenter="this.style.background='rgba(99,102,241,0.04)'" onmouseleave="this.style.background='transparent'">
                <td style="padding: 14px 20px; font-weight: 600; color: var(--c-text);">{{ m.nomEquipement || '—' }}</td>
                <td style="padding: 14px 20px; color: var(--c-text-muted);">{{ m.dateProgramme | date:'dd/MM/yyyy' }}</td>
                <td style="padding: 14px 20px;">
                  <span style="padding: 4px 10px; border-radius: 20px; font-size: 12px; font-weight: 600; background: rgba(99,102,241,0.1); color: #818cf8;">{{ m.frequence }}</span>
                </td>
                <td style="padding: 14px 20px;">
                  <span [style.background]="m.enRetard ? 'rgba(239,68,68,0.1)' : 'rgba(34,197,94,0.1)'"
                        [style.color]="m.enRetard ? '#ef4444' : '#22c55e'"
                        style="padding: 4px 10px; border-radius: 20px; font-size: 12px; font-weight: 600;">
                    {{ m.enRetard ? '⚠ En retard' : '✓ Planifiée' }}
                  </span>
                </td>
                <td style="padding: 14px 20px; color: var(--c-text-muted); font-size: 13px;">{{ m.description || '—' }}</td>
              </tr>
            </tbody>
          </table>
        </section>

        <section *ngIf="maintenances.length === 0" class="card-panel" style="padding: 80px 40px; text-align: center;">
          <svg width="64" height="64" fill="none" stroke="currentColor" stroke-width="1.5" viewBox="0 0 24 24" style="margin: 0 auto 24px; color: var(--c-blue);">
            <path stroke-linecap="round" stroke-linejoin="round" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"></path>
            <path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
          </svg>
          <h2 style="font-size: 22px; font-weight: 700; color: var(--c-text); margin-bottom: 12px;">Aucune maintenance planifiée</h2>
          <p style="color: var(--c-text-muted); font-size: 15px; max-width: 500px; margin: 0 auto;">
            Il n'y a pas de maintenance préventive planifiée pour le moment. Les nouvelles maintenances apparaîtront ici.
          </p>
        </section>
      </ng-container>
    </div>
  `,
  styleUrls: ['../ticket-list/ticket-list.component.css']
})
export class MaintenanceComponent implements OnInit {
  maintenances: any[] = [];
  loading = true;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.http.get<any[]>('http://localhost:8080/api/maintenance').subscribe({
      next: (data) => {
        this.maintenances = data;
        this.loading = false;
      },
      error: () => {
        this.maintenances = [];
        this.loading = false;
      }
    });
  }
}
