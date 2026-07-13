package com.kaue.batalhanaval.domain.match.service;

import com.kaue.batalhanaval.domain.match.dto.MatchHistoryResponse;
import com.kaue.batalhanaval.domain.match.entity.MatchHistory;
import com.kaue.batalhanaval.domain.match.repository.MatchHistoryRepository;
import com.kaue.batalhanaval.domain.player.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MatchHistoryService {

    private final MatchHistoryRepository matchHistoryRepository;
    private final PlayerService playerService;

    @Transactional
    public void recordMatch(UUID winnerId, UUID loserId) {
        matchHistoryRepository.save(new MatchHistory(winnerId, loserId));
        playerService.recordWin(winnerId);
        playerService.recordLoss(loserId);
    }

    public List<MatchHistory> getPlayerHistory(UUID playerId) {
        return matchHistoryRepository.findByWinnerIdOrLoserIdOrderByPlayedAtDesc(playerId, playerId);
    }

    public List<MatchHistoryResponse> getPlayerHistoryResponse(UUID playerId, int limit, int offset){
        return matchHistoryRepository
                .findByWinnerIdOrLoserIdOrderByPlayedAtDesc(playerId, playerId)
                .stream()
                .skip(offset)
                .limit(limit)
                .map(match -> new MatchHistoryResponse(
                        match.getId(),
                        match.getWinnerId(),
                        match.getLoserId(),
                        match.getWinnerId().equals(playerId),
                        match.getPlayedAt()
                ))
                .toList();
    }
}
