package org.com.pet_spr.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.pet_spr.constant.ErrorMessage;
import org.com.pet_spr.constant.OrderStatus;
import org.com.pet_spr.constant.PaymentStatus;
import org.com.pet_spr.constant.TypeInventory;
import org.com.pet_spr.domain.dto.pagination.ResultPaginationDto;
import org.com.pet_spr.domain.dto.request.ReqCreateOrderBuyNow;
import org.com.pet_spr.domain.dto.request.ReqCreateOrderFromCart;
import org.com.pet_spr.domain.dto.request.ReqUpdateOrderStatus;
import org.com.pet_spr.domain.dto.response.OrderDto;
import org.com.pet_spr.domain.dto.response.OrderStatusHistoryDto;
import org.com.pet_spr.domain.entity.*;
import org.com.pet_spr.domain.entity.OrderStatusHistory;
import org.com.pet_spr.domain.mapper.OrderDetailMapper;
import org.com.pet_spr.domain.mapper.OrderMapper;
import org.com.pet_spr.domain.mapper.ShippingAddressMapper;
import org.com.pet_spr.domain.specification.FilterProcessor;
import org.com.pet_spr.domain.specification.SpecificationBuilder;
import org.com.pet_spr.exception.BadRequestException;
import org.com.pet_spr.exception.ForbiddenException;
import org.com.pet_spr.exception.NotFoundException;
import org.com.pet_spr.repository.*;
import org.com.pet_spr.service.OrderService;
import org.com.pet_spr.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final OrderMapper orderMapper;
    private final CartRepository cartRepository;
    private final ShippingAddressRepository shippingAddressRepository;
    private final InventoryRepository inventoryRepository;
    private final ShippingAddressMapper shippingAddressMapper;


    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final ProductRepository productRepository;
    private final OrderDetailMapper orderDetailMapper;

    @Override
    @Transactional
    public OrderDto createOrderFromCart(ReqCreateOrderFromCart req) {
        log.info("[ORDER] Bắt đầu tạo đơn hàng từ giỏ hàng");

        // 1. Lấy user hiện tại
        User currentUser = userService.getUserLogin();

        // Lấy giỏ hàng của người dùng
        Cart cart = cartRepository.findByUserId(currentUser.getId()).orElseThrow(
                () -> new NotFoundException(ErrorMessage.Cart.ERR_CARD_NOT_FOUND)
        );


        List<CartItem> selectedItems = cart.getCartItems().stream()
                .filter(item -> req.getCartItemIds().contains(item.getId()))
                .toList();

        if (selectedItems.isEmpty()) {
            throw new BadRequestException(ErrorMessage.Cart.ERR_NO_SELECTED_ITEMS);
        }

        // 3. Lấy địa chỉ giao hàng
        ShippingAddress shippingAddress = shippingAddressRepository.findById(req.getAddressId()).orElseThrow(
                () -> new NotFoundException(ErrorMessage.ShippingAddress.ERR_NOT_FOUND_ID, new String[]{String.valueOf(req.getAddressId())})
        );
        if (!shippingAddress.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException(ErrorMessage.FORBIDDEN);
        }


        // 4. Kiểm tra tồn kho từng sản phẩm
        for (CartItem item : selectedItems) {
            Inventory inventory = inventoryRepository.findByProductId(item.getProduct().getId()).orElseThrow(
                    () -> new NotFoundException(ErrorMessage.Inventory.ERR_NOT_FOUND_ID, new String[]{String.valueOf(item.getProduct().getId())})
            );
            if (inventory.getQuantity() < item.getQuantity()) {
                throw new NotFoundException(ErrorMessage.Inventory.ERR_NOT_ENOUGH_QUANTITY, new String[]{String.valueOf(inventory.getQuantity())});
            }
        }

        // 5. Tạo Order
        Order order = new Order();
        order.setUser(currentUser);
        order.setShippingName(shippingAddress.getFullName());
        order.setShippingPhone(shippingAddress.getPhone());
        order.setShippingAddressFull(shippingAddressMapper.buildFullAddress(shippingAddress));
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDetails(new ArrayList<>());
        order.setStatusHistory(new ArrayList<>());

        // 6. Tạo OrderDetail + tính totalAmount
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem item : cart.getCartItems()) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(item.getProduct());
            orderDetail.setQuantity(item.getQuantity());
            orderDetail.setUnitPrice(item.getProduct().getPrice());

            order.getOrderDetails().add(orderDetail);


            totalAmount = totalAmount.add(
                    item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity()))
            );
        }
        order.setTotalAmount(totalAmount);

        // 7. Tạo Payment
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(req.getPaymentMethod());
        payment.setAmount(totalAmount);
        payment.setStatus(PaymentStatus.PENDING);

        order.setPayment(payment);

        // 8. Tạo OrderStatusHistory
        OrderStatusHistory orderStatusHistory = new OrderStatusHistory();
        orderStatusHistory.setOrder(order);
        orderStatusHistory.setStatus(OrderStatus.PENDING);
        orderStatusHistory.setNote("This order has been just created");
        orderStatusHistory.setChangedAt(LocalDateTime.now());

        order.getStatusHistory().add(orderStatusHistory);


        // 9. Save Order → cascade tự save OrderDetail + StatusHistory + Payment
        orderRepository.save(order);


        // // 10. Trừ tồn kho → ghi InventoryTransaction
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            Inventory inventory = inventoryRepository.findByProductId(orderDetail.getProduct().getId()).get();
            Integer oldQuantity = inventory.getQuantity();
            Integer newQuantity = oldQuantity - orderDetail.getQuantity();
            inventory.setQuantity(newQuantity);
            inventoryRepository.save(inventory);

            InventoryTransaction inventoryTransaction = new InventoryTransaction();
            inventoryTransaction.setInventory(inventory);
            inventoryTransaction.setQuantity(orderDetail.getQuantity());
            inventoryTransaction.setType(TypeInventory.EXPORT);
            inventoryTransaction.setNote("Export product to order");
            inventoryTransactionRepository.save(inventoryTransaction);

        }

        // 11. Xóa giỏ hàng
        cart.getCartItems().removeAll(selectedItems);
        cartRepository.save(cart);
        log.info("[CART] Xóa {} item đã mua khỏi giỏ hàng", selectedItems.size());

        return orderMapper.toDto(order);
    }

    @Override
    public OrderDto createOrderFromBuyNow(ReqCreateOrderBuyNow req) {
        log.info("[ORDER] Bắt đầu tạo đơn hàng mua ngay | Product ID: {}", req.getProductId());

        // Lấy user hiện tại
        User curentUser = userService.getUserLogin();

        // Kiểm tra product tồn tại
        Product product = productRepository.findById(req.getProductId()).orElseThrow(
                () -> new NotFoundException(ErrorMessage.Product.ERR_NOT_FOUND_ID, new String[]{String.valueOf(req.getProductId())})
        );

        //Validate quantity
        if (req.getQuantity() <= 0) {
            throw new BadRequestException("Số lượng phải lớn hơn 0");
        }


        //Kiểm tra tồn kho
        Inventory inventory = inventoryRepository.findByProductId(product.getId()).orElseThrow(
                () -> new NotFoundException(ErrorMessage.Inventory.ERR_NOT_FOUND_ID)
        );

        if (inventory.getQuantity() < req.getQuantity()) {
            throw new NotFoundException(ErrorMessage.Inventory.ERR_NOT_ENOUGH_QUANTITY, new String[]{String.valueOf(product.getInventory().getQuantity())});
        }

        //Lấy địa chỉ giao hàng
        ShippingAddress shippingAddress = shippingAddressRepository.findById(req.getAddressId()).orElseThrow(
                () -> new NotFoundException(ErrorMessage.ShippingAddress.ERR_NOT_FOUND_ID, new String[]{String.valueOf(req.getAddressId())})
        );

        if (!shippingAddress.getUser().getId().equals(curentUser.getId())) {
            throw new ForbiddenException(ErrorMessage.FORBIDDEN);
        }


        //Tính totalAmount
        BigDecimal totalAmount = product.getPrice()
                .multiply(new BigDecimal(req.getQuantity()));

        //Tạo Payment
        Payment payment = new Payment();
        //    payment.setOrder(order); payment không cần set order vì
        //    // nguyên tắc bên nào giữ FK -> bên đó Set.
        //    // 1-11-1. Payment là bên không giữ FK, nên chỉ cần set order.setPayment(payment)
        //    là đủ để thiết lập quan hệ
        payment.setPaymentMethod(req.getPaymentMethod());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setAmount(totalAmount);

        //Tạo Order
        Order order = new Order();
        order.setUser(curentUser);
        order.setShippingName(shippingAddress.getFullName());
        order.setShippingPhone(shippingAddress.getPhone());
        order.setShippingAddressFull(shippingAddressMapper.buildFullAddress(shippingAddress));
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(totalAmount);
        order.setPayment(payment);
        order.setOrderDetails(new ArrayList<>());
        order.setStatusHistory(new ArrayList<>());
        order.setPayment(payment);

        //Tạo OrderDetail
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setProduct(product);
        orderDetail.setOrder(order);
        orderDetail.setQuantity(req.getQuantity());
        orderDetail.setUnitPrice(product.getPrice());
        order.getOrderDetails().add(orderDetail);

        //Tạo OrderStatusHistory
        OrderStatusHistory orderStatusHistory = new OrderStatusHistory();
        orderStatusHistory.setOrder(order);
        orderStatusHistory.setStatus(OrderStatus.PENDING);
        orderStatusHistory.setNote("This order has been just created");
        orderStatusHistory.setChangedAt(LocalDateTime.now());
        order.getStatusHistory().add(orderStatusHistory);

        // 11. Save Order → cascade tự save OrderDetail + StatusHistory + Payment
        orderRepository.save(order);
        log.info("[ORDER] Tạo đơn hàng mua ngay thành công | Order ID: {}", order.getId());


        //Trừ tồn kho → ghi InventoryTransaction
        Integer oldQty = inventory.getQuantity();
        Integer newQty = oldQty - req.getQuantity();
        inventory.setQuantity(newQty);
        inventoryRepository.save(inventory);

        InventoryTransaction inventoryTransaction = new InventoryTransaction();
        inventoryTransaction.setInventory(inventory);
        inventoryTransaction.setQuantity(req.getQuantity());
        inventoryTransaction.setType(TypeInventory.EXPORT);
        inventoryTransaction.setNote("Export product to order");
        inventoryTransactionRepository.save(inventoryTransaction);

        log.info("[INVENTORY] Trừ kho Product ID: {} | {} → {}",
                req.getProductId(), oldQty, newQty);

        return orderMapper.toDto(order);
    }

    @Override
    public ResultPaginationDto getMyOrders(OrderStatus orderStatus, Pageable pageable) {
        log.info("[ORDER] Lấy danh sách đơn hàng | Status: {}", orderStatus);
        User currentUser = userService.getUserLogin();

        pageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("createdDate").descending());

        Page<Order> pageOrder = orderStatus != null
                ? orderRepository.findByUserIdAndStatus(currentUser.getId(), orderStatus, pageable)
                : orderRepository.findByUserId(currentUser.getId(), pageable);

        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageOrder.getTotalPages());
        meta.setTotal(pageOrder.getTotalElements());

        ResultPaginationDto data = new ResultPaginationDto();
        data.setMeta(meta);

        List<OrderDto> orderDtoList = orderMapper.toDtoList(pageOrder.getContent());

        data.setResult(orderDtoList);

        log.info("[ORDER] User ID: {} | Status: {} | Tổng: {}",
                currentUser.getId(), orderStatus, pageOrder.getTotalElements());
        return data;
    }

    @Override
    public OrderDto getOrderDetail(Long orderId) {
        log.info("[ORDER] Xem chi tiết đơn hàng ID: {}", orderId);

        //Tìm Order
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new NotFoundException(ErrorMessage.Order.ERR_NOT_FOUND_ID, new String[]{String.valueOf(orderId)}));

        //  Kiểm tra order có thuộc về user hiện tại không
        User currentUser = userService.getUserLogin();
        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException(ErrorMessage.FORBIDDEN);
        }

        return orderMapper.toDto(order);

    }

    @Override
    @Transactional
    public OrderDto cancelOrder(Long orderId) {
        // 1. Tìm Order
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new NotFoundException(ErrorMessage.Order.ERR_NOT_FOUND_ID, new String[]{String.valueOf(orderId)}));
        // 2. Kiểm tra order có thuộc về user hiện tại không
        User currentUser = userService.getUserLogin();
        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException(ErrorMessage.FORBIDDEN);
        }

        // 3. Kiểm tra trạng thái
        if (!order.getStatus().equals(OrderStatus.PENDING)) {
            throw new BadRequestException(ErrorMessage.Order.ERR_ORDER_NOT_PENDING);
        }


        // 4. Cộng lại tồn kho → ghi InventoryTransaction
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            Inventory inventory = inventoryRepository.findByProductId(orderDetail.getProduct().getId()).orElseThrow(
                    () -> new NotFoundException(ErrorMessage.Inventory.ERR_NOT_FOUND_ID, new String[]{String.valueOf(orderDetail.getProduct().getId())})
            );
            Integer oldQuantity = inventory.getQuantity();
            Integer newQuantity = oldQuantity + orderDetail.getQuantity();
            inventory.setQuantity(newQuantity);
            inventoryRepository.save(inventory);

            InventoryTransaction inventoryTransaction = new InventoryTransaction();
            inventoryTransaction.setInventory(inventory);
            inventoryTransaction.setQuantity(orderDetail.getQuantity());
            inventoryTransaction.setType(TypeInventory.IMPORT);
            inventoryTransaction.setNote("Import product from cancel order");
            inventoryTransactionRepository.save(inventoryTransaction);

            log.info("[INVENTORY] Nhập kho do hủy đơn hàng | Product ID: {} | {} → {}",
                    orderDetail.getProduct().getId(), oldQuantity, newQuantity);
        }

        // 5. Update status Order → CANCELLED
        order.setStatus(OrderStatus.CANCELLED);


        // 6. Update PaymentStatus
        if (order.getPayment() != null) {
            PaymentStatus currentPayMentStatus = order.getPayment().getStatus();
            if (currentPayMentStatus.equals(PaymentStatus.PENDING)) {
                // Chưa thanh toán → set FAILED
                order.getPayment().setStatus(PaymentStatus.FAILED);
            } else if (currentPayMentStatus.equals(PaymentStatus.SUCCESS)) {
                // Đã thanh toán rồi → set REFUNDED (admin sẽ hoàn tiền sau)
                order.getPayment().setStatus(PaymentStatus.REFUNDED);
            }
        }
        // 7. Thêm OrderStatusHistory
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus(OrderStatus.CANCELLED);
        history.setNote("Client cancel order");
        history.setChangedAt(LocalDateTime.now());
        order.getStatusHistory().add(history);


        orderRepository.save(order);
        log.info("[ORDER] Hủy đơn hàng thành công | Order ID: {}", orderId);

        return orderMapper.toDto(order);
    }

    public void validateStatusTransaction(OrderStatus current, OrderStatus next) {
        Map<OrderStatus, List<OrderStatus>> validTransactions = Map.of(
                OrderStatus.PENDING, List.of(OrderStatus.PROCESSING, OrderStatus.CANCELLED),
                OrderStatus.PROCESSING, List.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED),
                OrderStatus.SHIPPED, List.of(OrderStatus.DELIVERED),
                OrderStatus.DELIVERED, List.of(),
                OrderStatus.CANCELLED, List.of()

        );
        // map.getOrDefault()
        //Lấy value theo key, nếu key không tồn tại thì trả về giá trị mặc định.
        // neu current không có trong validTransactions thì trả về list rỗng
        // neu current có trong validTransactions thì trả về list status có thể chuyển sang
        List<OrderStatus> allowed = validTransactions.getOrDefault(current, List.of());
        if (!allowed.contains(next)) {
            throw new BadRequestException(String.format("Cannot change status from %s → %s", current, next));
        }
    }

    @Override
    @Transactional
    public OrderDto updateOrderStatus(ReqUpdateOrderStatus req) {
        log.info("[ORDER] Cập nhật trạng thái đơn hàng ID: {} → {}",
                req.getOrderId(), req.getStatus());

        // 1. Tìm Order
        Order order = orderRepository.findById(req.getOrderId()).orElseThrow(
                () -> new NotFoundException(ErrorMessage.Order.ERR_NOT_FOUND_ID, new String[]{String.valueOf(req.getOrderId())})
        );

        // 2. Kiểm tra chuyển trạng thái hợp lệ không
        validateStatusTransaction(order.getStatus(), req.getStatus());

        //  3. Nếu Admin hủy đơn → hoàn kho
        if (req.getStatus().equals(OrderStatus.CANCELLED)) {
            // Nếu chuyển sang CANCELLED thì phải cộng lại tồn kho và ghi InventoryTransaction
            for (OrderDetail orderDetail : order.getOrderDetails()) {
                Inventory inventory = inventoryRepository.findByProductId(orderDetail.getProduct().getId()).orElseThrow(
                        () -> new NotFoundException(ErrorMessage.Inventory.ERR_NOT_FOUND_ID, new String[]{String.valueOf(orderDetail.getProduct().getId())})
                );
                Integer oldQuantity = inventory.getQuantity();
                Integer newQuantity = oldQuantity + orderDetail.getQuantity();
                inventory.setQuantity(newQuantity);
                inventoryRepository.save(inventory);

                InventoryTransaction inventoryTransaction = new InventoryTransaction();
                inventoryTransaction.setInventory(inventory);
                inventoryTransaction.setQuantity(orderDetail.getQuantity());
                inventoryTransaction.setType(TypeInventory.IMPORT);
                inventoryTransaction.setNote("Import product from cancel order by admin");
                inventoryTransactionRepository.save(inventoryTransaction);

                log.info("[INVENTORY] Nhập kho do hủy đơn hàng | Product ID: {} | {} → {}",
                        orderDetail.getProduct().getId(), oldQuantity, newQuantity);
            }
        }

        // 4. Update PaymentStatus tương ứng
        if (order.getPayment() != null) {
            switch (req.getStatus()) {
                case DELIVERED:
                    order.getPayment().setStatus(PaymentStatus.SUCCESS);
                    break;
                case CANCELLED:
                    PaymentStatus paymentStatus = order.getPayment().getStatus();
                    if (paymentStatus.equals(PaymentStatus.SUCCESS)) {
                        order.getPayment().setStatus(PaymentStatus.REFUNDED);
                    } else {
                        order.getPayment().setStatus(PaymentStatus.FAILED);
                    }
                    break;
                default:
                    break;
            }
        }

        // 5. Update status Order
        order.setStatus(req.getStatus());

        // 6. Thêm OrderStatusHistory
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus(req.getStatus());
        history.setNote(req.getNote() != null ? req.getNote() : "Admin cập nhật trạng thái");
        history.setChangedAt(LocalDateTime.now());
        order.getStatusHistory().add(history);

        orderRepository.save(order);
        log.info("[ORDER] Cập nhật trạng thái thành công | Order ID: {} | Status: {}",
                req.getOrderId(), req.getStatus());

        return orderMapper.toDto(order);
    }

    @Override
    public ResultPaginationDto getAllOrders(List<String> filter, Pageable pageable) {
        SpecificationBuilder<Order> specificationBuilder = new SpecificationBuilder<>();
        FilterProcessor.process(specificationBuilder, filter);

        Page<Order> pageOrder = orderRepository.findAll(specificationBuilder.build(), pageable);

        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageOrder.getTotalPages());
        meta.setTotal(pageOrder.getTotalElements());

        ResultPaginationDto data = new ResultPaginationDto();
        data.setMeta(meta);
        data.setResult(orderMapper.toDtoList(pageOrder.getContent()));
        return data;

    }

    @Override
    public OrderDto getOrderDetailAdmin(Long orderId) {
        log.info("[ADMIN-ORDER] Xem chi tiết đơn hàng ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("[NOT_FOUND] Không tìm thấy đơn hàng ID: {}", orderId);
                    return new NotFoundException("Không tìm thấy đơn hàng ID: " + orderId);
                });

        log.info("[ADMIN-ORDER] Xem chi tiết thành công | Order ID: {}", orderId);
        return orderMapper.toDto(order);
    }

    @Override
    public List<OrderStatusHistoryDto> getOrderStatusHistory(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new NotFoundException(ErrorMessage.Order.ERR_NOT_FOUND_ID, new String[]{String.valueOf(orderId)})
        );

        User currentUser = userService.getUserLogin();
        if(!order.getUser().getId().equals(currentUser.getId())){
            throw new ForbiddenException(ErrorMessage.FORBIDDEN);
        }
        List<OrderStatusHistory> statusHistoryList = order.getStatusHistory();
        List<OrderStatusHistoryDto> dtos = statusHistoryList.stream().map(
                statusHistory -> new OrderStatusHistoryDto(
                        statusHistory.getId(),
                        statusHistory.getStatus(),
                        statusHistory.getNote(),
                        statusHistory.getChangedAt()
                )
        ).toList();

        return dtos;
    }

}
