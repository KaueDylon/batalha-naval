package com.kaue.batalhanaval.domain.game.controller;

import com.kaue.batalhanaval.domain.game.dto.AttackRequest;
import com.kaue.batalhanaval.domain.game.dto.AttackResult;
import com.kaue.batalhanaval.domain.game.dto.PlaceShipRequest;
import com.kaue.batalhanaval.domain.game.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestParam String playerBid,
                                         @AuthenticationPrincipal UUID user){
        String gameId = gameService.createGame(user.toString(), playerBid);
        return ResponseEntity.ok(gameId);
    }

    @PostMapping("/{id}/place")
    public ResponseEntity<Boolean> place(@PathVariable String id,
                                         @RequestBody PlaceShipRequest req,
                                         @AuthenticationPrincipal UUID user){
        boolean ok = gameService.placeShip(id, user.toString(), req);
        return ResponseEntity.ok(ok);
    }

    @PostMapping("/{id}/attack")
    public ResponseEntity<AttackResult> attack(@PathVariable String id,
                                               @RequestBody AttackRequest req,
                                               @AuthenticationPrincipal UUID user){
        AttackResult result = gameService.attack(id, user.toString(), req.row(), req.col());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/board/{targetId}")
    public ResponseEntity<int[][]> board(@PathVariable String id,
                                         @PathVariable String targetId,
                                         @AuthenticationPrincipal UUID user){
        int[][] board = gameService.getBoard(id, user.toString(), targetId);
        return ResponseEntity.ok(board);
    }


}
