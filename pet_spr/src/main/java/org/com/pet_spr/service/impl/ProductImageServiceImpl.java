package org.com.pet_spr.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.pet_spr.constant.ErrorMessage;
import org.com.pet_spr.domain.dto.request.ReqSetMainImage;
import org.com.pet_spr.domain.dto.response.CommonResponseDto;
import org.com.pet_spr.domain.dto.response.ResUploadFileResultDto;
import org.com.pet_spr.domain.entity.Product;
import org.com.pet_spr.domain.entity.ProductImage;
import org.com.pet_spr.exception.NotFoundException;
import org.com.pet_spr.repository.ProductImageRepository;
import org.com.pet_spr.repository.ProductRepository;
import org.com.pet_spr.service.FileService;
import org.com.pet_spr.service.ProductImageService;
import org.com.pet_spr.service.ProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {
    private final FileService fileService;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    @Value("${hoang.upload-file.base-uri}")
    private String baseUri;



    @Override
    public CommonResponseDto addImages(Long productId, List<MultipartFile> files) throws URISyntaxException, IOException {

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new NotFoundException(ErrorMessage.Product.ERR_NOT_FOUND_ID, new String[] {String.valueOf(productId)})
        );

        ResUploadFileResultDto uploadFileResultDto = fileService.uploadFile(files, "products");
        if(uploadFileResultDto.getResUploadFileDtoList() != null && !uploadFileResultDto.getResUploadFileDtoList().isEmpty()){
            List<ProductImage> images = uploadFileResultDto.getResUploadFileDtoList().stream()
                    .map(resUploadFileDto -> {
                        ProductImage productImage = new ProductImage();
                        productImage.setProduct(product);
                        productImage.setImageUrl(resUploadFileDto.getFileName());
                        return productImage;
                    }).toList();
            productImageRepository.saveAll(images);
            return new CommonResponseDto(true, "Thêm ảnh sản phẩm thành công");
        }
        return new CommonResponseDto(false, "Không có ảnh nào được thêm (file lỗi hoặc trống");
    }

    @Override
    public CommonResponseDto deleteImage(Long imageId) {
        ProductImage productImage = productImageRepository.findById(imageId).orElseThrow(
                () -> new NotFoundException(ErrorMessage.ProductImage.ERR_NOT_FOUND_ID, new String[] {String.valueOf(imageId)})
        );
        // 2. XÓA FILE VẬT LÝ TRÊN Ổ CỨNG TRƯỚC
        try {
            Path filePath = Paths.get(baseUri, "products", productImage.getImageUrl()).toAbsolutePath().normalize();
            boolean isDeleted = Files.deleteIfExists(filePath);
            if (isDeleted) {
                log.info("Xóa file vật lý thành công tại: {}", filePath);
            } else {
                log.warn("File vật lý không tồn tại trên ổ cứng nhưng vẫn tiến hành xóa DB: {}", filePath);
            }
        } catch (IOException e) {
            log.error("Lỗi khi xóa file vật lý của ảnh ID: {}", imageId, e);
            // Bạn có thể cân nhắc throw lỗi hoặc bỏ qua để xóa DB tiếp. Ở đây khuyên nên để tiếp tục xóa DB.
        }

        productImageRepository.delete(productImage);
        return new CommonResponseDto(true, "Xóa ảnh sản phẩm thành công");

    }

    @Override
    public CommonResponseDto changeMainImage(ReqSetMainImage req) {
        // 1. Kiểm tra sản phẩm có tồn tại không
        if (!productRepository.existsById(req.getProductId())) {
            throw new NotFoundException(ErrorMessage.Product.ERR_NOT_FOUND_ID, new String[] {String.valueOf(req.getProductId())});
        }

        // 2. Kiểm tra ảnh có tồn tại và có thuộc sản phẩm này không
        ProductImage targetImage = productImageRepository.findById(req.getImageId()).orElseThrow(
                () -> new NotFoundException(ErrorMessage.ProductImage.ERR_NOT_FOUND_ID, new String[] {String.valueOf(req.getImageId())})
        );

        if (!targetImage.getProduct().getId().equals(req.getProductId())) {
            return new CommonResponseDto(false, "Ảnh không thuộc về sản phẩm này");
        }

        // 3. Đưa tất cả ảnh của sản phẩm về ảnh phụ (isMain = false)
        productImageRepository.resetMainImageByProductId(req.getProductId());

        // 4. Kích hoạt ảnh được chọn làm ảnh chính (isMain = true)
        targetImage.setIsMain(true);
        productImageRepository.save(targetImage);

        return new CommonResponseDto(true, "Thay đổi ảnh đại diện sản phẩm thành công");
    }



}
