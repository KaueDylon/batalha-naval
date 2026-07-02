package com.kaue.batalhanaval.domain.ranking.dto;

import java.util.UUID;

public record RankingResponse(
        UUID playerId,
        String name,
        String nation,
        String portrait,
        int wins,
        int losses,
        double winrate
) {
}
