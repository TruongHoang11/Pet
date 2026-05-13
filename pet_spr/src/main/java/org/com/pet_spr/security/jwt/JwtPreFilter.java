package org.com.pet_spr.security.jwt;


import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.com.pet_spr.constant.ErrorMessage;
import org.com.pet_spr.domain.entity.UserSession;
import org.com.pet_spr.exception.UnauthorizedException;
import org.com.pet_spr.repository.TokenBlackListRepository;
import org.com.pet_spr.repository.UserSessionRepository;
import org.com.pet_spr.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Slf4j
@Component

public class JwtPreFilter extends OncePerRequestFilter {

  private final CustomUserDetailsService customUserDetailsService;
  private final TokenBlackListRepository tokenBlackListRepository;
  private final UserSessionRepository userSessionRepository;
  private final HandlerExceptionResolver resolver; // Cầu nối quan trọng nhất
  private final JwtTokenProvider tokenProvider;


  public JwtPreFilter(
          CustomUserDetailsService customUserDetailsService,
          TokenBlackListRepository tokenBlackListRepository,
          UserSessionRepository userSessionRepository,
          @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver, // @Qualifier phải đặt ở đây
          JwtTokenProvider tokenProvider
  ) {
    this.customUserDetailsService = customUserDetailsService;
    this.tokenBlackListRepository = tokenBlackListRepository;
    this.userSessionRepository = userSessionRepository;
    this.resolver = resolver;
    this.tokenProvider = tokenProvider;
  }

  @SneakyThrows
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
    try {
      String jwt = getJwtFromRequest(request);
      if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {

// 1. Kiểm tra Blacklist
        if (tokenBlackListRepository.existsByToken(jwt)) {
          log.warn("Token is blacklisted: {}", jwt);
          // Ném lỗi qua resolver, nó sẽ tự tìm đến GlobalExceptionHandler
          resolver.resolveException(request, response, null,
                  new UnauthorizedException(ErrorMessage.UNAUTHORIZED));
          return;
        }

        // 2. Kiểm tra UserSession Active
        UserSession userSession = userSessionRepository.findByToken(jwt);
        if (userSession == null || !userSession.getIsActive()) {
          log.warn("Session is inactive for token: {}", jwt);
          resolver.resolveException(request, response, null,
                  new UnauthorizedException(ErrorMessage.Auth.ERR_SESSION_EXPIRED));
          return;
        }

          String userId = tokenProvider.extractSubjectFromJwt(jwt);
          UserDetails userDetails = customUserDetailsService.loadUserById(userId);
          UsernamePasswordAuthenticationToken authenticationToken =
              new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
          authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      }
    } catch (Exception ex) {
      log.error("Could not set user authentication in security context", ex);
    }
    filterChain.doFilter(request, response);
  }

  public static String getJwtFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7, bearerToken.length());
    }
    return null;
  }

}
