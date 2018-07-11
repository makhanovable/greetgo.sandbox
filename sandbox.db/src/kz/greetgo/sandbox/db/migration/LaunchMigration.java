package kz.greetgo.sandbox.db.migration;

import kz.greetgo.sandbox.db.migration.util.SSHDataUtil;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

public class LaunchMigration {

    private static Connection connection;
    private static Logger logger = Logger.getLogger(LaunchMigration.class);

    public static void main(String args[]) throws Exception {
        connection = getConnection();

        int maxBatchSize = 500_000;
        startMigration(maxBatchSize);

        connection.close();
    }

    private static void startMigration(int maxBatchSize) throws Exception {
        long start = System.currentTimeMillis();
        logger.info("Starting Migration");

        {
            List<String> dataToMigrate = SSHDataUtil.downloadFilesAndExtract();
            if (!dataToMigrate.isEmpty())
                for (String file : dataToMigrate) {
                    if (file.endsWith("xml"))
                        executeCiaMigration(file, maxBatchSize);
                    else if (file.endsWith("txt"))
                        executeFrsMigration(file, maxBatchSize);
                }
        }


        long end = System.currentTimeMillis();
        logger.info("Migration Finished in " + (end - start) + " ms");
        logger.info("");
    }

    private static void executeCiaMigration(String path, int maxBatchSize) throws Exception {
        CIAMigration ciaMigration = new CIAMigration(connection, path, maxBatchSize);
        ciaMigration.migrate();
    }

    private static void executeFrsMigration(String path, int maxBatchSize) throws Exception {
        FRSMigration frsMigration = new FRSMigration(connection, path, maxBatchSize);
        frsMigration.migrate();
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
