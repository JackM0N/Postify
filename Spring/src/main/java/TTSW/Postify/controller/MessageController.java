package TTSW.Postify.controller;

import TTSW.Postify.dto.MessageDTO;
import TTSW.Postify.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/message")
public class MessageController {
    private final MessageService messageService;

    @GetMapping("/user/{username}")
    private ResponseEntity<Page<MessageDTO>> getUserMessages(@PathVariable String username, Pageable pageable) {
        return ResponseEntity.ok(messageService.getMessagesWithUser(username, pageable));
    }

    @PostMapping("/create")
    private ResponseEntity<MessageDTO> createMessage(@RequestBody MessageDTO messageDTO) {
        return ResponseEntity.ok(messageService.createMessageDTO(messageDTO));
    }

    @PutMapping("/update")
    private ResponseEntity<MessageDTO> updateMessage(@RequestBody MessageDTO messageDTO) throws AccessDeniedException {
        return ResponseEntity.ok(messageService.updateMessageDTO(messageDTO));
    }

    @DeleteMapping("/delete/{id}")
    private ResponseEntity<MessageDTO> deleteMessage(@PathVariable Long id) throws AccessDeniedException {
        messageService.deleteMessageDTO(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
