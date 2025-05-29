package com.repository;

import com.constant.NotificationType;
import com.entity.notify.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationRepo extends JpaRepository<Notification,Integer> {

    @Query("SELECT n FROM Notification n WHERE n.receive.id = :userId ORDER BY n.createdAt DESC")
    List<Notification> findByReceiveIdOrderByCreatedAtDesc(Integer userId);

    @Query("SELECT n FROM Notification n WHERE n.sender.id = :senderId AND n.receive.id = :userId AND n.type = :type")
    List<Notification> findBySenderIdAndReceiveIdAndType(Integer senderId, Integer userId, NotificationType type);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.receive.id = :userId")
    void markAllNotificationAsRead(@Param("userId") Integer userId);
}
