import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TicketResponse } from '../models/ticket-response.model';
import { TicketRequest } from '../models/ticket-request.model';

@Injectable({
  providedIn: 'root'
})
export class TicketsService {

  private apiUrl = 'http://localhost:8080/api/tickets';

  constructor(private http: HttpClient) { }

  getAllTickets(): Observable<TicketResponse[]> {
    return this.http.get<TicketResponse[]>(this.apiUrl);
  }

  getMyTickets(): Observable<TicketResponse[]> {
    return this.http.get<TicketResponse[]>(`${this.apiUrl}/my`);
  }

  getTicketById(id: string): Observable<TicketResponse> {
    return this.http.get<TicketResponse>(`${this.apiUrl}/${id}`);
  }

  createTicket(ticket: TicketRequest): Observable<TicketResponse> {
    return this.http.post<TicketResponse>(this.apiUrl, ticket);
  }

  assignTicket(ticketId: string, technicienId: number): Observable<TicketResponse> {
    return this.http.put<TicketResponse>(`${this.apiUrl}/${ticketId}/assign`, { technicienId });
  }

  updateStatus(ticketId: string, status: string): Observable<TicketResponse> {
    return this.http.put<TicketResponse>(`${this.apiUrl}/${ticketId}/status`, { status });
  }

  deleteTicket(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}