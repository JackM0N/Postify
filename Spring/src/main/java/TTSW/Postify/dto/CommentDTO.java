package TTSW.Postify.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link TTSW.Postify.model.Comment}
 */
@Data
@Getter
@Setter
public class CommentDTO implements Serializable {
    Long id;

    @NotNull
    SimplifiedWebsiteUserDTO user;

    @NotNull
    Long postId;

    @NotNull
    Long parentCommentId;

    @NotNull
    String text;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;
}