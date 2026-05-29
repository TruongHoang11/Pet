package org.com.pet_spr.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.com.pet_spr.base.RestApiV1;
import org.com.pet_spr.base.VsResponseUtil;
import org.com.pet_spr.constant.UrlConstant;
import org.com.pet_spr.domain.dto.request.auth.LoginRequestDto;
import org.com.pet_spr.domain.dto.request.auth.RegisterRequestDto;
import org.com.pet_spr.domain.dto.response.CommonResponseDto;
import org.com.pet_spr.domain.dto.response.auth.LoginResponseDto;
import org.com.pet_spr.domain.dto.response.auth.LoginResult;
import org.com.pet_spr.domain.dto.response.auth.RegisterResponseDto;
import org.com.pet_spr.service.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static org.com.pet_spr.base.VsResponseUtil.success;

@RestApiV1
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;


    @PostMapping(UrlConstant.Auth.LOGIN)
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequestDto, HttpServletRequest request){
            LoginResult loginResult = authService.login(loginRequestDto, request);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.SET_COOKIE, loginResult.getResponseCookie().toString());

            LoginResponseDto loginResponseDto = loginResult.getLoginResponseDto();

            return VsResponseUtil.success(headers,HttpStatus.OK, loginResponseDto);

    }

    @PostMapping(UrlConstant.Auth.REGISTER)
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDto registerRequestDto){
            RegisterResponseDto responseDto = authService.register(registerRequestDto);
            return VsResponseUtil.success(HttpStatus.OK, responseDto);

    }

    @PostMapping(UrlConstant.Auth.LOGOUT)
    public ResponseEntity<?> logout(HttpServletRequest request){
        CommonResponseDto commonResponseDto = authService.logout(request);
        ResponseCookie deleteSrpingCookie = ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE,deleteSrpingCookie.toString()) ;
        return VsResponseUtil.success(headers, HttpStatus.OK, commonResponseDto);
    }
}
