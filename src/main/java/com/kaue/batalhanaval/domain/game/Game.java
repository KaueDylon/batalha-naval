package com.kaue.batalhanaval.domain.game;

import com.kaue.batalhanaval.commons.enums.Phase;
import com.kaue.batalhanaval.domain.game.dto.AttackCellResult;
import com.kaue.batalhanaval.domain.game.dto.AttackResult;
import com.kaue.batalhanaval.domain.game.dto.BoardCellResponse;
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

        if (phase == Phase.SETUP) return new AttackResult("GAME_NOT_STARTED", null, null);

        if (phase == Phase.FINISHED) return new AttackResult("GAME_ALREADY_FINISHED", winner, null);

        if (!currentTurn.equals(attackerId)) return new AttackResult("NOT_YOUR_TURN", currentTurn, null);

        if (row < 0 || row >= 10 || col < 0 || col >= 10) return new AttackResult("INVALID_POSITION", currentTurn, null);

        Board targetBoard = attackerId.equals(playerAId) ? playerBBoard : playerABoard;
        AttackCellResult cellResult = targetBoard.receiveAttack(row, col);
        String result = cellResult.status();
        String shipTypeName = cellResult.shipType() != null ? cellResult.shipType().name() : null;

        if (result.equals("SUNK") && targetBoard.allShipsSunk()){
            winner = attackerId;
            phase = Phase.FINISHED;
            return new AttackResult("GAME_OVER", attackerId, shipTypeName);
        }

        if (result.equals("MISS")) switchTurn();

        return new AttackResult(result, currentTurn, shipTypeName);
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

    public synchronized String surrender(String playerId){
        if (phase == Phase.FINISHED) {
            throw new IllegalStateException("A partida já foi finalizada.");
        }
        if (!isParticipant(playerId)){
            throw new IllegalArgumentException("Jogador não participa dessa partida.");
        }

        String opponentId = playerId.equals(playerAId) ? playerBId : playerAId;
        this.winner = opponentId;
        this.phase = Phase.FINISHED;
        return opponentId;
    }

    public int[][] getBoardView(String requestId, String targetId){
        Board target = getOwnBoard(targetId);

        if (!requestId.equals(targetId)) return target.getGridForPlayer();
        return target.getGrid();
    }

    public BoardCellResponse[][] getBoardViewDetailed(String requestId, String targetId){
        Board target = getOwnBoard(targetId);
        int[][] grid = requestId.equals(targetId) ? target.getGrid() : target.getGridForPlayer();
        Ship[][] shipGrid = target.getShipGrid();

        BoardCellResponse[][] result = new BoardCellResponse[10][10];
        for (int r = 0; r < 10; r++){
            for (int c = 0; c < 10; c++){
                String shipType = null;
                // Mostrar tipo do navio se: é o próprio tabuleiro (vê tudo)
                // ou se a célula foi acertada (HIT=3) no tabuleiro do oponente
                if (requestId.equals(targetId) && grid[r][c] == Board.SHIP && shipGrid[r][c] != null) {
                    shipType = shipGrid[r][c].getShipType() != null ? shipGrid[r][c].getShipType().name() : null;
                } else if (grid[r][c] == Board.HIT && shipGrid[r][c] != null) {
                    shipType = shipGrid[r][c].getShipType() != null ? shipGrid[r][c].getShipType().name() : null;
                }
                result[r][c] = new BoardCellResponse(grid[r][c], shipType);
            }
        }
        return result;
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
