package com.repository;

import com.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepo extends JpaRepository<Like, Integer> {
    boolean existsByUserProfileIdAndPostId(Integer userId, Integer postId);
    void deleteByUserProfileIdAndPostId(Integer userId, Integer postId);
    long countByPostId(Integer postId);
}