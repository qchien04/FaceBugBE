package com.entity.chatrealtime;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "messagenotify")
public class MessageNotify {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    Integer id;

    @Column(name = "sender_id")
    Integer senderId;

    @Column(name = "receive_id")
    Integer receiveId;

    @Column(name = "conversation_id")
    Integer conversationId;

    @Column(name = "content")
    String content;

    @Column(name = "sendAt")
    LocalDateTime sendAt;

}
