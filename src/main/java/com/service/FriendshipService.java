package com.service;

import com.DTO.ProfileSummary;
import com.entity.auth.User;
import com.entity.auth.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FriendshipService {

    public void addFriend(Integer userId, Integer remoteUserId);
    public void unFriend(Integer userId, Integer remoteUserId);
    public List<ProfileSummary> getFriends(Integer userId,String key);
    public List<ProfileSummary> getFriendsByUserId(Integer userId);
    public Page<ProfileSummary> searchProfile(String key, Boolean isPage, Pageable pageable);
}
