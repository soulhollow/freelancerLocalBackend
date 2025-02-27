package com.example.freelancelocal.controller;

import com.example.freelancelocal.dto.ChatMessageDto;
import com.example.freelancelocal.model.ChatMessage;
import com.example.freelancelocal.model.User;
import com.example.freelancelocal.service.ChatService;
import com.example.freelancelocal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserService userService;

    @MessageMapping("/chat.sendMessage")
    public void processMessage(@Payload ChatMessageDto chatMessageDto) {
        // Sicherheitscheck - nur authentifizierte Benutzer dürfen Nachrichten senden
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        if (!currentUsername.equals(chatMessageDto.getSender())) {
            return; // Sicherheitsverletzung - Benutzer versucht, als jemand anderes Nachrichten zu senden
        }

        // Setze Zeitstempel
        chatMessageDto.setTimestamp(LocalDateTime.now());

        ChatMessage savedMsg = chatService.saveMessage(chatMessageDto);

        // Sende Nachricht an das Topic für den Empfänger
        messagingTemplate.convertAndSendToUser(
                chatMessageDto.getRecipient(),
                "/queue/messages",
                savedMsg);
    }

    @GetMapping("/history/{userId}/{recipientId}")
    public ResponseEntity<List<ChatMessageDto>> findChatHistory(
            @PathVariable Long userId,
            @PathVariable Long recipientId) {

        // Sicherheitscheck - nur der eigene Chatverlauf darf abgerufen werden
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(authentication.getName());

        if (!currentUser.getId().equals(userId)) {
            return ResponseEntity.status(403).build(); // Forbidden
        }

        return ResponseEntity.ok(chatService.findChatHistory(userId, recipientId));
    }

    @GetMapping("/recent-chats")
    public ResponseEntity<List<ChatMessageDto>> getRecentChats() {
        // Sicherheitscheck - eigene chats abrufen
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(authentication.getName());

        return ResponseEntity.ok(chatService.findRecentChats(currentUser.getId()));
    }
}