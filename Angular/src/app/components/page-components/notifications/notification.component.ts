import { Component, OnInit } from '@angular/core';
import { NotificationService } from '../../../services/notification.service';
import { NotificationDTO } from '../../../models/notification.model';
import { formatDateTime } from '../../../Util/formatDate';

@Component({
  selector: 'app-notifications',
  templateUrl: './notification.component.html',
  styleUrls: ['../../../styles/notification.component.css']
})
export class NotificationsComponent implements OnInit {
  notifications: NotificationDTO[] = [];
  protected formatDateTime = formatDateTime;
  page = 0;
  size = 10;

  constructor(private notificationService: NotificationService) {}

  ngOnInit(): void {
    this.loadNotifications();
  }

  loadNotifications(): void {
    this.notificationService.getNotifications(this.page, this.size).subscribe(
      (data) => {
        this.notifications = data.content;
      },
      (error) => {
        console.error('Error fetching notifications:', error);
      }
    );
  }
}