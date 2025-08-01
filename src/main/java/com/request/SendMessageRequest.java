package com.request;


import com.constant.MessageType;
import com.entity.chatrealtime.Message;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {
    private Integer senderId;
    private String nameSend;
    private String content;
    private LocalDateTime timeSend;
    private Integer conversationId;
    private String imageUrl;
    private MessageType messageType;
    private List<Integer> receiveIds;
    private Boolean typing;

    @Override
    public String toString() {
        return "SendMessageRequest{" +
                "senderId=" + senderId +
                ", content='" + content + '\'' +
                ", timeSend=" + timeSend +
                ", conversationId=" + conversationId +
                ", imageUrl='" + imageUrl + '\'' +
                ", messageType=" + messageType +
                ", ReceiveIds=" + receiveIds +
                '}';
    }
}
