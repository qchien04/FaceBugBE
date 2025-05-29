package com.entity.auth;

import com.constant.AccountType;
import com.constant.CategoryContent;
import com.constant.CommunityRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "userprofile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    @JsonIgnore
    private User user;

    private String name;
    private String avt;
    private String coverPhoto;
    private LocalDate dateOfBirth;
    private String school;
    private String comeFrom;
    private String relationshipStatus;
    private String phoneNumber;
    private String currentJob;
    private String education;
    private String currentCity;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String family;

    @Column(name = "description", length = 500, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CategoryContent categoryContent;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (categoryContent == null) {
            categoryContent = CategoryContent.USER;
        }
    }
    public enum Gender {
        BOY, GIRL, OTHER
    }
}
