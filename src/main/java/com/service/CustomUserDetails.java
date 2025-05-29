package com.service;

import com.DTO.FriendDTO;
import com.DTO.ProfileDTO;
import com.constant.AccountType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class CustomUserDetails implements UserDetails {
    private Integer accountId;
    private Integer id;
    private String username;
    private String password;
    private List<ProfileDTO> profiles;
    private Collection<? extends GrantedAuthority> authorities;
    private AccountType accountType;

    public CustomUserDetails(Integer accountId,Integer id, String username, String password, Collection<? extends GrantedAuthority> authorities,List<ProfileDTO> profiles,AccountType accountType) {
        this.accountId=accountId;
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.profiles= profiles;
        this.accountType=accountType;
    }
    public CustomUserDetails(Integer accountId,Integer id, String username) {
        this.accountId=accountId;
        this.id = id;
        this.username = username;
        this.password = null;
        this.authorities = null;
    }


    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
