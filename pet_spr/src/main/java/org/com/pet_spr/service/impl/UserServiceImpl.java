package org.com.pet_spr.service.impl;

import lombok.RequiredArgsConstructor;
import org.com.pet_spr.constant.ErrorMessage;
import org.com.pet_spr.constant.RoleConstant;
import org.com.pet_spr.domain.dto.request.UserCreateDto;
import org.com.pet_spr.domain.dto.response.UserDto;
import org.com.pet_spr.domain.entity.Role;
import org.com.pet_spr.domain.entity.User;
import org.com.pet_spr.domain.mapper.UserMapper;
import org.com.pet_spr.exception.ConflictException;
import org.com.pet_spr.repository.RoleRepository;
import org.com.pet_spr.repository.UserRepository;
import org.com.pet_spr.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

    @Override
    public UserDto createUser(UserCreateDto userCreateDto) {
        User createUser = userRepository.findByEmail(userCreateDto.getEmail()).orElseThrow(
                () -> new ConflictException(ErrorMessage.User.ERR_EXISTS_EMAIL)
        );
        createUser = userMapper.toUser(userCreateDto);

        //role User có rồi thì gắn vô defaultRole luôn
        // chưa có role User thì se thực hiện lệnh trong orElseGet:
        //tạo mới role và gắn ngược lại cho defaultRole
        Role defaultRole = roleRepository.findByName(RoleConstant.USER).orElseGet(
                () -> {
                    Role newRole = new Role();
                    newRole.setName(RoleConstant.USER);
                    return roleRepository.save(newRole);
                }
        );
        createUser.setRole(defaultRole);
        userRepository.save(createUser);


        return userMapper.toUserDto(createUser);
    }
}
