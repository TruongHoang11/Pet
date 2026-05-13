package org.com.pet_spr.controller;


import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.com.pet_spr.base.RestApiV1;
import org.com.pet_spr.base.VsResponseUtil;
import org.com.pet_spr.constant.UrlConstant;
import org.com.pet_spr.domain.dto.request.ChangePassword;
import org.com.pet_spr.domain.dto.request.RequestVerifyOtp;
import org.com.pet_spr.domain.dto.response.CommonResponseDto;
import org.com.pet_spr.service.ForgetPasswordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestApiV1
@RequiredArgsConstructor
public class ForgetPasswordController {
    private final ForgetPasswordService forgetPasswordService;

    @PostMapping(UrlConstant.ForgetPassword.VERIFY_EMAIL)
    public ResponseEntity<?> verifyEmail(@PathVariable String email) throws MessagingException {

        CommonResponseDto commonResponseDto = forgetPasswordService.verifyEmail(email);
        return VsResponseUtil.success(HttpStatus.OK, commonResponseDto);
    }

    @PostMapping(UrlConstant.ForgetPassword.VERIFY_OTP)
    public ResponseEntity<?> verifyOtp(@RequestBody RequestVerifyOtp requestVerifyOtp) throws MessagingException {
        forgetPasswordService.verifyOTP(requestVerifyOtp.getOtp(), requestVerifyOtp.getEmail());
        return VsResponseUtil.success(HttpStatus.OK, "Verify OTP successfully");
    }

    @PostMapping(UrlConstant.ForgetPassword.CHANGE_PASSWORD)
    public ResponseEntity<?> changePasswordHandler(@RequestBody ChangePassword changePassword,
                                                   @PathVariable String email) {
        forgetPasswordService.changePassword(changePassword, email);
        return VsResponseUtil.success("Password changed successfully");
    }
}
