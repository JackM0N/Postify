import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PostDTO } from '../models/post.model'; // Import your PostDTO model
import { Page } from '../models/page.model'; // If you're using pagination

@Injectable({
  providedIn: 'root'
})
export class PostService {
  private baseUrl = 'http://localhost:8080/post';

  constructor(private http: HttpClient) {}

  getPosts(filter: any, pageable: any): Observable<Page<PostDTO>> {
    return this.http.get<Page<PostDTO>>(`${this.baseUrl}/list`, { params: { ...filter, ...pageable } });
  }
}