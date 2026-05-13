package org.com.pet_spr.repository;

import org.com.pet_spr.domain.entity.TokenBlackList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenBlackListRepository extends JpaRepository<TokenBlackList, Long> {
    TokenBlackList findByToken(String token);

    boolean existsByToken(String token);
}
