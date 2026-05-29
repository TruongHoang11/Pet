package org.com.pet_spr.service;

import org.com.pet_spr.domain.dto.pagination.ResultPaginationDto;
import org.com.pet_spr.domain.dto.request.ReqCreateProduct;
import org.com.pet_spr.domain.dto.request.ReqUpdateProduct;
import org.com.pet_spr.domain.dto.response.CommonResponseDto;
import org.com.pet_spr.domain.dto.response.ProductDto;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface ProductService {
    ProductDto createProduct(ReqCreateProduct reqCreateProduct) ;

    ProductDto updateProduct(ReqUpdateProduct reqUpdateProduct) ;


    CommonResponseDto deleteProduct(Long id);

    ProductDto getProductById(Long id);

    ResultPaginationDto getAllProduct(List<String> filter, Pageable pageable);
}
