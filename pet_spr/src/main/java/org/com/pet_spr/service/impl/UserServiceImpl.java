package org.com.pet_spr.service.impl;

import ch.qos.logback.core.util.StringCollectionUtil;
import lombok.RequiredArgsConstructor;
import org.com.pet_spr.constant.ErrorMessage;
import org.com.pet_spr.constant.RoleConstant;
import org.com.pet_spr.domain.dto.pagination.ResultPaginationDto;
import org.com.pet_spr.domain.dto.request.UserCreateDto;
import org.com.pet_spr.domain.dto.request.UserUpdateDto;
import org.com.pet_spr.domain.dto.response.UserDto;
import org.com.pet_spr.domain.entity.Role;
import org.com.pet_spr.domain.entity.User;
import org.com.pet_spr.domain.mapper.UserMapper;
import org.com.pet_spr.domain.specification.FilterAttributeSearch;
import org.com.pet_spr.domain.specification.FilterProcessor;
import org.com.pet_spr.domain.specification.SEARCH_OPERATION;
import org.com.pet_spr.domain.specification.SpecificationBuilder;
import org.com.pet_spr.exception.ConflictException;
import org.com.pet_spr.exception.NotFoundException;
import org.com.pet_spr.exception.UnauthorizedException;
import org.com.pet_spr.repository.RoleRepository;
import org.com.pet_spr.repository.UserRepository;
import org.com.pet_spr.security.SecurityUtil;
import org.com.pet_spr.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;


    public void checkExistedUserByEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException(ErrorMessage.User.ERR_EXISTS_EMAIL, new String[]{email});
        }
    }

    @Override
    public UserDto getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_ID, new String[]{userId}));
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto createUser(UserCreateDto userCreateDto) {
        checkExistedUserByEmail(userCreateDto.getEmail());
        User createUser = new User();
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

    @Override
    public UserDto updateUser(UserUpdateDto userUpdateDto) {
        User updateUser = userRepository.findById(userUpdateDto.getId()).orElseThrow(
                () -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_ID, new String[]{String.valueOf(userUpdateDto.getId())})
        );
        if(userUpdateDto.getRole() != null){
            Role role = roleRepository.findById(userUpdateDto.getRole().getId()).orElseThrow(
                    () -> new NotFoundException(ErrorMessage.Role.ERR_NOT_FOUND_ROLE, new String[]{String.valueOf(userUpdateDto.getRole().getId())})
            );
            updateUser.setRole(role);
        }
        updateUser.setName(userUpdateDto.getName());
        updateUser.setAddress(userUpdateDto.getAddress());
        updateUser.setAge(userUpdateDto.getAge());
        updateUser.setGender(userUpdateDto.getGender());
        userRepository.save(updateUser);
        return userMapper.toUserDto(updateUser);
    }

    @Override
    public void deleteUser(String id) {
        User deleteUser = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_ID, new String[]{id})
        );
        // set flag(true) -> da xoa
        deleteUser.setDeleteFlag(true);
        userRepository.save(deleteUser);

    }

//    @Override
//    public ResultPaginationDto getAllUser(List<String> filter, Pageable pageable) {
//
//        SpecificationBuilder<User> specificationBuilder = new SpecificationBuilder<>();
//
//        if(filter != null && !filter.isEmpty()) {
//            // Regex bóc tách:
//            // Group 1: Dấu nháy đơn (') báo hiệu OR, có hoặc không (?)
//            // Group 2: Key (ví dụ: role.name)
//            // Group 3: Operation (ví dụ: :, >, <)
//            // Group 4: Value (ví dụ: *admin*)
//            Pattern pattern = Pattern.compile("^('?)([a-zA-Z0-9_.]+)([<:>~!])(.*)$");
//            for(String condition : filter){
//                Matcher matcher = pattern.matcher(condition);
//                if(matcher.find()){
//                    String key = matcher.group(2);
//                    String operation = matcher.group(3);
//                    String valueStr = matcher.group(4);
//                    String orIndicator = matcher.group(1);
//
//                    String prefix = null;
//                    String suffix = null;
//
//                    if(valueStr.startsWith("*")){
//                        prefix = "*";
//                        valueStr = valueStr.substring(1);
//                    }
//                    if(valueStr.endsWith("*")){
//                        suffix = "*";
//                        valueStr = valueStr.substring(0, valueStr.length() - 1);
//                    }
//                    boolean orPredicate = orIndicator != null && orIndicator.equals(SEARCH_OPERATION.OR_PREDICATE_FLAG);
//                    if(orPredicate){
//                        specificationBuilder.with(orIndicator, key, operation, valueStr, prefix, suffix);
//                    } else {
//                        specificationBuilder.with(key, operation, valueStr, prefix, suffix);
//                    }
//                }
//            }
//        }
//
//        Page<User> userPages = userRepository.findAll(specificationBuilder.build(), pageable);
//        ResultPaginationDto resultPaginationDto = new ResultPaginationDto();
//        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();
//        meta.setPage(pageable.getPageNumber() + 1);
//        meta.setPages(pageable.getPageSize());
//        meta.setPageSize(userPages.getTotalPages());
//        meta.setTotal(userPages.getTotalElements());
//
//        resultPaginationDto.setMeta(meta);
//        List<UserDto> result = userMapper.toUserDtos(userPages.getContent());
//        resultPaginationDto.setResult(result);
//
//        return resultPaginationDto;
//    }


    @Override
    public ResultPaginationDto getAllUser(List<String> filter, Pageable pageable) {
        SpecificationBuilder<User> specificationBuilder = new SpecificationBuilder<>();
        FilterProcessor.process(specificationBuilder, filter);

        Specification<User> spec = specificationBuilder.build();
        Specification<User> softDeleteSpec = (root, query, cb) -> cb.equal(root.get("deleteFlag"), false);

        Specification<User> finalSpec = (spec == null) ? softDeleteSpec : spec.and(softDeleteSpec);

        // Thực thi query
        Page<User> pageUser = userRepository.findAll(finalSpec, pageable);


        // Mapping kết quả
        ResultPaginationDto resultPaginationDTO = new ResultPaginationDto();
        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageUser.getTotalPages());
        meta.setTotal(pageUser.getTotalElements());

        List<UserDto> result = userMapper.toUserDtos(pageUser.getContent());

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(result);

        return resultPaginationDTO;
    }

    @Override
    public User getUserLogin() {
        String email = SecurityUtil.getCurrentUserLogin().orElseThrow(
                () -> new UnauthorizedException(ErrorMessage.LOGIN_REQUIRED)
        );
        User currentUser = userRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_EMAIL, new String[]{email})
        );
        return currentUser;
    }
}
