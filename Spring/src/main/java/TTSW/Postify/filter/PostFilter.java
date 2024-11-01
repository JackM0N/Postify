package TTSW.Postify.filter;

import TTSW.Postify.model.Hashtag;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.List;

@Setter
@Getter
public class PostFilter {
    Long userId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    Long createdAtForm;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    Long createdAtTo;

    List<Hashtag> inclusiveHashtags;
    List<Hashtag> exclusiveHashtags;
    List<Hashtag> negativeHashtags;
}
