package com.service.imple;


import com.entity.auth.UserProfile;
import com.entity.chatrealtime.Conversation;
import com.entity.chatrealtime.Message;
import com.repository.MessageRepo;
import com.request.SendMessageRequest;
import com.service.chatrealtime.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;



@Service
public class MessageServiceImp implements MessageService {

    @Autowired
    private MessageRepo messageRepo;

    @Autowired
    private UserServiceImp userService;


    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public Message sendMessage(SendMessageRequest req) {

        Message message = new Message();
        message.setConversation(Conversation.builder().id(req.getConversationId()).build());
        message.setContent(req.getContent());
        message.setMessageType(req.getMessageType());
        message.setTimeSend(req.getTimeSend());
        message.setSender(UserProfile.builder().id(req.getSenderId()).build());
        message = messageRepo.save(message);

        messagingTemplate.convertAndSend( "/friend/" + req.getConversationId(), message);

        return message;
    }

    @Override
    @Transactional
    public Message createMessage(Message message) {
        return messageRepo.save(message);
    }

    @Override
    @Transactional
    public Message createMessageFromRequest(SendMessageRequest req) {
        Message message = new Message();
        message.setConversation(Conversation.builder().id(req.getConversationId()).build());
        message.setContent(req.getContent());
        message.setMessageType(req.getMessageType());
        message.setTimeSend(req.getTimeSend());
        message.setSender(UserProfile.builder().id(req.getSenderId()).build());
        return messageRepo.save(message);
    }


    @Override
    public List<Message> findByConversationId(Integer id) {
        return messageRepo.findByConversationId(id);
    }

}
