package com.DTO;


import com.constant.MessageType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ConversationDTO {
    private Integer id;
    private String name;
    private String avt;
    private Integer type;
    private String conversationRole;
    private MessageDTO lastMessage;
    private LocalDateTime updatedAt;

    public ConversationDTO(Integer id,
                           String name,
                           String avt,
                           Integer type,
                           String conversationRole,
                           Integer messageId,
                           String messageContent,
                           String messageType,
                           Timestamp timeSend,
                           Integer senderId,
                           String senderName,
                           Timestamp updatedAt) {
        this.id = id;
        this.name = name;
        this.avt = avt;
        this.type = type;
        this.conversationRole=conversationRole;
        this.lastMessage = new MessageDTO(messageId,messageContent,messageType,timeSend,senderId,senderName);
        this.updatedAt = updatedAt.toLocalDateTime();
    }
}
