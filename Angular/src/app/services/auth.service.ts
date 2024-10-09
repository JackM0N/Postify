import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private loginUrl = 'http://localhost:8080/login';
  private registerUrl = 'http://localhost:8080/register';

  constructor(private http: HttpClient) {}

  login(userData: { email: string, password: string }): Observable<{ token: string }> {
    return this.http.post<{ token: string }>(this.loginUrl, userData);
  }

  register(data: any): Observable<any> {
    return this.http.post(this.registerUrl, data);
  }
}