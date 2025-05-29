package com.repository;

import com.entity.postRanking.ActionOnPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActionOnPostRepo extends JpaRepository<ActionOnPost,Integer> {
    @Query("SELECT a.post.id, SUM(a.actionCore) " +
            "FROM ActionOnPost a WHERE a.timeAction >= :since " +
            "GROUP BY a.post")
    List<Object[]> calculateTotalScoreForPosts(@Param("since") LocalDateTime since);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM action_on_post WHERE time_action < NOW() - INTERVAL '2 days'", nativeQuery = true)
    void deleteOlderThan2Days();


}
