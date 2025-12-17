package com.migrator.factory;

import com.migrator.core.failure.ContinueOnFailureStrategy;
import com.migrator.core.failure.StopOnFailureStrategy;
import com.migrator.core.failure.FailureStrategy;

public final class FailureStrategyFactory {

    private FailureStrategyFactory() {

    }

    public static FailureStrategy from(String strategy) {

        if (strategy == null) {
            return new StopOnFailureStrategy();
        }

        return switch (strategy.toUpperCase()) {
            case "STOP" -> new StopOnFailureStrategy();
            case "CONTINUE" -> new ContinueOnFailureStrategy();
            default -> throw new IllegalArgumentException(
                    "Unknown failure strategy: " + strategy +
                            ". Allowed values: STOP, CONTINUE"
            );
        };
    }
}

