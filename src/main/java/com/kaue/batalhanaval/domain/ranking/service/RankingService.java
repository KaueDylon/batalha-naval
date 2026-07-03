package com.kaue.batalhanaval.domain.ranking.service;

import com.kaue.batalhanaval.domain.player.entity.Player;
import com.kaue.batalhanaval.domain.player.repository.PlayerRepository;
import com.kaue.batalhanaval.domain.ranking.dto.RankingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RankingService {

    private static final int MIN_MATCHES = 5;
    private final PlayerRepository playerRepository;

    public List<RankingResponse> getTopPlayers(int limit, int offset){
        return playerRepository.findAll().stream()
                .filter(p -> (p.getWins() + p.getLosses() >= MIN_MATCHES))
                .map(this::toRanking)
                .sorted(Comparator.comparingDouble(RankingResponse::winrate).reversed())
                .skip(offset)
                .limit(limit)
                .toList();
    }

    public int getPlayerPosition(UUID playerId){
        List<RankingResponse> fullRanking = playerRepository.findAll().stream()
                .filter(p -> (p.getWins() + p.getLosses()) >= MIN_MATCHES)
                .map(this::toRanking)
                .sorted(Comparator.comparingDouble(RankingResponse::winrate).reversed())
                .toList();

        for (int i = 0; i < fullRanking.size(); i++){
            if (fullRanking.get(i).playerId().equals(playerId)){
                return i + 1;
            }
        }

        return -1;
    }

    private RankingResponse toRanking(Player player){
        int total = player.getWins() + player.getLosses();
        double winrate = total > 0 ? (double) player.getWins() / total * 100 : 0;

        return new RankingResponse(
                player.getId(),
                player.getName(),
                player.getNation() != null ? player.getNation().name() : null,
                player.getPortrait() != null ? player.getPortrait().getId() : null,
                player.getWins(),
                player.getLosses(),
                Math.round(winrate * 100.0) / 100.0
        );
    }

}
