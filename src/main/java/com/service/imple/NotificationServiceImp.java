package com.service.imple;

import com.constant.NotificationType;
import com.entity.auth.UserProfile;
import com.entity.notify.Notification;
import com.entity.notify.NotificationAction;
import com.repository.NotificationRepo;
import com.request.NotificationRequest;
import com.service.noitify.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@AllArgsConstructor
public class NotificationServiceImp implements NotificationService {

    private final NotificationRepo notificationRepository;

    private SimpMessagingTemplate messagingTemplate;

    @Override
    public Notification createNotification(Integer userId, NotificationType type, String message, String link, List<NotificationAction> actions) {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        Notification notification = Notification.builder()
                .receive(UserProfile.builder().id(userId).build())
                .type(type)
                .message(message)
                .link(link)
                .actions(actions)
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .isClicked(false)
                .sender(UserProfile.builder().id(myId).build())
                .build();
        return notificationRepository.save(notification);
    }

    @Override
    public Notification createNotification(NotificationRequest notificationRequest) {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        Notification notification = Notification.builder()
                .receive(UserProfile.builder().id(notificationRequest.getUserId()).build())
                .type(notificationRequest.getType())
                .message(notificationRequest.getMessage())
                .link(notificationRequest.getLink())
                .actions(notificationRequest.getActions())
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .isClicked(false)
                .sender(UserProfile.builder().id(myId).build())
                .build();

        return notificationRepository.save(notification);
    }

    @Override
    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getNotifications() {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return notificationRepository.findByReceiveIdOrderByCreatedAtDesc(myId);
    }

    @Override
    public List<Notification> findBySenderIdAndUserIdAndType(Integer senderId, Integer userId, NotificationType type) {
        return notificationRepository.findBySenderIdAndReceiveIdAndType(senderId,userId,type);
    }

    @Override
    public void saveAll(List<Notification> notifications) {
        notificationRepository.saveAll(notifications);
    }

    @Override
    public void markAllNotificationAsRead() {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        notificationRepository.markAllNotificationAsRead(myId);
    }

    @Override
    public void sendNotification(Notification notification,String principal) {
        messagingTemplate.convertAndSendToUser(principal, "/queue/notify", "hello");
    }

}
