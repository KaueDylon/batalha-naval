package com.kaue.batalhanaval.domain.auth.repository;

import com.kaue.batalhanaval.domain.auth.entity.TokenRevoked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TokenRevokedRepository extends JpaRepository<TokenRevoked, UUID> {

    boolean existsByTokenId(String tokenId);

}