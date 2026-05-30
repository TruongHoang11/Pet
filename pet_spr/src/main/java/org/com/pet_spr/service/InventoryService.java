package org.com.pet_spr.service;

import org.com.pet_spr.domain.dto.pagination.ResultPaginationDto;
import org.com.pet_spr.domain.dto.request.ReqAdjustProduct;
import org.com.pet_spr.domain.dto.request.ReqInventoryProduct;
import org.com.pet_spr.domain.dto.response.InventoryDto;
import org.com.pet_spr.domain.dto.response.InventoryTransactionDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InventoryService {

    // thêm sản phẩm vào kho lưu vết inventory transaction
    InventoryTransactionDto importProduct(ReqInventoryProduct reqInventoryProduct);

    // xuất sản phẩm ra kho lưu vết inventory transaction
    InventoryTransactionDto exportProduct(ReqInventoryProduct reqInventoryProduct);

    // điều chỉnh sl sản phẩm  kho lưu vết inventory transaction
    InventoryTransactionDto adjustProduct(ReqAdjustProduct reqAdjustProduct);

    // lấy tồn kho product (quantity)
    InventoryDto getInventoryByProductId(Long productId);


    ResultPaginationDto getInventoryTransactionHistory(List<String> filter, Pageable pageable);



}
