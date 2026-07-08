package com.kaue.batalhanaval.domain.player.entity;

import com.kaue.batalhanaval.commons.enums.Nation;
import com.kaue.batalhanaval.commons.enums.NationPortrait;
import com.kaue.batalhanaval.commons.enums.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void hasNation_shouldReturnFalse_whenNationIsNull() {
        Player player = new Player();
        assertFalse(player.hasNation());
    }

    @Test
    void hasNation_shouldReturnTrue_whenNationIsSet() {
        Player player = new Player();
        player.setNation(Nation.USA);
        assertTrue(player.hasNation());
    }

    @Test
    void newPlayer_shouldHaveZeroWinsAndLosses() {
        Player player = new Player();
        assertEquals(0, player.getWins());
        assertEquals(0, player.getLosses());
    }

    @Test
    void winsAndLosses_shouldBeUpdatable() {
        Player player = new Player();
        player.setWins(5);
        player.setLosses(3);
        assertEquals(5, player.getWins());
        assertEquals(3, player.getLosses());
    }

    @Test
    void portrait_shouldBeSettable() {
        Player player = new Player();
        player.setNation(Nation.USA);
        player.setPortrait(NationPortrait.USA_ADMIRAL);
        assertEquals(NationPortrait.USA_ADMIRAL, player.getPortrait());
    }
}
