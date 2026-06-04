package org.com.pet_spr.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.com.pet_spr.constant.ErrorMessage;
import org.com.pet_spr.constant.GenderEnum;
import org.com.pet_spr.validator.annotation.EnumValue;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReqCreatePet {
    @NotBlank(message = ErrorMessage.NOT_NULL_FIELD)
    private String name;

    @NotBlank(message = ErrorMessage.NOT_NULL_FIELD)
    private String specie;

    @NotNull(message = ErrorMessage.NOT_NULL_FIELD)
    @EnumValue(name = "gender", enumClass = GenderEnum.class)
    private String gender;

    @NotNull(message = ErrorMessage.NOT_NULL_FIELD) // Bổ sung: Bắt buộc chọn ngày sinh để tính tuổi
    @PastOrPresent(message = "Birthday must be past or present time")
    private LocalDateTime birthday;

    @NotNull(message = ErrorMessage.NOT_NULL_FIELD) // Bổ sung: Bắt buộc nhập cân nặng để tính giá spa
    @Positive(message = "Weight must be a positive number") // Bổ sung: Tránh nhập số âm hoặc bằng 0
    private Float weight;

    private String healthStatus;
}
