package com.kaue.batalhanaval.domain.game.entity;

import com.kaue.batalhanaval.domain.game.entity.Ship;

import java.util.ArrayList;
import java.util.List;

public class Board {
    public static final int WATER = 0;
    public static final int SHIP = 1;
    public static final int MISS = 2;
    public static final int HIT = 3;
    private int[][] grid;
    private Ship[][] shipGrid;
    private List<Ship> ships = new ArrayList<>();

    public Board(){
        grid = new int[10][10];
        shipGrid = new Ship[10][10];
    }

    public boolean placeShip(int row, int col, Ship ship){
        if (!isValidPosition(row,col,ship)) return false;
        for (int i = 0; i < ship.getSize(); i++){
            if (ship.getOrientation()){

                grid[row][col + i] = 1;
                shipGrid[row][col + i] = ship;
            }else{
                grid[row + i][col] = 1;
                shipGrid[row + i][col] = ship;
            }
        }
        ships.add(ship);
        return true;
    }

    public boolean allShipsSunk(){
        return ships.stream().allMatch(Ship::isSunk);
    }

    public boolean isValidPosition(int row, int col, Ship ship){
        for (int i = 0; i < ship.getSize(); i++){
            int r = ship.getOrientation() ? row : row + i;
            int c = ship.getOrientation() ? col + i : col;

            if (r >= 10 || c >= 10 || r < 0 || c < 0) return false;

            if (grid[r][c] == SHIP) return false;
        }
        return true;
    }

    public String receiveAttack(int row, int col){
        if (grid[row][col] == MISS || grid[row][col] == HIT){
            return "ATTACKED";
        }

        if (grid[row][col] == SHIP){
            grid[row][col] = HIT;
            Ship ship = shipGrid[row][col];
            ship.hit();

            if (ship.isSunk()) return "SUNK";
            return "HIT";
        }
        grid[row][col] = MISS;
        return "MISS";
    }

    public int[][] getGridForPlayer(){
        int[][] visible = new int[10][10];
        for (int r = 0; r < 10; r++){
            for (int c = 0; c < 10; c++) {
                visible[r][c] = grid[r][c] == SHIP ? WATER : grid[r][c];
            }
        }
        return visible;
    }

    public int[][] getGrid(){
        return grid;
    }
}
