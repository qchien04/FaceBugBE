package com.DTO;

import com.constant.CommunityRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CommunityUserprofileDTO {
    private Integer userId;
    private String name;
    private String avt;
    private CommunityRole role;
    private LocalDateTime joinAt;
}
