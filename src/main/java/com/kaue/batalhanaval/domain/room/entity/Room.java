package com.kaue.batalhanaval.domain.room.entity;

import com.kaue.batalhanaval.commons.enums.RoomStatus;
import lombok.*;

@Getter
@Setter
public class Room {

    private String id;
    private String code;
    private String hostId;
    private String hostName;
    private String guestId;
    private String gameId;
    private RoomStatus status;

    public Room(String id, String code, String hostId, String hostName){
        this.id = id;
        this.code = code;
        this.hostId = hostId;
        this.hostName = hostName;
        this.status = RoomStatus.WAITING;
    }

    public boolean join(String guestId){
        if (this.status != RoomStatus.WAITING) return false;
        if (guestId.equals(hostId)) return false;
        this.guestId = guestId;
        this.status = RoomStatus.FULL;
        return true;
    }
}
