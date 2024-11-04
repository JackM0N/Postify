package TTSW.Postify.service;

import TTSW.Postify.dto.MessageDTO;
import TTSW.Postify.mapper.MessageMapper;
import TTSW.Postify.model.Message;
import TTSW.Postify.model.WebsiteUser;
import TTSW.Postify.repository.MessageRepository;
import TTSW.Postify.repository.WebsiteUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final WebsiteUserService websiteUserService;
    private final WebsiteUserRepository websiteUserRepository;
    private final MessageMapper messageMapper;

    public Page<MessageDTO> getMessagesWithUser(String username, Pageable pageable) {
        //TODO: Think about when can ppl message each-other. Maybe they have to follow each-other? Or add friends?
        WebsiteUser currentUser = websiteUserService.getCurrentUser();
        WebsiteUser messagedUser = websiteUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Specification<Message> spec = getMessageSpecification(currentUser, messagedUser);

        Page<Message> messages = messageRepository.findAll(spec, pageable);

        List<Message> unreadMessages = messages.stream()
                .filter(message -> !message.getIsRead())
                .collect(Collectors.toList());

        if (!unreadMessages.isEmpty()) {
            markMessagesAsRead(unreadMessages);
        }

        return messages.map(messageMapper::toDto);
    }

    private static Specification<Message> getMessageSpecification(WebsiteUser currentUser, WebsiteUser messagedUser) {
        //Get messages that I'm a receiver of or person that im messaging is the receiver
        Specification<Message> recSpec = (root, query, builder) ->
                builder.or(
                        builder.equal(root.get("receiver"), currentUser),
                        builder.equal(root.get("receiver"), messagedUser)
                );

        //Get messages that he is sending or I'm sending
        Specification<Message> senSpec = (root, query, builder) ->
                builder.or(
                        builder.equal(root.get("sender"), currentUser),
                        builder.equal(root.get("sender"), messagedUser)
                );

        //Combine the two
        return senSpec.and(recSpec);
    }

    public void markMessagesAsRead(List<Message> messages) {
        for (Message message : messages) {
            if (!message.getIsRead()) {
                message.setIsRead(true);
            }
        }
        messageRepository.saveAll(messages);
    }

    public MessageDTO createMessage(MessageDTO messageDTO) {
        WebsiteUser currentUser = websiteUserService.getCurrentUser();
        Message message = messageMapper.toEntity(messageDTO);
        message.setSender(currentUser);
        message.setReceiver(websiteUserRepository.findByUsername(messageDTO.getReceiver().getUsername())
                .orElseThrow(() -> new RuntimeException("User not found")));
        messageRepository.save(message);
        return messageMapper.toDto(message);
    }

    public MessageDTO updateMessage(MessageDTO messageDTO) {
        WebsiteUser currentUser = websiteUserService.getCurrentUser();
        Message message = messageRepository.findById(messageDTO.getId())
                .orElseThrow(() -> new RuntimeException("Message not found"));
        if (message.getSender().getUsername().equals(currentUser.getUsername())) {
            messageMapper.partialUpdate(messageDTO,message);
            messageRepository.save(message);
            return messageMapper.toDto(message);
        } else {
            throw new BadCredentialsException("You dont have permission to edit this message");
        }
    }

    public void deleteMessage(Long id) {
        WebsiteUser currentUser = websiteUserService.getCurrentUser();
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        if (message.getSender().getUsername().equals(currentUser.getUsername())) {
            messageRepository.delete(message);
        } else {
            throw new BadCredentialsException("You dont dot have permission to delete this message");
        }
    }
}
