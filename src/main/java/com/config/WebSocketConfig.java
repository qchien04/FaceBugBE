package com.config;

import com.constant.JwtConstant;
import com.exception.UserException;
import com.service.chatrealtime.ConversationUserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

import javax.crypto.SecretKey;
import java.security.Principal;
import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@AllArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final ConversationUserService conversationUserService;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String token = null;
                    List<String> authHeaders = accessor.getNativeHeader("Authorization");
                    if (authHeaders != null && !authHeaders.isEmpty()) {
                        token = authHeaders.get(0);
                        System.out.println(token);
                        String jwt = token.substring(7);

                        try {
                            SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());
                            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();

                            Integer id = claims.get("id", Integer.class);

                            accessor.getSessionAttributes().put("USER_ID", id);
                            Principal principal=new CustomPrincipalChat(id);
                            accessor.setUser(principal);
                        } catch (Exception e) {
                            System.out.println("Invalid JWT token: " + e.getMessage());
                        }
                    }
                }
                else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    Integer userId = (Integer) accessor.getSessionAttributes().get("USER_ID");

                    if (userId == null && accessor.getSessionAttributes() != null) {
                        throw new UserException("Not authenticated");
                    }

                    String destination = accessor.getDestination();

                    if (destination != null) {
                        String s = destination.substring(1);
                        System.out.println(s +"-------------------------------------------");
                        String[] arr = s.split("/");
                        if (arr.length > 1) {
                            if(arr[0].equals("user")){
                                CustomPrincipalChat principalChat=(CustomPrincipalChat) accessor.getUser();
                                if(principalChat!=null && principalChat.getName()!=null){
                                    return message;
                                }
                                else throw new RuntimeException("Connect not valid because user not available!");
                            }
                            else{
                                try {
                                    Integer conversationId = Integer.parseInt(arr[1]);
                                    CustomPrincipalChat principalChat=(CustomPrincipalChat) accessor.getUser();
                                    boolean isInChat=conversationUserService.checkExist(conversationId,userId);
                                    if(!isInChat){
                                        throw new RuntimeException("You are not allowed to access channel " + destination);
                                    }
                                    assert principalChat != null;
                                    principalChat.addChannel(conversationId);
                                    String subscriptionId = accessor.getSubscriptionId();
                                    accessor.getSessionAttributes().put(subscriptionId, destination);

                                } catch (NumberFormatException e) {
                                    throw new RuntimeException("Conversation is not valid");
                                }
                            }
                        }
                    }
                }
                else if (StompCommand.UNSUBSCRIBE.equals(accessor.getCommand())) {

                    Integer userId = (Integer) accessor.getSessionAttributes().get("USER_ID");

                    if (userId == null && accessor.getSessionAttributes() != null) {
                        throw new UserException("Not authenticated");
                    }

                    String subscriptionId = accessor.getSubscriptionId();
                    String destination = (String) accessor.getSessionAttributes().get(subscriptionId);

                    if (destination != null) {
                        String s = destination.substring(1);
                        String[] arr = s.split("/");
                        if (arr.length > 1) {
                            try {
                                Integer conversationId = Integer.parseInt(arr[1]);
                                System.out.println("conId "+ conversationId);
                                CustomPrincipalChat principalChat=(CustomPrincipalChat) accessor.getUser();
                                principalChat.removeChannel(conversationId);
                            } catch (NumberFormatException e) {
                                throw new RuntimeException("Conversation is not valid");
                            }
                        }

                    }
                }

                return message;
            }
        });
    }



    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .addInterceptors(new CustomHandshakeInterceptor())
                .setAllowedOriginPatterns("*");
//                .setAllowedOrigins(
//                        "http://localhost:3000",
//                        "http://localhost:5174",
//                        "http://localhost:5173"
//                );
    }
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app"); // Channel for client to send messages to server
        registry.enableSimpleBroker("/conversation", "/queue", "/topic"); // Channels for client to listen to
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(512 * 1024) // 512KB
                .setSendBufferSizeLimit(1024 * 1024) // 1MB
                .setTimeToFirstMessage(5000); // 5 second timeout
    }

}