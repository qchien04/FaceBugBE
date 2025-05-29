package com.entity;

import com.entity.auth.UserProfile;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "friendship")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userProfile", nullable = false)
    private UserProfile userProfile;

    @ManyToOne
    @JoinColumn(name = "friendProfile", nullable = false)
    private UserProfile friendProfile;
}

