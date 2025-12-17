package com.migrator.core.db;

import com.migrator.model.MigrationScript;

import java.util.Set;

/**
 * Handles interaction with the "schema_migrations" table.
 *
 * Responsibilities:
 * -----------------
 * - Read all applied migration versions from the database
 * - Insert a new migration record after successful execution
 * - Ensure the migration engine knows what was already applied
 *
 * Table structure expected:
 *
 *   CREATE TABLE schema_migrations (
 *       id SERIAL PRIMARY KEY,
 *       version VARCHAR(50) NOT NULL,
 *       description VARCHAR(255),
 *       applied_at TIMESTAMP NOT NULL DEFAULT NOW(),
 *       checksum VARCHAR(255)
 *   );
 *
 * This repository does NOT execute SQL migrations.
 * It only tracks metadata about which scripts were applied.
 */
public interface DbVersionRepository {
    Set<String> getAppliedVersions();
    void save(MigrationScript script);
}
