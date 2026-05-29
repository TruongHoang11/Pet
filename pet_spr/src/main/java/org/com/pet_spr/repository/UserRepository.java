package org.com.pet_spr.repository;

import org.com.pet_spr.constant.ErrorMessage;
import org.com.pet_spr.domain.entity.User;
import org.com.pet_spr.exception.NotFoundException;
import org.com.pet_spr.security.UserPrincipal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {


    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findById(String id);

    default User getUser(UserPrincipal currentUser) {
        return findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_USERNAME,
                        new String[]{currentUser.getUsername()}));
    }
}
