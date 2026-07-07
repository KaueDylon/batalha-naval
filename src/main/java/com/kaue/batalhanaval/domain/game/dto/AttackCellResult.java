package com.kaue.batalhanaval.domain.game.dto;

import com.kaue.batalhanaval.commons.enums.ShipType;

public record AttackCellResult(String status, ShipType shipType) {
}
