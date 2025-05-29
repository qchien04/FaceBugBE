package com.service.auth;

import com.DTO.FriendDTO;
import com.entity.auth.UserProfile;
import com.exception.UserException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserProfileService {
    public UserProfile save(UserProfile userProfile);
    public FriendDTO findById(int id);
    public void deleteById(int id);
    public UserProfile findByUsername(String username);
    public UserProfile findByUserProfileId(int userId) throws UserException;
    public List<UserProfile> searchUser(String query);
}
