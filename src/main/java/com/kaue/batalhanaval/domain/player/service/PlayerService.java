package com.kaue.batalhanaval.domain.player.service;

import com.kaue.batalhanaval.domain.player.dto.PlayerResponse;
import com.kaue.batalhanaval.domain.player.dto.PlayerUpdateRequest;
import com.kaue.batalhanaval.domain.player.entity.Player;
import com.kaue.batalhanaval.domain.player.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;

    public PlayerResponse findById(UUID id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Jogador não encontrado."));
        return toResponse(player);
    }

    @Transactional
    public PlayerResponse update(UUID id, PlayerUpdateRequest req) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Jogador não encontrado."));

        if (req.name() != null && !req.name().isBlank()) {
            player.setName(req.name());
        }

        if (req.password() != null && !req.password().isBlank()) {
            player.setPassword(passwordEncoder.encode(req.password()));
        }

        playerRepository.save(player);
        return toResponse(player);
    }

    @Transactional
    public void delete(UUID id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Jogador não encontrado."));
        playerRepository.delete(player);
    }

    private PlayerResponse toResponse(Player player) {
        return new PlayerResponse(player.getId(), player.getName(), player.getEmail());
    }
}
