package com.kaue.batalhanaval.config;

import com.kaue.batalhanaval.domain.game.dto.*;
import com.kaue.batalhanaval.domain.game.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class WebSocketController {
    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/game/{gameId}/attack")
    public void attack(@DestinationVariable String gameId,
                       @Payload AttackRequest req,
                       Principal principal){

        String attackerId = principal.getName();
        AttackResult result = gameService.attack(gameId, attackerId, req.row(), req.col());

        AttackResponse response = new AttackResponse(result.status(), req.row(), req.col(), attackerId, result.nextTurn());

        messagingTemplate.convertAndSend("/topic/game/" + gameId, response);

        if ("GAME_OVER".equals(result.status())) {
            GameEvent event = new GameEvent("GAME_OVER", "Partida Finalizada", response.nextTurn());
            messagingTemplate.convertAndSend("/topic/game/" + gameId, event);
        }
    }

    @MessageMapping("/game/{gameId}/place")
    public void place(@DestinationVariable String gameId,
                      @Payload PlaceShipRequest req,
                      Principal principal){
        String playerId = principal.getName();
        boolean ok = gameService.placeShip(gameId,playerId,req);

        messagingTemplate.convertAndSendToUser(
                playerId, "/queue/place-result", ok ? "OK" : "INVALID"
        );
    }

    @MessageMapping("/game/{gameId}/ready")
    public void ready(@DestinationVariable String gameId, Principal principal){
        String playerId = principal.getName();
        boolean gameStarted = gameService.playerReady(gameId, playerId);

        GameEvent readyEvent = new GameEvent("PLAYER_READY", "Jogador pronto.", playerId);
        messagingTemplate.convertAndSend("/topic/game/"+gameId, readyEvent);

        if (gameStarted){
            String firstTurn = gameService.getGame(gameId).getCurrentTurn();
            GameEvent startEvent = new GameEvent("GAME_STARTED", "Ambos prontos.", firstTurn);
            messagingTemplate.convertAndSend("/topic/game/"+gameId, startEvent);
        }
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Exception exception){
        return exception.getMessage();
    }
}
