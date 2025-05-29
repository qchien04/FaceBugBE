package com.controller.chatrealtime;


import com.entity.chatrealtime.Message;
import com.service.chatrealtime.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/allMessage")
    public ResponseEntity<List<Message>> getChatMessageHandler(
                                        @RequestParam("conversationId") Integer conversationId) {

        List<Message> res=messageService.findByConversationId(conversationId);
        return new ResponseEntity<List<Message>>(res, HttpStatus.OK);
    }

}