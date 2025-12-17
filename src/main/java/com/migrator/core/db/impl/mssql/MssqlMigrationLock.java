package com.migrator.core.db.impl.mssql;

import com.migrator.core.db.impl.MigrationLock;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class MssqlMigrationLock implements MigrationLock {

    private final Connection connection;

    public MssqlMigrationLock(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void acquire() throws SQLException {
        try (CallableStatement stmt = connection.prepareCall(
                "{? = call sp_getapplock(?, ?, ?, ?)}"
        )) {
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setString(2, "schema_migration_lock");
            stmt.setString(3, "Exclusive");
            stmt.setString(4, "Transaction");
            stmt.setInt(5, 0); // timeout (0 = fail fast)

            stmt.execute();

            int result = stmt.getInt(1);
            if (result < 0) {
                throw new SQLException("Could not acquire migration lock (sp_getapplock)");
            }
        }
    }

    @Override
    public void release() throws SQLException {
        try (CallableStatement stmt = connection.prepareCall(
                "{call sp_releaseapplock(?, ?)}"
        )) {
            stmt.setString(1, "schema_migration_lock");
            stmt.setString(2, "Transaction");
            stmt.execute();
        }
    }
}

