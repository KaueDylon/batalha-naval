package com.kaue.batalhanaval.domain.player.dto;

import java.util.UUID;

public record PlayerResponse(
        UUID id,
        String name
) {
}
