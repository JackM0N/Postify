package TTSW.Postify.service;

import TTSW.Postify.dto.HashtagDTO;
import TTSW.Postify.mapper.HashtagMapper;
import TTSW.Postify.model.Hashtag;
import TTSW.Postify.model.Post;
import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.repository.HashtagRepository;
import TTSW.Postify.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HashtagService {
    private final HashtagRepository hashtagRepository;
    private final HashtagMapper hashtagMapper;
    private final PostRepository postRepository;
    private final WebsiteUserService websiteUserService;

    //For individual hashtag updating and deletion
    public HashtagDTO updateHashtag(Long postId, HashtagDTO hashtagDTO) {
        WebsiteUser currentUser = websiteUserService.getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        if (post.getUser().equals(currentUser)) {
            Hashtag hashtag = hashtagRepository.findById(hashtagDTO.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Hashtag not found"));
            if (hashtag.getPost().equals(post)) {
                hashtag.setHashtag(hashtagDTO.getHashtag());
                hashtagRepository.save(hashtag);
                return hashtagMapper.toDto(hashtag);
            }
        }
        throw new BadCredentialsException("You cannot update this hashtag");

    }

    public void deleteHashtag(Long postId, Long hashtagId) {
        WebsiteUser currentUser = websiteUserService.getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        if (post.getUser().equals(currentUser)) {
            Hashtag hashtag = hashtagRepository.findById(hashtagId)
                    .orElseThrow(() -> new EntityNotFoundException("Hashtag not found"));
            if (hashtag.getPost().equals(post)) {
                hashtagRepository.delete(hashtag);
            }
        }
        throw new BadCredentialsException("You cannot delete this hashtag");
    }
}
