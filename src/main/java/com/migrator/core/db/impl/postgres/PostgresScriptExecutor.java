package com.migrator.core.db.impl.postgres;

import com.migrator.core.db.ScriptExecutor;
import com.migrator.model.MigrationStatus;
import com.migrator.model.MigrationScript;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class PostgresScriptExecutor extends ScriptExecutor {
    private final Connection connection;

    /**
     * @param connection JDBC connection used to execute SQL statements
     */
    public PostgresScriptExecutor(Connection connection) {
        this.connection = connection;
    }

    /**
     * Executes the given migration script.
     *
     * @param script MigrationScript containing SQL content
     * @throws SQLException when the database rejects the SQL
     */
    public void execute(MigrationScript script) throws SQLException {

        // Create a basic JDBC statement
        try (Statement stmt = connection.createStatement()) {

            // Execute the raw SQL content
            stmt.execute(script.getContent());
            script.updateStrategy(MigrationStatus.APPLIED);

        } catch (SQLException e) {
            throw new SQLException(
                    "Migration failed while executing version " + script.getVersion()
                            + " (" + script.getDescription() + ")\n"
                            + "Cause: " + e.getMessage(),
                    e
            );
        }
    }
}
