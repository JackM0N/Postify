package TTSW.Postify.service;

import TTSW.Postify.model.PostLike;
import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final WebsiteUserService websiteUserService;
    private final PostLikeRepository postLikeRepository;

    public Boolean likePost(Long postId){
        WebsiteUser currentUser = websiteUserService.getCurrentUser();
        PostLike likeExists = postLikeRepository.findByUserIdAndPostId(currentUser.getId(), postId)
                .orElse(null);
        if(likeExists != null){
           postLikeRepository.delete(likeExists);
           return false;
        }
        PostLike postLike = new PostLike();
        postLike.setId(postId);
        postLike.setUser(currentUser);
        postLikeRepository.save(postLike);
        return true;
    }
}
