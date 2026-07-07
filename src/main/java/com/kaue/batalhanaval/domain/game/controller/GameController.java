package com.kaue.batalhanaval.domain.game.controller;
import com.kaue.batalhanaval.domain.game.dto.BoardCellResponse;
import com.kaue.batalhanaval.domain.game.dto.GameStateResponse;
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

    @GetMapping("/{id}/board/{targetId}")
    public ResponseEntity<BoardCellResponse[][]> board(@PathVariable String id,
                                         @PathVariable String targetId,
                                         @AuthenticationPrincipal UUID user){
        BoardCellResponse[][] board = gameService.getBoardDetailed(id, user.toString(), targetId);
        return ResponseEntity.ok(board);
    }

    @GetMapping("/{id}/state")
    public ResponseEntity<GameStateResponse> state(@PathVariable String id,
                                                   @AuthenticationPrincipal UUID user){
        GameStateResponse state = gameService.getGameState(id, user.toString());
        return ResponseEntity.ok(state);
    }


}
