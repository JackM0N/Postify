import { Component, OnInit } from '@angular/core';
import { WebsiteUserDTO } from '../../../models/website-user.model';
import { WebsiteUserService } from '../../../services/website-user.service';
import { formatDateTimeArray } from '../../../util/formatDate';

@Component({
  selector: 'app-profile',
  templateUrl: './account.component.html',
  styleUrls: ['../../../styles/profile.component.css']
})
export class AccountComponent implements OnInit {
  account: WebsiteUserDTO | null = null;
  protected formatDateTimeArray = formatDateTimeArray;

  constructor(private websiteUserService: WebsiteUserService) {}

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
}