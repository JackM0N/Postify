import { Component, EventEmitter, Inject, Output } from '@angular/core';
import { FormBuilder, FormGroup, FormControl, Validators } from '@angular/forms';
import { PostDTO } from '../../../models/post.model';
import { PostService } from '../../../services/post.service';
import { ToastrService } from 'ngx-toastr';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-post-form-dialog',
  templateUrl: './post-form-dialog.component.html',
  styleUrls: ['../../../styles/post-form.component.css']
})
export class PostFormDialogComponent {
  @Output() postUpdated = new EventEmitter<void>();

  protected postForm: FormGroup;
  public newHashtagControl: FormControl = new FormControl('');

  private mediaFiles: { file: File; mediumType: string }[] = [];

  constructor(
    private fb: FormBuilder,
    private postService: PostService,
    private toastr: ToastrService,
    private dialogRef: MatDialogRef<PostFormDialogComponent>,
    @Inject(MAT_DIALOG_DATA) protected data: {
      post?: PostDTO,
      editing: boolean
    }
  ) {
    this.postForm = this.fb.group({
      description: ['', Validators.required],
      hashtags: [[]],
      media: [[]]
    });
  }

  ngOnInit(): void {
    if (this.data.post) {
      this.postForm.patchValue({
        description: this.data.post.description,
        hashtags: this.data.post.hashtags ? this.data.post.hashtags.map(h => h.hashtag) : []
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
    const files: FileList = event.target.files;

    for (let i = 0; i < files.length; i++) {
      const file: File = files[i];
      this.mediaFiles.push({
        file: file,
        mediumType: file.type
      });
    }

    // Just for indication that files were added
    this.postForm.patchValue({
      media: this.mediaFiles.map(f => ({ mediumType: f.mediumType }))
    });
  }

  submitForm() {
    if (this.postForm.valid) {
      const formData = new FormData();
      const post = {
        ...this.data.post,
        ...this.postForm.value
      };

      formData.append('description', post.description);

      const hashtagsArray = this.postForm.get('hashtags')?.value || [];
      hashtagsArray.forEach((hashtag: string, index: number) => {
        formData.append(`hashtags[${index}].hashtag`, hashtag);
      });
    
      if (this.data.editing == true) {
        this.dialogRef.close(true);
        
        this.postService.editPost(post.id, formData).subscribe({
          next: (response) => {
            this.toastr.success('Post updated successfully!');
            this.postUpdated.emit();
            window.location.reload();
          },
          error: (error) => {
            this.toastr.error('Error updating post');
            console.error('Error updating post', error);
          }
        });
      } else {
        // Add media files to formData
        this.mediaFiles.forEach((medium, index) => {
          formData.append(`media[${index}].file`, medium.file);
          formData.append(`media[${index}].mediumType`, medium.mediumType);
        });

        this.dialogRef.close(true);
    
        this.postService.addPost(formData).subscribe({
          next: (response) => {
            this.toastr.success('Post created successfully!');
            this.postUpdated.emit();
          },
          error: (error) => {
            this.toastr.error('Error creating post');
            console.error('Error creating post', error);
          }
        });
      }
    }
  }
}
