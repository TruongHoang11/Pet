package org.com.pet_spr.domain.dto.response.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.com.pet_spr.constant.CommonConstant;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

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
