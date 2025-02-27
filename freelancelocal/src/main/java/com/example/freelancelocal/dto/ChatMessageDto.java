package com.example.freelancelocal.dto;

import com.example.freelancelocal.model.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    private Long id;
    private String sender;
    private String recipient;
    private String content;
    private LocalDateTime timestamp;
    private String senderName;
    private String recipientName;
    private Long senderId;
    private Long recipientId;

    // Konstruktor für die Konvertierung vom Entity
    public ChatMessageDto(ChatMessage chatMessage) {
        this.id = chatMessage.getId();
        this.sender = chatMessage.getSender();
        this.content = chatMessage.getContent();
        this.timestamp = chatMessage.getTimestamp();
    }
}