package com.DTO;

import com.constant.MediaType;
import com.entity.Group.Community;
import com.entity.auth.UserProfile;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDTO {

    private Integer id;

    private String title;

    private String media;

    private MediaType mediaType;

    private Boolean anonymous;
    private Boolean isPinned;
    private Integer authorId;
    private String authorName;
    private String authorAvatar;

    private Integer communityId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


}
