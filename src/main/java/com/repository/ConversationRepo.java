package com.repository;

import com.DTO.ConversationDTO;
import com.DTO.ProfileSummary;
import com.DTO.MemberGroupChatDTO;
import com.entity.chatrealtime.Conversation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;


@Repository
public interface ConversationRepo extends JpaRepository<Conversation,Integer> {

    @Modifying
    @Transactional
    @Query("UPDATE Conversation c SET c.lastMessage.id= :lastMessage WHERE c.id = :id")
    int updatePreviewConversation(@Param("id") Integer id, @Param("lastMessage") Integer lastMessage);


    @Query(value = "SELECT \n" +
            "    c.id AS id,\n" +   //id
            "    CASE \n" +         //tên đoạn chat
            "        WHEN c.type_room = 0 THEN up2.name \n" +
            "        ELSE c.name \n" +
            "    END AS name,\n" +

            "    CASE \n" +         // avt đoạn chat
            "        WHEN c.type_room = 0 THEN up2.avt \n" +
            "        ELSE c.avt \n" +
            "    END AS avt,\n" +

            "    c.type_room AS type,\n" + // Loại phòng
            "    cu.role AS conversationRole,\n" + //Role user hiện tại

            "    m.id AS messageId,"+
            "    m.content AS messageContent,"+
            "    m.message_type AS messageType,"+
            "    m.time_send AS timeSend,"+
            "    m.sender_id AS senderId,"+
            "    up.name AS senderName,"+

            "    c.updated_at AS updatedAt\n" +  //update at
            "FROM (\tSELECT *\n" +
            "\t\tFROM conversation_user as cc\n" +
            "\t\tWHERE cc.userprofile_id=:userId ) cu\n" +
            "JOIN conversation c ON c.id = cu.conversation_id\n" +
            "LEFT JOIN message m ON c.last_message = m.id\n"+ // message cuối
            "LEFT JOIN userprofile up ON up.user_id = m.sender_id\n"+ // up tên thằng gửi cuối

            "LEFT JOIN conversation_user cu2 ON cu2.conversation_id = c.id AND cu2.userprofile_id !=:userId AND c.type_room = 0\n" +
            "LEFT JOIN userprofile up2 ON up2.user_id = cu2.userprofile_id\n", //up2 avt thằng còn lại
            nativeQuery = true)
    List<ConversationDTO> findAllConversationByUserId(@Param("userId") Integer userId);


    @Query(value = "SELECT \n" +
            "    c.id AS id,\n" +   //id
            "    CASE \n" +         //tên đoạn chat
            "        WHEN c.type_room = 0 THEN up2.name \n" +
            "        ELSE c.name \n" +
            "    END AS name,\n" +

            "    CASE \n" +         // avt đoạn chat
            "        WHEN c.type_room = 0 THEN up2.avt \n" +
            "        ELSE c.avt \n" +
            "    END AS avt,\n" +

            "    c.type_room AS type,\n" + // Loại phòng
            "    cu.role AS conversationRole,\n" + //Role user hiện tại

            "    m.id AS messageId,"+
            "    m.content AS messageContent,"+
            "    m.message_type AS messageType,"+
            "    m.time_send AS timeSend,"+
            "    m.sender_id AS senderId,"+
            "    up.name AS senderName,"+

            "    c.updated_at AS updatedAt\n" +  //update at
            "FROM (\tSELECT *\n" +
            "\t\tFROM conversation\n" +
            "\t\tWHERE id=:conversationId ) c\n" +
            "LEFT JOIN message m ON c.last_message = m.id\n"+ // message cuối
            "LEFT JOIN userprofile up ON up.user_id = m.sender_id\n"+ // up tên thằng gửi cuối
            "LEFT JOIN conversation_user cu ON cu.conversation_id = c.id AND cu.userprofile_id =:userId "+
            "LEFT JOIN conversation_user cu2 ON cu2.conversation_id = c.id AND cu2.userprofile_id !=:userId AND c.type_room = 0\n" +
            "LEFT JOIN userprofile up2 ON up2.user_id = cu2.userprofile_id\n", //up2 avt thằng còn lại
            nativeQuery = true)
    ConversationDTO findConversationById(@Param("conversationId") Integer conversationId,@Param("userId") Integer userId);




    @Query("SELECT DISTINCT new com.DTO.MemberGroupChatDTO(cu1.conversation.id, up.id, up.name, up.avt, cu1.role) " +
            "FROM ConversationUser cu1 " +
            "JOIN UserProfile up ON cu1.userProfile.id = up.id " +
            "WHERE cu1.conversation.id = :conversationId " +
            "AND :userId IN (SELECT cu2.userProfile.id FROM ConversationUser cu2 " +
            "                WHERE cu2.conversation.id = :conversationId)")
    Set<MemberGroupChatDTO> findAllMembersInGroup(@Param("conversationId") Integer conversationId,
                                                  @Param("userId") Integer userId);



    @Query("SELECT c FROM Conversation c " +
            "JOIN ConversationUser cu ON cu.conversation.id = c.id " +
            "WHERE c.typeRoom = 0 AND cu.userProfile.id IN (:user1Id, :user2Id) " +
            "GROUP BY c.id " +
            "HAVING COUNT(DISTINCT cu.userProfile.id) = 2")
    List<Conversation> findIfConversationExist(@Param("user1Id") Integer user1Id,
                                               @Param("user2Id") Integer user2Id,
                                               Pageable pageable);
}
