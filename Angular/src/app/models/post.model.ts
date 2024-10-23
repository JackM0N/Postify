import { HashtagDTO } from "./hashtag.model";
import { SimplifiedUserDTO } from "./simplified-user.model";

export interface PostDTO {
  showAddMedium: boolean;
  mediumSide: string;
  showComments: boolean;
  comments: any;
  id: number;
  user: SimplifiedUserDTO;
  description: string;
  hashtags?: HashtagDTO[];
  createdAt: [];
  commentCount: number;
  likeCount: number;
  media: { id: number | undefined; url: string; type: string } [];
  currentMediumIndex: number;
  isLiked?: boolean;
}
