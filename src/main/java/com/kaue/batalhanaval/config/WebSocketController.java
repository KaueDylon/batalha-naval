package com.kaue.batalhanaval.config;

import com.kaue.batalhanaval.domain.game.dto.AttackRequest;
import com.kaue.batalhanaval.domain.game.dto.AttackResponse;
import com.kaue.batalhanaval.domain.game.dto.AttackResult;
import com.kaue.batalhanaval.domain.game.dto.PlaceShipRequest;
import com.kaue.batalhanaval.domain.game.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
}
