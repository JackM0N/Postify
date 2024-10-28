import { Component, Input } from '@angular/core';
import { PostDTO } from '../../../models/post.model';
import { CommentService } from '../../../services/comment.service';
import { PostService } from '../../../services/post.service';
import { formatDateTimeArray } from '../../../util/formatDate';
import { MediumDTO } from '../../../models/medium.model';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-post-list',
  templateUrl: './post-list.component.html',
  styleUrls: ['../../../styles/post.component.css'],
})
export class PostListComponent {
  @Input() public posts: PostDTO[] = [];
  @Input() public showComments: boolean = true;
  @Input() public canEdit: boolean = false;
  protected formatDateTimeArray = formatDateTimeArray;

  private selectedFile: File | undefined;
  protected editingPost: PostDTO | null = null;

  constructor(
    private commentService: CommentService,
    private postService: PostService,
    private toastr: ToastrService
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
      this.commentService.getComments(post.id).subscribe({
        next: data => {
          post.comments = data.content;
        },
        error: error => {
          this.toastr.error('Error loading comments');
          console.error('Error loading comments:', error);
        }
      });
    }
  }

  loadMediaForPost(post: PostDTO): void {
    post.media = [];
    this.postService.getPostMedia(post.id).subscribe({
      next: media => {
        if (media) {
          post.media = media.map(medium => ({
            id: medium.id,
            mediumUrl: `data:${medium.type};base64,${medium.base64Data}`,
            mediumType: medium.type.startsWith('image') ? 'image' : 'video'
          }));
        }
      },
      error: error => {
        this.toastr.error('Error loading media');
        console.error('Error loading media:', error);
      }
    });
  }

  toggleComments(post: PostDTO): void {
    post.showComments = !post.showComments;
    if (post.showComments) {
      this.loadCommentsForPost(post);
    }
  }

  likePost(post: PostDTO): void {
    this.postService.likePost(post.id).subscribe({
      next: () => {
        post.isLiked = !post.isLiked;

        this.postService.getPostById(post.id).subscribe({
          next: updatedPost => {
            post.likeCount = updatedPost.likeCount;
          },
          error: error => {
            this.toastr.error('Error fetching updated post');
            console.error('Error fetching updated post:', error);
          }
        });
      },
      error: (error) => {
        this.toastr.error('Error liking post');
        console.error('Error liking post:', error);
      }
    });
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

  editPost(post: PostDTO): void {
    this.editingPost = post;
  }

  cancelEdit(): void {
    this.editingPost = null;
  }

  toggleAddMedium(post: PostDTO, side: string): void {
    post.showAddMedium = !post.showAddMedium;
    post.mediumSide = side; // Store which side the medium should be added to
  }

  toggleEditMedium(post: PostDTO): void {
    post.showAddMedium = !post.showAddMedium;
    post.mediumSide = 'edit'; // Set side to 'edit' for editing existing medium
  }

  onFileSelected(event: any, post: PostDTO): void {
    this.selectedFile = event.target.files[0];
  }

  submitAddMedium(post: PostDTO): void {
    if (!this.selectedFile) {
      alert("Please select a file to upload.");
      return;
    }

    const mediumDTO: MediumDTO = {
      file: this.selectedFile,
      postId: post.id,
      mediumType: this.selectedFile.type
    };

    if (post.mediumSide === 'edit') {
      this.postService.updateMedium(post.id, post.currentMediumIndex, mediumDTO).subscribe(updatedPost => {
        this.loadMediaForPost(updatedPost);
      });
    } else {
      const index = post.mediumSide === 'left' ? post.currentMediumIndex - 1 : post.currentMediumIndex + 1;
      this.postService.addMedium(post.id, index, mediumDTO).subscribe(updatedPost => {
        this.loadMediaForPost(post);
      });
    }
  }

  deleteMedium(post: PostDTO, index: number): void {
    const mediumDTO: MediumDTO = {
      id: post.media[index].id,
      postId: post.id,
      mediumType: post.media[index].mediumType,
      mediumUrl: post.media[index].mediumUrl
    };
  
    this.postService.deleteMedium(mediumDTO, index).subscribe({
      next: () => {
        post.media.splice(index, 1);
      },
      error: error => {
        this.toastr.error('Error deleting medium');
        console.error('Error deleting medium:', error);
      }
    });
  }
}
