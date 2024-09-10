package TTSW.Postify.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for {@link TTSW.Postify.model.Post}
 */
@Data
@Getter
@Setter
public class PostDTO implements Serializable {
    Long id;
    @NotNull
    WebsiteUserDTO user;
    String description;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    LocalDateTime deletedAt;
    Set<CommentDTO> comments;
    Set<HashtagDTO> hashtags;
    Set<MediumDTO> media;
    Set<NotificationDTO> notifications;
    Set<PostLikeDTO> postLikes;
}