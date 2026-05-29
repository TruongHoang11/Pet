package org.com.pet_spr.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.com.pet_spr.constant.ErrorMessage;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReqCreateProduct {

    @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
    private String name;

    @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
    private String description;

    @NotNull(message = ErrorMessage.NOT_NULL_FIELD)
    private BigDecimal price;

    @NotNull(message = ErrorMessage.NOT_NULL_FIELD)
    private Long categoryId;

    @NotNull(message = ErrorMessage.NOT_NULL_FIELD)
    private Integer quantity; // Số lượng hàng nhập kho ban đầu


}
