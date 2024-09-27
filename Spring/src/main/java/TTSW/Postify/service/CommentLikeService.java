package TTSW.Postify.service;

import TTSW.Postify.model.CommentLike;
import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.repository.CommentLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentLikeService {
    private final CommentLikeRepository commentLikeRepository;
    private final WebsiteUserService websiteUserService;

    public Boolean likeComment(Long commentId) {
        WebsiteUser currentUser = websiteUserService.getCurrentUser();
        CommentLike likeExists = commentLikeRepository.findByUserIdAndCommentId(currentUser.getId(), commentId)
                .orElse(null);
        if(likeExists != null){
            commentLikeRepository.delete(likeExists);
            return false;
        }
        CommentLike like = new CommentLike();
        like.setId(commentId);
        like.setUser(currentUser);
        commentLikeRepository.save(like);
        return true;
    }
}
