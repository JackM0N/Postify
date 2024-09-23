package TTSW.Postify.service;

import TTSW.Postify.interfaces.HasAuthor;
import TTSW.Postify.model.WebsiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationService {
    private final WebsiteUserService websiteUserService;

    public boolean canModifyEntity(HasAuthor entity) {
        WebsiteUser currentUser = websiteUserService.getCurrentUser();
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> "ADMIN".equals(role.getRoleName()));
        boolean isAuthor = currentUser.equals(entity.getUser());
        return isAdmin || isAuthor;
    }
}
