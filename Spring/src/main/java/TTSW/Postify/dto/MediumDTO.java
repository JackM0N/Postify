package TTSW.Postify.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link TTSW.Postify.model.Medium}
 */
@Value
public class MediumDTO implements Serializable {
    Long id;

    @NotNull
    PostDTO post;

    @NotNull
    @Size(max = 255)
    String mediumUrl;

    @NotNull
    @Size(max = 50)
    String mediumType;
}