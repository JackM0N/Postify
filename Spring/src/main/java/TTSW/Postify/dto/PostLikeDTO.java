package TTSW.Postify.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * DTO for {@link TTSW.Postify.model.PostLike}
 */
@Data
@Getter
@Setter
public class PostLikeDTO implements Serializable {
    Long id;
    @NotNull
    WebsiteUserDTO user;
    @NotNull
    PostDTO post;
}