import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SimplifiedUserDTO } from '../models/simplified-user.model';

@Injectable({
  providedIn: 'root',
})
export class FollowedUsersService {
  private baseUrl = '/follow/followed-users';
  private pfpUrl = '/follow/pfp';

  constructor(private http: HttpClient) {}

  getFollowedUsers(searchText: string, page: number, size: number): Observable<SimplifiedUserDTO[]> {
    const params = new HttpParams()
      .set('searchText', searchText)
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<SimplifiedUserDTO[]>(this.baseUrl, { params });
  }
}