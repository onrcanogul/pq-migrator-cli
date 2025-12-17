package com.migrator.model;

/**
 * Holds database connection configuration.
 *
 * This class is a simple data holder.
 * It contains NO database-specific logic.
 */
public record DbConfig(
        DatabaseType type,
        String host,
        int port,
        String database,
        String user,
        String password,
        String failureStrategy
) {
}
