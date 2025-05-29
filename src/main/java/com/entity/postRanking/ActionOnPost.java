package com.entity.postRanking;

import com.entity.Post;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "action_on_post")
public class ActionOnPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name = "action_core", nullable = false)
    private Integer actionCore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post", nullable = false)
    private Post post;

    @Column(nullable = false, updatable = false)
    private LocalDateTime timeAction;

}