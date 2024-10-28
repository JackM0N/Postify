import { Component, OnInit } from '@angular/core';
import { PostService } from '../../../services/post.service';
import { PostDTO } from '../../../models/post.model';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-post',
  templateUrl: './post.component.html',
})
export class PostComponent implements OnInit {
  protected posts: PostDTO[] = [];
  protected showPostForm: boolean = false;

  constructor(
    private postService: PostService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.loadPosts();
  }

  loadPosts(): void {
    this.postService.getPosts(0, 10).subscribe({
      next: data => {
        this.posts = data.content;
      },
      error: error => {
        this.toastr.error('Error loading posts');
        console.error('Error loading posts:', error);
      }
    });
  }
}
