package com.service.noitify;


import com.constant.NotificationType;
import com.entity.notify.Notification;
import com.entity.notify.NotificationAction;
import com.request.NotificationRequest;

import java.util.List;

public interface NotificationService {
    public Notification createNotification(Integer userId, NotificationType type,
                                           String message, String link,
                                           List<NotificationAction> actions);
    public Notification createNotification(NotificationRequest notificationRequest);
    public Notification createNotification(Notification notification);
    public List<Notification> getNotificationsByUserId(Integer userId);
    public List<Notification> findBySenderIdAndUserIdAndType(Integer senderId, Integer userId,NotificationType type);
    void saveAll(List<Notification> notifications);

    public void markAllNotificationAsRead();
}
