package com.controller.auth;



import com.DTO.FriendDTO;
import com.DTO.PostDTO;
import com.DTO.ProfileDTO;
import com.DTO.UserProfileDTO;
import com.constant.AccountType;
import com.constant.MediaType;
import com.entity.OTPCode;
import com.entity.auth.Permission;
import com.entity.auth.Role;
import com.entity.auth.User;
import com.entity.auth.UserProfile;
import com.exception.UserException;
import com.request.AuthAccount;
import com.request.CreatePageRequest;
import com.request.UpdateAboutRequest;
import com.response.ApiResponse;
import com.response.AuthRespone;
import com.response.AuthoritiesResponse;
import com.service.CustomUserDetails;
import com.service.UpLoadImageFileService;
import com.service.auth.UserProfileService;
import com.service.auth.UserService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private UserService userService;
    private UserProfileService userProfileService;
    private UpLoadImageFileService upLoadImageFileService;



    @GetMapping("/")
    public ResponseEntity<String> getUserTokenHandler(@RequestHeader("Authorization") String token){
        return new ResponseEntity<String>(token, HttpStatus.OK);
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserProfile> getUserProfileHandler(@PathVariable("userId") Integer userId) throws UserException {
        UserProfile user=userProfileService.findByUserProfileId(userId);
        return new ResponseEntity<UserProfile>(user, HttpStatus.OK);
    }

    @GetMapping("/basicInfo")
    public ResponseEntity<AuthoritiesResponse> getAuthoritiesHandler() throws UserException {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        Integer accountId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getAccountId();
        FriendDTO userProfile=userProfileService.findById(myId);
        User user=userService.findById(accountId);
        List<String> roles=new ArrayList<>();
        List<String> authorities=new ArrayList<>();

        List<ProfileDTO> profiles=((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getProfiles();
        AccountType accountType=((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getAccountType();
        for(Role role: user.getRoles()){
            roles.add(role.getRoleName());
            for(Permission permission: role.getPermissions()){
                authorities.add(permission.getPermissionName());
            }
        }

        for(Role role: user.getRoles()){
            roles.add(role.getRoleName());
            for(Permission permission: role.getPermissions()){
                authorities.add(permission.getPermissionName());
            }
        }

        AuthoritiesResponse authoritiesResponse=new AuthoritiesResponse(
                user.getEmail(),
                userProfile.getFriendAvt(),
                userProfile.getFriendId(),
                userProfile.getFriendName(),
                authorities,
                roles,
                profiles,
                accountType);
        return new ResponseEntity<AuthoritiesResponse>(authoritiesResponse, HttpStatus.OK);
    }


    @GetMapping("/{query}")
    public ResponseEntity<List<UserProfile>> searchUserHandler(@PathVariable("query") String query) throws UserException {
        List<UserProfile> users=userProfileService.searchUser(query);
        System.out.println(users.size());
        return new ResponseEntity<List<UserProfile>>(users, HttpStatus.OK);
    }



    @PutMapping("/profile/about")
    public ResponseEntity<ApiResponse> updateUserHandler(@RequestBody UpdateAboutRequest req) {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        UserProfile userProfile=userProfileService.findByUserProfileId(myId);

        userProfile.setName(req.getName());
        userProfile.setSchool(req.getSchool());
        userProfile.setComeFrom(req.getComeFrom());
        userProfile.setRelationshipStatus(req.getRelationshipStatus());
        userProfile.setPhoneNumber(req.getPhoneNumber());
        userProfile.setCurrentCity(req.getCurrentCity());
        userProfile.setCurrentJob(req.getCurrentJob());
        userProfile.setEducation(req.getEducation());
        userProfile.setGender(req.getGender());
        userProfile.setFamily(req.getFamily());

        userProfileService.save(userProfile);

        ApiResponse apiResponse=new ApiResponse("Update successfully",true);
        return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.ACCEPTED);
    }

    @PostMapping("/changeCoverPhoto")
    public ResponseEntity<String> createNotification(@RequestParam("media") MultipartFile media) throws IOException {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        UserProfile userProfile=userProfileService.findByUserProfileId(myId);
        String newCoverPhoto=upLoadImageFileService.uploadImage(media);

        userProfile.setCoverPhoto(newCoverPhoto);
        userProfileService.save(userProfile);
        return new ResponseEntity<String>(newCoverPhoto, HttpStatus.OK);
    }

    @PostMapping("/profile/description")
    public ResponseEntity<String> changeDescription(@RequestBody String des) {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        UserProfile userProfile=userProfileService.findByUserProfileId(myId);

        userProfile.setDescription(des.substring(1,des.length()-1));
        userProfileService.save(userProfile);
        return new ResponseEntity<String>(des, HttpStatus.OK);
    }

    @PostMapping("/changeAvt")
    public ResponseEntity<String> changeAvtHandle(@RequestParam("media") MultipartFile media) throws IOException {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        UserProfile userProfile=userProfileService.findByUserProfileId(myId);
        String newAvt=upLoadImageFileService.uploadImage(media);

        userProfile.setAvt(newAvt);
        userProfileService.save(userProfile);
        return new ResponseEntity<String>(newAvt, HttpStatus.OK);
    }

    @PostMapping("/createPage")
    public ResponseEntity<String> authAccountHandler(@RequestBody CreatePageRequest request) {
        Integer accountId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getAccountId();

        UserProfile userProfile=UserProfile.builder().accountType(AccountType.PAGE)
                .name(request.getName())
                .avt("https://static.vecteezy.com/system/resources/previews/009/292/244/non_2x/default-avatar-icon-of-social-media-user-vector.jpg")
                .categoryContent(request.getContent())
                .user(User.builder().id(accountId).build()).build();
        userProfile.setAccountType(AccountType.PAGE);
        userProfile.setUser(User.builder().id(accountId).build());

        userProfileService.save(userProfile);
        return new ResponseEntity<String>("suc", HttpStatus.OK);
    }


}
