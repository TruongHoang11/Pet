package org.com.pet_spr.repository;

import org.com.pet_spr.domain.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    List<UserSession> findAllByEmail(String email);

    UserSession findByIpAddressAndEmail(String ipAddress, String email);

    UserSession findByToken(String token);


}
