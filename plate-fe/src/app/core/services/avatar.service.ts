import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AvatarService {
  private apiUrl = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}

  uploadAvatar(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<any>(`${this.apiUrl}/avatar/upload`, formData);
  }

  deleteAvatar(): Observable<any> {
    return this.http.delete(`${this.apiUrl}/avatar`);
  }

  getAvatarUrl(userId: number): string {
    return `${this.apiUrl}/${userId}/avatar`;
  }
}

