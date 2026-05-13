package org.com.pet_spr.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.com.pet_spr.constant.GenderEnum;
import org.com.pet_spr.domain.dto.common.DateAuditing;

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
