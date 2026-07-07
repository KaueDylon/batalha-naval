package com.kaue.batalhanaval.config;

import com.kaue.batalhanaval.domain.game.Game;
import com.kaue.batalhanaval.domain.game.dto.*;
import com.kaue.batalhanaval.domain.game.service.GameService;
import com.kaue.batalhanaval.domain.match.service.MatchHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class WebSocketController {
    private final GameService gameService;
    private final MatchHistoryService matchHistoryService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/game/{gameId}/attack")
    public void attack(@DestinationVariable String gameId,
                       @Payload AttackRequest req,
                       Principal principal){

        String attackerId = principal.getName();
        AttackResult result = gameService.attack(gameId, attackerId, req.row(), req.col());

        AttackResponse response = new AttackResponse(result.status(), req.row(), req.col(), attackerId, result.nextTurn(), result.shipType());

        messagingTemplate.convertAndSend("/topic/game/" + gameId, response);

        if ("GAME_OVER".equals(result.status())) {

            Game game = gameService.getGame(gameId);
            String loserId = attackerId.equals(game.getPlayerAId()) ? game.getPlayerBId() : game.getPlayerAId();

            matchHistoryService.recordMatch(UUID.fromString(attackerId), UUID.fromString(loserId));

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

    @MessageMapping("/game/{gameId}/clear")
    public void clear(@DestinationVariable String gameId, Principal principal){
        String playerId = principal.getName();
        gameService.clearBoard(gameId, playerId);

        messagingTemplate.convertAndSendToUser(
                playerId, "/queue/place-result", "BOARD_CLEARED"
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

    @MessageMapping("/game/{gameId}/surrender")
    public void surrender(@DestinationVariable String gameId, Principal principal){
        String playerId = principal.getName();
        String winnerId = gameService.surrender(gameId, playerId);

        matchHistoryService.recordMatch(UUID.fromString(winnerId), UUID.fromString(playerId));

        GameEvent event = new GameEvent("PLAYER_SURRENDERED", "Jogador abandonou a partida.", playerId);
        messagingTemplate.convertAndSend("/topic/game/" + gameId, event);

        GameEvent gameOver = new GameEvent("GAME_OVER", "Partida finalizada por abandono.", winnerId);
        messagingTemplate.convertAndSend("/topic/game/" + gameId, gameOver);
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Exception exception){
        return exception.getMessage();
    }
}
