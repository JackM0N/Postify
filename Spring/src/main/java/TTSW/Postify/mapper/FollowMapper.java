package TTSW.Postify.mapper;

import TTSW.Postify.dto.FollowDTO;
import TTSW.Postify.model.Follow;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface FollowMapper {
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    Follow toEntity(FollowDTO followDTO);

    FollowDTO toDto(Follow follow);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Follow partialUpdate(FollowDTO followDTO, @MappingTarget Follow follow);
}