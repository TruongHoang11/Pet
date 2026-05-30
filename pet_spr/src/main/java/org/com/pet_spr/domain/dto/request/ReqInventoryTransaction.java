package org.com.pet_spr.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.com.pet_spr.constant.ErrorMessage;
import org.com.pet_spr.constant.TypeInventory;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ReqInventoryTransaction {

    @NotNull(message = ErrorMessage.NOT_NULL_FIELD)
    private Long productId;

    @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
    private TypeInventory type;

    private LocalDate fromDate;

    private LocalDate toDate;
}
