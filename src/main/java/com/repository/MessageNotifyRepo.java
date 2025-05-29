package com.repository;



import com.DTO.MessageNotifyDTO;
import com.entity.chatrealtime.MessageNotify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


public interface MessageNotifyRepo extends JpaRepository<MessageNotify,Integer> {
    Optional<MessageNotify> findById(Integer id);

    @Query("SELECT new com.DTO.MessageNotifyDTO(u.name, mn.receiveId, mn.conversationId, mn.content,mn.sendAt) " +
            "FROM MessageNotify mn " +
            "JOIN UserProfile u ON mn.senderId=u.id " +
            "WHERE mn.receiveId=:id ")
    List<MessageNotifyDTO> findByUser(@Param("id")Integer id);


    List<MessageNotify> findByReceiveId(Integer id);

    @Modifying
    @Transactional
    @Query("DELETE FROM MessageNotify WHERE receiveId=:userId AND conversationId=:conversationId")
    void deleteByUserConversationId(@Param("userId")Integer userId, @Param("conversationId")Integer conversationId);


}

