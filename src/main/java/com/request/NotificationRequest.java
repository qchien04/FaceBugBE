package com.request;

import com.constant.NotificationType;
import com.entity.notify.NotificationAction;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {
    private Integer userId;
    private NotificationType type;
    private String message;
    private String link;
    private List<NotificationAction> actions;
}

