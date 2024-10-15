import { Component, OnInit } from '@angular/core';
import { PostService } from '../../../services/post.service';
import { PostDTO } from '../../../models/post.model';
import { formatDateTimeArray } from '../../../Util/formatDate';
import { CommentService } from '../../../services/comment.service';

@Component({
  selector: 'app-post',
  templateUrl: './post.component.html',
  styleUrls: ['../../../styles/post.component.css'],
})
export class PostComponent implements OnInit {
  posts: PostDTO[] = [];
  currentMediumIndex: number = 0;
  protected formatDateTimeArray = formatDateTimeArray;

  constructor(private postService: PostService, private commentService: CommentService) {}

  ngOnInit(): void {
    this.loadPosts();
  }

  loadPosts(): void {
    this.postService.getPosts({}, {}).subscribe(data => {
        this.posts = data.content
        this.posts.forEach(post => {
          post.currentMediumIndex = post.currentMediumIndex ?? 0;
          post.comments = [];
          post.showComments = false;
          this.loadMediaForPost(post);
        });
      }, error => {
        console.error('Error loading posts:', error);
      });
  }

  loadMediaForPost(post: PostDTO): void {
    this.postService.getPostMedia(post.id).subscribe(media => {
      post.media = media.map(medium => ({
        url: `data:${medium.type};base64,${medium.base64Data}`,
        type: medium.type.startsWith('image') ? 'image' : 'video'
      }));
    }, error => {
      console.error(`Error loading media for post ${post.id}:`, error);
    });
  }

  toggleComments(post: PostDTO): void {
    post.showComments = !post.showComments;
    if (post.showComments && post.comments.length === 0) {
      this.loadCommentsForPost(post);
    }
  }

  loadCommentsForPost(post: PostDTO): void {
    this.commentService.getComments(post.id).subscribe(data => {
      post.comments = data.content;
    }, error => {
      console.error(`Error loading comments for post ${post.id}:`, error);
      post.comments = [];
    });
  }

  previousMedium(post: PostDTO): void {
    if (post.media && (post.currentMediumIndex ?? 0) > 0) {
        post.currentMediumIndex!--;
    }
  }

  nextMedium(post: PostDTO): void {
    if (post.media && (post.currentMediumIndex ?? 0) < (post.media.length - 1)) {
        post.currentMediumIndex!++;
    }
  }
}