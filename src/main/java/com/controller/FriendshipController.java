package com.controller;


import com.DTO.FriendDTO;
import com.entity.auth.UserProfile;

import com.exception.ConversationException;
import com.exception.UserException;
import com.request.CreateConversationRequest;
import com.response.ApiResponse;

import com.response.FriendshipInfoResponse;
import com.service.CustomUserDetails;
import com.service.FriendshipService;
import com.service.SseService;
import com.service.auth.UserProfileService;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/friendship")
@AllArgsConstructor
public class FriendshipController {

    private UserProfileService userProfileService;
    private FriendshipService friendshipService;
    private SseService sseService;

    @GetMapping("/getFriendProfiles")
    public ResponseEntity<List<FriendDTO>> getFriendUserProfileHandler(@RequestParam("key") String key) {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        List<FriendDTO> friendProfiles=friendshipService.getFriends(myId,key);

        System.out.println(friendProfiles.size());

        return new ResponseEntity<List<FriendDTO>>(friendProfiles, HttpStatus.OK);
    }

    @GetMapping("/allFriend")
    public ResponseEntity<List<FriendDTO>> getFriendProfileHandler(@RequestParam("userId") Integer userId) {
        List<FriendDTO> friendProfiles=friendshipService.getFriendsByUserId(userId);

        return new ResponseEntity<List<FriendDTO>>(friendProfiles, HttpStatus.OK);
    }


    @PostMapping("/addFriend")
    public ResponseEntity<ApiResponse> createConversationHandler(@RequestBody CreateConversationRequest createConversationRequest) throws UserException, ConversationException {

        friendshipService.addFriend(createConversationRequest.getLocalUserId(), createConversationRequest.getRemoteUserId());
        ApiResponse res = new ApiResponse("Add Successfully...", true);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/searchFriend")  //search user toàn cục
    public ResponseEntity<Page<FriendDTO>> createConversationHandle2r(
            @RequestParam("key") String key,
            @RequestParam("isPage") Boolean isPage,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FriendDTO> arr=friendshipService.searchProfile(key,isPage,pageable);

        return new ResponseEntity<Page<FriendDTO>>(arr, HttpStatus.OK);
    }

    @PostMapping("/unFriend/{friendId}")
    public ResponseEntity< ApiResponse > unFriendHandle(@PathVariable("friendId") Integer friendId)  {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        friendshipService.unFriend(myId,friendId);

        ApiResponse res = new ApiResponse("Successfully...", true);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/onlines")
    public ResponseEntity< List<Integer> > getOnlineFriend(@RequestBody List<Integer> onlines)  {
        List<Integer> res=sseService.getOnlineUsers(onlines);

        return new ResponseEntity<List<Integer>>(res, HttpStatus.OK);
    }


}
