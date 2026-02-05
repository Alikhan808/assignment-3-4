package com.canteen.service;

import com.canteen.config.TaxConfig;
import com.canteen.domain.*;
import com.canteen.dto.Result;
import com.canteen.exceptions.InvalidQuantityException;
import com.canteen.exceptions.MenuItemNotAvailableException;
import com.canteen.exceptions.OrderNotFoundException;
import com.canteen.repository.CustomerRepository;
import com.canteen.repository.MenuItemRepository;
import com.canteen.repository.OrderItemRepository;
import com.canteen.repository.OrderRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class OrderService {
    private final CustomerRepository customerRepo;
    private final MenuItemRepository menuRepo;
    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;
    private final PaymentService paymentService;

    public OrderService(CustomerRepository customerRepo,
                        MenuItemRepository menuRepo,
                        OrderRepository orderRepo,
                        OrderItemRepository orderItemRepo,
                        PaymentService paymentService) {
        this.customerRepo = customerRepo;
        this.menuRepo = menuRepo;
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
        this.paymentService = paymentService;
    }

    //User story: place an order
    public Result<Long> placeOrder(long customerId, OrderType type, String deliveryAddress, List<ItemRequest> requestedItems) {
        if (requestedItems == null || requestedItems.isEmpty()) {
            return Result.fail("Order must contain at least 1 item");
        }

        customerRepo.findById(customerId).orElseThrow(() ->
                new IllegalArgumentException("Customer not found: " + customerId));

        // 1) validate + build items
        List<OrderItem> itemsToInsert = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (ItemRequest req : requestedItems) {
            if (req.quantity() <= 0) throw new InvalidQuantityException("Invalid quantity for menuItemId=" + req.menuItemId());

            MenuItem mi = menuRepo.findById(req.menuItemId()).orElseThrow(() ->
                    new IllegalArgumentException("Menu item not found: " + req.menuItemId()));

            if (!mi.available()) throw new MenuItemNotAvailableException("Menu item not available: " + mi.name());

            subtotal = subtotal.add(mi.price().multiply(BigDecimal.valueOf(req.quantity())));
            itemsToInsert.add(new OrderItem(0, 0, mi.id(), req.quantity(), mi.price()));
        }

        // 2) tax rules (Singleton)
        BigDecimal tax = subtotal.multiply(TaxConfig.getInstance().getTaxRate());
        BigDecimal total = subtotal.add(tax).setScale(2, RoundingMode.HALF_UP);

        // 3) "pay"
        paymentService.pay(customerId, total);

        // 4) create order header via Builder
        Order.Builder builder = new Order.Builder()
                .customerId(customerId)
                .type(type);

        if (type == OrderType.DELIVERY) {
            builder.deliveryAddress(deliveryAddress);
        }

        for (OrderItem oi : itemsToInsert) {
            builder.addItem(oi);
        }

        Order order = builder.build();

        // 5) persist order + items
        long orderId = orderRepo.create(order);

        for (OrderItem oi : itemsToInsert) {
            OrderItem dbItem = new OrderItem(0, orderId, oi.menuItemId(), oi.quantity(), oi.unitPrice());
            orderItemRepo.addItem(dbItem);
        }

        return Result.ok(orderId);
    }

    //User story: view active orders
    public Result<List<Order>> viewActiveOrders() {
        List<Order> shells = orderRepo.findByStatus(OrderStatus.ACTIVE);

        List<Order> result = new ArrayList<>();
        for (Order shell : shells) {
            List<OrderItem> items = orderItemRepo.findByOrderId(shell.getId());
            Order rebuilt = rebuild(shell, items);
            result.add(rebuilt);
        }
        return Result.ok(result);
    }

    // -------- User story: mark order as completed ----------
    public void markOrderCompleted(long orderId) {
        Order shell = orderRepo.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));

        if (shell.getStatus() == OrderStatus.COMPLETED) {
            return;
        }

        orderRepo.updateStatus(orderId, OrderStatus.COMPLETED);
    }

    private Order rebuild(Order shell, List<OrderItem> items) {
        Order.Builder b = new Order.Builder()
                .customerId(shell.getCustomerId())
                .type(shell.getType())
                .deliveryAddress(shell.getDeliveryAddress());

        for (OrderItem oi : items) b.addItem(oi);

        Order built = b.build();
        built.setId(shell.getId());
        built.setStatus(shell.getStatus());
        return built;
    }

    // DTO for requests
    public record ItemRequest(long menuItemId, int quantity) {}
}
