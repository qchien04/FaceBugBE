package com.service.chatrealtime;

import com.DTO.MessageNotifyDTO;
import com.entity.chatrealtime.MessageNotify;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MessageNotifyService {
    MessageNotify findById(Integer id);

    MessageNotify create(MessageNotify messageNotify);

    List<MessageNotifyDTO> findByUser(Integer id);

    void deleteByUserConversationId(Integer conversationId);

    MessageNotify deleteBySendFrom(String email);


}
