import { SimplifiedUserDTO } from "./simplified-user.model";

export interface CommentDTO{
  id: number;
  user: SimplifiedUserDTO;
  postId: number;
  parentCommentId: number;
  text: string;
  createdAt: string;
  updatedAt: string;
}
