package com.migrator.model;

public class MigrationResult {
    private final MigrationScript script;
    private final boolean success;
    private final Exception error;

    public static MigrationResult success(MigrationScript s) {
        return new MigrationResult(s, true, null);
    }

    public static MigrationResult failure(MigrationScript s, Exception e) {
        return new MigrationResult(s, false, e);
    }

    public MigrationResult(MigrationScript script, boolean success, Exception error) {
        this.error = error;
        this.script = script;
        this.success = success;
    }

    public Exception getError() {
        return error;
    }

    public boolean isSuccess() {
        return success;
    }

    public MigrationScript getScript() {
        return script;
    }
}

