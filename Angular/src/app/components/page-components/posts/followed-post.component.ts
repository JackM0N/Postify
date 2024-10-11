import { Component, OnInit } from '@angular/core';
import { PostService } from '../../../services/post.service';
import { PostDTO } from '../../../models/post.model';
import { formatDateTimeArray } from '../../../Util/formatDate';
import { Page } from '../../../models/page.model';

@Component({
  selector: 'app-followed-posts',
  templateUrl: './followed-post.component.html',
  styleUrls: ['../../../styles/post.component.css'],
})
export class FollowedPostsComponent implements OnInit {
  followedPosts: PostDTO[] = [];
  protected formatDateTimeArray = formatDateTimeArray;
  currentPage = 0;
  pageSize = 10;

  constructor(private postService: PostService) {}

  ngOnInit(): void {
    this.loadFollowedPosts();
  }

  loadFollowedPosts(): void {
    this.postService.getFollowedPosts(0, 10).subscribe(data => {
        this.followedPosts = data.content;
        this.followedPosts.forEach(post => {
          post.currentMediumIndex = post.currentMediumIndex ?? 0;
          this.loadMediaForPost(post);
        });
      }, error => {
        console.error('Error loading followed posts:', error);
        this.followedPosts = [];
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