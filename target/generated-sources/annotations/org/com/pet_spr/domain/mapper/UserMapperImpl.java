package org.com.pet_spr.domain.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.com.pet_spr.domain.dto.request.UserCreateDto;
import org.com.pet_spr.domain.dto.response.UserDto;
import org.com.pet_spr.domain.entity.Role;
import org.com.pet_spr.domain.entity.User;
import org.com.pet_spr.domain.entity.User.UserBuilder;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-10T00:16:38+0700",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 22.0.1 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toUser(UserCreateDto userCreateDTO) {
        if ( userCreateDTO == null ) {
            return null;
        }

        UserBuilder user = User.builder();

        user.name( userCreateDTO.getName() );
        user.email( userCreateDTO.getEmail() );
        user.password( userCreateDTO.getPassword() );
        user.age( userCreateDTO.getAge() );
        user.gender( userCreateDTO.getGender() );
        user.address( userCreateDTO.getAddress() );

        return user.build();
    }

    @Override
    public UserDto toUserDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto userDto = new UserDto();

        userDto.setRoleName( userRoleName( user ) );
        userDto.setCreatedDate( user.getCreatedDate() );
        userDto.setLastModifiedDate( user.getLastModifiedDate() );
        userDto.setId( user.getId() );
        userDto.setName( user.getName() );
        userDto.setEmail( user.getEmail() );
        userDto.setAge( user.getAge() );
        userDto.setGender( user.getGender() );
        userDto.setAddress( user.getAddress() );

        return userDto;
    }

    @Override
    public List<UserDto> toUserDtos(List<User> users) {
        if ( users == null ) {
            return null;
        }

        List<UserDto> list = new ArrayList<UserDto>( users.size() );
        for ( User user : users ) {
            list.add( toUserDto( user ) );
        }

        return list;
    }

    private String userRoleName(User user) {
        if ( user == null ) {
            return null;
        }
        Role role = user.getRole();
        if ( role == null ) {
            return null;
        }
        String name = role.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
