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
@Table(name = "post_ranking")
public class PostRanking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name = "core", nullable = false)
    private Long core;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post", nullable = false)
    private Post post;

    @Column(nullable = false, updatable = false)
    private LocalDateTime timeCalculator;

}
