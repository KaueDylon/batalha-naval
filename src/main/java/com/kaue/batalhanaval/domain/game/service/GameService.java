package com.kaue.batalhanaval.domain.game.service;

import com.kaue.batalhanaval.domain.game.dto.PlaceShipRequest;
import com.kaue.batalhanaval.domain.game.entity.Ship;
import com.kaue.batalhanaval.domain.game.Game;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class GameService {

    private final Map<String, Game> games = new HashMap<>();

    public String createGame(String playerAId, String playerBId) {
        String gameId = UUID.randomUUID().toString();
        games.put(gameId, new Game(playerAId, playerBId));
        return gameId;
    }

    public boolean placeShip(String gameId, String playerId, PlaceShipRequest req) {
        Game game = getGame(gameId);
        Ship ship = new Ship(req.size(), req.orientation(), 0, false);
        return game.placeShip(playerId, req.row(), req.col(), ship);
    }

    public String attack(String gameId, String attackerId, int row, int col){
        return getGame(gameId).processAttack(attackerId, row, col);
    }

    public int[][] getBoard(String gameId, String requesterId, String targetId){
        return getGame(gameId).getBoardView(requesterId, targetId);
    }

    private Game getGame(String gameId){
        Game game = games.get(gameId);
        if (game == null) throw new RuntimeException("Partida não foi encontrada.");
        return game;
    }
}
