package TTSW.Postify.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link TTSW.Postify.model.Follow}
 */
@Data
@Getter
@Setter
public class FollowDTO implements Serializable {
    Long id;

    @NotNull
    SimplifiedWebsiteUserDTO follower;

    @NotNull
    SimplifiedWebsiteUserDTO followed;

    LocalDateTime createdAt;
}