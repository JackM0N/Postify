import { Component, OnInit } from '@angular/core';
import { WebsiteUserDTO } from '../../../models/website-user.model';
import { WebsiteUserService } from '../../../services/website-user.service';
import { formatDateTimeArray } from '../../../util/formatDate';
import { Router } from '@angular/router';
import { MediumBase64DTO } from '../../../models/medium-base64.model';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-profile',
  templateUrl: './account.component.html',
  styleUrls: ['../../../styles/profile.component.css']
})
export class AccountComponent implements OnInit {
  protected account: WebsiteUserDTO | null = null;
  protected formatDateTimeArray = formatDateTimeArray;
  protected profilePictureUrl: string | null = null;

  constructor(
    private websiteUserService: WebsiteUserService,
    private router: Router,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.loadUserAccount();
  }

  loadUserAccount(): void {
    this.websiteUserService.getAccount().subscribe({
      next: response => {
        this.account = response;
        this.loadProfilePicture(response.id);
      },
      error: error => {
        this.toastr.error('Failed to load account info');
        console.error('Failed to load account info:', error);
      }
    });
  }

  loadProfilePicture(userId: number): void {
    this.websiteUserService.getProfilePicture(userId).subscribe({
      next: (response: MediumBase64DTO) => {
        if (response && response.base64Data && response.type) {
          this.profilePictureUrl = `data:${response.type};base64,${response.base64Data}`;
        }
      },
      error: error => {
        this.toastr.error('Failed to load profile picture');
        console.error('Failed to load profile picture:', error);
      }
    });
  }
  
  navigateToEdit(): void {
    this.router.navigate(['/account/edit']);
  }
}
