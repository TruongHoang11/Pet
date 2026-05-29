package org.com.pet_spr.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.pet_spr.base.RestApiV1;
import org.com.pet_spr.base.VsResponseUtil;
import org.com.pet_spr.constant.UrlConstant;
import org.com.pet_spr.domain.dto.response.CommonResponseDto;
import org.com.pet_spr.service.ProductImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestApiV1
public class ProductImageController {
    private final ProductImageService productImageService;

    @PostMapping(UrlConstant.ProductImages.ADD_IMAGES)
    public ResponseEntity<?> addImages(@RequestParam Long productId,
                                       @RequestParam List<MultipartFile> files) throws URISyntaxException, IOException {
        CommonResponseDto commonResponseDto = productImageService.addImages(productId, files);
        return VsResponseUtil.success(HttpStatus.OK, commonResponseDto);
    }

    @DeleteMapping(UrlConstant.ProductImages.DELETE_IMAGE)
    public ResponseEntity<?> deleteImage(@PathVariable(name = "id") Long imageId){
        CommonResponseDto commonResponseDto = productImageService.deleteImage(imageId);
        return VsResponseUtil.success(HttpStatus.OK, commonResponseDto);
    }
}
