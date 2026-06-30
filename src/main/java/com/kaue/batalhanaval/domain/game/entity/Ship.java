package com.kaue.batalhanaval.domain.game.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Ship {
    private int size;
    private Boolean orientation;
    private int hits = 0;
    private Boolean sunk = false;

    public int hit(){
        return this.hits = this.hits +1;
    }

    public Boolean isSunk(){
        return getHits() == getSize();
    }

}
