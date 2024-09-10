package TTSW.Postify.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for {@link TTSW.Postify.model.WebsiteUser}
 */
@Data
@Setter
@Getter
public class WebsiteUserDTO implements Serializable {
    Long id;
    @Size(max = 50)
    String username;
    @Size(max = 100)
    String email;
    @Size(max = 255)
    String password;
    @Size(max = 100)
    String fullName;
    String bio;
    @Size(max = 255)
    String profilePictureUrl;
    LocalDate joinDate;
    List<RoleDTO> roles;
}