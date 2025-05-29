package com.repository;

import com.entity.SuggestPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuggestPostRepo extends JpaRepository<SuggestPost,Integer> {


    @Query(value = """
    SELECT *
    FROM suggest_post
    WHERE render_time_remaining > 0
    AND profile_seen = :profileSeenId
    ORDER BY RAND()
    LIMIT 5
    """, nativeQuery = true)
    List<SuggestPost> findByProfileSeenId(@Param("profileSeenId") Integer profileSeenId);

    @Modifying
    @Query("""
    UPDATE SuggestPost s
    SET s.renderTimeRemaining = s.renderTimeRemaining - 1
    WHERE s.profileSeen.id = :profileSeenId AND s.post.id IN :postIds
    """)
    int decreaseRenderTimeForPosts(@Param("profileSeenId") Integer profileSeenId,
                                   @Param("postIds") List<Integer> postIds);

    @Modifying
    @Query("DELETE FROM SuggestPost s WHERE s.renderTimeRemaining <= 0")
    int deleteAllExpired();
}
