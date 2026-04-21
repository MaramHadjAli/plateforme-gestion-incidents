import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TicketResponse } from '../models/ticket-response.model';
import { TicketRequest } from '../models/ticket-request.model';

@Injectable({
  providedIn: 'root'
})
export class TicketsService {

  private apiUrl = 'http://localhost:8080/api/tickets';

  constructor(private http: HttpClient) {}

  getAllTickets(): Observable<TicketResponse[]> {
    return this.http.get<TicketResponse[]>(this.apiUrl);
  }

  createTicket(ticket: TicketRequest): Observable<TicketResponse> {
    return this.http.post<TicketResponse>(this.apiUrl, ticket);
  }

  /**
   * Génère et ouvre dans le navigateur le PDF Demande de Prix
   * pour un ticket spécifique (panne signalée → appel d'offres).
   * @param ticketId  l'id du ticket
   * @param dpNumber  numéro du DP (ex: 5 → DP-05/2026), défaut = 5
   */
  downloadDemandePrixPdf(ticketId: string, dpNumber: number = 5): void {
    this.http
      .get(`${this.apiUrl}/${ticketId}/demande-prix/pdf?dpNumber=${dpNumber}`, {
        responseType: 'blob'
      })
      .subscribe({
        next: (blob) => {
          const url = URL.createObjectURL(blob);
          window.open(url, '_blank');          // ouvre le PDF dans un nouvel onglet
          setTimeout(() => URL.revokeObjectURL(url), 10_000); // libère la mémoire
        },
        error: (err) => console.error('Erreur génération PDF:', err)
      });
  }

  /**
   * Génère le PDF pour une sélection de tickets.
   * @param ticketIds liste des IDs
   * @param dpNumber  numéro du DP
   */
  downloadDemandePrixPdfSelection(ticketIds: string[], dpNumber: number = 5): void {
    this.http
      .post(
        `${this.apiUrl}/demande-prix/pdf`,
        { ticketIds, dpNumber },
        { responseType: 'blob' }
      )
      .subscribe({
        next: (blob) => {
          const url = URL.createObjectURL(blob);
          window.open(url, '_blank');
          setTimeout(() => URL.revokeObjectURL(url), 10_000);
        },
        error: (err) => console.error('Erreur génération PDF:', err)
      });
  }
}