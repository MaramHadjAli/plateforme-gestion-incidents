import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Salle } from '../models/salle.model';

@Injectable({
  providedIn: 'root'
})
export class SalleService {
  private apiUrl = 'http://localhost:8080/api/salles';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Salle[]> {
    return this.http.get<Salle[]>(this.apiUrl);
  }

  getById(id: string): Observable<Salle> {
    return this.http.get<Salle>(`${this.apiUrl}/${id}`);
  }

  create(salle: Salle): Observable<Salle> {
    return this.http.post<Salle>(this.apiUrl, salle);
  }

  update(id: string, salle: Salle): Observable<Salle> {
    return this.http.put<Salle>(`${this.apiUrl}/${id}`, salle);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
