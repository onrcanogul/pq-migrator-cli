package com.migrator.core.failure;

import com.migrator.model.MigrationResult;

public class StopOnFailureStrategy implements FailureStrategy {

    public boolean shouldContinue(MigrationResult result) {
        return result.isSuccess();
    }
}
