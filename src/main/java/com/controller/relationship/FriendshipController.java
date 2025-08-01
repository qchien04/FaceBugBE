package com.controller.relationship;


import com.DTO.ProfileSummary;

import com.exception.ConversationException;
import com.exception.UserException;
import com.request.CreateConversationRequest;
import com.response.ApiResponse;

import com.service.imple.CustomUserDetails;
import com.service.FriendshipService;
import com.service.auth.UserProfileService;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/friendship")
@AllArgsConstructor
public class FriendshipController {

    private SimpUserRegistry simpUserRegistry;
    private UserProfileService userProfileService;
    private FriendshipService friendshipService;

    @GetMapping("/getFriendProfiles")
    public ResponseEntity<List<ProfileSummary>> getFriendUserProfileHandler(@RequestParam("key") String key) {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        List<ProfileSummary> friendProfiles=friendshipService.getFriends(myId,key);

        System.out.println(friendProfiles.size());

        return new ResponseEntity<List<ProfileSummary>>(friendProfiles, HttpStatus.OK);
    }

    @GetMapping("/allFriend")
    public ResponseEntity<List<ProfileSummary>> getFriendProfileHandler(@RequestParam("userId") Integer userId) {
        List<ProfileSummary> friendProfiles=friendshipService.getFriendsByUserId(userId);

        return new ResponseEntity<List<ProfileSummary>>(friendProfiles, HttpStatus.OK);
    }


    @PostMapping("/addFriend")
    public ResponseEntity<ApiResponse> createConversationHandler(@RequestBody CreateConversationRequest createConversationRequest) throws UserException, ConversationException {

        friendshipService.addFriend(createConversationRequest.getLocalUserId(), createConversationRequest.getRemoteUserId());
        ApiResponse res = new ApiResponse("Add Successfully...", true);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/searchFriend")  //search user toàn cục
    public ResponseEntity<Page<ProfileSummary>> createConversationHandle2r(
            @RequestParam("key") String key,
            @RequestParam("isPage") Boolean isPage,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProfileSummary> arr=friendshipService.searchProfile(key,isPage,pageable);

        return new ResponseEntity<Page<ProfileSummary>>(arr, HttpStatus.OK);
    }

    @PostMapping("/unFriend/{friendId}")
    public ResponseEntity< ApiResponse > unFriendHandle(@PathVariable("friendId") Integer friendId)  {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        friendshipService.unFriend(myId,friendId);

        ApiResponse res = new ApiResponse("Successfully...", true);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

//    @PostMapping("/onlines")
//    public ResponseEntity< List<Integer> > getOnlineFriend(@RequestBody List<Integer> onlines)  {
//
//            return simpUserRegistry.getUsers()
//                    .stream()
//                    .map(SimpUser::getName)
//                    .collect(Collectors.toList());
//        }
//        List<Integer> res=sseService.getOnlineUsers(onlines);
//
//        return new ResponseEntity<List<Integer>>(res, HttpStatus.OK);
//    }

    @PostMapping("/onlines")
    public ResponseEntity< List<Integer> > getConnectedUsers(@RequestBody List<Integer> onlines) {
        Set<Integer> set = new HashSet<>(onlines);

        Set<Integer> userIds = simpUserRegistry.getUsers()
                .stream()
                .map(SimpUser::getName)
                .filter(name -> name.matches("\\d+")) // chỉ nhận tên user là số
                .map(Integer::valueOf)
                .collect(Collectors.toSet());

        set.retainAll(userIds);

        List<Integer> result = new ArrayList<>(set);

        return new ResponseEntity<List<Integer>>(result, HttpStatus.OK);


    }


}
