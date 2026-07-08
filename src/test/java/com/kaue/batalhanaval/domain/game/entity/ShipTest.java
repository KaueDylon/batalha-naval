package com.kaue.batalhanaval.domain.game.entity;

import com.kaue.batalhanaval.commons.enums.ShipType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShipTest {

    @Test
    void newShip_shouldNotBeSunk() {
        Ship ship = new Ship(3, true, 0, false);
        assertFalse(ship.isSunk());
    }

    @Test
    void hit_shouldIncrementHitCount() {
        Ship ship = new Ship(3, true, 0, false);
        ship.hit();
        assertEquals(1, ship.getHits());
    }

    @Test
    void isSunk_shouldReturnTrue_whenHitsEqualSize() {
        Ship ship = new Ship(2, true, 0, false);
        ship.hit();
        ship.hit();
        assertTrue(ship.isSunk());
    }

    @Test
    void isSunk_shouldReturnFalse_whenHitsLessThanSize() {
        Ship ship = new Ship(3, true, 0, false);
        ship.hit();
        ship.hit();
        assertFalse(ship.isSunk());
    }

    @Test
    void shipType_shouldBeSetCorrectly() {
        Ship ship = new Ship(5, true, 0, false);
        ship.setShipType(ShipType.CARRIER);
        assertEquals(ShipType.CARRIER, ship.getShipType());
        assertEquals(5, ShipType.CARRIER.getSize());
    }
}
