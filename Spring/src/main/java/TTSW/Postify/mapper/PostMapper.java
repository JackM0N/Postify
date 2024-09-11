package TTSW.Postify.mapper;

import TTSW.Postify.dto.PostDTO;
import TTSW.Postify.model.Post;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PostMapper {
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Post toEntity(PostDTO postDTO);

    @AfterMapping
    default void linkComments(@MappingTarget Post post) {
        post.getComments().forEach(comment -> comment.setPost(post));
    }

    @AfterMapping
    default void linkHashtags(@MappingTarget Post post) {
        post.getHashtags().forEach(hashtag -> hashtag.setPost(post));
    }

    @AfterMapping
    default void linkMedia(@MappingTarget Post post) {
        post.getMedia().forEach(media -> media.setPost(post));
    }

    @AfterMapping
    default void linkNotifications(@MappingTarget Post post) {
        post.getNotifications().forEach(notification -> notification.setPost(post));
    }

    @AfterMapping
    default void linkPostLikes(@MappingTarget Post post) {
        post.getPostLikes().forEach(postLike -> postLike.setPost(post));
    }

    PostDTO toDto(Post post);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "description", target = "description")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Post partialUpdate(PostDTO postDTO, @MappingTarget Post post);
}