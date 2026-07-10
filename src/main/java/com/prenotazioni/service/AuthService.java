package com.prenotazioni.service;

import com.prenotazioni.dto.AuthLoginRequest;
import com.prenotazioni.dto.AuthLoginResponse;
import com.prenotazioni.dto.AuthRegisterRequest;

public interface AuthService {

    AuthLoginResponse register(AuthRegisterRequest request);

    AuthLoginResponse login(AuthLoginRequest request);
}