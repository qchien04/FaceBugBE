package com.service;


import com.DTO.FriendDTO;
import com.DTO.ProfileDTO;
import com.constant.AccountType;
import com.entity.auth.Permission;
import com.entity.auth.Role;
import com.entity.auth.UserProfile;
import com.exception.UserException;
import com.service.auth.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
    private UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.entity.auth.User user=this.userService.findByUsername(username);
        if(user==null){
            user=this.userService.findByEmail(username);
            if (user==null){
                throw new RuntimeException("User not found : "+username);
            }
        }

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        for (Role role : user.getRoles()) {
            for (Permission permission: role.getPermissions() ){
                authorities.add(new SimpleGrantedAuthority( permission.getPermissionName() ) );
            }
        }
        UserProfile mainProfile=new UserProfile();
        List<ProfileDTO> profiles=new ArrayList<>();
        for(UserProfile i:user.getUserProfile()){
            if(i.getAccountType().equals(AccountType.NORMAL)){
                mainProfile=i;
            }
            ProfileDTO profileDTO=ProfileDTO.builder().avt(i.getAvt())
                    .id(i.getId())
                    .name(i.getName())
                    .accountType(i.getAccountType())
                    .build();
            profiles.add(profileDTO);
        }
        if(mainProfile.getId()==(null)){
            throw new UserException("Không thể tìm thấy tài khoản");
        }
        return new CustomUserDetails(
                user.getId(),
                mainProfile.getId(),
                user.getEmail(),
                user.getPassword(),
                authorities,
                profiles,
                mainProfile.getAccountType());
    }
}
