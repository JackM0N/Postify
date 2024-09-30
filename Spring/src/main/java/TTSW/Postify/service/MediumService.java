package TTSW.Postify.service;

import TTSW.Postify.dto.MediumDTO;
import TTSW.Postify.dto.PostDTO;
import TTSW.Postify.mapper.PostMapper;
import TTSW.Postify.model.Medium;
import TTSW.Postify.model.Post;
import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.repository.MediumRepository;
import TTSW.Postify.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MediumService {
    private final MediumRepository mediumRepository;
    private final PostRepository postRepository;
    private final AuthorizationService authorizationService;
    private final PostMapper postMapper;
    private final WebsiteUserService websiteUserService;

    @Value("${directory.media.posts}")
    private String mediaDirectory = "../Media/posts/";

    public List<byte[]> getMediaForPost(Long id) throws IOException {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        List<Medium> media = post.getMedia();
        List<byte[]> mediaBytes = new ArrayList<>();
        for (Medium medium : media) {
            Path mediaPath = Paths.get(medium.getMediumUrl());
            mediaBytes.add(Files.readAllBytes(mediaPath));
        }
        return mediaBytes;
    }

    public PostDTO addMediumAtIndex(Integer index, MediumDTO mediumDTO) throws IOException {
        Post post = postRepository.findById(mediumDTO.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        List<Medium> media = post.getMedia();
        WebsiteUser currentUser = websiteUserService.getCurrentUser();

        if (currentUser.equals(post.getUser())) {
            MultipartFile file = mediumDTO.getFile();
            String extension = getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
            String filename = getFileName(post.getId(), index, extension);

            String baseDir = mediaDirectory + post.getId();

            Medium newMedium = new Medium();
            newMedium.setPost(post);
            newMedium.setMediumType(mediumDTO.getMediumType());
            newMedium.setMediumUrl(baseDir + "/" + filename);

            Path filePath = Paths.get(baseDir, filename);
            Files.copy(file.getInputStream(), filePath);

            media.add(index, newMedium);
            mediumRepository.save(newMedium);

            int i = 0;
            for (Medium medium : media) {
                String newExtension = getFileExtension(Objects.requireNonNull(medium.getMediumUrl()));
                String newFilename = getFileName(post.getId(), i, newExtension);
                medium.setMediumUrl(baseDir + "/" + newFilename);
                i++;
            }

            mediumRepository.saveAll(media);
            post.setMedia(media);
            postRepository.save(post);
            return postMapper.toDto(post);
        } else {
            throw new BadCredentialsException("You dont have permission to add medium to this post");
        }
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf('.') + 1);
    }

    private String getFileName(Long postId, int index, String extension) {
        return "post_" + postId + "_media_" + index + "." + extension;
    }

    public PostDTO updateMedium(MediumDTO mediumDTO, int position) throws IOException {
        Post post = postRepository.findById(mediumDTO.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        WebsiteUser currentUser = websiteUserService.getCurrentUser();
        if (currentUser.equals(post.getUser())) {
            if (position < 0 || position >= post.getMedia().size()) {
                position = post.getMedia().size() - 1;
            }

            Medium medium = post.getMedia().get(position);
            if (!Path.of(medium.getMediumUrl()).startsWith(Path.of(mediaDirectory)) || !Files.exists(Paths.get(medium.getMediumUrl()))) {
                throw new BadRequestException("Medium url doesn't match or does not exist");
            }
            medium.setMediumType(mediumDTO.getMediumType());
            if (mediumDTO.getFile() == null) {
                throw new EntityNotFoundException("File not found");
            }

            Path filePath = Paths.get(medium.getMediumUrl());
            Files.deleteIfExists(filePath);
            Files.copy(mediumDTO.getFile().getInputStream(), filePath);
            mediumRepository.save(medium);
            return postMapper.toDto(post);
        } else {
            throw new BadCredentialsException("You do not have permission to update this medium");
        }
    }

    public void deleteMedium(MediumDTO mediumDTO, int position) throws IOException {
        Post post = postRepository.findById(mediumDTO.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        if (authorizationService.canModifyEntity(post)) {
            Medium medium = post.getMedia().get(position);
            Files.deleteIfExists(Path.of(medium.getMediumUrl()));
            post.getMedia().remove(position);
            postRepository.save(post);
        } else {
            throw new BadCredentialsException("You do not have permission to delete this medium");
        }
    }
}
