package org.com.pet_spr.service;

import org.com.pet_spr.domain.dto.request.UserCreateDto;
import org.com.pet_spr.domain.dto.response.UserDto;

public interface UserService {

    UserDto createUser(UserCreateDto userCreateDto);
}
