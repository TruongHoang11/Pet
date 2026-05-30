package org.com.pet_spr.domain.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.com.pet_spr.domain.dto.common.UserDateAuditing;

@Getter
@Setter
public class InventoryTransactionDto extends UserDateAuditing {
   // private Long id;
    private Integer quantity;
    private String type; // export / import
    private String note; // ghi chu
    private Integer currentStock; // tồn kho sau khi import/ export
   // private String createdBy; // ai thực hiện
}
