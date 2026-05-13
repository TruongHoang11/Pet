package org.com.pet_spr.service.impl;

import jakarta.mail.search.HeaderTerm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.com.pet_spr.constant.ErrorMessage;
import org.com.pet_spr.constant.RoleConstant;
import org.com.pet_spr.domain.dto.request.LoginRequestDto;
import org.com.pet_spr.domain.dto.request.RegisterRequestDto;
import org.com.pet_spr.domain.dto.response.CommonResponseDto;
import org.com.pet_spr.domain.dto.response.LoginResponseDto;
import org.com.pet_spr.domain.dto.response.LoginResult;
import org.com.pet_spr.domain.dto.response.RegisterResponseDto;
import org.com.pet_spr.domain.entity.Role;
import org.com.pet_spr.domain.entity.User;
import org.com.pet_spr.domain.entity.UserSession;
import org.com.pet_spr.exception.ConflictException;
import org.com.pet_spr.exception.NotFoundException;
import org.com.pet_spr.exception.UnauthorizedException;
import org.com.pet_spr.repository.RoleRepository;
import org.com.pet_spr.repository.TokenBlackListRepository;
import org.com.pet_spr.repository.UserRepository;
import org.com.pet_spr.repository.UserSessionRepository;
import org.com.pet_spr.security.CurrentUser;
import org.com.pet_spr.security.UserPrincipal;
import org.com.pet_spr.security.jwt.JwtPreFilter;
import org.com.pet_spr.security.jwt.JwtTokenProvider;
import org.com.pet_spr.service.AuthService;
import org.com.pet_spr.util.TokenBlackListUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.ResponseCache;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserSessionRepository userSessionRepository;
    private final UserRepository userRepository;
    private final TokenBlackListRepository tokenBlackListRepository;
    private final RoleRepository roleRepository;

    @Value("${jwt.refresh.expiration_time}")
    private Long refreshExpiration;


    @Override
    public LoginResult login(LoginRequestDto loginRequestDto, HttpServletRequest request) {
        // nap input email/password vao security
        try {
            List<UserSession> userSessionList = userSessionRepository.findAllByEmail(loginRequestDto.getEmail());
            for(UserSession userSession : userSessionList){
                if(userSession.getIsActive()){
                    throw new ConflictException(ErrorMessage.Auth.ERR_ALREADY_LOGGED_IN);
                }
            }

            Authentication authenticationToken = new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword());

            // xac thuc nguoi dung qua ham loadByUsername
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            //set thong tin nguoi dung dang nhap vao security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

            String accessToken = jwtTokenProvider.generateToken(userPrincipal, false);
            String refreshToken = jwtTokenProvider.generateToken(userPrincipal, true);

            User user = userRepository.findById(userPrincipal.getId()).orElseThrow(
                    () -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_ID, new String[]{userPrincipal.getId()})
            );

            String ipAddress = TokenBlackListUtil.getClientIP(request);

            UserSession userSession = userSessionRepository.findByIpAddressAndEmail(ipAddress, userPrincipal.getUsername());
            if(userSession == null){
                 userSession = new UserSession();
                userSession.setEmail(user.getEmail());
                userSession.setToken(accessToken);
                userSession.setRefreshToken(refreshToken);
                userSession.setIpAddress(ipAddress);
                userSession.setUser(user);


            } else{
                userSession.setToken(accessToken);
                userSession.setRefreshToken(refreshToken);
                userSession.setIsActive(true);
            }

            userSessionRepository.save(userSession);
            LoginResponseDto loginResponseDto = new LoginResponseDto(userPrincipal.getId(), accessToken, userPrincipal.getAuthorities());

            ResponseCookie responseCookie = ResponseCookie.from("refresh_token", refreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(refreshExpiration)
                    .build();

            LoginResult result = new LoginResult();
            result.setLoginResponseDto(loginResponseDto);
            result.setResponseCookie(responseCookie);

            return result;
        } catch (InternalAuthenticationServiceException e) {
            throw new UnauthorizedException(ErrorMessage.Auth.ERR_INCORRECT_EMAIL);
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException(ErrorMessage.Auth.ERR_INCORRECT_PASSWORD);
        }



    }

    @Override
    public RegisterResponseDto register(RegisterRequestDto registerRequestDto) {
        if(userRepository.existsByEmail(registerRequestDto.getEmail())){
            throw new ConflictException(ErrorMessage.Auth.ERR_ALREADY_EXISTS_EMAIL, new String[]{registerRequestDto.getEmail()});
        }
        User registerUser = new User();
        registerUser.setName(registerRequestDto.getName());
        registerUser.setEmail(registerRequestDto.getEmail());
        registerUser.setAddress(registerRequestDto.getAddress());
        registerUser.setAge(registerRequestDto.getAge());
        registerUser.setGender(registerRequestDto.getGender());
        registerUser.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
        registerUser.setRole(roleRepository.findByName(RoleConstant.USER).orElseThrow(
                () -> new NotFoundException(ErrorMessage.Role.ERR_NOT_FOUND_ROLE, new String[]{RoleConstant.USER})
        ));
        userRepository.save(registerUser);
        RegisterResponseDto registerResponseDto = new RegisterResponseDto();
        registerResponseDto.setId(registerUser.getId());
        registerResponseDto.setAddress(registerUser.getAddress());
        registerResponseDto.setAge(registerUser.getAge());
        registerResponseDto.setEmail(registerUser.getEmail());
        registerResponseDto.setGender(registerUser.getGender());
        registerResponseDto.setName(registerUser.getName());


        return registerResponseDto;
    }

    @Override
    public CommonResponseDto logout(HttpServletRequest request) {

        String token = JwtPreFilter.getJwtFromRequest(request);
        if(token == null){
            throw new UnauthorizedException(ErrorMessage.UNAUTHORIZED);
        }

        UserSession userSession = userSessionRepository.findByToken(token);
        if(userSession == null){
            throw new UnauthorizedException(ErrorMessage.UNAUTHORIZED);
        }
        userSession.setIsActive(false);

        String refreshToken = userSession.getRefreshToken();

        userSessionRepository.save(userSession);

        TokenBlackListUtil.addTokenToBlackList(token, "Logout access token", tokenBlackListRepository);

        if(refreshToken != null && !refreshToken.isEmpty()){
            TokenBlackListUtil.addTokenToBlackList(refreshToken, "Logout refresh token", tokenBlackListRepository);
        }

        SecurityContextHolder.clearContext();

        return new CommonResponseDto(true, "Logout Successfully");

    }
}
