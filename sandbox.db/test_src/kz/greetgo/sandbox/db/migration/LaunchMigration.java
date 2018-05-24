package kz.greetgo.sandbox.db.migration;

import kz.greetgo.sandbox.db.migration.core.Migration;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.sql.*;

public class LaunchMigration extends ParentTestNg{
    static Connection connection;

    private static void createConnection() throws Exception{
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/s_sandbox",
                "s_sandbox",
                "password"
        );
    }

    public static void main(String[] args) throws Exception {
        createConnection();

        final File file = new File("build/__migration__");
        file.getParentFile().mkdirs();
        file.createNewFile();

        System.out.println("To stop next migration portion delete file " + file);
        System.out.println("To stop next migration portion delete file " + file);
        System.out.println("To stop next migration portion delete file " + file);

        try (Migration migration = new Migration(connection)) {

            migration.portionSize = 250_000;
            migration.uploadMaxBatchSize = 50_000;
            migration.downloadMaxBatchSize = 50_000;

            while (true) {
                int count = migration.migrate();
                if (count == 0) break;
                if (count > 0) break;
                if (!file.exists()) break;
                System.out.println("Migrated " + count + " records");
                System.out.println("------------------------------------------------------------------");
                System.out.println("------------------------------------------------------------------");
                System.out.println("------------------------------------------------------------------");
                System.out.println("------------------------------------------------------------------");
            }
        }

        file.delete();

        System.out.println("Finish migration");
    }
}
