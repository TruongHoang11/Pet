package org.com.pet_spr.service;

import org.com.pet_spr.domain.dto.pagination.ResultPaginationDto;
import org.com.pet_spr.domain.dto.request.UserCreateDto;
import org.com.pet_spr.domain.dto.request.UserUpdateDto;
import org.com.pet_spr.domain.dto.response.UserDto;
import org.com.pet_spr.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface UserService {
    UserDto getUserById(String userId);

    UserDto createUser(UserCreateDto userCreateDto);

    UserDto updateUser(UserUpdateDto userUpdateDto);

    void deleteUser(String id);

    ResultPaginationDto getAllUser(List<String> filter, Pageable pageable);
}
