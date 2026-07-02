package com.kaue.batalhanaval.domain.match.repository;

import com.kaue.batalhanaval.domain.match.entity.MatchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MatchHistoryRepository extends JpaRepository<MatchHistory, UUID> {
    List<MatchHistory> findByWinnerIdOrLoserIdOrderByPlayedAtDesc(UUID winnerId, UUID loserId);

}