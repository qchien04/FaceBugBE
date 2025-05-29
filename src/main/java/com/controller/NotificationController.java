package com.controller;



import com.DTO.FriendDTO;
import com.constant.NotificationType;
import com.entity.notify.Notification;
import com.request.NotificationRequest;
import com.response.ApiResponse;
import com.service.CustomUserDetails;
import com.service.noitify.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping("/markAll")
    public ResponseEntity<ApiResponse> markAllNotification() {
        notificationService.markAllNotificationAsRead();
        ApiResponse res = new ApiResponse("Successfully...", true);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }


    @PostMapping("/create")
    public ResponseEntity<Notification> createNotification(@RequestBody NotificationRequest request) {
        Notification notification= notificationService.createNotification(request);
        return new ResponseEntity<>(notification, HttpStatus.OK);

    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable Integer userId) {
        List<Notification> notification= notificationService.getNotificationsByUserId(userId);
        return new ResponseEntity<>(notification, HttpStatus.OK);
    }
}
