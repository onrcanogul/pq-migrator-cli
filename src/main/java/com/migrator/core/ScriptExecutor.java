package com.migrator.core;

import com.migrator.model.MigrationScript;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Executes the SQL content of a MigrationScript.
 *
 * Responsibilities:
 * -----------------
 * - Execute the raw SQL contained inside a migration file
 * - Fail fast when any SQL statement inside the script fails
 * - Use JDBC Statement to support multi-line SQL
 *
 * This executor does NOT split SQL by semicolons.
 * It passes the entire script directly to the database driver.
 *
 * This is intentional because:
 * - Most databases allow executing multi-statement SQL blocks
 * - Splitting SQL manually is error-prone (especially with functions/triggers)
 */
public class ScriptExecutor {

    private final Connection connection;

    /**
     * @param connection JDBC connection used to execute SQL statements
     */
    public ScriptExecutor(Connection connection) {
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
