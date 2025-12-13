package com.migrator.cli;

import com.migrator.core.DbVersionRepository;
import com.migrator.core.MigrationService;
import com.migrator.core.ScriptExecutor;
import com.migrator.core.ScriptLoader;

import java.sql.Connection;
import java.sql.DriverManager;
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

        String host = params.get("db.host");
        String port = params.get("db.port");
        String user = params.get("db.user");
        String pass = params.get("db.pass");
        String dbName = params.get("db.name");
        String migrationDir = params.get("migrations");

        String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;

        System.out.println("Connecting to database: " + url);

        try (Connection connection = DriverManager.getConnection(url, user, pass)) {

            ScriptLoader loader = new ScriptLoader(migrationDir);
            DbVersionRepository repo = new DbVersionRepository(connection);
            ScriptExecutor executor = new ScriptExecutor(connection);

            MigrationService service = new MigrationService(loader, repo, executor);
            service.migrate();

        }

        System.out.println("Migration completed successfully.");
    }

    // ---------------------------
    // Helpers for argument parsing
    // ---------------------------

    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> map = new HashMap<>();

        for (String arg : args) {
            if (!arg.contains("=")) continue;

            String[] parts = arg.split("=");
            String key = parts[0].replace("--", "");
            String value = parts[1];

            map.put(key, value);
        }
        return map;
    }

    private static void validateParams(Map<String, String> params) {
        String[] required = {
                "db.host", "db.port", "db.user", "db.pass", "db.name", "migrations"
        };

        for (String key : required) {
            if (!params.containsKey(key)) {
                throw new RuntimeException("Missing required argument: --" + key);
            }
        }
    }
}
