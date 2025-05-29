package com.DTO;


import com.constant.Privacy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class CommunityDTO {
    private Integer communityId;
    private String communityName;
    private String communityDescription;
    private String coverPhoto;
    private Privacy privacy;
    private Long totalMembers;
    private List<String> avts;


    public CommunityDTO(Integer communityId, String communityName,String communityDescription, String coverPhoto, String privacy,
                        Long totalMembers, String avts) {
        this.communityId = communityId;
        this.communityDescription=communityDescription;
        this.communityName = communityName;
        this.coverPhoto = coverPhoto;
        this.privacy = Privacy.valueOf(privacy.toUpperCase());
        this.totalMembers = totalMembers;

        this.avts = Arrays.asList(avts.split(","));
    }

    public CommunityDTO(Integer communityId, String communityName,String communityDescription, String coverPhoto, String privacy,
                        Long totalMembers) {
        this.communityId = communityId;
        this.communityDescription=communityDescription;
        this.communityName = communityName;
        this.coverPhoto = coverPhoto;
        this.privacy = Privacy.valueOf(privacy.toUpperCase());
        this.totalMembers = totalMembers;
    }


}