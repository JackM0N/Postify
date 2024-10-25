import { Role } from "./role.enum";

export interface WebsiteUserDTO {
    id: number;
    username: string;
    email: string;
    fullName: string;
    bio: string;
    profilePictureUrl: string;
    roles: Role[];
    joinDate: number[];
  }