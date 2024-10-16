import { Component, OnInit } from '@angular/core';
import { PostService } from '../../../services/post.service';
import { PostDTO } from '../../../models/post.model';

@Component({
  selector: 'app-post',
  templateUrl: './post.component.html',
})
export class PostComponent implements OnInit {
  posts: PostDTO[] = [];

  constructor(private postService: PostService) {}

  ngOnInit(): void {
    this.loadPosts();
  }

  loadPosts(): void {
    this.postService.getPosts(0, 10).subscribe(data => {
      this.posts = data.content;
    }, error => {
      console.error('Error loading posts:', error);
    });
  }
}
