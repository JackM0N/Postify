import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { WebsiteUserDTO } from '../models/website-user.model';

@Injectable({
    providedIn: 'root'
})
    export class WebsiteUserService {
    private profileUrl = '/user/my-profile'; // API endpoint
  
    constructor(private http: HttpClient) {}
  
    getMyProfile(): Observable<WebsiteUserDTO> {
      return this.http.get<WebsiteUserDTO>(this.profileUrl);
    }
}