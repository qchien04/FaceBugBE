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
public class ChangeAvtGroupRequest {
    private Integer conversationId;
    private List<Integer> members;
}
