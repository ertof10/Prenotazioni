package com.prenotazioni.controller;

import com.prenotazioni.dto.AuthLoginRequest;
import com.prenotazioni.dto.AuthLoginResponse;
import com.prenotazioni.dto.AuthRegisterRequest;
import com.prenotazioni.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Tag(
        name = "Autenticazione",
        description = "API per la gestione dell'autenticazione degli utenti"
)
@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthLoginResponse register(@Valid @RequestBody AuthRegisterRequest request) {

        log.info("Richiesta registrazione cliente | email={}", request.getEmail());

        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthLoginResponse login(@Valid @RequestBody AuthLoginRequest request) {

        log.info("Richiesta login account | email={}", request.getEmail());

        return authService.login(request);
    }
}