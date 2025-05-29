package com.repository;

import com.entity.chatrealtime.ConversationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ConversationUserRepo extends JpaRepository<ConversationUser,Integer> {
    @Query("SELECT cu FROM ConversationUser cu WHERE cu.conversation.id = :conversationId AND cu.userProfile.id = :userProfileId")
    Optional<ConversationUser> findByConversationIdAndUserProfileId(Integer conversationId, Integer userProfileId);

    @Query("SELECT CASE WHEN COUNT(cu) > 0 THEN TRUE ELSE FALSE END FROM ConversationUser cu WHERE cu.conversation.id = :conversationId AND cu.userProfile.id = :userProfileId")
    Boolean existsByConversationIdAndUserProfileId(Integer conversationId, Integer userProfileId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ConversationUser cu WHERE cu.conversation.id = :conversationId AND cu.userProfile.id = :userProfileId")
    int outConversation(@Param("conversationId") Integer conversationId, @Param("userProfileId") Integer userProfileId);
}

