package com.kaue.batalhanaval.domain.game;

import com.kaue.batalhanaval.commons.enums.Phase;
import com.kaue.batalhanaval.commons.enums.ShipType;
import com.kaue.batalhanaval.domain.game.dto.AttackResult;
import com.kaue.batalhanaval.domain.game.entity.Ship;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private static final String PLAYER_A = "player-a";
    private static final String PLAYER_B = "player-b";

    private Game game;

    @BeforeEach
    void setUp() {
        game = new Game(PLAYER_A, PLAYER_B);
    }

    @Test
    void newGame_shouldBeInSetupPhase() {
        assertEquals(Phase.SETUP, game.getPhase());
        assertFalse(game.isPlayerAReady());
        assertFalse(game.isPlayerBReady());
        assertNull(game.getWinner());
    }

    @Test
    void newGame_shouldHavePlayerAAsFirstTurn() {
        assertEquals(PLAYER_A, game.getCurrentTurn());
    }

    @Test
    void isParticipant_shouldReturnTrue_forValidPlayers() {
        assertTrue(game.isParticipant(PLAYER_A));
        assertTrue(game.isParticipant(PLAYER_B));
    }

    @Test
    void isParticipant_shouldReturnFalse_forStranger() {
        assertFalse(game.isParticipant("stranger"));
    }

    @Nested
    class PlaceShipTests {

        @Test
        void placeShip_shouldSucceed_duringSetup() {
            Ship ship = new Ship(3, true, 0, false);
            assertTrue(game.placeShip(PLAYER_A, 0, 0, ship));
        }

        @Test
        void placeShip_shouldFail_afterPlayerIsReady() {
            placeAllShipsForPlayer(PLAYER_A);
            game.setPlayerReady(PLAYER_A);

            Ship extra = new Ship(2, true, 0, false);
            assertFalse(game.placeShip(PLAYER_A, 9, 0, extra));
        }

        @Test
        void placeShip_shouldFail_afterGameStarted() {
            startGame();

            Ship ship = new Ship(2, true, 0, false);
            assertFalse(game.placeShip(PLAYER_A, 9, 0, ship));
        }
    }

    @Nested
    class SetPlayerReadyTests {

        @Test
        void setPlayerReady_shouldThrow_whenNotEnoughShips() {
            Ship ship = new Ship(3, true, 0, false);
            game.placeShip(PLAYER_A, 0, 0, ship);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> game.setPlayerReady(PLAYER_A));
            assertTrue(ex.getMessage().contains("navios"));
        }

        @Test
        void setPlayerReady_shouldThrow_whenPlayerNotInGame() {
            assertThrows(IllegalArgumentException.class,
                    () -> game.setPlayerReady("stranger"));
        }

        @Test
        void setPlayerReady_shouldReturnFalse_whenOnlyOneReady() {
            placeAllShipsForPlayer(PLAYER_A);
            assertFalse(game.setPlayerReady(PLAYER_A));
            assertEquals(Phase.SETUP, game.getPhase());
        }

        @Test
        void setPlayerReady_shouldStartGame_whenBothReady() {
            placeAllShipsForPlayer(PLAYER_A);
            game.setPlayerReady(PLAYER_A);

            placeAllShipsForPlayer(PLAYER_B);
            assertTrue(game.setPlayerReady(PLAYER_B));
            assertEquals(Phase.PLAYING, game.getPhase());
        }

        @Test
        void setPlayerReady_shouldFail_inPlayingPhase() {
            startGame();
            assertFalse(game.setPlayerReady(PLAYER_A));
        }
    }

    @Nested
    class ProcessAttackTests {

        @BeforeEach
        void startTheGame() {
            startGame();
        }

        @Test
        void attack_shouldReturnNotYourTurn_whenWrongPlayer() {
            AttackResult result = game.processAttack(PLAYER_B, 0, 0);
            assertEquals("NOT_YOUR_TURN", result.status());
            assertEquals(PLAYER_A, result.nextTurn());
        }

        @Test
        void attack_shouldReturnMiss_onWater() {
            AttackResult result = game.processAttack(PLAYER_A, 9, 9);
            assertEquals("MISS", result.status());
        }

        @Test
        void attack_miss_shouldSwitchTurn() {
            game.processAttack(PLAYER_A, 9, 9); // MISS on B's board
            assertEquals(PLAYER_B, game.getCurrentTurn());
        }

        @Test
        void attack_hit_shouldNotSwitchTurn() {
            // Player B has ship at row 0 col 0 (placed via placeAllShipsForPlayer)
            AttackResult result = game.processAttack(PLAYER_A, 0, 0);
            assertEquals("HIT", result.status());
            assertEquals(PLAYER_A, game.getCurrentTurn());
        }

        @Test
        void attack_shouldReturnInvalidPosition_forOutOfBounds() {
            AttackResult result = game.processAttack(PLAYER_A, -1, 0);
            assertEquals("INVALID_POSITION", result.status());

            AttackResult result2 = game.processAttack(PLAYER_A, 0, 10);
            assertEquals("INVALID_POSITION", result2.status());
        }

        @Test
        void attack_shouldReturnGameOver_whenAllShipsSunk() {
            // Sink all of player B's ships
            sinkAllShips(PLAYER_A, PLAYER_B);
            assertEquals(Phase.FINISHED, game.getPhase());
            assertEquals(PLAYER_A, game.getWinner());
        }

        @Test
        void attack_shouldReturnGameAlreadyFinished_afterGameOver() {
            sinkAllShips(PLAYER_A, PLAYER_B);

            AttackResult result = game.processAttack(PLAYER_A, 0, 0);
            assertEquals("GAME_ALREADY_FINISHED", result.status());
        }

        @Test
        void attack_shouldReturnSunk_whenShipIsDestroyed() {
            // Player B has a ship of size 2 at row 4, cols 0-1
            game.processAttack(PLAYER_A, 4, 0); // HIT
            AttackResult result = game.processAttack(PLAYER_A, 4, 1); // SUNK
            assertEquals("SUNK", result.status());
            assertNotNull(result.shipType());
        }
    }

    @Nested
    class AttackDuringSetupTests {

        @Test
        void attack_shouldReturnGameNotStarted_inSetupPhase() {
            AttackResult result = game.processAttack(PLAYER_A, 0, 0);
            assertEquals("GAME_NOT_STARTED", result.status());
        }
    }

    @Nested
    class ClearBoardTests {

        @Test
        void clearBoard_shouldWork_duringSetup() {
            Ship ship = new Ship(3, true, 0, false);
            game.placeShip(PLAYER_A, 0, 0, ship);

            game.clearPlayerBoard(PLAYER_A);
            // After clearing, player can place ships again from scratch
            Ship newShip = new Ship(3, true, 0, false);
            assertTrue(game.placeShip(PLAYER_A, 0, 0, newShip));
        }

        @Test
        void clearBoard_shouldThrow_whenPlayerIsReady() {
            placeAllShipsForPlayer(PLAYER_A);
            game.setPlayerReady(PLAYER_A);

            assertThrows(IllegalArgumentException.class,
                    () -> game.clearPlayerBoard(PLAYER_A));
        }

        @Test
        void clearBoard_shouldThrow_afterGameStarted() {
            startGame();
            assertThrows(IllegalArgumentException.class,
                    () -> game.clearPlayerBoard(PLAYER_A));
        }
    }

    @Nested
    class SurrenderTests {

        @Test
        void surrender_shouldMakeOpponentWinner() {
            String winnerId = game.surrender(PLAYER_A);
            assertEquals(PLAYER_B, winnerId);
            assertEquals(PLAYER_B, game.getWinner());
            assertEquals(Phase.FINISHED, game.getPhase());
        }

        @Test
        void surrender_shouldThrow_whenGameAlreadyFinished() {
            game.surrender(PLAYER_A);
            assertThrows(IllegalStateException.class,
                    () -> game.surrender(PLAYER_B));
        }

        @Test
        void surrender_shouldThrow_whenPlayerNotInGame() {
            assertThrows(IllegalArgumentException.class,
                    () -> game.surrender("stranger"));
        }

        @Test
        void surrender_shouldWork_duringSetup() {
            String winnerId = game.surrender(PLAYER_B);
            assertEquals(PLAYER_A, winnerId);
            assertEquals(Phase.FINISHED, game.getPhase());
        }

        @Test
        void surrender_shouldWork_duringPlaying() {
            startGame();
            String winnerId = game.surrender(PLAYER_A);
            assertEquals(PLAYER_B, winnerId);
        }
    }

    @Nested
    class BoardViewTests {

        @Test
        void getBoardView_ownBoard_shouldShowShips() {
            Ship ship = new Ship(3, true, 0, false);
            game.placeShip(PLAYER_A, 0, 0, ship);

            int[][] view = game.getBoardView(PLAYER_A, PLAYER_A);
            assertEquals(1, view[0][0]); // SHIP
        }

        @Test
        void getBoardView_opponentBoard_shouldHideShips() {
            Ship ship = new Ship(3, true, 0, false);
            game.placeShip(PLAYER_B, 0, 0, ship);

            int[][] view = game.getBoardView(PLAYER_A, PLAYER_B);
            assertEquals(0, view[0][0]); // WATER (hidden)
        }
    }

    // --- Helper methods ---

    private void placeAllShipsForPlayer(String playerId) {
        // 5 ships: sizes 5, 4, 3, 3, 2
        ShipType[] types = {ShipType.CARRIER, ShipType.BATTLESHIP, ShipType.CRUISER, ShipType.SUBMARINE, ShipType.DESTROYER};
        int[] sizes = {5, 4, 3, 3, 2};
        for (int i = 0; i < 5; i++) {
            Ship ship = new Ship(sizes[i], true, 0, false);
            ship.setShipType(types[i]);
            game.placeShip(playerId, i, 0, ship);
        }
    }

    private void startGame() {
        placeAllShipsForPlayer(PLAYER_A);
        game.setPlayerReady(PLAYER_A);
        placeAllShipsForPlayer(PLAYER_B);
        game.setPlayerReady(PLAYER_B);
    }

    /**
     * Sinks all ships of the defender by attacking all known ship positions.
     * Ships are placed at rows 0-4, horizontally starting at col 0.
     * Sizes: 5, 4, 3, 3, 2
     */
    private void sinkAllShips(String attacker, String defender) {
        int[] sizes = {5, 4, 3, 3, 2};
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < sizes[row]; col++) {
                AttackResult result = game.processAttack(attacker, row, col);
                // If it was a miss for some reason (shouldn't happen), 
                // the other player needs to attack back to give turn back
                if ("NOT_YOUR_TURN".equals(result.status())) {
                    // This means turn switched due to a miss - attack with other player on empty cell
                    game.processAttack(defender, 9, 9);
                    game.processAttack(attacker, row, col);
                }
            }
        }
    }
}
