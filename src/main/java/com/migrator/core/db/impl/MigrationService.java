package com.migrator.core.db.impl;

import com.migrator.core.db.DbVersionRepository;
import com.migrator.core.db.ScriptExecutor;
import com.migrator.core.failure.FailureStrategy;
import com.migrator.model.MigrationResult;
import com.migrator.model.MigrationScript;

import java.sql.Connection;
import java.util.List;
import java.util.Set;

/**
 * Main orchestrator of the migration engine.
 *
 * Responsibilities:
 * -----------------
 * - Load all migration scripts from filesystem
 * - Determine which scripts have NOT been applied yet
 * - Execute pending migrations in order
 * - Save each successfully applied script to the database
 *
 * This class does not know how to:
 * - read files (handled by ScriptLoader)
 * - execute SQL (handled by ScriptExecutor)
 * - track applied versions (handled by DbVersionRepository)
 *
 * It only coordinates these components.
 */
public class MigrationService {

    private final ScriptLoader loader;
    private final DbVersionRepository repository;
    private final ScriptExecutor executor;
    private static final int MAX_RETRIES = 3;


    public MigrationService(ScriptLoader loader,
                            DbVersionRepository repo,
                            ScriptExecutor executor) {
        this.loader = loader;
        this.repository = repo;
        this.executor = executor;
    }

    /**
     * Main entrypoint for running migrations.
     *
     * Steps:
     * 1. Load all migration files
     * 2. Fetch applied versions from database
     * 3. Filter scripts that are NOT applied yet
     * 4. Execute each pending script in order
     * 5. Save migration record in DB
     *
     * @throws Exception if any migration fails
     */
    public void migrate(Connection connection, FailureStrategy strategy) throws Exception {

        System.out.println("Loading migration scripts...");
        List<MigrationScript> scripts = loader.loadScripts();

        Set<String> applied = repository.getAppliedVersions();

        List<MigrationScript> pending = scripts.stream()
                .filter(s -> !applied.contains(s.getVersion()))
                .toList();

        for (MigrationScript script : pending) {

            MigrationResult result = executor.executeTransactional(connection, script, MAX_RETRIES);

            repository.save(script);

            if (!strategy.shouldContinue(result)) {
                throw result.getError();
            }
        }

        System.out.println("All pending migrations applied.");
    }

}
