package com.service.imple;

import com.constant.AccountType;
import com.entity.Friendship;
import com.entity.Post;
import com.entity.SuggestPost;
import com.entity.auth.UserProfile;
import com.repository.CommunityUserprofileRepo;
import com.repository.FollowRepo;
import com.repository.FriendshipRepo;
import com.repository.SuggestPostRepo;
import com.service.CustomUserDetails;
import com.service.FriendshipService;
import com.service.SuggestPostService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@AllArgsConstructor
public class SuggestPostServiceImp implements SuggestPostService {
    private final SuggestPostRepo suggestPostRepo;
    private final FriendshipRepo friendshipRepo;
    private final CommunityUserprofileRepo communityUserprofileRepo;
    private final FollowRepo followRepo;

    @Override
    @Transactional
    public void decreaseRenderTime(Integer profileSeen, List<Integer> postIds) {
        int num=suggestPostRepo.decreaseRenderTimeForPosts(profileSeen,postIds);
    }



    @Override
    @Async
    public void pushSuggestPost(AccountType accountType,Integer authorId,Post post,Boolean isGroup) {
        List<Integer> profiles;
        if(isGroup){
            profiles=communityUserprofileRepo.findMemberIdsByCommunityId(post.getCommunity().getId());
        }
        else{
            if(AccountType.PAGE.equals(accountType)){
                profiles= followRepo.findAllFollowerByProfileId(authorId);
            }
            else{
                profiles=friendshipRepo.findAllFriendByUserProfileId(authorId);
            }

        }

        List<SuggestPost> list = new ArrayList<>();
        for(Integer i:profiles){
            SuggestPost suggestPost=SuggestPost.builder().
                    postCreatedTime(LocalDateTime.now())
                    .renderTimeRemaining(3)
                    .profileSeen(UserProfile.builder().id(i).build())
                    .post(Post.builder().id(post.getId()).build())
                    .build();
            list.add(suggestPost);

        }
        suggestPostRepo.saveAll(list);
    }

    @Override
    public List<SuggestPost> getSuggestPosts(Integer profileSeen) {
        return suggestPostRepo.findByProfileSeenId(profileSeen);
    }
}
