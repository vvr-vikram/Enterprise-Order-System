package com.enterprise.order.order;

import com.enterprise.order.common.OrderRequest;
import com.enterprise.order.inventory.InventoryService;
import com.enterprise.order.payment.PaymentService;
import com.enterprise.order.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final PaymentService paymentService;
    private final InventoryService inventoryService;

    public OrderService(OrderRepository orderRepository,
                        UserService userService,
                        PaymentService paymentService,
                        InventoryService inventoryService) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.paymentService = paymentService;
        this.inventoryService = inventoryService;
    }

    @Transactional
    public Order createOrder(OrderRequest request, String idempotencyKey) {
        log.info("Starting order placement process for user {} and product {}", request.getUserId(), request.getProductId());

        if (idempotencyKey == null || idempotencyKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Idempotency-Key header is required");
        }

        // 1. Idempotency Check
        Optional<Order> existingOrder = orderRepository.findByIdempotencyKey(idempotencyKey);
        if (existingOrder.isPresent()) {
            log.info("Duplicate request detected. Returning existing order {}", existingOrder.get().getId());
            return existingOrder.get();
        }

        // 2. Validate User
        userService.getUserById(request.getUserId());

        // 3. Save Order as PENDING
        String orderId = UUID.randomUUID().toString();
        Order order = new Order(
                orderId,
                request.getUserId(),
                "PENDING",
                request.getTotal(),
                idempotencyKey,
                request.getProductId(),
                request.getQuantity(),
                LocalDateTime.now()
        );
        order = orderRepository.save(order);
        log.info("Created PENDING order with ID {}", orderId);

        // 4. Process Payment (Synchronous service call)
        paymentService.processPayment(orderId, request.getTotal(), request.getProductId(), request.getQuantity());

        // 5. Reserve Inventory (Synchronous service call)
        inventoryService.reserveInventory(orderId, request.getProductId(), request.getQuantity());

        // 6. Complete Order
        order.setStatus("COMPLETED");
        Order completedOrder = orderRepository.save(order);
        log.info("Order {} successfully COMPLETED!", orderId);

        return completedOrder;
    }

    public Order getOrderById(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + id));
    }
}
