package com.request;


import com.DTO.ProfileSummary;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddMemberGroupRequest {
    private Integer conversationId;
    private ProfileSummary friend;
    private List<Integer> members;
}
