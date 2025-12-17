package com.migrator.core.db.impl.mssql;

import com.migrator.core.db.DbVersionRepository;
import com.migrator.model.MigrationScript;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;


/**
 * MSSQL implementation of schema migration metadata repository.
 */
public class MssqlDbVersionRepository implements DbVersionRepository {

    private final Connection connection;

    public MssqlDbVersionRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Set<String> getAppliedVersions() {
        Set<String> versions = new HashSet<>();

        try {
            ensureMigrationInfrastructure();

            try (PreparedStatement stmt = connection.prepareStatement(
                    "SELECT version FROM schema_migrations"
            )) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    versions.add(rs.getString("version"));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch applied migrations", e);
        }

        return versions;
    }

    @Override
    public void save(MigrationScript script) {
        try (PreparedStatement stmt = connection.prepareStatement(
                """
                INSERT INTO schema_migrations
                    (version, description, checksum)
                VALUES (?, ?, ?)
                """
        )) {
            stmt.setString(1, script.getVersion());
            stmt.setString(2, script.getDescription());
            stmt.setString(3, script.getChecksum());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to save migration record for version " + script.getVersion(), e
            );
        }
    }

    /**
     * Creates schema_migrations table if it does not exist (MSSQL compatible).
     */
    private void ensureMigrationInfrastructure() throws SQLException {

        String sql = """
        /* 1. schema_migrations table */
        IF OBJECT_ID('dbo.schema_migrations', 'U') IS NULL
        BEGIN
            CREATE TABLE dbo.schema_migrations (
                id INT IDENTITY(1,1) PRIMARY KEY,
                version NVARCHAR(50) NOT NULL UNIQUE,
                description NVARCHAR(255),
                applied_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
                checksum NVARCHAR(255)
            );
        END;

        /* 2. schema_migration_lock table */
        IF OBJECT_ID('dbo.schema_migration_lock', 'U') IS NULL
        BEGIN
            CREATE TABLE dbo.schema_migration_lock (
                id INT PRIMARY KEY,
                locked_at DATETIME2
            );
        END;

        /* 3. ensure single lock row */
        IF NOT EXISTS (
            SELECT 1 FROM dbo.schema_migration_lock WHERE id = 1
        )
        BEGIN
            INSERT INTO dbo.schema_migration_lock (id, locked_at)
            VALUES (1, SYSDATETIME());
        END;
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

}

