package com.enterprise.order.inventory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class InventoryService {
    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);
    private final InventoryRepository inventoryRepository;
    private final OrderReservationRepository reservationRepository;

    public InventoryService(InventoryRepository inventoryRepository, OrderReservationRepository reservationRepository) {
        this.inventoryRepository = inventoryRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public void reserveInventory(String orderId, String productId, Integer quantity) {
        log.info("Processing inventory reservation for order {} product {} quantity {}", orderId, productId, quantity);

        // Idempotency Check
        Optional<OrderReservation> existingRes = reservationRepository.findById(orderId);
        if (existingRes.isPresent()) {
            log.info("Reservation already processed for order {}. Status: {}", orderId, existingRes.get().getStatus());
            return;
        }

        // Check Inventory Stock
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found in inventory: " + productId));

        if (inventory.getStockCount() < quantity) {
            log.warn("Insufficient stock for product {}. Stock available: {}, Requested: {}",
                    productId, inventory.getStockCount(), quantity);
            
            // Save failed reservation log before rolling back (will be skipped on exception unless caught, but throwing rolls back everything)
            throw new IllegalStateException("Insufficient stock for product " + productId + ". Stock available: " + inventory.getStockCount());
        }

        // Deduct Stock and Reserve
        inventory.setStockCount(inventory.getStockCount() - quantity);
        inventoryRepository.save(inventory);

        OrderReservation reservation = new OrderReservation(orderId, "RESERVED", productId, quantity);
        reservationRepository.save(reservation);

        log.info("Stock reserved successfully for order {}. Remaining stock for product {}: {}",
                orderId, productId, inventory.getStockCount());
    }

    @Transactional
    public void releaseInventory(String orderId) {
        log.info("Compensating Transaction: Attempting to release inventory for order {}", orderId);
        reservationRepository.findById(orderId).ifPresent(reservation -> {
            if ("RESERVED".equals(reservation.getStatus())) {
                inventoryRepository.findByProductId(reservation.getProductId()).ifPresent(inventory -> {
                    inventory.setStockCount(inventory.getStockCount() + reservation.getQuantity());
                    inventoryRepository.save(inventory);
                    log.info("Compensating Transaction: Released {} units of product {} back to inventory",
                            reservation.getQuantity(), reservation.getProductId());
                });

                reservation.setStatus("RELEASED");
                reservationRepository.save(reservation);
                log.info("Compensating Transaction: Order reservation status updated to RELEASED");
            }
        });
    }
}
