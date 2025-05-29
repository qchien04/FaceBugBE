package com.controller.chatrealtime;

import com.DTO.ConversationDTO;
import com.DTO.FriendDTO;
import com.DTO.MemberGroupChatDTO;
import com.entity.auth.UserProfile;
import com.entity.chatrealtime.Conversation;
import com.entity.chatrealtime.Message;
import com.exception.ConversationException;
import com.exception.UserException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.request.AddMemberGroupRequest;
import com.request.ChangeAvtGroupRequest;
import com.request.CreateConversationRequest;
import com.request.RemoveMemberGroupRequest;
import com.response.ApiResponse;
import com.service.CustomUserDetails;
import com.service.SseService;
import com.service.UpLoadImageFileService;
import com.service.chatrealtime.ConversationService;
import com.service.chatrealtime.ConversationUserService;
import com.service.chatrealtime.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/conversation")
public class ConversationController {

    @Autowired
    private UpLoadImageFileService uploadImageFile;

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ConversationUserService conversationUserService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private SseService sseService;

    @PostMapping("/create")
    public ResponseEntity<ConversationDTO> createConversationHandler(@RequestBody CreateConversationRequest createConversationRequest) throws UserException {

        ConversationDTO conversation=conversationService.createConversation(createConversationRequest);

        return new ResponseEntity<ConversationDTO>(conversation, HttpStatus.OK);

    }

    @PostMapping("/addMemberGroup")
    public ResponseEntity<MemberGroupChatDTO> addGroupConversationHandler(@RequestBody AddMemberGroupRequest request) throws UserException {

        MemberGroupChatDTO memberGroupChatDTO=conversationUserService.addMemberToGroup(request.getConversationId(),
                request.getFriend().getFriendId());

        for(int i:request.getMembers()){
            sseService.sendAddMember(i,request.getConversationId(),memberGroupChatDTO);
        }

        Integer id=((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        Message message=new Message();
        message.setConversation(Conversation.builder().id(request.getConversationId()).build());
        message.setMessageType(Message.MessageType.NOTICE);
        message.setContent("Đã thêm "+request.getFriend().getFriendName()+" vào nhóm!");
        message.setSender(UserProfile.builder().id(id).build());
        message.setTimeSend(LocalDateTime.now());

        messagingTemplate.convertAndSend("/conversation/" + request.getConversationId(),
                message);

        messageService.createMessage(message);

        return new ResponseEntity<MemberGroupChatDTO>(memberGroupChatDTO, HttpStatus.OK);

    }
    
    @GetMapping("/allConversation")
    public ResponseEntity<List<ConversationDTO>> findAllConversationByUserEmailHandler(@RequestHeader("Authorization") String jwt)
            throws UserException {
        List<ConversationDTO> chats = conversationService.findUserConversation();
        return new ResponseEntity<List<ConversationDTO>>(chats, HttpStatus.OK);
    }


    @GetMapping("/allMemberInConversation")
    public ResponseEntity<List<MemberGroupChatDTO>> findAllConversationByUserEmailHandler(@RequestParam(value = "conversationId") Integer conversationId)
            throws UserException {
        List<MemberGroupChatDTO> chats = conversationService.findAllMemberInConversation(conversationId);
        for(MemberGroupChatDTO i :chats){
            System.out.println(i);
        }
        return new ResponseEntity<List<MemberGroupChatDTO>>(chats, HttpStatus.OK);
    }

    @GetMapping("/getConversation")
    public ResponseEntity<ConversationDTO> getConversationHandler(@RequestParam(value = "conversationId", required = false) Integer conversationId)
            throws UserException {

        ConversationDTO c = conversationService.findConversationById(conversationId);
        return new ResponseEntity<ConversationDTO>(c, HttpStatus.OK);
    }

    @DeleteMapping("/removeMember")
    public ResponseEntity<ApiResponse> removeMemberHandler(@RequestBody RemoveMemberGroupRequest request){


        conversationUserService.removeMember(request.getConversationId(),request.getMember().getMemberId());
        for(int i:request.getMembers()){
            sseService.sendKickMember(i,request.getConversationId(),request.getMember().getMemberId());
        }
        Integer id=((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        Message message=new Message();
        message.setConversation(Conversation.builder().id(request.getConversationId()).build());
        message.setMessageType(Message.MessageType.NOTICE);
        message.setContent("Đã đá "+request.getMember().getMemberName()+" ra khỏi nhóm!");
        message.setSender(UserProfile.builder().id(id).build());
        message.setTimeSend(LocalDateTime.now());

        messagingTemplate.convertAndSend("/conversation/" + request.getConversationId(),
                message);

        ApiResponse res = new ApiResponse("Deleted Successfully...", true);
        messageService.createMessage(message);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/changeAvtGroup")
    public ResponseEntity<ApiResponse> changeAvtGroupHandler(
            @RequestParam("file") MultipartFile file,
            @RequestParam("request") String requestJson) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        ChangeAvtGroupRequest request = objectMapper.readValue(requestJson, ChangeAvtGroupRequest.class);

        Integer userId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        request.getMembers().add(userId);

        String imageUrl = uploadImageFile.uploadImage(file);
        System.out.println(imageUrl);
        for (int memberId : request.getMembers()) {
            sseService.sendChangeAvt(memberId, request.getConversationId(), imageUrl);
        }

        Message message = new Message();
        message.setConversation(Conversation.builder().id(request.getConversationId()).build());
        message.setMessageType(Message.MessageType.NOTICE);
        message.setContent("Ảnh nhóm đã được thay đổi");
        message.setSender(UserProfile.builder().id(userId).build());
        message.setTimeSend(LocalDateTime.now());

        messagingTemplate.convertAndSend("/conversation/" + request.getConversationId(), message);
        messageService.createMessage(message);

        Conversation conversation=conversationService.findById(request.getConversationId());
        conversation.setAvt(imageUrl);
        conversationService.save(conversation);
        // Trả về phản hồi thành công
        return ResponseEntity.ok(new ApiResponse("Ảnh nhóm đã được cập nhật!", true));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> outConversationHandle(@PathVariable(value = "id") Integer id)
            throws UserException {

        boolean success=conversationUserService.outConversation(id);
        ApiResponse apiResponse=new ApiResponse();
        apiResponse.setMessage("Success");
        apiResponse.setStatus(true);
        return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);
    }



}