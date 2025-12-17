package com.migrator.core.db.impl.oracle;

import com.migrator.core.db.DatabaseConnector;
import com.migrator.model.DbConfig;

import java.sql.Connection;
import java.sql.DriverManager;

public class OracleConnector implements DatabaseConnector {
    @Override
    public Connection connect(DbConfig config) throws Exception {

        String url = "jdbc:oracle:thin:@%s:%d/%s"
                .formatted(
                        config.host(),
                        config.port(),
                        config.database() // service name
                );

        return DriverManager.getConnection(
                url,
                config.user(),
                config.password()
        );
    }
}
