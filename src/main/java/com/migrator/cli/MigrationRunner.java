package com.migrator.cli;

import com.migrator.core.db.DatabaseConnector;
import com.migrator.core.db.DbVersionRepository;
import com.migrator.core.db.impl.MigrationLock;
import com.migrator.core.db.impl.MigrationService;
import com.migrator.core.db.ScriptExecutor;
import com.migrator.core.db.impl.ScriptLoader;
import com.migrator.core.failure.FailureStrategy;
import com.migrator.factory.DatabaseComponentFactory;
import com.migrator.factory.DatabaseConnectorFactory;
import com.migrator.factory.FailureStrategyFactory;
import com.migrator.factory.MigrationLockFactory;
import com.migrator.model.DatabaseType;
import com.migrator.model.DbConfig;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * CLI entry point for running database migrations.
 *
 * Responsibilities:
 * -----------------
 * - Parse command-line arguments
 * - Build JDBC connection
 * - Initialize core migration components
 * - Trigger migration execution
 *
 * Example usage:
 *   java -jar migrator-cli.jar \
 *      --db.type=postgres
 *      --db.host=localhost \
 *      --db.port=5432 \
 *      --db.user=postgres \
 *      --db.pass=postgres \
 *      --db.name=mydb \
 *      --migrations=./migrations
 */
public class MigrationRunner {

    public static void main(String[] args) throws Exception {

        System.out.println("Starting Database Migration Tool...");

        Map<String, String> params = parseArgs(args);
        validateParams(params);

        // Database type
        DatabaseType dbType = DatabaseType.from(params.get("db.type"));

        // Build DbConfig (pure data)
        DbConfig config = new DbConfig(
                dbType,
                params.get("db.host"),
                Integer.parseInt(params.get("db.port")),
                params.get("db.name"),
                params.get("db.user"),
                params.get("db.pass"),
                params.get("failure-strategy")
        );

        String migrationDir = params.get("migrations");

        // Create DB connection via connector
        DatabaseConnector connector =
                DatabaseConnectorFactory.create(dbType);

        Connection connection = connector.connect(config);

        MigrationLock lock = MigrationLockFactory.create(dbType.name(), connection);
        System.out.println("Acquiring migration lock...");
        lock.acquire();
        try {
            System.out.println("Connected to database (" + dbType + ")");

            // Core components
            ScriptLoader loader = new ScriptLoader(migrationDir);

            DbVersionRepository repository = DatabaseComponentFactory.createRepository(dbType, connection);

            ScriptExecutor executor = DatabaseComponentFactory.createExecutor(dbType, connection);

            // Run migration
            MigrationService service = new MigrationService(loader, repository, executor);

            FailureStrategy failureStrategy = FailureStrategyFactory.from(config.failureStrategy());

            service.migrate(connection, failureStrategy);
        }
        finally {
            System.out.println("Releasing migration lock...");
            lock.release();
        }

        System.out.println("Migration completed successfully.");
    }

    // ---------------------------
    // Argument parsing helpers
    // ---------------------------

    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> map = new HashMap<>();

        for (String arg : args) {
            if (!arg.startsWith("--") || !arg.contains("=")) continue;

            String[] parts = arg.split("=", 2);
            String key = parts[0].substring(2);
            String value = parts[1];

            map.put(key, value);
        }
        return map;
    }

    private static void validateParams(Map<String, String> params) {

        String[] required = {
                "db.type",
                "db.host",
                "db.port",
                "db.user",
                "db.pass",
                "db.name",
                "migrations"
        };

        for (String key : required) {
            if (!params.containsKey(key)) {
                throw new RuntimeException(
                        "Missing required argument: --" + key
                );
            }
        }
    }
}
