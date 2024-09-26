package TTSW.Postify.controller;

import TTSW.Postify.dto.MediumDTO;
import TTSW.Postify.dto.PostDTO;
import TTSW.Postify.service.MediumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/medium")
public class MediumController {
    private final MediumService mediumService;

    @GetMapping("/list/{postId}")
    private ResponseEntity<List<byte[]>> getPostMedia(@PathVariable Long postId) throws IOException {
        return ResponseEntity.ok(mediumService.getMediaForPost(postId));
    }

    @PutMapping("/add/{index}")
    private ResponseEntity<PostDTO> addMedium(@PathVariable Integer index, @RequestBody MediumDTO mediumDTO) throws IOException {
        return ResponseEntity.ok(mediumService.addMediumAtIndex(index, mediumDTO));
    }

    @PutMapping("/edit/{position}")
    private ResponseEntity<PostDTO> updateMedium(@PathVariable int position, @RequestBody MediumDTO mediumDTO) throws IOException {
        return ResponseEntity.ok(mediumService.updateMedium(mediumDTO,position));
    }

    @DeleteMapping("/delete/{position}")
    private ResponseEntity<Boolean> deleteMedium(@PathVariable int position, @RequestBody MediumDTO mediumDTO) throws IOException {
        mediumService.deleteMedium(mediumDTO, position);
        return ResponseEntity.ok().build();
    }

}
