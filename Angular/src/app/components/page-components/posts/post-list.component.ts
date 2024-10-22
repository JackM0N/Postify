import { Component, Input } from '@angular/core';
import { PostDTO } from '../../../models/post.model';
import { CommentService } from '../../../services/comment.service';
import { PostService } from '../../../services/post.service';
import { formatDateTimeArray } from '../../../Util/formatDate';
import { MediumDTO } from '../../../models/medium.model';

@Component({
  selector: 'app-post-list',
  templateUrl: './post-list.component.html',
  styleUrls: ['../../../styles/post.component.css'],
})
export class PostListComponent {
  @Input() posts: PostDTO[] = [];
  @Input() showComments: boolean = true;
  @Input() canEdit: boolean = false;
  protected formatDateTimeArray = formatDateTimeArray;

  selectedFile: File | undefined;

  constructor(
    private commentService: CommentService,
    private postService: PostService,
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
        id: medium.id,
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

  likePost(post: PostDTO): void {
    this.postService.likePost(post.id).subscribe(() => {
        post.isLiked = !post.isLiked;

        this.postService.getPostById(post.id).subscribe(updatedPost => {
          post.likeCount = updatedPost.likeCount;
        }, error => {
          console.error('Error fetching updated post:', error);
        });
      },
      (error) => {
        console.error('Error liking post:', error);
      }
    );
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

//TODO:Add proper post editing

  editPost(post: PostDTO): void {
    console.log('Editing post:', post);
  }

  addMedium(post: PostDTO, index: number): void {
    const mediumDTO: MediumDTO = {
      postId: post.id,
      mediumType: 'image',
      file: this.selectedFile
    };

    this.postService.addMedium(post.id, index, mediumDTO).subscribe(
      updatedPost => {
        post.media = updatedPost.media; // Aktualizuj media w poście
      },
      error => {
        console.error('Error adding medium:', error);
      }
    );
  }

  editMedium(post: PostDTO, index: number): void {
    const mediumDTO: MediumDTO = {
      postId: post.id,
      mediumType: 'image',
      file: this.selectedFile
    };

    this.postService.updateMedium(post.id, index, mediumDTO).subscribe(
      updatedPost => {
        post.media = updatedPost.media;
      },
      error => {
        console.error('Error updating medium:', error);
      }
    );
  }

  deleteMedium(post: PostDTO, index: number): void {
    const mediumDTO: MediumDTO = {
      id: post.media[index].id,
      postId: post.id,
      mediumType: post.media[index].type,
      mediumUrl: post.media[index].url
    };
  
    this.postService.deleteMedium(mediumDTO, index).subscribe(
      () => {
        post.media.splice(index, 1);
      },
      error => {
        console.error('Error deleting medium:', error);
      }
    );
  }

}
