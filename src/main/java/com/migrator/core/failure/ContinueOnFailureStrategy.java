package com.migrator.core.failure;

import com.migrator.model.MigrationResult;

public class ContinueOnFailureStrategy implements FailureStrategy {
    public boolean shouldContinue(MigrationResult result) {
        return true;
    }
}

