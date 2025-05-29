package com.service;

import com.DTO.PostDTO;

public interface LikeService {
    void likePost(Integer userId, PostDTO postId);
    void unlikePost(Integer userId, Integer postId);
    long countLikes(Integer postId);
    boolean isLiked(Integer userId, Integer postId);
}
