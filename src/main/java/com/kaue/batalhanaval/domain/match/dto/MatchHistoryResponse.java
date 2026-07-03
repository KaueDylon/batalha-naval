package com.kaue.batalhanaval.domain.match.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record MatchHistoryResponse(
        UUID matchId,
        UUID winnerId,
        UUID loserId,
        boolean victory,
        LocalDateTime playedAt
){
}
