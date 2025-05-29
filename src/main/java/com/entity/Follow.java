package com.entity;


import com.entity.auth.UserProfile;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "follow")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "pageProfile", nullable = false)
    private UserProfile pageProfile;

    @ManyToOne
    @JoinColumn(name = "followerProfile", nullable = false)
    private UserProfile followerProfile;
}

