package TTSW.Postify.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * DTO for {@link TTSW.Postify.model.Role}
 */
@Data
@Setter
@Getter
public class RoleDTO implements Serializable {
    Long id;
    @Size(max = 50)
    String roleName;
}