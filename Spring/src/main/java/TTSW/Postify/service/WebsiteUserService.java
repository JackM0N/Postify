package TTSW.Postify.service;

import TTSW.Postify.dto.WebsiteUserDTO;
import TTSW.Postify.enums.Role;
import TTSW.Postify.filter.WebsiteUserFilter;
import TTSW.Postify.mapper.WebsiteUserMapper;
import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.repository.WebsiteUserRepository;
import TTSW.Postify.security.AuthenticationResponse;
import TTSW.Postify.security.IAuthenticationFacade;
import TTSW.Postify.security.JWTService;
import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
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
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    @Value("${directory.media.profilePictures}")
    private String mediaDirectory = "../Media/profilePictures/";

    public Page<WebsiteUserDTO> getWebsiteUsers(WebsiteUserFilter websiteUserFilter, Pageable pageable) {
        Specification<WebsiteUser> spec = Specification.where(null);
        if (websiteUserFilter.getIsDeleted() != null){
            if (websiteUserFilter.getIsDeleted()){
                spec = spec.and((root, query, builder) -> builder.isNotNull(root.get("deletedAt")));
            } else {
                spec = spec.and((root, query, builder) -> builder.isNull(root.get("deletedAt")));
            }
        }
        if (websiteUserFilter.getJoinDateFrom() != null) {
            spec = spec.and((root, query, builder) -> builder
                    .greaterThanOrEqualTo(root.get("joinDate"), websiteUserFilter.getJoinDateFrom()));
        }
        if (websiteUserFilter.getJoinDateTo() != null) {
            spec = spec.and((root, query, builder) -> builder
                    .lessThanOrEqualTo(root.get("joinDate"), websiteUserFilter.getJoinDateTo()));
        }
        //Role filtering
        if (websiteUserFilter.getRoles() != null && !websiteUserFilter.getRoles().isEmpty()) {
            spec = spec.and((root, query, builder) -> {
                Join<WebsiteUser, Role> rolesJoin = root.join("userRoles");
                return rolesJoin.get("roleName").in(websiteUserFilter.getRoles());
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
                .orElse(null);
        if (websiteUser == null || websiteUser.getDeletedAt() != null) {
            throw new RuntimeException("This user does not exist or got deleted");
        }
        return websiteUserMapper.toDtoWithoutSensitiveInfo(websiteUser);
    }

    public WebsiteUserDTO getCurrentUserProfile(){
        WebsiteUser currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("Sorry, something went wrong");
        }
        return websiteUserMapper.toDto(currentUser);
    }

    public byte[] getUserProfilePicture(Long id) throws IOException {
        WebsiteUser websiteUser = websiteUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
      
        Path path = null;
        if (websiteUser.getProfilePictureUrl() == null) {
             path = Paths.get(mediaDirectory + "default.jpg");
        } else {
             path = Paths.get(websiteUser.getProfilePictureUrl());
        }
      
        return Files.readAllBytes(path);
    }

    public AuthenticationResponse editWebsiteUser(WebsiteUserDTO websiteUserDTO) throws IOException {
        WebsiteUser websiteUser = getCurrentUser();
        websiteUserMapper.partialUpdate(websiteUserDTO, websiteUser);
        if (websiteUserDTO.getPassword() != null) {
            websiteUser.setPassword(passwordEncoder.encode(websiteUserDTO.getPassword()));
        }
        if (websiteUserDTO.getProfilePicture() != null) {

            if (!Files.exists(Paths.get(mediaDirectory))) {
                File dirFile = new File(mediaDirectory);
                dirFile.mkdirs();
            }



            if (websiteUserDTO.getProfilePicture().toString().contains("..")) {
                Files.deleteIfExists(Path.of(websiteUser.getProfilePictureUrl()));
            }
            MultipartFile file = websiteUserDTO.getProfilePicture();
            String filename = "pfp_" + websiteUser.getUsername() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(mediaDirectory, filename);
            Files.copy(file.getInputStream(), filePath);
            websiteUser.setProfilePictureUrl(filePath.toString());
        }
        websiteUserRepository.save(websiteUser);
        String token = jwtService.generateToken(websiteUser);
        return new AuthenticationResponse(token);
    }

    public void deleteWebsiteUser(Long id) throws IOException {
        WebsiteUser websiteUser = websiteUserRepository.findById(id)
                .orElseThrow(() -> new BadCredentialsException("User not found"));
        WebsiteUser currentUser = getCurrentUser();
        boolean isAdmin = currentUser.getUserRoles().stream()
                .anyMatch(role -> role.getRoleName() == Role.ADMIN);
        boolean isAuthor = currentUser.equals(websiteUser);
        if (isAdmin || isAuthor) {
            if (websiteUser.getDeletedAt() != null) {
                throw new BadRequestException("This user does not exist or already got deleted");
            }
            Files.deleteIfExists(Path.of(websiteUser.getProfilePictureUrl()));
            websiteUser.setDeletedAt(LocalDateTime.now());
            websiteUserRepository.save(websiteUser);
        } else {
            throw new BadRequestException("You dont have permission to perform this action");
        }
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
