export interface PostDTO {
    id: number;
    user: WebsiteUserDTO;
    description: string;
    createdAt: [];
    commentCount: number;
    likeCount: number;
    media: MediumDTO[];
  }
  
  export interface WebsiteUserDTO {
    id: number;
    username: string;
  }
  
  export interface MediumDTO {
    id: number;
    type: string; // e.g., "image" or "video"
    url: string;  // Path to media
  }