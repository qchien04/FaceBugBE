package com.service.imple;

import com.DTO.ProfileSummary;
import com.DTO.MemberGroupChatDTO;
import com.entity.auth.UserProfile;
import com.entity.chatrealtime.Conversation;
import com.entity.chatrealtime.ConversationUser;
import com.repository.ConversationRepo;
import com.repository.ConversationUserRepo;
import com.repository.UserProfileRepo;
import com.service.chatrealtime.ConversationUserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ConversationUserServiceImp implements ConversationUserService {
    private ConversationUserRepo conversationUserRepo;
    private ConversationRepo conversationRepo;
    private UserProfileRepo userProfileRepo;

    @Override
    @Transactional
    public ConversationUser create(ConversationUser conversationUser) {
        return conversationUserRepo.save(conversationUser);

    }

    @Override
    public MemberGroupChatDTO addMemberToGroup(Integer conversationId, Integer friendId) {
        UserProfile userProfile=UserProfile.builder().id(friendId).build();
        Conversation conversation=Conversation.builder().id(conversationId).build();
        ConversationUser cu=new ConversationUser(userProfile,conversation, ConversationUser.Role.MEMBER);

        conversationUserRepo.save(cu);

        ProfileSummary friendDTO=userProfileRepo.searchUserProfileDTO(friendId);
        MemberGroupChatDTO mgDTO=new MemberGroupChatDTO(
                conversationId,
                friendDTO.getId(),
                friendDTO.getName(),
                friendDTO.getAvt(),
                ConversationUser.Role.MEMBER);

        return mgDTO;
    }

    @Override
    public void removeMember(Integer conversationId, Integer friendId) {
        ConversationUser member = conversationUserRepo.findByConversationIdAndUserProfileId(conversationId, friendId)
                .orElseThrow(() -> new RuntimeException("Thành viên không tồn tại trong nhóm"));

        conversationUserRepo.delete(member);
    }

    @Override
    public Boolean checkExist(Integer conversationId, Integer userId) {
        return conversationUserRepo.existsByConversationIdAndUserProfileId(conversationId,userId);
    }

    @Override
    public boolean outConversation(Integer conversationId) {
        Integer id=((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        UserProfile up=userProfileRepo.findById(id).get();
        int n=conversationUserRepo.outConversation(conversationId,id);
        Conversation c=conversationRepo.findById(conversationId).get();
        if(c.getTypeRoom()==0 && n==1){
            c.setAvt(up.getAvt());
            c.setName(up.getName());
            System.out.println(c);
            conversationRepo.save(c);
            System.out.println("------------------------------------------------");
            return true;
        }
        
        return false;
    }
}
