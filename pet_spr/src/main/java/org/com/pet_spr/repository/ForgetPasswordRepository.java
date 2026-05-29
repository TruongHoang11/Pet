package org.com.pet_spr.repository;

import org.com.pet_spr.domain.entity.ForgetPassword;
import org.com.pet_spr.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ForgetPasswordRepository extends JpaRepository<ForgetPassword, Long> {
    Optional<ForgetPassword> findByOtpAndUser(String otp, User user);

    void deleteByUser(User user);

    Optional<ForgetPassword> findByUser(User user);
}
