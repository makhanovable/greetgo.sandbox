package kz.greetgo.sandbox.db.migration.util;


import kz.greetgo.conf.SysParams;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionUtils {
    public static Connection getPostgresAdminConnection() throws Exception {
        Class.forName("org.postgresql.Driver");
        return  DriverManager.getConnection(
                SysParams.pgAdminUrl(),
                SysParams.pgAdminUserid(),
                SysParams.pgAdminPassword()
        );
    }
}
