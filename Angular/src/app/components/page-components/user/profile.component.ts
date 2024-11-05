import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { WebsiteUserDTO } from '../../../models/website-user.model';
import { WebsiteUserService } from '../../../services/website-user.service';
import { formatDateTimeArray } from '../../../util/formatDate';
import { MediumBase64DTO } from '../../../models/medium-base64.model';
import { FollowedUsersService } from '../../../services/followers.service';
import { FollowDTO } from '../../../models/follow.model';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['../../../styles/profile.component.css']
})
export class ProfileComponent implements OnInit {
  account: WebsiteUserDTO | null = null;
  protected formatDateTimeArray = formatDateTimeArray;
  profilePictureUrl: string | null = null;
  username: string | null = null;
  isFollowing: boolean = false;

  constructor(
    private websiteUserService: WebsiteUserService,
    private route: ActivatedRoute,
    private router: Router,
    private followedUsersService: FollowedUsersService,
    private toastr: ToastrService,
  ) {}

  ngOnInit(): void {
    this.username = this.route.snapshot.paramMap.get('username');
    if (this.username) {
      this.loadUserProfile(this.username);
      this.checkIfFollowing();
    } else {
      console.error('Username not provided in route');
    }
  }

  loadUserProfile(username: string): void {
    this.websiteUserService.getUserProfile(username).subscribe(
      (data) => {
        this.account = data;
        this.loadProfilePicture(data.id);
      },
      (error) => {
        console.error('Failed to load user profile:', error);
      }
    );
  }

  loadProfilePicture(userId: number): void {
    this.websiteUserService.getProfilePicture(userId).subscribe({
      next: (response: MediumBase64DTO) => {
        if (response && response.base64Data && response.type) {
          this.profilePictureUrl = `data:${response.type};base64,${response.base64Data}`;
        } else {
          console.warn("Profile picture data is missing:", response);
        }
      },
      error: error => {
        console.error('Failed to load profile picture:', error);
      }
    });
  }
  

  checkIfFollowing(): void {
    if (this.account && this.account.id) {
      this.followedUsersService.isFollowed(this.account.id).subscribe({
        next: (isFollowed) => {
          this.isFollowing = isFollowed;
        },
        error: () => {
          this.toastr.error('Error checking follow status');
        }
      });
    }
  }

  followUser(): void {
    if (this.account) {
      const followerUsername = this.websiteUserService.getCurrentUsername();


    if (!followerUsername) {
      this.toastr.error('You need to be logged in to follow someone');
      return;
    }

      const followDTO: FollowDTO = {
        follower: {
          username: followerUsername,
        },
        followed: {
          id: this.account.id,
          username: this.account?.username || '',
        }
      };

      this.followedUsersService.followUser(followDTO).subscribe({
        next: () => {
          this.isFollowing = true;
          this.toastr.success(`Started following ${this.account?.username || 'this user'}`);
        },
        error: () => {
          this.toastr.error('Error following user');
        }
      });
    }
  }

  unfollowUser(): void {
    if (this.account) {
      this.followedUsersService.unfollowUser(this.account.username).subscribe({
        next: () => {
          this.isFollowing = false;
          this.toastr.success(`Unfollowed ${this.account?.username || 'this user'}`);
        },
        error: () => {
          this.toastr.error('Error unfollowing user');
        }
      });
    }
  }

  navigateToEdit(): void {
    this.router.navigate(['/account/edit']);
  }
}
