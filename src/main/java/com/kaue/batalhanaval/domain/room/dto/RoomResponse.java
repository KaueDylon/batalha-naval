package com.kaue.batalhanaval.domain.room.dto;

public record RoomResponse(
        String roomId,
        String code,
        String hostId,
        String hostName,
        String guestId,
        String guestName,
        String gameId,
        String status
) {}