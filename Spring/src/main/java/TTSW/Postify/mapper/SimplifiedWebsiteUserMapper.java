package TTSW.Postify.mapper;

import TTSW.Postify.dto.SimplifiedWebsiteUserDTO;
import TTSW.Postify.model.WebsiteUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SimplifiedWebsiteUserMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "bio", source = "bio")
    @Mapping(target = "profilePictureUrl", source = "profilePictureUrl")
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "joinDate", source = "joinDate")
    SimplifiedWebsiteUserDTO toDto(WebsiteUser websiteUser);
}
