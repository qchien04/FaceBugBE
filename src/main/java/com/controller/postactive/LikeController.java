package com.controller.postactive;

import com.service.imple.CustomUserDetails;
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
    public void likePost(@PathVariable Integer postId) {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        likeService.likePost(myId, postId);
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
