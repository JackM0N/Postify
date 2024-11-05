import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { WebsiteUserDTO } from '../models/website-user.model';
import { environment } from '../../environments/environment';
import { MediumBase64DTO } from '../models/medium-base64.model';
import { JwtHelperService } from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root'
})
  export class WebsiteUserService {
  private baseUrl = environment.apiUrl + '/user';
  private helper: JwtHelperService = new JwtHelperService();

  constructor(private http: HttpClient) {}

  getAccount(): Observable<WebsiteUserDTO> {
    return this.http.get<WebsiteUserDTO>(`${this.baseUrl}/account`, {responseType: 'json' as const});
  }

  updateAccount(accountData: FormData): Observable<{ token: string }> {
    return this.http.put<{ token: string }>(`${this.baseUrl}/edit-profile`, accountData);
  }

  getUserProfile(username: string): Observable<WebsiteUserDTO> {
    return this.http.get<WebsiteUserDTO>(`${this.baseUrl}/profile/${username}`);
  }

  getProfilePicture(userId: number): Observable<MediumBase64DTO>{
    return this.http.get<MediumBase64DTO>(`${this.baseUrl}/pfp/${userId}`)
  }


  getCurrentUsername(): string | null {
    const token = localStorage.getItem(environment.tokenKey);
    if (token && !this.helper.isTokenExpired(token)) {
      const decodedToken = this.helper.decodeToken(token);
      return decodedToken.username || null;
    }
    return null;
  }
}
