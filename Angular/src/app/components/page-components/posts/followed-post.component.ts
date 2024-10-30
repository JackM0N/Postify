import { Component, OnInit } from '@angular/core';
import { PostService } from '../../../services/post.service';
import { PostDTO } from '../../../models/post.model';

@Component({
  selector: 'app-followed-posts',
  templateUrl: './followed-post.component.html',
  styleUrls: ['../../../styles/post.component.css'],
})
export class FollowedPostsComponent implements OnInit {
  followedPosts: PostDTO[] = [];

  constructor(private postService: PostService) {}

  ngOnInit(): void {
    this.loadFollowedPosts();
  }

  loadFollowedPosts(): void {
    this.postService.getFollowedPosts(0, 10).subscribe({
      next: data => {
        this.followedPosts = data.content;
      },
      error: error => {
        console.error('Error loading followed posts:', error);
      }
    });
  }
}
