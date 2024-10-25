import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Page } from '../models/page.model';
import { CommentDTO } from '../models/comment.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CommentService {
  private commentUrl = environment.apiUrl + '/comment/post';

  constructor(private http: HttpClient) {}

  getComments(postId: number): Observable<Page<CommentDTO>> {
    return this.http.get<Page<CommentDTO>>(`${this.commentUrl}/${postId}`);
  }
}
