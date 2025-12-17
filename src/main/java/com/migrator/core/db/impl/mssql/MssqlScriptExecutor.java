package com.migrator.core.db.impl.mssql;

import com.migrator.core.db.ScriptExecutor;
import com.migrator.model.MigrationStatus;
import com.migrator.model.MigrationScript;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * MSSQL-specific script executor.
 * Handles MSSQL batch execution by splitting scripts on "GO" statements.
 */
public class MssqlScriptExecutor extends ScriptExecutor {

    private final Connection connection;

    public MssqlScriptExecutor(Connection connection) {
        this.connection = connection;
    }

    public void execute(MigrationScript script) throws SQLException {

        // Split script by MSSQL batch separator "GO"
        String[] batches = script.getContent()
                .split("(?im)^\\s*GO\\s*$");

        try (Statement stmt = connection.createStatement()) {

            for (String batch : batches) {

                String sql = batch.trim();
                if (sql.isEmpty()) {
                    continue;
                }

                stmt.execute(sql);
                script.updateStrategy(MigrationStatus.APPLIED);
            }

        } catch (SQLException e) {
            throw new SQLException(
                    "Migration failed while executing version "
                            + script.getVersion()
                            + " (" + script.getDescription() + ")",
                    e
            );
        }
    }
}