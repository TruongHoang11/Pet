package org.com.pet_spr.domain.dto.request.auth;

import lombok.Getter;

@Getter
public class ChangePassword {
    private String newPassword;
    private String repeatPassword;
    private String otp;
}
