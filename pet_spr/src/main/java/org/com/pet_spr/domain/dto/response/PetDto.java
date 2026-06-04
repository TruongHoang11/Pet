package org.com.pet_spr.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PetDto {
    private Long id;
    private String name;
    private String specie;
    private String gender;
    private LocalDateTime birthday;
    private float weight;
    private String healthStatus;
    private Boolean activeFlag;
    private Boolean deleteFlag;
}
