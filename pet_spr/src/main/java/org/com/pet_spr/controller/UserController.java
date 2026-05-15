package org.com.pet_spr.controller;

import lombok.RequiredArgsConstructor;
import org.com.pet_spr.base.RestApiV1;
import org.com.pet_spr.base.VsResponseUtil;
import org.com.pet_spr.constant.UrlConstant;
import org.com.pet_spr.domain.dto.request.UserCreateDto;
import org.com.pet_spr.domain.dto.request.UserUpdateDto;
import org.com.pet_spr.domain.dto.response.UserDto;
import org.com.pet_spr.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestApiV1
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUser(@PathVariable String id){
            return VsResponseUtil.success(HttpStatus.OK,userService.getUserById(id) );

    }

    @PostMapping(UrlConstant.User.CREATE_USER)
    public ResponseEntity<?> createUser(@RequestBody UserCreateDto userCreateDto){
        UserDto userDto = userService.createUser(userCreateDto);
        return VsResponseUtil.success(HttpStatus.OK, userDto);
    }

    @PutMapping("/user")
    public ResponseEntity<?> updateUser(@RequestBody UserUpdateDto user){
            return VsResponseUtil.success(HttpStatus.OK,userService.updateUser(user) );

    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id){
            userService.deleteUser(id);
            return VsResponseUtil.success(HttpStatus.OK, null);

    }

    @GetMapping("/user")
    public ResponseEntity<?> getAllUser(
            @RequestParam(value = "filter", required = false) List<String> filter,
            Pageable pageable){

            return VsResponseUtil.success(HttpStatus.OK,userService.getAllUser(filter,pageable) );

    }



}
