package org.com.pet_spr.domain.mapper;

import org.com.pet_spr.domain.dto.response.OrderDetailDto;
import org.com.pet_spr.domain.dto.response.OrderDto;
import org.com.pet_spr.domain.entity.Order;
import org.com.pet_spr.domain.entity.OrderDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
    uses = OrderDetailMapper.class
)
public interface OrderMapper {

// chỉ cần goi uses thì nó tự map List<OrderDetail> thành List<OrderDetailDto> không cần ghi mapping thêm
    @Mapping(target = "paymentStatus", source = "payment.status")
    OrderDto toDto(Order order);

    List<OrderDto> toDtoList(List<Order> orders);



}
