// followed-users.component.ts
import { Component, OnInit } from '@angular/core';
import { FollowedUsersService } from '../../../services/followers.service';
import { SimplifiedUserDTO } from '../../../models/simplified-user.model';
import { WebsiteUserService } from '../../../services/website-user.service';
import { MediumBase64DTO } from '../../../models/medium-base64.model';

@Component({
  selector: 'app-followed-users',
  templateUrl: './followed.component.html',
  styleUrls: ['../../../styles/followed.component.css']
})
export class FollowedUsersComponent implements OnInit {
  users: SimplifiedUserDTO[] = [];

  constructor(
    private followedUsersService: FollowedUsersService, 
    private websiteUserService: WebsiteUserService
  ) {}

  ngOnInit(): void {
    this.loadFollowedUsers();
  }

  loadFollowedUsers(): void {
    this.followedUsersService.getFollowedUsers('', 0, 10).subscribe((users) => {
      users.forEach((user) => {
        this.loadProfilePicture(user);
      });
    });
  }

  loadProfilePicture(user: SimplifiedUserDTO): void {
    this.websiteUserService.getProfilePicture(user.id).subscribe({
      next: (response: MediumBase64DTO) => {
        if (response && response.base64Data && response.type) {
          user.profilePicture = `data:${response.type};base64,${response.base64Data}`;
        }
      },
      error: error => {
        console.error(`Failed to load profile picture for user ${user.id}:`, error);
      }
    });
    this.users.push(user);
  }
}
