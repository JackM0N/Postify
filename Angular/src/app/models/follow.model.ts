import { SimplifiedUserDTO } from "./simplified-user.model";

export interface FollowDTO{
  id?: number;
  follower?: {username: string}
  followed?: {id: number, username: string};
  createdAt?: []
}