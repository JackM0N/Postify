package TTSW.Postify.mapper;

import TTSW.Postify.dto.PostDTO;
import TTSW.Postify.model.Hashtag;
import TTSW.Postify.model.Post;
import org.mapstruct.*;
import java.util.HashSet;
import java.util.Set;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {MediumMapper.class, PostLikeMapper.class, HashtagMapper.class,SimplifiedWebsiteUserMapper.class})
public interface PostMapper {
    @Mapping(target = "user", source =  "user")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "hashtags", source = "hashtags")
    @Mapping(target = "commentCount", constant = "0L")
    @Mapping(target = "likeCount", constant = "0L")
    Post toEntity(PostDTO postDTO);

    @AfterMapping
    default void linkHashtags(@MappingTarget Post post) {
        if (post != null && post.getHashtags() != null) {
            post.getHashtags().forEach(hashtag -> hashtag.setPost(post));
        }
    }

    default void updateHashtags(PostDTO postDTO, @MappingTarget Post post) {
        if (postDTO.getHashtags() != null) {
            Set<String> existingHashtags = new HashSet<>();
            post.getHashtags().forEach(existingHashtag -> existingHashtags.add(existingHashtag.getHashtag()));

            postDTO.getHashtags().forEach(dtoHashtag -> {
                String newHashtag = dtoHashtag.getHashtag();
                if (!existingHashtags.contains(newHashtag)) {
                    System.out.println("1");
                    Hashtag hashtag = new Hashtag();
                    hashtag.setHashtag(newHashtag);
                    hashtag.setPost(post);

                    post.getHashtags().add(hashtag);
                }
            });
        }
    }

    @AfterMapping
    default void linkMedia(@MappingTarget Post post) {
        post.getMedia().forEach(media -> media.setPost(post));
    }

//    @AfterMapping
//    default void linkPostLikes(@MappingTarget Post post) {
//        post.getPostLikesIds().forEach(postLike -> postLike.setPost(post));
//    }

    @Mapping(source = "postLikes", target = "postLikesIds")
    PostDTO toDto(Post post);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "description", target = "description")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "hashtags", ignore = true)
    Post partialUpdate(PostDTO postDTO, @MappingTarget Post post);
}
