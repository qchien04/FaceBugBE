package com.controller.auth;

import com.DTO.FriendDTO;
import com.DTO.ProfileDTO;
import com.constant.AccountType;
import com.entity.auth.User;
import com.entity.auth.UserProfile;
import com.response.ApiResponse;
import com.response.AuthRespone;
import com.security.TokenProvider;
import com.service.CustomUserDetails;
import com.service.CustomUserDetailsService;
import com.service.auth.UserProfileService;
import com.service.auth.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/google/login")
@AllArgsConstructor
public class GoogleAuthController{

    private UserService userService;
    private UserProfileService userProfileService;
    private CustomUserDetailsService customUserDetailsService;
    private TokenProvider tokenProvider;

    @PostMapping("/user")
    public ResponseEntity<?> loginWithGoogle(@RequestBody Map<String, String> request) {
        String accessToken = request.get("accessToken");
        String tokenInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo?" + accessToken.substring(1);

        RestTemplate restTemplate = new RestTemplate();
        try {
            Map<String, Object> tokenInfo = restTemplate.getForObject(tokenInfoUrl, Map.class);
            if (tokenInfo != null && tokenInfo.containsKey("email")) {
                String email = (String) tokenInfo.get("email");
                String name = (String) tokenInfo.get("name");
                String picture = (String) tokenInfo.get("picture");


                User find_user =userService.findByEmail(email);

                if(find_user==null){
                    User user=new User(email,email,"123");
                    user = userService.save(user);
                    UserProfile userProfile=new UserProfile();
                    userProfile.setName(name);
                    userProfile.setAvt(picture);
                    userProfile.setUser(user);
                    userProfile.setAccountType(AccountType.NORMAL);
                    userProfile=userProfileService.save(userProfile);

                    List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
                    CustomUserDetails userDetails = new CustomUserDetails(user.getId(),userProfile.getId(),email,"123456789" ,
                            authorities,
                            Collections.singletonList(ProfileDTO.builder().id(userProfile.getId())
                                    .name(userProfile.getName())
                                    .avt(userProfile.getAvt())
                                    .accountType(userProfile.getAccountType())
                                    .build()),
                            userProfile.getAccountType());
                    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    String jwt=tokenProvider.genarateToken(authentication);
                    AuthRespone res=new AuthRespone(jwt,true);
                    return new ResponseEntity<AuthRespone>(res, HttpStatus.OK);

                }
                else{
                    Authentication authentication=authenticate(email);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    String jwt=tokenProvider.genarateToken(authentication);
                    AuthRespone res=new AuthRespone(jwt,true);
                    return new ResponseEntity<AuthRespone>(res, HttpStatus.OK);
                }

            } else {
                ApiResponse apiResponse=new ApiResponse("Something went wrong!",false);
                return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);
            }
        } catch (Exception e) {
            ApiResponse apiResponse=new ApiResponse("Something went wrong!",false);
            return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);
        }
    }

    public Authentication authenticate(String username){
        CustomUserDetails userDetails=(CustomUserDetails) customUserDetailsService.loadUserByUsername(username);

        if(userDetails==null){
            throw new BadCredentialsException("Invalid user");
        }
        return new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
    }
}
