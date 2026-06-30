package com.kaue.batalhanaval.domain.auth.service;

import com.kaue.batalhanaval.commons.enums.Role;
import com.kaue.batalhanaval.domain.auth.dto.AuthLoginRequest;
import com.kaue.batalhanaval.domain.auth.dto.AuthRegisterRequest;
import com.kaue.batalhanaval.domain.auth.dto.AuthTokenResponse;
import com.kaue.batalhanaval.domain.player.entity.Player;
import com.kaue.batalhanaval.domain.player.repository.PlayerRepository;
import com.kaue.batalhanaval.infra.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthService {

    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(PlayerRepository userAuthRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.playerRepository = userAuthRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthTokenResponse register(AuthRegisterRequest req){
        if(playerRepository.existsByEmail(req.email())){
            throw new IllegalArgumentException("Esse e-mail já foi cadastrado.");
        }

        var player = new Player();
        player.setName(req.name());
        player.setEmail(req.email());
        player.setRolePlayer(Role.PLAYER);
        player.setPassword(passwordEncoder.encode(req.password()));
        playerRepository.save(player);

        return new AuthTokenResponse(jwtService.createToken(player.getId(), player.getEmail()));
    }

    @Transactional
    public AuthTokenResponse login(AuthLoginRequest req){

        var player = playerRepository.findByEmail(req.email())
                .orElseThrow( () -> new IllegalArgumentException("Email incorreto."));

        if (!passwordEncoder.matches(req.password(), player.getPassword())){
            throw new IllegalArgumentException("Senha incorreta.");
        }

        return new AuthTokenResponse(jwtService.createToken(player.getId(), player.getEmail()));
    }

    @Transactional
    public void delete(UUID userId){
        var user = playerRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
        playerRepository.delete(user);
    }

}

