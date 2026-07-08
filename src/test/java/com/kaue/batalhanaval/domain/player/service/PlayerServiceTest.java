package com.kaue.batalhanaval.domain.player.service;

import com.kaue.batalhanaval.commons.enums.Nation;
import com.kaue.batalhanaval.commons.enums.NationPortrait;
import com.kaue.batalhanaval.commons.enums.Role;
import com.kaue.batalhanaval.domain.player.dto.PlayerResponse;
import com.kaue.batalhanaval.domain.player.dto.PlayerUpdateRequest;
import com.kaue.batalhanaval.domain.player.entity.Player;
import com.kaue.batalhanaval.domain.player.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PlayerService playerService;

    private Player player;
    private UUID playerId;

    @BeforeEach
    void setUp() {
        playerId = UUID.randomUUID();
        player = new Player();
        player.setId(playerId);
        player.setName("Kaue");
        player.setEmail("kaue@test.com");
        player.setPassword("encoded");
        player.setRolePlayer(Role.PLAYER);
        player.setWins(0);
        player.setLosses(0);
    }

    @Test
    void findById_shouldReturnPlayerResponse() {
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));

        PlayerResponse response = playerService.findById(playerId);

        assertEquals(playerId, response.id());
        assertEquals("Kaue", response.name());
        assertEquals("kaue@test.com", response.email());
    }

    @Test
    void findById_shouldThrow_whenPlayerNotFound() {
        when(playerRepository.findById(playerId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> playerService.findById(playerId));
    }

    @Test
    void update_shouldUpdateName_whenProvided() {
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));
        when(playerRepository.save(any())).thenReturn(player);

        PlayerUpdateRequest req = new PlayerUpdateRequest("NewName", null);
        playerService.update(playerId, req);

        assertEquals("NewName", player.getName());
        verify(playerRepository).save(player);
    }

    @Test
    void update_shouldUpdatePassword_whenProvided() {
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));
        when(playerRepository.save(any())).thenReturn(player);
        when(passwordEncoder.encode("newpass")).thenReturn("encodedNewPass");

        PlayerUpdateRequest req = new PlayerUpdateRequest(null, "newpass");
        playerService.update(playerId, req);

        assertEquals("encodedNewPass", player.getPassword());
        verify(passwordEncoder).encode("newpass");
    }

    @Test
    void update_shouldNotUpdateName_whenBlank() {
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));
        when(playerRepository.save(any())).thenReturn(player);

        PlayerUpdateRequest req = new PlayerUpdateRequest("  ", null);
        playerService.update(playerId, req);

        assertEquals("Kaue", player.getName()); // unchanged
    }

    @Test
    void setNation_shouldSucceed_whenPlayerHasNoNation() {
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));
        when(playerRepository.save(any())).thenReturn(player);

        playerService.setNation(playerId, Nation.UK);

        assertEquals(Nation.UK, player.getNation());
    }

    @Test
    void setNation_shouldThrow_whenPlayerAlreadyHasNation() {
        player.setNation(Nation.USA);
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));

        assertThrows(IllegalArgumentException.class,
                () -> playerService.setNation(playerId, Nation.UK));
    }

    @Test
    void setPortrait_shouldSucceed_whenNationMatchesPortrait() {
        player.setNation(Nation.USA);
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));
        when(playerRepository.save(any())).thenReturn(player);

        playerService.setPortrait(playerId, NationPortrait.USA_ADMIRAL);

        assertEquals(NationPortrait.USA_ADMIRAL, player.getPortrait());
    }

    @Test
    void setPortrait_shouldThrow_whenNoNation() {
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));

        assertThrows(IllegalArgumentException.class,
                () -> playerService.setPortrait(playerId, NationPortrait.USA_ADMIRAL));
    }

    @Test
    void setPortrait_shouldThrow_whenPortraitNationMismatch() {
        player.setNation(Nation.USA);
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));

        assertThrows(IllegalArgumentException.class,
                () -> playerService.setPortrait(playerId, NationPortrait.UK_GENERAL));
    }

    @Test
    void recordWin_shouldIncrementWins() {
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));
        when(playerRepository.save(any())).thenReturn(player);

        playerService.recordWin(playerId);

        assertEquals(1, player.getWins());
        verify(playerRepository).save(player);
    }

    @Test
    void recordLoss_shouldIncrementLosses() {
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));
        when(playerRepository.save(any())).thenReturn(player);

        playerService.recordLoss(playerId);

        assertEquals(1, player.getLosses());
        verify(playerRepository).save(player);
    }

    @Test
    void delete_shouldRemovePlayer() {
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));

        playerService.delete(playerId);

        verify(playerRepository).delete(player);
    }

    @Test
    void delete_shouldThrow_whenPlayerNotFound() {
        when(playerRepository.findById(playerId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> playerService.delete(playerId));
    }
}
