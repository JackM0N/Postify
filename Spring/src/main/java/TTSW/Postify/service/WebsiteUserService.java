package TTSW.Postify.service;

import TTSW.Postify.dto.WebsiteUserDTO;
import TTSW.Postify.filter.WebsiteUserFilter;
import TTSW.Postify.mapper.WebsiteUserMapper;
import TTSW.Postify.model.Role;
import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.repository.WebsiteUserRepository;
import TTSW.Postify.security.IAuthenticationFacade;
import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WebsiteUserService {
    private final WebsiteUserRepository websiteUserRepository;
    private final IAuthenticationFacade authenticationFacade;
    private final WebsiteUserMapper websiteUserMapper;

    @Value("${directory.media.profilePictures}")
    private final String mediaDirectory = "../Media/profilePictures/";

    public Page<WebsiteUserDTO> getWebsiteUsers(WebsiteUserFilter websiteUserFilter, Pageable pageable) {
        Specification<WebsiteUser> spec = Specification.where(null);
        if (websiteUserFilter.getJoinDateFrom() != null) {
            spec = spec.and((root, query, builder) -> builder
                    .greaterThanOrEqualTo(root.get("joinDate"), websiteUserFilter.getJoinDateFrom()));
        }
        if (websiteUserFilter.getJoinDateTo() != null) {
            spec = spec.and((root, query, builder) -> builder
                    .lessThanOrEqualTo(root.get("joinDate"), websiteUserFilter.getJoinDateTo()));
        }
        if (websiteUserFilter.getRoleIds() != null && !websiteUserFilter.getRoleIds().isEmpty()) {
            spec = spec.and((root, query, builder) -> {
                Join<WebsiteUser, Role> rolesJoin = root.join("roles");
                return rolesJoin.get("id").in(websiteUserFilter.getRoleIds());
            });
        }
        if (websiteUserFilter.getSearchText() != null
                && !websiteUserFilter.getSearchText().isEmpty()
                && !isNumeric(websiteUserFilter.getSearchText() )) {
            String likePattern = "%" + websiteUserFilter.getSearchText().toLowerCase() + "%";
            spec = spec.and((root, query, builder) -> builder.or(
                    builder.like(builder.lower(root.get("username")), likePattern),
                    builder.like(builder.lower(root.get("fullName")), likePattern),
                    builder.like(builder.lower(root.get("email")), likePattern)
            ));
        }
        if (websiteUserFilter.getSearchText() != null
                && !websiteUserFilter.getSearchText().isEmpty()
                && isNumeric(websiteUserFilter.getSearchText())) {
            spec = spec.and((root, query, builder) -> builder
                    .equal(root.get("id"), Long.parseLong(websiteUserFilter.getSearchText())));
        }
        Page<WebsiteUser> websiteUsers = websiteUserRepository.findAll(spec, pageable);
        return websiteUsers.map(websiteUserMapper::toDto);
    }

    public WebsiteUserDTO getWebsiteUser(String username) {
        WebsiteUser websiteUser = websiteUserRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid username"));
        return websiteUserMapper.toDtoWithoutSensitiveInfo(websiteUser);
    }

    public WebsiteUserDTO editWebsiteUser(WebsiteUserDTO websiteUserDTO) throws IOException {
        WebsiteUser websiteUser = getCurrentUser();
        websiteUser = websiteUserMapper.partialUpdate(websiteUserDTO, websiteUser);
        if (websiteUserDTO.getProfilePicture() != null) {
            MultipartFile file = websiteUserDTO.getProfilePicture();
            String filename = "pfp_" + websiteUser.getUsername() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(mediaDirectory, filename);
            Files.copy(file.getInputStream(), filePath);
            websiteUser.setProfilePictureUrl(filePath.toString());
        }
        websiteUser = websiteUserRepository.save(websiteUser);
        return websiteUserMapper.toDtoWithoutSensitiveInfo(websiteUser);
    }

    public boolean deleteWebsiteUser(Long id){
        WebsiteUser websiteUser = websiteUserRepository.findById(id)
                .orElseThrow(() -> new BadCredentialsException("User not found"));
        websiteUser.setDeletedAt(LocalDateTime.now());
        websiteUserRepository.save(websiteUser);
        return true;
    }

    public WebsiteUser getCurrentUser() {
        Authentication authentication = authenticationFacade.getAuthentication();
        String username = authentication.getName();
        return websiteUserRepository.findByEmail(username)
                .orElseThrow(() -> new BadCredentialsException("You are not logged in"));
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Long.parseLong(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
