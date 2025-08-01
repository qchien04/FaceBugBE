package com.service;

import com.DTO.ProfileSummary;
import com.constant.FriendRequestStatus;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface FriendRequestService {
    public List<ProfileSummary> getAllRequest();
    public void sendFriendRequest(Integer sender, Integer receiver);
    public void acceptFriendRequest(Integer senderId, Integer receiverId);
    public void removeFriendRequest(Integer senderId, Integer receiverId);
    public void refuseFriendRequest(Integer senderId, Integer receiverId);
    public FriendRequestStatus checkFriendRequestStatus(Integer senderId, Integer receiverId);
}
