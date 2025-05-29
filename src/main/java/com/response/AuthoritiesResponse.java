package com.response;

import com.DTO.ProfileDTO;
import com.constant.AccountType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthoritiesResponse {
    private String email;
    private String avt;
    private Integer id;
    private String name;
    private List<String> authorities;
    private List<String> roles;
    private List<ProfileDTO> profiles;
    private AccountType accountType;

    @Override
    public String toString() {
        return "AuthoritiesResponse{" +
                "email='" + email + '\'' +
                ", avt='" + avt + '\'' +
                ", name='" + name + '\'' +
                ", authorities=" + authorities +
                ", roles=" + roles +
                '}';
    }
}
