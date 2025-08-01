package com.service.imple;

import com.DTO.ProfileSummary;
import com.entity.Friendship;
import com.entity.auth.UserProfile;
import com.repository.FriendshipRepo;
import com.repository.UserProfileRepo;
import com.repository.UserRepo;
import com.service.FriendshipService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class FriendshipServiceImp implements FriendshipService{
    private FriendshipRepo friendshipRepository;
    private UserRepo userRepo;
    private UserProfileRepo userProfileRepo;


    @Override
    public void addFriend(Integer userId, Integer remoteUserId) {
        UserProfile user = userProfileRepo.findById(userId).get();
        UserProfile friend = userProfileRepo.findById(remoteUserId).get();

        Friendship friendship = new Friendship();

        friendship.setUserProfile(user);
        friendship.setFriendProfile(friend);

        friendshipRepository.save(friendship);
    }

    @Override
    @Transactional
    public void unFriend(Integer userId, Integer remoteUserId) {
        friendshipRepository.unFriend(userId,remoteUserId);
    }

    @Override
    public List<ProfileSummary> getFriends(Integer userId,String key) {
        List<ProfileSummary> friendships = friendshipRepository.findByUserProfileId(userId,key);
        return friendships;
    }

    @Override
    public List<ProfileSummary> getFriendsByUserId(Integer userId) {
        return friendshipRepository.findByUserProfileId(userId,"");
    }

    @Override
    public Page<ProfileSummary> searchProfile(String key, Boolean isPage, Pageable pageable) {
        if(isPage){
            return userProfileRepo.searchPageProfiles(key, pageable);
        }
        else{
            return userProfileRepo.searchNormalUserProfiles(key, pageable);
        }
    }




}



