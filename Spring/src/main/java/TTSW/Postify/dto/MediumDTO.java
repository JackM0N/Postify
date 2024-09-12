package TTSW.Postify.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * DTO for {@link TTSW.Postify.model.Medium}
 */
@Data
@Getter
@Setter
public class MediumDTO implements Serializable {
    Long id;

    @NotNull
    Long postId;

    @NotNull
    @Size(max = 255)
    String mediumUrl;

    @NotNull
    @Size(max = 50)
    String mediumType;

    public MultipartFile file;
}