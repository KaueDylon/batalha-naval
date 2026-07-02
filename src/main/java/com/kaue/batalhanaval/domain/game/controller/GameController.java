package com.kaue.batalhanaval.domain.game.controller;
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

    @GetMapping("/{id}/board/{targetId}")
    public ResponseEntity<int[][]> board(@PathVariable String id,
                                         @PathVariable String targetId,
                                         @AuthenticationPrincipal UUID user){
        int[][] board = gameService.getBoard(id, user.toString(), targetId);
        return ResponseEntity.ok(board);
    }


}
