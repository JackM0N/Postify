import { Role } from "./role.enum";

export interface SimplifiedUserDTO{
  id: number;
  username: string;
  fullName: string;
  bio: string;
  profilePicture: string;
  roles: Role[];
  joindate: string;
}
