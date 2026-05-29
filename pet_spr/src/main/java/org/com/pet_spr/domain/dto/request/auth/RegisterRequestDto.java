package org.com.pet_spr.domain.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.com.pet_spr.constant.ErrorMessage;
import org.com.pet_spr.constant.GenderEnum;
import org.com.pet_spr.domain.dto.common.DateAuditing;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDto extends DateAuditing {

    @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
    private String name;

    @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
    @Email(message = ErrorMessage.INVALID_EMAIL)
    private String email;

    @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
    private String password;

    private GenderEnum gender;

    @NotNull(message = ErrorMessage.NOT_BLANK_FIELD)
    private Integer age;

    @NotBlank(message =  ErrorMessage.NOT_BLANK_FIELD)
    private String address;
}
