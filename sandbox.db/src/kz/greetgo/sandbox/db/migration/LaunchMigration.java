package kz.greetgo.sandbox.db.migration;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.register.MigrationRegister;
import kz.greetgo.sandbox.db.configs.DbConfig;
import kz.greetgo.sandbox.db.migration.util.SSHDataUtil;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;

@Bean
public class LaunchMigration implements MigrationRegister {

    public static BeanGetter<DbConfig> dbConfig;
    private static Connection connection;
    private static Logger logger = Logger.getLogger(LaunchMigration.class);

    public void start() throws Exception {
        connection = getConnection();

        int maxBatchSize = 500_000;
        startMigration(maxBatchSize);

        connection.close();
    }

    private static void startMigration(int maxBatchSize) throws Exception {
        ArrayList<String> dataToMigrate = SSHDataUtil.downloadFilesAndExtract();
        if (!dataToMigrate.isEmpty())
            for (String file : dataToMigrate) {
                if (file.endsWith("xml"))
                    executeCiaMigration(file, maxBatchSize);
                else if (file.endsWith("txt"))
                    executeFrsMigration(file, maxBatchSize);
            }
    }

    private static void executeCiaMigration(String path, int maxBatchSize) throws Exception {
        System.out.println();
        System.out.println("Starting Migration of " + path);
        logger.info("Starting Migration of " + path);
        long start = System.currentTimeMillis();

        {
            CIAMigration ciaMigration = new CIAMigration(connection, path, maxBatchSize);
            ciaMigration.migrate();
        }

        long end = System.currentTimeMillis();
        System.out.println("Migration Finished in " + (end - start) + " ms");
        logger.info("Migration Finished in " + (end - start) + " ms");
        logger.info("");
    }

    private static void executeFrsMigration(String path, int maxBatchSize) throws Exception {
        System.out.println();
        System.out.println("Starting Migration of " + path);
        logger.info("Starting Migration of " + path);
        long start = System.currentTimeMillis();

        {
            FRSMigration frsMigration = new FRSMigration(connection, path, maxBatchSize);
            frsMigration.migrate();
        }

        long end = System.currentTimeMillis();
        System.out.println("Migration Finished in " + (end - start) + " ms");
        logger.info("Migration Finished in " + (end - start) + " ms");
        logger.info("");
    }

    private static Connection getConnection() throws Exception {
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(dbConfig.get().url(),
                dbConfig.get().username(),
                dbConfig.get().password());
    }

}
