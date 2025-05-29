package com.DTO;


import com.entity.chatrealtime.ConversationUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
public class ConversationDTO {
    private Integer id;
    private String name;
    private String avt;
    private Integer type;
    private String conversationRole;
    private String lastMessage;
    private String userSendLast;
    private LocalDateTime updatedAt;

    public ConversationDTO(Integer id, String name, String avt, Integer type,String conversationRole, String lastMessage,
                           String userSendLast, Timestamp updatedAt) {
        this.id = id;
        this.name = name;
        this.avt = avt;
        this.type = type;
        this.conversationRole=conversationRole;
        this.lastMessage = lastMessage;
        this.userSendLast=userSendLast;
        this.updatedAt = updatedAt != null ? updatedAt.toLocalDateTime() : null;


    }

    @Override
    public String toString() {
        return "ConversationDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", avt='" + avt + '\'' +
                ", type=" + type +
                ", lastMessage='" + lastMessage + '\'' +
                ", userSendLast='" + userSendLast + '\'' +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
