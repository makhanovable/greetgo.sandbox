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

        startMigration();

        connection.close();
        long end = System.currentTimeMillis();
        System.out.println();
        System.out.println("TOTAL TIME = " + (end - start));
    }

    private static void startMigration() throws Exception {

//        long start = System.currentTimeMillis();
//        System.out.println("Starting Extracting and Downloading...");
//        List<String> dataToMigrate = SSHDataUtil.downloadFilesAndExtract();
//        System.out.println("TIME TO DOWNLOAD and EXTRACT = " + (System.currentTimeMillis() - start));
        List<String> dataToMigrate = new ArrayList<>();
        dataToMigrate.add("build/out_files/from_cia_2018-02-21-154929-1-300.xml");
        dataToMigrate.add("build/out_files/from_frs_2018-02-21-155112-1-30002.json_row.txt");

        long a, b;
        if (!dataToMigrate.isEmpty())
            for (String file : dataToMigrate) {
                if (file.endsWith("xml")) {
                    a = System.currentTimeMillis();
                    executeCiaMigration(file);
                    b = System.currentTimeMillis();
                    System.out.println("Time to migrate one CIA file = " + (b-a) + " FileName: " + file);
                }
                else if (file.endsWith("txt")) {
                    a = System.currentTimeMillis();
                    executeFrsMigration(file);
                    b = System.currentTimeMillis();
                    System.out.println("Time to migrate one FRS file " + (b-a) + " FileName: " + file);
                }
            }
    }

    private static void executeCiaMigration(String path) throws Exception {
        CIAMigration ciaMigration = new CIAMigration(connection, path);
        ciaMigration.migrate();
    }

    private static void executeFrsMigration(String path) throws Exception {
        FRSMigration frsMigration = new FRSMigration(connection, path);
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
