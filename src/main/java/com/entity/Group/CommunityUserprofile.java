package com.entity.Group;


import com.constant.CommunityRole;
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
@Table(name = "community_userprofile")
public class CommunityUserprofile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "community", nullable = false)
    private Community community;

    @ManyToOne
    @JoinColumn(name = "userProfile", nullable = false)
    private UserProfile userProfile;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CommunityRole communityRole;

    @Column(nullable = false, updatable = false)
    private LocalDateTime joinAt;

    @PrePersist
    protected void onCreate() {
        this.joinAt = LocalDateTime.now();
    }

}
