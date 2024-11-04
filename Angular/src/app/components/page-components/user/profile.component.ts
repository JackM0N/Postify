import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { WebsiteUserDTO } from '../../../models/website-user.model';
import { WebsiteUserService } from '../../../services/website-user.service';
import { formatDateTimeArray } from '../../../util/formatDate';
import { MediumBase64DTO } from '../../../models/medium-base64.model';

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

  constructor(
    private websiteUserService: WebsiteUserService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.username = this.route.snapshot.paramMap.get('username');
    if (this.username) {
      this.loadUserProfile(this.username);
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
  
  navigateToEdit(): void {
    this.router.navigate(['/account/edit']);
  }
}
