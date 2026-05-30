package org.com.pet_spr.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.pet_spr.constant.ErrorMessage;
import org.com.pet_spr.constant.TypeInventory;
import org.com.pet_spr.domain.dto.pagination.ResultPaginationDto;
import org.com.pet_spr.domain.dto.request.ReqAdjustProduct;
import org.com.pet_spr.domain.dto.request.ReqInventoryProduct;
import org.com.pet_spr.domain.dto.response.InventoryDto;
import org.com.pet_spr.domain.dto.response.InventoryTransactionDto;
import org.com.pet_spr.domain.entity.Inventory;
import org.com.pet_spr.domain.entity.InventoryTransaction;
import org.com.pet_spr.domain.entity.Product;
import org.com.pet_spr.domain.mapper.InventoryMapper;
import org.com.pet_spr.domain.mapper.InventoryTransactionMapper;
import org.com.pet_spr.domain.specification.FilterProcessor;
import org.com.pet_spr.domain.specification.InventoryTransactionSpec;
import org.com.pet_spr.domain.specification.SpecificationBuilder;
import org.com.pet_spr.exception.BadRequestException;
import org.com.pet_spr.exception.NotFoundException;
import org.com.pet_spr.repository.InventoryRepository;
import org.com.pet_spr.repository.InventoryTransactionRepository;
import org.com.pet_spr.repository.ProductRepository;
import org.com.pet_spr.service.InventoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final InventoryTransactionMapper mapper;
    private final InventoryMapper inventoryMapper;
    @Override
    @Transactional
    public InventoryTransactionDto importProduct(ReqInventoryProduct reqInventoryProduct) {

        // kiểm tra xem product có tồn tại không
        Product product = productRepository.findById(reqInventoryProduct.getProductId()).orElseThrow(
                () -> new NotFoundException(ErrorMessage.Product.ERR_NOT_FOUND_ID, new String[]{String.valueOf(reqInventoryProduct.getProductId())})
        );

        // Tìm iventory tương ứng với product
        Inventory inventory = inventoryRepository.findByProductId(reqInventoryProduct.getProductId()).orElseThrow(
                () -> new NotFoundException(ErrorMessage.Inventory.ERR_NOT_FOUND_ID, new String[]{String.valueOf(reqInventoryProduct.getProductId())})
        );

        // validate quantity
        if (reqInventoryProduct.getQuantity() <= 0){
            throw new BadRequestException(ErrorMessage.Inventory.ERR_INVALID_QUANTITY, new String[]{String.valueOf(reqInventoryProduct.getQuantity())});
        }

        // cộng quantity
        Integer oldQty = inventory.getQuantity();
        Integer newQty = oldQty + reqInventoryProduct.getQuantity();
        inventory.setQuantity(newQty);
        inventoryRepository.save(inventory);
        log.info("[IMPORT] Tồn kho thay đổi {} → {}", oldQty, newQty);

        // tạo inventory transaction
        InventoryTransaction inventoryTransaction = createInventoryTransaction(inventory, reqInventoryProduct.getQuantity(), TypeInventory.IMPORT, reqInventoryProduct.getNote());
        InventoryTransaction savedTransaction = inventoryTransactionRepository.save(inventoryTransaction);
        log.info("[IMPORT] Ghi transaction thành công | Product ID: {} | quantity: {}",
                reqInventoryProduct.getProductId(), reqInventoryProduct.getQuantity());

     InventoryTransactionDto inventoryTransactionDto = new InventoryTransactionDto();
//        inventoryTransactionDto.setQuantity(reqImportProduct.getQuantity());
//        inventoryTransactionDto.setCurrentStock(newQty);
//        inventoryTransactionDto.setType(TypeInventory.IMPORT.name());
//        inventoryTransactionDto.setNote(reqImportProduct.getNote());
//        inventoryTransactionDto.setCreatedBy(savedTransaction.getCreatedBy());
//        inventoryTransactionDto.setCreatedDate(savedTransaction.getCreatedDate());
//        inventoryTransactionDto.setLastModifiedBy(savedTransaction.getLastModifiedBy());
//        inventoryTransactionDto.setLastModifiedDate(savedTransaction.getLastModifiedDate());
        inventoryTransactionDto =  mapper.toInventoryTransactionDto(savedTransaction);
   //     inventoryTransactionDto.setCurrentStock(newQty);

        return inventoryTransactionDto;
    }

    @Override
    @Transactional
    public InventoryTransactionDto exportProduct(ReqInventoryProduct reqInventoryProduct) {
        Product product = productRepository.findById(reqInventoryProduct.getProductId()).orElseThrow(
                () -> new NotFoundException(ErrorMessage.Product.ERR_NOT_FOUND_ID, new String[]{String.valueOf(reqInventoryProduct.getProductId())})
        );
        Inventory inventory = inventoryRepository.findByProductId(reqInventoryProduct.getProductId()).orElseThrow(
                () -> new NotFoundException(ErrorMessage.Inventory.ERR_NOT_FOUND_ID, new String[]{String.valueOf(reqInventoryProduct.getProductId())})
        );

        if (reqInventoryProduct.getQuantity() <= 0){
            throw new BadRequestException(ErrorMessage.Inventory.ERR_INVALID_QUANTITY, new String[]{String.valueOf(reqInventoryProduct.getQuantity())});
        }

        // kiểm tra tồn kho đủ k
        Integer oldQty = inventory.getQuantity();
        if(oldQty < reqInventoryProduct.getQuantity()){
            log.info("[EXPORT] Tồn kho không đủ | Product ID: {} | quantity: {} | current stock: {}",
                    reqInventoryProduct.getProductId(), reqInventoryProduct.getQuantity(), oldQty);
            throw new BadRequestException(ErrorMessage.Inventory.ERR_NOT_ENOUGH_QUANTITY, new String[]{String.valueOf(oldQty)});
        }
        Integer newQty = oldQty - reqInventoryProduct.getQuantity();
        inventory.setQuantity(newQty);
        inventoryRepository.save(inventory);
        log.info("[EXPORT] Tồn kho thay đổi {} → {}", oldQty, newQty);

        InventoryTransaction inventoryTransaction = createInventoryTransaction(inventory, reqInventoryProduct.getQuantity(), TypeInventory.EXPORT, reqInventoryProduct.getNote());
        InventoryTransaction savedTransaction = inventoryTransactionRepository.save(inventoryTransaction);
        log.info("[EXPORT] Ghi transaction thành công | Product ID: {} | quantity: {}",
                reqInventoryProduct.getProductId(), reqInventoryProduct.getQuantity());
        InventoryTransactionDto inventoryTransactionDto = new InventoryTransactionDto();
        inventoryTransactionDto =  mapper.toInventoryTransactionDto(savedTransaction);
     //   inventoryTransactionDto.setCurrentStock(newQty);

        return inventoryTransactionDto;

    }

    @Override
    @Transactional
    public InventoryTransactionDto adjustProduct(ReqAdjustProduct reqAdjustProduct) {
        Product product = productRepository.findById(reqAdjustProduct.getProductId()).orElseThrow(
                () -> new NotFoundException(ErrorMessage.Product.ERR_NOT_FOUND_ID, new String[]{String.valueOf(reqAdjustProduct.getProductId())})
        );
        Inventory inventory = inventoryRepository.findByProductId(reqAdjustProduct.getProductId()).orElseThrow(
                () -> new NotFoundException(ErrorMessage.Inventory.ERR_NOT_FOUND_ID, new String[]{String.valueOf(reqAdjustProduct.getProductId())})
        );

        if (reqAdjustProduct.getNewQuantity() < 0){
            throw new BadRequestException(ErrorMessage.Inventory.ERR_INVALID_QUANTITY, new String[]{String.valueOf(reqAdjustProduct.getNewQuantity())});
        }
        Integer oldQty = inventory.getQuantity();
        Integer newQty = reqAdjustProduct.getNewQuantity();

        Integer delta = newQty - oldQty;

        if(delta == 0){
            log.info("[ADJUST] Số lượng không thay đổi | Product ID: {} | quantity: {}",
                    reqAdjustProduct.getProductId(), reqAdjustProduct.getNewQuantity());
            throw new BadRequestException(ErrorMessage.Inventory.ERR_ADJUST_QUANTITY_SAME_AS_CURRENT, new String[]{String.valueOf(reqAdjustProduct.getNewQuantity())});
        }

        // update inventory
        inventory.setQuantity(newQty);
        inventoryRepository.save(inventory);

        // tao inventory transaction
        String note =  ("[" + (delta > 0 ? "+" : "") + delta + "] " + reqAdjustProduct.getNote()); // VD: [+5] Kiểm kê tháng 6
        InventoryTransaction inventoryTransaction = createInventoryTransaction(inventory, Math.abs(delta), TypeInventory.ADJUST, note);
        InventoryTransaction savedTransaction = inventoryTransactionRepository.save(inventoryTransaction);

        InventoryTransactionDto transactionDto = mapper.toInventoryTransactionDto(savedTransaction);
     //   transactionDto.setCurrentStock(newQty);
        log.info("[ADJUST] Ghi transaction thành công | Product ID: {} | delta: {}",
                reqAdjustProduct.getProductId(), delta);
        return transactionDto;
    }

    @Override
    public InventoryDto getInventoryByProductId(Long productId) {
        log.info("[INVENTORY] Xem tồn kho cho Product ID: {}", productId);
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new NotFoundException(ErrorMessage.Product.ERR_NOT_FOUND_ID, new String[]{String.valueOf(productId)})
                );
        Inventory inventory = inventoryRepository.findByProductId(productId).orElseThrow(
                () -> new NotFoundException(ErrorMessage.Inventory.ERR_NOT_FOUND_ID, new String[]{String.valueOf(productId)})
        );
//        InventoryDto inventoryDto = new InventoryDto();
//        inventoryDto.setId(inventory.getId());
//        inventoryDto.setProductId(inventory.getProduct().getId());
//        inventoryDto.setProductName(inventory.getProduct().getName());
//        inventoryDto.setProductPrice(inventory.getProduct().getPrice());
//        inventoryDto.setQuantity(inventory.getQuantity());
        log.info("[INVENTORY] Product ID: {} | Tồn kho hiện tại: {}", productId, inventory.getQuantity());
        return inventoryMapper.toDto(inventory);
    }

    @Override
    public ResultPaginationDto getInventoryTransactionHistory(List<String> filter, Pageable pageable) {
        SpecificationBuilder<InventoryTransaction> specificationBuilder = new SpecificationBuilder<>();
        FilterProcessor.process(specificationBuilder,filter);

        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdDate").descending());
//        Specification<InventoryTransaction> spec = specificationBuilder.build();
//        spec.and(InventoryTransactionSpec.fromDate())
        Page<InventoryTransaction> page = inventoryTransactionRepository.findAll(specificationBuilder.build(), pageable);

        ResultPaginationDto resultPaginationDto = new ResultPaginationDto();
        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        List<InventoryTransactionDto> dtoList = mapper.toListInventoryTransaction(page.getContent());
        resultPaginationDto.setMeta(meta);
        resultPaginationDto.setResult(dtoList);
        return resultPaginationDto;
    }

    private InventoryTransaction createInventoryTransaction(Inventory inventory, Integer quantity, TypeInventory type, String note){
        InventoryTransaction inventoryTransaction = new InventoryTransaction();
        inventoryTransaction.setQuantity(quantity);
        inventoryTransaction.setType(type);
        inventoryTransaction.setNote(note);
        inventoryTransaction.setInventory(inventory);
        return inventoryTransactionRepository.save(inventoryTransaction);
    }
}
