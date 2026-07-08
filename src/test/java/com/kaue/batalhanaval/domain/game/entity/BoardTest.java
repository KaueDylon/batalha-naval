package com.kaue.batalhanaval.domain.game.entity;

import com.kaue.batalhanaval.commons.enums.ShipType;
import com.kaue.batalhanaval.domain.game.dto.AttackCellResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    @Test
    void newBoard_shouldHaveEmptyGrid() {
        int[][] grid = board.getGrid();
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                assertEquals(Board.WATER, grid[r][c]);
            }
        }
    }

    @Test
    void placeShip_horizontal_shouldSucceed() {
        Ship ship = new Ship(3, true, 0, false);
        assertTrue(board.placeShip(0, 0, ship));
        assertEquals(1, board.getShipCount());

        int[][] grid = board.getGrid();
        assertEquals(Board.SHIP, grid[0][0]);
        assertEquals(Board.SHIP, grid[0][1]);
        assertEquals(Board.SHIP, grid[0][2]);
    }

    @Test
    void placeShip_vertical_shouldSucceed() {
        Ship ship = new Ship(3, false, 0, false);
        assertTrue(board.placeShip(0, 0, ship));

        int[][] grid = board.getGrid();
        assertEquals(Board.SHIP, grid[0][0]);
        assertEquals(Board.SHIP, grid[1][0]);
        assertEquals(Board.SHIP, grid[2][0]);
    }

    @Test
    void placeShip_shouldFail_whenExceedsBoardHorizontally() {
        Ship ship = new Ship(4, true, 0, false);
        assertFalse(board.placeShip(0, 8, ship));
        assertEquals(0, board.getShipCount());
    }

    @Test
    void placeShip_shouldFail_whenExceedsBoardVertically() {
        Ship ship = new Ship(4, false, 0, false);
        assertFalse(board.placeShip(8, 0, ship));
        assertEquals(0, board.getShipCount());
    }

    @Test
    void placeShip_shouldFail_whenOverlapsExistingShip() {
        Ship ship1 = new Ship(3, true, 0, false);
        board.placeShip(0, 0, ship1);

        Ship ship2 = new Ship(3, false, 0, false);
        assertFalse(board.placeShip(0, 1, ship2));
        assertEquals(1, board.getShipCount());
    }

    @Test
    void placeShip_shouldFail_whenMaxShipsReached() {
        for (int i = 0; i < Board.MAX_SHIPS; i++) {
            Ship ship = new Ship(2, true, 0, false);
            board.placeShip(i, 0, ship);
        }
        assertEquals(Board.MAX_SHIPS, board.getShipCount());

        Ship extra = new Ship(2, true, 0, false);
        assertFalse(board.placeShip(9, 0, extra));
    }

    @Test
    void placeShip_shouldFail_withNegativeCoordinates() {
        Ship ship = new Ship(2, true, 0, false);
        assertFalse(board.placeShip(-1, 0, ship));
    }

    @Test
    void receiveAttack_onWater_shouldReturnMiss() {
        AttackCellResult result = board.receiveAttack(5, 5);
        assertEquals("MISS", result.status());
        assertNull(result.shipType());
        assertEquals(Board.MISS, board.getGrid()[5][5]);
    }

    @Test
    void receiveAttack_onShip_shouldReturnHit() {
        Ship ship = new Ship(3, true, 0, false);
        ship.setShipType(ShipType.CRUISER);
        board.placeShip(0, 0, ship);

        AttackCellResult result = board.receiveAttack(0, 0);
        assertEquals("HIT", result.status());
        assertEquals(ShipType.CRUISER, result.shipType());
        assertEquals(Board.HIT, board.getGrid()[0][0]);
    }

    @Test
    void receiveAttack_onSameCell_shouldReturnAttacked() {
        board.receiveAttack(5, 5); // MISS
        AttackCellResult result = board.receiveAttack(5, 5);
        assertEquals("ATTACKED", result.status());
    }

    @Test
    void receiveAttack_sinkingShip_shouldReturnSunk() {
        Ship ship = new Ship(2, true, 0, false);
        ship.setShipType(ShipType.DESTROYER);
        board.placeShip(0, 0, ship);

        board.receiveAttack(0, 0); // HIT
        AttackCellResult result = board.receiveAttack(0, 1); // SUNK
        assertEquals("SUNK", result.status());
        assertEquals(ShipType.DESTROYER, result.shipType());
    }

    @Test
    void receiveAttack_onAlreadyHitCell_shouldReturnAttacked() {
        Ship ship = new Ship(3, true, 0, false);
        board.placeShip(0, 0, ship);

        board.receiveAttack(0, 0); // HIT
        AttackCellResult result = board.receiveAttack(0, 0);
        assertEquals("ATTACKED", result.status());
    }

    @Test
    void allShipsSunk_shouldReturnFalse_whenShipsRemain() {
        Ship ship = new Ship(2, true, 0, false);
        board.placeShip(0, 0, ship);
        assertFalse(board.allShipsSunk());
    }

    @Test
    void allShipsSunk_shouldReturnTrue_whenAllDestroyed() {
        Ship ship = new Ship(2, true, 0, false);
        board.placeShip(0, 0, ship);
        board.receiveAttack(0, 0);
        board.receiveAttack(0, 1);
        assertTrue(board.allShipsSunk());
    }

    @Test
    void clearBoard_shouldResetEverything() {
        Ship ship = new Ship(3, true, 0, false);
        board.placeShip(0, 0, ship);
        board.receiveAttack(0, 0);

        board.clearBoard();

        assertEquals(0, board.getShipCount());
        assertEquals(Board.WATER, board.getGrid()[0][0]);
    }

    @Test
    void getGridForPlayer_shouldHideShips() {
        Ship ship = new Ship(3, true, 0, false);
        board.placeShip(0, 0, ship);

        int[][] visible = board.getGridForPlayer();
        // Ships appear as water
        assertEquals(Board.WATER, visible[0][0]);
        assertEquals(Board.WATER, visible[0][1]);
        assertEquals(Board.WATER, visible[0][2]);
    }

    @Test
    void getGridForPlayer_shouldShowHitsAndMisses() {
        Ship ship = new Ship(3, true, 0, false);
        board.placeShip(0, 0, ship);
        board.receiveAttack(0, 0); // HIT
        board.receiveAttack(1, 0); // MISS

        int[][] visible = board.getGridForPlayer();
        assertEquals(Board.HIT, visible[0][0]);
        assertEquals(Board.MISS, visible[1][0]);
    }
}
