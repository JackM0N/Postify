import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { PostService } from '../../../services/post.service';

@Component({
  selector: 'app-post-form',
  templateUrl: './post-form.component.html',
  styleUrls: ['../../../styles/post-form.component.css']
})
export class PostFormComponent {
    postForm: FormGroup;
  
    constructor(private fb: FormBuilder, private http: HttpClient) {
      this.postForm = this.fb.group({
        description: [''],
        userId: ['1'],
        media: [[]]
      });
    }
  
    onFileChange(event: any) {
      const files = event.target.files;
      const mediaArray = this.postForm.get('media')?.value || [];
      
      for (let i = 0; i < files.length; i++) {
        mediaArray.push({
          file: files[i],
          mediumType: files[i].type
        });
      }
      
      this.postForm.get('media')?.setValue(mediaArray);
    }
  
    submitForm() {
      const formData = new FormData();
      
      // Add description
      formData.append('description', this.postForm.get('description')?.value);
  
      // Add userId
      formData.append('user.id', this.postForm.get('userId')?.value);
  
      // Add media files
      const mediaArray = this.postForm.get('media')?.value || [];
      mediaArray.forEach((medium: any, index: number) => {
        formData.append(`media[${index}].file`, medium.file);
        formData.append(`media[${index}].mediumType`, medium.mediumType);
      });
  
      this.http.post('http://localhost:8080/post/create', formData).subscribe({
        next: (response) => console.log('Post created successfully!', response),
        error: (error) => console.error('Error creating post', error)
      });
    }
 }
