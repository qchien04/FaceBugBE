package com.controller.relationship;


import com.constant.FollowState;
import com.response.ApiResponse;
import com.service.FollowService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/follow")
@AllArgsConstructor
public class FollowController {

    private FollowService followService;

    @PostMapping("/{id}")
    public ResponseEntity<ApiResponse> followPageHandle(@PathVariable("id") Integer pageId) {

        followService.followPage(pageId);

        ApiResponse res = new ApiResponse("Successfully...", true);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/{id}/unfollow")
    public ResponseEntity<ApiResponse> unfollowPageHandle(@PathVariable("id") Integer pageId) {

        followService.unFollowPage(pageId);

        ApiResponse res = new ApiResponse("Successfully...", true);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/{id}/checkFollow")
    public ResponseEntity<FollowState> isFollowHandle(@PathVariable("id") Integer pageId) {

        FollowState followState=followService.checkFollow(pageId);

        return new ResponseEntity<FollowState>(followState, HttpStatus.OK);
    }



}