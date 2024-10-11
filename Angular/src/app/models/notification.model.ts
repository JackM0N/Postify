export interface NotificationDTO {
    id: number;
    user: string;
    triggeredBy: string;
    notificationType: string;
    postId?: number;
    commentId?: number;
    isRead: boolean;
    createdAt: Date;
  }