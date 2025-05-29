package com.service.imple;

import com.DTO.MessageNotifyDTO;
import com.entity.chatrealtime.MessageNotify;
import com.repository.MessageNotifyRepo;
import com.service.CustomUserDetails;
import com.service.chatrealtime.MessageNotifyService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
public class MessageNotifyServiceImp implements MessageNotifyService {
    private MessageNotifyRepo messageNotifyRepo;


    @Override
    public MessageNotify findById(Integer id) {
        Optional<MessageNotify> m=messageNotifyRepo.findById(id);
        return m.isPresent()?m.get():null;

    }

    @Override
    @Transactional
    public MessageNotify create(MessageNotify messageNotify) {
        return messageNotifyRepo.save(messageNotify);
    }

    @Override
    public List<MessageNotifyDTO> findByUser(Integer id) {
        List<MessageNotifyDTO> m=messageNotifyRepo.findByUser(id);
        return m;
    }

    @Override
    public void deleteByUserConversationId(Integer conversationId) {
        Integer userId=((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        messageNotifyRepo.deleteByUserConversationId(userId,conversationId);
    }


    @Override
    public MessageNotify deleteBySendFrom(String email) {
        return null;
    }
}
