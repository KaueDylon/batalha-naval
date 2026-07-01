package com.kaue.batalhanaval.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.kaue.batalhanaval.commons.enums.Role;
import com.kaue.batalhanaval.domain.auth.entity.TokenRevoked;
import com.kaue.batalhanaval.domain.auth.repository.TokenRevokedRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-hours:2}")
    private int exp;

    private final TokenRevokedRepository tokenRevokedRepository;

    public JwtService(TokenRevokedRepository tokenRevokedRepository) {
        this.tokenRevokedRepository = tokenRevokedRepository;
    }

    public String createToken(UUID userId, String email){
        return JWT.create()
                .withIssuer("batalha-naval")
                .withSubject(userId.toString())
                .withClaim("email", email)
                .withClaim("role", Role.PLAYER.name())
                .withJWTId(UUID.randomUUID().toString())
                .withExpiresAt(expirationInstant())
                .sign(Algorithm.HMAC256(secret));
    }

    public DecodedJWT validateToken(String token){
        var decoded = JWT.require(Algorithm.HMAC256(secret))
                .withIssuer("batalha-naval")
                .build()
                .verify(token);

        if(tokenRevokedRepository.existsByTokenId(decoded.getId())){
            throw new RuntimeException("Token foi revogado/inválidado");
        }

        return decoded;

    }

    public void revokeToken(String token){
        var decoded = JWT.decode(token);
        var revoked = new TokenRevoked();
        revoked.setTokenId(decoded.getId());
        revoked.setExpiresAt(decoded.getExpiresAtAsInstant().atZone(ZoneOffset.UTC).toLocalDateTime());
        tokenRevokedRepository.save(revoked);
    }


    private Instant expirationInstant() {
        return Instant.now().plus(exp, ChronoUnit.HOURS);
    }

}

