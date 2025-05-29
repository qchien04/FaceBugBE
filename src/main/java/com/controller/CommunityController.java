package com.controller;


import com.DTO.CommunityDTO;
import com.DTO.CommunityUserprofileDTO;
import com.DTO.FriendDTO;
import com.DTO.PostDTO;
import com.constant.CommunityRole;
import com.entity.Group.Community;
import com.entity.Group.CommunityUserprofile;
import com.entity.auth.UserProfile;
import com.exception.CommunityException;
import com.response.ApiResponse;
import com.service.CustomUserDetails;
import com.service.UpLoadImageFileService;
import com.service.group.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommunityController {
    private final CommunityService communityService;
    private final UpLoadImageFileService upLoadImageFileService;

    @PostMapping("/create")
    public ResponseEntity<Community> createNotification(@RequestBody CommunityDTO communityDTO) {
        Community community = communityService.createCommunity(communityDTO.getCommunityName(), communityDTO.getPrivacy());
        return new ResponseEntity<Community>(community, HttpStatus.OK);
    }

    @GetMapping("/searchCommunity")
    public ResponseEntity<Page<CommunityDTO>> searchCommunityWithPagination(
            @RequestParam("key") String key,
            @RequestParam(value = "page", defaultValue = "0") int page,  // Trang bắt đầu từ 0
            @RequestParam(value = "size", defaultValue = "10") int size   // Kích thước trang mặc định là 10
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CommunityDTO> result = communityService.searchCommunity(key, pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{communityId}")
    public ResponseEntity<CommunityDTO> getNotifications(@PathVariable Integer communityId) {
        CommunityDTO communityDTO= communityService.getCommunityDTOById(communityId);
        return new ResponseEntity<CommunityDTO>(communityDTO, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CommunityDTO>> getall() {
        List<CommunityDTO> communityDTOs= communityService.getAllUserCommunity();
        return new ResponseEntity<List<CommunityDTO>>(communityDTOs, HttpStatus.OK);
    }

    @GetMapping("/{communityId}/inCommunity")
    public ResponseEntity<CommunityRole> checkIfin(@PathVariable Integer communityId) {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        CommunityRole exist= communityService.checkExits(myId,communityId);
        return new ResponseEntity<CommunityRole>(exist, HttpStatus.OK);
    }

    @GetMapping("/{communityId}/members")
    public ResponseEntity<List<CommunityUserprofileDTO>> members(@PathVariable Integer communityId) {
        List<CommunityUserprofileDTO> cu= communityService.findMembers(communityId);
        return new ResponseEntity<List<CommunityUserprofileDTO>>(cu, HttpStatus.OK);
    }

    @GetMapping("/{communityId}/pendingMembers")
    public ResponseEntity<List<CommunityUserprofileDTO>> pendingMembers(@PathVariable Integer communityId) {
        List<CommunityUserprofileDTO> cu= communityService.findPendingMembers(communityId);
        return new ResponseEntity<List<CommunityUserprofileDTO>>(cu, HttpStatus.OK);
    }

    @PostMapping("/{communityId}/outCommunity")
    public ResponseEntity<ApiResponse> refuseRequestMakeFriendHandler(@PathVariable("communityId") Integer communityId) {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        communityService.outCommunity(myId, communityId);
        ApiResponse res = new ApiResponse("Successfully...", true);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/{communityId}/joinCommunity")
    public ResponseEntity<CommunityRole> joinCommunityHandler(@PathVariable("communityId") Integer communityId) {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        CommunityUserprofile newCu=communityService.joinCommunity(myId, communityId);
        return new ResponseEntity<CommunityRole>(newCu.getCommunityRole(), HttpStatus.OK);
    }

    @PostMapping("/{communityId}/acceptJoinCommunity")
    public ResponseEntity<ApiResponse> acceptJoinCommunity(@PathVariable("communityId") Integer communityId,@RequestBody Integer userId) {
        communityService.acceptJoinCommunity(userId, communityId);
        ApiResponse res = new ApiResponse("Successfully...", true);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/{communityId}/inviteCommunity")
    public ResponseEntity<ApiResponse> inviteCommunityHandler(@PathVariable("communityId") Integer communityId, @RequestBody FriendDTO friendDTO) {
        communityService.inviteCommunity(friendDTO.getFriendId(), communityId);
        ApiResponse res = new ApiResponse("Successfully...", true);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/{communityId}/description")
    public ResponseEntity<ApiResponse> inviteCommunityHandler(@PathVariable("communityId") Integer communityId, @RequestBody String description) {
        communityService.changeDescription(communityId, description);
        ApiResponse res = new ApiResponse("Successfully...", true);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping("/{communityId}/deleteMember")
    public ResponseEntity<ApiResponse> kickMemberHandle(@PathVariable("communityId") Integer communityId,@RequestParam("profileId") Integer profileId) {
        communityService.kickMember(profileId, communityId);
        ApiResponse res = new ApiResponse("Successfully...", true);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/changeCoverPhoto")
    public ResponseEntity<String> createNotification(@RequestParam("media") MultipartFile media,
                                                     @RequestParam("communityId") Integer communityId) throws IOException {

        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        CommunityRole communityRole=communityService.checkExits(myId,communityId);
        if(communityRole.equals(CommunityRole.ADMIN)){
            Community community=communityService.getCommunityById(communityId);
            String newCoverPhoto=upLoadImageFileService.uploadImage(media);

            community.setCoverPhoto(newCoverPhoto);
            communityService.save(community);
            return new ResponseEntity<String>(newCoverPhoto, HttpStatus.OK);
        }
        else throw new CommunityException("Not permission");

    }
}

