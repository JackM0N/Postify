package TTSW.Postify.mapper;

import TTSW.Postify.dto.WebsiteUserDTO;
import TTSW.Postify.model.WebsiteUser;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface WebsiteUserMapper {
    @Mapping(target = "joinDate", expression = "java(java.time.LocalDateTime.now())")
    WebsiteUser toEntity(WebsiteUserDTO websiteUserDTO);

    @Mapping(target = "profilePicture", ignore = true)
    WebsiteUserDTO toDto(WebsiteUser websiteUser);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "profilePicture", ignore = true)
    WebsiteUserDTO toDtoWithoutSensitiveInfo(WebsiteUser websiteUser);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "joinDate", ignore = true)
    @Mapping(target = "profilePictureUrl", ignore = true)
    WebsiteUser partialUpdate(WebsiteUserDTO websiteUserDTO, @MappingTarget WebsiteUser websiteUser);
}