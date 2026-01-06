package com.repository;

import com.constant.AccountType;
import com.constant.MediaType;
import com.entity.Post;
import com.entity.postRanking.PostRanking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRankingRepo extends JpaRepository<PostRanking,Integer> {
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM post_ranking WHERE time_calculator < NOW()", nativeQuery = true)
    void deleteOlderThan6Days();

    @Query(value = "SELECT COUNT(*) FROM post_ranking WHERE time_calculator > NOW()", nativeQuery = true)
    int countEligiblePosts();

    @Query(value = """   
    SELECT p.post
    FROM post_ranking p
    JOIN post po ON po.id=p.post
    JOIN userprofile u ON u.user_id=po.author_id
    WHERE p.time_calculator > NOW() - INTERVAL '6 days' AND u.account_type='PAGE'
    LIMIT 10 OFFSET :offset
    """, nativeQuery = true)
    List<Integer> findRecommendedPosts(@Param("offset") int offset);

    @Query("""
    SELECT p
    FROM PostRanking pr
    JOIN pr.post p
    JOIN FETCH p.author a
    WHERE pr.timeCalculator > :timeThreshold
      AND a.accountType = :accountType
      AND p.mediaType = :mediaType
    ORDER BY pr.core DESC
    """)
    List<Post> findRecommendedPostsVideo(
            @Param("accountType") AccountType accountType,
            @Param("mediaType") MediaType mediaType,
            @Param("timeThreshold") LocalDateTime timeThreshold
    );


}
