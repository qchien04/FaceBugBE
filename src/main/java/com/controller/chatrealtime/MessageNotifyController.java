package com.controller.chatrealtime;


import com.DTO.MessageNotifyDTO;
import com.entity.chatrealtime.Conversation;
import com.entity.chatrealtime.MessageNotify;
import com.exception.ConversationException;
import com.exception.UserException;
import com.request.CreateConversationRequest;
import com.response.ApiResponse;
import com.service.CustomUserDetails;
import com.service.chatrealtime.ConversationService;
import com.service.chatrealtime.MessageNotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/messageNotify")
public class MessageNotifyController {

    @Autowired
    private MessageNotifyService messageNotifyService;



    @GetMapping("/allMessageNotify")
    public ResponseEntity<List<MessageNotifyDTO>> findAllMessageNotifyByUserEmailHandler()
            throws UserException {
        Integer id=((CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        List<MessageNotifyDTO> mn = messageNotifyService.findByUser(id);

        return new ResponseEntity<List<MessageNotifyDTO>>(mn, HttpStatus.OK);
    }

    @DeleteMapping("/deleteMessageNotify")
    public ResponseEntity<ApiResponse> findConversationByUserEmailHandler(@RequestParam("conversationId") Integer conversationId)
            throws UserException {

        messageNotifyService.deleteByUserConversationId(conversationId);

        ApiResponse res = new ApiResponse("Deleted Successfully...", true);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }


}