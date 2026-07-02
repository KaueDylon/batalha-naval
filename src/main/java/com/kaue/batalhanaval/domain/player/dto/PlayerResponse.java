package com.kaue.batalhanaval.domain.player.dto;

import java.util.UUID;

public record PlayerResponse(
        UUID id,
        String name,
        String email,
        String nation,
        String portrait,
        int wins,
        int losses
) {
}
