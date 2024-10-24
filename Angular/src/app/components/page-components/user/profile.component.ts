import { Component, OnInit } from '@angular/core';
import { WebsiteUserDTO } from '../../../models/website-user.model';
import { WebsiteUserService } from '../../../services/website-user.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['../../../styles/profile.component.css']
})
export class ProfileComponent implements OnInit {
  profile: WebsiteUserDTO | null = null;

  constructor(private websiteUserService: WebsiteUserService) {}

  ngOnInit(): void {
    this.loadUserProfile();
  }

  loadUserProfile(): void {
    this.websiteUserService.getMyProfile().subscribe(
      (data) => {
        this.profile = data;
      },
      (error) => {
        console.error('Failed to load profile:', error);
      }
    );
  }
}