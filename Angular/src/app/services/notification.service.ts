import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { NotificationDTO } from '../models/notification.model';
import { Page } from '../models/page.model';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private apiUrl = environment.apiUrl + '/notification/list';

  constructor(private http: HttpClient) {}

  getNotifications(page: number, size: number): Observable<Page<NotificationDTO>> {
    return this.http.get<Page<NotificationDTO>>(`${this.apiUrl}?page=${page}&size=${size}`);
  }
}
