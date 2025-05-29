package com.aop;

import com.DTO.PostDTO;
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

@Aspect
@Component
@AllArgsConstructor
public class LikeLogAspect {
    private static final Logger logger = LoggerFactory.getLogger(LikeLogAspect.class);
    private ActionOnPostRepo actionOnPostRepo;

    @AfterReturning(
        value = "execution(* com.service.imple.LikeServiceImp.likePost(..)) && args(userIdLike, postDto)",
        argNames = "joinPoint,userIdLike,postDto"
    )
    public void logLikeAction(JoinPoint joinPoint, Integer userIdLike, PostDTO postDto) {
        try {
            Integer postId = postDto.getId(); // nếu PostDto có getId()

            ActionOnPost actionOnPost=ActionOnPost.builder()
                    .post(Post.builder().id(postId).build())
                    .timeAction(LocalDateTime.now())
                    .actionCore(2)
                    .build();
            actionOnPostRepo.save(actionOnPost);
            logger.info("[AOP] User {} liked post {} at {}", userIdLike, postId, LocalDateTime.now());
        } catch (Exception e) {
            logger.warn("[AOP] Could not log like action: {}", e.getMessage());
        }
    }
} 