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

  getPostById(id: number): Observable<PostDTO> {
    return this.http.get<PostDTO>(`${this.baseUrl}/id/${id}`);
  }

  getPosts(filter: any, pageable: any): Observable<Page<PostDTO>> {
    return this.http.get<Page<PostDTO>>(`${this.baseUrl}/list`, { params: { ...filter, ...pageable } });
  }

  getPostMedia(postId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.mediaUrl}/list/${postId}`);
  }

  getFollowedPosts(page: number, size: number): Observable<Page<PostDTO>> {
    return this.http.get<Page<PostDTO>>(`${this.baseUrl}/followed?page=${page}&size=${size}`);
  }

  likePost(postId: number): Observable<boolean> {
    return this.http.post<boolean>(`${this.baseUrl}/like/${postId}`, {});
  }
}