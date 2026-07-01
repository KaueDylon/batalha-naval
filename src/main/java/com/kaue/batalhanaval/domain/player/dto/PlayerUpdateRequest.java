package com.kaue.batalhanaval.domain.player.dto;

import jakarta.validation.constraints.Size;

public record PlayerUpdateRequest(
        String name,
        @Size(min = 6) String password
) {
}
