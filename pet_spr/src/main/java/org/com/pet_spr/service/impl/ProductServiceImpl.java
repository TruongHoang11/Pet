package org.com.pet_spr.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.pet_spr.constant.ErrorMessage;
import org.com.pet_spr.domain.dto.pagination.ResultPaginationDto;
import org.com.pet_spr.domain.dto.request.ReqCreateProduct;
import org.com.pet_spr.domain.dto.request.ReqUpdateProduct;
import org.com.pet_spr.domain.dto.response.CommonResponseDto;
import org.com.pet_spr.domain.dto.response.ProductDto;
import org.com.pet_spr.domain.entity.*;
import org.com.pet_spr.domain.mapper.ProductMapper;
import org.com.pet_spr.domain.specification.FilterProcessor;
import org.com.pet_spr.domain.specification.SpecificationBuilder;
import org.com.pet_spr.exception.ConflictException;
import org.com.pet_spr.exception.NotFoundException;
import org.com.pet_spr.repository.CategoryRepository;
import org.com.pet_spr.repository.InventoryRepository;
import org.com.pet_spr.repository.ProductRepository;
import org.com.pet_spr.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final InventoryRepository inventoryRepository;

    public void checkExistProductByName(String name) {
        log.info("[CHECK] Kiểm tra trùng lặp tên sản phẩm: '{}'", name);
        if (productRepository.existsByName(name)) {
            log.warn("[CONFLICT] Tên sản phẩm '{}' đã tồn tại trong hệ thống", name);
            throw new ConflictException(ErrorMessage.Product.ERR_EXISTS_NAME, new String[]{name});
        }
    }

    @Override
    @Transactional
    public ProductDto createProduct(ReqCreateProduct reqCreateProduct) {
        log.info("[CREATE] Bắt đầu luồng tạo sản phẩm mới với tên: '{}'", reqCreateProduct.getName());

        // 1. Kiểm tra trùng tên
        checkExistProductByName(reqCreateProduct.getName());

        // 2. Map DTO sang Entity
        Product product = productMapper.toProduct(reqCreateProduct);
        log.info("[CREATE] Đã map dữ liệu request sang Entity Product");

        // 3. Xử lý Category
        if (reqCreateProduct.getCategoryId() != null) {
            log.info("[CREATE] Đang tìm kiếm Category liên kết với ID: {}", reqCreateProduct.getCategoryId());
            Category category = categoryRepository.findById(reqCreateProduct.getCategoryId())
                    .orElseThrow(() -> {
                        log.warn("[NOT_FOUND] Không tìm thấy Category ID: {}", reqCreateProduct.getCategoryId());
                        return new NotFoundException(ErrorMessage.Category.ERR_NOT_FOUND_ID,
                                new String[]{String.valueOf(reqCreateProduct.getCategoryId())});
                    });
            product.setCategory(category);
            log.info("[CREATE] Đã liên kết sản phẩm với Category: '{}'", category.getName());
        }

        // 4. Lưu Product TRƯỚC để sinh ra ID tự động
        product = productRepository.save(product);
        log.info("[CREATE] Lưu sản phẩm thành công. Đã sinh ra Product ID: {}", product.getId());

        // 5. Xử lý Inventory (Khởi tạo kho hàng)
        if (reqCreateProduct.getQuantity() != null) {
            log.info("[CREATE] Đang khởi tạo bản ghi Inventory với số lượng ban đầu: {}", reqCreateProduct.getQuantity());
            Inventory inventory = new Inventory();
            inventory.setQuantity(reqCreateProduct.getQuantity());
            inventory.setProduct(product);

            inventoryRepository.save(inventory);
            product.setInventory(inventory);
            log.info("[CREATE] Đã lưu Inventory thành công cho sản phẩm ID: {}", product.getId());
        }

        log.info("[CREATE] Hoàn thành tạo sản phẩm mới thành công (ID: {})", product.getId());
        return productMapper.toProductDto(product);
    }

    @Override
    @Transactional
    public ProductDto updateProduct(ReqUpdateProduct reqUpdateProduct) {
        log.info("[UPDATE] Bắt đầu luồng cập nhật sản phẩm có ID: {}", reqUpdateProduct.getId());

        Product updateProduct = productRepository.findById(reqUpdateProduct.getId()).orElseThrow(() -> {
            log.warn("[NOT_FOUND] Thất bại khi cập nhật, không tồn tại sản phẩm ID: {}", reqUpdateProduct.getId());
            return new NotFoundException(ErrorMessage.Product.ERR_NOT_FOUND_ID, new String[]{String.valueOf(reqUpdateProduct.getId())});
        });

        // Kiểm tra trùng tên chỉ khi tên mới khác tên cũ hiện tại
        if (!updateProduct.getName().equals(reqUpdateProduct.getName())) {
            log.info("[UPDATE] Phát hiện tên sản phẩm thay đổi từ '{}' sang '{}'", updateProduct.getName(), reqUpdateProduct.getName());
            checkExistProductByName(reqUpdateProduct.getName());
        }

        updateProduct.setName(reqUpdateProduct.getName());
        updateProduct.setDescription(reqUpdateProduct.getDescription());
        updateProduct.setPrice(reqUpdateProduct.getPrice());

        // Cập nhật Danh mục (Category)
        if (reqUpdateProduct.getCategoryId() != null) {
            log.info("[UPDATE] Đang cập nhật liên kết sang Category ID: {}", reqUpdateProduct.getCategoryId());
            Category category = categoryRepository.findById(reqUpdateProduct.getCategoryId()).orElseThrow(() -> {
                log.warn("[NOT_FOUND] Không tìm thấy Category ID: {}", reqUpdateProduct.getCategoryId());
                return new NotFoundException(ErrorMessage.Category.ERR_NOT_FOUND_ID, new String[]{String.valueOf(reqUpdateProduct.getCategoryId())});
            });
            updateProduct.setCategory(category);
        }

        // Cập nhật Số lượng kho (Inventory)
        if (reqUpdateProduct.getQuantity() != null) {
            Inventory inventory = updateProduct.getInventory();
            if (inventory != null && !reqUpdateProduct.getQuantity().equals(inventory.getQuantity())) {
                log.info("[UPDATE] Cập nhật số lượng kho hàng cũ từ {} sang {}", inventory.getQuantity(), reqUpdateProduct.getQuantity());
                inventory.setQuantity(reqUpdateProduct.getQuantity());
                inventoryRepository.save(inventory);
            } else if (inventory == null) {
                log.info("[UPDATE] Không tìm thấy bản ghi kho cũ, đang tạo mới Inventory với số lượng: {}", reqUpdateProduct.getQuantity());
                Inventory newInventory = new Inventory();
                newInventory.setQuantity(reqUpdateProduct.getQuantity());
                newInventory.setProduct(updateProduct);
                inventoryRepository.save(newInventory);
                updateProduct.setInventory(newInventory);
            }
        }

        productRepository.save(updateProduct);
        log.info("[UPDATE] Cập nhật thành công mọi thông tin cho sản phẩm ID: {}", updateProduct.getId());
        return productMapper.toProductDto(updateProduct);
    }

    @Override
    @Transactional
    public CommonResponseDto deleteProduct(Long id) {
        log.info("[DELETE] Bắt đầu yêu cầu xóa mềm sản phẩm có ID: {}", id);

        Product deleteProduct = productRepository.findById(id).orElseThrow(() -> {
            log.warn("[NOT_FOUND] Thất bại khi xóa, không tồn tại sản phẩm ID: {}", id);
            return new NotFoundException(ErrorMessage.Product.ERR_NOT_FOUND_ID, new String[]{String.valueOf(id)});
        });

        deleteProduct.setDeleteFlag(true);
        productRepository.save(deleteProduct);

        log.info("[DELETE] Đã bật deleteFlag = true thành công cho sản phẩm ID: {}", id);
        return new CommonResponseDto(true, "Delete product successfully");
    }

    @Override
    public ProductDto getProductById(Long id) {
        log.info("[GET_BY_ID] Đang truy vấn chi tiết sản phẩm ID: {}", id);

        Product product = productRepository.findById(id).orElseThrow(() -> {
            log.warn("[NOT_FOUND] Không tìm thấy sản phẩm có ID: {}", id);
            return new NotFoundException(ErrorMessage.Product.ERR_NOT_FOUND_ID, new String[]{String.valueOf(id)});
        });

        if (Boolean.TRUE.equals(product.getDeleteFlag())) {
            log.warn("[NOT_FOUND] Truy cập thất bại! Sản phẩm ID: {} đã bị xóa mềm trước đó", id);
            throw new NotFoundException(ErrorMessage.Product.ERR_NOT_FOUND_ID, new String[]{String.valueOf(id)});
        }

        log.info("[GET_BY_ID] Truy vấn thành công sản phẩm: '{}'", product.getName());
        return productMapper.toProductDto(product);
    }

    @Override
    public ResultPaginationDto getAllProduct(List<String> filter, Pageable pageable) {
        log.info("[GET_ALL] Yêu cầu lấy danh sách sản phẩm. Page: {}, Size: {}, Sort: {}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        if (filter != null && !filter.isEmpty()) {
            log.info("[GET_ALL] Áp dụng các bộ lọc tìm kiếm (Filter): {}", filter);
        }

        // Khởi tạo cấu trúc query động từ bộ lọc dữ liệu
        SpecificationBuilder<Product> specificationBuilder = new SpecificationBuilder<>();
        FilterProcessor.process(specificationBuilder, filter);

        Specification<Product> spec = specificationBuilder.build();
        Specification<Product> softDeleteSpec = (root, query, cb) -> cb.equal(root.get("deleteFlag"), false);

        // Gom các cấu trúc query lại thành câu lệnh kiểm tra cuối cùng
        Specification<Product> finalSpec = (spec == null) ? softDeleteSpec : spec.and(softDeleteSpec);

        // Thực thi gọi Database lấy dữ liệu phân trang
        Page<Product> pageUser = productRepository.findAll(finalSpec, pageable);
        log.info("[GET_ALL] Kết quả truy vấn Database: Tìm thấy {} bản ghi phù hợp. Tổng số trang: {}",
                pageUser.getTotalElements(), pageUser.getTotalPages());

        // Đóng gói ánh xạ dữ liệu đầu ra trả về Client
        ResultPaginationDto resultPaginationDTO = new ResultPaginationDto();
        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageUser.getTotalPages());
        meta.setTotal(pageUser.getTotalElements());

        List<ProductDto> result = productMapper.productDtos(pageUser.getContent());

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(result);

        log.info("[GET_ALL] Hoàn thành phân trang dữ liệu sản phẩm thành công.");
        return resultPaginationDTO;
    }
}