package com.canteen.MenuManagementComponent;

import java.util.List;
import java.util.Optional;


public interface MenuItemRepository {
    Optional<MenuItem> findById(long id);
    List<MenuItem> findAllAvailable();
}