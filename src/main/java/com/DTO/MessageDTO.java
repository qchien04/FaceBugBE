package com.DTO;
import com.constant.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
public class MessageDTO {

    private Integer id;

    private String content;

    private MessageType messageType;

    private LocalDateTime timeSend;

    private Integer senderId;

    private String nameSend;

    public MessageDTO(Integer id, String content, String messageType, Timestamp timeSend, Integer senderId, String senderName) {
        this.content = content;
        this.id = id;
        if(messageType!=null){
            this.messageType = MessageType.valueOf(messageType);
        }
        if(timeSend!=null){
            this.timeSend = timeSend.toLocalDateTime();
        }
        this.senderId = senderId;
        this.nameSend = senderName;
    }
}
