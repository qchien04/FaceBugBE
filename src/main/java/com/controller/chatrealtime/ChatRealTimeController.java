package com.controller.chatrealtime;

import com.DTO.MessageNotifyDTO;
import com.config.CustomPrincipalChat;
import com.entity.auth.UserProfile;
import com.entity.chatrealtime.Conversation;
import com.entity.chatrealtime.Message;
import com.entity.chatrealtime.MessageNotify;
import com.exception.ConversationException;
import com.request.SendMessageRequest;
import com.request.TypingPayload;
import com.service.CustomUserDetails;
import com.service.SseService;
import com.service.UpLoadImageFileService;
import com.service.chatrealtime.ConversationService;
import com.service.chatrealtime.MessageNotifyService;
import com.service.chatrealtime.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Set;


@RestController
public class ChatRealTimeController {
    @Autowired
    private UpLoadImageFileService uploadImageFile;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MessageNotifyService messageNotifyService;

    @Autowired
    private SseService sseService;

    @MessageMapping("/news")
    @SendTo("/topic/news")
    public String broadcastNews(@Payload String message) {
        System.out.println("goi new");
        return message;
    }

    @MessageMapping("/sendMessage")
    public void broadcast(@Payload SendMessageRequest sendMessageRequest, Principal principal) {
        if (principal == null) {
           throw new ConversationException("Something went wrong");
        }

        String username = principal.getName();
        System.out.println("Tên người dùng: " + username);
        if (principal instanceof CustomPrincipalChat principalChat) {
            System.out.println("Các kênh đã đăng ký:");
            principalChat.getSubscribedChannels().forEach(System.out::println);
            Set<Integer> channels = principalChat.getSubscribedChannels();

            if (!channels.isEmpty()) {
                Integer firstChannel = channels.iterator().next();
                System.out.println("Kênh đầu tiên: " + firstChannel);
                sendMessageRequest.setConversationId(firstChannel);
            }

        } else {
            System.out.println("Principal không phải kiểu CustomPrincipalChat");
        }
        boolean isImage=sendMessageRequest.getMessageType().equals(Message.MessageType.IMAGE);
        if(!isImage){
            messagingTemplate.convertAndSend("/conversation/" + sendMessageRequest.getConversationId(),
                                            sendMessageRequest);
            Message newMessage=new Message();
            newMessage.setSender(UserProfile.builder().id(sendMessageRequest.getSenderId()).build());
            newMessage.setMessageType(Message.MessageType.TEXT);
            newMessage.setTimeSend(LocalDateTime.now());
            newMessage.setConversation(Conversation.builder().id(sendMessageRequest.getConversationId()).build());
            newMessage.setContent(sendMessageRequest.getContent());


            SaveAndSendMessageNotify(sendMessageRequest,false);


            newMessage=messageService.createMessage(newMessage);
            //update last message
            conversationService.updatePreviewonversation(sendMessageRequest.getConversationId(),
                                                        sendMessageRequest.getSenderId(),
                                                        newMessage.getId());

            //luu tin nhan
            messageService.createMessage(newMessage);

        }
        else{
            try {
                if (!(sendMessageRequest.getImageUrl().isEmpty())) {
                    MultipartFile multipartFile = convert(sendMessageRequest.getImageUrl(), "converted_file.png", "image/png");
                    //lay link anh cloud
                    String url = uploadImageFile.uploadImage(multipartFile);

                    Message newMessage=new Message();
                    newMessage.setSender(UserProfile.builder().id(sendMessageRequest.getSenderId()).build());
                    newMessage.setTimeSend(LocalDateTime.now());
                    newMessage.setConversation(Conversation.builder().id(sendMessageRequest.getConversationId()).build());


                    //kiem tra co kèm nhan tin hay khong
                    String content=sendMessageRequest.getContent().trim();
                    if(!content.isEmpty()){

                        newMessage.setMessageType(Message.MessageType.TEXT);
                        newMessage.setContent(sendMessageRequest.getContent());

                        messagingTemplate.convertAndSend("/conversation/" + sendMessageRequest.getConversationId(),
                                                        newMessage);

                        //Gui sse
                        SaveAndSendMessageNotify(sendMessageRequest,false);

                        //luu tin nhan
                        newMessage=messageService.createMessage(newMessage);
                        //update last message
                        conversationService.updatePreviewonversation(sendMessageRequest.getConversationId(),
                                sendMessageRequest.getSenderId(),
                                newMessage.getId());
                    }

                    //Gui link anh
                    System.out.println(url);

                    newMessage.setMessageType(Message.MessageType.IMAGE);
                    newMessage.setContent(url);

                    messagingTemplate.convertAndSend("/conversation/" + sendMessageRequest.getConversationId(),
                            newMessage);

                    //Gui sse
                    SaveAndSendMessageNotify(sendMessageRequest,true);


                    //update last message
                    newMessage=messageService.createMessage(newMessage);
                    //update last message
                    conversationService.updatePreviewonversation(sendMessageRequest.getConversationId(),
                            sendMessageRequest.getSenderId(),
                            newMessage.getId());
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @MessageMapping("/typing")
    public void typing(@Payload TypingPayload typingPayload) {
        System.out.println(typingPayload);
        messagingTemplate.convertAndSend("/typing/" + typingPayload.getConversationId(), typingPayload);
    }


    public void SaveAndSendMessageNotify(SendMessageRequest sendMessageRequest,Boolean isImage){
        for(Integer i:sendMessageRequest.getReceiveIds()){
            MessageNotify mn=new MessageNotify();
            mn.setSenderId(sendMessageRequest.getSenderId());
            mn.setConversationId(sendMessageRequest.getConversationId());
            mn.setReceiveId(i);
            mn.setContent(isImage?"Đã gửi 1 ảnh":sendMessageRequest.getContent());
            mn.setSendAt(sendMessageRequest.getTimeSend());
            //luu thong bao
            messageNotifyService.create(mn);
            //gui thong bao sse
            MessageNotifyDTO notifyDTO=MessageNotifyDTO.builder()
                    .content(mn.getContent())
                    .conversationId(mn.getConversationId())
                    .senderName(sendMessageRequest.getNameSend())
                    .receiveId(i)
                    .sendAt(sendMessageRequest.getTimeSend()).build();
            sseService.sendNotification(i, notifyDTO);
        }
    }

    public static MultipartFile convert(String base64, String fileName, String contentType) throws IOException {
        if (base64.contains(",")) {
            base64 = base64.split(",")[1];
        }

        byte[] fileBytes = Base64.getDecoder().decode(base64);

        // Tạo MultipartFile từ byte[]
        return new MockMultipartFile(fileName, fileName, contentType, fileBytes);
    }

    //    @MessageMapping("/personnalmessage")
//    @SendToUser("/queue/personnalmessage")
//    public String reply(@Payload String message,
//                        Principal user) {
//        System.out.println("goi personnalmessage");
//        //System.out.println(SecurityContextHolder.getContext().getAuthentication().getName());
//        return message;
//    }

}

