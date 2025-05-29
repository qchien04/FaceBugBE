package com.DTO;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageNotifyDTO {
    String senderName;
    Integer receiveId;
    Integer conversationId;
    String content;
    LocalDateTime sendAt;

//    public MessageNotifyDTO(String senderName, Integer receiveId, Integer conversationId, String content, Timestamp sendAt) {
//        this.senderName = senderName;
//        this.receiveId = receiveId;
//        this.conversationId = conversationId;
//        this.content = content;
//        this.sendAt = sendAt != null ? sendAt.toLocalDateTime() : null;
//    }

}
