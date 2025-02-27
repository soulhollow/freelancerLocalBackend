package com.example.freelancelocal.service;

import com.example.freelancelocal.dto.ChatMessageDto;
import com.example.freelancelocal.model.ChatMessage;
import com.example.freelancelocal.model.User;
import com.example.freelancelocal.repository.ChatMessageRepository;
import com.example.freelancelocal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserRepository userRepository;

    public ChatMessage saveMessage(ChatMessageDto chatMessageDto) {
        // Erstelle eine neue ChatMessage Entity aus dem DTO
        ChatMessage message = new ChatMessage();
        message.setSender(chatMessageDto.getSender());
        message.setContent(chatMessageDto.getContent());
        message.setTimestamp(LocalDateTime.now());

        // Speichere und gib zurück
        return chatMessageRepository.save(message);
    }

    public List<ChatMessageDto> findChatHistory(Long userId, Long recipientId) {
        // Hole Chatverlauf (beide Richtungen)
        List<ChatMessage> messages = chatMessageRepository.findChatHistory(userId, recipientId);

        // Konvertiere zu DTOs
        return messages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ChatMessageDto> findRecentChats(Long userId) {
        // Hole die neuesten Chats mit anderen Benutzern
        List<Object[]> recentChats = chatMessageRepository.findRecentChats(userId);

        // Konvertiere zu DTOs
        return recentChats.stream()
                .map(row -> {
                    ChatMessageDto dto = new ChatMessageDto();
                    dto.setId((Long) row[0]);
                    dto.setContent((String) row[1]);
                    dto.setTimestamp((LocalDateTime) row[2]);

                    // Setze Sender/Empfänger Information
                    Long senderId = (Long) row[3];
                    User sender = userRepository.findById(senderId).orElse(null);
                    if (sender != null) {
                        dto.setSenderId(senderId);
                        dto.setSenderName(sender.getUsername());
                    }

                    // Bestimme den anderen Teilnehmer (nicht der aktuelle Benutzer)
                    Long otherUserId = senderId.equals(userId) ? (Long) row[4] : senderId;
                    User otherUser = userRepository.findById(otherUserId).orElse(null);
                    if (otherUser != null) {
                        dto.setRecipientId(otherUserId);
                        dto.setRecipientName(otherUser.getUsername());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    private ChatMessageDto convertToDto(ChatMessage message) {
        ChatMessageDto dto = new ChatMessageDto(message);

        // Weitere Informationen können hier hinzugefügt werden, falls nötig

        return dto;
    }
}