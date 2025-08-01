package com.service.chatrealtime;


import com.DTO.ConversationDTO;
import com.DTO.ProfileSummary;
import com.DTO.MemberGroupChatDTO;
import com.entity.chatrealtime.Conversation;
import com.exception.ConversationException;
import com.exception.UserException;
import com.request.CreateConversationRequest;

import java.util.List;


public interface ConversationService {

    public ConversationDTO createConversation(CreateConversationRequest createConversationRequest) throws UserException;


    public void updatePreviewonversation(Integer id,Integer lastMessage);
    

    public List<ConversationDTO> findUserConversation();

    public ConversationDTO findConversationById(Integer conversationId);

    public Conversation checkHaveConversation(Integer friendId);

    public Conversation findById(Integer conversationId);
    public Conversation save(Conversation conversation);

    public List<MemberGroupChatDTO> findAllMemberInConversation(Integer conversationId);
}
