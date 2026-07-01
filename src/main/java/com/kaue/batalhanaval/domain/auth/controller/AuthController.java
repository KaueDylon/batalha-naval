package com.kaue.batalhanaval.domain.auth.controller;

import com.kaue.batalhanaval.domain.auth.dto.AuthLoginRequest;
import com.kaue.batalhanaval.domain.auth.dto.AuthRegisterRequest;
import com.kaue.batalhanaval.domain.auth.dto.AuthTokenResponse;
import com.kaue.batalhanaval.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthTokenResponse> register(@RequestBody @Valid AuthRegisterRequest req){
        return ResponseEntity.ok(authService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthTokenResponse> login(@RequestBody @Valid AuthLoginRequest req){
        return ResponseEntity.ok(authService.login(req));
    }
}
