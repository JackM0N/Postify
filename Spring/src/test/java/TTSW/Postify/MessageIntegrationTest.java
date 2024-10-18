package TTSW.Postify;

import TTSW.Postify.dto.MessageDTO;
import TTSW.Postify.dto.SimplifiedWebsiteUserDTO;
import TTSW.Postify.mapper.MessageMapper;
import TTSW.Postify.mapper.SimplifiedWebsiteUserMapper;
import TTSW.Postify.model.Message;
import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.repository.*;
import TTSW.Postify.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@WithMockUser("john@example.com")
public class MessageIntegrationTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private WebsiteUserRepository websiteUserRepository;

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private SimplifiedWebsiteUserMapper simplifiedWebsiteUserMapper;

    private WebsiteUser currentUser;
    private WebsiteUser receiverUser;
    private Message message;

    @BeforeEach
    void setUp() {
        currentUser = websiteUserRepository.findByEmail("john@example.com").get();
        receiverUser = websiteUserRepository.findByUsername("jane_smith").get();

        message = new Message();
        message.setSender(currentUser);
        message.setReceiver(receiverUser);
        message.setMessageText("Hello!");
        message.setIsRead(false);
        message.setCreatedAt(LocalDateTime.now());
        message = messageRepository.save(message);
    }

    @Test
    void testGetMessagesWithUser_Success() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<MessageDTO> messages = messageService.getMessagesWithUser(receiverUser.getUsername(), pageable);

        assertNotNull(messages);
        assertTrue(messages.getTotalElements() > 0);
    }

    @Test
    void testGetMessagesWithUser_UserNotFound() {
        Pageable pageable = PageRequest.of(0, 10);

        assertThrows(RuntimeException.class, () -> messageService.getMessagesWithUser("unknownUser", pageable));
    }

    @Test
    void testCreateMessage_Success() {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setReceiver(simplifiedWebsiteUserMapper.toDto(receiverUser));
        messageDTO.setMessageText("Hello!");

        MessageDTO createdMessage = messageService.createMessage(messageDTO);

        assertNotNull(createdMessage);
        assertEquals("Hello!", createdMessage.getMessageText());
        assertEquals(currentUser.getUsername(), createdMessage.getSender().getUsername());
        assertEquals(receiverUser.getUsername(), createdMessage.getReceiver().getUsername());
    }

    @Test
    void testCreateMessage_ReceiverNotFound() {
        MessageDTO messageDTO = new MessageDTO();
        SimplifiedWebsiteUserDTO websiteUserDTO = new SimplifiedWebsiteUserDTO();
        websiteUserDTO.setUsername("unknownUser");
        messageDTO.setReceiver(websiteUserDTO);
        messageDTO.setMessageText("Hello!");

        assertThrows(RuntimeException.class, () -> messageService.createMessage(messageDTO));
    }

    @Test
    void testUpdateMessage_Success() {
        MessageDTO messageDTO = messageMapper.toDto(message);
        messageDTO.setMessageText("Hai!");

        MessageDTO updatedMessage = messageService.updateMessage(messageDTO);

        assertNotNull(updatedMessage);
        assertEquals("Hai!", updatedMessage.getMessageText());
    }

    @Test
    @WithMockUser("jane@example.com")
    void testUpdateMessage_NoPermission() {

        MessageDTO messageDTO = messageMapper.toDto(message);
        messageDTO.setMessageText("Unauthorized Update");

        assertThrows(BadCredentialsException.class, () -> messageService.updateMessage(messageDTO));
    }

    @Test
    void testUpdateMessage_MessageNotFound() {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId(999L);
        messageDTO.setMessageText("Non-existent message");

        assertThrows(RuntimeException.class, () -> messageService.updateMessage(messageDTO));
    }

    @Test
    void testDeleteMessage_Success() {
        messageService.deleteMessage(message.getId());

        assertTrue(messageRepository.findById(message.getId()).isEmpty());
    }

    @Test
    @WithMockUser("jane@example.com")
    void testDeleteMessage_NoPermission() {
        assertThrows(BadCredentialsException.class, () -> messageService.deleteMessage(message.getId()));
    }

    @Test
    void testDeleteMessage_MessageNotFound() {
        assertThrows(RuntimeException.class, () -> messageService.deleteMessage(999L));
    }

    @Test
    void testMarkMessagesAsRead_Success() {
        List<Message> unreadMessages = List.of(message);

        messageService.markMessagesAsRead(unreadMessages);

        Message updatedMessage = messageRepository.findById(message.getId()).orElseThrow();
        assertTrue(updatedMessage.getIsRead());
    }

    @Test
    void testMarkMessagesAsRead_NoUnreadMessages() {
        message.setIsRead(true);
        messageRepository.save(message);

        List<Message> unreadMessages = List.of(message);

        messageService.markMessagesAsRead(unreadMessages);

        Message updatedMessage = messageRepository.findById(message.getId()).orElseThrow();
        assertTrue(updatedMessage.getIsRead());
    }
}

