package org.com.pet_spr.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.pet_spr.constant.ErrorMessage;
import org.com.pet_spr.domain.entity.User;
import org.com.pet_spr.exception.NotFoundException;
import org.com.pet_spr.repository.UserRepository;
import org.com.pet_spr.security.UserPrincipal;
import org.com.pet_spr.service.CustomUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetails implements CustomUserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = this.userRepository.findByEmail(email).orElseThrow(
                () ->  new UsernameNotFoundException(String.format(ErrorMessage.User.ERR_NOT_FOUND_USERNAME, email))
        );


        return UserPrincipal.create(user);
    }

    @Override
    public UserDetails loadUserById(String id) {
        User user = this.userRepository.findById(id).orElseThrow(
                () ->  new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_ID, new String[]{id})
        );
        return UserPrincipal.create(user);
    }
}
