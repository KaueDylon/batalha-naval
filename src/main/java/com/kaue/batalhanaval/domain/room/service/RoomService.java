package com.kaue.batalhanaval.domain.room.service;

import com.kaue.batalhanaval.commons.enums.RoomStatus;
import com.kaue.batalhanaval.domain.game.service.GameService;
import com.kaue.batalhanaval.domain.room.dto.RoomResponse;
import com.kaue.batalhanaval.domain.room.entity.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();

    public Room createRoom(String hostId, String hostName) {
        String roomId = UUID.randomUUID().toString();
        String code = generateCode();
        Room room = new Room(roomId, code, hostId, hostName);
        rooms.put(roomId, room);
        return room;
    }

    public Room joinRoom(String roomId, String guestId) {
        Room room = getRoom(roomId);
        if (!room.join(guestId)) {
            throw new IllegalStateException("Não foi possível entrar na sala.");
        }

        String gameId = gameService.createGame(room.getHostId(), guestId);
        room.setGameId(gameId);
        room.setStatus(RoomStatus.IN_GAME);

        RoomResponse response = toResponse(room);
        messagingTemplate.convertAndSendToUser(
                room.getHostId(), "/queue/room-joined", response
        );

        return room;
    }

    public Room joinByCode(String code, String guestId) {
        Room room = rooms.values().stream()
                .filter(r -> r.getCode().equals(code) && r.getStatus() == RoomStatus.WAITING)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Sala não encontrada ou já está cheia."));
        return joinRoom(room.getId(), guestId);
    }

    public List<Room> listOpenRooms() {
        return rooms.values().stream()
                .filter(r -> r.getStatus() == RoomStatus.WAITING)
                .toList();
    }

    public Room getRoom(String roomId) {
        Room room = rooms.get(roomId);
        if (room == null) throw new IllegalArgumentException("Sala não encontrada.");
        return room;
    }

    private RoomResponse toResponse(Room room) {
        return new RoomResponse(
                room.getId(), room.getCode(), room.getHostId(),
                room.getHostName(), room.getGuestId(), room.getGameId(),
                room.getStatus().name()
        );
    }

    private String generateCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

}
