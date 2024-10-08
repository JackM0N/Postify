export interface PostDTO {
    id: number;
    user: WebsiteUserDTO;
    description: string;
    createdAt: [];
    commentCount: number;
    likeCount: number;
    media?: { url: string; type: string }[];
    currentMediumIndex?: number;
  }
  
  export interface WebsiteUserDTO {
    id: number;
    username: string;
  }