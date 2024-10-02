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
  protected formatDateTimeArray = formatDateTimeArray;

  constructor(private postService: PostService) {}

  ngOnInit(): void {
    this.loadPosts();
  }

  loadPosts(): void {
    this.postService.getPosts({}, {}).subscribe(data => {
        console.log('Loaded posts:', data);
        this.posts = data.content
      }, error => {
        console.error('Error loading posts:', error);
      });
  }
}