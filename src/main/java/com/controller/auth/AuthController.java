package com.controller.auth;


import com.DTO.ProfileDTO;
import com.constant.AccountType;
import com.entity.OTPCode;
import com.entity.auth.User;
import com.entity.auth.UserProfile;
import com.exception.UserException;
import com.request.AuthAccount;
import com.request.ChangePasswordRequest;
import com.request.LoginRequest;
import com.request.UserRegister;
import com.response.ApiResponse;
import com.response.AuthRespone;
import com.response.RegisterRespone;
import com.security.TokenProvider;
import com.service.CustomUserDetails;
import com.service.MailService;
import com.service.OTPCodeService;
import com.service.CustomUserDetailsService;
import com.service.auth.UserProfileService;
import com.service.auth.UserService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final MailService mailService;
    private OTPCodeService otpCodeService;
    private UserService userService;
    private UserProfileService userProfileService;
    private PasswordEncoder passwordEncoder;
    private CustomUserDetailsService customUserDetailsService;
    private TokenProvider tokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<?> createUserHandler(@RequestBody UserRegister userRegister) throws UserException, MessagingException {
        String username=userRegister.getUsername();
        String email=userRegister.getEmail();
        String password=userRegister.getPassword();
        User isUser=userService.findByEmail(email);

        if(isUser!=null){
            RegisterRespone res=new RegisterRespone();
            res.setMessage("not accept");
            return new ResponseEntity<RegisterRespone>(res, HttpStatus.OK);
        }


        Random random = new Random();
        String randomInt = (10000 + random.nextInt(89999))+"";

        OTPCode otpCode=new OTPCode(randomInt,email,5);
        otpCodeService.saveOTPCode(otpCode);

        mailService.sendEmail(email,"Xac thuc tai khoan",randomInt,null);


        RegisterRespone res=new RegisterRespone();
        res.setEmail(email);
        res.setUsername(username);
        res.setPassword(password);
        res.setMessage("ok");

        return new ResponseEntity<RegisterRespone>(res, HttpStatus.OK);
    }


    @PostMapping("/authAccount")
    public ResponseEntity<AuthRespone> authAccountHandler(@RequestBody AuthAccount authAccount) throws UserException, MessagingException {
        String username=authAccount.getUsername();
        String email=authAccount.getEmail();
        String password=authAccount.getPassword();
        OTPCode otp=otpCodeService.findOTPCode(email,authAccount.getOtp());

        if(otp==null){
            AuthRespone res=new AuthRespone("",false);
            return new ResponseEntity<AuthRespone>(res, HttpStatus.OK);
        }

        User createdUser=new User();
        createdUser.setEmail(email);
        createdUser.setUsername(username);
        createdUser.setPassword(passwordEncoder.encode(password));
        createdUser = userService.save(createdUser);

        UserProfile userProfile=new UserProfile();
        userProfile.setUser(createdUser);
        userProfile.setAvt("https://static.vecteezy.com/system/resources/previews/009/292/244/non_2x/default-avatar-icon-of-social-media-user-vector.jpg");
        userProfile.setName("Người dùng FaceBug");
        userProfile.setAccountType(AccountType.NORMAL);
        userProfileService.save(userProfile);
        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(email);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt=tokenProvider.genarateToken(authentication);
        AuthRespone res=new AuthRespone(jwt,true);
        return new ResponseEntity<AuthRespone>(res, HttpStatus.OK);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthRespone> loginHandler(@RequestBody LoginRequest loginRequest) throws UserException {
        String username=loginRequest.getUsername();
        String password=loginRequest.getPassword();
        System.out.println(username+" "+password);
        Authentication authentication=authenticate(username,password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt=tokenProvider.genarateToken(authentication);
        AuthRespone res=new AuthRespone(jwt,true);
        return new ResponseEntity<AuthRespone>(res, HttpStatus.OK);

    }

    public Authentication authenticate(String username,String password){
        CustomUserDetails userDetails=(CustomUserDetails) customUserDetailsService.loadUserByUsername(username);

        if(userDetails==null){
            throw new UserException("Invalid user");
        }
        if(!passwordEncoder.matches(password,userDetails.getPassword())){
            throw new UserException("Invalid password or username");
        }
        return new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
    }

    @PostMapping("/changePassword")
    public ResponseEntity<ApiResponse> changePasswordHandler(@RequestBody ChangePasswordRequest changePasswordRequest) throws UserException {


        Integer accountId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getAccountId();
        User user=userService.findById(accountId);

        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new UserException("Mật khẩu cũ không đúng");
        } else {
            String newHashPass=passwordEncoder.encode(changePasswordRequest.getNewPassword());
            user.setPassword(newHashPass);
            userService.save(user);
        }

        ApiResponse response=new ApiResponse("Success",true);
        return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);
    }

    @PostMapping("/switchProfile/{id}")
    public ResponseEntity<AuthRespone> changePasswordHandler(@PathVariable("id") Integer id) throws UserException {
        System.out.println("Swap profile");
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            System.out.println("CustomUserDetails");
        } else if (principal instanceof String) {
            System.out.println(principal);
        }
        CustomUserDetails userDetails = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        boolean check=false;
        AccountType accountType=AccountType.PAGE;
        for(ProfileDTO i: userDetails.getProfiles()){
            if(id.equals(i.getId())){
                check=true;
                accountType=i.getAccountType();
                break;
            }
        }
        if(!check){
            throw new UserException("Profile không hợp lệ");
        }else{
            String jwt=tokenProvider.genarateToken(userDetails,email,id,null,accountType);
            AuthRespone res=new AuthRespone(jwt,true);
            return new ResponseEntity<AuthRespone>(res, HttpStatus.OK);
        }


    }


}
