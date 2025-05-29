package com.service.imple;


import com.DTO.FriendDTO;
import com.entity.auth.User;
import com.entity.auth.UserProfile;
import com.exception.UserException;
import com.repository.UserProfileRepo;
import com.security.TokenProvider;
import com.service.auth.UserProfileService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class UserProfileServiceImp implements UserProfileService {

    private final UserProfileRepo userProfileRepo;

    private TokenProvider tokenProvider;


    public UserProfileServiceImp(UserProfileRepo userProfileRepo,TokenProvider tokenProvider) {
        this.userProfileRepo = userProfileRepo;
        this.tokenProvider=tokenProvider;
    }


    @Override
    @Transactional
    public UserProfile save(UserProfile userProfile) {
        return userProfileRepo.save(userProfile);
    }

    @Override
    public FriendDTO findById(int id) {
        return userProfileRepo.searchUserProfileDTO(id);
    }

    @Override
    public void deleteById(int id) {

    }

    @Override
    public UserProfile findByUsername(String username) {
        return null;
    }

    @Override
    public UserProfile findByUserProfileId(int userId) throws UserException {
        Optional<UserProfile> userProfile= userProfileRepo.findById(userId);
        if(userProfile==null){
            throw new UserException("User profile not found");
        }
        return userProfile.get();
    }

    @Override
    public List<UserProfile> searchUser(String query) {
        List<UserProfile> listUser=userProfileRepo.searchUserProfile(query);
        return listUser;
    }
}
