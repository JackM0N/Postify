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
import java.nio.file.StandardCopyOption;
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
            if (index < 0) {
                index = 0;
            }else if (index > media.size()) {
                index = media.size() - 1;
            }

            MultipartFile file = mediumDTO.getFile();
            String extension = getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));

            String baseDir = mediaDirectory + post.getId();

            // Rename all media from the last index to the target index
            for (int i = media.size() - 1; i >= index; i--) {
                Medium medium = media.get(i);
                String newExtension = getFileExtension(Objects.requireNonNull(medium.getMediumUrl()));
                String newFilename = getFileName(post.getId(), i + 1, newExtension); // Shift index by 1
                Path oldFilePath = Paths.get(medium.getMediumUrl());
                Path newFilePath = Paths.get(baseDir, newFilename);

                // Rename the file
                if (Files.exists(newFilePath)) {
                    Files.move(oldFilePath, newFilePath, StandardCopyOption.REPLACE_EXISTING);
                } else {
                    Files.move(oldFilePath, newFilePath);
                }
                medium.setMediumUrl(baseDir + "/" + newFilename);
            }

            // Add the new medium at the target index
            String newFileName = getFileName(post.getId(), index, extension); // File for the new medium
            Medium newMedium = createNewMedium(mediumDTO, post, file, baseDir, newFileName);
            media.add(index, newMedium);
            mediumRepository.save(newMedium);

            mediumRepository.saveAll(media);
            post.setMedia(media);
            postRepository.save(post);
            return postMapper.toDto(post);
        } else {
            throw new BadCredentialsException("You dont have permission to add medium to this post");
        }
    }

    static Medium createNewMedium(MediumDTO mediumDTO, Post post, MultipartFile file, String baseDir, String newFileName) throws IOException {
        Path newMediumPath = Paths.get(baseDir, newFileName);
        Files.copy(file.getInputStream(), newMediumPath);

        Medium newMedium = new Medium();
        newMedium.setPost(post);
        newMedium.setMediumType(mediumDTO.getMediumType());
        newMedium.setMediumUrl(baseDir + "/" + newFileName);

        return newMedium;
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
        List<Medium> media = post.getMedia();
        if (media.size() == 1){
            throw new RuntimeException("Post has to contain at least one medium");
        }
        if (authorizationService.canModifyEntity(post)) {
            Medium deleteMedium = post.getMedia().get(position);
            Files.deleteIfExists(Path.of(deleteMedium.getMediumUrl()));
            media.remove(position);
            mediumRepository.delete(deleteMedium);

            String baseDir = mediaDirectory + post.getId();
            for (int i = position; i < media.size(); i++) {
                Medium medium = media.get(i);
                String extension = getFileExtension(Objects.requireNonNull(medium.getMediumUrl()));
                String newFilename = getFileName(post.getId(), i, extension);
                Path oldFilePath = Paths.get(medium.getMediumUrl());
                Path newFilePath = Paths.get(baseDir, newFilename);

                Files.move(oldFilePath, newFilePath, StandardCopyOption.REPLACE_EXISTING);

                medium.setMediumUrl(baseDir + "/" + newFilename);
            }
            post.getMedia().remove(media.size() - 1);
            mediumRepository.saveAll(media);
            postRepository.save(post);
        } else {
            throw new BadCredentialsException("You do not have permission to delete this medium");
        }
    }

    public String getMediaType(byte[] bytes) {
        if (bytes == null || bytes.length < 4) {
            return "application/octet-stream"; // Default
        }

        // JPG: 0xFF 0xD8 0xFF
        if (bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xD8 && bytes[2] == (byte) 0xFF) {
            return "image/jpeg";
        }

        // PNG: 0x89 0x50 0x4E 0x47
        if (bytes[0] == (byte) 0x89 && bytes[1] == (byte) 0x50 && bytes[2] == (byte) 0x4E && bytes[3] == (byte) 0x47) {
            return "image/png";
        }

        // GIF: 0x47 0x49 0x46
        if (bytes[0] == (byte) 0x47 && bytes[1] == (byte) 0x49 && bytes[2] == (byte) 0x46) {
            return "image/gif";
        }

        // MP4: 0x00 0x00 0x00 0x18 0x66 0x74 0x79 0x70
        if (bytes.length > 11 && bytes[4] == (byte) 0x66 && bytes[5] == (byte) 0x74 && bytes[6] == (byte) 0x79 && bytes[7] == (byte) 0x70) {
            return "video/mp4";
        }

        return "application/octet-stream";
    }
}
