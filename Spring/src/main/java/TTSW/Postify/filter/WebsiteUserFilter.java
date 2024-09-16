package TTSW.Postify.filter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class WebsiteUserFilter {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate joinDateFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate joinDateTo;
    List<Long> roleIds;
    String searchText;
    Boolean isDeleted;
}
