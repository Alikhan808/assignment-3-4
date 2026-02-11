package com.canteen;

import com.canteen.config.DbConfig;
import com.canteen.config.DbConnectionFactory;
import com.canteen.patterns.DeliveryFactory;
import com.canteen.patterns.DeliveryOption;
import com.canteen.jdbc.JdbcCustomerRepository;
import com.canteen.jdbc.JdbcMenuItemRepository;
import com.canteen.jdbc.JdbcOrderRepository;
import com.canteen.jdbc.JdbcOrderItemRepository;
import com.canteen.service.MenuService;
import com.canteen.service.OrderService;
import com.canteen.service.PaymentService;

import java.util.List;

public class App {

    static void main() {

        DbConfig cfg = new DbConfig(
                "jdbc:postgresql://aws-1-ap-southeast-2.pooler.supabase.com:5432/postgres",
                "postgres.lrdcyvldecniiuotppfh",
                ""
        );

        DbConnectionFactory db = new DbConnectionFactory(cfg);

        var customerRepo = new JdbcCustomerRepository(db);
        var menuRepo = new JdbcMenuItemRepository(db);
        var orderRepo = new JdbcOrderRepository(db);
        var orderItemRepo = new JdbcOrderItemRepository(db);

        var paymentService = new PaymentService();
        var menuService = new MenuService(menuRepo);
        var orderService = new OrderService(customerRepo, menuRepo, orderRepo, orderItemRepo, paymentService);

        var menuRes = menuService.getAvailableMenu();
        if (!menuRes.isSuccess()) {
            System.out.println("Failed to load menu: " + menuRes.getError());
            return;
        }
        System.out.println("Available menu: " + menuRes.getData());

        DeliveryOption option = DeliveryFactory.fromString("DELIVERY");

        var orderIdRes = orderService.placeOrder(
                1L,
                option,
                "Astana, Mangilik El 10",
                List.of(
                        new OrderService.ItemRequest(1L, 2),
                        new OrderService.ItemRequest(2L, 1)
                )
        );

        if (!orderIdRes.isSuccess()) {
            System.out.println("Order failed: " + orderIdRes.getError());
            return;
        }

        long orderId = orderIdRes.getData();
        System.out.println("Created orderId=" + orderId);

        var activeRes = orderService.viewActiveOrders();
        if (!activeRes.isSuccess()) {
            System.out.println("Failed to list active orders: " + activeRes.getError());
            return;
        }
        System.out.println("Active orders: " + activeRes.getData());
        orderService.markOrderCompleted(orderId);
        System.out.println("Order completed: " + orderId);
    }
}
