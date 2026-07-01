package com.kaue.batalhanaval.domain.game.dto;

public record AttackResponse(String status, int row, int col, String attackerId, String nextTurn) {
}
