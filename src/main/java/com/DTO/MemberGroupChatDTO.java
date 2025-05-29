package com.DTO;


import com.entity.chatrealtime.ConversationUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MemberGroupChatDTO {
    private Integer memberId;
    private String memberName;
    private String memberAvt;
    private ConversationUser.Role memberRole;
}