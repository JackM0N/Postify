import { Component, Input } from '@angular/core';
import { PostDTO } from '../../../models/post.model';
import { CommentService } from '../../../services/comment.service';
import { PostService } from '../../../services/post.service';
import { formatDateTimeArray } from '../../../Util/formatDate';

@Component({
  selector: 'app-post-list',
  templateUrl: './post-list.component.html',
  styleUrls: ['../../../styles/post.component.css'],
})
export class PostListComponent {
  @Input() posts: PostDTO[] = [];
  @Input() showComments: boolean = true;
  protected formatDateTimeArray = formatDateTimeArray;

  constructor(
    private commentService: CommentService,
    private postService: PostService
  ) {}

  ngOnChanges(): void {
    this.posts.forEach(post => {
      post.currentMediumIndex = post.currentMediumIndex ?? 0;
      post.comments = post.comments ?? [];
      this.loadMediaForPost(post);
    });
  }

  loadCommentsForPost(post: PostDTO): void {
    if (post.showComments && post.comments.length === 0) {
      this.commentService.getComments(post.id).subscribe(data => {
        post.comments = data.content;
      }, error => {
        console.error('Error loading comments:', error);
      });
    }
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

  toggleComments(post: PostDTO): void {
    post.showComments = !post.showComments;
    if (post.showComments) {
      this.loadCommentsForPost(post);
    }
  }

  previousMedium(post: PostDTO): void {
    const index = post.currentMediumIndex ?? 0;
    if (post.media && index > 0) {
      post.currentMediumIndex = index - 1;
    }
  }

  nextMedium(post: PostDTO): void {
    const index = post.currentMediumIndex ?? 0;
    if (post.media && index < (post.media.length - 1)) {
      post.currentMediumIndex = index + 1;
    }
  }
}
