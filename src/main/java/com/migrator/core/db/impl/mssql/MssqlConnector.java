package com.migrator.core.db.impl.mssql;

import com.migrator.core.db.DatabaseConnector;
import com.migrator.model.DbConfig;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * MSSQL database connector.
 * Builds MSSQL-specific JDBC URL and opens connection.
 */
public class MssqlConnector implements DatabaseConnector {

    @Override
    public Connection connect(DbConfig config) throws Exception {

        // Force driver loading (required for shaded jars)
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

        String url = "jdbc:sqlserver://" + config.host() + ":" + config.port()
                + ";databaseName=" + config.database()
                + ";encrypt=true;trustServerCertificate=true";

        System.out.println("JDBC URL = " + url);


        return DriverManager.getConnection(
                url,
                config.user(),
                config.password()
        );
    }
}


