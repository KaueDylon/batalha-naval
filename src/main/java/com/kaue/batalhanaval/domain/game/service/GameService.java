package com.kaue.batalhanaval.domain.game.service;

import com.kaue.batalhanaval.domain.game.dto.AttackResult;
import com.kaue.batalhanaval.domain.game.dto.GameStateResponse;
import com.kaue.batalhanaval.domain.game.dto.PlaceShipRequest;
import com.kaue.batalhanaval.domain.game.entity.Ship;
import com.kaue.batalhanaval.domain.game.Game;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {

    private final Map<String, Game> games = new ConcurrentHashMap<>();
    private final Map<String, String> playerToGame = new ConcurrentHashMap<>();

    public String createGame(String playerAId, String playerBId) {
        String gameId = UUID.randomUUID().toString();
        games.put(gameId, new Game(playerAId, playerBId));
        playerToGame.put(playerAId, gameId);
        playerToGame.put(playerBId, gameId);
        return gameId;
    }

    public boolean placeShip(String gameId, String playerId, PlaceShipRequest req) {
        Game game = getGame(gameId);
        Ship ship = new Ship(req.size(), req.orientation(), 0, false);
        return game.placeShip(playerId, req.row(), req.col(), ship);
    }

    public void clearBoard(String gameId, String playerId) {
        Game game = getGame(gameId);
        game.clearPlayerBoard(playerId);
    }

    public boolean playerReady(String gameId, String playerId) {
        Game game = getGame(gameId);
        return game.setPlayerReady(playerId);
    }

    public AttackResult attack(String gameId, String attackerId, int row, int col) {
        return getGame(gameId).processAttack(attackerId, row, col);
    }

    public int[][] getBoard(String gameId, String requesterId, String targetId) {
        return getGame(gameId).getBoardView(requesterId, targetId);
    }

    public GameStateResponse getGameState(String gameId, String requesterId) {
        Game game = getGame(gameId);

        if (!game.isParticipant(requesterId)) {
            throw new IllegalArgumentException("Você não participa dessa partida.");
        }

        return new GameStateResponse(
                gameId,
                game.getPhase().name(),
                game.getCurrentTurn(),
                game.getPlayerAId(),
                game.getPlayerBId(),
                game.isPlayerAReady(),
                game.isPlayerBReady(),
                game.getWinner()
        );
    }

    public String findGameIdByPlayer(String playerId) {
        return playerToGame.get(playerId);
    }

    public String getOpponentId(String gameId, String playerId) {
        Game game = games.get(gameId);
        if (game == null) return null;
        return playerId.equals(game.getPlayerAId()) ? game.getPlayerBId() : game.getPlayerAId();
    }

    public void removePlayerFromGame(String playerId) {
        playerToGame.remove(playerId);
    }

    public Game getGame(String gameId) {
        Game game = games.get(gameId);
        if (game == null) throw new IllegalArgumentException("Partida não foi encontrada.");
        return game;
    }
}
