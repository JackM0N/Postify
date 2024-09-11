package TTSW.Postify.mapper;

import TTSW.Postify.dto.MediumDTO;
import TTSW.Postify.model.Medium;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface MediumMapper {
    Medium toEntity(MediumDTO mediumDTO);

    MediumDTO toDto(Medium medium);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Medium partialUpdate(MediumDTO mediumDTO, @MappingTarget Medium medium);
}