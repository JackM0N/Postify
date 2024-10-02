package TTSW.Postify;

import TTSW.Postify.enums.Role;
import TTSW.Postify.model.Comment;
import TTSW.Postify.model.UserRole;
import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.service.AuthorizationService;
import TTSW.Postify.service.WebsiteUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class AuthorizationUnitTest {

    @Mock
    private WebsiteUserService websiteUserService;

    @InjectMocks
    private AuthorizationService authorizationService;

    private WebsiteUser author;
    private Comment comment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        author = new WebsiteUser();
        author.setId(1L);
        author.setUsername("john_doe");

        comment = new Comment();
        comment.setId(1L);
        comment.setUser(author);
    }

    @Test
    void testCanModifyEntity_Success() {
        when(websiteUserService.getCurrentUser()).thenReturn(author);

        boolean result = authorizationService.canModifyEntity(comment);

        assertTrue(result);
    }

    @Test
    void testCanModifyEntity_Success_Admin() {
        WebsiteUser admin = new WebsiteUser();
        admin.setId(2L);
        admin.setUsername("testadmin");
        UserRole adminRole = new UserRole();
        adminRole.setId(1L);
        adminRole.setRoleName(Role.ADMIN);
        admin.setUserRoles(List.of(adminRole));

        when(websiteUserService.getCurrentUser()).thenReturn(admin);

        boolean result = authorizationService.canModifyEntity(comment);

        assertTrue(result);
    }

    @Test
    void testCanModifyEntity_NotAuthor() {
        WebsiteUser notAuthor = new WebsiteUser();
        notAuthor.setId(3L);
        notAuthor.setUsername("jane_smith");

        when(websiteUserService.getCurrentUser()).thenReturn(notAuthor);

        boolean result = authorizationService.canModifyEntity(comment);

        assertFalse(result);
    }
}
