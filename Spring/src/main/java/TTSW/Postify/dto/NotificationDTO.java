package TTSW.Postify.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link TTSW.Postify.model.Notification}
 */
@Data
@Getter
@Setter
public class NotificationDTO implements Serializable {
    Long id;

    @NotNull
    WebsiteUserDTO user;

    @NotNull
    WebsiteUserDTO triggeredBy;

    @NotNull
    @Size(max = 50)
    String notificationType;

    PostDTO post;

    CommentDTO comment;

    @NotNull
    Boolean isRead;

    LocalDateTime createdAt;
}