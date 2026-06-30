package com.kaue.batalhanaval.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService){ this.jwtService = jwtService; }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain)
            throws ServletException, IOException {

        var header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")){
            var decoded = jwtService.validateToken(header.substring(7));
            var authorities = new ArrayList<SimpleGrantedAuthority>();

            String role = decoded.getClaim("role").asString();

            if(role != null){
                authorities.add(
                        new SimpleGrantedAuthority("ROLE_"+ role)
                );
            }

            var auth = new UsernamePasswordAuthenticationToken
                    (
                            UUID.fromString(decoded.getSubject()), null, authorities
                    );

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(req, res);
    }

}
