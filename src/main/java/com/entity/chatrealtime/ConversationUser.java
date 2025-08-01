package com.entity.chatrealtime;

import com.entity.auth.UserProfile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "conversation_user")
public class ConversationUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userprofile_id", nullable = false)
    private UserProfile userProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @Column(name = "active",nullable = true) // can user see conversation ?
    private Boolean isActive;

    @Column(name = "last_read_at",nullable = true)
    private LocalDateTime  lastReadAt= LocalDateTime.now();

    @Column(name = "joined_at",nullable = false)
    private LocalDateTime joinedAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "role",nullable = false)
    private Role role;

    public enum Role {
        ADMIN, MEMBER
    }

    public ConversationUser(UserProfile userProfile, Conversation conversation, Role role) {
        this.userProfile = userProfile;
        this.conversation = conversation;
        this.role = role;
    }
    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
    }
}

