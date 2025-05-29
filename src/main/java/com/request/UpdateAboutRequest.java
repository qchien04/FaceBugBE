package com.request;

import com.entity.auth.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateAboutRequest {
    private Integer id;
    private String name;
    private String school;
    private String comeFrom;
    private String relationshipStatus;
    private String phoneNumber;
    private String currentJob;
    private String education;
    private String currentCity;
    private UserProfile.Gender gender;
    private String family;


}
