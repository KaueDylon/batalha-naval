package com.kaue.batalhanaval.domain.player.service;

import com.kaue.batalhanaval.commons.enums.Nation;
import com.kaue.batalhanaval.commons.enums.NationPortrait;
import com.kaue.batalhanaval.domain.player.dto.PlayerProfileResponse;
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

    public PlayerProfileResponse findProfileById(UUID id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Jogador não encontrado."));
        return toProfileResponse(player);
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
    public PlayerResponse setNation(UUID id, Nation nation){
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Jogador não encontrado"));

        if (player.hasNation()){
            throw new IllegalArgumentException("Nação já foi escolhida e não pode ser alterada");
        }

        player.setNation(nation);
        playerRepository.save(player);
        return toResponse(player);
    }

    @Transactional
    public PlayerResponse setPortrait(UUID id, NationPortrait portrait){
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Jogador não encontrado"));

        if (player.getNation() == null){
            throw new IllegalArgumentException("Escolha uma nação antes de selecionar um portrait.");
        }

        if (portrait.getNation() != player.getNation()){
            throw new IllegalArgumentException("Este portrait não pertence à sua nação.");
        }

        player.setPortrait(portrait);
        playerRepository.save(player);
        return toResponse(player);
    }

    @Transactional
    public void recordWin(UUID playerId){
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Jogador não encontrado"));
        player.setWins(player.getWins() + 1);
        playerRepository.save(player);
    }

    @Transactional
    public void recordLoss(UUID playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Jogador não encontrado."));
        player.setLosses(player.getLosses() + 1);
        playerRepository.save(player);
    }

    @Transactional
    public void delete(UUID id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Jogador não encontrado."));
        playerRepository.delete(player);
    }

    private PlayerResponse toResponse(Player player) {
        return new PlayerResponse(player.getId(), player.getName(), player.getEmail(),
                player.getNation() != null ? player.getNation().name() : null,
                player.getPortrait() != null ? player.getPortrait().getId() : null,
                player.getWins(), player.getLosses());
    }

    private PlayerProfileResponse toProfileResponse(Player player) {
        return new PlayerProfileResponse(
                player.getId(),
                player.getName(),
                player.getNation() != null ? player.getNation().name() : null,
                player.getPortrait() != null ? player.getPortrait().getId() : null
        );
    }
}
