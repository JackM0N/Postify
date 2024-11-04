package TTSW.Postify.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
@AllArgsConstructor
public class MediumBase64DTO {
    private String base64Data;
    private String type;
}
