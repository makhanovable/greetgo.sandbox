package kz.greetgo.sandbox.db.migration;

import java.sql.Connection;
import java.sql.DriverManager;

public class LaunchMigration {

    public static void main(String args[]) throws Exception {
        Connection connection = getConnection();
        long start = System.currentTimeMillis();
        Migration migration = new Migration(connection);
        migration.downloadMaxBatchSize = 10_000;

        migration.migrate();

        connection.close();
        long end = System.currentTimeMillis();
        System.out.println();
        System.out.println("TOTAL TIME = " + (end - start));
    }

    private static Connection getConnection() throws Exception {
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(
                    "jdbc:postgresql://127.0.0.1:5432/makhan_sandbox",
                "makhan_sandbox",
                "111"
        );
    }

}
