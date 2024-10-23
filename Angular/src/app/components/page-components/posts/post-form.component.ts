import { Component, Input } from '@angular/core';
import { FormBuilder, FormGroup, FormControl } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { PostDTO } from '../../../models/post.model';

@Component({
  selector: 'app-post-form',
  templateUrl: './post-form.component.html',
  styleUrls: ['../../../styles/post-form.component.css']
})
export class PostFormComponent {
  @Input() post: PostDTO | null = null;
  postForm: FormGroup;
  newHashtagControl: FormControl = new FormControl('');

  constructor(private fb: FormBuilder, private http: HttpClient) {
    this.postForm = this.fb.group({
      description: [''],
      hashtags: [[]],
      media: [[]]
    });
  }

  ngOnInit(): void {
    if (this.post) {
      this.postForm.patchValue({
        description: this.post.description,
        hashtags: this.post.hashtags ? this.post.hashtags.map(h => h.hashtag) : []
      });
    }
  }

  addHashtag() {
    const currentHashtags = this.postForm.get('hashtags')?.value || [];
    const newHashtag = this.newHashtagControl.value.trim();

    if (newHashtag) {
      currentHashtags.push(newHashtag);
      this.postForm.get('hashtags')?.setValue(currentHashtags);
      this.newHashtagControl.reset();
    } else {
      console.warn('Hashtag input is empty!');
    }
  }

  removeHashtag(index: number) {
    const currentHashtags = this.postForm.get('hashtags')?.value || [];
    currentHashtags.splice(index, 1);
    this.postForm.get('hashtags')?.setValue(currentHashtags);
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
    
    formData.append('description', this.postForm.get('description')?.value);
  
    const hashtagsArray = this.postForm.get('hashtags')?.value || [];
    hashtagsArray.forEach((hashtag: string, index: number) => {
      formData.append(`hashtags[${index}].hashtag`, hashtag);
    });
  
    if (this.post) {
      this.http.put(`http://localhost:8080/post/edit/${this.post.id}`, formData).subscribe({
        next: (response) => {
          console.log('Post updated successfully!', response);
          window.location.reload();
        },
        error: (error) => console.error('Error updating post', error)
      });
    } else {
      const mediaArray = this.postForm.get('media')?.value || [];
      mediaArray.forEach((medium: any, index: number) => {
        formData.append(`media[${index}].file`, medium.file);
        formData.append(`media[${index}].mediumType`, medium.mediumType);
      });
  
      this.http.post('http://localhost:8080/post/create', formData).subscribe({
        next: (response) => {
          console.log('Post created successfully!', response);
          window.location.reload();
        },
        error: (error) => console.error('Error creating post', error)
      });
    }
  }
}
