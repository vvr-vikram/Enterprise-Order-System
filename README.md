# Enterprise Order Application

This is a consolidated, single-process version of the **Enterprise Order System**, combining the Order, Payment, Inventory, and User domains into a modular Spring Boot application. 

It is designed to run locally in **Eclipse** with zero external dependencies (no Kafka or API Gateway brokers needed) and leverages local ACID database transactions instead of distributed Sagas.

---

## Architecture Overview

Instead of coordinating distributed actions via a message broker, this unified application manages domain execution in-process:

```
+--------------------------------------------------------------+
|                  Enterprise Order Application                |
|                          (Port 8080)                         |
+--------------------------------------------------------------+
     |                  |                   |             |
     v                  v                   v             v
[User Domain]    [Order Domain]     [Payment Domain]  [Inventory Domain]
     |                  |                   |             |
     +------------------+---------+---------+-------------+
                                  |
                                  v
                    MySQL Database (enterprise_order_db)
```

1. **User Domain**: Stores and validates user accounts.
2. **Order Domain**: Manages order creation, lifecycle, and client-side idempotency.
3. **Payment Domain**: Simulates payment debits and processes payment logs.
4. **Inventory Domain**: Tracks product stock and handles reservations.

### ACID Transaction Flow
When a client sends a `POST /orders` request:
1. `OrderService` checks the `Idempotency-Key` header.
2. If it's a new request, it starts a database transaction:
   * Creates a `PENDING` order.
   * Calls `PaymentService.processPayment(...)` to process the payment.
   * Calls `InventoryService.reserveInventory(...)` to decrement product stock.
   * Updates the order status to `COMPLETED`.
3. If any step fails (e.g. payment limit exceeded or out-of-stock), an exception is thrown, causing the database to automatically roll back all order, payment, and inventory modifications.

---

## Setup & Running in Eclipse

### Prerequisites
1. **Java 17** or higher.
2. **MySQL Server** running locally on port `3306` (Default user: `root`, password: `1254`).

### Steps to Run
1. Open Eclipse.
2. In the Package Explorer, navigate to `src/main/java/com/enterprise/order/EnterpriseOrderApplication.java`.
3. Right-click the file and select **Run As** -> **Java Application**.
4. The application will start on port `8080`.
5. The database schema `enterprise_order_db` and tables will be created automatically, and mock data will be seeded.

---

## API Endpoints

### 1. Fetch User Info
* **Method**: `GET`
* **URL**: `http://localhost:8080/users/{userId}`
* **Example**: `http://localhost:8080/users/user-123`

### 2. Create Order
* **Method**: `POST`
* **URL**: `http://localhost:8080/orders`
* **Headers**:
  * `Idempotency-Key`: `unique-uuid-key`
  * `Content-Type`: `application/json`
* **Body**:
  ```json
  {
    "userId": "user-123",
    "total": 250.00,
    "productId": "prod-101",
    "quantity": 2
  }
  ```

### 3. Fetch Order Status
* **Method**: `GET`
* **URL**: `http://localhost:8080/orders/{orderId}`

### 4. Process Payment (Direct)
* **Method**: `POST`
* **URL**: `http://localhost:8080/payments`
* **Headers**:
  * `Content-Type`: `application/json`
* **Body**:
  ```json
  {
    "orderId": "order-uuid-here",
    "amount": 250.00,
    "productId": "prod-101",
    "quantity": 2
  }
  ```
