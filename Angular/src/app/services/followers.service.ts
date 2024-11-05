import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SimplifiedUserDTO } from '../models/simplified-user.model';
import { Page } from '../models/page.model';
import { FollowDTO } from '../models/follow.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class FollowedUsersService {
  private baseUrl = environment.apiUrl + '/follow'
  private followedUsersUrl = environment.apiUrl + '/follow/followed-users';

  constructor(private http: HttpClient) {}

  followUser(followDTO: FollowDTO): Observable<FollowDTO> {
    return this.http.post<FollowDTO>(`${this.baseUrl}/create`, followDTO);
  }

  unfollowUser(username: string): Observable<any> {
    return this.http.delete(`${this.baseUrl}/delete/${username}`);
  }

  isFollowed(userId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.baseUrl}/is-followed/${userId}`);
  }

  getFollowedUsers(searchText: string, page: number, size: number): Observable<Page<SimplifiedUserDTO>> {
    const params = new HttpParams()
    .set('searchText', searchText)
    .set('page', page.toString())
    .set('size', size.toString());
    return this.http.get<Page<SimplifiedUserDTO>>(this.followedUsersUrl, { params });
  }
}
