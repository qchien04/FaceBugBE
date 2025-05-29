package com.DTO;

import com.entity.auth.UserProfile;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDTO {
    private Integer id;
    private String name;
    private String avt;
    private String coverPhoto;
    private LocalDate dateOfBirth;
    private String school;
    private String comeFrom;
    private String relationshipStatus;
    private String phoneNumber;
    private String currentJob;
    private String education;
    private String currentCity;
    private UserProfile.Gender gender;
    private String family;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}

