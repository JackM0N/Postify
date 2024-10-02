package TTSW.Postify;

import TTSW.Postify.model.Comment;
import TTSW.Postify.repository.CommentRepository;
import TTSW.Postify.service.AuthorizationService;
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

    @Test
    @WithMockUser("jane@example.com")
    void testCanModifyEntity_Success() {
        Comment comment = commentRepository.findById(1L).orElseThrow();
        boolean canModify = authorizationService.canModifyEntity(comment);

        assertTrue(canModify);
    }

    @Test
    @WithMockUser("testadmin@localhost")
    void testCanModifyEntity_Success_Admin() {
        Comment comment = commentRepository.findById(1L).orElseThrow();
        boolean canModify = authorizationService.canModifyEntity(comment);

        assertTrue(canModify);
    }

    @Test
    @WithMockUser("john@example.com")
    void testCanModifyEntity_NotAuthor() {
        Comment comment = commentRepository.findById(1L).orElseThrow();
        boolean canModify = authorizationService.canModifyEntity(comment);

        assertFalse(canModify);
    }
}


