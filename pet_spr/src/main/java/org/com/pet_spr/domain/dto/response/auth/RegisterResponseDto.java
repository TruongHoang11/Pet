package org.com.pet_spr.domain.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.com.pet_spr.constant.GenderEnum;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponseDto   {

    private String id;

    private String name;

    private String email;

    private GenderEnum gender;

    private Integer age;

    private String address;


}
