package com.canteen.repository;

import com.canteen.domain.MenuItem;
import java.util.List;
import java.util.Optional;


public interface MenuItemRepository {
    Optional<MenuItem> findById(long id);
    List<MenuItem> findAllAvailable();
}