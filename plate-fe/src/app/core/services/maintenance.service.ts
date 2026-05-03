import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Maintenance {
  idMaintenance: number;
  dateProgramme: Date;
  frequence: string;
  description: string;
  status: string;
  idEquipement: string;
  nomEquipement: string;
  salleNom: string;
  enRetard: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class MaintenanceService {
  private apiUrl = 'http://localhost:8080/api/maintenances';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Maintenance[]> {
    return this.http.get<Maintenance[]>(this.apiUrl);
  }

  updateStatus(id: number, status: string): Observable<Maintenance> {
    return this.http.patch<Maintenance>(`${this.apiUrl}/${id}/status?status=${status}`, {});
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
