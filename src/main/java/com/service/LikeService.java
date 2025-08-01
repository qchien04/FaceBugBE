package com.service;

import com.DTO.PostDTO;

public interface LikeService {
    void likePost(Integer userId, Integer postId);
    void unlikePost(Integer userId, Integer postId);
    long countLikes(Integer postId);
    boolean isLiked(Integer userId, Integer postId);
}
