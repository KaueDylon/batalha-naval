package com.kaue.batalhanaval.domain.ranking.controller;

import com.kaue.batalhanaval.domain.ranking.dto.RankingResponse;
import com.kaue.batalhanaval.domain.ranking.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ranking")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    @GetMapping
    public ResponseEntity<List<RankingResponse>> top(@RequestParam(defaultValue = "20") int limit,
                                                     @RequestParam(defaultValue = "0") int offset){
        return ResponseEntity.ok(rankingService.getTopPlayers(limit, offset));
    }

    @GetMapping("/me")
    public ResponseEntity<Integer> myPosition(@AuthenticationPrincipal UUID userId){
        return ResponseEntity.ok(rankingService.getPlayerPosition(userId));
    }
}
