package com.controller.relationship;


import com.DTO.ProfileSummary;
import com.constant.FriendRequestStatus;
import com.response.ApiResponse;
import com.service.imple.CustomUserDetails;
import com.service.FriendRequestService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friendRequest")
@AllArgsConstructor
public class FriendRequestController {
    private FriendRequestService friendRequestService;

    @PostMapping("/sendRequest/{id}")
    public ResponseEntity<FriendRequestStatus> sendRequestMakeFriendHandler(@PathVariable("id") Integer friendId) {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        friendRequestService.sendFriendRequest(myId,friendId);
        return new ResponseEntity<>(FriendRequestStatus.SENT, HttpStatus.OK);
    }


    @PostMapping("/acceptRequest/{friendId}")
    public ResponseEntity<ApiResponse> acceptRequestMakeFriendHandler(@PathVariable("friendId") Integer friendId) {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        friendRequestService.acceptFriendRequest(friendId, myId);

        ApiResponse res = new ApiResponse("Successfully...", true);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/removeRequest/{friendId}")
    public ResponseEntity<ApiResponse> removeRequestMakeFriendHandler(@PathVariable("friendId") Integer friendId) {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        friendRequestService.removeFriendRequest(myId, friendId);

        ApiResponse res = new ApiResponse("Successfully...", true);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/refuseRequest/{friendId}")
    public ResponseEntity<ApiResponse> refuseRequestMakeFriendHandler(@PathVariable("friendId") Integer friendId) {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        friendRequestService.refuseFriendRequest(friendId, myId);

        ApiResponse res = new ApiResponse("Successfully...", true);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProfileSummary>> allRequest() {
        List<ProfileSummary> request=friendRequestService.getAllRequest();
        return new ResponseEntity<>(request, HttpStatus.OK);
    }


    @GetMapping("/checkFriendStatus")
    public ResponseEntity<FriendRequestStatus> checkFriendStatusHandler(@RequestParam("userId") Integer userId) {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        FriendRequestStatus status=friendRequestService.checkFriendRequestStatus(myId, userId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }


}
