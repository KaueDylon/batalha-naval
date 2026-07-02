package com.kaue.batalhanaval.domain.game;

import com.kaue.batalhanaval.commons.enums.Phase;
import com.kaue.batalhanaval.domain.game.dto.AttackResult;
import com.kaue.batalhanaval.domain.game.entity.Board;
import com.kaue.batalhanaval.domain.game.entity.Ship;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Game {

    private final Board playerABoard = new Board();
    private final Board playerBBoard = new Board();
    private String currentTurn;
    private String winner;
    private Phase phase;
    private String playerAId;
    private String playerBId;
    private boolean playerAReady;
    private boolean playerBReady;

    public Game(String playerAId, String playerBId){
        this.playerAId = playerAId;
        this.playerBId = playerBId;
        this.currentTurn = playerAId;
        this.phase = Phase.SETUP;
        this.playerAReady = false;
        this.playerBReady = false;
    }

    public synchronized boolean placeShip(String playerId, int row, int col, Ship ship){
        if (phase != Phase.SETUP) return false;
        if (isPlayerReady(playerId)) return false;
        Board board = getOwnBoard(playerId);
        return board.placeShip(row, col, ship);
    }

    public synchronized boolean setPlayerReady(String playerId){
        if (phase != Phase.SETUP) return false;

        if (!isParticipant(playerId)){
            throw new IllegalArgumentException("Jogador não participa dessa partida.");
        }

        Board board = getOwnBoard(playerId);
        if (board.getShipCount() < 5 ){
            throw new IllegalArgumentException("Posicione todos os navios antes de ficar pronto.");
        }

        if(playerId.equals(playerAId)){
            playerAReady = true;
        } else {
            playerBReady = true;
        }

        if (playerAReady && playerBReady){
            phase = Phase.PLAYING;
            return true;
        }
        return false;
    }


    public synchronized AttackResult processAttack(String attackerId, int row, int col){

        if (phase == Phase.SETUP) return new AttackResult("GAME_NOT_STARTED", null);

        if (phase == Phase.FINISHED) return new AttackResult("GAME_ALREADY_FINISHED", winner);

        if (!currentTurn.equals(attackerId)) return new AttackResult("NOT_YOUR_TURN", currentTurn);

        if (row < 0 || row >= 10 || col < 0 || col >= 10) return new AttackResult("INVALID_POSITION", currentTurn);

        Board targetBoard = attackerId.equals(playerAId) ? playerBBoard : playerABoard;
        String result = targetBoard.receiveAttack(row, col);

        if (result.equals("SUNK") && targetBoard.allShipsSunk()){
            winner = attackerId;
            phase = Phase.FINISHED;
            return new AttackResult("GAME_OVER", attackerId);
        }

        if (result.equals("MISS")) switchTurn();

        return new AttackResult(result, currentTurn);
    }

    public synchronized void clearPlayerBoard(String playerId){
        if (phase != Phase.SETUP) {
            throw new IllegalArgumentException("Tabuleiro é limpo apenas na fase de setup");
        }
        if (isPlayerReady(playerId)){
            throw new IllegalArgumentException("Você já confirmou prontidão.");
        }
        Board board = getOwnBoard(playerId);
        board.clearBoard();
    }

    public int[][] getBoardView(String requestId, String targetId){
        Board target = getOwnBoard(targetId);

        if (!requestId.equals(targetId)) return target.getGridForPlayer();
        return target.getGrid();
    }

    public boolean isParticipant(String playerId){
        return playerId.equals(playerAId) || playerId.equals(playerBId);
    }

    public boolean isPlayerReady(String playerId){
        if (playerId.equals(playerAId)) return playerAReady;
        if (playerId.equals(playerBId)) return playerBReady;
        return false;
    }

    private void switchTurn(){
        currentTurn = currentTurn.equals(playerAId) ? playerBId : playerAId;
    }

    private Board getOwnBoard(String playerId){
        return playerId.equals(playerAId) ? playerABoard : playerBBoard;
    }

    public String getWinner() { return winner; }
    public String getCurrentTurn() { return currentTurn; }
    public String getPlayerAId() { return  playerAId; }
    public String getPlayerBId() { return playerBId; }


}
