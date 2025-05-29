package com.service.imple;

import com.constant.FollowState;
import com.entity.Follow;
import com.entity.auth.UserProfile;
import com.repository.FollowRepo;
import com.service.CustomUserDetails;
import com.service.FollowService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class FollowServiceImp implements FollowService {
    private final FollowRepo followRepo;

    @Override
    public void followPage(Integer pageId) {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        UserProfile userProfile=UserProfile.builder().id(myId).build();
        UserProfile pageProfile=UserProfile.builder().id(pageId).build();

        Follow follow=Follow.builder().followerProfile(userProfile).pageProfile(pageProfile).build();

        followRepo.save(follow);
    }

    @Override
    @Transactional
    public void unFollowPage(Integer pageId) {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        followRepo.unfollow(myId,pageId);
    }

    @Override
    public FollowState checkFollow(Integer pageId) {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        boolean b=followRepo.isFollowing(myId,pageId);
        return b?FollowState.FOLLOW:FollowState.NONE;
    }
}
