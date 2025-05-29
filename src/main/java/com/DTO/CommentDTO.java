package com.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CommentDTO {
    private Integer id;

    private Integer authorId;
    private String authorName;
    private String authorAvatar;

    private String content;
    private Integer postId;
    private Integer replyCounter;
    private Integer parent;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Override
    public String toString() {
        return "CommentDTO{" +
                "id=" + id +
                ", authorId=" + authorId +
                ", authorName='" + authorName + '\'' +
                ", authorAvatar='" + authorAvatar + '\'' +
                ", content='" + content + '\'' +
                ", postId=" + postId +
                ", parent=" + parent +
                ", createdAt=" + createdAt +
                '}';
    }
}

