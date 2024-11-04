package TTSW.Postify.mapper;

import TTSW.Postify.dto.SimplifiedWebsiteUserDTO;
import TTSW.Postify.enums.Role;
import TTSW.Postify.model.UserRole;
import TTSW.Postify.model.WebsiteUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SimplifiedWebsiteUserMapper {
    @Mapping(target = "roles", source = "userRoles")
    SimplifiedWebsiteUserDTO toDto(WebsiteUser websiteUser);

    default List<Role> mapRoles(List<UserRole> userRoles) {
        return userRoles.stream()
                .map(UserRole::getRoleName)
                .collect(Collectors.toList());
    }
}
