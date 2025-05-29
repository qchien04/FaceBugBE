package com.controller;

import com.DTO.PostDTO;
import com.service.CustomUserDetails;
import com.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/{postId}/like")
    public void likePost(@PathVariable Integer postId, @RequestBody PostDTO PostDTO) {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        likeService.likePost(myId, PostDTO);
    }

    @DeleteMapping("/{postId}/unlike")
    public void unlikePost(@PathVariable Integer postId) {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        likeService.unlikePost(myId, postId);
    }

    @GetMapping("/{postId}/count")
    public long countLikes(@PathVariable Integer postId) {
        return likeService.countLikes(postId);
    }

    @GetMapping("/{postId}/isLiked")
    public boolean islikedHandle(@PathVariable Integer postId) {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return likeService.isLiked(myId,postId);
    }
}
