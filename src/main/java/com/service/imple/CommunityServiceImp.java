package com.service.imple;


import com.DTO.CommunityDTO;
import com.DTO.CommunityUserprofileDTO;
import com.DTO.FriendDTO;
import com.constant.CommunityRole;
import com.constant.NotificationType;
import com.constant.Privacy;
import com.entity.Group.Community;
import com.entity.Group.CommunityUserprofile;
import com.entity.auth.UserProfile;
import com.entity.notify.Notification;
import com.entity.notify.NotificationAction;
import com.exception.CommunityException;
import com.mapper.CommunityUserprofileMapper;
import com.repository.CommunityRepo;
import com.repository.CommunityUserprofileRepo;
import com.repository.UserProfileRepo;
import com.service.CustomUserDetails;
import com.service.SseService;
import com.service.group.CommunityService;
import com.service.noitify.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CommunityServiceImp implements CommunityService {
    private final CommunityRepo communityRepo;
    private final CommunityUserprofileRepo communityUserprofileRepo;
    private final UserProfileRepo userProfileRepo;
    private final NotificationService notificationService;
    private final CommunityUserprofileMapper communityUserprofileMapper;
    private final SseService sseService;


    @Override
    public Community createCommunity(String name, Privacy privacy) {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        UserProfile userProfile=userProfileRepo.findById(myId).get();

        Community newgroup= Community.builder().name(name)
                .privacy(privacy)
                .coverPhoto("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRciEnKmy_N2HRYMFez7oYZO4pK0aE8vbHO9A&s")
                .build();

        newgroup= communityRepo.save(newgroup);

        CommunityUserprofile communityUserprofile = CommunityUserprofile.builder()
                .communityRole(CommunityRole.ADMIN)
                .userProfile(userProfile)
                .community(newgroup)
                .joinAt(LocalDateTime.now())
                .build();
        communityUserprofileRepo.save(communityUserprofile);

        return newgroup;
    }

    @Override
    public CommunityDTO getCommunityDTOById(Integer id) {
        CommunityDTO communityDTO=communityRepo.findCommunityInfo(id);
        return communityDTO;
    }

    @Override
    public Community getCommunityById(Integer id) {
        Community community=communityRepo.findById(id).get();
        return community;
    }

    @Override
    public Community save(Community community) {
        return communityRepo.save(community);
    }

    @Override
    public List<CommunityDTO> getAllUserCommunity() {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        List<CommunityDTO> ans = communityRepo.findAllCommunitiesOfUser(myId);
        return ans;
    }

    @Override
    public Page<CommunityDTO> searchCommunity(String key, Pageable pageable) {
        Page<CommunityDTO> ans = communityRepo.searchCommunity(key,pageable);
        return ans;
    }

    @Override
    public void changeDescription(Integer communityId, String description) {
        Community community=communityRepo.findById(communityId).get();

        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        CommunityUserprofile member=communityUserprofileRepo.findByUserProfileIdAndCommunityId(myId,communityId).get();

        if(member.getCommunityRole().equals(CommunityRole.ADMIN)){
            community.setDescription(description);
        }
        else throw new CommunityException("Khong co quyen");


        community.setDescription(description.substring(1,description.length()-1));
        communityRepo.save(community);
    }

    @Override
    public List<CommunityUserprofileDTO> findMembers(Integer communityId) {
        List<CommunityUserprofile> communityUserprofile = communityUserprofileRepo.findMembersByCommunityId(communityId);

        return communityUserprofileMapper.toDTOs(communityUserprofile);
    }

    @Override
    public List<CommunityUserprofileDTO> findPendingMembers(Integer communityId) {
        List<CommunityUserprofile> communityUserprofile = communityUserprofileRepo.findPendingMembersByCommunityId(communityId);
        return communityUserprofileMapper.toDTOs(communityUserprofile);
    }

    @Override
    public CommunityRole checkExits(Integer userProfileId, Integer communityId) {
        CommunityRole communityRole=CommunityRole.valueOf(
                communityUserprofileRepo.findCommunityRole(userProfileId,communityId));

        return communityRole;
    }

    @Override
    public CommunityUserprofile joinCommunity(Integer userProfileId, Integer communityId) {
        UserProfile userProfile=UserProfile.builder().id(userProfileId).build();

        Optional<CommunityUserprofile> checkExist=communityUserprofileRepo.findByUserProfileIdAndCommunityId(userProfileId,communityId);
        if(checkExist.isPresent()){
            throw new CommunityException("Đã tồn tại trong nhóm");
        }
        else{
            Community community=communityRepo.findById(communityId).get();

            CommunityUserprofile communityUserprofile = CommunityUserprofile.builder()
                    .communityRole(CommunityRole.MEMBER)
                    .userProfile(userProfile)
                    .community(community)
                    .joinAt(LocalDateTime.now())
                    .build();

            if(community.getPrivacy().equals(Privacy.PRIVATE)){
                communityUserprofile.setCommunityRole(CommunityRole.PENDING);
            }
            CommunityUserprofile newCu=communityUserprofileRepo.save(communityUserprofile);

            return newCu;
        }
    }

    @Override
    public void kickMember(Integer userProfileId, Integer communityId) {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        CommunityRole adminRole=CommunityRole.valueOf(communityUserprofileRepo.findCommunityRole(myId,communityId));
        CommunityUserprofile member=communityUserprofileRepo.findByUserProfileIdAndCommunityId(userProfileId,communityId)
                .orElseThrow(() -> new CommunityException("User not found in community"));

        if(adminRole.equals(CommunityRole.ADMIN)
                && member.getCommunityRole().equals(CommunityRole.MEMBER)){
            outCommunity(userProfileId,communityId);
            Community community=communityRepo.findById(communityId).get();
            Notification joinSuccessNoti= Notification.builder()
                    .link("/community/"+communityId)
                    .isClicked(false)
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .type(NotificationType.NORMAL)
                    .message("Bạn bị kick ra khỏi nhóm!")
                    .avt(community.getCoverPhoto())
                    .actions(null)
                    .receive(UserProfile.builder().id(userProfileId).build())
                    .sender(UserProfile.builder().id(myId).build())
                    .build();

            Notification notification = notificationService.createNotification(joinSuccessNoti);
            sseService.pushNotify(userProfileId,notification);
        }
        else throw new CommunityException("Không có quyền");
    }

    @Override
    public void acceptJoinCommunity(Integer userProfileId, Integer communityId) {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        CommunityRole adminRole=CommunityRole.valueOf(communityUserprofileRepo.findCommunityRole(myId,communityId));
        CommunityUserprofile pendingMember=communityUserprofileRepo.findByUserProfileIdAndCommunityId(userProfileId,communityId)
                .orElseThrow(() -> new CommunityException("User not found in community"));

        if(adminRole.equals(CommunityRole.ADMIN)
                && pendingMember.getCommunityRole().equals(CommunityRole.PENDING)){
            pendingMember.setCommunityRole(CommunityRole.MEMBER);

            Community community=communityRepo.findById(communityId).get();
            Notification joinSuccessNoti= Notification.builder()
                    .link("/community/"+communityId)
                    .isClicked(false)
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .type(NotificationType.NORMAL)
                    .message("Tham gia nhóm thành công")
                    .avt(community.getCoverPhoto())
                    .actions(null)
                    .receive(UserProfile.builder().id(userProfileId).build())
                    .sender(UserProfile.builder().id(myId).build())
                    .build();

            Notification notification = notificationService.createNotification(joinSuccessNoti);
            sseService.pushNotify(userProfileId,notification);
            communityUserprofileRepo.save(pendingMember);
        }
        else throw new CommunityException("Không có quyền");
    }

    @Override
    public void outCommunity(Integer userProfileId, Integer communityId) {
        Optional<CommunityUserprofile> cu = communityUserprofileRepo
                .findByUserProfileIdAndCommunityId(userProfileId, communityId);

        cu.ifPresent(communityUserprofileRepo::delete);
    }

    @Override
    public void inviteCommunity(Integer userProfileId, Integer communityId) {

        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        Optional<CommunityUserprofile> checkExist=communityUserprofileRepo.findByUserProfileIdAndCommunityId(userProfileId,communityId);
        if(checkExist.isPresent()){
            throw new CommunityException("Đã tồn tại trong nhóm");
        }
        else{
            Community community=communityRepo.findById(communityId).get();
            FriendDTO friendDTO=userProfileRepo.searchUserProfileDTO(myId);

            List<NotificationAction> actions=new ArrayList<>();
            NotificationAction action=new NotificationAction("Chấp nhận",
                    "/community/"+communityId+"/joinCommunity",
                    "POST");
            NotificationAction action2=new NotificationAction("Từ chối",
                    "",
                    "POST");
            actions.add(action);
            actions.add(action2);

            Notification addFriendNotification=Notification.builder()
                    .link("/community/"+communityId)
                    .isClicked(false)
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .type(NotificationType.INVITE_COMMUNITY)
                    .message(friendDTO.getFriendName()+" đã mời bạn vào nhóm "+community.getName())
                    .avt(friendDTO.getFriendAvt())
                    .actions(actions)
                    .receive(UserProfile.builder().id(userProfileId).build())
                    .sender(UserProfile.builder().id(myId).build())
                    .build();

            notificationService.createNotification(addFriendNotification);
        }
    }

}
