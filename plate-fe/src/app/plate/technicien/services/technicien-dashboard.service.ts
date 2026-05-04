import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TechnicienDashboardDTO } from '../models/technicien-dashboard.model';

@Injectable({
  providedIn: 'root'
})
export class TechnicienDashboardService {
  private apiUrl = 'http://localhost:8080/api/technicien/dashboard';

  constructor(private http: HttpClient) {}

  getDashboard(): Observable<TechnicienDashboardDTO> {
    return this.http.get<TechnicienDashboardDTO>(this.apiUrl);
  }
}
