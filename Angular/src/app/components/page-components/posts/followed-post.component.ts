import { Component, OnInit } from '@angular/core';
import { PostService } from '../../../services/post.service';
import { PostDTO } from '../../../models/post.model';
import { CommentService } from '../../../services/comment.service';

@Component({
  selector: 'app-followed-posts',
  templateUrl: './followed-post.component.html',
  styleUrls: ['../../../styles/post.component.css'],
})
export class FollowedPostsComponent implements OnInit {
[x: string]: any;
  followedPosts: PostDTO[] = [];

  constructor(private postService: PostService, private commentService: CommentService) {}

  ngOnInit(): void {
    this.loadFollowedPosts();
  }

  loadFollowedPosts(): void {
    this.postService.getFollowedPosts(0, 10).subscribe(data => {
      this.followedPosts = data.content;
    }, error => {
      console.error('Error loading followed posts:', error);
    });
  }
}
