package com.repository;



import com.DTO.ProfileSummary;
import com.entity.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepo extends JpaRepository<FriendRequest, Integer> {
    Optional<FriendRequest> findBySenderIdAndReceiverId(Integer senderId, Integer receiverId);

    @Query("SELECT new com.DTO.ProfileSummary(f.sender.id, f.sender.name, f.sender.avt) " +
            "FROM FriendRequest f " +
            "JOIN f.sender " +
            "WHERE f.receiver.id = :userId ")
    List<ProfileSummary> findFriendRequests(
            @Param("userId") Integer userId
    );

    @Query(value = "SELECT " +
            "   CASE " +
            "       WHEN EXISTS ( " +
            "           SELECT 1 FROM friend_request f " +
            "           WHERE f.sender_id = :senderId AND f.receiver_id = :receiverId " +
            "       ) THEN 'SENT' " + // Người gửi đã gửi lời mời
            "       WHEN EXISTS ( " +
            "           SELECT 1 FROM friend_request f " +
            "           WHERE f.sender_id = :receiverId AND f.receiver_id = :senderId " +
            "       ) THEN 'RECEIVED' " + // Người nhận đã gửi lời mời
            "       WHEN EXISTS ( " +
            "           SELECT 1 FROM friendship " +
            "           WHERE (user_profile = :senderId AND friend_profile = :receiverId) " +
            "              OR (user_profile = :receiverId AND friend_profile = :senderId) " +
            "       ) THEN 'FRIENDS' " + // Đã là bạn bè
            "       ELSE 'NONE' " + // Không là gì của nhau
            "   END",
            nativeQuery = true)
    String checkFriendRequestStatus(@Param("senderId") Integer senderId,
                                    @Param("receiverId") Integer receiverId);



    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END " +
            "FROM FriendRequest f " +
            "WHERE (f.sender.id = :userId AND f.receiver.id = :friendId) " +
            "   OR (f.sender.id = :friendId AND f.receiver.id = :userId) " +
            "   OR EXISTS (SELECT 1 FROM Friendship fs " +
            "             WHERE (fs.userProfile.id = :userId AND fs.friendProfile.id = :friendId) " +
            "                OR (fs.userProfile.id = :friendId AND fs.friendProfile.id = :userId))")
    boolean existsFriendshipOrRequest(@Param("userId") Integer userId, @Param("friendId") Integer friendId);


}
