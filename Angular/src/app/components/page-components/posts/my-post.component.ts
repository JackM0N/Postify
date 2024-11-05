import { Component, OnInit } from '@angular/core';
import { PostService } from '../../../services/post.service';
import { PostDTO } from '../../../models/post.model';
import { ToastrService } from 'ngx-toastr';
import { MatDialog } from '@angular/material/dialog';
import { PostFormDialogComponent } from './post-form-dialog.component';

@Component({
  selector: 'app-my-posts',
  templateUrl: './my-post.component.html',
  styleUrls: ['../../../styles/post.component.css']
})
export class MyPostsComponent implements OnInit {
  protected posts: PostDTO[] = [];
  protected showPostForm = false;

  constructor(
    private postService: PostService,
    private toastr: ToastrService,
    protected dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadMyPosts();
  }

  loadMyPosts(): void {
    this.postService.getMyPosts(0, 10).subscribe({
      next: data => {
        this.posts = data.content;
      },
      error: error => {
        this.toastr.error('Error loading posts');
        console.error('Error loading posts:', error);
      }
    });
  }

  openPostFormDialog(): void {
    const dialogRef = this.dialog.open(PostFormDialogComponent, {
      data: {
        editing: false
      }
    });

    dialogRef.componentInstance.postUpdated.subscribe(() => {
      this.loadMyPosts();
    });
  }
}
