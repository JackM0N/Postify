package TTSW.Postify.mapper;

import TTSW.Postify.dto.HashtagDTO;
import TTSW.Postify.model.Hashtag;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface HashtagMapper {
    Hashtag toEntity(HashtagDTO hashtagDTO);

    HashtagDTO toDto(Hashtag hashtag);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Hashtag partialUpdate(HashtagDTO hashtagDTO, @MappingTarget Hashtag hashtag);
}