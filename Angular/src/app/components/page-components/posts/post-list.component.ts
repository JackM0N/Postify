import { Component, Input, Output, EventEmitter } from '@angular/core';
import { PostDTO } from '../../../models/post.model';
import { formatDateTimeArray } from '../../../Util/formatDate';

@Component({
  selector: 'app-post-list',
  templateUrl: './post-list.component.html',
  styleUrls: ['../../../styles/post-list.component.css'],
})
export class PostListComponent {
  @Input() posts: PostDTO[] = [];
  @Input() showComments: boolean = true;
  @Output() loadCommentsForPost = new EventEmitter<PostDTO>();
  @Output() loadMediaForPost = new EventEmitter<PostDTO>();
  protected formatDateTimeArray = formatDateTimeArray;
  currentMediumIndex: number = 0;

  ngOnChanges(): void {
    this.posts.forEach(post => {
      post.currentMediumIndex = post.currentMediumIndex ?? 0;
      post.comments = post.comments ?? [];
      this.loadMediaForPost.emit(post);
    });
  }

  toggleComments(post: PostDTO): void {
    if (this.showComments) {
      post.showComments = !post.showComments;
      if (post.showComments && post.comments.length === 0) {
        this.loadCommentsForPost.emit(post);
      }
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
