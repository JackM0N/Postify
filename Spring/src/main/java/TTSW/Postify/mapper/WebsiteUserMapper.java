package TTSW.Postify.mapper;

import TTSW.Postify.dto.WebsiteUserDTO;
import TTSW.Postify.model.WebsiteUser;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface WebsiteUserMapper {
    WebsiteUser toEntity(WebsiteUserDTO websiteUserDTO);

    WebsiteUserDTO toDto(WebsiteUser websiteUser);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    WebsiteUser partialUpdate(WebsiteUserDTO websiteUserDTO, @MappingTarget WebsiteUser websiteUser);
}