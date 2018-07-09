package kz.greetgo.sandbox.db.migration;

import kz.greetgo.sandbox.db.migration.util.SSHDataUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

public class LaunchMigration {

    private static Connection connection;

    public static void main(String args[]) throws Exception {
        connection = getConnection();
        long start = System.currentTimeMillis();

        startMigration(500_000);

        connection.close();
        long end = System.currentTimeMillis();
        System.out.println();
        System.out.println("TOTAL TIME = " + (end - start));
    }

    private static void startMigration(int maxBatchSize) throws Exception {

//        long start = System.currentTimeMillis();
//        System.out.println("Starting Extracting and Downloading...");
//        List<String> dataToMigrate = SSHDataUtil.downloadFilesAndExtract();
//        System.out.println("TIME TO DOWNLOAD and EXTRACT = " + (System.currentTimeMillis() - start));
        List<String> dataToMigrate = new ArrayList<>();
//        dataToMigrate.add("build/out_files/from_cia_2018-02-21-154532-1-300.xml");
//        dataToMigrate.add("build/out_files/from_frs_2018-02-21-154543-1-30009.json_row.txt");
//        dataToMigrate.add("build/out_files/from_frs_2018-02-21-154551-3-1000004.json_row.txt");
//        dataToMigrate.add("build/out_files/from_frs_2018-02-21-155121-3-10000007.json_row.txt");
        dataToMigrate.add("build/out_files/from_cia_2018-02-21-154955-5-1000000.xml");

        long a, b;
        if (!dataToMigrate.isEmpty())
            for (String file : dataToMigrate) {
                if (file.endsWith("xml")) {
                    a = System.currentTimeMillis();
                    executeCiaMigration(file, maxBatchSize);
                    b = System.currentTimeMillis();
                    System.out.println("Time to migrate one CIA with PARSING file = " + (b-a) + " FileName: " + file);
                }
                else if (file.endsWith("txt")) {
                    a = System.currentTimeMillis();
                    executeFrsMigration(file, maxBatchSize);
                    b = System.currentTimeMillis();
                    System.out.println("Time to migrate one FRS with PARSING file " + (b-a) + " FileName: " + file);
                }
            }
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
