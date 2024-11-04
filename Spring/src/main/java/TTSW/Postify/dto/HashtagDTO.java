package TTSW.Postify.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

/**
 * DTO for {@link TTSW.Postify.model.Hashtag}
 */
@Data
@Setter
@Getter
public class HashtagDTO implements Serializable {
    Long id;

    @NotNull
    @Size(max = 100)
    String hashtag;

    @NotNull
    Long postId;
}
