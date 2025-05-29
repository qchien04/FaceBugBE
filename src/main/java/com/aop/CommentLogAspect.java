package com.aop;

import com.entity.Post;
import com.entity.postRanking.ActionOnPost;
import com.repository.ActionOnPostRepo;
import lombok.AllArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import com.DTO.CommentDTO;

@Aspect
@Component
@AllArgsConstructor
public class CommentLogAspect {
    private ActionOnPostRepo actionOnPostRepo;

    private static final Logger logger = LoggerFactory.getLogger(CommentLogAspect.class);

    @AfterReturning(
        value = "execution(* com.service.imple.CommentServiceImp.addComment(..)) && args(commentDto)",
        argNames = "joinPoint,commentDto"
    )
    public void logCommentAction(JoinPoint joinPoint, CommentDTO commentDto) {
        try {
            Integer commentId = commentDto.getId();
            Integer authorId = commentDto.getAuthorId();
            Integer postId = commentDto.getPostId();

            ActionOnPost actionOnPost=ActionOnPost.builder()
                    .post(Post.builder().id(postId).build())
                    .timeAction(LocalDateTime.now())
                    .actionCore(commentDto.getParent()==null?3:1)
                    .build();
            actionOnPostRepo.save(actionOnPost);
            logger.info("[AOP] User {} commented (id: {}) on post {} at {}", authorId, commentId, postId, LocalDateTime.now());
        } catch (Exception e) {
            logger.warn("[AOP] Could not log comment action: {}", e.getMessage());
        }
    }
} 