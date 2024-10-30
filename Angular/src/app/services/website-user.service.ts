import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { WebsiteUserDTO } from '../models/website-user.model';
import { environment } from '../../environments/environment';
import { MediumBase64DTO } from '../models/medium-base64.model';

@Injectable({
    providedIn: 'root'
})
    export class WebsiteUserService {
    private baseUrl = environment.apiUrl + '/user';
  
    constructor(private http: HttpClient) {}
  
    getAccount(): Observable<WebsiteUserDTO> {
      return this.http.get<WebsiteUserDTO>(`${this.baseUrl}/account`, {responseType: 'json' as 'json'});
    }

    updateAccount(accountData: FormData): Observable<{ token: string }> {
      return this.http.put<{ token: string }>(`${this.baseUrl}/edit-profile`, accountData);
    }

    getProfilePicture(userId: number): Observable<MediumBase64DTO>{
      return this.http.get<MediumBase64DTO>(`${this.baseUrl}/pfp/${userId}`)
    }
}