package TTSW.Postify.service;

import TTSW.Postify.dto.MediumDTO;
import TTSW.Postify.dto.PostDTO;
import TTSW.Postify.filter.PostFilter;
import TTSW.Postify.mapper.PostMapper;
import TTSW.Postify.model.Medium;
import TTSW.Postify.model.Post;
import TTSW.Postify.repository.MediumRepository;
import TTSW.Postify.repository.PostRepository;
import TTSW.Postify.repository.WebsiteUserRepository;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final WebsiteUserRepository websiteUserRepository;
    private final MediumRepository mediumRepository;

    @Value("${directory.media}")
    private final String mediaDirectory = "../Media/";

    public Page<PostDTO> getPosts(PostFilter postFilter,Pageable pageable) {
        Specification<Post> spec = Specification.where(null);
        if (postFilter.getUserId() != null) {
            spec = spec.and((root, query, builder) ->
                    builder.equal(root.get("user").get("id"), postFilter.getUserId()));
        }
        if (postFilter.getCreatedAtForm() != null){
            spec = spec.and((root, query, builder) ->
                    builder.greaterThanOrEqualTo(root.get("createdAt"), postFilter.getCreatedAtForm()));
        }
        if (postFilter.getCreatedAtTo() != null){
            spec = spec.and((root, query, builder) ->
                    builder.lessThanOrEqualTo(root.get("createdAt"), postFilter.getCreatedAtTo()));
        }
        if (postFilter.getExclusiveHashtags() == null) {
            if (postFilter.getInclusiveHashtags() != null) {
                spec = spec.and((root, query, builder) ->
                        builder.isTrue(root.join("hashtags").in(postFilter.getInclusiveHashtags())));
            }
            if (postFilter.getNegativeHashtags() != null) {
                spec = spec.and((root, query, builder) ->
                        builder.not(root.join("hashtags").in(postFilter.getNegativeHashtags())));
            }
        }
        if (postFilter.getExclusiveHashtags() != null){
            spec = spec.and((root, query, builder) -> {
                assert query != null;
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<Post> subRoot = subquery.from(Post.class);
                subquery.select(builder.count(subRoot.get("hashtags")))
                        .where(builder.equal(subRoot, root),
                                builder.in(subRoot.get("hashtags")).value(postFilter.getNegativeHashtags()));

                return builder.equal(subquery, postFilter.getNegativeHashtags().size());
            });
        }
        Page<Post> posts = postRepository.findAll(spec, pageable);
        return posts.map(postMapper::toDto);
    }

    public PostDTO createPost(PostDTO postDTO) throws IOException {
        if (postDTO.getMedia() == null || postDTO.getMedia().isEmpty()) {
            throw new RuntimeException("Post must contain at least one piece of media (photo or video)");
        }

        Post post = postMapper.toEntity(postDTO);
        post.setUser(websiteUserRepository.findById(postDTO.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found")));
        postRepository.save(post);

        String baseDir = mediaDirectory + post.getId();
        File postDir = new File(baseDir);
        if (!postDir.exists()) {
            postDir.mkdirs();
        }

        Set<Medium> media = new LinkedHashSet<>();
        int index = 0;

        for (MediumDTO mediumDTO : postDTO.getMedia()) {
            MultipartFile file = mediumDTO.getFile();
            if (file != null && !file.isEmpty()) {
                String extension = getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
                String filename = "post_" + post.getId() + "_media_" + index + "." + extension;
                String filePath = baseDir + "/" + filename;

                File dest = new File(filePath);
                file.transferTo(dest);

                Medium medium = new Medium();
                medium.setPost(post);
                medium.setMediumType(mediumDTO.getMediumType());
                medium.setMediumUrl(baseDir + "/" + filename);

                media.add(medium);
                index++;
            }
        }
        mediumRepository.saveAll(media);

        return postMapper.toDto(post);
    }

    public PostDTO updatePost(Long id, PostDTO postDTO) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post = postMapper.partialUpdate(postDTO, post);
        postRepository.save(post);
        return postMapper.toDto(post);
    }

    public boolean deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setDeletedAt(LocalDateTime.now());
        postRepository.save(post);
        return true;
    }

        private String getFileExtension(String filename) {
            return filename.substring(filename.lastIndexOf('.') + 1);
        }
}
