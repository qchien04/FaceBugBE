package com.service.chatrealtime;

import com.DTO.FriendDTO;
import com.DTO.MemberGroupChatDTO;
import com.entity.chatrealtime.ConversationUser;

public interface ConversationUserService {
    ConversationUser create(ConversationUser conversationUser);
    MemberGroupChatDTO addMemberToGroup(Integer conversationId, Integer friendId);
    void removeMember(Integer conversationId,Integer friendId);
    Boolean checkExist(Integer conversationId,Integer userId);
    boolean outConversation(Integer conversationId);
}
