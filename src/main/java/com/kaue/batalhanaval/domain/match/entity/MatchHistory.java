package com.kaue.batalhanaval.domain.match.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "match_history")
@Getter
@Setter
@NoArgsConstructor
public class MatchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID winnerId;

    @Column(nullable = false)
    private UUID loserId;

    @Column(nullable = false)
    private LocalDateTime playedAt;

    public MatchHistory(UUID winnerId, UUID loserId) {
        this.winnerId = winnerId;
        this.loserId = loserId;
        this.playedAt = LocalDateTime.now();
    }

}
