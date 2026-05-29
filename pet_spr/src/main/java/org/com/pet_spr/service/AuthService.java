package org.com.pet_spr.service;

import jakarta.servlet.http.HttpServletRequest;
import org.com.pet_spr.domain.dto.request.auth.LoginRequestDto;
import org.com.pet_spr.domain.dto.request.auth.RegisterRequestDto;
import org.com.pet_spr.domain.dto.response.CommonResponseDto;
import org.com.pet_spr.domain.dto.response.auth.LoginResult;
import org.com.pet_spr.domain.dto.response.auth.RegisterResponseDto;

public interface AuthService {
    LoginResult login(LoginRequestDto loginRequestDto, HttpServletRequest request);

    RegisterResponseDto register(RegisterRequestDto registerRequestDto);

    CommonResponseDto logout(HttpServletRequest request);

}
