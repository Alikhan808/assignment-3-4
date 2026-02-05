package com.canteen.service;

import com.canteen.domain.MenuItem;
import com.canteen.dto.Result;

import java.util.List;

public class MenuService {
    private final MenuItemRepository menuRepo;

    public MenuService(MenuItemRepository menuRepo) {
        this.menuRepo = menuRepo;
    }

    public Result<List<MenuItem>> getAvailableMenu() {
        return Result.ok(menuRepo.findAllAvailable());
    }
}
