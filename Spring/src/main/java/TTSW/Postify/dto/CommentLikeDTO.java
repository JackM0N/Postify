package TTSW.Postify.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * DTO for {@link TTSW.Postify.model.CommentLike}
 */
@Data
@Getter
@Setter
public class CommentLikeDTO implements Serializable {
    Long id;

    @NotNull
    WebsiteUserDTO user;

    @NotNull
    CommentDTO comment;
}