package org.com.pet_spr.domain.dto.response;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.com.pet_spr.constant.GenderEnum;
import org.com.pet_spr.domain.dto.common.DateAuditing;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto extends DateAuditing {

  private String id;
  private String name;
  private String email;
  private Integer age;
  private GenderEnum gender;
  private String address;

  private String roleName;

}

