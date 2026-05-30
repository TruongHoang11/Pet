package org.com.pet_spr.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.pet_spr.base.RestApiV1;
import org.com.pet_spr.base.VsResponseUtil;
import org.com.pet_spr.constant.UrlConstant;
import org.com.pet_spr.domain.dto.request.ReqAdjustProduct;
import org.com.pet_spr.domain.dto.request.ReqInventoryProduct;
import org.com.pet_spr.domain.dto.response.InventoryTransactionDto;
import org.com.pet_spr.service.InventoryService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestApiV1
@RequiredArgsConstructor
@Slf4j
public class InventoryController {
    private final InventoryService inventoryService;


    @GetMapping(UrlConstant.Inventory.GET_INVENTORY_BY_PRODUCT_ID)
    public ResponseEntity<?> getInventoryByProductId(@PathVariable Long id){
        return VsResponseUtil.success(HttpStatus.OK,inventoryService.getInventoryByProductId(id));

    }

    @PostMapping(UrlConstant.Inventory.IMPORT_PRODUCT)
    public ResponseEntity<?> importInventory(@RequestBody @Valid ReqInventoryProduct reqInventoryProduct)  {
        InventoryTransactionDto importInventory = inventoryService.importProduct(reqInventoryProduct);

        return VsResponseUtil.success(HttpStatus.OK, importInventory);
    }

    @PostMapping(UrlConstant.Inventory.EXPORT_PRODUCT)
    public ResponseEntity<?> exportInventory(@RequestBody @Valid ReqInventoryProduct reqInventoryProduct)  {
        InventoryTransactionDto exportInventory = inventoryService.exportProduct(reqInventoryProduct);

        return VsResponseUtil.success(HttpStatus.OK, exportInventory);
    }

    @PostMapping(UrlConstant.Inventory.ADJUST_PRODUCT)
    public ResponseEntity<?> adjustInventory(@RequestBody @Valid ReqAdjustProduct reqAdjustProduct)  {
        InventoryTransactionDto adjustInventory = inventoryService.adjustProduct(reqAdjustProduct);

        return VsResponseUtil.success(HttpStatus.OK, adjustInventory);
    }

//    @PutMapping(UrlConstant.Product.UPDATE_PRODUCT)
//    public ResponseEntity<?> updateProduct(@RequestBody @Valid ReqUpdateProduct reqUpdateProduct) {
//        return VsResponseUtil.success(HttpStatus.OK,productService.updateProduct(reqUpdateProduct) );
//
//    }
//
//    @DeleteMapping(UrlConstant.Product.DELETE_PRODUCT)
//    public ResponseEntity<?> deleteProduct(@PathVariable Long id){
//        CommonResponseDto commonResponseDto = productService.deleteProduct(id);
//
//        return VsResponseUtil.success(HttpStatus.OK, commonResponseDto);
//
//    }
//
    @GetMapping(UrlConstant.Inventory.GET_INVENTORY_TRANSACTION_HISTORY)
    public ResponseEntity<?> getInventoryTransactionHistory(
            @RequestParam(value = "filter", required = false) List<String> filter,
            Pageable pageable){

        return VsResponseUtil.success(HttpStatus.OK, inventoryService.getInventoryTransactionHistory(filter,pageable) );

    }
}
