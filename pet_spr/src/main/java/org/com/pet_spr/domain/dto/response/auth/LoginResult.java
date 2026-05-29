package org.com.pet_spr.domain.dto.response.auth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseCookie;

@Getter
@Setter
public class LoginResult {
    private LoginResponseDto loginResponseDto;
    private ResponseCookie responseCookie;
}
