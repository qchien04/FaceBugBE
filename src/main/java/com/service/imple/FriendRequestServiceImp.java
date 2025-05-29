package com.service.imple;

import com.DTO.FriendDTO;
import com.constant.FriendRequestStatus;
import com.constant.NotificationType;
import com.entity.FriendRequest;
import com.entity.Friendship;
import com.entity.auth.UserProfile;
import com.entity.notify.Notification;
import com.entity.notify.NotificationAction;
import com.exception.FriendException;
import com.repository.FriendRequestRepo;
import com.repository.FriendshipRepo;
import com.repository.UserProfileRepo;
import com.service.CustomUserDetails;
import com.service.FriendRequestService;
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
public class FriendRequestServiceImp implements FriendRequestService {

    private FriendRequestRepo friendRequestRepo;
    private FriendshipRepo friendshipRepo;
    private UserProfileRepo userProfileRepo;
    private NotificationService notificationService;
    private SseService sseService;

    @Override
    public List<FriendDTO> getAllRequest() {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        List<FriendDTO> request=friendRequestRepo.findFriendRequests(myId);
        return request;
    }

    @Override
    @Transactional
    public void sendFriendRequest(Integer senderId, Integer receiverId) {
        boolean exists = friendRequestRepo.existsFriendshipOrRequest(senderId, receiverId);
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        if (exists) {
            throw new FriendException("Hai người đã là bạn bè hoặc đã có lời mời trước đó!");
        }

        List<NotificationAction> actions=new ArrayList<>();
        NotificationAction action=new NotificationAction("Chấp nhận",
                "/friendRequest/acceptRequest/"+senderId,
                "POST");
        NotificationAction action2=new NotificationAction("Từ chối",
                "/friendRequest/refuseRequest/"+senderId,
                "POST");
        actions.add(action);
        actions.add(action2);
        FriendDTO friendDTO=userProfileRepo.searchUserProfileDTO(senderId);
        FriendRequest newRequest = FriendRequest.builder()
                .sender(UserProfile.builder().id(senderId).build())
                .receiver(UserProfile.builder().id(receiverId).build())
                .build();

        Notification addFriendNotification=new Notification().builder()
                .link("/profile/"+senderId)
                .isClicked(false)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .type(NotificationType.FRIEND_REQUEST)
                .message(friendDTO.getFriendName()+" đã gửi 1 lời mời kết bạn!")
                .avt(friendDTO.getFriendAvt())
                .actions(actions)
                .receive(UserProfile.builder().id(receiverId).build())
                .sender(UserProfile.builder().id(myId).build())
                .build();

        notificationService.createNotification(addFriendNotification);

        sseService.pushNotify(receiverId,addFriendNotification);
        friendRequestRepo.save(newRequest);
    }

    @Override
    @Transactional
    public void acceptFriendRequest(Integer senderId, Integer receiverId) {
        FriendRequest request = friendRequestRepo.findBySenderIdAndReceiverId(senderId, receiverId)
                .orElseThrow(() -> new FriendException("Lời mời kết bạn không tồn tại"));

        UserProfile u1=new UserProfile();
        u1.setId(senderId);
        UserProfile u2=new UserProfile();
        u2.setId(receiverId);
        Friendship friendship1 = Friendship.builder()
                .userProfile(u1)
                .friendProfile(u2)
                .build();
        Friendship friendship2 = Friendship.builder()
                .userProfile(u2)
                .friendProfile(u1)
                .build();
        
        friendshipRepo.save(friendship1);
        friendshipRepo.save(friendship2);
        friendRequestRepo.delete(request);

        List<Notification> notifications=notificationService.findBySenderIdAndUserIdAndType(senderId,receiverId,NotificationType.FRIEND_REQUEST);
        for(Notification i: notifications){
            i.setActions(null);
        }
        notificationService.saveAll(notifications);
    }

    @Override
    @Transactional
    public void removeFriendRequest(Integer senderId, Integer receiverId) {
        FriendRequest request = friendRequestRepo.findBySenderIdAndReceiverId(senderId, receiverId)
                .orElseThrow(() -> new FriendException("Lời mời kết bạn không tồn tại"));
        friendRequestRepo.deleteById(request.getId());
    }

    @Override
    public void refuseFriendRequest(Integer senderId, Integer receiverId) {
        List<Notification> notifications=notificationService.findBySenderIdAndUserIdAndType(senderId,receiverId,NotificationType.FRIEND_REQUEST);
        for(Notification i: notifications){
            i.setActions(null);
        }
        notificationService.saveAll(notifications);
        FriendRequest request = friendRequestRepo.findBySenderIdAndReceiverId(senderId, receiverId)
                .orElseThrow(() -> new FriendException("Lời mời kết bạn không tồn tại"));
        friendRequestRepo.deleteById(request.getId());
    }

    @Override
    @Transactional
    public FriendRequestStatus checkFriendRequestStatus(Integer senderId, Integer receiverId) {
        String status = friendRequestRepo.checkFriendRequestStatus(senderId, receiverId);
        return FriendRequestStatus.valueOf(status);
    }

}
