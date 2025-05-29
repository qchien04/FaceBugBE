package com.service.imple;

import com.DTO.FriendDTO;
import com.DTO.PostDTO;
import com.constant.NotificationType;
import com.entity.FriendRequest;
import com.entity.Like;
import com.entity.Post;
import com.entity.auth.UserProfile;
import com.entity.notify.Notification;
import com.entity.notify.NotificationAction;
import com.exception.UserException;
import com.repository.LikeRepo;
import com.repository.PostRepo;
import com.repository.UserProfileRepo;
import com.service.CustomUserDetails;
import com.service.LikeService;
import com.service.SseService;
import com.service.noitify.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class LikeServiceImp implements LikeService {
    private final LikeRepo likeRepository;
    private final UserProfileRepo userRepository;
    private final NotificationService notificationService;
    private final SseService sseService;
    @Override
    public void likePost(Integer userIdLike, PostDTO postDto) {
        int postId=postDto.getId();
        if (likeRepository.existsByUserProfileIdAndPostId(userIdLike, postId)) {
            throw new UserException("Đã like");
        }
        Like like = new Like();

        UserProfile userProfileLike=UserProfile.builder().id(userIdLike).build();
        Post post=Post.builder().id(postDto.getId()).build();

        like.setUserProfile(userProfileLike);
        like.setPost(post);

        //thong bao
        FriendDTO friendDTO=userRepository.searchUserProfileDTO(userIdLike);

        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        if(!myId.equals(postDto.getAuthorId())){
            Notification likeNotification=new Notification().builder()
                    .link("/post/"+postDto.getId())
                    .isClicked(false)
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .type(NotificationType.POST_LIKE)
                    .message(friendDTO.getFriendName()+" đã thích bài viết của bạn!")
                    .avt(friendDTO.getFriendAvt())
                    .receive(UserProfile.builder().id(postDto.getAuthorId()).build())
                    .sender(UserProfile.builder().id(userIdLike).build())
                    .build();
            notificationService.createNotification(likeNotification);

            sseService.pushNotify(postDto.getAuthorId(), likeNotification);
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
