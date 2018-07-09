package kz.greetgo.sandbox.db.migration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class MigrationAbstract {

    private Connection connection;

    public MigrationAbstract(Connection connection) {
        this.connection = connection;
    }

    public abstract void migrate() throws Exception;

    public void exec(String sql) {
        long start = System.currentTimeMillis();
        System.out.println("NOW EXECUTING = " + sql);
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("TIME TO EXECUTE SQL ABOVE: " + (end - start));
        System.out.println();
    }
}
