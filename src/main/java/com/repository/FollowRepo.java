package com.repository;

import com.DTO.ProfileSummary;
import com.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowRepo extends JpaRepository<Follow, Integer> {

    @Query("SELECT new com.DTO.ProfileSummary(u.pageProfile.id, u.pageProfile.name, u.pageProfile.avt)" +
            "FROM Follow u " +
            "WHERE u.followerProfile.id = :id")
    List<ProfileSummary> findALlPageUserFollow(@Param("id") Integer id);

    @Query("SELECT f.followerProfile.id " +
            "FROM Follow f " +
            "WHERE f.pageProfile.id = :id "
            )
    List<Integer> findAllFollowerByProfileId(@Param("id") Integer id);

    @Modifying
    @Query("DELETE FROM Follow f WHERE f.followerProfile.id = :followerId AND f.pageProfile.id = :pageId")
    void unfollow(@Param("followerId") Integer followerId, @Param("pageId") Integer pageId);

    @Query("SELECT COUNT(f) > 0 FROM Follow f WHERE f.followerProfile.id = :followerId AND f.pageProfile.id = :pageId")
    boolean isFollowing(@Param("followerId") Integer followerId, @Param("pageId") Integer pageId);


}
