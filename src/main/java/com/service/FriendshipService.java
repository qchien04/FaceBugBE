package com.service;

import com.DTO.FriendDTO;
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
    public List<FriendDTO> getFriends(Integer userId,String key);
    public List<FriendDTO> getFriendsByUserId(Integer userId);
    public Page<FriendDTO> searchProfile(String key, Boolean isPage, Pageable pageable);
}
