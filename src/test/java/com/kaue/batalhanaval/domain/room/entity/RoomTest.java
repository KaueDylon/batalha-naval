package com.kaue.batalhanaval.domain.room.entity;

import com.kaue.batalhanaval.commons.enums.RoomStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoomTest {

    private Room room;

    @BeforeEach
    void setUp() {
        room = new Room("room-1", "ABC123", "host-id", "Host");
    }

    @Test
    void newRoom_shouldHaveWaitingStatus() {
        assertEquals(RoomStatus.WAITING, room.getStatus());
    }

    @Test
    void newRoom_shouldHaveCorrectHostInfo() {
        assertEquals("host-id", room.getHostId());
        assertEquals("Host", room.getHostName());
        assertEquals("ABC123", room.getCode());
    }

    @Test
    void join_shouldSucceed_whenStatusIsWaiting() {
        boolean result = room.join("guest-id", "Guest");
        assertTrue(result);
        assertEquals("guest-id", room.getGuestId());
        assertEquals("Guest", room.getGuestName());
        assertEquals(RoomStatus.FULL, room.getStatus());
    }

    @Test
    void join_shouldFail_whenRoomIsFull() {
        room.join("guest-1", "Guest 1");
        boolean result = room.join("guest-2", "Guest 2");
        assertFalse(result);
    }

    @Test
    void join_shouldFail_whenHostTriesToJoinOwnRoom() {
        boolean result = room.join("host-id", "Host");
        assertFalse(result);
        assertEquals(RoomStatus.WAITING, room.getStatus());
    }

    @Test
    void join_shouldFail_whenRoomStatusIsNotWaiting() {
        room.setStatus(RoomStatus.IN_GAME);
        boolean result = room.join("guest-id", "Guest");
        assertFalse(result);
    }
}
