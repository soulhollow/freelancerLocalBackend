package com.example.freelancelocal.repository;

import com.example.freelancelocal.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT m FROM ChatMessage m WHERE " +
            "(m.sender = :userId AND m.recipient = :recipientId) OR " +
            "(m.sender = :recipientId AND m.recipient = :userId) " +
            "ORDER BY m.timestamp ASC")
    List<ChatMessage> findChatHistory(@Param("userId") Long userId, @Param("recipientId") Long recipientId);

    @Query(value = "SELECT m.id, m.content, m.timestamp, m.sender, " +
            "CASE WHEN m.sender = :userId THEN m.recipient ELSE m.sender END as other_user " +
            "FROM (" +
            "  SELECT MAX(id) as max_id " +
            "  FROM chat_message " +
            "  WHERE sender = :userId OR recipient = :userId " +
            "  GROUP BY CASE WHEN sender = :userId THEN recipient ELSE sender END" +
            ") last_msgs " +
            "JOIN chat_message m ON m.id = last_msgs.max_id " +
            "ORDER BY m.timestamp DESC", nativeQuery = true)
    List<Object[]> findRecentChats(@Param("userId") Long userId);
}