package com.pharmatel.backend.controller;

import com.pharmatel.backend.dto.auth.AuthResponse;
import com.pharmatel.backend.dto.auth.LoginRequest;
import com.pharmatel.backend.dto.auth.RegisterRequest;
import com.pharmatel.backend.entity.Account;
import com.pharmatel.backend.service.AuthService;
import com.pharmatel.backend.service.EmailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {

    private final AuthService authService;
    
    
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Register account",
        description = "Registers a new patient or pharmacy account and returns JWT access token with role and profile IDs."
    )
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) throws MessagingException {
        log.info("Incoming register request username={} role={}", request.getUsername(), request.getRole());
        

        return authService.register(request);
    }

    @PostMapping("/login")
    @Operation(
        summary = "Login",
        description = "Authenticates user credentials and returns JWT access token, account data, and role."
    )
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        log.info("Incoming login request username={} role={}", request.getUsername(), request.getRole());
        

        return authService.login(request);
    }



    
}
