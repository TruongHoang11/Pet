package org.com.pet_spr.service.impl;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.com.pet_spr.constant.ErrorMessage;
import org.com.pet_spr.constant.UrlConstant;
import org.com.pet_spr.domain.dto.common.DataMailDto;
import org.com.pet_spr.domain.dto.request.ChangePassword;
import org.com.pet_spr.domain.dto.response.CommonResponseDto;
import org.com.pet_spr.domain.entity.ForgetPassword;
import org.com.pet_spr.domain.entity.User;
import org.com.pet_spr.exception.BadRequestException;
import org.com.pet_spr.exception.NotFoundException;
import org.com.pet_spr.repository.ForgetPasswordRepository;
import org.com.pet_spr.repository.UserRepository;
import org.com.pet_spr.service.ForgetPasswordService;
import org.com.pet_spr.util.SendMailUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ForgetPasswordImpl implements ForgetPasswordService {
    private static final SecureRandom secureRandom = new SecureRandom();
    private final SendMailUtil sendMailUtil;
    private final UserRepository userRepository;
    private final ForgetPasswordRepository forgetPasswordRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public CommonResponseDto verifyEmail(String email) throws MessagingException {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_EMAIL, new String[]{email})
        );
        String otp = generateOTP(6);

        ForgetPassword forgetPassword = forgetPasswordRepository.findByUser(user).orElseGet(
                () -> new ForgetPassword()
        );
        forgetPassword.setOtp(otp);
        forgetPassword.setUser(user);
        forgetPassword.setExpiryDate(LocalDateTime.now().plusMinutes(3));
        forgetPasswordRepository.save(forgetPassword);

        DataMailDto dataMailDto = new DataMailDto();
        dataMailDto.setTo(email);
        dataMailDto.setSubject("Pett OTP");
        dataMailDto.setContent("OTP for forgetting password:" + otp);
        sendMailUtil.sendEmail(dataMailDto);
        return new CommonResponseDto(true, "Send OTP successfully");
    }

    @Override
    public void verifyOTP(String otp, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_EMAIL, new String[]{email})
        );

        ForgetPassword forgetPassword = forgetPasswordRepository.findByOtpAndUser(otp, user).orElseThrow(
                () -> new BadRequestException(ErrorMessage.INVALID_OTP)
        );

        if(forgetPassword.getExpiryDate().isBefore(LocalDateTime.now())){
            throw new BadRequestException(ErrorMessage.OTP_EXPIRED);
        }
    }

    @Override
    public void changePassword(ChangePassword changePassword,  String email) {
        if(!changePassword.getNewPassword().equals(changePassword.getRepeatPassword())){
            throw new BadRequestException(ErrorMessage.INVALID_REPEAT_PASSWORD);
        }
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_EMAIL, new String[]{email})
        );
        ForgetPassword forgetPassword = forgetPasswordRepository.findByOtpAndUser(changePassword.getOtp(), user).orElseThrow(
                () -> new BadRequestException(ErrorMessage.INVALID_OTP));
        if(forgetPassword.getExpiryDate().isBefore(LocalDateTime.now())){
            throw new BadRequestException(ErrorMessage.OTP_EXPIRED);
        }

        //set passwod
        user.setPassword(passwordEncoder.encode(changePassword.getNewPassword()));
        userRepository.save(user);

        forgetPasswordRepository.delete(forgetPassword);
    }

    public static String generateOTP(int length) {
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < length; i++) {
            // Sinh số ngẫu nhiên từ 0-9
            int digit = secureRandom.nextInt(10);
            otp.append(digit);
        }
        return otp.toString();
    }
}
