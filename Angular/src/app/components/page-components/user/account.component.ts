import { Component, OnInit } from '@angular/core';
import { WebsiteUserDTO } from '../../../models/website-user.model';
import { WebsiteUserService } from '../../../services/website-user.service';
import { formatDateTimeArray } from '../../../util/formatDate';
import { Router } from '@angular/router';

@Component({
  selector: 'app-profile',
  templateUrl: './account.component.html',
  styleUrls: ['../../../styles/profile.component.css']
})
export class AccountComponent implements OnInit {
  account: WebsiteUserDTO | null = null;
  protected formatDateTimeArray = formatDateTimeArray;

  constructor(private websiteUserService: WebsiteUserService, private router:Router) {}

  ngOnInit(): void {
    this.loadUserAccount();
  }

  loadUserAccount(): void {
    this.websiteUserService.getAccount().subscribe(
      (data) => {
        this.account = data;
      },
      (error) => {
        console.error('Failed to load account info:', error);
      }
    );
  }

  navigateToEdit(): void {
    this.router.navigate(['/account/edit']);
  }
}