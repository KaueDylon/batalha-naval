package com.kaue.batalhanaval.domain.game.controller;

import com.kaue.batalhanaval.domain.game.dto.AttackRequest;
import com.kaue.batalhanaval.domain.game.dto.PlaceShipRequest;
import com.kaue.batalhanaval.domain.game.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestParam String playerBid,
                                         @AuthenticationPrincipal UserDetails user){
        String gameId = gameService.createGame(user.getUsername(), playerBid);
        return ResponseEntity.ok(gameId);
    }

    @PostMapping("/{id}/place")
    public ResponseEntity<Boolean> place(@PathVariable String id,
                                         @RequestBody PlaceShipRequest req,
                                         @AuthenticationPrincipal UserDetails user){
        boolean ok = gameService.placeShip(id, user.getUsername(), req);
        return ResponseEntity.ok(ok);
    }

    @PostMapping("/{id}/attack")
    public ResponseEntity<String> attack(@PathVariable String id,
                                         @RequestBody AttackRequest req,
                                         @AuthenticationPrincipal UserDetails user){
        String result = gameService.attack(id, user.getUsername(), req.row(), req.col());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/board/{targetId}")
    public ResponseEntity<int[][]> board(@PathVariable String id,
                                         @PathVariable String targetId,
                                         @AuthenticationPrincipal UserDetails user){
        int[][] board = gameService.getBoard(id, user.getUsername(), targetId);
        return ResponseEntity.ok(board);
    }


}
