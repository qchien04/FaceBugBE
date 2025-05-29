package com.service.imple;

import com.DTO.CommentDTO;
import com.DTO.FriendDTO;
import com.constant.NotificationType;
import com.entity.Comment;
import com.entity.Post;
import com.entity.auth.UserProfile;
import com.entity.notify.Notification;
import com.exception.CommentException;
import com.repository.CommentRepo;
import com.repository.PostRepo;
import com.repository.UserProfileRepo;
import com.service.CommentService;
import com.service.CustomUserDetails;
import com.service.SseService;
import com.service.noitify.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CommentServiceImp implements CommentService {
    private final CommentRepo commentRepository;
    private final PostRepo postRepo;
    private final UserProfileRepo userProfileRepo;
    private final NotificationService notificationService;
    private final SseService sseService;

    @Override
    public List<CommentDTO> getCommentsByPost(Integer postId) {
        List<CommentDTO> comments = commentRepository.findByPostId(postId);
        return comments;
    }

    @Override
    public List<CommentDTO> getChildComment(Integer commentId) {
        List<CommentDTO> comments = commentRepository.getChildComment(commentId);
        return comments;
    }

    @Override
    public CommentDTO addComment(CommentDTO comment) {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        Post post=Post.builder().id(comment.getPostId()).build();
        Comment parentComment=new Comment();
        if(comment.getParent()!=null){
            parentComment.setId(comment.getParent());
            Optional<Comment> oldComment=commentRepository.findById(comment.getParent());

            if(oldComment.isPresent()){
                oldComment.get().setReplyCounter(oldComment.get().getReplyCounter()==null?1:oldComment.get().getReplyCounter()+1);
                commentRepository.save(oldComment.get());
            }
            else throw new CommentException("Bình luận trả lời không tồn tại");

        }
        else parentComment=null;
        UserProfile author=UserProfile.builder().id(myId).build();
        Comment newComment=Comment.builder()
                .post(post)
                .author(author)
                .content(comment.getContent())
                .parent(parentComment)
                .replyCounter(null)
                .build();


        Comment commentAfterSave=commentRepository.save(newComment);

        comment.setAuthorId(myId);
        comment.setId(commentAfterSave.getId());
        comment.setCreatedAt(LocalDateTime.now());

        //thong bao
        Post srcPost = postRepo.findById(comment.getPostId()).get();
        if(!myId.equals(srcPost.getAuthor().getId())) {
            FriendDTO friendDTO = userProfileRepo.searchUserProfileDTO(comment.getAuthorId());
            Notification commentNotification = Notification.builder()
                    .link("/post/" + comment.getPostId() + "?commentId=" + commentAfterSave.getId())
                    .isClicked(false)
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .type(NotificationType.COMMENT)
                    .message(friendDTO.getFriendName() + " đã bình luận về bài viết của bạn!")
                    .avt(friendDTO.getFriendAvt())
                    .receive(UserProfile.builder().id(srcPost.getAuthor().getId()).build())
                    .sender(UserProfile.builder().id(comment.getAuthorId()).build())
                    .build();
            notificationService.createNotification(commentNotification);

            sseService.pushNotify(srcPost.getAuthor().getId(), commentNotification);
        }
        return comment;
    }

    public void deleteComment(Integer commentId) {
        commentRepository.deleteById(commentId);
    }

}
