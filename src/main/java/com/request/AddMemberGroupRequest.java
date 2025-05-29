package com.request;


import com.DTO.FriendDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddMemberGroupRequest {
    private Integer conversationId;
    private FriendDTO friend;
    private List<Integer> members;
}
