package org.com.pet_spr.service;

import org.com.pet_spr.domain.dto.request.ReqSetMainImage;
import org.com.pet_spr.domain.dto.response.CommonResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface ProductImageService {

    CommonResponseDto addImages(Long productId, List<MultipartFile> files) throws URISyntaxException, IOException;

    CommonResponseDto deleteImage(Long imageId);

    CommonResponseDto changeMainImage(ReqSetMainImage reqSetMainImage);
}
