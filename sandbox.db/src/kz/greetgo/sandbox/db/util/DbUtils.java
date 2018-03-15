package kz.greetgo.sandbox.db.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DbUtils {
  public static Connection getPostgresConnection(String url, String username, String password) throws Exception {
    Class.forName("org.postgresql.Driver");
    return DriverManager.getConnection(url, username, password);
  }
}
