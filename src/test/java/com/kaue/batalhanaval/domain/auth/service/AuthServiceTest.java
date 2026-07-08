package com.kaue.batalhanaval.domain.auth.service;

import com.kaue.batalhanaval.commons.enums.Role;
import com.kaue.batalhanaval.domain.auth.dto.AuthLoginRequest;
import com.kaue.batalhanaval.domain.auth.dto.AuthRegisterRequest;
import com.kaue.batalhanaval.domain.auth.dto.AuthTokenResponse;
import com.kaue.batalhanaval.domain.player.entity.Player;
import com.kaue.batalhanaval.domain.player.repository.PlayerRepository;
import com.kaue.batalhanaval.infra.security.JwtService;
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
class AuthServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private Player existingPlayer;

    @BeforeEach
    void setUp() {
        existingPlayer = new Player();
        existingPlayer.setId(UUID.randomUUID());
        existingPlayer.setName("Kaue");
        existingPlayer.setEmail("kaue@test.com");
        existingPlayer.setPassword("encodedPassword");
        existingPlayer.setRolePlayer(Role.PLAYER);
    }

    @Test
    void register_shouldSucceed_whenEmailIsNew() {
        when(playerRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encodedPass");
        when(playerRepository.save(any(Player.class))).thenAnswer(inv -> {
            Player p = inv.getArgument(0);
            p.setId(UUID.randomUUID());
            return p;
        });
        when(jwtService.createToken(any(), eq("new@test.com"))).thenReturn("jwt-token");

        AuthRegisterRequest req = new AuthRegisterRequest("New Player", "new@test.com", "pass123");
        AuthTokenResponse response = authService.register(req);

        assertNotNull(response);
        assertEquals("jwt-token", response.tokenJWT());
        verify(playerRepository).save(any(Player.class));
    }

    @Test
    void register_shouldThrow_whenEmailAlreadyExists() {
        when(playerRepository.existsByEmail("kaue@test.com")).thenReturn(true);

        AuthRegisterRequest req = new AuthRegisterRequest("Kaue", "kaue@test.com", "pass");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> authService.register(req));
        assertTrue(ex.getMessage().contains("e-mail"));
        verify(playerRepository, never()).save(any());
    }

    @Test
    void login_shouldSucceed_withCorrectCredentials() {
        when(playerRepository.findByEmail("kaue@test.com")).thenReturn(Optional.of(existingPlayer));
        when(passwordEncoder.matches("correctPass", "encodedPassword")).thenReturn(true);
        when(jwtService.createToken(existingPlayer.getId(), "kaue@test.com")).thenReturn("jwt-token");

        AuthLoginRequest req = new AuthLoginRequest("kaue@test.com", "correctPass");
        AuthTokenResponse response = authService.login(req);

        assertEquals("jwt-token", response.tokenJWT());
    }

    @Test
    void login_shouldThrow_whenEmailNotFound() {
        when(playerRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        AuthLoginRequest req = new AuthLoginRequest("unknown@test.com", "pass");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> authService.login(req));
        assertTrue(ex.getMessage().contains("Email"));
    }

    @Test
    void login_shouldThrow_whenPasswordIsWrong() {
        when(playerRepository.findByEmail("kaue@test.com")).thenReturn(Optional.of(existingPlayer));
        when(passwordEncoder.matches("wrongPass", "encodedPassword")).thenReturn(false);

        AuthLoginRequest req = new AuthLoginRequest("kaue@test.com", "wrongPass");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> authService.login(req));
        assertTrue(ex.getMessage().contains("Senha"));
    }

    @Test
    void logout_shouldRevokeToken() {
        String token = "some-jwt-token";

        authService.logout(token);

        verify(jwtService).revokeToken(token);
    }
}
