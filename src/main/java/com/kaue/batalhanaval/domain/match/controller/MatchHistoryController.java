package com.kaue.batalhanaval.domain.match.controller;

import com.kaue.batalhanaval.domain.match.dto.MatchHistoryResponse;
import com.kaue.batalhanaval.domain.match.service.MatchHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/matches")
@RequiredArgsConstructor
public class MatchHistoryController {

    private final MatchHistoryService matchHistoryService;

    @GetMapping("/me")
    public ResponseEntity<List<MatchHistoryResponse>> myHistory(
            @AuthenticationPrincipal UUID userId) {
        return ResponseEntity.ok(matchHistoryService.getPlayerHistoryResponse(userId));
    }

}
