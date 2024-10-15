import { Component, OnInit } from '@angular/core';
import { PostService } from '../../../services/post.service';
import { PostDTO } from '../../../models/post.model';
import { CommentService } from '../../../services/comment.service';

@Component({
  selector: 'app-post',
  templateUrl: './post.component.html',
  styleUrls: ['../../../styles/post.component.css'],
})
export class PostComponent implements OnInit {
  posts: PostDTO[] = [];

  constructor(private postService: PostService, private commentService: CommentService) {}

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

  loadCommentsForPost(post: PostDTO): void {
    this.commentService.getComments(post.id).subscribe(data => {
      post.comments = data.content;
    }, error => {
      console.error('Error loading comments:', error);
    });
  }

  loadMediaForPost(post: PostDTO): void {
    this.postService.getPostMedia(post.id).subscribe(media => {
      post.media = media.map(medium => ({
        url: `data:${medium.type};base64,${medium.base64Data}`,
        type: medium.type.startsWith('image') ? 'image' : 'video'
      }));
    }, error => {
      console.error('Error loading media:', error);
    });
  }
}
