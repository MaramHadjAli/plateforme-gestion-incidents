import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PrioriteTicket } from '../models/priority-ticket.enum';
import { TicketResponse } from '../models/ticket-response.model';
import { TicketRequest } from '../models/ticket-request.model';


@Injectable({
  providedIn: 'root'
})
export class TicketsService {

  private apiUrl = 'http://localhost:8080/api/tickets';

  constructor(private http: HttpClient) { }

  /**
   * Fetches the list of all tickets from the backend.
   */
  getAllTickets(): Observable<TicketResponse[]> {
    return this.http.get<TicketResponse[]>(this.apiUrl);
  }

  /**
   * Sends a POST request to create a new ticket.
   * @param ticket The ticket data matching TicketRequestDTO
   */
  createTicket(ticket: TicketRequest): Observable<TicketResponse> {
    return this.http.post<TicketResponse>(this.apiUrl, ticket);
  }
}