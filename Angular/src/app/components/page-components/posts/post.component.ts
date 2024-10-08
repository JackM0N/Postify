import { Component, OnInit } from '@angular/core';
import { PostService } from '../../../services/post.service';
import { PostDTO } from '../../../models/post.model';
import { formatDateTimeArray } from '../../../Util/formatDate';

@Component({
  selector: 'app-post',
  templateUrl: './post.component.html',
  styleUrls: ['../../../styles/post.component.css'],
})
export class PostComponent implements OnInit {
  posts: PostDTO[] = [];
  currentMediumIndex: number = 0;
  protected formatDateTimeArray = formatDateTimeArray;

  constructor(private postService: PostService) {}

  ngOnInit(): void {
    this.loadPosts();
  }

  loadPosts(): void {
    this.postService.getPosts({}, {}).subscribe(data => {
        this.posts = data.content
        this.posts.forEach(post => {
          post.currentMediumIndex = post.currentMediumIndex ?? 0;
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

  previousMedium(post: PostDTO): void {
    if (post.media && (post.currentMediumIndex ?? 0) > 0) {
        post.currentMediumIndex!--; // Use the non-null assertion operator
    }
  }

  nextMedium(post: PostDTO): void {
    if (post.media && (post.currentMediumIndex ?? 0) < (post.media.length - 1)) {
        post.currentMediumIndex!++;
    }
  }
}