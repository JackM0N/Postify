import { Component, OnInit } from '@angular/core';
import { NotificationService } from '../../../services/notification.service';
import { NotificationDTO } from '../../../models/notification.model';
import { formatDateTime } from '../../../util/formatDate';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-notifications',
  templateUrl: './notification.component.html',
  styleUrls: ['../../../styles/notification.component.css']
})
export class NotificationsComponent implements OnInit {
  protected notifications: NotificationDTO[] = [];
  protected formatDateTime = formatDateTime;
  private page = 0;
  private size = 10;

  constructor(
    private notificationService: NotificationService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.loadNotifications();
  }

  loadNotifications(): void {
    this.notificationService.getNotifications(this.page, this.size).subscribe({
      next: data => {
        this.notifications = data.content;
      },
      error: error => {
        this.toastr.error('Error fetching notifications');
        console.error('Error fetching notifications:', error);
      }
    });
  }
}
