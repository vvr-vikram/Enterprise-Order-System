package com.enterprise.order.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderReservationRepository extends JpaRepository<OrderReservation, String> {
}
