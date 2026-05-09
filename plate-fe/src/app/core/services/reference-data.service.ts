import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { EquipementSummary, SalleSummary } from '../models/reference-data.model';

@Injectable({
  providedIn: 'root'
})
export class ReferenceDataService {
  private readonly apiBase = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  getSalles(): Observable<SalleSummary[]> {
    return this.http.get<SalleSummary[]>(`${this.apiBase}/salles`);
  }

  getEquipements(salleId?: string | null): Observable<EquipementSummary[]> {
    const query = salleId ? `?salleId=${encodeURIComponent(salleId)}` : '';
    return this.http.get<EquipementSummary[]>(`${this.apiBase}/equipements${query}`);
  }
}

