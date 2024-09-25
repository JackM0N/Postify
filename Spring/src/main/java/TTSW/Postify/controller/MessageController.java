package TTSW.Postify.controller;

import TTSW.Postify.dto.MessageDTO;
import TTSW.Postify.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/message")
public class MessageController {
    private final MessageService messageService;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{username}")
    private ResponseEntity<Page<MessageDTO>> getUserMessages(@PathVariable String username, Pageable pageable) {
        return ResponseEntity.ok(messageService.getMessagesWithUser(username, pageable));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("")
    private ResponseEntity<MessageDTO> createMessage(@RequestBody MessageDTO messageDTO) {
        return ResponseEntity.ok(messageService.createMessage(messageDTO));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("")
    private ResponseEntity<MessageDTO> updateMessage(@RequestBody MessageDTO messageDTO) {
        return ResponseEntity.ok(messageService.updateMessage(messageDTO));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/{id}")
    private ResponseEntity<MessageDTO> deleteMessage(@PathVariable Long id) {
        messageService.deleteMessage(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
