package com.service.imple;


import com.DTO.ProfileSummary;
import com.DTO.ProfileSummary;
import com.entity.auth.UserProfile;
import com.exception.UserException;
import com.mapper.UserProfileMapper;
import com.repository.UserProfileRepo;
import com.service.auth.UserProfileService;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
@AllArgsConstructor
public class UserProfileServiceImp implements UserProfileService {

    private final UserProfileRepo userProfileRepo;
    private final UserProfileMapper userProfileMapper;



    @Override
    @Transactional
    public UserProfile save(UserProfile userProfile) {
        return userProfileRepo.save(userProfile);
    }

    @Override
    public ProfileSummary findById(int id) {
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
    public List<UserProfile> findByAccountId(int accountId) throws UserException {
        List<UserProfile> userProfiles= userProfileRepo.findByUserId(accountId);

        return userProfiles;
    }

    @Override
    public List<ProfileSummary> findSummaryByAccountId(int accountId) throws UserException {
        List<UserProfile> userProfiles= userProfileRepo.findByUserId(accountId);

        return userProfileMapper.toProfileSummaries(userProfiles);
    }

    @Override
    public List<UserProfile> searchUser(String query) {
        List<UserProfile> listUser=userProfileRepo.searchUserProfile(query);
        return listUser;
    }
}
