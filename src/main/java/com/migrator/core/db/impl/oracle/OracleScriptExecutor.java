package com.migrator.core.db.impl.oracle;

import com.migrator.core.db.ScriptExecutor;
import com.migrator.model.MigrationStatus;
import com.migrator.model.MigrationScript;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class OracleScriptExecutor extends ScriptExecutor {
    private final Connection connection;

    public OracleScriptExecutor(Connection connection) {
        this.connection = connection;
    }

    @Override
    protected void execute(MigrationScript script) throws Exception {
        // Oracle scripts are typically separated by "/"
        String[] statements = script.getContent()
                .split("(?m)^\\s*/\\s*$");

        try (Statement stmt = connection.createStatement()) {

            for (String raw : statements) {
                String sql = raw.trim();

                if (sql.isEmpty()) {
                    continue;
                }

                // Remove trailing semicolon if present
                if (sql.endsWith(";")) {
                    sql = sql.substring(0, sql.length() - 1);
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
