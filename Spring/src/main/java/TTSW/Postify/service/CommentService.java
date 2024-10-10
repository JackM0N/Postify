package TTSW.Postify.service;

import TTSW.Postify.dto.CommentDTO;
import TTSW.Postify.enums.NotificationType;
import TTSW.Postify.mapper.CommentMapper;
import TTSW.Postify.model.Comment;
import TTSW.Postify.model.Notification;
import TTSW.Postify.model.Post;
import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.repository.CommentRepository;
import TTSW.Postify.repository.NotificationRepository;
import TTSW.Postify.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final WebsiteUserService websiteUserService;
    private final PostRepository postRepository;
    private final AuthorizationService authorizationService;
    private final NotificationRepository notificationRepository;

    public Page<CommentDTO> getAllCommentsForPost(Long postId, Pageable pageable) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        Specification<Comment> specification = (root, query, builder) ->
                builder.equal(root.get("post"), post);
        Page<Comment> comments = commentRepository.findAll(specification, pageable);
        return comments.map(commentMapper::toDto);
    }

    public CommentDTO createComment(CommentDTO commentDTO) {
        WebsiteUser currentUser = websiteUserService.getCurrentUser();
        Post post = postRepository.findById(commentDTO.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        Comment comment = commentMapper.toEntity(commentDTO);
        comment.setUser(currentUser);
        comment.setPost(post);

        Notification notification = new Notification();
        notification.setComment(comment);
        notification.setTriggeredBy(currentUser);
        notification.setNotificationType(NotificationType.COMMENT);
        notification.setUser(post.getUser());
        notification.setCreatedAt(LocalDateTime.now());

        if (comment.getParentComment() != null) {
            comment.setParentComment(commentRepository.findById(commentDTO.getParentComment().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent comment not found")));
            notification.setUser(comment.getParentComment().getUser());
        }

        notificationRepository.save(notification);
        commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }

    public CommentDTO updateComment(CommentDTO commentDTO) {
        WebsiteUser currentUser = websiteUserService.getCurrentUser();
        Comment comment = commentRepository.findById(commentDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        if (currentUser.equals(comment.getUser())) {
            commentMapper.partialUpdate(commentDTO, comment);
            commentRepository.save(comment);
            return commentMapper.toDto(comment);
        } else {
            throw new BadCredentialsException("You can only edit your own comments");
        }
    }

    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        if (authorizationService.canModifyEntity(comment)) {
            commentRepository.delete(comment);
        } else {
            throw new BadCredentialsException("You dont have permission to delete this comment");
        }
    }
}
