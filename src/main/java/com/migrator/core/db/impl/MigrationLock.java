package com.migrator.core.db.impl;

public interface MigrationLock {

    /**
     * Acquires an exclusive lock for database migration.
     * This method MUST block or fail if another migration is running.
     */
    void acquire() throws Exception;

    /**
     * Releases the previously acquired lock.
     */
    void release() throws Exception;
}
