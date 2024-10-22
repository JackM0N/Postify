import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PostDTO } from '../models/post.model';
import { Page } from '../models/page.model';
import { MediumDTO } from '../models/medium.model';

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

  getMyPosts(filter: any, pageable: any): Observable<Page<PostDTO>> {
    return this.http.get<Page<PostDTO>>(`${this.baseUrl}/my-posts`, { params: { ...filter, ...pageable } });
  }

  getFollowedPosts(page: number, size: number): Observable<Page<PostDTO>> {
    return this.http.get<Page<PostDTO>>(`${this.baseUrl}/followed?page=${page}&size=${size}`);
  }

  likePost(postId: number): Observable<boolean> {
    return this.http.post<boolean>(`${this.baseUrl}/like/${postId}`, {});
  }

  createPost(postData: FormData): Observable<any> {
    return this.http.post(`${this.baseUrl}/create`, postData);
  }


  getPostMedia(postId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.mediaUrl}/list/${postId}`);
  }

  addMedium(postId: number, index: number, mediumDTO: MediumDTO): Observable<PostDTO> {
    const formData = new FormData();
    formData.append('file', mediumDTO.file!);
    formData.append('mediumType', mediumDTO.mediumType);
    
    return this.http.put<PostDTO>(`${this.mediaUrl}/add/${index}`, formData);
  }
  
  updateMedium(postId: number, position: number, mediumDTO: MediumDTO): Observable<PostDTO> {
    const formData = new FormData();
    formData.append('file', mediumDTO.file!);
    formData.append('mediumType', mediumDTO.mediumType);
  
    return this.http.put<PostDTO>(`${this.mediaUrl}/edit/${position}`, formData);
  }
  
  deleteMedium(mediumDTO: MediumDTO, position: number): Observable<any> {
    return this.http.request<any>('DELETE', `${this.mediaUrl}/delete/${position}`, {
      body: mediumDTO,
    });
  }
}