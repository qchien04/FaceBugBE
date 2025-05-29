package com.entity;

import com.constant.MediaType;
import com.constant.PostType;
import com.constant.Visibility;
import com.entity.Group.Community;
import com.entity.auth.UserProfile;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "suggest_post")
public class SuggestPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer renderTimeRemaining;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_seen", nullable = false)
    private UserProfile profileSeen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;


    @Column(nullable = false, updatable = false)
    private LocalDateTime postCreatedTime;

}
