package com.service.imple;


import com.DTO.ConversationDTO;
import com.DTO.FriendDTO;
import com.DTO.MemberGroupChatDTO;
import com.entity.auth.User;
import com.entity.auth.UserProfile;
import com.entity.chatrealtime.Conversation;
import com.entity.chatrealtime.ConversationUser;
import com.exception.ConversationException;
import com.exception.UserException;
import com.repository.ConversationRepo;
import com.repository.ConversationUserRepo;
import com.request.CreateConversationRequest;
import com.service.CustomUserDetails;
import com.service.auth.UserProfileService;
import com.service.auth.UserService;
import com.service.chatrealtime.ConversationService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class ConversationServiceImp implements ConversationService {

    private ConversationRepo conversationRepo;
    private ConversationUserRepo conversationUserRepo;
    private UserProfileService userProfileService;

    @Override
    @Transactional
    public ConversationDTO createConversation(CreateConversationRequest createConversationRequest) throws UserException {
        Integer localUserId=createConversationRequest.getLocalUserId();
        Integer remoteUserId=createConversationRequest.getRemoteUserId();

        UserProfile localUser=UserProfile.builder().id(localUserId).build();
        UserProfile remoteUser=UserProfile.builder().id(remoteUserId).build();

        Boolean isGroup=createConversationRequest.getIsGroup();
        if(isGroup){
            Conversation conversation = new Conversation();
            conversation.setCreatedAt(LocalDateTime.now());
            conversation.setAvt("http://res.cloudinary.com/dr83zoguh/image/upload/0895e179-902d-4e1f-81d4-195b7731b56f_converted_file.png");
            conversation.setTypeRoom(1);
            conversation.setName(createConversationRequest.getName());
            conversation.setLastMessage(null);
            conversation.setUserSendLastMessage(localUser);
            conversation=conversationRepo.save(conversation);

            ConversationUser conversationUser=new ConversationUser(localUser,conversation, ConversationUser.Role.ADMIN);
            conversationUserRepo.save(conversationUser);

            ConversationDTO conversationDTO=new ConversationDTO();
            conversationDTO.setAvt(conversation.getAvt());
            conversationDTO.setType(1);
            conversationDTO.setId(conversation.getId());
            conversationDTO.setName(conversation.getName());
            conversationDTO.setLastMessage("Đã tạo nhóm");
            conversationDTO.setUserSendLast("");
            conversationDTO.setConversationRole(ConversationUser.Role.ADMIN.toString());
            conversationDTO.setUpdatedAt(conversation.getCreatedAt());

            return conversationDTO;
        }

        //List<Conversation> isChatExist = conversationRepo.findIfConversationExist(localUserId, remoteUserId);
        Conversation isChatExist = conversationRepo
                .findIfConversationExist(localUserId, remoteUserId, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElse(null);
        if (isChatExist != null) {
            return conversationRepo.findConversationById(isChatExist.getId(),localUserId);
        }
        if(localUser.equals(remoteUser)){
            throw new ConversationException("Can not create room");

        }
        Conversation conversation = new Conversation();
        conversation.setAvt("");
        conversation.setTypeRoom(0);
        conversation.setName("");
        conversation.setLastMessage(null);
        conversation.setUserSendLastMessage(null);

        conversation=conversationRepo.save(conversation);

        ConversationUser conversationUser1=new ConversationUser(localUser,conversation, ConversationUser.Role.MEMBER);
        ConversationUser conversationUser2=new ConversationUser(remoteUser,conversation, ConversationUser.Role.MEMBER);
        conversationUserRepo.save(conversationUser2);
        conversationUserRepo.save(conversationUser1);

        UserProfile userProfile=userProfileService.findByUserProfileId(remoteUserId);

        ConversationDTO conversationDTO=new ConversationDTO();
        conversationDTO.setAvt(userProfile.getAvt());
        conversationDTO.setType(0);
        conversationDTO.setId(conversation.getId());
        conversationDTO.setName(userProfile.getName());
        conversationDTO.setLastMessage("");
        conversationDTO.setUserSendLast("");
        return conversationDTO;
    }

    @Override
    @Transactional
    public void updatePreviewonversation(Integer id, Integer userId, Integer lastMessage) {
        int t=conversationRepo.updatePreviewConversation(id,userId,lastMessage);
    }


    @Override
    public List<ConversationDTO> findUserConversation() {
        Integer id=((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return conversationRepo.findAllConversationByUserId(id);
    }

    @Override
    public ConversationDTO findConversationById(Integer conversationId) {
        Integer id=((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return conversationRepo.findConversationById(conversationId,id);
    }

    @Override
    public Conversation findById(Integer conversationId) {
        return conversationRepo.findById(conversationId).get();
    }

    @Override
    public Conversation save(Conversation conversation) {
        return conversationRepo.save(conversation);
    }

    @Override
    public List<MemberGroupChatDTO> findAllMemberInConversation(Integer conversationId) {
        Integer id=((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        Set<MemberGroupChatDTO> se=conversationRepo.findAllMembersInGroup(conversationId,id);
        return se.stream().toList();
    }



}
