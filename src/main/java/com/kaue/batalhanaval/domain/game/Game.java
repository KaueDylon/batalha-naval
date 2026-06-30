package com.kaue.batalhanaval.domain.game;

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
    private String playerAId;
    private String playerBId;

    public Game(String playerAId, String playerBId){
        this.playerAId = playerAId;
        this.playerBId = playerBId;
        this.currentTurn = playerAId;
    }

    public boolean placeShip(String playerId, int row, int col, Ship ship){
        Board board = getOwnBoard(playerId);
        return board.placeShip(row, col, ship);
    }

    public String processAttack(String attackerId, int row, int col){

        if (!currentTurn.equals(attackerId)) return "NOT_YOUR_TURN";

        Board targetBoard = attackerId.equals(playerAId) ? playerBBoard : playerABoard;
        String result = targetBoard.receiveAttack(row, col);

        if(result.equals("SUNK") && targetBoard.allShipsSunk()){
            winner = attackerId;
            return "GAME_OVER";
        }

        if (result.equals("MISS")) switchTurn();

        return result;
    }

    public int[][] getBoardView(String requestId, String targetId){
        Board target = getOwnBoard(targetId);

        if (!requestId.equals(targetId)) return target.getGridForPlayer();
        return target.getGrid();
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
