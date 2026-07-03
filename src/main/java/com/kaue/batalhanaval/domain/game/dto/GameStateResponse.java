package com.kaue.batalhanaval.domain.game.dto;

public record GameStateResponse(
        String gameId,
        String phase,
        String currentTurn,
        String playerAId,
        String plauerBid,
        boolean playerAReady,
        boolean playerBReady,
        String winner
) {}
