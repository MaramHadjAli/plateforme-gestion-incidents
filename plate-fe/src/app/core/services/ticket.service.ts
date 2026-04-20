import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface TicketCreateResponse {
  idTicket: string;
  dateCreation: string | null;
  dateLimiteReparation: string | null;
  status: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class TicketService {
  private readonly apiBase = 'http://localhost:8080/api/tickets';

  constructor(private http: HttpClient) {}

  createTicket(payload: FormData): Observable<TicketCreateResponse> {
    return this.http.post<TicketCreateResponse>(this.apiBase, payload);
  }
}

