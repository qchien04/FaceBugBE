package com.entity;

import com.entity.auth.UserProfile;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile author;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = true)
    private Integer replyCounter;

    @Column(nullable = true, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = true)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;


    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    @PrePersist
    protected void onCreate() {
        updatedAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }
}
