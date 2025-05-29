package com.service;

import com.DTO.CommentDTO;
import com.DTO.FriendDTO;
import com.DTO.MemberGroupChatDTO;
import com.DTO.MessageNotifyDTO;
import com.entity.chatrealtime.MessageNotify;
import com.entity.notify.Notification;
import com.repository.NotificationRepo;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
public class SseService {
    private final Map<Integer, SseEmitter> userEmitters = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public SseEmitter addEmitter(Integer userId) {
        SseEmitter emitter = new SseEmitter(0L); // Không giới hạn thời gian
        userEmitters.put(userId, emitter);

        // Xóa emitter nếu client đóng kết nối
        emitter.onCompletion(() -> userEmitters.remove(userId));
        emitter.onTimeout(() -> userEmitters.remove(userId));
        emitter.onError((ex) -> {
            userEmitters.remove(userId);
            emitter.complete();
        });
        return emitter;
    }


    private void pingAllClients() {
        for (Map.Entry<Integer, SseEmitter> entry : userEmitters.entrySet()) {
            try {
                entry.getValue().send(SseEmitter.event().name("ping").data("keep-alive"));
            } catch (IOException e) {
                userEmitters.remove(entry.getKey());
            } catch (Exception e) {
                System.err.println("Lỗi bất ngờ khi ping client " + entry.getKey() + ": " + e.getMessage());
            }
        }
    }

    @PostConstruct
    public void init() {
        scheduler.scheduleAtFixedRate(this::pingAllClients, 0, 10, TimeUnit.SECONDS);
    }

    public List<Integer> getOnlineUsers(List<Integer> userIds) {
        List<Integer> onlineUsers = new ArrayList<>();

        for (Integer userId : userIds) {
            if (userEmitters.containsKey(userId)) {
                onlineUsers.add(userId);
            }
        }
        return onlineUsers;
    }

    public void sendNotification(Integer userId, MessageNotifyDTO message) {
        SseEmitter emitter = userEmitters.get(userId);

        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("messageNotify")
                        .data(message));
            } catch (IOException e) {
                emitter.complete();
                userEmitters.remove(userId);
            }
        }
    }

    public void sendAddMember(Integer userId, Integer conversationId, MemberGroupChatDTO memberGroupChatDTO) {
        SseEmitter emitter = userEmitters.get(userId);

        if (emitter != null) {
            try {
                Map<String, Object> data = new HashMap<>();
                data.put("conversationId", conversationId);
                data.put("memberGroupChatDTO", memberGroupChatDTO);

                emitter.send(SseEmitter.event()
                        .name("addmember")
                        .data(data));
            } catch (IOException e) {
                emitter.complete();
                userEmitters.remove(userId);
            }
        }
    }

    public void sendKickMember(Integer userId,Integer conversationId, Integer memberId) {
        SseEmitter emitter = userEmitters.get(userId);

        if (emitter != null) {
            try {
                Map<String, Object> data = new HashMap<>();
                data.put("conversationId", conversationId);
                data.put("memberId", memberId);

                emitter.send(SseEmitter.event()
                        .name("kickmember")
                        .data(data));
            } catch (IOException e) {
                emitter.complete();
                userEmitters.remove(userId);
            }
        }
    }
    public void sendChangeAvt(Integer userId,Integer conversationId,String url) {
        SseEmitter emitter = userEmitters.get(userId);

        if (emitter != null) {
            try {
                Map<String, Object> data = new HashMap<>();
                data.put("conversationId", conversationId);
                data.put("newAvt", url);

                emitter.send(SseEmitter.event()
                        .name("changeAvt")
                        .data(data));
            } catch (IOException e) {
                emitter.complete();
                userEmitters.remove(userId);
            }
        }
    }

    public void pushNotify(Integer userId, Notification notification) {
        SseEmitter emitter = userEmitters.get(userId);

        if (emitter != null) {
            try {
                Map<String, Object> data = new HashMap<>();
                data.put("notification", notification);

                emitter.send(SseEmitter.event()
                        .name("pushNotify")
                        .data(data));
            } catch (IOException e) {
                emitter.complete();
                userEmitters.remove(userId);
            }
        }
    }

}

