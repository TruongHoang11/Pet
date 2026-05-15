package org.com.pet_spr.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.com.pet_spr.constant.ErrorMessage;
import org.com.pet_spr.constant.GenderEnum;
import org.com.pet_spr.domain.entity.Role;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserUpdateDto {

    @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
    private String id;

    @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
    private String name;

    @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    private Integer age;

    @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
    private String address;

    @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    private GenderEnum gender;

    private Role role;
}
