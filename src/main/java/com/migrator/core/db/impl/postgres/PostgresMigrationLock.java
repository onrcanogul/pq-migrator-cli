package com.migrator.core.db.impl.postgres;

import com.migrator.core.db.impl.MigrationLock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PostgresMigrationLock implements MigrationLock {

    private final Connection connection;

    public PostgresMigrationLock(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void acquire() throws SQLException {
        connection.setAutoCommit(false);

        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT id FROM schema_migration_lock WHERE id = 1 FOR UPDATE"
        )) {
            stmt.executeQuery();
        }
    }

    @Override
    public void release() throws SQLException {
        connection.commit();
        connection.setAutoCommit(true);
    }
}

