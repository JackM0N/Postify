package TTSW.Postify.controller;

import TTSW.Postify.dto.MediumDTO;
import TTSW.Postify.dto.PostDTO;
import TTSW.Postify.service.MediumService;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/medium")
public class MediumController {
    private final MediumService mediumService;

    @PermitAll
    @GetMapping("/{postId}")
    private ResponseEntity<List<byte[]>> getPostMedia(@PathVariable Long postId) throws IOException {
        return ResponseEntity.ok(mediumService.getMediaForPost(postId));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/add/{index}")
    private ResponseEntity<PostDTO> addMedium(@PathVariable Integer index, @RequestBody MediumDTO mediumDTO) throws IOException {
        return ResponseEntity.ok(mediumService.addMediumAtIndex(index, mediumDTO));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/{position}")
    private ResponseEntity<PostDTO> updateMedium(@PathVariable int position, @RequestBody MediumDTO mediumDTO) throws IOException {
        return ResponseEntity.ok(mediumService.updateMedium(mediumDTO,position));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/{position}")
    private ResponseEntity<Boolean> deleteMedium(@PathVariable int position, @RequestBody MediumDTO mediumDTO) throws IOException {
        mediumService.deleteMedium(mediumDTO, position);
        return ResponseEntity.ok().build();
    }

}
