import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UserProfile {
  id: number;
  nom: string;
  email: string;
  telephone: string;
  role: string;
  avatarUrl?: string;
}

export interface UpdateProfile {
  nom: string;
  telephone: string;
}

export interface ChangePassword {
  oldPassword: string;
  newPassword: string;
  confirmPassword: string;
}

export interface Ticket {
  idTicket: number;
  titre: string;
  description: string;
  priorite: string;
  status: string;
  dateCreation: string;
  dateLimiteReparation: string;
  salle: {
    idSalle: number;
    nom: string;
  };
  equipement: {
    idEquipement: number;
    nom: string;
  };
}

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) { }

  getCurrentUser(): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.apiUrl}/users/me`);
  }

  updateProfile(data: UpdateProfile): Observable<UserProfile> {
    return this.http.put<UserProfile>(`${this.apiUrl}/users/me`, data);
  }

  changePassword(data: ChangePassword): Observable<any> {
    return this.http.post(`${this.apiUrl}/users/change-password`, data);
  }

  getMyTickets(): Observable<Ticket[]> {
    return this.http.get<Ticket[]>(`${this.apiUrl}/tickets/my-tickets`);
  }

  getTicketsByCurrentUser(): Observable<Ticket[]> {
    return this.getMyTickets();
  }
}



