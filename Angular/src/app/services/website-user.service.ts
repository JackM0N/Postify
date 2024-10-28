import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { WebsiteUserDTO } from '../models/website-user.model';
import { environment } from '../../environments/environment';

@Injectable({
    providedIn: 'root'
})
    export class WebsiteUserService {
    private baseUrl = environment.apiUrl + '/user';
  
    constructor(private http: HttpClient) {}
  
    getAccount(): Observable<WebsiteUserDTO> {
      return this.http.get<WebsiteUserDTO>(`${this.baseUrl}/account`, {responseType: 'json' as 'json'});
    }

    updateAccount(accountData: Partial<WebsiteUserDTO>): Observable<WebsiteUserDTO> {
      const formData = new FormData();

      Object.keys(accountData).forEach(key => {
        const value = accountData[key as keyof WebsiteUserDTO];
        if (value !== null && value !== undefined) {
          formData.append(key, value as string | Blob);
        }
      });

      return this.http.put<WebsiteUserDTO>(`${this.baseUrl}/edit-profile`, formData);
    }
}