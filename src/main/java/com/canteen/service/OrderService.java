package com.canteen.service;

import com.canteen.BillingComponent.PaymentService;
import com.canteen.MenuManagementComponent.MenuItem;
import com.canteen.OrderingComponent.Order;
import com.canteen.OrderingComponent.OrderItem;
import com.canteen.OrderingComponent.OrderStatus;
import com.canteen.BillingComponent.TaxConfig;
import com.canteen.BillingComponent.Result;
import com.canteen.exceptions.InvalidQuantityException;
import com.canteen.exceptions.MenuItemNotAvailableException;
import com.canteen.exceptions.OrderNotFoundException;
import com.canteen.OrderingComponent.CustomerRepository;
import com.canteen.MenuManagementComponent.MenuItemRepository;
import com.canteen.OrderingComponent.OrderItemRepository;
import com.canteen.OrderingComponent.OrderRepository;
import com.canteen.DeliveryComponent.DeliveryOption;


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

    public Result<Long> placeOrder(long customerId,
                                   DeliveryOption deliveryOption,
                                   String deliveryAddress,
                                   List<ItemRequest> requestedItems) {

        if (requestedItems == null || requestedItems.isEmpty()) {
            return Result.fail("Order must contain at least 1 item");
        }

        customerRepo.findById(customerId).orElseThrow(() ->
                new IllegalArgumentException("Customer not found: " + customerId));

        List<OrderItem> itemsToInsert = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        requestedItems.forEach(req -> {
            if (req.quantity() <= 0) {
                throw new InvalidQuantityException("Invalid quantity for menuItemId=" + req.menuItemId());
            }

            MenuItem mi = menuRepo.findById(req.menuItemId()).orElseThrow(() ->
                    new IllegalArgumentException("Menu item not found: " + req.menuItemId()));

            if (!mi.available()) {
                throw new MenuItemNotAvailableException("Menu item not available: " + mi.name());
            }

            itemsToInsert.add(new OrderItem(0, 0, mi.id(), req.quantity(), mi.price()));
        });

        subtotal = itemsToInsert.stream()
                .map(oi -> oi.unitPrice().multiply(BigDecimal.valueOf(oi.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tax = subtotal.multiply(TaxConfig.getInstance().getTaxRate());
        BigDecimal total = subtotal.add(tax).setScale(2, RoundingMode.HALF_UP);

        paymentService.pay(customerId, total);

        Order.Builder builder = new Order.Builder()
                .customerId(customerId)
                .type(deliveryOption.type());

        deliveryOption.apply(builder, deliveryAddress);

        itemsToInsert.forEach(builder::addItem);

        Order order = builder.build();

        long orderId = orderRepo.create(order);

        for (OrderItem oi : itemsToInsert) {
            OrderItem dbItem = new OrderItem(0, orderId, oi.menuItemId(), oi.quantity(), oi.unitPrice());
            orderItemRepo.addItem(dbItem);
        }

        return Result.ok(orderId);
    }

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


    public record ItemRequest(long menuItemId, int quantity) {}
}
