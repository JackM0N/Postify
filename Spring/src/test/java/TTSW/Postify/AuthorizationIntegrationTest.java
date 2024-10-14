package TTSW.Postify;

import TTSW.Postify.model.Comment;
import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.repository.CommentRepository;
import TTSW.Postify.service.AuthorizationService;
import TTSW.Postify.service.WebsiteUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
public class AuthorizationIntegrationTest {

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private WebsiteUserService websiteUserService;

    private Comment comment;

    @BeforeEach
    void setUp() {
        comment = commentRepository.findById(1L).orElseThrow();
    }

    @Test
    @WithMockUser("jane@example.com")
    void testCanModifyEntity_Success() {
        WebsiteUser author = websiteUserService.getCurrentUser();
        assertEquals(comment.getUser().getUsername(), author.getUsername());
        boolean canModify = authorizationService.canModifyEntity(comment);

        assertTrue(canModify);
    }

    @Test
    @WithMockUser("testadmin@localhost")
    void testCanModifyEntity_Success_Admin() {
        WebsiteUser author = websiteUserService.getCurrentUser();
        assertNotEquals(comment.getUser().getUsername(), author.getUsername());
        boolean canModify = authorizationService.canModifyEntity(comment);

        assertTrue(canModify);
    }

    @Test
    @WithMockUser("john@example.com")
    void testCanModifyEntity_NotAuthor() {
        WebsiteUser author = websiteUserService.getCurrentUser();
        assertNotEquals(comment.getUser().getUsername(), author.getUsername());
        boolean canModify = authorizationService.canModifyEntity(comment);

        assertFalse(canModify);
    }
}


