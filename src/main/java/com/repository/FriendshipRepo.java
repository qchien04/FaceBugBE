package com.repository;

import com.DTO.FriendDTO;
import com.entity.Friendship;
import com.entity.auth.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

public interface FriendshipRepo extends JpaRepository<Friendship, Long> {

    @Query("SELECT new com.DTO.FriendDTO(u.friendProfile.id, u.friendProfile.name, u.friendProfile.avt) " +
            "FROM Friendship u " +
            "WHERE u.userProfile.id = :id " +
            "AND u.friendProfile.name LIKE %:key%")
    List<FriendDTO> findByUserProfileId(@Param("id") Integer id, @Param("key") String key);

    @Query("SELECT u.friendProfile.id " +
            "FROM Friendship u " +
            "WHERE u.userProfile.id = :id "
            )
    List<Integer> findAllFriendByUserProfileId(@Param("id") Integer id);

    @Query(value = "SELECT EXISTS ( " +
            "   SELECT 1 FROM friendship f " +
            "   WHERE (f.user_id = :userId AND f.friend_id = :friendId) " +
            "      OR (f.user_id = :friendId AND f.friend_id = :userId) " +
            ")",
            nativeQuery = true)
    boolean existsFriendship(@Param("userId") Integer userId,
                             @Param("friendId") Integer friendId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Friendship u WHERE (u.userProfile.id = :id1 AND u.friendProfile.id = :id2) " +
            "OR (u.userProfile.id = :id2 AND u.friendProfile.id = :id1)")
    void unFriend(@Param("id1")Integer userId, @Param("id2")Integer friendId);


}
