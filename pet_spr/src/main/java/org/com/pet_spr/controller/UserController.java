package org.com.pet_spr.controller;

import lombok.RequiredArgsConstructor;
import org.com.pet_spr.base.RestApiV1;
import org.com.pet_spr.base.VsResponseUtil;
import org.com.pet_spr.constant.UrlConstant;
import org.com.pet_spr.domain.dto.request.UserCreateDto;
import org.com.pet_spr.domain.dto.response.UserDto;
import org.com.pet_spr.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestApiV1
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping(UrlConstant.User.CREATE_USER)
    public ResponseEntity<?> createUser(@RequestBody UserCreateDto userCreateDto){
        UserDto userDto = userService.createUser(userCreateDto);
        return VsResponseUtil.success(HttpStatus.OK, userDto);
    }

}
