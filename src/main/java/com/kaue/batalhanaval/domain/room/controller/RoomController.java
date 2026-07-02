package com.kaue.batalhanaval.domain.room.controller;

import com.kaue.batalhanaval.domain.room.dto.RoomResponse;
import com.kaue.batalhanaval.domain.room.entity.Room;
import com.kaue.batalhanaval.domain.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping("/create")
    public ResponseEntity<RoomResponse> create(@RequestParam String hostName,
                                               @AuthenticationPrincipal UUID user) {
        Room room = roomService.createRoom(user.toString(), hostName);
        return ResponseEntity.ok(toResponse(room));
    }

    @PostMapping("/{roomId}/join")
    public ResponseEntity<RoomResponse> join(@PathVariable String roomId,
                                             @AuthenticationPrincipal UUID user) {
        Room room = roomService.joinRoom(roomId, user.toString());
        return ResponseEntity.ok(toResponse(room));
    }

    @PostMapping("/join-by-code")
    public ResponseEntity<RoomResponse> joinByCode(@RequestParam String code,
                                                   @AuthenticationPrincipal UUID user) {
        Room room = roomService.joinByCode(code, user.toString());
        return ResponseEntity.ok(toResponse(room));
    }

    @GetMapping
    public ResponseEntity<List<RoomResponse>> listOpen() {
        List<RoomResponse> rooms = roomService.listOpenRooms().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(rooms);
    }

    private RoomResponse toResponse(Room room) {
        return new RoomResponse(
                room.getId(), room.getCode(), room.getHostId(),
                room.getHostName(), room.getGuestId(), room.getGameId(),
                room.getStatus().name()
        );
    }
}
