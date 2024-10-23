import { Component } from '@angular/core';
import { FormBuilder, FormGroup, FormControl } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-post-form',
  templateUrl: './post-form.component.html',
  styleUrls: ['../../../styles/post-form.component.css']
})
export class PostFormComponent {
  postForm: FormGroup;
  newHashtagControl: FormControl = new FormControl(''); // Use FormControl for hashtag input

  constructor(private fb: FormBuilder, private http: HttpClient) {
    this.postForm = this.fb.group({
      description: [''],
      hashtags: [[]],
      media: [[]]
    });
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

    console.log('Hashtags after removing:', currentHashtags);  // Debug log
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

    const mediaArray = this.postForm.get('media')?.value || [];
    mediaArray.forEach((medium: any, index: number) => {
      formData.append(`media[${index}].file`, medium.file);
      formData.append(`media[${index}].mediumType`, medium.mediumType);
    });

    //TODO: Add finding the newly made post
    this.http.post('http://localhost:8080/post/create', formData).subscribe({
      next: (response) => {
        console.log('Post created successfully!', response),
        window.location.reload();
      },
      error: (error) => console.error('Error creating post', error)
    });
  }
}
