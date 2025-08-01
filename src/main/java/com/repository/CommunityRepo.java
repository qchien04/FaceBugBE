package com.repository;

import com.DTO.CommunityDTO;
import com.entity.Group.Community;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface CommunityRepo extends JpaRepository<Community, Integer> {
    @Query(value = """
        SELECT
            g.id            AS communityId,
            g.name          AS communityName,
            g.description   AS communityDescription,
            g.cover_photo   AS coverPhoto,
            g.privacy        AS privacy,
            COUNT(cu.user_profile)   AS totalMembers,
           (
              SELECT STRING_AGG(limited_avts.avt, ',')
              FROM (
                  SELECT up.avt
                  FROM community_userprofile cu2
                  JOIN userprofile up ON cu2.user_profile = up.user_id
                  WHERE cu2.community = g.id
                  AND cu2.community_role != 'PENDING'
                  LIMIT 10
              ) AS limited_avts
          ) AS avts
        FROM community g
        JOIN community_userprofile cu ON cu.community = g.id
        WHERE g.id = :communityId
        GROUP BY g.id
    """, nativeQuery = true)
    CommunityDTO findCommunityInfo(@Param("communityId") Integer communityId);


    @Query(value = """
        SELECT
            g.id            AS communityId,
            g.name          AS communityName,
            g.description   AS communityDescription,
            g.cover_photo   AS coverPhoto,
            g.privacy       AS privacy,
            COUNT(cu2.user_profile) AS totalMembers,
            (
                SELECT STRING_AGG(up2.avt, ',')
                FROM community_userprofile cu3
                JOIN userprofile up2 ON cu3.user_profile = up2.user_id
                WHERE cu3.community = g.id
                LIMIT 10
            ) AS avts
        FROM community g
        JOIN community_userprofile cu1 ON cu1.community = g.id
        JOIN community_userprofile cu2 ON cu2.community = g.id
        WHERE cu1.user_profile = :userId
        GROUP BY g.id
    """, nativeQuery = true)
    List<CommunityDTO> findAllCommunitiesOfUser(@Param("userId") Integer userId);

    @Query(value = """
        SELECT
            g.id            AS communityId,
            g.name          AS communityName,
            g.description   AS communityDescription,
            g.cover_photo   AS coverPhoto,
            g.privacy       AS privacy,
            COUNT(cu.user_profile) AS totalMembers
        FROM community g
        JOIN community_userprofile cu ON cu.community = g.id
        WHERE g.name LIKE CONCAT('%', :key, '%')
        GROUP BY g.id
    """, nativeQuery = true)
    Page<CommunityDTO> searchCommunity(@Param("key") String key, Pageable pageable);
}
