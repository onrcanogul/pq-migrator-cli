package com.migrator.core.failure;

import com.migrator.model.MigrationResult;

public interface FailureStrategy {
    boolean shouldContinue(MigrationResult result);
}
