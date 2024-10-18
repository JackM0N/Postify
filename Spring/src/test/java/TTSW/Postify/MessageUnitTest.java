package TTSW.Postify;

import TTSW.Postify.dto.MessageDTO;
import TTSW.Postify.dto.SimplifiedWebsiteUserDTO;
import TTSW.Postify.mapper.MessageMapper;
import TTSW.Postify.mapper.MessageMapperImpl;
import TTSW.Postify.model.Message;
import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.repository.MessageRepository;
import TTSW.Postify.repository.WebsiteUserRepository;
import TTSW.Postify.service.MessageService;
import TTSW.Postify.service.WebsiteUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.BadCredentialsException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class MessageUnitTest {

    @MockBean
    private MessageRepository messageRepository;

    @MockBean
    private WebsiteUserService websiteUserService;

    @MockBean
    private WebsiteUserRepository websiteUserRepository;

    @Spy
    private MessageMapper messageMapper = new MessageMapperImpl();

    @Autowired
    private MessageService messageService;

    private WebsiteUser currentUser;
    private WebsiteUser receiverUser;
    private Message message;

    @BeforeEach
    void setUp() {
        currentUser = new WebsiteUser();
        currentUser.setId(1L);
        currentUser.setUsername("john_doe");

        receiverUser = new WebsiteUser();
        receiverUser.setId(2L);
        receiverUser.setUsername("jane_smith");

        // Mocking a message
        message = new Message();
        message.setId(1L);
        message.setSender(currentUser);
        message.setReceiver(receiverUser);
        message.setMessageText("Hello!");
        message.setIsRead(false);
        message.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testGetMessagesWithUser_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        when(websiteUserService.getCurrentUser()).thenReturn(currentUser);
        when(websiteUserRepository.findByUsername("jane_smith")).thenReturn(Optional.of(receiverUser));
        Page<Message> messagePage = new PageImpl<>(List.of(message), pageable, 1);
        when(messageRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(messagePage);

        Page<MessageDTO> result = messageService.getMessagesWithUser("jane_smith", pageable);

        verify(messageRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        assertNotNull(result.getContent());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testGetMessagesWithUser_UserNotFound() {
        when(websiteUserRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> messageService.getMessagesWithUser("unknownUser", Pageable.unpaged()));
    }

    @Test
    void testMarkMessagesAsRead_Success() {
        List<Message> unreadMessages = List.of(message);

        messageService.markMessagesAsRead(unreadMessages);

        assertTrue(message.getIsRead());
        verify(messageRepository, times(1)).saveAll(unreadMessages);
    }

    @Test
    void testCreateMessage_Success() {
        MessageDTO messageDTO = new MessageDTO();
        SimplifiedWebsiteUserDTO receiver = new SimplifiedWebsiteUserDTO();
        receiver.setUsername("jane_smith");
        messageDTO.setReceiver(receiver);
        messageDTO.setMessageText("Hello!");

        when(websiteUserService.getCurrentUser()).thenReturn(currentUser);
        when(websiteUserRepository.findByUsername("jane_smith")).thenReturn(Optional.of(receiverUser));
        when(messageRepository.save(any(Message.class))).thenReturn(message);

        MessageDTO createdMessage = messageService.createMessage(messageDTO);

        verify(messageRepository, times(1)).save(any(Message.class));
        assertNotNull(createdMessage);
        assertEquals("jane_smith", createdMessage.getReceiver().getUsername());
    }

    @Test
    void testCreateMessage_ReceiverNotFound() {
        MessageDTO messageDTO = new MessageDTO();
        SimplifiedWebsiteUserDTO receiver = new SimplifiedWebsiteUserDTO();
        receiver.setUsername("unknownUser");
        messageDTO.setReceiver(receiver);
        messageDTO.setMessageText("Hello!");

        when(websiteUserRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> messageService.createMessage(messageDTO));
    }

    @Test
    void testUpdateMessage_Success() {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId(1L);
        messageDTO.setMessageText("Updated Message");

        when(websiteUserService.getCurrentUser()).thenReturn(currentUser);
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));

        MessageDTO updatedMessage = messageService.updateMessage(messageDTO);

        verify(messageRepository, times(1)).save(any(Message.class));
        assertNotNull(updatedMessage);
        assertEquals("Updated Message", message.getMessageText());
    }

    @Test
    void testUpdateMessage_NoPermission() {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId(1L);

        when(websiteUserService.getCurrentUser()).thenReturn(receiverUser);
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));

        assertThrows(BadCredentialsException.class, () -> messageService.updateMessage(messageDTO));
    }

    @Test
    void testDeleteMessage_Success() {
        when(websiteUserService.getCurrentUser()).thenReturn(currentUser);
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));

        messageService.deleteMessage(1L);

        verify(messageRepository, times(1)).delete(message);
    }

    @Test
    void testDeleteMessage_NoPermission() {
        when(websiteUserService.getCurrentUser()).thenReturn(receiverUser);
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));

        assertThrows(BadCredentialsException.class, () -> messageService.deleteMessage(1L));
    }
}
