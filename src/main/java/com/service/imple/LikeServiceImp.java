package com.service.imple;

import com.DTO.ProfileSummary;
import com.constant.NotificationType;
import com.entity.Like;
import com.entity.Post;
import com.entity.auth.UserProfile;
import com.entity.notify.Notification;
import com.exception.UserException;
import com.repository.LikeRepo;
import com.repository.UserProfileRepo;
import com.service.LikeService;
import com.service.PostService;
import com.service.noitify.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class LikeServiceImp implements LikeService {
    private final LikeRepo likeRepository;
    private final UserProfileRepo userRepository;
    private final NotificationService notificationService;
    private final PostService postService;
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public void likePost(Integer userIdLike,Integer postId) {
        if (likeRepository.existsByUserProfileIdAndPostId(userIdLike, postId)) {
            throw new UserException("Đã like");
        }
        Like like = new Like();

        UserProfile userProfileLike=UserProfile.builder().id(userIdLike).build();
        Post post=postService.findById(postId);

        like.setUserProfile(userProfileLike);
        like.setPost(post);

        //thong bao
        ProfileSummary friendDTO=userRepository.searchUserProfileDTO(userIdLike);

        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        if(!myId.equals(post.getAuthor().getId())){
            Notification likeNotification=new Notification().builder()
                    .link("/post/"+post.getId())
                    .isClicked(false)
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .type(NotificationType.POST_LIKE)
                    .message(friendDTO.getName()+" đã thích bài viết của bạn!")
                    .avt(friendDTO.getAvt())
                    .receive(UserProfile.builder().id(post.getAuthor().getId()).build())
                    .sender(UserProfile.builder().id(userIdLike).build())
                    .build();
            notificationService.createNotification(likeNotification);

            messagingTemplate.convertAndSendToUser(post.getAuthor().getId().toString(), "/queue/notifications", likeNotification);
        }
        likeRepository.save(like);
    }

    @Override
    @Transactional
    public void unlikePost(Integer userId, Integer postId) {
        likeRepository.deleteByUserProfileIdAndPostId(userId, postId);
    }

    @Override
    public long countLikes(Integer postId) {
        return likeRepository.countByPostId(postId);
    }

    @Override
    public boolean isLiked(Integer userId, Integer postId) {
        return likeRepository.existsByUserProfileIdAndPostId(userId, postId);
    }
}
