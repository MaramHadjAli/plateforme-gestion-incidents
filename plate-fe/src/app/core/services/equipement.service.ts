import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Equipement } from '../models/equipement.model';

@Injectable({
  providedIn: 'root'
})
export class EquipementService {
  private apiUrl = 'http://localhost:8080/api/equipements';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Equipement[]> {
    return this.http.get<Equipement[]>(this.apiUrl);
  }

  getById(id: string): Observable<Equipement> {
    return this.http.get<Equipement>(`${this.apiUrl}/${id}`);
  }

  getBySalle(idSalle: string): Observable<Equipement[]> {
    return this.http.get<Equipement[]>(`${this.apiUrl}/salle/${idSalle}`);
  }

  create(equipement: Equipement): Observable<Equipement> {
    return this.http.post<Equipement>(this.apiUrl, equipement);
  }

  update(id: string, equipement: Equipement): Observable<Equipement> {
    return this.http.put<Equipement>(`${this.apiUrl}/${id}`, equipement);
  }

  updateEtat(id: string, etat: string): Observable<Equipement> {
    return this.http.patch<Equipement>(`${this.apiUrl}/${id}/etat?etat=${etat}`, {});
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
