package com.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateConversationRequest {
    private Integer localUserId;
    private Integer remoteUserId;
    private Boolean isGroup;
    private String name;

}
