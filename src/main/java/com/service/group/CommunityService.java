package com.service.group;

import com.DTO.CommunityDTO;
import com.DTO.CommunityUserprofileDTO;
import com.constant.CommunityRole;
import com.constant.Privacy;
import com.entity.Group.Community;
import com.entity.Group.CommunityUserprofile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommunityService {
    Community createCommunity(String name, Privacy privacy);
    CommunityDTO getCommunityDTOById(Integer id);
    Community getCommunityById(Integer id);


    Community save(Community community);

    List<CommunityDTO> getAllUserCommunity();
    Page<CommunityDTO> searchCommunity(String key, Pageable pageable);

    void changeDescription(Integer communityId,String description);
    List<CommunityUserprofileDTO> findMembers(Integer communityId);

    List<CommunityUserprofileDTO> findPendingMembers(Integer communityId);
    CommunityRole checkExits(Integer userProfileId, Integer communityId);
    CommunityUserprofile joinCommunity(Integer userProfileId, Integer communityId);
    void kickMember(Integer userProfileId, Integer communityId);
    void acceptJoinCommunity(Integer userProfileId, Integer communityId);

    void outCommunity(Integer userProfileId,Integer communityId);
    void inviteCommunity(Integer userProfileId,Integer communityId);


}
