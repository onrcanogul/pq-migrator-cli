package com.migrator.core.db.impl.oracle;

import com.migrator.core.db.impl.MigrationLock;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class OracleMigrationLock implements MigrationLock {

    private final Connection connection;

    public OracleMigrationLock(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void acquire() throws SQLException {
        try (CallableStatement stmt = connection.prepareCall(
                "{ call DBMS_LOCK.REQUEST(?, DBMS_LOCK.X_MODE, 0, TRUE) }"
        )) {
            stmt.setInt(1, 123456); // constant lock ID
            stmt.execute();
        }
    }

    @Override
    public void release() throws SQLException {
        try (CallableStatement stmt = connection.prepareCall(
                "{ call DBMS_LOCK.RELEASE(?) }"
        )) {
            stmt.setInt(1, 123456);
            stmt.execute();
        }
    }
}
