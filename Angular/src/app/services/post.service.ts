import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PostDTO } from '../models/post.model';
import { Page } from '../models/page.model';

@Injectable({
  providedIn: 'root'
})
export class PostService {
  private baseUrl = 'http://localhost:8080/post';
  private mediaUrl = 'http://localhost:8080/medium';

  constructor(private http: HttpClient) {}

  getPosts(filter: any, pageable: any): Observable<Page<PostDTO>> {
    return this.http.get<Page<PostDTO>>(`${this.baseUrl}/list`, { params: { ...filter, ...pageable } });
  }

  getPostMedia(postId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.mediaUrl}/list/${postId}`);
  }
}