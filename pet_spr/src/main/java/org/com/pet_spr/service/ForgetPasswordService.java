package org.com.pet_spr.service;

import jakarta.mail.MessagingException;
import org.com.pet_spr.domain.dto.request.ChangePassword;
import org.com.pet_spr.domain.dto.response.CommonResponseDto;

public interface ForgetPasswordService {

    CommonResponseDto verifyEmail(String email) throws MessagingException;

    void verifyOTP(String otp, String email);

    void changePassword(ChangePassword changePassword, String email);



}
