package TTSW.Postify.dto;

import TTSW.Postify.enums.Role;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
public class SimplifiedWebsiteUserDTO {
    Long id;

    @Size(max = 50)
    String username;

    @Size(max = 100)
    String fullName;

    String bio;

    @Size(max = 255)
    String profilePictureUrl;

    List<Role> roles;

    LocalDateTime joinDate;
}
