package com.kaue.batalhanaval.domain.game.service;

import com.kaue.batalhanaval.commons.enums.ShipType;
import com.kaue.batalhanaval.domain.game.Game;
import com.kaue.batalhanaval.domain.game.dto.AttackResult;
import com.kaue.batalhanaval.domain.game.dto.PlaceShipRequest;
import com.kaue.batalhanaval.domain.game.dto.GameStateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    private GameService gameService;
    private String gameId;

    private static final String PLAYER_A = "player-a";
    private static final String PLAYER_B = "player-b";

    @BeforeEach
    void setUp() {
        gameService = new GameService();
        gameId = gameService.createGame(PLAYER_A, PLAYER_B);
    }

    @Test
    void createGame_shouldReturnGameId() {
        assertNotNull(gameId);
    }

    @Test
    void createGame_shouldAllowFindingByPlayer() {
        assertEquals(gameId, gameService.findGameIdByPlayer(PLAYER_A));
        assertEquals(gameId, gameService.findGameIdByPlayer(PLAYER_B));
    }

    @Test
    void getGame_shouldThrow_whenGameNotFound() {
        assertThrows(IllegalArgumentException.class,
                () -> gameService.getGame("nonexistent-id"));
    }

    @Test
    void placeShip_shouldSucceed_withValidRequest() {
        PlaceShipRequest req = new PlaceShipRequest(0, 0, 3, true, ShipType.CRUISER);
        assertTrue(gameService.placeShip(gameId, PLAYER_A, req));
    }

    @Test
    void placeShip_shouldFail_withInvalidPosition() {
        PlaceShipRequest req = new PlaceShipRequest(0, 9, 5, true, ShipType.CARRIER);
        assertFalse(gameService.placeShip(gameId, PLAYER_A, req));
    }

    @Test
    void clearBoard_shouldAllowReplacing() {
        PlaceShipRequest req = new PlaceShipRequest(0, 0, 3, true, ShipType.CRUISER);
        gameService.placeShip(gameId, PLAYER_A, req);
        gameService.clearBoard(gameId, PLAYER_A);

        // Can place ships again
        assertTrue(gameService.placeShip(gameId, PLAYER_A, req));
    }

    @Test
    void playerReady_shouldReturnFalse_whenOnlyOneReady() {
        placeAllShips(PLAYER_A);
        assertFalse(gameService.playerReady(gameId, PLAYER_A));
    }

    @Test
    void playerReady_shouldReturnTrue_whenBothReady() {
        placeAllShips(PLAYER_A);
        gameService.playerReady(gameId, PLAYER_A);

        placeAllShips(PLAYER_B);
        assertTrue(gameService.playerReady(gameId, PLAYER_B));
    }

    @Test
    void attack_shouldWork_afterGameStarted() {
        startGame();
        AttackResult result = gameService.attack(gameId, PLAYER_A, 9, 9);
        assertNotNull(result);
    }

    @Test
    void getGameState_shouldReturnCorrectInfo() {
        GameStateResponse state = gameService.getGameState(gameId, PLAYER_A);
        assertEquals(gameId, state.gameId());
        assertEquals("SETUP", state.phase());
        assertEquals(PLAYER_A, state.currentTurn());
        assertEquals(PLAYER_A, state.playerAId());
        assertEquals(PLAYER_B, state.playerBid());
    }

    @Test
    void getGameState_shouldThrow_whenPlayerNotParticipant() {
        assertThrows(IllegalArgumentException.class,
                () -> gameService.getGameState(gameId, "stranger"));
    }

    @Test
    void surrender_shouldReturnWinnerId() {
        String winnerId = gameService.surrender(gameId, PLAYER_A);
        assertEquals(PLAYER_B, winnerId);
    }

    @Test
    void surrender_shouldRemovePlayersFromMapping() {
        gameService.surrender(gameId, PLAYER_A);
        assertNull(gameService.findGameIdByPlayer(PLAYER_A));
        assertNull(gameService.findGameIdByPlayer(PLAYER_B));
    }

    @Test
    void getOpponentId_shouldReturnCorrectOpponent() {
        assertEquals(PLAYER_B, gameService.getOpponentId(gameId, PLAYER_A));
        assertEquals(PLAYER_A, gameService.getOpponentId(gameId, PLAYER_B));
    }

    @Test
    void getOpponentId_shouldReturnNull_whenGameNotFound() {
        assertNull(gameService.getOpponentId("nonexistent", PLAYER_A));
    }

    @Test
    void removeGame_shouldDeleteGameAndMappings() {
        gameService.removeGame(gameId);
        assertNull(gameService.findGameIdByPlayer(PLAYER_A));
        assertNull(gameService.findGameIdByPlayer(PLAYER_B));
        assertThrows(IllegalArgumentException.class,
                () -> gameService.getGame(gameId));
    }

    @Test
    void removePlayerFromGame_shouldOnlyAffectOnePlayer() {
        gameService.removePlayerFromGame(PLAYER_A);
        assertNull(gameService.findGameIdByPlayer(PLAYER_A));
        assertEquals(gameId, gameService.findGameIdByPlayer(PLAYER_B));
    }

    // --- Helpers ---

    private void placeAllShips(String playerId) {
        ShipType[] types = {ShipType.CARRIER, ShipType.BATTLESHIP, ShipType.CRUISER, ShipType.SUBMARINE, ShipType.DESTROYER};
        int[] sizes = {5, 4, 3, 3, 2};
        for (int i = 0; i < 5; i++) {
            PlaceShipRequest req = new PlaceShipRequest(i, 0, sizes[i], true, types[i]);
            gameService.placeShip(gameId, playerId, req);
        }
    }

    private void startGame() {
        placeAllShips(PLAYER_A);
        gameService.playerReady(gameId, PLAYER_A);
        placeAllShips(PLAYER_B);
        gameService.playerReady(gameId, PLAYER_B);
    }
}
