import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PostService } from '../../../services/post.service';
import { PostDTO } from '../../../models/post.model';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-user-post',
  templateUrl: './user-post.component.html',
  styleUrls: ['../../../styles/post.component.css']
})
export class UserPostsComponent implements OnInit {
  @Input() userId!: number;
  protected posts: PostDTO[] = [];

  constructor(
    private postService: PostService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    if (this.userId) {
      this.loadUserPosts();
    }
  }

  loadUserPosts(): void {
    this.postService.getUserPosts(this.userId, {}, { page: 0, size: 10 }).subscribe({
      next: data => {
        this.posts = data.content;
      },
      error: error => {
        this.toastr.error('Error loading user posts');
        console.error('Error loading user posts:', error);
      }
    });
  }
}
