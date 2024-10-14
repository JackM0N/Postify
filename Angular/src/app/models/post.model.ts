import { SimplifiedUserDTO } from "./simplified-user.model";

export interface PostDTO {
    id: number;
    user: SimplifiedUserDTO;
    description: string;
    createdAt: [];
    commentCount: number;
    likeCount: number;
    media?: { url: string; type: string }[];
    currentMediumIndex?: number;
  }