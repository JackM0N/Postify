package TTSW.Postify.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link TTSW.Postify.model.Message}
 */
@Data
@Getter
@Setter
public class MessageDTO implements Serializable {
    Long id;

    @NotNull
    WebsiteUserDTO sender;

    @NotNull
    WebsiteUserDTO receiver;

    @NotNull
    String messageText;

    @NotNull
    Boolean isRead;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;
}