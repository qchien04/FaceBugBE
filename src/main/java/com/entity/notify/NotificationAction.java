package com.entity.notify;
import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationAction {
    private String label;
    private String action;
    private String method;
}