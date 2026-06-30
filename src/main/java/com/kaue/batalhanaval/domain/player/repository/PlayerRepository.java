package com.kaue.batalhanaval.domain.player.repository;

import com.kaue.batalhanaval.domain.player.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PlayerRepository extends JpaRepository<Player, UUID> {

    Optional<Player> findByEmail(String name);
    boolean existsByEmail (String email);

}
