package com.migrator.factory;

import com.migrator.core.db.DbVersionRepository;
import com.migrator.core.db.impl.mssql.MssqlDbVersionRepository;
import com.migrator.core.db.impl.mssql.MssqlScriptExecutor;
import com.migrator.core.db.impl.oracle.OracleDbVersionRepository;
import com.migrator.core.db.impl.oracle.OracleScriptExecutor;
import com.migrator.core.db.impl.postgres.PostgresDbVersionRepository;
import com.migrator.core.db.impl.postgres.PostgresScriptExecutor;
import com.migrator.core.db.ScriptExecutor;
import com.migrator.model.DatabaseType;

import java.sql.Connection;

public class DatabaseComponentFactory {

    public static ScriptExecutor createExecutor(
            DatabaseType type,
            Connection connection
    ) {
        return switch (type) {
            case POSTGRES -> new PostgresScriptExecutor(connection);
            case MSSQL   -> new MssqlScriptExecutor(connection);
            case ORACLE  -> new OracleScriptExecutor(connection);
        };
    }

    public static DbVersionRepository createRepository(
            DatabaseType type,
            Connection connection
    ) {
        return switch (type) {
            case POSTGRES -> new PostgresDbVersionRepository(connection);
            case MSSQL   -> new MssqlDbVersionRepository(connection);
            case ORACLE  -> new OracleDbVersionRepository(connection);
        };
    }
}
