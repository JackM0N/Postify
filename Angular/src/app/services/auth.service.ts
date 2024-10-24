import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { JwtHelperService } from '@auth0/angular-jwt';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private loginUrl = environment.apiUrl + '/login';
  private registerUrl = environment.apiUrl + '/register';
  private isLoggedInSubject = new BehaviorSubject<boolean>(this.checkToken());

  constructor(private http: HttpClient) {}

  login(userData: { email: string, password: string }): Observable<{ token: string }> {
    return this.http.post<{ token: string }>(this.loginUrl, userData);
  }

  register(data: any): Observable<any> {
    return this.http.post(this.registerUrl, data);
  }

  checkToken(): boolean {
    const token = localStorage.getItem(environment.tokenKey);
    return !!token && !new JwtHelperService().isTokenExpired(token);
  }

  get isLoggedIn$(): Observable<boolean> {
    return this.isLoggedInSubject.asObservable();
  }

  setLoggedIn(status: boolean): void {
    this.isLoggedInSubject.next(status);
  }

  logout(): void {
    localStorage.removeItem(environment.tokenKey);
    this.setLoggedIn(false);
  }
}
