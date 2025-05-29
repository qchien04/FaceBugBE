package com.repository;

import com.DTO.ConversationDTO;
import com.DTO.FriendDTO;
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
    @Query("UPDATE Conversation c SET c.lastMessage.id= :lastMessage, c.userSendLastMessage.id = :userId WHERE c.id = :id")
    int updatePreviewConversation(@Param("id") Integer id, @Param("userId") Integer userId, @Param("lastMessage") Integer lastMessage);


    @Query(value = "SELECT \n" +
            "    c.id AS id,\n" +
            "    CASE \n" +
            "        WHEN c.type_room = 0 THEN COALESCE(up2.name, c.name) \n" +
            "        ELSE c.name \n" +
            "    END AS name,\n" +
            "    CASE \n" +
            "        WHEN c.type_room = 0 THEN COALESCE(up2.avt, c.avt) \n" +
            "        ELSE c.avt \n" +
            "    END AS avt,\n" +
            "    c.type_room AS type,\n" +
            "    cu.role AS conversationRole,\n" +
            "    CASE \n" +
            "        WHEN m.id = 1 THEN 'Start' \n" +
            "        WHEN m.message_type = 'TEXT' THEN COALESCE(m.content, '')  \n" +
            "        ELSE 'Gửi 1 ảnh' \n" +
            "    END AS lastMessage, \n" +
            "    CASE \n" +
            "        WHEN m.id = 1 THEN '' \n" +
            "        WHEN c.user_send_last_message = 1 THEN 'Bạn' \n" +
            "        ELSE COALESCE(up.name, 'Unknown') \n" +
            "    END AS userSendLast, \n" +
            "    c.updated_at AS updatedAt\n" +
            "FROM (\tSELECT *\n" +
            "\t\tFROM conversation_user as cc\n" +
            "\t\tWHERE cc.userprofile_id=:userId ) cu\n" +
            "JOIN conversation c ON c.id = cu.conversation_id\n" +
            "LEFT JOIN conversation_user cu2 ON cu2.conversation_id = c.id AND cu2.userprofile_id != cu.userprofile_id AND c.type_room = 0\n" +
            "LEFT JOIN userprofile up ON up.user_id = c.user_send_last_message\n" +
            "LEFT JOIN userprofile up2 ON up2.user_id = cu2.userprofile_id\n" +
            "LEFT JOIN message m ON c.last_message = m.id\n",
            nativeQuery = true)
    List<ConversationDTO> findAllConversationByUserId(@Param("userId") Integer userId);

    @Query(value = "SELECT \n" +
            "    c.id AS id,\n" +
            "    CASE \n" +
            "        WHEN c.type_room = 0 THEN up2.name \n" +
            "        ELSE c.name \n" +
            "    END AS name,\n" +
            "    CASE \n" +
            "        WHEN c.type_room = 0 THEN up2.avt \n" +
            "        ELSE c.avt \n" +
            "    END AS avt,\n" +
            "    c.type_room AS type,\n" +
            "    cu2.role AS conversationRole,\n" +
            "    CASE \n" +
            "        WHEN m.id = 1 THEN 'Start' \n" +
            "        WHEN m.message_type = 'TEXT' THEN COALESCE(m.content, '')  \n" +
            "        ELSE 'Gửi 1 ảnh' \n" +
            "    END AS lastMessage, \n" +
            "    CASE \n" +
            "        WHEN m.id = 1 THEN '' \n" +
            "        WHEN c.user_send_last_message = 1 THEN 'Bạn' \n" +
            "        ELSE COALESCE(up.name, 'Unknown') \n" +
            "    END AS userSendLast, \n" +
            "    c.updated_at AS updatedAt\n" +
            "FROM (\tSELECT *\n" +
            "\t\tFROM conversation\n" +
            "\t\tWHERE id=:conversationId) c\n" +
            "LEFT JOIN conversation_user cu2 ON cu2.conversation_id = c.id AND cu2.userprofile_id =:userId\n" +
            "LEFT JOIN conversation_user cu ON cu.conversation_id = c.id AND cu.userprofile_id !=:userId AND c.type_room = 0\n" +
            "LEFT JOIN userprofile up ON up.user_id = c.user_send_last_message\n" +
            "LEFT JOIN userprofile up2 ON up2.user_id = cu.userprofile_id\n" +
            "LEFT JOIN message m ON c.last_message = m.id",
            nativeQuery = true)
    ConversationDTO findConversationById(@Param("conversationId") Integer conversationId,@Param("userId") Integer userId);




    @Query("SELECT DISTINCT new com.DTO.MemberGroupChatDTO(up.id, up.name, up.avt, cu1.role) " +
            "FROM ConversationUser cu1 " +
            "JOIN UserProfile up ON cu1.userProfile.id = up.id " +
            "WHERE cu1.conversation.id = :conversationId " +
            "AND up.id <> :userId " +
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
