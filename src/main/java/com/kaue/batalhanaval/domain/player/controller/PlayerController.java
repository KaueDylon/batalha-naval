package com.kaue.batalhanaval.domain.player.controller;

import com.kaue.batalhanaval.domain.player.dto.PlayerResponse;
import com.kaue.batalhanaval.domain.player.dto.PlayerUpdateRequest;
import com.kaue.batalhanaval.domain.player.service.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/player")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping("/me")
    public ResponseEntity<PlayerResponse> me(@AuthenticationPrincipal UUID userId) {
        return ResponseEntity.ok(playerService.findById(userId));
    }

    @PatchMapping("/me")
    public ResponseEntity<PlayerResponse> update(@AuthenticationPrincipal UUID userId,
                                                  @RequestBody @Valid PlayerUpdateRequest req) {
        return ResponseEntity.ok(playerService.update(userId, req));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UUID userId) {
        playerService.delete(userId);
        return ResponseEntity.noContent().build();
    }
}
