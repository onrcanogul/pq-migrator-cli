package com.migrator.factory;

import com.migrator.core.db.DatabaseConnector;
import com.migrator.core.db.impl.mssql.MssqlConnector;
import com.migrator.core.db.impl.oracle.OracleConnector;
import com.migrator.core.db.impl.postgres.PostgresConnector;
import com.migrator.model.DatabaseType;

public class DatabaseConnectorFactory {

    public static DatabaseConnector create(DatabaseType type) {
        return switch (type) {
            case POSTGRES -> new PostgresConnector();
            case MSSQL   -> new MssqlConnector();
            case ORACLE  -> new OracleConnector();
        };
    }
}
