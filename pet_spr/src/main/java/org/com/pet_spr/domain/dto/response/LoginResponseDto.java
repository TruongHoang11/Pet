package org.com.pet_spr.domain.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.MultiValuedMap;
import org.com.pet_spr.constant.CommonConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponseDto {

    private String tokenType = CommonConstant.BEARER_TOKEN;

    private String accessToken;



    private String id;

    private Collection<? extends GrantedAuthority> authorities;



    public LoginResponseDto( String id,String accessToken, Collection<? extends GrantedAuthority> authorities){
        this.id = id;
        this.accessToken = accessToken;

        this.authorities = authorities;
    }
}
