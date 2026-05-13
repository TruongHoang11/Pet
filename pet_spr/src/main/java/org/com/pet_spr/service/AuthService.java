package org.com.pet_spr.service;

import jakarta.servlet.http.HttpServletRequest;
import org.com.pet_spr.domain.dto.request.LoginRequestDto;
import org.com.pet_spr.domain.dto.request.RegisterRequestDto;
import org.com.pet_spr.domain.dto.response.CommonResponseDto;
import org.com.pet_spr.domain.dto.response.LoginResponseDto;
import org.com.pet_spr.domain.dto.response.LoginResult;
import org.com.pet_spr.domain.dto.response.RegisterResponseDto;
import org.com.pet_spr.security.UserPrincipal;

import java.util.Map;

public interface AuthService {
    LoginResult login(LoginRequestDto loginRequestDto, HttpServletRequest request);

    RegisterResponseDto register(RegisterRequestDto registerRequestDto);

    CommonResponseDto logout(HttpServletRequest request);

}
