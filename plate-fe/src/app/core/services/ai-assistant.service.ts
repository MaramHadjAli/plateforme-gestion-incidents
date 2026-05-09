import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AiAssistantService {
  private apiUrl = `${environment.apiUrl}/ai/maintenance`;

  constructor(private http: HttpClient) {}

  chat(prompt: string, equipmentId?: string): Observable<string> {
    return this.http.post(`${this.apiUrl}/chat`, { prompt, equipmentId }, { responseType: 'text' });
  }

  suggestNextMaintenance(equipmentId: string): Observable<string> {
    return this.http.get(`${this.apiUrl}/suggest/${equipmentId}`, { responseType: 'text' });
  }

  getPreventionAdvice(ticketId: string): Observable<string> {
    return this.http.get(`${this.apiUrl}/prevent/${ticketId}`, { responseType: 'text' });
  }
}
