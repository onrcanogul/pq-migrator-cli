package com.migrator.core.db;

import com.migrator.model.MigrationResult;
import com.migrator.model.MigrationStatus;
import com.migrator.model.MigrationScript;

import java.sql.Connection;
import java.sql.SQLException;


public abstract class ScriptExecutor {
    protected abstract void execute(MigrationScript script) throws Exception;
    public void executeWithRetry(MigrationScript script, int maxRetries) {

        int attempt = 1;

        while (true) {
            try {
                execute(script);
                return;
            } catch (Exception e) {
                if (attempt >= maxRetries) {
                    script.updateStrategy(MigrationStatus.FAILED);
                    throw new RuntimeException(
                            "Migration failed after " + maxRetries + " attempts", e
                    );
                }

                System.out.println("Migration failed, retrying... Attempt " + attempt);

                attempt++;
            }
        }
    }

    public MigrationResult executeTransactional(Connection connection, MigrationScript script, int maxRetries) {
        try {
            connection.setAutoCommit(false);
            executeWithRetry(script, maxRetries);
            connection.commit();
            return MigrationResult.success(script);

        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException ignored) {}
            return MigrationResult.failure(script, e);

        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignored) {}
        }
    }
}
