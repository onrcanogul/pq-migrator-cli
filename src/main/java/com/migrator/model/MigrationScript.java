package com.migrator.model;

/**
 * Represents a single database migration script.
 *
 * Each migration file in the /migrations directory contains:
 * - A version (usually timestamp-based, for ordering)
 * - A description (human readable)
 * - The script content (actual SQL)
 * - A checksum (used to detect changes after deployment)
 *
 * Storing these four fields together makes it easy for the
 * migration engine to:
 * - Sort scripts by version
 * - Detect if a script has already been applied
 * - Validate script integrity via checksum comparison
 * - Pass script objects between components (loader, executor, tracker)
 */
public class MigrationScript {

    /** Timestamp or increasing version number. Must be sortable. */
    private final String version;

    /** Human-readable description (comes from the filename). */
    private final String description;

    /** The SQL content read directly from the migration file. */
    private final String content;

    /** SHA-256 checksum of the SQL content. */
    private final String checksum;

    /**
     * Creates a new MigrationScript instance.
     *
     * @param version     Unique version/timestamp from filename
     * @param description Human-readable description from filename
     * @param content     Full SQL content of the migration file
     * @param checksum    Hash value used to detect modifications
     */
    public MigrationScript(String version, String description, String content, String checksum) {
        this.version = version;
        this.description = description;
        this.content = content;
        this.checksum = checksum;
    }

    /** @return Migration version identifier */
    public String getVersion() {
        return version;
    }

    /** @return Description extracted from filename */
    public String getDescription() {
        return description;
    }

    /** @return Raw SQL content */
    public String getContent() {
        return content;
    }

    /** @return SHA-256 checksum of the SQL content */
    public String getChecksum() {
        return checksum;
    }
}
