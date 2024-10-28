package TTSW.Postify.mapper;

import TTSW.Postify.dto.WebsiteUserDTO;
import TTSW.Postify.enums.Role;
import TTSW.Postify.model.UserRole;
import TTSW.Postify.model.WebsiteUser;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface WebsiteUserMapper {
    @Mapping(target = "joinDate", expression = "java(java.time.LocalDateTime.now())")
    WebsiteUser toEntity(WebsiteUserDTO websiteUserDTO);

    @Mapping(target = "profilePicture", ignore = true)
    @Mapping(target = "roles", source = "userRoles")
    WebsiteUserDTO toDto(WebsiteUser websiteUser);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "profilePicture", ignore = true)
    @Mapping(target = "roles", source = "userRoles")
    WebsiteUserDTO toDtoWithoutSensitiveInfo(WebsiteUser websiteUser);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "joinDate", ignore = true)
    @Mapping(target = "profilePictureUrl", ignore = true)
    WebsiteUser partialUpdate(WebsiteUserDTO websiteUserDTO, @MappingTarget WebsiteUser websiteUser);

    default List<Role> mapUserRolesToRoles(List<UserRole> userRoles) {
        return userRoles.stream()
                .map(UserRole::getRoleName)
                .collect(Collectors.toList());
    }
}