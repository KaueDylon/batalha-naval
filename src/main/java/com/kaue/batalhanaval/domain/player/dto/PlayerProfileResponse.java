package com.kaue.batalhanaval.domain.player.dto;

import java.util.UUID;

public record PlayerProfileResponse(
        UUID id,
        String name,
        String nation,
        String portrait
) {
}
