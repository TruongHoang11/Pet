package org.com.pet_spr.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.com.pet_spr.base.RestApiV1;
import org.com.pet_spr.base.VsResponseUtil;
import org.com.pet_spr.constant.UrlConstant;
import org.com.pet_spr.domain.dto.request.ReqCreateProduct;
import org.com.pet_spr.domain.dto.request.ReqUpdateProduct;
import org.com.pet_spr.domain.dto.request.UserCreateDto;
import org.com.pet_spr.domain.dto.request.UserUpdateDto;
import org.com.pet_spr.domain.dto.response.CommonResponseDto;
import org.com.pet_spr.domain.dto.response.ProductDto;
import org.com.pet_spr.domain.dto.response.UserDto;
import org.com.pet_spr.service.ProductService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@RestApiV1
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;


    @GetMapping(UrlConstant.Product.GET_PRODUCT)
    public ResponseEntity<?> getProduct(@PathVariable Long id){
        return VsResponseUtil.success(HttpStatus.OK,productService.getProductById(id) );

    }

    @PostMapping(UrlConstant.Product.CREATE_PRODUCT)
    public ResponseEntity<?> createProduct(@RequestBody @Valid ReqCreateProduct reqCreateProduct)  {
        ProductDto productDto = productService.createProduct(reqCreateProduct);
        return VsResponseUtil.success(HttpStatus.OK, productDto);
    }

    @PutMapping(UrlConstant.Product.UPDATE_PRODUCT)
    public ResponseEntity<?> updateProduct(@RequestBody @Valid ReqUpdateProduct reqUpdateProduct) {
        return VsResponseUtil.success(HttpStatus.OK,productService.updateProduct(reqUpdateProduct) );

    }

    @DeleteMapping(UrlConstant.Product.DELETE_PRODUCT)
    public ResponseEntity<?> deleteProduct(@PathVariable Long id){
        CommonResponseDto commonResponseDto = productService.deleteProduct(id);

        return VsResponseUtil.success(HttpStatus.OK, commonResponseDto);

    }

    @GetMapping(UrlConstant.Product.GET_PRODUCTS)
    public ResponseEntity<?> getAllProduct(
            @RequestParam(value = "filter", required = false) List<String> filter,
            Pageable pageable){

        return VsResponseUtil.success(HttpStatus.OK,productService.getAllProduct(filter,pageable) );

    }
}
