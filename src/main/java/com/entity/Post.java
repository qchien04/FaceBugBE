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
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = true)
    private String media;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private UserProfile author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community", nullable = true)
    private Community community;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private PostType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Visibility visibility;

    @Column(name = "is_anonymous", nullable = false)
    private boolean isAnonymous = false;

    @Column(name = "is_pinned", nullable = true)
    private Boolean isPinned = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
