package org.com.pet_spr.domain.mapper;

import org.com.pet_spr.domain.dto.request.ReqCreateProduct;
import org.com.pet_spr.domain.dto.response.ProductDto;
import org.com.pet_spr.domain.entity.Product;
import org.com.pet_spr.domain.entity.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // Chiều vào: Bỏ mapping category đi, chỉ map các trường primitive
    @Mapping(target = "category", ignore = true) // Bảo MapStruct đừng động vào trường này
    @Mapping(target = "inventory", ignore = true)
 //   @Mapping(target ="productImages", ignore = true)
    Product toProduct(ReqCreateProduct reqCreateProduct);

    // Map nested category fields from Product -> ProductDto
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "stockQuantity", source = "inventory.quantity")
  //  @Mapping(target = "imageUrls", expression = "java(mapImages(product.getProductImages()))") // Sử dụng method mapImages để chuyển đổi
    ProductDto toProductDto(Product product);

    // Chuyển danh sách (MapStruct sẽ tự dùng cái toProductDto ở trên cho từng phần tử)
    List<ProductDto> productDtos(List<Product> products);

//    default List<String> mapImages(List<ProductImage> productImages){
//        if(productImages == null){
//            return null;
//        }
//        return productImages.stream()
//                .map(productImage -> productImage.getImageUrl())
//                .collect(Collectors.toList());
//    }
}
