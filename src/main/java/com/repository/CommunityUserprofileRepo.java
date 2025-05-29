package com.repository;

import com.entity.Group.CommunityUserprofile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommunityUserprofileRepo extends JpaRepository<CommunityUserprofile, Integer> {
    @Query(value = """
    SELECT
        COALESCE(
            (
                SELECT cu.community_role
                FROM community_userprofile cu
                WHERE cu.community = :communityId
                  AND cu.user_profile = :userProfileId
                LIMIT 1
            ), 'NONE'
        ) AS role
    """, nativeQuery = true)
    String findCommunityRole(
            @Param("userProfileId") Integer userProfileId,
            @Param("communityId") Integer communityId
    );

    Optional<CommunityUserprofile> findByUserProfileIdAndCommunityId(Integer userProfileId, Integer communityId);


    @Query("SELECT cu " +
            "FROM CommunityUserprofile cu JOIN FETCH cu.userProfile " +
            "WHERE cu.community.id=:communityId AND cu.communityRole!=CommunityRole.PENDING")
    List<CommunityUserprofile> findMembersByCommunityId(@Param("communityId") Integer communityId);

    @Query("SELECT cu.userProfile.id " +
            "FROM CommunityUserprofile cu " +
            "WHERE cu.community.id=:communityId " +
            "AND cu.communityRole!=CommunityRole.PENDING")
    List<Integer> findMemberIdsByCommunityId(@Param("communityId") Integer communityId);

    @Query("SELECT cu " +
            "FROM CommunityUserprofile cu JOIN FETCH cu.userProfile " +
            "WHERE cu.community.id=:communityId AND cu.communityRole=CommunityRole.PENDING")
    List<CommunityUserprofile> findPendingMembersByCommunityId(@Param("communityId") Integer communityId);
}
