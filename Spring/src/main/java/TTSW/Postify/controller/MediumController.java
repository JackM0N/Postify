package TTSW.Postify.controller;

import TTSW.Postify.dto.MediumBase64DTO;
import TTSW.Postify.dto.MediumDTO;
import TTSW.Postify.dto.PostDTO;
import TTSW.Postify.service.MediumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/medium")
public class MediumController {
    private final MediumService mediumService;

    @GetMapping("/list/{postId}")
    private ResponseEntity<List<MediumBase64DTO>> getPostMedia(@PathVariable Long postId) throws IOException {
        List<byte[]> media = mediumService.getMediaForPost(postId);
        List<MediumBase64DTO> base64Media = media.stream()
                .map(bytes -> {
                    String base64Data = Base64.getEncoder().encodeToString(bytes);
                    String type = mediumService.getMediaType(bytes);
                    return new MediumBase64DTO(base64Data, type);
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(base64Media);
    }

    @PutMapping("/add/{index}")
    private ResponseEntity<PostDTO> addMedium(@PathVariable Integer index, @ModelAttribute MediumDTO mediumDTO) throws IOException {
        return ResponseEntity.ok(mediumService.addMediumAtIndex(index, mediumDTO));
    }

    @PutMapping("/edit/{position}")
    private ResponseEntity<PostDTO> updateMedium(@PathVariable int position, @ModelAttribute MediumDTO mediumDTO) throws IOException {
        return ResponseEntity.ok(mediumService.updateMedium(mediumDTO,position));
    }

    @DeleteMapping("/delete/{position}")
    private ResponseEntity<Boolean> deleteMedium(@PathVariable int position, @RequestBody MediumDTO mediumDTO) throws IOException {
        mediumService.deleteMedium(mediumDTO, position);
        return ResponseEntity.ok().build();
    }

}
