package com.canteen.config;

import java.util.ResourceBundle;

public final class DatabaseConfigLoader {

    private DatabaseConfigLoader() {}

    public static DbConfig load() {
        ResourceBundle bundle = ResourceBundle.getBundle("config.database");

        String url = bundle.getString("db.url");
        String user = bundle.getString("db.user");
        String password = bundle.getString("db.password");

        return new DbConfig(url, user, password);
    }
}
