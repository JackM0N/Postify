package TTSW.Postify.mapper;

import TTSW.Postify.dto.PostLikeDTO;
import TTSW.Postify.model.PostLike;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING,
        uses = SimplifiedWebsiteUserMapper.class)
public interface PostLikeMapper {
    @Mapping(target = "user", source = "user")
    PostLike toEntity(PostLikeDTO postLikeDTO);

    PostLikeDTO toDto(PostLike postLike);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PostLike partialUpdate(PostLikeDTO postLikeDTO, @MappingTarget PostLike postLike);

    default Set<Long> mapPostLikesToIds(Set<PostLike> postLikes) {
        if (postLikes == null) {
            return null;
        }
        return postLikes.stream()
                .map(PostLike::getId)
                .collect(Collectors.toSet());
    }
}