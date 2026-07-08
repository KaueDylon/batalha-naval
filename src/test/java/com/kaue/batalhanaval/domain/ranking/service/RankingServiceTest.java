package com.kaue.batalhanaval.domain.ranking.service;

import com.kaue.batalhanaval.commons.enums.Nation;
import com.kaue.batalhanaval.commons.enums.Role;
import com.kaue.batalhanaval.domain.player.entity.Player;
import com.kaue.batalhanaval.domain.player.repository.PlayerRepository;
import com.kaue.batalhanaval.domain.ranking.dto.RankingResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RankingServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private RankingService rankingService;

    private Player playerA;
    private Player playerB;
    private Player playerC;

    @BeforeEach
    void setUp() {
        playerA = createPlayer("Alice", 8, 2); // 80% winrate
        playerB = createPlayer("Bob", 6, 4);   // 60% winrate
        playerC = createPlayer("Carol", 2, 1);
    }

    @Test
    void getTopPlayers_shouldOnlyIncludePlayersWithMinMatches() {
        when(playerRepository.findAll()).thenReturn(List.of(playerA, playerB, playerC));

        List<RankingResponse> ranking = rankingService.getTopPlayers(10, 0);

        assertEquals(2, ranking.size());
        // playerC excluded (only 3 matches, minimum is 5)
    }

    @Test
    void getTopPlayers_shouldBeSortedByWinrateDescending() {
        when(playerRepository.findAll()).thenReturn(List.of(playerB, playerA));

        List<RankingResponse> ranking = rankingService.getTopPlayers(10, 0);

        assertEquals("Alice", ranking.get(0).name());
        assertEquals("Bob", ranking.get(1).name());
    }

    @Test
    void getTopPlayers_shouldRespectLimitAndOffset() {
        when(playerRepository.findAll()).thenReturn(List.of(playerA, playerB));

        List<RankingResponse> ranking = rankingService.getTopPlayers(1, 0);
        assertEquals(1, ranking.size());
        assertEquals("Alice", ranking.get(0).name());

        List<RankingResponse> ranking2 = rankingService.getTopPlayers(1, 1);
        assertEquals(1, ranking2.size());
        assertEquals("Bob", ranking2.get(0).name());
    }

    @Test
    void getTopPlayers_shouldCalculateWinrateCorrectly() {
        when(playerRepository.findAll()).thenReturn(List.of(playerA));

        List<RankingResponse> ranking = rankingService.getTopPlayers(10, 0);
        // 8 wins / 10 total = 80%
        assertEquals(80.0, ranking.get(0).winrate());
    }

    @Test
    void getPlayerPosition_shouldReturnCorrectPosition() {
        when(playerRepository.findAll()).thenReturn(List.of(playerA, playerB));

        int positionA = rankingService.getPlayerPosition(playerA.getId());
        int positionB = rankingService.getPlayerPosition(playerB.getId());

        assertEquals(1, positionA); // Highest winrate
        assertEquals(2, positionB);
    }

    @Test
    void getPlayerPosition_shouldReturnMinusOne_whenPlayerNotInRanking() {
        when(playerRepository.findAll()).thenReturn(List.of(playerA, playerB));

        // playerC is not in ranking because of insufficient matches
        int position = rankingService.getPlayerPosition(playerC.getId());
        assertEquals(-1, position);
    }

    @Test
    void getPlayerPosition_shouldReturnMinusOne_whenPlayerDoesNotExist() {
        when(playerRepository.findAll()).thenReturn(List.of(playerA, playerB));

        int position = rankingService.getPlayerPosition(UUID.randomUUID());
        assertEquals(-1, position);
    }

    @Test
    void getTopPlayers_shouldReturnEmptyList_whenNoPlayersQualify() {
        when(playerRepository.findAll()).thenReturn(List.of(playerC));

        List<RankingResponse> ranking = rankingService.getTopPlayers(10, 0);
        assertTrue(ranking.isEmpty());
    }

    @Test
    void getTopPlayers_shouldHandleZeroWinrate() {
        Player loser = createPlayer("Loser", 0, 5);
        when(playerRepository.findAll()).thenReturn(List.of(loser));

        List<RankingResponse> ranking = rankingService.getTopPlayers(10, 0);
        assertEquals(1, ranking.size());
        assertEquals(0.0, ranking.get(0).winrate());
    }

    // --- Helper ---

    private Player createPlayer(String name, int wins, int losses) {
        Player player = new Player();
        player.setId(UUID.randomUUID());
        player.setName(name);
        player.setEmail(name.toLowerCase() + "@tests.com");
        player.setPassword("encoded");
        player.setRolePlayer(Role.PLAYER);
        player.setWins(wins);
        player.setLosses(losses);
        return player;
    }
}
