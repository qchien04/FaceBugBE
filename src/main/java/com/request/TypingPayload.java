package com.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TypingPayload {
    private String name;
    private String type;
    private Integer senderId;
    private Integer conversationId;
}
