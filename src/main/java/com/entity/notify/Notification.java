package com.entity.notify;

import com.constant.NotificationType;
import com.converter.NotificationActionConverter;
import com.entity.auth.UserProfile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receive_id", nullable = false)
    private UserProfile receive;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = true)
    private UserProfile sender;

    @Column(name = "type", length = 50)
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(name = "message", length = 500)
    private String message;

    @Column(name = "link", length = 255, nullable = true)
    private String link;

    @Column(name = "avt", nullable = true)
    private String avt;

    @Column(name = "is_read")
    private Boolean isRead;

    @Column(name = "is_clicked")
    private Boolean isClicked;


    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Convert(converter = NotificationActionConverter.class)
    @Column(name = "actions", columnDefinition = "TEXT", nullable = true)
    private List<NotificationAction> actions;

    @JsonProperty("senderId")
    public Integer getSenderId() {
        return sender != null ? sender.getId() : null;
    }

    @JsonProperty("receiveId")
    public Integer getReceiveId() {
        return receive != null ? receive.getId() : null;
    }
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
