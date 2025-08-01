package com.request;


import com.DTO.ProfileSummary;
import com.DTO.MemberGroupChatDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RemoveMemberGroupRequest {
    private Integer conversationId;
    private MemberGroupChatDTO member;
    private List<Integer> members;
}
