package com.kaue.batalhanaval.config;

import com.kaue.batalhanaval.domain.game.dto.GameEvent;
import com.kaue.batalhanaval.domain.game.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketDisconnectListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final GameService gameService;

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        Principal principal = event.getUser();
        if (principal == null) {
            return;
        }

        String playerId = principal.getName();
        log.info("Jogador desconectou: {}", playerId);

        String gameId = gameService.findGameIdByPlayer(playerId);
        if (gameId == null) {
            return;
        }

        // Notifica o oponente e todos inscritos no tópico da partida
        GameEvent disconnectEvent = new GameEvent("PLAYER_DISCONNECTED", "Oponente desconectou.", playerId);
        messagingTemplate.convertAndSend("/topic/game/" + gameId, disconnectEvent);
    }
}
