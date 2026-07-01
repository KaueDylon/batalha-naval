package com.kaue.batalhanaval.config;

import com.kaue.batalhanaval.infra.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel){
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())){
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")){
                try{
                    var decoded = jwtService.validateToken(authHeader.substring(7));
                    String role = decoded.getClaim("role").asString();

                    var auth = new UsernamePasswordAuthenticationToken(
                            UUID.fromString(decoded.getSubject()),
                            null,
                            role != null ? List.of(new SimpleGrantedAuthority("ROLE_" + role)) : List.of()
                    );

                    accessor.setUser(auth);
                }catch (Exception e){
                    throw new IllegalArgumentException("Token no WebSocket inválido", e);
                }
            }
        }
        return message;
    }

}
