package com.migrator.core;

import com.migrator.model.MigrationScript;

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
    private final DbVersionRepository repo;
    private final ScriptExecutor executor;

    public MigrationService(ScriptLoader loader,
                            DbVersionRepository repo,
                            ScriptExecutor executor) {
        this.loader = loader;
        this.repo = repo;
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
    public void migrate() throws Exception {

        System.out.println("Loading migration scripts...");
        List<MigrationScript> scripts = loader.loadScripts();

        System.out.println("Total scripts found: " + scripts.size());

        System.out.println("Fetching applied migrations from database...");
        Set<String> applied = repo.getAppliedVersions();

        System.out.println("Already applied: " + applied.size());

        // Filter only scripts that are NOT applied
        List<MigrationScript> pending = scripts.stream()
                .filter(s -> !applied.contains(s.getVersion()))
                .toList();

        System.out.println("Pending migrations: " + pending.size());

        for (MigrationScript script : pending) {
            System.out.println(
                    "➡ Applying migration " + script.getVersion() +
                            " (" + script.getDescription() + ")..."
            );

            executor.execute(script);

            repo.save(script);

            System.out.println("Migration applied successfully!");

            System.out.println("Applied migration count: " + applied.size());
            System.out.println("Pending migration count: " +
                    scripts.stream()
                            .filter(s -> !applied.contains(s.getVersion()))
                            .count());
        }

        System.out.println("All pending migrations applied.");
    }
}
